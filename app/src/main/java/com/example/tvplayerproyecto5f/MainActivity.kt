package com.example.tvplayerproyecto5f

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Define la estructura de datos para un canal.
// Cada canal tiene un nombre, una URL para el stream y una URL para su logo.
data class Channel(val name: String, val url: String, val logo: String)

class MainActivity : AppCompatActivity() {

    // Declaración de variables para los componentes de la UI y el reproductor.
    private lateinit var player: ExoPlayer // El reproductor de video ExoPlayer.
    private lateinit var playerView: PlayerView // La vista que muestra el video.
    private lateinit var channelList: RecyclerView // La lista de canales.
    private lateinit var addChannelButton: FloatingActionButton // El botón para añadir nuevos canales.

    // Lista en memoria que contiene los objetos Channel.
    private val channels = mutableListOf<Channel>()
    // Variable temporal para guardar el canal que se está editando.
    private var editingChannel: Channel? = null

    // Inicialización perezosa (lazy) de SharedPreferences.
    // Solo se crea una vez cuando se accede por primera vez.
    private val sharedPreferences by lazy {
        getSharedPreferences("channels", Context.MODE_PRIVATE)
    }

    // Gestiona el resultado de la actividad para añadir un nuevo canal.
    private val addChannelResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val channelName = data?.getStringExtra("channel_name")
            val channelUrl = data?.getStringExtra("channel_url")
            val channelLogo = data?.getStringExtra("channel_logo")
            if (channelName != null && channelUrl != null && channelLogo != null) {
                val newChannel = Channel(channelName, channelUrl, channelLogo)
                channels.add(newChannel) // Añade el nuevo canal a la lista.
                channelList.adapter?.notifyItemInserted(channels.size - 1) // Notifica al adaptador que un nuevo ítem ha sido añadido.
                saveChannels() // Guarda la lista actualizada.
            }
        }
    }

    // Gestiona el resultado de la actividad para editar un canal existente.
    private val editChannelResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val channelName = data?.getStringExtra("channel_name")
            val channelUrl = data?.getStringExtra("channel_url")
            val channelLogo = data?.getStringExtra("channel_logo")
            if (channelName != null && channelUrl != null && channelLogo != null && editingChannel != null) {
                val index = channels.indexOf(editingChannel) // Encuentra el índice del canal editado.
                if (index != -1) {
                    val updatedChannel = Channel(channelName, channelUrl, channelLogo)
                    channels[index] = updatedChannel // Actualiza el canal en la lista.
                    channelList.adapter?.notifyItemChanged(index) // Notifica al adaptador que el ítem ha cambiado.
                    saveChannels() // Guarda la lista actualizada.
                }
            }
        }
        editingChannel = null // Limpia la variable del canal en edición.
    }

    // Metodo principal que se llama al crear la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Asigna las vistas del layout a las variables.
        playerView = findViewById(R.id.player_view)
        channelList = findViewById(R.id.channel_list)
        addChannelButton = findViewById(R.id.add_channel_button)

        // Configura el listener para el botón de añadir canal.
        addChannelButton.setOnClickListener {
            val intent = Intent(this, AddEditChannelActivity::class.java)
            addChannelResultLauncher.launch(intent) // Lanza la actividad para añadir un canal.
        }

        // Carga los canales guardados, e inicializa el reproductor y la lista.
        loadChannels()
        initializePlayer()
        initializeChannelList()
    }

    // Inicializa el reproductor ExoPlayer.
    private fun initializePlayer() {
        try {
            player = ExoPlayer.Builder(this).build() // Construye una instancia de ExoPlayer.
            player.addListener(object : Player.Listener { // Añade un listener para errores de reproducción.
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(this@MainActivity, "Player Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
            playerView.player = player // Asigna el reproductor a la vista.

            // Si hay canales, reproduce el primero por defecto.
            if (channels.isNotEmpty()) {
                playChannel(channels[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing player: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Inicializa el RecyclerView para la lista de canales.
    private fun initializeChannelList() {
        channelList.layoutManager = LinearLayoutManager(this) // Define el layout de la lista (lineal).
        // Crea y asigna el adaptador para la lista.
        val adapter = ChannelAdapter(channels,
            { channel -> // Lambda para gestionar el clic en un canal.
                playChannel(channel)
            },
            { channel, view -> // Lambda para gestionar el clic largo en un canal.
                showPopupMenu(channel, view)
            }
        )
        channelList.adapter = adapter
    }

    // Prepara y reproduce un canal específico.
    private fun playChannel(channel: Channel) {
        if (!this::player.isInitialized) return // Comprueba si el reproductor está inicializado.
        // Crea el MediaItem, especificando el tipo MIME si es un stream HLS (.m3u8).
        val mediaItem = if (channel.url.endsWith(".m3u8")) {
            MediaItem.Builder()
                .setUri(channel.url)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build()
        } else {
            MediaItem.fromUri(channel.url)
        }
        player.setMediaItem(mediaItem) // Asigna el stream al reproductor.
        player.prepare() // Prepara el reproductor.
        player.play() // Inicia la reproducción.
    }

    // Muestra un menú emergente para editar o eliminar un canal.
    private fun showPopupMenu(channel: Channel, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add("Edit") // Opción para editar.
        popupMenu.menu.add("Delete") // Opción para eliminar.
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Edit" -> {
                    editingChannel = channel // Guarda el canal que se va a editar.
                    val intent = Intent(this, AddEditChannelActivity::class.java)
                    // Pasa los datos del canal a la actividad de edición.
                    intent.putExtra("channel_name", channel.name)
                    intent.putExtra("channel_url", channel.url)
                    intent.putExtra("channel_logo", channel.logo)
                    editChannelResultLauncher.launch(intent) // Lanza la actividad de edición.
                }
                "Delete" -> {
                    val index = channels.indexOf(channel)
                    if (index != -1) {
                        channels.removeAt(index) // Elimina el canal de la lista.
                        channelList.adapter?.notifyItemRemoved(index) // Notifica al adaptador.
                        saveChannels() // Guarda los cambios.
                    }
                }
            }
            true
        }
        popupMenu.show() // Muestra el menú.
    }

    // Guarda la lista de canales en SharedPreferences en formato JSON.
    private fun saveChannels() {
        val gson = Gson()
        val json = gson.toJson(channels) // Convierte la lista a un string JSON.
        // Utiliza la función de extensión KTX para editar SharedPreferences de forma concisa.
        sharedPreferences.edit {
            putString("channel_list", json)
        }
    }

    // Carga la lista de canales desde SharedPreferences.
    private fun loadChannels() {
        try {
            // Carga el string JSON de SharedPreferences.
            sharedPreferences.getString("channel_list", null)?.let { json ->
                val type = object : TypeToken<MutableList<Channel>>() {}.type
                // Convierte el JSON de nuevo a una lista de objetos Channel.
                val loadedChannels: List<Channel>? = Gson().fromJson(json, type)
                loadedChannels?.let {
                    channels.clear()
                    channels.addAll(it) // Actualiza la lista en memoria.
                }
            }
        } catch (e: Exception) {
            // Si hay un error (ej. datos corruptos), lo notifica y limpia los datos.
            e.printStackTrace()
            Toast.makeText(this, "Error loading channels. Resetting.", Toast.LENGTH_LONG).show()
            // Limpia los datos corruptos de SharedPreferences.
            sharedPreferences.edit { clear() }
            channels.clear()
        }
    }

    // Se llama cuando la actividad está a punto de ser destruida.
    override fun onDestroy() {
        super.onDestroy()
        // Libera los recursos del reproductor para evitar fugas de memoria.
        if (this::player.isInitialized) {
            player.release()
        }
    }
}

package com.example.tvplayerproyecto5f

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

// Esta actividad se encarga de la interfaz para añadir o editar un canal.
class AddEditChannelActivity : AppCompatActivity() {

    //Metodo que se llama al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_channel)

        // Asigna los campos de texto y el botón del layout a las variables.
        val channelNameInput = findViewById<EditText>(R.id.channel_name_input)
        val channelUrlInput = findViewById<EditText>(R.id.channel_url_input)
        val channelLogoInput = findViewById<EditText>(R.id.channel_logo_input)
        val saveButton = findViewById<Button>(R.id.save_channel_button)

        // Obtiene los datos del canal pasados desde MainActivity (si se está editando).
        val channelName = intent.getStringExtra("channel_name")
        val channelUrl = intent.getStringExtra("channel_url")
        val channelLogo = intent.getStringExtra("channel_logo")

        // Rellena los campos con los datos del canal si existen.
        channelNameInput.setText(channelName)
        channelUrlInput.setText(channelUrl)
        channelLogoInput.setText(channelLogo)

        // Configura el listener para el botón de guardar.
        saveButton.setOnClickListener {
            val resultIntent = Intent() // Crea un nuevo Intent para devolver los datos.
            // Añade los datos introducidos por el usuario al Intent.
            resultIntent.putExtra("channel_name", channelNameInput.text.toString())
            resultIntent.putExtra("channel_url", channelUrlInput.text.toString())
            resultIntent.putExtra("channel_logo", channelLogoInput.text.toString())
            // Establece el resultado como OK y devuelve el Intent a MainActivity.
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Cierra esta actividad y vuelve a la anterior.
        }
    }
}

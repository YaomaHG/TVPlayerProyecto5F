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

data class Channel(val name: String, val url: String, val logo: String = "")

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var channelList: RecyclerView
    private lateinit var addChannelButton: FloatingActionButton

    private val channels = mutableListOf<Channel>()
    private var editingChannel: Channel? = null

    private val addChannelResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val channelName = data?.getStringExtra("channel_name")
            val channelUrl = data?.getStringExtra("channel_url")
            if (channelName != null && channelUrl != null) {
                val newChannel = Channel(channelName, channelUrl)
                channels.add(newChannel)
                channelList.adapter?.notifyItemInserted(channels.size - 1)
                saveChannels()
            }
        }
    }

    private val editChannelResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val channelName = data?.getStringExtra("channel_name")
            val channelUrl = data?.getStringExtra("channel_url")
            if (channelName != null && channelUrl != null && editingChannel != null) {
                val index = channels.indexOf(editingChannel)
                if (index != -1) {
                    val updatedChannel = Channel(channelName, channelUrl)
                    channels[index] = updatedChannel
                    channelList.adapter?.notifyItemChanged(index)
                    saveChannels()
                }
            }
        }
        editingChannel = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        channelList = findViewById(R.id.channel_list)
        addChannelButton = findViewById(R.id.add_channel_button)

        addChannelButton.setOnClickListener {
            val intent = Intent(this, AddEditChannelActivity::class.java)
            addChannelResultLauncher.launch(intent)
        }

        loadChannels()
        initializePlayer()
        initializeChannelList()
    }

    private fun initializePlayer() {
        try {
            player = ExoPlayer.Builder(this).build()
            player.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(this@MainActivity, "Player Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
            playerView.player = player

            if (channels.isNotEmpty()) {
                playChannel(channels[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing player: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeChannelList() {
        channelList.layoutManager = LinearLayoutManager(this)
        val adapter = ChannelAdapter(channels, 
            { channel -> // onChannelClick
                playChannel(channel)
            },
            { channel, view -> // onChannelLongClick
                showPopupMenu(channel, view)
            }
        )
        channelList.adapter = adapter
    }

    private fun playChannel(channel: Channel) {
        if (!this::player.isInitialized) return
        val mediaItem = if (channel.url.endsWith(".m3u8")) {
            MediaItem.Builder()
                .setUri(channel.url)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build()
        } else {
            MediaItem.fromUri(channel.url)
        }
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun showPopupMenu(channel: Channel, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add("Edit")
        popupMenu.menu.add("Delete")
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Edit" -> {
                    editingChannel = channel
                    val intent = Intent(this, AddEditChannelActivity::class.java)
                    intent.putExtra("channel_name", channel.name)
                    intent.putExtra("channel_url", channel.url)
                    editChannelResultLauncher.launch(intent)
                }
                "Delete" -> {
                    val index = channels.indexOf(channel)
                    if (index != -1) {
                        channels.removeAt(index)
                        channelList.adapter?.notifyItemRemoved(index)
                        saveChannels()
                    }
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun saveChannels() {
        val sharedPreferences = getSharedPreferences("channels", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(channels)
        editor.putString("channel_list", json)
        editor.apply()
    }

    private fun loadChannels() {
        try {
            val sharedPreferences = getSharedPreferences("channels", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("channel_list", null)
            if (json != null) {
                val type = object : TypeToken<MutableList<Channel>>() {}.type
                val channelList: MutableList<Channel>? = gson.fromJson(json, type)
                if (channelList != null) {
                    channels.clear()
                    channels.addAll(channelList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading channels. Resetting.", Toast.LENGTH_LONG).show()
            // Clear corrupted data
            getSharedPreferences("channels", Context.MODE_PRIVATE).edit().clear().apply()
            channels.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::player.isInitialized) {
            player.release()
        }
    }
}
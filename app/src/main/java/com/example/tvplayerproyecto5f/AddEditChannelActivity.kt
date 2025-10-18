package com.example.tvplayerproyecto5f

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddEditChannelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_channel)

        val channelNameInput = findViewById<EditText>(R.id.channel_name_input)
        val channelUrlInput = findViewById<EditText>(R.id.channel_url_input)
        val channelLogoInput = findViewById<EditText>(R.id.channel_logo_input)
        val saveButton = findViewById<Button>(R.id.save_channel_button)

        val channelName = intent.getStringExtra("channel_name")
        val channelUrl = intent.getStringExtra("channel_url")
        val channelLogo = intent.getStringExtra("channel_logo")
        channelNameInput.setText(channelName)
        channelUrlInput.setText(channelUrl)
        channelLogoInput.setText(channelLogo)

        saveButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("channel_name", channelNameInput.text.toString())
            resultIntent.putExtra("channel_url", channelUrlInput.text.toString())
            resultIntent.putExtra("channel_logo", channelLogoInput.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
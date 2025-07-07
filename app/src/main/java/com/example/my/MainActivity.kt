package com.example.my

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.search_BTN).setOnClickListener(this)
        findViewById<Button>(R.id.media_library_BTN).setOnClickListener(this)
        findViewById<Button>(R.id.settings_BTN).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_BTN -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.media_library_BTN -> startActivity(Intent(this, MediaLibraryActivity::class.java))
            R.id.settings_BTN -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
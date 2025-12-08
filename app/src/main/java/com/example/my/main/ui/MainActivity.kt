package com.example.my.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.my.databinding.ActivityMainBinding
import com.example.my.mediaLibrary.ui.MediaLibraryActivity
import com.example.my.search.presentation.ui.SearchActivity
import com.example.my.setting.ui.SettingsActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBTN.setOnClickListener(this)
        binding.mediaLibraryBTN.setOnClickListener(this)
        binding.settingsBTN.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
           binding.searchBTN -> startActivity(Intent(this, SearchActivity::class.java))
           binding.mediaLibraryBTN -> startActivity(Intent(this, MediaLibraryActivity::class.java))
           binding.settingsBTN -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}

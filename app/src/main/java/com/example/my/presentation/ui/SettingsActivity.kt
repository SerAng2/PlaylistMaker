package com.example.my.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.my.databinding.ActivitySettingsBinding
import com.example.my.presentation.App
import com.example.my.presentation.Creator.provideSupportInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()

        binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }

    private fun setupViews() {
        binding.container.setNavigationOnClickListener {
            finish()
        }

        binding.shareAppTV.setOnClickListener {
            provideSupportInteractor().shareApp()
        }

        binding.contactSupportTV.setOnClickListener {
            provideSupportInteractor().contactSupport()
        }

        binding.userAgreementTV.setOnClickListener {
            provideSupportInteractor().openUserAgreement()
        }
    }
}

package com.example.my.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.my.Creator
import com.example.my.databinding.ActivitySettingsBinding
import com.example.my.presentation.App

class SettingsActivity : AppCompatActivity() {

    private val creatorSupport by lazy { Creator(applicationContext) }

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
            creatorSupport.provideSupportUseCase().shareApp(this@SettingsActivity)
        }

        binding.contactSupportTV.setOnClickListener {
            creatorSupport.provideSupportUseCase().contactSupport(this@SettingsActivity)
        }

        binding.userAgreementTV.setOnClickListener {
            creatorSupport.provideSupportUseCase().openUserAgreement(this@SettingsActivity)
        }
    }
}

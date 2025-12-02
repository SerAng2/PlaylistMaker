package com.example.my.setting.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.my.databinding.ActivitySettingsBinding
import com.example.my.setting.ui.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingViewModel


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

        viewModel = ViewModelProvider(this, SettingViewModel.getFactory())
            .get(SettingViewModel::class.java)
    }

    private fun setupViews() {
        binding.container.setNavigationOnClickListener {
            finish()
        }

            binding.shareAppTV.setOnClickListener {
                viewModel.shareApp()
            }

            binding.contactSupportTV.setOnClickListener {
                viewModel.contactSupport()
            }

            binding.userAgreementTV.setOnClickListener {
                viewModel.openUserAgreement()
            }
        }
    }
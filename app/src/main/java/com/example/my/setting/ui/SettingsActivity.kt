package com.example.my.setting.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.my.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingViewModel by viewModel()


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
                viewModel.onShareAppClicked()
            }

            binding.contactSupportTV.setOnClickListener {
                viewModel.onContactSupportClicked()
            }

            binding.userAgreementTV.setOnClickListener {
                viewModel.onOpenUserAgreementClicked()
            }
        }
    }

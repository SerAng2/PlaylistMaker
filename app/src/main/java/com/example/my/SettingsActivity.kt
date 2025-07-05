package com.example.playlistmaket

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.container.setNavigationOnClickListener {
            finish()
        }

        binding.shareAppTV.setOnClickListener { shareApp() }
        binding.contactSupportTV.setOnClickListener { contactSupport() }
        binding.userAgreementTV.setOnClickListener { openUserAgreement() }
    }

    private fun shareApp() {
        val shareText = "Рекомендую курс по Android-разработке в Яндекс Практикуме: " +
                "https://practicum.yandex.ru/android-developer/"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Поделиться через"))
    }

    private fun contactSupport() {
        val email = "96-94@mail.ru"
        val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val body = "Спасибо разработчикам и разработчицам за крутое приложение!"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Почтовый клиент не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement() {
        val url = "https://yandex.ru/legal/practicum_offer/"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Браузер не найден", Toast.LENGTH_SHORT).show()
        }
    }
}
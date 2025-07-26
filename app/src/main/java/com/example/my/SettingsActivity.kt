package com.example.my

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
        val shareText = getString(R.string.share_app_text)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun contactSupport() {
        val email = getString(R.string.my_email)
        val subject = getString(R.string.email_subject)
        val body = getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_email_client), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement() {
        val url = getString(R.string.yandex_offer)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_browser), Toast.LENGTH_SHORT).show()
        }
    }
}

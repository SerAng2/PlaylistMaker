package com.example.my.setting.data.impl

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.my.R
import com.example.my.setting.domain.interactor.SupportInteractor

class SupportInteractorImpl(private val context: Context) : SupportInteractor {

    override fun shareApp() {
        val shareText = context.getString(R.string.share_app_text)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooserIntent = Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_app)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }

    override fun contactSupport() {
        val email = context.getString(R.string.my_email)
        val subject = context.getString(R.string.email_subject)
        val body = context.getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, context.getString(
                    R.string.no_email_client
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun openUserAgreement() {
        val url = context.getString(R.string.yandex_offer)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, context.getString(
                    R.string.no_browser
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
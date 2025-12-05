package com.example.my.setting.data.impl

import android.content.Context
import com.example.my.R
import com.example.my.setting.domain.interactor.SupportStringsRepository

class SupportStringsRepositoryImpl(private val context: Context) : SupportStringsRepository {

        override val shareAppText: String get() = context.getString(R.string.share_app_text)
        override val shareAppTitle: String get() = context.getString(R.string.share_app)
        override val myEmail: String get() = context.getString(R.string.my_email)
        override val emailSubject: String get() = context.getString(R.string.email_subject)
        override val emailBody: String get() = context.getString(R.string.email_body)
        override val noEmailClient: String get() = context.getString(R.string.no_email_client)
        override val noBrowser: String get() = context.getString(R.string.no_browser)
        override val yandexOfferUrl: String get() = context.getString(R.string.yandex_offer)
    }

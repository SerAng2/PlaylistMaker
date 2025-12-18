package com.example.my.setting.domain.impl

import com.example.my.setting.domain.interactor.SupportInteractor
import com.example.my.setting.domain.interactor.SupportStringsRepository

class SupportInteractorImpl(
    private val resourceProvider: SupportStringsRepository
) : SupportInteractor {

    override fun getShareAppData(): Pair<String, String> {
        val text = resourceProvider.shareAppText
        val title = resourceProvider.shareAppTitle
        return Pair(text, title)
    }

    override fun getContactSupportData(): Triple<String, String, String> {
        val email = resourceProvider.myEmail
        val subject = resourceProvider.emailSubject
        val body = resourceProvider.emailBody
        return Triple(email, subject, body)
    }

    override fun getUserAgreementUrl(): String {
        return resourceProvider.yandexOfferUrl
    }
}

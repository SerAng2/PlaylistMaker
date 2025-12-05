package com.example.my.setting.ui

import androidx.lifecycle.ViewModel
import com.example.my.setting.domain.interactor.SupportInteractor
import com.example.my.setting.domain.interactor.SupportNavigator

class SettingViewModel(
    private val interactor: SupportInteractor,
    private val navigator: SupportNavigator
) : ViewModel() {

    fun onShareAppClicked() {
        val (text, title) = interactor.getShareAppData()
        navigator.shareApp(text, title)
    }

    fun onContactSupportClicked() {
        val (email, subject, body) = interactor.getContactSupportData()
        navigator.contactSupport(email, subject, body)
    }

    fun onOpenUserAgreementClicked() {
        val url = interactor.getUserAgreementUrl()
        navigator.openUserAgreement(url)
    }
}
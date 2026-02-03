package com.example.playlistMaker.setting.domain.interactor

interface SupportNavigator {
    fun shareApp(text: String, title: String)
    fun contactSupport(email: String, subject: String, body: String)
    fun openUserAgreement(url: String)
}

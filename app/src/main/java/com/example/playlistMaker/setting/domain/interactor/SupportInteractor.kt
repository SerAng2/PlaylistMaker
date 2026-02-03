package com.example.playlistMaker.setting.domain.interactor

interface SupportInteractor {
    fun getShareAppData(): Pair<String, String>
    fun getContactSupportData(): Triple<String, String, String>
    fun getUserAgreementUrl(): String
}

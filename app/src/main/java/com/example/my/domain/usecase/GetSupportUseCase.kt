package com.example.my.domain.usecase

import com.example.my.domain.interactor.SupportInteractor

class GetSupportUseCase(
    private val supportInteractor: SupportInteractor,
) {

    fun shareApp() {
        supportInteractor.shareApp()
    }

    fun contactSupport() {
        supportInteractor.contactSupport()
    }

    fun openUserAgreement() {
        supportInteractor.openUserAgreement()
    }
}

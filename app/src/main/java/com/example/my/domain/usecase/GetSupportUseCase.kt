package com.example.my.domain.usecase

import android.content.Context
import com.example.my.domain.repository.SupportRepository

class GetSupportUseCase(
    private val supportRepository: SupportRepository,
) {

    fun shareApp(context: Context) {
        supportRepository.shareApp(context)
    }

    fun contactSupport(context: Context) {
        supportRepository.contactSupport(context)
    }

    fun openUserAgreement(context: Context) {
        supportRepository.openUserAgreement(context)
    }
}
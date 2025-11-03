package com.example.my.domain.repository

import android.content.Context

interface SupportRepository {
    fun shareApp(context: Context)
    fun contactSupport(context: Context)
    fun openUserAgreement(context: Context)
}

package com.example.my.data.network

import com.example.my.ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitNetworkClient {
    private const val BASE_URL = "https://itunes.apple.com"

    val api: ITunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }
}

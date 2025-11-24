package com.example.my.data.network

import com.example.my.data.dto.SearchDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchSongs(@Query("term") text: String): Response<SearchDto>
}

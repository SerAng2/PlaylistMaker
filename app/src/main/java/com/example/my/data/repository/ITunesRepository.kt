package com.example.my.data.repository

import com.example.my.data.dto.TrackDto

interface ITunesRepository {
    suspend fun searchSongs(term: String): List<TrackDto>
}

class ITunesRepositoryImpl(
    private val api: ITunesApi
) : ITunesRepository {
    override suspend fun searchSongs(term: String): List<TrackDto> {
        val response = api.searchSongs(term)  // Вызов API
        if (!response.isSuccessful) throw Exception("API Error")
        return response.body()?.results ?: emptyList()
    }
}
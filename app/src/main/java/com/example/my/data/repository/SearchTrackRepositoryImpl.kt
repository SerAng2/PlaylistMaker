package com.example.my.data.repository

import com.example.my.data.mapper.MapperTrackResponseToTrack
import com.example.my.data.network.RetrofitNetworkClient
import com.example.my.domain.models.SearchResult
import com.example.my.domain.repository.SearchTrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchTrackRepositoryImpl : SearchTrackRepository {

   override suspend fun performSearch(term: String) : SearchResult { // выполнить поиск

           return try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitNetworkClient.api.searchSongs(term)
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    val results = body?.results ?: emptyList()
                    if (results.isEmpty()) {
                        SearchResult.Empty // showPlaceholderNoResults()
                    } else {
                        val tracks = results.map {
                            MapperTrackResponseToTrack.mapTrackResponseToTrack(it) }
                        SearchResult.Success(tracks) // showTracks(tracks)
                    }
                } else {
                    SearchResult.Error // showPlaceholderError()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    SearchResult.Error
                }
            }
        }
    }

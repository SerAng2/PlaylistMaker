package com.example.my.search.data.repository

import com.example.my.common.domain.model.Track
import com.example.my.search.data.mapper.MapperTrackResponse
import com.example.my.search.data.network.RetrofitNetworkClient
import com.example.my.search.domain.repository.PerformSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PerformSearchRepositoryImpl : PerformSearchRepository {
    override suspend fun performSearch(term: String): Flow<List<Track>> = flow {
            val response = RetrofitNetworkClient.api.searchSongs(term)
            if (response.isSuccessful) {
                val body = response.body()
                val results = body?.results ?: emptyList()
                val tracks = results.map {
                    MapperTrackResponse.mapTrackResponse(it)
                }
                emit(tracks)
            } else {
                val error = Exception("API Error: ${response.message()}")
                throw error
            }
        }
    }


package com.example.my.data.repository

import android.util.Log
import com.example.my.data.mapper.MapperTrackResponse
import com.example.my.data.network.RetrofitNetworkClient
import com.example.my.domain.models.Track
import com.example.my.domain.repository.PerformSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerformSearchRepositoryImpl : PerformSearchRepository {
    override suspend fun performSearch(term: String): List<Track> {
        Log.d("Repo", "Searching for: $term")
        return withContext(Dispatchers.IO) {
            val response = RetrofitNetworkClient.api.searchSongs(term)
            Log.d("Repo", "Response success: ${response.isSuccessful}, code: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("Repo", "Body: $body, Results size: ${body?.results?.size}")
                val results = body?.results ?: emptyList()
                val tracks = results.map {
                    Log.d("Repo", "Mapping: $it")
                    MapperTrackResponse.mapTrackResponse(it)
                }
                Log.d("Repo", "Mapped tracks: ${tracks.size}")
                tracks
            } else {
                val error = Exception("API Error: ${response.message()}")
                Log.e("Repo", "API Error", error)
                throw error  // Или верните emptyList()
            }
        }
    }
}

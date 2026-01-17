package com.example.playlistMaker.search.data.repositoryImpl

import android.util.Log
import com.example.playlistMaker.common.data.dao.TrackDao
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.search.data.mapper.MapperTrackResponse
import com.example.playlistMaker.search.data.network.RetrofitNetworkClient
import com.example.playlistMaker.search.domain.repository.PerformSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class PerformSearchRepositoryImpl(
    private val trackDao: TrackDao
) : PerformSearchRepository {
    override suspend fun performSearch(term: String): Flow<List<Track>> = flow {
        try {
            val response = RetrofitNetworkClient.api.searchSongs(term)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val results = body.results ?: emptyList()
                    val tracks = results.map {
                        MapperTrackResponse.mapTrackResponse(it)
                    }

                    try {
                        val favoriteIds: Set<Long> = trackDao.getTrackId().firstOrNull()
                            ?.map { it.trackId }?.toSet() ?: emptySet()
                        val updatedTracks = tracks.map { track ->
                            track.copy(isFavorite = favoriteIds.contains(track.trackId))
                        }
                        emit(updatedTracks)
                    } catch (e: Exception) {
                        Log.e("PerformSearchRepository", "2a - Ошибка при работе с базой данных", e)
                        throw e  // Пробрасываем исключение, чтобы flow тоже сообщил об ошибке
                    }
                }
            } else {
                Log.e("PerformSearchRepository", "2c - API Error: ${response.code()} ${response.message()}")
                throw Exception("API Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("PerformSearchRepository", "2d - Общая ошибка при поиске: ${e.message}", e)
            throw e // Пробрасываем исключение, чтобы flow тоже сообщил об ошибке
        }
    }
}

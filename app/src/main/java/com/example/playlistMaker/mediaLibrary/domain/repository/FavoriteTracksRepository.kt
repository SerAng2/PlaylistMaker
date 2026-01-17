package com.example.playlistMaker.mediaLibrary.domain.repository

import com.example.playlistMaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrackFavorite(track: Track)      // метод для добавления трека в избранное;
    suspend fun removeTrackFavorite(track: Track)   // метод для удаления трека из избранного;
    fun getListTracksFavorite(): Flow<List<Track>> // метод получения списка со всеми треками, добавленными в избранное.
    suspend fun isTrackFavorite(trackId: Long): Boolean
}
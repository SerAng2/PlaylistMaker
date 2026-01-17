package com.example.playlistMaker.mediaLibrary.domain.interactor

import com.example.playlistMaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: Long): Boolean
    }

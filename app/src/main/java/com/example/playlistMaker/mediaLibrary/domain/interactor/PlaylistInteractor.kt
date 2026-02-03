package com.example.playlistMaker.mediaLibrary.domain.interactor

import android.util.Log
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long // Создайте список воспроизведения

    fun getAllPlaylists(): Flow<List<Playlist>>  // Получить все списки воспроизведения
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long?) {
        Log.d("PlaylistInteractor", "addTrackToPlaylist called: playlist=$playlistId, track=$trackId")
    }
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long?): Boolean
    suspend fun refreshPlaylists()
}

package com.example.playlistMaker.mediaLibrary.domain.repository

import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(title: String, description: String, coverPath: String?): Long
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long?): Boolean
    suspend fun refreshPlaylists()
}

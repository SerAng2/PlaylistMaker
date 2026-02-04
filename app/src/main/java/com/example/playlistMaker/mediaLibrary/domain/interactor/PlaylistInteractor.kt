package com.example.playlistMaker.mediaLibrary.domain.interactor

import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long

    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long?) {}
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long?): Boolean
    suspend fun refreshPlaylists()
}

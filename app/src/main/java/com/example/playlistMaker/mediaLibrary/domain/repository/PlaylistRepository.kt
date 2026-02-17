package com.example.playlistMaker.mediaLibrary.domain.repository

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
    suspend fun getPlaylistTracks(playlistId: Long): List<Track>
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long): List<Track>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    suspend fun getPlaylistByIdOnce(id: Long): Playlist
    suspend fun updatePlaylist(id: Long, name: String, description: String?, coverPath: String?)
    suspend fun deletePlaylist(playlistId: Long)
}

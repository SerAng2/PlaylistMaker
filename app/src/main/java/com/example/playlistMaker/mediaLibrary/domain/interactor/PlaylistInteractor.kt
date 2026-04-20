package com.example.playlistMaker.mediaLibrary.domain.interactor

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.player.presentation.mapper.toDomainTrack
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    suspend fun getPlaylist(id: Long): Playlist
    suspend fun updatePlaylist(id: Long, name: String, description: String?, coverPath: String?)
    suspend fun deletePlaylist(playlistId: Long)

}

package com.example.playlistMaker.mediaLibrary.domain.interactorImpl

import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long {
        return playlistRepository.createPlaylist(
            title = name,
            description = description ?: "",
            coverPath = coverPath
        )
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long?) {
        if (trackId == null) return
        return playlistRepository.addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long?): Boolean {
        if (trackId == null) return false
        return playlistRepository.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun refreshPlaylists() {
        playlistRepository.refreshPlaylists()
    }
}
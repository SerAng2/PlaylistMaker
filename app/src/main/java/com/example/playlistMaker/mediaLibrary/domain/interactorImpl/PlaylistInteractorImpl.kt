package com.example.playlistMaker.mediaLibrary.domain.interactorImpl

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?
    ): Long {
        return playlistRepository.createPlaylist(
            name = name,
            description = description ?: "",
            coverPath = coverPath
        )
    }
    override fun getAllPlaylists(): Flow<List<Playlist>> {
            return playlistRepository.getAllPlaylists()
        }

        override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
            playlistRepository.addTrackToPlaylist(playlistId, track)
        }

       override suspend fun getPlaylistTracks(playlistId: Long): List<Track> {
         return playlistRepository.getPlaylistTracks(playlistId)
       }

       override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
           return playlistRepository.isTrackInPlaylist(playlistId, trackId)
       }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long): List<Track> =
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)

       override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
           return playlistRepository.getPlaylistById(playlistId)
    }


    override suspend fun getPlaylist(id: Long): Playlist =
        playlistRepository.getPlaylistByIdOnce(id)

    override suspend fun updatePlaylist(
        id: Long,
        name: String,
        description: String?,
        coverPath: String?
    ) = playlistRepository.updatePlaylist(id, name, description, coverPath)

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }
}
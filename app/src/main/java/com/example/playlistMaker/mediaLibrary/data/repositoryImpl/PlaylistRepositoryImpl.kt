package com.example.playlistMaker.mediaLibrary.data.repositoryImpl

import android.util.Log
import com.example.playlistMaker.mediaLibrary.data.FileManager
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistDao
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val gson: Gson,
    private val fileManager: FileManager
) : PlaylistRepository {

    private val _refreshTrigger = MutableStateFlow(0)

    override suspend fun refreshPlaylists() {
        _refreshTrigger.value = _refreshTrigger.value + 1
        Log.d("PlaylistRepo", "Refresh triggered: ${_refreshTrigger.value}")
    }

    override suspend fun createPlaylist(title: String, description: String, coverPath: String?): Long {
        Log.d(
            "PlaylistCreation",
            "Repository: Starting playlist creation - title: '$title', description: '$description'"
        )

        return try {
            val internalCoverPath = coverPath?.let { path ->
                Log.d("PlaylistCreation", "Repository: Copying cover from $path")
                fileManager.copyImageToInternalStorage(path, "playlist_covers")
            }

            val entity = PlaylistEntity(
                name = title,
                description = description,
                coverPath = internalCoverPath,
                trackIdsJson = gson.toJson(emptyList<Long>()),
                trackCount = 0
            )

            val result = playlistDao.insertPlaylist(entity)

            refreshPlaylists()

            Log.d("PlaylistCreation", "Repository: Playlist created with ID: $result")
            result

        } catch (e: Exception) {
            Log.e("PlaylistCreation", "Repository: Error creating playlist", e)
            throw e
        }
    }


    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return _refreshTrigger.flatMapLatest {
            playlistDao.getAllPlaylists().map { entities ->
                entities.map { it.toDomain(gson) }
            }
        }
    }


    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        Log.d("PlaylistRepo", "START: Adding track $trackId to playlist $playlistId")

        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return

        val currentTrackIds = gson.fromJson(playlistEntity.trackIdsJson, Array<Long>::class.java).toMutableList()
        if (!currentTrackIds.contains(trackId)) {
            currentTrackIds.add(trackId)

            val updatedEntity = playlistEntity.copy(
                trackIdsJson = gson.toJson(currentTrackIds),
                trackCount = currentTrackIds.size
            )

            playlistDao.updatePlaylist(updatedEntity)

            refreshPlaylists()

            Log.d("PlaylistRepo", "END: Track $trackId added to playlist $playlistId. New count: ${currentTrackIds.size}")
            Log.d("PlaylistRepo", "Refresh trigger updated to: ${_refreshTrigger.value}")
        } else {
            Log.d("PlaylistRepo", "Track $trackId already in playlist $playlistId")
        }
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long?): Boolean {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return false
        val trackIds = gson.fromJson(playlistEntity.trackIdsJson, Array<Long>::class.java)
        return trackIds.contains(trackId)
    }
}

private fun PlaylistEntity.toDomain(gson: Gson): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        coverPath = this.coverPath,
        trackIds = gson.fromJson(this.trackIdsJson, Array<Long>::class.java).toList(),
        trackCount = this.trackCount
    )
}

package com.example.playlistMaker.mediaLibrary.data.repositoryImpl

import android.util.Log
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.example.playlistMaker.mediaLibrary.data.mapper.TrackDbConvertor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: TrackDbConvertor
) : PlaylistRepository {

    // Создание плейлиста
    override suspend fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?
    ): Long {
        val playlistEntity = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath
        )
        return appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    // Получение всех плейлистов
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getAllPlaylists().map { entities ->
            entities.map { entity ->
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    coverPath = entity.coverPath,
                    trackCount = getPlaylistTrackCount(entity.id)
                )
            }
        }
    }

    // Добавление трека в плейлист
    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        val trackEntity = convertor.mapToData(track)
        appDatabase.tracksDao().insertTrack(trackEntity) // Сохраняем трек (если ещё не существует)

        // ✅ КЛЮЧЕВОЙ ШАГ: Создаём связь между плейлистом и треком
        val playlistTrackEntity = PlaylistTrackEntity(
            playlistId = playlistId,
            trackId = trackEntity.trackId // 👈 ИСПОЛЬЗУЕМ id из сохранённого трека
        )
        appDatabase.playlistTrackDao().insertPlaylistTrack(playlistTrackEntity) // ✅ СВЯЗЬ СОЗДАНА!
    }


    // Получение треков плейлиста
    override fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> {
        return appDatabase.playlistTrackDao().getPlaylistTracks(playlistId).map { playlistTracks ->
            val trackIds = playlistTracks.map { it.trackId }
            val trackEntities = appDatabase.tracksDao().getTracksByIds(trackIds)
            playlistTracks.mapNotNull { pte ->
                trackEntities.find { it.trackId == pte.trackId }?.let { convertor.mapToDomain(it) }
            }
        }
    }

    // Проверка, есть ли трек в плейлисте
    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return appDatabase.playlistTrackDao().isTrackInPlaylist(playlistId, trackId)
    }

    // Получение количества треков в плейлисте
    private suspend fun getPlaylistTrackCount(playlistId: Long): Int {
        return appDatabase.playlistTrackDao().getTrackCount(playlistId)
    }

    // Удаление трека из плейлиста
    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        appDatabase.playlistTrackDao().removeTrackFromPlaylist(playlistId, trackId)
        appDatabase.playlistDao().decrementTrackCount(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
        Log.d("Delete", playlistId.toString())
    }

    // Получение плейлиста по ID
    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return appDatabase.playlistDao().getPlaylistById(playlistId).map { entity ->
            entity?.let {
                Playlist(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    coverPath = it.coverPath,
                    trackCount = getPlaylistTrackCount(it.id)
                )
            }
        }
    }

    override suspend fun getPlaylistByIdOnce(id: Long): Playlist {
        val entity = appDatabase.playlistDao().getByIdOnce(id)
        val count = appDatabase.playlistTrackDao().getTrackCount(id)
        return Playlist(
            id = entity?.id ?: 0,
            name = entity?.name ?: "",
            description = entity?.description ?: "",
            coverPath = entity?.coverPath,
            trackCount = count
        )
    }

    override suspend fun updatePlaylist(
        id: Long,
        name: String,
        description: String?,
        coverPath: String?
    ) = appDatabase.playlistDao().updatePlaylist(id, name, description ?: "", coverPath)

}



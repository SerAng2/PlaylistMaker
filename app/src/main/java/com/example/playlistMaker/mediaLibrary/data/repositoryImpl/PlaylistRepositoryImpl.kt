package com.example.playlistMaker.mediaLibrary.data.repositoryImpl

import android.system.Os.link
import android.util.Log
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistDao
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.example.playlistMaker.mediaLibrary.data.db.dao.TracksDao
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.example.playlistMaker.mediaLibrary.data.mapper.TrackDbConvertor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val tracksDao: TracksDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val convertor: TrackDbConvertor
) : PlaylistRepository {

    // Создание плейлиста
    override suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long {
        val playlistEntity = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath
        )
        return playlistDao.insertPlaylist(playlistEntity)
    }

    // Получение всех плейлистов
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
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
        // 1. Сохраняем трек (если ещё не сохранён)
        val trackEntity = convertor.mapToData(track)
        tracksDao.insertTrack(trackEntity)

        // 2. Следующая позиция
        val nextPosition = (playlistTrackDao.getMaxPosition(playlistId) ?: 0) + 1

        // 3. Создаём связь
        val playlistTrack = PlaylistTrackEntity(
            playlistId = playlistId,
            trackId    = track.trackId,
            position   = nextPosition
        )
        val rowId = playlistTrackDao.insertPlaylistTrack(playlistTrack)
        Log.d("REPO", "insertPlaylistTrack rowId=$rowId") // ожидаемо положительное число
    }


    // Получение треков плейлиста
    override suspend fun getPlaylistTracks(playlistId: Long): List<Track> {
        // Получаем связи плейлиста с треками
        val playlistTracks = playlistTrackDao.getPlaylistTracks(playlistId)

        // Получаем ID треков
        val trackIds = playlistTracks.map { it.trackId }

        // Получаем треки из базы
        val trackEntities = tracksDao.getTracksByIds(trackIds)

        // Сортируем по позиции
        val sortedTracks = playlistTracks.sortedBy { it.position }.map { playlistTrack ->
            trackEntities.find { it.trackId == playlistTrack.trackId }
        }.filterNotNull()

        return sortedTracks.map { convertor.mapToDomain(it) }
    }

    // Проверка, есть ли трек в плейлисте
    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return playlistTrackDao.isTrackInPlaylist(playlistId, trackId)
    }

    // Получение количества треков в плейлисте
    private suspend fun getPlaylistTrackCount(playlistId: Long): Int {
        return playlistTrackDao.getTrackCount(playlistId)
    }

    // Удаление трека из плейлиста
    override suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): List<Track> {
        // 1. Удаляем и получаем ОСТАВШИЕСЯ связи в правильном порядке
        val remaining = playlistTrackDao.deleteAndGetRemainingTracks(playlistId, trackId)

        // 2. Получаем сами треки в том же порядке
        val trackIds = remaining.map { it.trackId }
        val entities = tracksDao.getTracksByIds(trackIds)

        // 3. Сопоставляем по ID и сохраняем порядок
        val idToTrack = entities.associateBy { it.trackId }
        val orderedTracks = remaining.mapNotNull { idToTrack[it.trackId] }

        Log.d("REPO", "remaining links=${remaining.size}, ordered tracks=${orderedTracks.size}")

        return orderedTracks.map { convertor.mapToDomain(it) }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val entity = playlistDao.getByIdOnce(playlistId)
        entity?.let {
            playlistDao.deletePlaylist(it)
        }
    }

    // Получение плейлиста по ID
    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map { entity ->
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
        val entity = playlistDao.getByIdOnce(id)
        val count = playlistTrackDao.getTrackCount(id)
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
    ) = playlistDao.updatePlaylist(id, name, description ?: "", coverPath)
}



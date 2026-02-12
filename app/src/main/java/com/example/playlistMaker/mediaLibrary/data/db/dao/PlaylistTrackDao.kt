package com.example.playlistMaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert  (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity) // метод @Insert для добавления трека в таблицу с избранными треками;

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity) // метод @Delete для удаления трека из таблицы избранных треков;

    @Query("SELECT * FROM playlist_track_table WHERE isFavorite = 1")
    fun gePlaylistTracks(): Flow<List<PlaylistTrackEntity>> // метод @Query для получения списка со всеми треками, добавленными в избранное;

    @Query("SELECT * FROM playlist_track_table")
    fun getTrackId(): Flow<List<PlaylistTrackEntity>> // метод @Query для получения списка идентификаторов всех треков, которые добавлены в избранное.

    @Query("SELECT COUNT(*) FROM playlist_track_table WHERE trackId = :trackId AND isFavorite = 1")
    suspend fun isPlaylist(trackId: Long): Int

    @Query("SELECT * FROM playlist_track_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity?
}

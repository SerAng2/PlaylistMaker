package com.example.playlistMaker.common.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistMaker.mediaLibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert  (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity) // метод @Insert для добавления трека в таблицу с избранными треками;

    @Delete
    suspend fun deleteTrack(track: TrackEntity) // метод @Delete для удаления трека из таблицы избранных треков;

    @Query("SELECT * FROM track_table WHERE isFavorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>> // метод @Query для получения списка со всеми треками, добавленными в избранное;

    @Query("SELECT * FROM track_table")
    fun getTrackId(): Flow<List<TrackEntity>> // метод @Query для получения списка идентификаторов всех треков, которые добавлены в избранное.

    @Query("SELECT COUNT(*) FROM track_table WHERE trackId = :trackId AND isFavorite = 1")
    suspend fun isTrackFavorite(trackId: Long): Int
}
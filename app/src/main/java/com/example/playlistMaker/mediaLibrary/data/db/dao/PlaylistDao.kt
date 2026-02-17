package com.example.playlistMaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

//@Dao
//interface PlaylistDao {
//    @Insert
//    suspend fun insert(playlistTrack: PlaylistTrackEntity)
//
//    @Query("SELECT trackId FROM playlist_track_table WHERE playlistId = :playlistId ORDER BY position")
//    suspend fun getTrackIdsByPlaylistId(playlistId: Long): List<Long>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertPlaylist(playlist: PlaylistEntity): Long // вставить плейлист
//
//    @Update
//    suspend fun updatePlaylist(playlist: PlaylistEntity) // обновить плейлист
//
//    @Query("SELECT * FROM playlist_table ORDER BY id DESC") // получить все плейлисты
//    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
//
//    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")  // получить плейлисты по индефикатору
//    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?> // добавление в плейлист
//
//    @Query("SELECT COUNT(*) FROM playlist_table") //  посчитать количество плейлистов, треков
//    suspend fun getPlaylistCount(): Int
//}
@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY created_at DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun getByIdOnce(id: Long): PlaylistEntity?

    @Query("""UPDATE playlist_table 
              SET name = :name, description = :description, cover_path = :coverPath 
              WHERE id = :id""")
    suspend fun updatePlaylist(id: Long, name: String, description: String?, coverPath: String?)
}

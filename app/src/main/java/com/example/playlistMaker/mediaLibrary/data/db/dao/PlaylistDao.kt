package com.example.playlistMaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlist_table WHERE id = :id")
    suspend fun deletePlaylist(id: Long)

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

    @Query("UPDATE playlist_table SET track_count = track_count - 1 WHERE id = :playlistId")
    suspend fun decrementTrackCount(playlistId: Long)
}

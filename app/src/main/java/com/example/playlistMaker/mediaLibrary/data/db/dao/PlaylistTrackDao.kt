package com.example.playlistMaker.mediaLibrary.data.db.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.example.playlistMaker.mediaLibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(entity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table WHERE playlist_id = :playlistId ORDER BY added_at DESC")
    fun getPlaylistTracks(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT COUNT(*) FROM playlist_track_table WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Query("SELECT COUNT(*) FROM playlist_track_table WHERE playlist_id = :playlistId")
    suspend fun getTrackCount(playlistId: Long): Int

    @Query("DELETE FROM playlist_track_table WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun deleteTrack(playlistId: Long, trackId: Long)

    @Transaction
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        deleteTrack(playlistId, trackId)
    }
}

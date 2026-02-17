package com.example.playlistMaker.mediaLibrary.data.db.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(entity: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_track_table WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("SELECT * FROM playlist_track_table WHERE playlist_id = :playlistId ORDER BY position")
    suspend fun getPlaylistTracks(playlistId: Long): List<PlaylistTrackEntity>

    @Query("SELECT MAX(position) FROM playlist_track_table WHERE playlist_id = :playlistId")
    suspend fun getMaxPosition(playlistId: Long): Int?

    @Query("SELECT COUNT(*) FROM playlist_track_table WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Query("SELECT COUNT(*) FROM playlist_track_table WHERE playlist_id = :playlistId")
    suspend fun getTrackCount(playlistId: Long): Int

    @Transaction
    suspend fun deleteAndGetRemainingTracks(
        playlistId: Long,
        trackId: Long
    ): List<PlaylistTrackEntity> {
        removeTrackFromPlaylist(playlistId, trackId)
        return getPlaylistTracks(playlistId)
    }
}

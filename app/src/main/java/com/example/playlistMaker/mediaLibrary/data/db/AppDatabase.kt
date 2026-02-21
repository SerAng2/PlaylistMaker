package com.example.playlistMaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistMaker.common.data.dao.TrackDao
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistDao
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.example.playlistMaker.mediaLibrary.data.db.dao.TracksDao
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.example.playlistMaker.mediaLibrary.data.db.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
               ], version = 14
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun tracksDao(): TracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}

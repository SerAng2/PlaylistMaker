package com.example.playlistMaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistDao
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity

@Database(version = 1, entities = [PlaylistEntity::class])
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}

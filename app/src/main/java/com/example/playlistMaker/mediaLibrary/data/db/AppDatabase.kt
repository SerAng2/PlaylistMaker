package com.example.playlistMaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistMaker.common.data.dao.TrackDao
import com.example.playlistMaker.mediaLibrary.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}

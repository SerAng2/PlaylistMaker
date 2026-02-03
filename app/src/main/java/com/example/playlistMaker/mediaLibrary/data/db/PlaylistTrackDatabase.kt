package com.example.playlistMaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistMaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistTrackEntity

@Database(version = 1, entities = [PlaylistTrackEntity::class])
abstract class PlaylistTrackDatabase : RoomDatabase() {
    abstract fun playlistTrackDao(): PlaylistTrackDao
}

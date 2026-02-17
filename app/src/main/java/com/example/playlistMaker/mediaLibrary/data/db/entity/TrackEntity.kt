package com.example.playlistMaker.mediaLibrary.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
    data class TrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "trackId")
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTime: String,
    val previewUrl: String,
    val isFavorite: Boolean = false
    )

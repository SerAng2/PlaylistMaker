package com.example.playlistMaker.mediaLibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "playlist_track_table")
data class PlaylistTrackEntity(
    @PrimaryKey
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

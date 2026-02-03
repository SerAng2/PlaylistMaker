package com.example.playlistMaker.mediaLibrary.data.db

import com.example.playlistMaker.mediaLibrary.data.db.entity.PlaylistEntity

data class PlaylistWithTracks(
    val playlist: PlaylistEntity,
    val trackIds: List<Long>,
    val trackCount: Int
)

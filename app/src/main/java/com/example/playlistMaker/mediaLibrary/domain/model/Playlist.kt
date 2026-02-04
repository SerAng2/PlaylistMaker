package com.example.playlistMaker.mediaLibrary.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long>,
    val trackCount: Int
)
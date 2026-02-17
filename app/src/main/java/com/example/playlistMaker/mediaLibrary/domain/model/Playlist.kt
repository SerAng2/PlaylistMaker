package com.example.playlistMaker.mediaLibrary.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverPath: String? = null,
    val trackCount: Int = 0,
)
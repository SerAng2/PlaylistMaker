package com.example.playlistMaker.mediaLibrary.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverPath: String? = null,
    val trackCount: Int = 0,
): Parcelable
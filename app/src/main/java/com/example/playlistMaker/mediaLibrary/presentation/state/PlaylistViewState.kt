package com.example.playlistMaker.mediaLibrary.presentation.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistViewState(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long>,
    val trackCount: Int
) : Parcelable
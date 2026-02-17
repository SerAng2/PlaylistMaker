package com.example.playlistMaker.mediaLibrary.presentation.state

import android.os.Parcelable
import com.example.playlistMaker.common.domain.model.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistViewState(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long>? = null,
    val tracks: List<Track>? = null,
    val trackCount: Int
) : Parcelable
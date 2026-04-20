package com.example.playlistMaker.mediaLibrary.presentation.utils

import android.content.Context
import com.example.playlistMaker.R

fun formatTrackCount(trackCount: Int, context: Context): String {
    return context.getString(R.string.tracks, trackCount)
}
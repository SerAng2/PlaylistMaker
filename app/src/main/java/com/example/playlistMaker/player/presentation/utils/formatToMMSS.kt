package com.example.playlistMaker.player.presentation.utils

import java.text.SimpleDateFormat
import java.util.Locale

    fun Long.formatToMMSS(): String {
        val formatter =
            SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(this)
    }


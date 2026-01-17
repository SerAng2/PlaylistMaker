package com.example.playlistMaker.player.presentation.state

import com.example.playlistMaker.R
import com.example.playlistMaker.player.presentation.utils.formatToMMSS

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonText: Int, val progress: String) {

    class Default : PlayerState(false, R.drawable.ic_play, 0L.formatToMMSS())

    class Prepared : PlayerState(true, R.drawable.ic_play, 0L.formatToMMSS())

    class Playing(progress: String) : PlayerState(true, R.drawable.ic_pause, progress)

    class Paused(progress: String) : PlayerState(true, R.drawable.ic_play, progress)
}

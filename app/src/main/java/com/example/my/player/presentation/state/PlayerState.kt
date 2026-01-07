package com.example.my.player.presentation.state

import com.example.my.R


sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonText: Int, val progress: String) {

    class Default : PlayerState(false, R.drawable.ic_play, "00:00")

    class Prepared : PlayerState(true, R.drawable.ic_play, "00:00")

    class Playing(progress: String) : PlayerState(true, R.drawable.ic_pause, progress)

    class Paused(progress: String) : PlayerState(true, R.drawable.ic_play, progress)
}
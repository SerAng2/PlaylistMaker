package com.example.my.player.presentation.view_model

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my.common.domain.model.Track
import com.example.my.player.presentation.state.PlayerState
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.player.presentation.utils.formatToMMSS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayer: MediaPlayer) : ViewModel() {

    private val trackStateLiveData = MutableLiveData<TrackViewState?>()
    val observeTrack: LiveData<TrackViewState?> get() = trackStateLiveData

    val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private var track: Track? = null
    private var timerJob: Job? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> {}
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun preparePlayer() {
        val previewUrl = track?.previewUrl
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))

    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(
                    timeMillis = DELAY_TIME_MS
                )
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(mediaPlayer.currentPosition)
            ?: 0L.formatToMMSS()
    }

    fun loadTrackInfo(track: TrackViewState?) {
        this.track = track?.toTrack()
        trackStateLiveData.value = track
        preparePlayer()
    }

    fun TrackViewState.toTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            genre = genre ?: "",
            country = country,
            previewUrl = previewUrl,
        )
    }

    companion object {
        private const val DELAY_TIME_MS = 300L
    }
}

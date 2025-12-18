package com.example.my.player.presentation.view_model

import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.my.common.domain.model.Track
import com.example.my.player.presentation.state.TrackViewState
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerViewModel(private val mediaPlayer: MediaPlayer) : ViewModel() {

    private val _currentTrack = MutableLiveData<String>()
    val currentTrack: LiveData<String> get() = _currentTrack

    private val _playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    val playerStateLiveData: LiveData<Int> get() = _playerStateLiveData

    private val trackStateLiveData = MutableLiveData<TrackViewState?>()
    val observeTrack: LiveData<TrackViewState?> get() = trackStateLiveData

    private var track: Track? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 300L

    private val updateTimerTask = object : Runnable {
        override fun run() {
            if (_playerStateLiveData.value == STATE_PLAYING) {
                updateCurrentPosition()
                handler.postDelayed(this, updateIntervalMillis)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimerTask)
    }

    fun onPause() {
        if (playerStateLiveData.value == STATE_PLAYING) {
            pausePlayer()
        }
    }

    fun onPlayButtonClicked() {
        when (_playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    fun preparePlayer() {
        val previewUrl = track?.previewUrl
        if (!previewUrl.isNullOrEmpty()) {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    _playerStateLiveData.postValue(STATE_PREPARED)
                }
                mediaPlayer.setOnCompletionListener {
                    _playerStateLiveData.postValue(STATE_PREPARED)
                }
                mediaPlayer.setOnErrorListener { _, _, _ ->
                    _playerStateLiveData.postValue(STATE_DEFAULT)
                    true
                }
            } catch (e: IOException) {
                _playerStateLiveData.postValue(STATE_DEFAULT)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        _playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(updateTimerTask)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        _playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(updateTimerTask)
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    private fun updateCurrentPosition() {
        val currentPosition = mediaPlayer.currentPosition
        val formattedTime = formatTime(currentPosition.toLong())
        _currentTrack.postValue(formattedTime)
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    private fun formatTime(millis: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }

    fun loadTrackInfo(track: TrackViewState?) {
        this.track = track?.toTrack()
        trackStateLiveData.value = track
        preparePlayer()
    }

    fun TrackViewState.toTrack(): Track {
        val timeParts = (trackTime ?: "0:0").split(":")
        val minutes = timeParts.getOrNull(0)?.toLongOrNull() ?: 0L
        val seconds = timeParts.getOrNull(1)?.toLongOrNull() ?: 0L
        val trackTimeMillis = (minutes * 60 * 1000) + (seconds * 1000)

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
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}

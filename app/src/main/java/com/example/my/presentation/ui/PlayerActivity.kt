package com.example.my.presentation.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.my.R
import com.example.my.databinding.ActivityAudioPlayerBinding
import com.example.my.domain.models.Track
import com.example.my.presentation.DisplayPx
import com.example.my.presentation.utils.TimeUtils

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var track: Track? = null
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerTask = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentPosition: Int = mediaPlayer.currentPosition
                binding.playingTime.text = TimeUtils.formatTime(
                    currentPosition
                )
            }
            handler.postDelayed(this, DELAY_MILLIS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar = binding.backPlaylist
        toolbar.setNavigationOnClickListener {
            pausePlayer()
            finish()
        }

        binding.playButton.setOnClickListener {
            playbackControl()
        }

        track = intent.getParcelableExtra(
            SearchActivity.Companion.TRACK_DATA,
            Track::class.java
        )
        if (track != null) {
            displayTrackInfo(track!!)
            preparePlayer()
        } else {
            Log.e(
                "PlayerActivity",
                "Track is null, " +
                        "finishing activity"
            )
            finish()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        track?.let {
            outState.putParcelable(SearchActivity.Companion.TRACK_DATA, it)
        }
    }

    fun Context.cornerRadius() = DisplayPx.dpToPx(8f, this)
    private fun displayTrackInfo(track: Track) {

        val coverImageView = binding.coverPlaylist
        Glide.with(this)
            .load(track.getCoverArtwork())
            .transform(RoundedCorners(cornerRadius()))
            .placeholder(R.drawable.cover_cap)
            .into(coverImageView)

        binding.nameAlbum.text = track.trackName
        binding.nameMusic.text = track.artistName
        binding.durationTime.text = track.trackTime
        binding.albumName.text = track.trackName
        binding.genreMusic.text = track.primaryGenreName
        binding.countryMusic.text = track.country
        binding.yearMusic.text =
            track.releaseDate?.substring(0, 4) ?: "Unknown"
    }

    private fun preparePlayer() {
        val previewUrl = track?.previewUrl
        if (!previewUrl.isNullOrEmpty()) {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                binding.playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                binding.playButton.setImageResource(R.drawable.ic_play)
                playerState = STATE_PREPARED
                isRunning = false
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        isRunning = true
        handler.post(updateTimerTask)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        isRunning = false
        handler.removeCallbacks(updateTimerTask)
    }

    override fun onStop() {
        super.onStop()
        binding.playButton.setImageResource(R.drawable.ic_play)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_MILLIS = 1000L
    }
}

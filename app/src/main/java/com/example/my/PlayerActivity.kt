package com.example.my

import Track
import android.media.MediaPlayer
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.my.SearchActivity.Companion.TRACK_DATA
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {

    private var track: Track? = null

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_MILLIS = 1000L
    }

    private var playerState = STATE_DEFAULT
    private lateinit var play: ImageView
    private lateinit var time: TextView
    private var mediaPlayer = MediaPlayer()
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerTask = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentPosition: Int = mediaPlayer.currentPosition  // в миллисекундах
                time.text = TimeUtils.formatTime(currentPosition)
            }
            handler.postDelayed(this, DELAY_MILLIS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        play = findViewById(R.id.playButton)
        time = findViewById(R.id.playingTime)

        val toolbar = findViewById<MaterialToolbar>(R.id.backPlaylist)
        toolbar.setNavigationOnClickListener {
            pausePlayer()
            finish()
        }

        play.setOnClickListener {
            playbackControl()
        }

        track = intent.getParcelableExtra(TRACK_DATA, Track::class.java)
        if (track != null) {
            displayTrackInfo(track!!)
            preparePlayer()
        } else {
            Log.e("PlayerActivity", "Track is null, finishing activity")
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
            outState.putParcelable(TRACK_DATA, it)
        }
    }

    fun Context.cornerRadius() = DisplayPx.dpToPx(8f, this)
    private fun displayTrackInfo(track: Track) {

        val coverImageView = findViewById<ImageView>(R.id.coverPlaylist)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .transform(RoundedCorners(cornerRadius()))
            .placeholder(R.drawable.cover_cap)
            .into(coverImageView)

        // Установка названия альбома/трека
        findViewById<TextView>(R.id.nameAlbum).text = track.trackName
        findViewById<TextView>(R.id.nameMusic).text = track.artistName
        // Установка длительности
        // Остальные поля (если есть данные)
        findViewById<TextView>(R.id.durationTime).text = track.trackTime
        findViewById<TextView>(R.id.albumName).text = track.trackName
        findViewById<TextView>(R.id.genreMusic).text = track.primaryGenreName
        findViewById<TextView>(R.id.countryMusic).text = track.country
        findViewById<TextView>(R.id.yearMusic).text =
            track.releaseDate?.substring(0, 4) ?: "Unknown"
    }

    private fun preparePlayer() {
        val previewUrl = track?.previewUrl  // или как называется поле с URL в Track
        if (!previewUrl.isNullOrEmpty()) {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                play.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                play.setImageResource(R.drawable.ic_play)
                playerState = STATE_PREPARED
                isRunning = false
            }
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        isRunning = true
        handler.post(updateTimerTask)

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        isRunning = false
        handler.removeCallbacks(updateTimerTask)
    }

    override fun onStop() {
        super.onStop()
        play.setImageResource(R.drawable.ic_play)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }
}

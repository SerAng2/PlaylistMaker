package com.example.my

import Track
import android.content.Context
import android.os.Build
import android.os.Bundle
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        val toolbar = findViewById<MaterialToolbar>(R.id.backPlaylist)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra(TRACK_DATA, Track::class.java)
        if (track != null) {
            Log.d("PlayerActivity", "Track received: ${track.trackName}, releaseDate: ${track.releaseDate}")
            displayTrackInfo(track)
        } else {
            Log.e("PlayerActivity", "Track is null, finishing activity")
            finish()
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
        findViewById<TextView>(R.id.playingTime).text = track.trackTime
        // Остальные поля (если есть данные)
         findViewById<TextView>(R.id.durationTime).text = track.trackTime
         findViewById<TextView>(R.id.albumName).text = track.trackName
         findViewById<TextView>(R.id.genreMusic).text = track.primaryGenreName
         findViewById<TextView>(R.id.countryMusic).text = track.country
        findViewById<TextView>(R.id.yearMusic).text = track.releaseDate?.substring(0, 4) ?: "Unknown"
    }
}

package com.example.my.player.presentation.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.my.R
import com.example.my.databinding.ActivityAudioPlayerBinding
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.player.presentation.utils.DisplayPx
import com.example.my.player.presentation.view_model.PlayerViewModel
import com.example.my.search.presentation.ui.SearchActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()

   private var track: TrackViewState? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTrackInfoObserver()

        track = intent.getParcelableExtra(
            SearchActivity.Companion.TRACK_DATA,
            TrackViewState::class.java
        )

        if (track != null) {
            viewModel.loadTrackInfo(track)
            displayTrackInfo(track!!)
        }

         viewModel.playerStateLiveData.observe(this) {
             changeButtonIcon(it == PlayerViewModel.Companion.STATE_PLAYING)
             enableButton(it != PlayerViewModel.Companion.STATE_DEFAULT)
         }

         viewModel. currentTrack.observe(this) {
             binding.playingTime.text = it as? CharSequence
         }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        val toolbar = binding.backPlaylist
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupTrackInfoObserver() {
        viewModel.observeTrack.observe(this) { uiModel ->
            uiModel?.let { displayTrackInfo(it) }
        }
    }

    fun Context.cornerRadius() = DisplayPx.dpToPx(8f, this)
    private fun displayTrackInfo(track: TrackViewState) {

        val coverImageView = binding.coverPlaylist
        Glide.with(this)
            .load(track.getCoverArtwork())
            .transform(RoundedCorners(cornerRadius()))
            .placeholder(R.drawable.cover_cap)
            .into(coverImageView)

        binding.apply {
            nameAlbum.text = track.trackName
            nameMusic.text = track.artistName
            durationTime.text = track.trackTime
            albumName.text = track.trackName
            genreMusic.text = track.primaryGenreName
            countryMusic.text = track.country
            yearMusic.text =
                track.releaseDate?.substring(0, 4) ?: "Unknown"
        }
    }
    private fun enableButton(isEnabled: Boolean) {
        binding.playButton.isEnabled = isEnabled
    }

    private fun changeButtonIcon(isPlaying: Boolean) {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
       binding.playButton.setImageResource(iconRes)
    }
}

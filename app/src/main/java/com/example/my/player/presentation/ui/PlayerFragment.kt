package com.example.my.player.presentation.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.my.R
import com.example.my.databinding.FragmentAudioPlayerBinding
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.player.presentation.utils.DisplayPx
import com.example.my.player.presentation.view_model.PlayerViewModel
import com.example.my.search.presentation.ui.SearchFragment.Companion.TRACK_DATA
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

    private var track: TrackViewState? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTrackInfoObserver()

        track = arguments?.getParcelable(
            TRACK_DATA,
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

        viewModel.currentTrack.observe(this) {
            binding.playingTime.text = it as? CharSequence
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.backPlaylist.setNavigationOnClickListener {
            try {
                if (isAdded) {
                    val navController = findNavController()
                    if (!navController.navigateUp()) {  // Если navigateUp() не сработал (например, из-за bottom nav)
                        navController.popBackStack()  // Fallback: принудительное удаление из стека
                    }
                    // Дополнительно: обновите bottom nav selected item, если нужно
                    val bottomNav =
                        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav?.selectedItemId = R.id.searchFragment  // Или ID вашей вкладки
                }
            } catch (e: Exception) {
                Log.e("PlayerFragment", "Error handling back navigation: ${e.message}")
                // В крайнем случае, просто popBackStack без условий
                parentFragmentManager.popBackStack()
            }
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
            .transform(RoundedCorners(requireContext().cornerRadius()))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

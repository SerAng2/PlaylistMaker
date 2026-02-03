package com.example.playlistMaker.player.presentation.ui

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
import com.example.playlistMaker.R
import com.example.playlistMaker.common.presentation.constans.UiConstans.TRACK_DATA
import com.example.playlistMaker.databinding.FragmentPlayerBinding
import com.example.playlistMaker.player.presentation.state.TrackViewState
import com.example.playlistMaker.player.presentation.utils.DisplayPx
import com.example.playlistMaker.player.presentation.view_model.PlayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()
    private var track: TrackViewState? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(
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

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.playButton.isEnabled = it.isPlayButtonEnabled
            binding.playButton.setImageResource(it.buttonText)
            binding.playingTime.text = it.progress
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.favourites.setOnClickListener {
            Log.d("PlayerFragment", "Adding track with ID: ${track?.trackId}")
            viewModel.observeTrack.value?.let { currentTrack ->
                viewModel.onFavoriteClicked(currentTrack)
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            val res = if (isFav) R.drawable.ic_favourites_active else R.drawable.ic_favourites
            binding.favourites.setImageResource(res)
        }

        binding.backPlaylist.setNavigationOnClickListener {
            try {
                if (isAdded) {
                    val navController = findNavController()
                    if (!navController.navigateUp()) {
                        navController.popBackStack()
                    }

                    val bottomNav =
                        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav?.selectedItemId = R.id.searchFragment
                }
            } catch (e: Exception) {
                Log.e("PlayerFragment", "Error handling back navigation: ${e.message}")
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupTrackInfoObserver() {
        viewModel.observeTrack.observe(viewLifecycleOwner) { uiModel ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

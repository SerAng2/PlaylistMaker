package com.example.playlistMaker.mediaLibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistMaker.R
import com.example.playlistMaker.common.presentation.constans.UiConstans.TRACK_DATA
import com.example.playlistMaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistMaker.mediaLibrary.presentation.state.FavoritesState
import com.example.playlistMaker.mediaLibrary.presentation.view_model.FavoriteTracksViewModel
import com.example.playlistMaker.search.presentation.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private val adapter = TrackAdapter(
        tracks = emptyList(),
        onTrackClick = { track ->
            val bundle = Bundle().apply {
                putParcelable(
                    TRACK_DATA,
                    track.copy(isFavorite = true)
                )
            }
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment, bundle)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerFavorite.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FavoriteTracksFragment.adapter
        }

        viewModel.favoritesObserver.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> {
                    binding.recyclerFavorite.visibility = View.GONE
                    binding.placeholderGroupFavorite.visibility = View.VISIBLE
                }

                is FavoritesState.Content -> {
                    binding.recyclerFavorite.visibility = View.VISIBLE
                    binding.placeholderGroupFavorite.visibility = View.GONE
                    adapter.updateTracks(state.tracks)
                }

                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
    }
}

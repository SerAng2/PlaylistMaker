package com.example.playlistMaker.mediaLibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistMaker.R
import com.example.playlistMaker.common.presentation.PlaylistAdapter
import com.example.playlistMaker.databinding.FragmentPlaylistBinding
import com.example.playlistMaker.mediaLibrary.presentation.state.PlaylistState
import com.example.playlistMaker.mediaLibrary.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observePlaylists()
        viewModel.loadPlaylist()
    }

    private fun setupRecyclerView() {
        adapter = PlaylistAdapter(emptyList()) { playlist ->
            // Обработка клика по плейлисту
        }
        binding.playlistsRecyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.layoutManager = layoutManager
    }

    private fun setupClickListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }
    }

    private fun observePlaylists() {
        viewModel.playlistObserver.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    adapter.updatePlaylists(state.playlists)
                    binding.playlistsRecyclerView.visibility = View.VISIBLE
                    binding.noMusicIV.visibility = View.GONE
                    binding.playlistsNotCreatedTV.visibility = View.GONE
                }
                is PlaylistState.Empty -> {
                    adapter.updatePlaylists(emptyList())
                    binding.playlistsRecyclerView.visibility = View.GONE
                    binding.noMusicIV.visibility = View.VISIBLE
                    binding.playlistsNotCreatedTV.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SONG_NAME_KEY = "SONG_NAME_KEY"

        fun getInstance(songName: Int): PlaylistsFragment = PlaylistsFragment().apply {
            arguments = bundleOf(SONG_NAME_KEY to songName)
        }
    }
}

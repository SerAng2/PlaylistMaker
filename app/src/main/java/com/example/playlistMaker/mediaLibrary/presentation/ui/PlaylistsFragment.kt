package com.example.playlistMaker.mediaLibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistMaker.databinding.FragmentPlaylistBinding

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noMusicIV.visibility = View.VISIBLE
        binding.playlistsNotCreatedTV.visibility = View.VISIBLE
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

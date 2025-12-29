package com.example.my.mediaLibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.my.databinding.FragmentFavoriteTracksBinding
import com.example.my.mediaLibrary.presentation.view_model.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()

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

        binding.noMusicIV
        binding.mediaEmptyTV
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

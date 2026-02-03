package com.example.playlistMaker.mediaLibrary.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class NumbersViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.getInstance()
            1 -> PlaylistsFragment.getInstance(position + 1)
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}

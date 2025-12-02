package com.example.my.search.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.my.R
import com.example.my.databinding.ItemTrackBinding
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.player.presentation.utils.DisplayPx

class TrackAdapter(
    private var tracks: List<TrackViewState>,
    private val onTrackClick: (track: TrackViewState) -> Unit
) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    fun updateTracks(newTracks: List<TrackViewState>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onTrackClick(track) }
    }

    override fun getItemCount(): Int = tracks.size

    open inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: TrackViewState) {
            binding.apply {
                trackName.text = track.trackName
                artistName.text = track.artistName
                trackTime.text = track.trackTime
            }

            val cornerRadius = DisplayPx.dpToPx(8f, binding.root.context)

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(binding.artwork)
        }
    }
}
package com.example.playlistMaker.common.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistMaker.R
import com.example.playlistMaker.databinding.ItemPlaylistsBinding
import com.example.playlistMaker.mediaLibrary.presentation.state.PlaylistViewState
import com.example.playlistMaker.player.presentation.utils.DisplayPx

class PlaylistAdapter(
    private var playlists: List<PlaylistViewState>,
    var onPlaylistClick: (playlist: PlaylistViewState) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    fun updatePlaylists(newPlaylists: List<PlaylistViewState>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            ItemPlaylistsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistViewHolder(private val binding: ItemPlaylistsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: PlaylistViewState) {
            binding.apply {
                // Устанавливаем название плейлиста
                namePlaylist.text = playlist.name
                // Форматируем количество треков
                quantityTracks.text = formatTrackCount(playlist.trackCount, binding.root.context)
                // Загружаем обложку плейлиста
                loadPlaylistCover(playlist)
            }
        }

        private fun formatTrackCount(trackCount: Int, context: Context): String {
            return context.getString(R.string.tracks, trackCount)
        }

        private fun loadPlaylistCover(playlist: PlaylistViewState) {
            val cornerRadius = DisplayPx.dpToPx(8f, binding.root.context)

            if (!playlist.coverPath.isNullOrEmpty()) {
                Glide.with(binding.coverPlaylist)
                    .load(playlist.coverPath)
                    .placeholder(R.drawable.cover_cap)
                    .centerCrop()
                    .transform(RoundedCorners(cornerRadius))
                    .into(binding.coverPlaylist)
            } else {
                // Если нет обложки, показываем заглушку
                binding.coverPlaylist.setImageResource(R.drawable.cover_cap)
            }
        }
    }
}
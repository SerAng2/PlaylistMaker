package com.example.my

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.my.databinding.ItemTrackBinding
import com.example.my.domain.models.Track

class TrackAdapter(private var tracks: List<Track>, private val onTrackClick: (Track) -> Unit) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    open inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime.text = track.trackTime

            binding.root.setOnClickListener {
                onTrackClick(track)
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
internal object DisplayPx{
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
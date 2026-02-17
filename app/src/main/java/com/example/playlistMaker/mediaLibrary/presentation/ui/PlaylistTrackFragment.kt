package com.example.playlistMaker.mediaLibrary.presentation.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistMaker.R
import com.example.playlistMaker.common.presentation.constans.UiConstans.TRACK_DATA
import com.example.playlistMaker.databinding.FragmentPlaylistTrackBinding
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.presentation.utils.formatTrackCount
import com.example.playlistMaker.mediaLibrary.presentation.view_model.PlaylistTrackViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.ShareEvent
import com.example.playlistMaker.player.presentation.state.TrackViewState
import com.example.playlistMaker.player.presentation.utils.DisplayPx
import com.example.playlistMaker.search.presentation.ui.TrackAdapter
import com.example.playlistMaker.setting.presentation.view_model.SettingViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistTrackFragment : Fragment() {

    private var _binding: FragmentPlaylistTrackBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistTrackViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private var currentPlaylist: Playlist? = null
    private var currentPlaylistId: Long = -1L
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPlaylistId = requireArguments().getLong(ARG_PLAYLIST_ID)
        viewModel.loadPlaylistTracks(currentPlaylistId)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiMessage.collect { msg ->
                    if (msg == "Плейлист удалён") {
                        findNavController().popBackStack()   // уходим назад
                    }
                }
            }
        }

        val bottomSheet = binding.standardBottomSheet
        bottomSheetBehavior =
            BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_COLLAPSED

        // Инициализация адаптера
        // В PlaylistTrackFragment замените код адаптера:
        adapter = TrackAdapter(
            emptyList(),
            onTrackClick = { track ->
                val bundle = Bundle().apply { putParcelable(TRACK_DATA, track) }
                findNavController().navigate(
                    R.id.action_playlistTrackFragment_to_playerFragment,
                    bundle
                )
            },
            onTrackLongClick = { track ->
                showDeleteDialog(track, currentPlaylistId)
            }
        )
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shareEvent.collect { event ->
                    when (event) {
                        is ShareEvent.EmptyPlaylist -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Нет треков")
                                .setMessage("В данном плейлисте нет треков, которыми можно поделиться.")
                                .setPositiveButton("ОК", null)
                                .show()
                        }

                        is ShareEvent.ShareText -> {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, event.text)
                            }
                            val chooser = Intent.createChooser(sendIntent, "Поделиться плейлистом")
                            startActivity(chooser)
                        }
                    }
                }
            }
        }

        binding.share.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlaylistTrackFragment.adapter
        }

        // Получаем playlistId
        val playlistId = arguments?.getLong("playlistId") ?: run {
            Log.e("PLAYLIST_TRACK", "No playlistId in arguments!")
            return
        }

        Log.d("PLAYLIST_TRACK", "Loading playlist with id: $playlistId")

        // Настраиваем кнопку назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.playlistActionsBottomSheet
            .findViewById<View>(R.id.toShare) // ← УБЕДИТЕСЬ, ЧТО ID В XML = toShare
            .setOnClickListener {
                viewModel.sharePlaylist()
            }

        // Загружаем данные
        viewModel.loadPlaylistTracks(playlistId)

        // Подписываемся на изменения
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.allPlaylists.collect { playlists ->
                        val pl = playlists.find { it.id == playlistId }
                        Log.d("PLAYLIST_TRACK", "currentPlaylist = $pl")
                        currentPlaylist = pl
                        currentPlaylist?.let {
                            updatePlaylistInfo(it) // верхний экран
                            updateBottomSheet(it)    // нижняя шторка }
                        }
                    }
                }

                launch {
                    viewModel.currentPlaylistTracks.collect { tracks ->
                        Log.d("PLAYLIST_TRACK", "Tracks loaded: ${tracks.size}")
                        adapter.updateTracks(tracks)

                        // Обновляем интерфейс
                        if (tracks.isNotEmpty()) {
                            binding.tracksRecyclerView.visibility = View.VISIBLE
                        } else {
                            binding.tracksRecyclerView.visibility = View.GONE
                        }
                    }
                }

                // ✅ ПРАВИЛЬНАЯ подписка на общую длительность (отдельный launch)
                launch {
                    viewModel.totalDuration.collect { duration ->
                        binding.trackTime.text = duration
                        binding.trackTime.visibility = View.VISIBLE
                    }
                }
            }
        }


        val bottomSheetContainer = binding.playlistActionsBottomSheet

        //  BottomSheetBehavior.from() — вспомогательная функция, позволяющая получить объект BottomSheetBehavior, связанный с контейнером BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetContainer.findViewById<View>(R.id.editInformation)
            .setOnClickListener {
                Log.d("PLAYLIST_TRACK", "💡 editInformation clicked")
                currentPlaylist?.let { pl ->
                    Log.d("PLAYLIST_TRACK", "🚀 navigate to edit, id=${pl.id}")
                    val action =
                        PlaylistTrackFragmentDirections
                            .actionPlaylistTrackFragmentToNewPlaylistFragment(playlistId = pl.id)
                    findNavController().navigate(action)
                } ?: Log.e("PLAYLIST_TRACK", "❗ currentPlaylist is null!")
            }


        binding.mode.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playlistActionsBottomSheet
            .findViewById<View>(R.id.deletePlaylist)
            .setOnClickListener { showDeletePlaylistDialog() }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        // когда шторка начала открываться – обновляем данные
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED ||
                            newState == BottomSheetBehavior.STATE_EXPANDED
                        ) {
                            currentPlaylist?.let { updateBottomSheet(it) }
                        }
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun updatePlaylistInfo(playlist: Playlist?) {
        if (playlist == null) {
            binding.namePlaylist.text = "Test Playlist"
            binding.descriptionPlaylist.text = "Test description"
            binding.trackCount.text = "2324"
            binding.coverPlaylist.setImageResource(R.drawable.cover_cap)
            Log.d("PLAYLIST_TRACK", "Playlist is null, showing test data")
            return
        }

        binding.namePlaylist.text = playlist.name
        binding.trackCount.text = formatTrackCount(playlist.trackCount, binding.root.context)
        binding.descriptionPlaylist.text = playlist.description ?: ""
        binding.descriptionPlaylist.visibility =
            if (playlist.description.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (!playlist.coverPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(playlist.coverPath)
                .placeholder(R.drawable.cover_cap)
                .centerCrop()
                .into(binding.coverPlaylist)
        } else {
            binding.imageColor.setBackgroundResource(R.drawable.playlist_track_background)
            binding.coverPlaylist.setImageResource(R.drawable.cover_cap)
        }
    }

    private fun updateBottomSheet(playlist: Playlist) {
        with(binding) {
            // обложка
            coverP.loadPlaylistCover(playlist.coverPath)

            // текстовые поля
            nameP.text = playlist.name
            quantityP.text = formatTrackCount(playlist.trackCount, requireContext())
        }
    }

    fun ImageView.loadPlaylistCover(coverPath: String?) {
        val cornerRadius = DisplayPx.dpToPx(2f, context)

        if (!coverPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(coverPath)
                .placeholder(R.drawable.cover_cap)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(this)
        } else {
            setImageResource(R.drawable.cover_cap)
        }
    }
    private fun showDeleteDialog(track: TrackViewState, playlistId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить трек?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.removeTrackFromPlaylist(track, playlistId)
            }
            .setNegativeButton("НЕТ") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист?")
            .setMessage("Все треки останутся в медиатеке.")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deletePlaylist(currentPlaylistId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

const val ARG_PLAYLIST_ID = "ARG_PLAYLIST_ID"

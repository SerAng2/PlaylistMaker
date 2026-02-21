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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
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
import com.example.playlistMaker.mediaLibrary.presentation.state.FavoritesState
import com.example.playlistMaker.mediaLibrary.presentation.utils.PlaylistAdapter
import com.example.playlistMaker.mediaLibrary.presentation.utils.formatTrackCount
import com.example.playlistMaker.mediaLibrary.presentation.view_model.NewPlaylistViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.PlaylistTrackViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.PlaylistsViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.ShareEvent
import com.example.playlistMaker.player.presentation.state.TrackViewState
import com.example.playlistMaker.player.presentation.utils.DisplayPx
import com.example.playlistMaker.search.presentation.ui.TrackAdapter
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        // 🔹 Настраиваем навигацию
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        // 🔹 Получаем playlistId из аргументов
        val playlistId = arguments?.getLong("playlistId") ?: run {
            Log.e("PLAYLIST_TRACK", "No playlistId in arguments!")
            return
        }
        this.currentPlaylistId = playlistId

        // 🔹 Инициализация адаптера
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
            },
        )

        viewModel.loadPlaylistTracks(currentPlaylistId)


        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tracksRecyclerView.adapter = adapter // 👈 КЛЮЧЕВОЙ ФИКС!


        // 🔹 Настраиваем BottomSheet
        val bottomSheet = binding.tracksBottomSheet
        bottomSheetBehavior =
            BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_COLLAPSED

        binding.playlistActionsBottomSheet
            .findViewById<View>(R.id.toShare) // ← УБЕДИТЕСЬ, ЧТО ID В XML = toShare
            .setOnClickListener {
                viewModel.sharePlaylist(currentPlaylistId)
            }

        binding.share.setOnClickListener { viewModel.sharePlaylist(currentPlaylistId) }

        binding.playlistActionsBottomSheet
            .findViewById<View>(R.id.mode) // ← УБЕДИТЕСЬ, ЧТО ID В XML = toShare

        val bottomSheetContainer = binding.playlistActionsBottomSheet

        //  BottomSheetBehavior.from() — вспомогательная функция, позволяющая получить объект BottomSheetBehavior, связанный с контейнером BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetContainer.findViewById<View>(R.id.editInformation)
            .setOnClickListener {
                val action =
                    PlaylistTrackFragmentDirections
                        .actionPlaylistTrackFragmentToNewPlaylistFragment(playlistId)
                findNavController().navigate(action)
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

        // 🔹 Обработка сообщений (удаление плейлиста)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiMessage.collect { msg ->
                    if (msg == "Плейлист удалён") {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        // 🔹 Обработка событий шаринга
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
                            startActivity(Intent.createChooser(sendIntent, "Поделиться плейлистом"))
                        }
                    }
                }
            }
        }

        viewModel.favoritesObserver.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> {
                    binding.tracksRecyclerView.visibility = View.GONE
                    binding.noTracks.visibility = View.VISIBLE
                }

                is FavoritesState.Content -> {
                    binding.tracksRecyclerView.visibility = View.VISIBLE
                    binding.noTracks.visibility = View.GONE
                    adapter.updateTracks(state.tracks)
                }

                else -> {}
            }
        }

        // Загружаем данные
        viewModel.loadPlaylistTracks(playlistId)

        // Подписываемся на изменения
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.allPlaylists.collect { playlists ->
                        val pl = playlists.find { it.id == playlistId }
                        currentPlaylist = pl
                        currentPlaylist?.let { updatePlaylistInfo(it); updateBottomSheet(it) }
                    }
                }

                launch {
                    viewModel.currentPlaylistTracks.collect { tracks ->
                        adapter.updateTracks(tracks)
                        binding.tracksRecyclerView.visibility =
                            if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
                    }
                }

                viewModel.totalDuration.collect { duration ->
                    binding.trackTime.text = duration
                    binding.trackTime.visibility = View.VISIBLE
                }
            }
        }

        binding.share.setOnClickListener { viewModel.sharePlaylist(currentPlaylistId) }


// 🔹 Добавляем callback для playlistActionsBottomSheet (для overlay)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

// 🔹 Обработчики кнопок внутри playlistActionsBottomSheet
        bottomSheetContainer.findViewById<View>(R.id.deletePlaylist)
            .setOnClickListener { showDeletePlaylistDialog() }
        bottomSheetContainer.findViewById<View>(R.id.toShare)
            .setOnClickListener { viewModel.sharePlaylist(currentPlaylistId) }
        // 🔹 Добавляем callback для BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun updatePlaylistInfo(playlist: Playlist?) {
        binding.namePlaylist.text = playlist?.name ?: "Test Playlist"
        binding.trackCount.text =
            playlist?.let { formatTrackCount(it.trackCount, binding.root.context) } ?: "2324"
        binding.descriptionPlaylist.text = playlist?.description ?: ""
        binding.descriptionPlaylist.visibility =
            if (playlist?.description.isNullOrBlank()) View.GONE else View.VISIBLE

        if (!playlist?.coverPath.isNullOrEmpty()) {
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
            coverP.loadPlaylistCover(playlist.coverPath)
            nameP.text = playlist.name
            quantityP.text = formatTrackCount(playlist.trackCount, requireContext())
        }
    }

    // 🔸 Утилиты
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
            .setTitle("Удалить трек?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.removeTrackFromPlaylist(playlistId, track)
                Toast.makeText(requireContext(), "${track.trackName} удален(а)", Toast.LENGTH_SHORT)
                    .show()
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

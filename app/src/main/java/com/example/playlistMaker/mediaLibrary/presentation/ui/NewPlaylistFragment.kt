package com.example.playlistMaker.mediaLibrary.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.playlistMaker.R
import com.example.playlistMaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistMaker.mediaLibrary.presentation.view_model.NavigationEvent
import com.example.playlistMaker.mediaLibrary.presentation.view_model.NewPlaylistViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.UiEvent
import com.example.playlistMaker.player.presentation.utils.DisplayPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var isShowingIcon = true
    private var isExitDialogShowing = false
    private var currentImageUri: Uri? = null
    private val viewModel: NewPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()
        subscribeToUiEvents()
        setupBackCallback()



        // ✅ 3. Проверьте, что ViewModel инициализирован
        Log.d("NewPlaylistFragment", "ViewModel: ${viewModel.hashCode()}")

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.bottom.isEnabled = state.isCreateButtonEnabled

                if (state.isCreateButtonEnabled) {
                    binding.bottom.setBackgroundColor(requireContext().getColor(R.color.YP_blue))
                    binding.bottom.setTextColor(requireContext().getColor(R.color.YP_white))
                } else {
                    binding.bottom.setBackgroundColor(requireContext().getColor(R.color.YP_Text_Gray))
                    binding.bottom.setTextColor(requireContext().getColor(R.color.YP_white))
                }
            }
        }
    }


    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.error?.let { error ->
                    showErrorDialog(error)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    is NavigationEvent.NavigateBack -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Photo picker
        val pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                // ✅ Переключаем на фото с растягиванием

                val savedPath = saveImageToPrivateStorage(uri)
                showPhoto(uri)
                viewModel.onCoverPathChanged(savedPath)
            } else {
                // ✅ Возвращаем к иконке (если отменили выбор)
                if (!isShowingIcon) {
                    viewModel.onCoverPathChanged(null)
                }
            }
        }

        binding.addImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.name.addTextChangedListener { editable ->
            viewModel.onTitleChanged(editable?.toString() ?: "")
        }

        binding.description.addTextChangedListener { editable ->
            viewModel.onDescriptionChanged(editable?.toString() ?: "")
        }

        binding.bottom.setOnClickListener {
            viewModel.onCreatePlaylist()
        }

        binding.backPlaylist.setOnClickListener {
            // Прямой вызов той же логики, что и в OnBackPressedCallback
            val state = viewModel.uiState.value
            if (state.hasChanges) {
                showExitConfirmationDialog()
            } else {
                findNavController().popBackStack() // или viewModel.onBackClicked(), если он делает popBackStack
            }
        }
        }

    private fun Context.cornerRadius() = DisplayPx.dpToPx(8f, this)
    // ✅ ПОКАЗАТЬ ФОТО (растягиваем)
    private fun showPhoto(uri: Uri) {

        val coverImageView = binding.addImage
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .apply(
                RequestOptions()
                    .centerCrop()
                    .transform(RoundedCorners(requireContext().cornerRadius()))
            )
            .error(R.drawable.ic_add_image)
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .into(coverImageView)

        binding.addImage.scaleType = ImageView.ScaleType.CENTER_CROP
        currentImageUri = uri
        isShowingIcon = false
    }

    private fun showIcon() {
        Glide.with(this)
            .load(R.drawable.ic_add_image)
            .apply(
                RequestOptions()
                    .centerInside()
                    .transform(RoundedCorners(requireContext().cornerRadius()))
            )
            .centerInside()
            .into(binding.addImage)

        binding.addImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        currentImageUri = null
        isShowingIcon = true
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                viewModel.clearError()
            }
            .show()
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "playlist_cover_$timeStamp.jpg"

            val filePath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlist_covers"
            )

            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            val file = File(filePath, imageFileName)
            val inputStream = context?.contentResolver?.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            BitmapFactory.decodeStream(inputStream)
                ?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            Log.e("NewPlaylistFragment", "Error saving image", e)
            null
        }
    }

    private fun subscribeToUiEvents() {
        // ✅ ПОДПИСЫВАЕМСЯ НА UiEvent — ИСПОЛЬЗУЕМ lifecycleScope
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowToast -> {
                        // ✅ ТУТ ЕСТЬ КОНТЕКСТ — ИДЕАЛЬНОЕ МЕСТО!
                        Toast.makeText(
                            requireContext(),
                            event.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
    // ✅ Перехват системной кнопки "Назад"
    private fun setupBackCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) { // viewLifecycleOwner!!!
                override fun handleOnBackPressed() {
                    Log.e("NewPlaylistFragment", "🔥 BACK BUTTON PRESSED — WORKING NOW!")
                    val state = viewModel.uiState.value
                    if (state.hasChanges) {
                        showExitConfirmationDialog()
                    } else {
                        viewModel.onBackClicked()
                    }
                }
            })
    }

    private fun showExitConfirmationDialog() {
        if (isExitDialogShowing) return
        isExitDialogShowing = true

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, _ ->
                dialog.dismiss()
                isExitDialogShowing = false
            }
            .setPositiveButton("Завершить") { dialog, _ ->
                dialog.dismiss()
                isExitDialogShowing = false
                viewModel.onBackClicked() // ✅ ЗАКРЫВАЕМ ЭКРАН
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isExitDialogShowing = false
    }
}

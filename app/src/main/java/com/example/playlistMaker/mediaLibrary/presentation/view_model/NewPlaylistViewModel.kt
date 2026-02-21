package com.example.playlistMaker.mediaLibrary.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.use_case.CreatePlaylistUseCaseImpl
import com.example.playlistMaker.mediaLibrary.presentation.state.NewPlaylistUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCaseImpl,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewPlaylistUiState())
    val uiState: StateFlow<NewPlaylistUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    private val _uiEvent = MutableSharedFlow<UiEvent>(replay = 0)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onCoverPathChanged(coverPath: String?) {
        _uiState.update { it.copy(coverPath = coverPath) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateBack)
        }
    }

    fun onCreatePlaylist() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val currentState = _uiState.value

                // Создание плейлиста
                val result = createPlaylistUseCase(
                    title = currentState.title.trim(),
                    description = currentState.description.trim(),
                    coverPath = currentState.coverPath
                )

                if (result.isSuccess) {
                    _uiEvent.emit(UiEvent.ShowToast("Вы создали плейлист «${currentState.title}»"))
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exceptionOrNull()?.message ?: "Ошибка создания плейлиста"
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadPlaylistForEdit(playlistId: Long) {
        viewModelScope.launch {
            Log.d("NewPlaylistVM", "Loading playlist for edit: $playlistId")
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                Log.d("NewPlaylistVM", "Loaded: ${playlist?.name}")
                if (playlist != null) {
                    _uiState.value = NewPlaylistUiState(
                        title = playlist.name,
                        description = playlist.description,
                        coverPath = playlist.coverPath,
                        isEditing = true,
                        playlistId = playlist.id
                    )
                }
            }
        }
    }
}

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
}

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}

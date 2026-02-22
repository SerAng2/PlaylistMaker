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
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
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


    fun savePlaylistChanges() {
        // Убедитесь, что isEditing установлен в true при загрузке для редактирования
        if (_uiState.value.isEditing && _uiState.value.playlistId != null) {
            viewModelScope.launch {
                val currentUiState = _uiState.value
                // Проверка, что у нас есть все для обновления
                if (currentUiState.playlistId != null) {
                    // Здесь вызываем updatePlaylist с актуальными данными
                    playlistInteractor.updatePlaylist(
                        id = currentUiState.playlistId,
                        name = currentUiState.title,
                        description = currentUiState.description,
                        coverPath = currentUiState.coverPath
                    )
                    // После сохранения, можно сбросить состояние или перейти обратно
                    // _uiState.value = NewPlaylistUiState() // Пример сброса
                    // navigationActions.navigateBack()
                }
            }
        } else {
            // Обработка случая, когда это новый плейлист, а не редактирование
            // Или когда playlistId отсутствует
            Log.w("NewPlaylistVM", "Cannot save: not in editing mode or playlist ID is missing.")
        }
    }
}

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
}

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}

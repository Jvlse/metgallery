package de.example.met_gallery.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.example.met_gallery.model.Artwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface DetailUiState {
    data class Success(
        val artwork: Artwork?
    ) : DetailUiState
    object Error : DetailUiState
    object Loading : DetailUiState
}

class DetailViewModel(
) : ViewModel() {
    var detailUiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    private val _artwork: MutableStateFlow<Artwork?> = MutableStateFlow(null)
    val artwork: StateFlow<Artwork?> = _artwork.asStateFlow()

    fun setArtwork(art: Artwork) {
        _artwork.value = art
    }

    fun leaveDetailScreen() {
        _artwork.value = null
    }
}
package de.example.met_gallery.ui.screens.detail

import androidx.lifecycle.ViewModel
import de.example.met_gallery.model.Artwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailViewModel(
) : ViewModel() {

    private val _artwork: MutableStateFlow<Artwork?> = MutableStateFlow(null)
    val artwork: StateFlow<Artwork?> = _artwork.asStateFlow()

    fun setArtwork(art: Artwork) {
        _artwork.value = art
    }

    fun leaveDetailScreen() {
        _artwork.value = null
    }
}
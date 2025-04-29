package de.example.met_gallery.ui.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.network.ArtworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import java.lang.Exception

sealed interface ObjectListUiState {
    data class Success(
        val objects: ObjectList
    ) : ObjectListUiState
    data class Error(
        val e: Exception
    ) : ObjectListUiState
    object Loading : ObjectListUiState
}

sealed interface ArtworkUiState {
    data class Success(
        val artwork: Artwork?
    ) : ArtworkUiState
    data class Error(
        val e: Exception
    ) : ArtworkUiState
    object Loading : ArtworkUiState
}

class SearchViewModel(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {
    var objectListUiState: ObjectListUiState by mutableStateOf(ObjectListUiState.Loading)
        private set

    private val _artworkUiStateList = MutableStateFlow<List<ArtworkUiState>>(listOf(ArtworkUiState.Loading))
    val artworkUiStateList: StateFlow<List<ArtworkUiState>> = _artworkUiStateList.asStateFlow()

    private val _artworks: MutableStateFlow<Set<Artwork>> = MutableStateFlow(emptySet())
    val artworks: StateFlow<Set<Artwork>> = _artworks.asStateFlow()

    private val _errors: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())

    init {
        getObjectIds()
    }

    fun getObjectIds() {
        if(objectListUiState is ObjectListUiState.Success) {
            return
        } else {
            viewModelScope.launch {
                objectListUiState = ObjectListUiState.Loading
                objectListUiState = try {
                    ObjectListUiState.Success(
                        artworkRepository.getArtworks()
                    )
                } catch (e: IOException) {
                    ObjectListUiState.Error(e)
                } catch (e: HttpException) {
                    ObjectListUiState.Error(e)
                }
            }
        }
    }

    fun searchArtworks(query: String) {
        viewModelScope.launch {
            objectListUiState = ObjectListUiState.Loading
            objectListUiState = try {
                ObjectListUiState.Success(
                    artworkRepository.searchArtworks(query)
                )
            } catch (e: IOException) {
                ObjectListUiState.Error(e)
            } catch (e: HttpException) {
                ObjectListUiState.Error(e)
            }
        }
    }

    fun getArtworkById(id: Int, index: Int) {
        // already fetched
        if (getLocalArtwork(id) != null) return

        if (objectListUiState is ObjectListUiState.Success) {
            viewModelScope.launch {
                _artworkUiStateList.update { currentList ->
                    if (index in currentList.indices) {
                        currentList.toMutableList().apply {
                            this += ArtworkUiState.Loading
                        }.toList() // Create a new list with the updated element
                    } else {
                        currentList // Return the original list if the index is invalid
                    }
                }
                handleResult(id, index)
            }
        }
    }

    fun getLocalArtwork(id: Int): Artwork? {
        return _artworks.value.firstOrNull { it.id == id }
    }

    suspend fun handleResult(id: Int, index: Int) {
        val searchResult = artworkRepository.getArtworkById(id)
        searchResult.fold(
            onSuccess = { artwork ->
                if(artwork.primaryImage.isNotBlank()
                    || artwork.primaryImageSmall.isNotBlank()) {
                    _artworkUiStateList.update { currentList ->
                        currentList.toMutableList().apply {
                            this[index] = ArtworkUiState.Success(artwork)
                        }
                    }
                    _artworks.value += artwork
                } else {
                    removeObjectFromList(id)
                }
            },
            onFailure = {
                removeObjectFromList(id)
                _errors.value += id
            }
        )
    }

    // filter ID out of list so that Card doesn't get rendered in UI
    fun removeObjectFromList(id: Int) {
        objectListUiState = ObjectListUiState.Success(
            ObjectList(
                (objectListUiState as ObjectListUiState.Success).objects.total,
                (objectListUiState as ObjectListUiState.Success)
                    .objects.objectIds.filter { it != id }
            )
        )
    }
}
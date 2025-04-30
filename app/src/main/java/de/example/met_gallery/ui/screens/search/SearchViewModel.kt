package de.example.met_gallery.ui.screens.search

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

class SearchViewModel(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {
    var objectListUiState: ObjectListUiState by mutableStateOf(ObjectListUiState.Loading)
        private set

    private val _artworks: MutableStateFlow<Set<Artwork>> = MutableStateFlow(emptySet())
    val artworks: StateFlow<Set<Artwork>> = _artworks.asStateFlow()

    private val _search: MutableStateFlow<String> = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val _errors: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())

    fun getArtworks(query: String? = null) {
        viewModelScope.launch {
            objectListUiState = ObjectListUiState.Loading
            objectListUiState = try {
                ObjectListUiState.Success(
                    artworkRepository.searchArtworks(query ?: "")
                )
            } catch (e: IOException) {
                ObjectListUiState.Error(e)
            } catch (e: HttpException) {
                ObjectListUiState.Error(e)
            }
        }
    }

    fun getArtworkById(id: Int) {
        // already fetched
        if (getLocalArtwork(id) != null) return

        if (objectListUiState is ObjectListUiState.Success) {
            viewModelScope.launch {
                handleResult(id)
            }
        }
    }

    fun getLocalArtwork(id: Int): Artwork? {
        return _artworks.value.firstOrNull { it.id == id }
    }

    fun setSearch(search : String) {
        _search.value = search
    }
    fun getSearch(): String {
        return search.value
    }

    suspend fun handleResult(id: Int) {
        val searchResult = artworkRepository.getArtworkById(id)
        searchResult.fold(
            onSuccess = { artwork ->
                if(artwork.primaryImage.isNotBlank()
                    || artwork.primaryImageSmall.isNotBlank()) {
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
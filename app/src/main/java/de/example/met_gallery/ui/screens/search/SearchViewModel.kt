package de.example.met_gallery.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.network.RequestFailedException
import de.example.met_gallery.network.ArtworkRepository
import de.example.met_gallery.network.NoArtworkFoundException
import de.example.met_gallery.network.SearchArtworksUseCase
import de.example.met_gallery.ui.screens.search.state.ObjectListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

internal class SearchViewModel (
    private val artworkRepository: ArtworkRepository,
    // private val navigator: NavController,
    searchArtworks: SearchArtworksUseCase
) : ViewModel() {
    val uiState:
            MutableStateFlow<ObjectListUiState> = MutableStateFlow(ObjectListUiState.Loading)

    private val _search: MutableStateFlow<String> = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

//  init {
//        viewModelScope.launch {
//            search.collect {
//
//            }
//        }
//    }
//
//    val uiState = search.transform { query ->
//        emit(ObjectListUiState.Loading)
//        val state = try {
//            ObjectListUiState.Success(
//                artworkRepository.getArtworks(query)
//            )
//        } catch (e: IOException) {
//            ObjectListUiState.Error(e)
//        } catch (e: NoArtworkFoundException) {
//            ObjectListUiState.Error(e)
//        } catch (e: Exception) {
//            ObjectListUiState.Error(e)
//        }
//        emit(state)
//    }
//
//    .combine(objectListUiState) { uiState, artworkState ->
//        if (artworkState is ObjectListUiState.Success) {
//            ObjectListUiState.Success(
//                ObjectList(
//                    uiState.objects.total,
//                    (artworkState as ObjectListUiState.Success).objects.artworks
//                )
//            )
//        }
//    }

    fun getArtworks() {
        uiState.update { ObjectListUiState.Loading }
        viewModelScope.launch {
            uiState.update {
                try {
                    ObjectListUiState.Success(
                        artworkRepository.getArtworks(_search.value)
                    )
                } catch (e: NoArtworkFoundException) {
                    ObjectListUiState.Error(e)
                } catch (e: IOException) {
                    ObjectListUiState.Error(e)
                } catch (e: Exception) {
                    ObjectListUiState.Error(e)
                }
            }
        }
    }

    fun getArtworkById(id: Int) {
        // already fetched
        if (getLocalArtworkById(id) != null) return

        viewModelScope.launch {
            val searchResult = artworkRepository.getArtworkById(id)
            searchResult.fold(
                onSuccess = { artwork ->
                    if(artwork.primaryImage.isNotBlank()
                        || artwork.primaryImageSmall.isNotBlank()) {
                        try {
                            uiState.update { currentState ->
                                if (currentState is ObjectListUiState.Success) {
                                    val currentArtworks =
                                        currentState.objects.artworks.toMutableMap()

                                    currentArtworks[id] = artwork

                                    ObjectListUiState.Success(
                                        ObjectList(
                                            currentState.objects.total,
                                            currentArtworks.toMap()
                                        )
                                    )
                                } else {
                                    currentState
                                }
                            }
                        } catch (e: Exception) {
                            ObjectListUiState.Error(e)
                        }
                    } else {
                        removeObjectFromList(id)
                    }
                },
                onFailure = { exception ->
                    if(exception is RequestFailedException) {
                        removeObjectFromList(id)
                    }
                }
            )
        }
    }

    fun getLocalArtworkById(id: Int): Artwork? {
        if(uiState.value is ObjectListUiState.Success) {
            return (uiState.value as ObjectListUiState.Success).objects.artworks[id]
        }
        return null
    }

    fun setSearch(search : String) {
        _search.value = search
    }

    fun getSearch(): String {
        return search.value
    }

    // filter ID out of list so that Card doesn't get rendered in UI
    fun removeObjectFromList(id: Int) {
        uiState.update { currentState ->
            if (currentState is ObjectListUiState.Success) {
                val newArtworks = currentState.objects.artworks.filter { it.key != id }
                if(newArtworks.isEmpty()) {
                    ObjectListUiState.Error(
                        NoArtworkFoundException(
                            message = "Could not find Artworks matching: ${_search.value}"
                        )
                    )
                } else {
                    ObjectListUiState.Success(
                        ObjectList(
                            currentState.objects.total,
                            newArtworks
                        )
                    )
                }
            } else {
                currentState
            }
        }
    }

//    fun onEvent(event: SearchScreenEvent) {
//        when (event) {
//            SearchScreenEvent.NavigateToDetail -> navigator.navigateUp()
//            SearchScreenEvent.Refresh ->
//        }
//    }
}
package de.example.met_gallery.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import okio.IOException

internal class SearchViewModel (
    private val artworkRepository: ArtworkRepository,
    // private val navigator: NavController,
    searchArtworks: SearchArtworksUseCase
) : ViewModel() {
    var objectListUiState: ObjectListUiState by mutableStateOf(ObjectListUiState.Loading)
        private set

    private val _artworks: MutableStateFlow<Map<Int, Artwork>> = MutableStateFlow(emptyMap())
    val artworks: StateFlow<Map<Int, Artwork>> = _artworks.asStateFlow()

    private val _search: MutableStateFlow<String> = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val _errors: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())

    fun getArtworks() {
        viewModelScope.launch {
            objectListUiState = ObjectListUiState.Loading
            objectListUiState = try {
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

//    init {
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
//    }.combine(artworks) { uiState, artworkState ->
//        if (uiState is ObjectListUiState.Success) {
//            ObjectListUiState.Success(
//                ObjectList(
//                    uiState.objects.total,
//                    artworkState
//                )
//            )
//        }
//    }

    fun getArtworkById(id: Int) {
        // already fetched
        if (getLocalArtworkById(id) != null) return

        viewModelScope.launch {
            val searchResult = artworkRepository.getArtworkById(id)
            searchResult.fold(
                onSuccess = { artwork ->
                    if(artwork.primaryImage.isNotBlank()
                        || artwork.primaryImageSmall.isNotBlank()) {
//                        objectListUiState = try {
//                            val updatedMap = (objectListUiState as ObjectListUiState.Success)
//                                .objects.artworks.filter {it.key == id}
//                            updatedMap.plus(Pair(id, artwork))
//                            ObjectListUiState.Success(
//                                ObjectList(
//                                    (objectListUiState as ObjectListUiState.Success).objects.total,
//                                    updatedMap
//                                )
//                            )
//                    } catch (e: Exception) {
//                    ObjectListUiState.Error(e)
//                }
                        _artworks.value += Pair(artwork.id, artwork)
                    } else {
                        removeObjectFromList(id)
                    }
                },
                onFailure = { exception ->
                    if(exception is RequestFailedException) {
                        removeObjectFromList(id)
                        _errors.value += id
                    }
                }
            )
        }
    }
    /*
                    if(artwork.primaryImage.isNotBlank()
                        || artwork.primaryImageSmall.isNotBlank()) {
                        objectListUiState = try {
                            val updatedMap = (objectListUiState as ObjectListUiState.Success)
                                .objects.artworks.filter {it.key == id}
                            updatedMap.plus(Pair(id, artwork))
                            ObjectListUiState.Success(
                                ObjectList(
                                    (objectListUiState as ObjectListUiState.Success).objects.total,
                                    updatedMap
                                )
                            )
                        } catch (e: Exception) {
                            ObjectListUiState.Error(e)
                        }
                    } else {
                        removeObjectFromList(id)
                    }
     */

    fun getLocalArtworkById(id: Int): Artwork? {
        return _artworks.value.getOrElse(id) { null }
    // (objectListUiState as ObjectListUiState.Success).objects.artworks.get(id)
    }

    fun setSearch(search : String) {
        _search.value = search
    }

    fun getSearch(): String {
        return search.value
    }

    // filter ID out of list so that Card doesn't get rendered in UI
    fun removeObjectFromList(id: Int) {
        if(objectListUiState is ObjectListUiState.Success) {
            val objectListSuccess = (objectListUiState as ObjectListUiState.Success)
            objectListUiState = ObjectListUiState.Success(
                ObjectList(
                    objectListSuccess.objects.total,
                    objectListSuccess.objects.objectIds.filter { it != id }
                )
            )
        }
    }

//    fun onEvent(event: SearchScreenEvent) {
//        when (event) {
//            SearchScreenEvent.NavigateToDetail -> navigator.navigateUp()
//            SearchScreenEvent.Refresh -> TODO()
//        }
//    }
}
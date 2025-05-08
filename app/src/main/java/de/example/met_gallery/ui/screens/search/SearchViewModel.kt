package de.example.met_gallery.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.example.met_gallery.fake.FakeDataSource
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
    val objectListUiState:
            MutableStateFlow<ObjectListUiState> = MutableStateFlow(ObjectListUiState.Loading)

    private val _search: MutableStateFlow<String> = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val _errors: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())

    fun getArtworks() {
        viewModelScope.launch {
            objectListUiState.update { ObjectListUiState.Loading }
            objectListUiState.update {
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

        var tmp: Artwork = FakeDataSource.artworkOne

        viewModelScope.launch {
            val searchResult = artworkRepository.getArtworkById(id)
            searchResult.fold(
                onSuccess = { artwork ->
                    if(artwork.primaryImage.isNotBlank()
                        || artwork.primaryImageSmall.isNotBlank()) {
                        try {
                            objectListUiState.update { currentState ->
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
                        _errors.value += id
                    }
                    tmp = FakeDataSource.artworkTwo
                }
            )
            Log.d("ARTWORKS", "${Pair(id, tmp)}")
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
        if(objectListUiState.value is ObjectListUiState.Success) {
            return (objectListUiState.value as ObjectListUiState.Success).objects.artworks[id]
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
        Log.d("ARTWORKS", "REMOVE ID $id")
        objectListUiState.update { currentState ->
            if (currentState is ObjectListUiState.Success) {
                val currentObjects = currentState.objects
                ObjectListUiState.Success(
                    ObjectList(
                        currentObjects.total,
                        currentObjects.artworks.filter { it.key != id }
                    )
                )
            } else {
                currentState
            }
        }
    }

//    fun onEvent(event: SearchScreenEvent) {
//        when (event) {
//            SearchScreenEvent.NavigateToDetail -> navigator.navigateUp()
//            SearchScreenEvent.Refresh -> TODO()
//        }
//    }
}
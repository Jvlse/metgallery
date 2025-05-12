package de.example.met_gallery.ui.screens.search.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.network.ArtworkRepository
import de.example.met_gallery.network.NoArtworkFoundException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

internal class SearchViewModel(
    private val artworkRepository: ArtworkRepository,
    // private val navigator: NavController,
) : ViewModel() {

    val artworkImageMap = MutableStateFlow<Map<Int, Result<Artwork>>>(mapOf())

    val query = MutableSharedFlow<String>(replay = 1).apply { tryEmit("") }

    val searchArtwork = query.map { query ->
        try {
            Result.success(artworkRepository.getArtworks(query))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: NoArtworkFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    val uiState = combine(searchArtwork, artworkImageMap, query) { searchArtworkResult, images, query ->
        searchArtworkResult
            .fold(
                onSuccess = { artworkSearchObjectList ->
                    val artworkWithImageMap = mutableMapOf<Int, Artwork?>()

                    artworkSearchObjectList.artworks.forEach {
                        val result = images[it.key]
                        if (result != null) {
                            if (result.isSuccess == true) {
                                val artworkWithImage = result.getOrNull()
                                if (artworkWithImage != null && (
                                        artworkWithImage.primaryImage.isNotBlank() ||
                                            artworkWithImage.primaryImageSmall.isNotBlank()
                                        )
                                ) {
                                    artworkWithImageMap.put(it.key, artworkWithImage)
                                }
                            }
                        } else {
                            artworkWithImageMap.put(it.key, null)
                        }
                    }

                    SearchUiState(
                        query,
                        ObjectListState.Success(
                            ObjectList(
                                artworkWithImageMap.size,
                                artworkWithImageMap.toMap()
                            )
                        )
                    )
                },
                onFailure = {
                    SearchUiState(query, ObjectListState.Error(it as java.lang.Exception))
                }
            )
    }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, SearchUiState("", ObjectListState.Loading))

    fun getArtworkById(id: Int) {
        if (getLocalArtworkById(id) != null) return

        viewModelScope.launch {
            val result = artworkRepository.getArtworkById(id)
            artworkImageMap.update {
                val mutableMap = it.toMutableMap()
                mutableMap.put(id, result)
                mutableMap
            }
        }
    }

    fun getLocalArtworkById(id: Int): Artwork? {
        if (uiState.value.objectListState is ObjectListState.Success) {
            return (uiState.value.objectListState as ObjectListState.Success).objects.artworks[id]
        }
        return null
    }

    fun search(search: String) {
        query.tryEmit(search)
    }
}

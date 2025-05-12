package de.example.met_gallery.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.network.ArtworkRepository
import de.example.met_gallery.network.NoArtworkFoundException
import de.example.met_gallery.network.SearchArtworksUseCase
import de.example.met_gallery.ui.screens.search.state.ObjectListState
import de.example.met_gallery.ui.screens.search.state.SearchUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

internal class SearchViewModel(
    private val artworkRepository: ArtworkRepository,
    // private val navigator: NavController,
    searchArtworks: SearchArtworksUseCase,
) : ViewModel() {

    val artworkImageMap = MutableStateFlow< Map<Int, Result<Artwork>>>(mapOf())

    val query = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }

    val searchArtwork = query.map { query ->
        try {
            if (query != null) {
                Result.success(artworkRepository.getArtworks(query))
            } else {
                Result.success(null)
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: NoArtworkFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    val uiState2 = combine(searchArtwork, artworkImageMap,query) { searchArtworkResult, images,query ->
        searchArtworkResult
            .fold(onSuccess = { artworkSearchObjectList ->
                if (artworkSearchObjectList == null) return@combine SearchUiState(query, ObjectListState.Empty)

                val artworkWithImageMap = mutableMapOf<Int, Artwork?>()

                artworkSearchObjectList.artworks.forEach { it ->
                   val result =  images[it.key]
                    if(result!=null){
                        if(result.isSuccess == true){
                            val artworkWithImage = result.getOrNull()
                            if(artworkWithImage!=null && (artworkWithImage.primaryImage.isNotBlank() ||
                                    artworkWithImage.primaryImageSmall.isNotBlank())) {
                                artworkWithImageMap.put(it.key, artworkWithImage)
                            }
                        }
                    }else{
                        artworkWithImageMap.put(it.key,null)
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
            }, onFailure = {
                SearchUiState(query, ObjectListState.Error(NoArtworkFoundException(it.message?:"")))
            }
            )
    }.stateIn(viewModelScope, SharingStarted.Lazily, SearchUiState(null, ObjectListState.Empty))

    fun getArtworkById(id: Int) {
        if (getLocalArtworkById(id) != null) return


        viewModelScope.launch {
            val result = artworkRepository.getArtworkById(id)
            artworkImageMap.update {
                val mutableMap = it.toMutableMap()
                mutableMap.put(id,result)
                mutableMap
            }
        }
    }

    fun getLocalArtworkById(id: Int): Artwork? {
        if (uiState2.value.objectListState is ObjectListState.Success) {
            return (uiState2.value.objectListState as ObjectListState.Success).objects.artworks[id]
        }
        return null
    }

    fun setSearch(search: String) {
        query.tryEmit( search)
    }
}

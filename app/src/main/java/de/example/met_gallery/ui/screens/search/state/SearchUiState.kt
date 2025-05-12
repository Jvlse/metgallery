package de.example.met_gallery.ui.screens.search.state

import de.example.met_gallery.model.ObjectList
import java.lang.Exception

sealed interface ObjectListState {
    object Loading : ObjectListState
    data class Success(
        val objects: ObjectList,
    ) : ObjectListState

    data class Error(
        val e: Exception
    ) : ObjectListState
}

data class SearchUiState(val query: String, val objectListState: ObjectListState)

package de.example.met_gallery.ui.screens.search.state

import de.example.met_gallery.model.ObjectList
import java.lang.Exception

sealed interface ObjectListUiState {
    object Loading : ObjectListUiState
    data class Success(
        val objects: ObjectList,
    ) : ObjectListUiState
    data class Error(
        val e: Exception
    ) : ObjectListUiState
}
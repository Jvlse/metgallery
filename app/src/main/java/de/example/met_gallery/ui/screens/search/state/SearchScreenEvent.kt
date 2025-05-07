package de.example.met_gallery.ui.screens.search.state

internal sealed interface SearchScreenEvent {
    data object NavigateToDetail : SearchScreenEvent
    data object Refresh : SearchScreenEvent
}
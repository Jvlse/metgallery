package de.example.met_gallery.ui.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import de.example.met_gallery.ui.screens.search.ArtworkUiState
import de.example.met_gallery.ui.screens.search.ErrorScreen
import de.example.met_gallery.ui.screens.search.LoadingScreen
import de.example.met_gallery.ui.screens.search.SearchViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDetailScreen(
    detailViewModel: DetailViewModel,
    searchViewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    when (searchViewModel.artworkUiStateList.value[0]) {
        is ArtworkUiState.Loading -> LoadingScreen(navController)
        is ArtworkUiState.Success -> SimpleArtworkScreen(
            detailViewModel = detailViewModel,
            searchViewModel = searchViewModel,
            navController = navController,
        )
        is ArtworkUiState.Error -> ErrorScreen({}, modifier = modifier.fillMaxSize())
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SimpleArtworkScreen(
    detailViewModel: DetailViewModel,
    searchViewModel: SearchViewModel,
    navController: NavController,
) {
    detailViewModel.setArtwork((searchViewModel.artworkUiStateList.value[0] as ArtworkUiState.Success).artwork!!)
    DetailScreen(
        detailViewModel = detailViewModel,
        navController = navController,
    )
}
package de.example.met_gallery.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.example.met_gallery.ui.screens.search.SearchViewModel
import de.example.met_gallery.ui.screens.detail.DetailScreen
import de.example.met_gallery.ui.screens.detail.DetailViewModel
import de.example.met_gallery.ui.screens.search.SearchScreen

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val detailViewModel = koinViewModel<DetailViewModel>()
    val searchViewModel = koinViewModel<SearchViewModel>()
    searchViewModel.getArtworks(searchViewModel.getSearch())

    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(
                searchViewModel = searchViewModel,
                objectListUiState = searchViewModel.objectListUiState,
                navController = navController,
                modifier = modifier,
            )
        }
        composable(
            route = "detail/{objectId}",
            arguments = listOf(
                navArgument("objectId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
            val artwork = searchViewModel.artworks.value.first { it.id == objectId }
            detailViewModel.setArtwork(artwork)
            DetailScreen(
                detailViewModel = detailViewModel,
                navController = navController,
            )
        }
    }
}

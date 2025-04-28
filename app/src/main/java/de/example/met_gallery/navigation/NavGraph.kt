package de.example.met_gallery.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.example.met_gallery.ui.screens.search.SearchViewModel
import de.example.met_gallery.ui.screens.search.SimpleSearchScreen
import de.example.met_gallery.ui.screens.detail.DetailScreen
import de.example.met_gallery.ui.screens.detail.DetailViewModel
import de.example.met_gallery.ui.screens.detail.SimpleDetailScreen
import de.example.met_gallery.ui.screens.search.SearchScreen

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val searchViewModel = koinViewModel<SearchViewModel>()
    val detailViewModel = koinViewModel<DetailViewModel>()

    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(
                searchViewModel = searchViewModel,
                objectListUiState = searchViewModel.objectListUiState,
                artworkUiState = searchViewModel.artworkUiStateList,
                contentPadding = PaddingValues(0.dp),
                navController = navController,
                modifier = modifier,
            )
        }
        composable("simpleSearch") {
            SimpleSearchScreen(
                searchViewModel = searchViewModel,
                objectListUiState = searchViewModel.objectListUiState,
                contentPadding = PaddingValues(0.dp),
                navController = navController,
                modifier = modifier,
            )
        }
        composable(
            route = "detail/{objectId}/{index}",
            arguments = listOf(
                navArgument("objectId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
            val artwork = searchViewModel.artworks.value.first() { it?.id == objectId }
            if(artwork != null) {
                detailViewModel.setArtwork(artwork)
                DetailScreen(
                    detailViewModel = detailViewModel,
                    navController = navController,
                    modifier = modifier,
                )
            }
        }
        composable(
            route = "simpleDetail/{objectId}/{index}",
            arguments = listOf(
                navArgument("objectId") { type = NavType.IntType },
                navArgument("index") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
            val artwork = searchViewModel.getArtworkById(objectId, 0)
            SimpleDetailScreen(
                detailViewModel = detailViewModel,
                searchViewModel = searchViewModel,
                navController = navController,
                modifier = modifier,
            )
        }
    }
}

package de.example.met_gallery.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import de.example.met_gallery.ui.screens.detail.DetailScreen
import de.example.met_gallery.ui.screens.detail.DetailViewModel
import de.example.met_gallery.ui.screens.detail.SimpleDetailScreen
import de.example.met_gallery.ui.screens.search.ArtworkGrid
import de.example.met_gallery.ui.screens.search.ObjectListUiState
import de.example.met_gallery.ui.screens.search.SearchScreen
import de.example.met_gallery.ui.screens.search.SimpleArtworkGrid

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
                navController = navController,
                onSuccessState = {
                    ArtworkGrid(
                        searchViewModel = searchViewModel,
                        objectList = (searchViewModel.objectListUiState
                                as ObjectListUiState.Success).objects,
                        contentPadding = PaddingValues(0.dp),
                        modifier = modifier
                            .fillMaxSize()
                            .padding(
                                PaddingValues(
                                    top = it.calculateTopPadding() / 1.3f,
                                    bottom = 0.dp,
                                )),
                        navController = navController,
                    )
                },
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

        composable("simpleSearch") {
            SearchScreen(
                searchViewModel = searchViewModel,
                objectListUiState = searchViewModel.objectListUiState,
                navController = navController,
                onSuccessState = {
                    SimpleArtworkGrid(
                        objectList = (searchViewModel.objectListUiState
                                as ObjectListUiState.Success).objects,
                        contentPadding = PaddingValues(0.dp),
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    top = it.calculateTopPadding() / 1.3f,
                                    bottom = 0.dp,
                                )),
                        navController = navController,
                    )
                },
                modifier = modifier,
            )
        }
        composable(
            route = "simpleDetail/{objectId}",
            arguments = listOf(
                navArgument("objectId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
            searchViewModel.getArtworkById(objectId, 0)
            SimpleDetailScreen(
                detailViewModel = detailViewModel,
                searchViewModel = searchViewModel,
                navController = navController,
                modifier = modifier,
            )
        }
    }
}

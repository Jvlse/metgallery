package de.example.met_gallery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.example.met_gallery.ui.screens.detail.DetailScreen
import de.example.met_gallery.ui.screens.search.SearchScreen
import de.example.met_gallery.ui.screens.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val searchViewModel = koinViewModel<SearchViewModel>()

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable(Routes.SEARCH) {
            SearchScreen(
                searchViewModel = searchViewModel,
                navController = navController,
                modifier = modifier,
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("objectId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val objectId = backStackEntry.arguments?.getInt("objectId") ?: -1
            val artwork = searchViewModel.getLocalArtworkById(objectId)
            if(artwork != null) {
                DetailScreen(
                    artwork = artwork,
                    navController = navController,
                )
            } else {
                navController.navigate(Routes.SEARCH)
            }
        }
    }
}

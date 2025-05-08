package de.example.met_gallery.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.example.met_gallery.R
import de.example.met_gallery.navigation.Routes
import de.example.met_gallery.network.NoArtworkFoundException
import de.example.met_gallery.ui.screens.search.state.ObjectListUiState

@Composable
fun ErrorScreen(
    objectListUiState: ObjectListUiState.Error,
    retryAction: () -> Unit,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = @Composable {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    shape = RoundedCornerShape(16.dp),
                )
            } },
        content = { innerPadding ->
            Box (
                modifier = Modifier.padding(innerPadding)
            ) {
                when (objectListUiState.e) {
                    is NoArtworkFoundException ->
                        NoArtworkFoundErrorScreen(
                            snackbarHostState = snackbarHostState
                        )
                    else ->
                        NoInternetErrorScreen(
                            retryAction = retryAction,
                            navController = navController,
                            snackbarHostState = snackbarHostState
                        )
                }
            }
        }
    )
}

@Composable
fun NoArtworkFoundErrorScreen(
    snackbarHostState: SnackbarHostState,
) {
    val snackbarMessage = stringResource(R.string.no_artworks_found_matching_the_search)
    LaunchedEffect(
        remember { derivedStateOf { snackbarHostState } }
    ) {
        snackbarHostState.showSnackbar(
            message = snackbarMessage,
            duration = SnackbarDuration.Short,
        )
    }
}

@Composable
fun NoInternetErrorScreen(
    retryAction: () -> Unit,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val snackbarMessage = stringResource(R.string.no_internet_connection)
    val actionLabel = stringResource(R.string.retry)
    LaunchedEffect(
        remember { derivedStateOf { snackbarHostState } }
    ) {
        if (
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Indefinite,
            ) == SnackbarResult.ActionPerformed
        ) {
            retryAction
            navController.navigate(Routes.SEARCH)
        }
    }
    Box (
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.WifiOff,
            contentDescription = stringResource(R.string.no_internet_connection),
            modifier = Modifier
                .size(64.dp)
                .padding(end = 8.dp)
        )
    }
}

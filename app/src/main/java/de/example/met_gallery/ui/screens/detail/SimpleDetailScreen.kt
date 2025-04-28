package de.example.met_gallery.ui.screens.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.example.met_gallery.model.Artwork
import androidx.core.net.toUri
import de.example.met_gallery.ui.screens.detail.DetailScreen
import de.example.met_gallery.ui.screens.search.ArtworkUiState
import de.example.met_gallery.ui.screens.search.ErrorScreen
import de.example.met_gallery.ui.screens.search.LoadingScreen
import de.example.met_gallery.ui.screens.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel

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
        is ArtworkUiState.Loading -> LoadingScreen()
        is ArtworkUiState.Success -> SimpleArtworkScreen(
            detailViewModel = detailViewModel,
            searchViewModel = searchViewModel,
            navController = navController,
            modifier = modifier,
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
    modifier: Modifier = Modifier,
) {
    detailViewModel.setArtwork((searchViewModel.artworkUiStateList.value[0] as ArtworkUiState.Success).artwork!!)
    DetailScreen(
        detailViewModel = detailViewModel,
        navController = navController,
        modifier = modifier,
    )
}
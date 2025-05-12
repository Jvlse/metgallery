package de.example.met_gallery.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.example.met_gallery.R
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.navigation.Routes
import de.example.met_gallery.ui.screens.common.ErrorScreen
import de.example.met_gallery.ui.screens.common.LoadingCard
import de.example.met_gallery.ui.screens.common.LoadingScreen
import de.example.met_gallery.ui.screens.search.state.ObjectListState
import de.example.met_gallery.ui.screens.search.state.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    searchViewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchBar(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = uiState.query,
                        onQueryChange = {
                            searchViewModel.search(it)
                        },
                        onSearch = { searchViewModel.search(it) },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text(stringResource(R.string.search_keyword)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    )
                },
                expanded = false,
                onExpandedChange = { },
                content = { }
            )
        },
    ) { innerPadding ->
        when (uiState.objectListState) {
            is ObjectListState.Loading -> LoadingScreen(navController)
            is ObjectListState.Success ->
                ArtworkGrid(
                    searchViewModel = searchViewModel,
                    objectList = (uiState.objectListState as ObjectListState.Success).objects,
                    contentPadding = contentPadding,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(
                            PaddingValues(
                                top = innerPadding.calculateTopPadding() / 1.3f,
                                bottom = 0.dp,
                            )
                        ),
                    navController = navController,
                )
            is ObjectListState.Error -> {
                ErrorScreen(
                    uiState = (uiState.objectListState as ObjectListState.Error),
                    retryAction = { searchViewModel.search(uiState.query) },
                    navController = navController
                )
            }
        }
    }
}

@Composable
internal fun ArtworkGrid(
    searchViewModel: SearchViewModel,
    objectList: ObjectList,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(125.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        val artworkList = objectList.artworks
        itemsIndexed(artworkList.keys.toList(), key = { index, id -> id }) { index, id ->
            val artwork = artworkList[id]
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (artwork != null) {
                    ArtworkCard(
                        artwork = artwork,
                        navController = navController
                    )
                } else {
                    LoadingCard()
                    LaunchedEffect(
                        remember { derivedStateOf { gridState } }
                    ) {
                        searchViewModel.getArtworkById(id)
                    }
                }
            }
        }
    }
}

@Composable
fun ArtworkCard(
    artwork: Artwork,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            onClick = { navController.navigate(Routes.detailsScreen(artwork.id)) },
        ) {
            DisplayArtworkImage(artwork)
        }
    }
}

@Composable
fun DisplayArtworkImage(artwork: Artwork) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(
                artwork.primaryImageSmall.ifBlank {
                    artwork.primaryImage
                }
            ).crossfade(true).build(),
        contentDescription = artwork.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth()
    )
}

//@SuppressLint("StateFlowValueCalledInComposition")
//@Preview
//@Composable
//private fun SearchScreenPreview() {
//    val navController = rememberNavController()
//    val repository = FakeArtworkRepository()
//
//    val viewModel = SearchViewModel(
//        artworkRepository = repository,
//        searchArtworks = SearchArtworksUseCase(repository)
//    )
//    viewModel.uiState.value = SearchUiState.Success(FakeDataSource.objectList)
//
//    SearchScreen(
//        searchViewModel = viewModel,
//        navController = navController,
//        modifier = Modifier,
//        retryAction = { },
//    )
//}
//
//@SuppressLint("StateFlowValueCalledInComposition")
//@Preview
//@Composable
//private fun LoadingSearchScreenPreview() {
//    val navController = rememberNavController()
//    val repository = FakeArtworkRepository()
//
//    val viewModel = SearchViewModel(
//        artworkRepository = repository,
//        searchArtworks = SearchArtworksUseCase(repository)
//    )
//    viewModel.uiState.value = SearchUiState.Loading
//
//    SearchScreen(
//        searchViewModel = viewModel,
//        navController = navController,
//        modifier = Modifier,
//        retryAction = { },
//    )
//}

//@SuppressLint("StateFlowValueCalledInComposition")
//@Preview
//@Composable
//private fun NoInternetErrorScreenPreview() {
//    val navController = rememberNavController()
//    val repository = FakeArtworkRepository()
//
//    val viewModel = SearchViewModel(
//        artworkRepository = repository,
//        searchArtworks = SearchArtworksUseCase(repository)
//    )
//    viewModel.uiState.value = SearchUiState.Error(RequestFailedException(""))
//
//    SearchScreen(
//        searchViewModel = viewModel,
//        navController = navController,
//        modifier = Modifier,
//        retryAction = { },
//    )
//}
//
//@SuppressLint("StateFlowValueCalledInComposition")
//@Preview
//@Composable
//private fun NoArtworkFoundErrorScreenPreview() {
//    val navController = rememberNavController()
//    val repository = FakeArtworkRepository()
//
//    val viewModel = SearchViewModel(
//        artworkRepository = repository,
//        searchArtworks = SearchArtworksUseCase(repository)
//    )
//    viewModel.uiState.value = SearchUiState.Error(NoArtworkFoundException(""))
//
//    SearchScreen(
//        searchViewModel = viewModel,
//        navController = navController,
//        modifier = Modifier,
//        retryAction = { },
//    )
//}

package de.example.met_gallery.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.example.met_gallery.R
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.fake.FakeArtworkRepository
import de.example.met_gallery.fake.FakeDataSource
import de.example.met_gallery.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    objectListUiState: ObjectListUiState,
    navController: NavController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    retryAction: () -> Unit = { searchViewModel.getArtworks(searchViewModel.getSearch());  },
) {
    LaunchedEffect(
        remember { derivedStateOf { objectListUiState } }
    ) {
        searchViewModel.getArtworks(searchViewModel.getSearch())
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val text by searchViewModel.search.collectAsState()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchBar(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = {
                            searchViewModel.setSearch(it)
                            searchViewModel.getArtworks(searchViewModel.getSearch())
                                        },
                        onSearch = { searchViewModel.getArtworks(searchViewModel.getSearch()) },
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
        when (objectListUiState) {
            is ObjectListUiState.Loading -> LoadingScreen(navController)
            is ObjectListUiState.Success ->
                ArtworkGrid(
                    searchViewModel = searchViewModel,
                    objectList = objectListUiState.objects,
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
            is ObjectListUiState.Error -> ErrorScreen(retryAction, navController = navController, modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(navController: NavController) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.details)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, navController: NavController, modifier: Modifier = Modifier) {
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
                val snackbarMessage = stringResource(R.string.no_internet_connection)
                val actionLabel = stringResource(R.string.retry)
                LaunchedEffect(
                    remember { derivedStateOf { snackbarHostState } }
                ) {
                    when(
                        snackbarHostState.showSnackbar(
                        message = snackbarMessage,
                        actionLabel = actionLabel,
                        duration = SnackbarDuration.Indefinite,
                            )
                    ) {
                        SnackbarResult.ActionPerformed -> {
                            retryAction
                            navController.navigate(Routes.SEARCH)
                        }
                        SnackbarResult.Dismissed -> TODO()
                    }
                }
            }
        }
    )
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

@Composable
fun ArtworkGrid(
    searchViewModel: SearchViewModel,
    objectList: ObjectList,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
) {
    val artworks by searchViewModel.artworks.collectAsState()
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(125.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        val ids = objectList.objectIds
        itemsIndexed(ids, key = { index, id -> id }) { index, id ->
            val artwork = artworks[ids[index]]
            Box (
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if(artwork != null) {
                    ArtworkCard(
                        artwork = artwork,
                        navController = navController
                    )
                } else {
                    LoadingCard()
                    LaunchedEffect(
                        remember { derivedStateOf { gridState } },
                        remember { artworks }
                    ) {
                        searchViewModel.getArtworkById(ids[index])
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
    modifier: Modifier = Modifier,
    ) {
    Box (
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Surface (
                modifier = Modifier.fillMaxSize(),
                onClick = { navController.navigate(Routes.detailsScreen(artwork.id)) },
            ) {
                DisplayArtworkImage(artwork = artwork, large = false)
            }
        }
    }
}

@Composable
fun DisplayArtworkImage(artwork: Artwork, large: Boolean = true) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(
                if (artwork.primaryImage.isNotBlank()
                    && (large || artwork.primaryImageSmall.isBlank())) {
                    artwork.primaryImage
                }
                else {
                    artwork.primaryImageSmall
                }
            ).crossfade(true).build(),
        contentDescription = artwork.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LoadingCard() {
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    val navController = rememberNavController()

    val viewModel = SearchViewModel(
        artworkRepository = FakeArtworkRepository()
    )
    SearchScreen(
        searchViewModel = viewModel,
        objectListUiState = ObjectListUiState.Success(FakeDataSource.objectList),
        navController = navController,
        modifier = Modifier,
        retryAction = { },
    )
}

@Preview
@Composable
private fun LoadingSearchScreenPreview() {
    val navController = rememberNavController()

    val viewModel = SearchViewModel(
        artworkRepository = FakeArtworkRepository()
    )
    SearchScreen(
        searchViewModel = viewModel,
        objectListUiState = ObjectListUiState.Loading,
        navController = navController,
        modifier = Modifier,
        retryAction = { },
    )
}

@Preview
@Composable
private fun ErrorScreenPreview() {
    val navController = rememberNavController()

    val viewModel = SearchViewModel(
        artworkRepository = FakeArtworkRepository()
    )
    SearchScreen(
        searchViewModel = viewModel,
        objectListUiState = ObjectListUiState.Error(Exception()),
        navController = navController,
        modifier = Modifier,
        retryAction = { },
    )
}

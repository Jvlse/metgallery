package de.example.met_gallery.ui.screens.search

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    objectListUiState: ObjectListUiState,
    // artworkUiState: StateFlow<List<ArtworkUiState>>,
    navController: NavController,
    onSuccessState: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    retryAction: () -> Unit = { searchViewModel.getObjectIds() },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var text: String by remember { mutableStateOf("")}
    var active = false
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchBar(
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = {
                            active = false
                            searchViewModel.searchArtworks(text)
                                   },
                        expanded = active,
                        onExpandedChange = { active = it },
                        placeholder = { Text("Search keyword") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    )
                },
                expanded = active,
                onExpandedChange = { active = it },
                content = { }
            )
        },
    ) { innerPadding ->
        when (objectListUiState) {
            is ObjectListUiState.Loading -> LoadingScreen(navController)
            is ObjectListUiState.Success -> onSuccessState(innerPadding)
            is ObjectListUiState.Error -> ErrorScreen(retryAction, modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(navController: NavController) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Failed to load")
        Button(onClick = retryAction) {
            Text("Retry")
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ArtworkGrid(
    searchViewModel: SearchViewModel,
    objectList: ObjectList,
    // artworkUiState: StateFlow<List<ArtworkUiState>>,
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
        items(ids.size) { index ->
            searchViewModel.getLocalArtwork(ids[index])
            val artwork = artworks.firstOrNull { it.id == ids[index] }
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
//                        remember { derivedStateOf {
//                            searchViewModel.artworkUiStateList.value[index+1]
//                        } },
                    ) {
                        searchViewModel.getArtworkById(ids[index], index)
                    }
                }
            }
        }
    }
}

//@SuppressLint("StateFlowValueCalledInComposition")
//@Composable
//fun ArtworkCardState(
//    searchViewModel: SearchViewModel,
//    index: Int,
//    artwork: Artwork,
//    navController: NavController,
//    modifier: Modifier = Modifier,
//) {
//    when (searchViewModel.artworkUiStateList.value[index+1]) {
//        is ArtworkUiState.Loading -> LoadingCard()
//        is ArtworkUiState.Success ->
//            ArtworkCard(
//                artwork = artwork,
//                navController = navController
//            )
//        is ArtworkUiState.Error -> LoadingCard()
//    }
//}

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
                onClick = { navController.navigate("detail/${artwork.id}") },
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
            .data( if (artwork.primaryImage.isNotBlank()
                && (large || artwork.primaryImageSmall.isBlank())) artwork.primaryImage
            else artwork.primaryImageSmall)
            .crossfade(true).build(),
        placeholder = null, // TODO placeholder hinzufügen
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

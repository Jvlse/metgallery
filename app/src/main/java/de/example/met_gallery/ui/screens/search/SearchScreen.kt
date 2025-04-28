package de.example.met_gallery.ui.screens.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.ui.screens.detail.DisplayArtworkImage
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    objectListUiState: ObjectListUiState,
    // artworkUiState: StateFlow<List<ArtworkUiState>>,
    navController: NavController,
    modifier: Modifier = Modifier,
    retryAction: () -> Unit = { searchViewModel.getObjectIds() },
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    var text = ""
    var active = false
    Scaffold (
        topBar = {
            SearchBar(
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { active = false },
                        expanded = active,
                        onExpandedChange = { active = it },
                        placeholder = { Text("Search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    )
                },
                expanded = active,
                onExpandedChange = { active = it },
            ) {

            }
        },
    ) { innerPadding ->
        when (objectListUiState) {
            is ObjectListUiState.Loading -> LoadingScreen(navController)
            is ObjectListUiState.Success -> ArtworkGrid(
                searchViewModel,
                objectListUiState.objects,
                contentPadding = contentPadding,
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                navController = navController,
            )
            is ObjectListUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
    // val counter by remember { mutableStateOf(0) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(125.dp),
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Example horizontal spacing
        verticalArrangement = Arrangement.spacedBy(8.dp),   // Example vertical spacing
        contentPadding = contentPadding,
    ) {
        items(objectList.objectIDs.size) { index ->
            val artwork = artworks.firstOrNull() { it?.id == objectList.objectIDs[index] }
            Box (
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if(artwork != null) {
                    ArtworkCardState(
                        searchViewModel = searchViewModel,
                        index = index,
                        artwork = artwork,
                        navController = navController
                    )
                } else {
                    LaunchedEffect(
                        remember { derivedStateOf { gridState } },
                        remember { derivedStateOf { searchViewModel.artworks } },
                        /*remember { derivedStateOf {
                            if (index + 1 in artworkUiState.value.indices) {
                                artworkUiState.value[index+1]
                            } else {
                                artworkUiState.value[index]
                            }
                        } }*/
                    ) {
                        searchViewModel.getArtworkById(objectList.objectIDs[index], index) // == null) {
                            // counter increase und index+counter = neuer index
                            // Problem: kann nicht zwischen nicht geholtem und null Artwork unterscheiden

                    }
                }
            }
        }
        /*
        items(objectList.objectIDs.size) { index ->
            val artwork = artworks.firstOrNull() { it?.id == objectList.objectIDs[index] }
            Box (
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if(artwork != null) {
                    ArtworkCard(
                        viewModel = viewModel,
                        counter = counter,
                        index = index,
                        navController = navController
                    )
                } else {
                    LaunchedEffect(
                        remember { derivedStateOf { gridState } }
                    ) {
                        if(viewModel.getArtworkById(objectList.objectIDs[index]) == null) {
                            // TODO
                        }
                    }
                }
            }
        }
         */
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ArtworkCardState(
    searchViewModel: SearchViewModel,
    index: Int,
    artwork: Artwork,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    when (searchViewModel.artworkUiStateList.value[index+1]) {
        is ArtworkUiState.Loading -> ArtworkCard(
            index = index,
            artwork = artwork,
            navController = navController
        )
        is ArtworkUiState.Success -> ArtworkCard(
            index = index,
            artwork = artwork,
            navController = navController
        )
        is ArtworkUiState.Error -> ErrorScreen({}, modifier = modifier.fillMaxSize())
    }
        /*

    val artwork = artworks.firstOrNull() { it?.id == objectList.objectIDs[index] }
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
                onClick = { navController.navigate("detail/${artwork.id}") }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(if (artwork.primaryImageSmall.isNotBlank()) artwork.primaryImageSmall
                        else artwork.primaryImage)
                        .crossfade(true).build(),
                    error = null, //painterResource(R.drawable.ic_broken_image),
                    placeholder = null, //painterResource(R.drawable.loading_img),
                    contentDescription = artwork.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
         */
}

@Composable
fun ArtworkCard(
    index: Int,
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
                onClick = { navController.navigate("detail/${artwork.id}/$index") },
            ) {
                DisplayArtworkImage(artwork = artwork, large = false)
            }
        }
    }
}

//@Composable
//fun LoadingCard() {
//    Row (
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .height(200.dp)
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(16.dp))
//    ) {
//        CircularProgressIndicator()
//    }
//}

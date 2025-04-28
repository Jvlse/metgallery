package de.example.met_gallery.ui.screens.search

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import de.example.met_gallery.model.ObjectList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchScreen(
    searchViewModel: SearchViewModel,
    objectListUiState: ObjectListUiState,
    navController: NavController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    retryAction: () -> Unit = { searchViewModel.getObjectIds() },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Search")
                },
                actions = {
                    IconButton(
                        onClick = { /* do something */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        when (objectListUiState) {
            is ObjectListUiState.Loading -> LoadingScreen(navController)
            is ObjectListUiState.Success -> SimpleArtworkGrid(
                objectList = objectListUiState.objects,
                contentPadding = contentPadding,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                navController = navController,
            )
            is ObjectListUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
        }
        Log.d("AAA","$objectListUiState")
    }
}

@Composable
fun SimpleArtworkGrid(
    objectList: ObjectList,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(75.dp),
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Example horizontal spacing
        verticalArrangement = Arrangement.spacedBy(8.dp),   // Example vertical spacing
        contentPadding = contentPadding,
    ) {
        items(objectList.objectIDs) { id ->
            Surface (
                onClick = { navController.navigate("simpleDetail/${id}/${0}") }
            ) {
                SimpleArtworkCard(
                    id = id,
                )
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SimpleArtworkCard(
    id: Int,
    modifier: Modifier = Modifier,
) {
    Box (
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Card(
            modifier = modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.small,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$id")
            }
        }
    }
}
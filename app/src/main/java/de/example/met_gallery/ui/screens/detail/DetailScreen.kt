package de.example.met_gallery.ui.screens.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Surface
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

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel,
    onLeave: () -> Unit = { detailViewModel.leaveDetailScreen() },
    navController: NavController,
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp(); onLeave }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { innerPadding ->
        ArtworkScreen(artwork = detailViewModel.artwork.value!!, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ArtworkScreen(
    artwork: Artwork,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Surface (
                onClick = { /* Open Image Fullscreen */ }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    DisplayArtworkImage(artwork)
                }
            }
        }
        Text("by ${artwork.constituents?.get(0)?.name ?: "Unknown"}")
        // ${artwork.artistPrefix}
        ArtworkDetail(artwork)
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
        contentDescription = artwork.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ArtworkDetail(artwork: Artwork) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(artwork.title)
                }
                append(", " + artwork.objectName)
            }
        )
        Show("Department:", artwork.department)
        Show("Culture:", artwork.culture)
        Show("Period:", artwork.period)
        Show("Portfolio:", artwork.portfolio)
        // show("Classification:", artwork.classification)
        Show("Rights:", artwork.rightsAndReproduction)

        val context = LocalContext.current
        if (artwork.objectUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, artwork.objectUrl.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open MET Webpage")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        PrintAdditionalImages(artwork = artwork)
    }
}

@Composable
fun PrintAdditionalImages(artwork: Artwork) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show images if URLs are not blank
        (listOf(
            artwork.primaryImage,
        ) + artwork.additionalImages).filter { it.isNotBlank() }.forEach { url ->
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Surface (
                    onClick = { /* TODO Open Image Fullscreen */ }
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun Show(label: String, value: String) {
    if (value.isNotBlank()) {
        Text("$label $value")
    }
}

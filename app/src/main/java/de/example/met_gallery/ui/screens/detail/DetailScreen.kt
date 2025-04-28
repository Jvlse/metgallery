package de.example.met_gallery.ui.screens.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import de.example.met_gallery.ui.screens.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ){ innerPadding ->
        ArtworkScreen(artwork = detailViewModel.artwork.value!!, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ArtworkScreen(
    artwork: Artwork,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
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
                        .height(500.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
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
fun DisplayArtworkImage(artwork: Artwork) {
    if(artwork.primaryImage.isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(artwork.primaryImage)
                .crossfade(true).build(),
            error = null, //painterResource(R.drawable.ic_broken_image),
            placeholder = null, //painterResource(R.drawable.loading_img),
            contentDescription = artwork.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(artwork.primaryImageSmall)
                .crossfade(true).build(),
            error = null, //painterResource(R.drawable.ic_broken_image),
            placeholder = null, //painterResource(R.drawable.loading_img),
            contentDescription = artwork.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ArtworkDetail(artwork: Artwork) {
    val imageModifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 300.dp)
        .clip(RoundedCornerShape(12.dp))
        .padding(vertical = 8.dp)

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

        // Show images if URLs are not blank
        listOf(
            artwork.primaryImage,
            artwork.primaryImageSmall
        ).filter { it.isNotBlank() }.forEach { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = imageModifier
            )
        }

        artwork.additionalImages
            .filter { it.isNotBlank() }
            .forEach { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = imageModifier
                )
            }
    }
}

@Composable
fun Show(label: String, value: String) {
    if (value.isNotBlank()) {
        Text("$label $value")
    }
}

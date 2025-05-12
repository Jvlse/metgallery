package de.example.met_gallery.ui.screens.detail

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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.example.met_gallery.R
import de.example.met_gallery.fake.FakeDataSource
import de.example.met_gallery.model.Artwork
import de.example.met_gallery.ui.screens.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    artwork: Artwork,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.details)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        DisplayArtworkDetails(artwork = artwork, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun DisplayArtworkDetails(
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                DisplayArtworkWithPlaceholder(artwork)
            }
        }
        Text(
            stringResource(
                R.string.by,
                artwork.constituents?.get(0)?.name ?: stringResource(R.string.unknown)
            )
        )
        ArtworkDetail(artwork)
    }
}

@Composable
fun DisplayArtworkWithPlaceholder(artwork: Artwork) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(
                artwork.primaryImage.ifBlank {
                    artwork.primaryImageSmall
                }
            ).crossfade(true).build(),
        contentDescription = artwork.title,
        placeholder = painterResource(R.drawable.loading_img),
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
        Show(stringResource(R.string.department), artwork.department)
        Show(stringResource(R.string.culture), artwork.culture)
        Show(stringResource(R.string.made_during), artwork.period)
        Show(stringResource(R.string.portfolio), artwork.portfolio)
        Show(stringResource(R.string.rights), artwork.rightsAndReproduction)

        val context = LocalContext.current
        if (artwork.objectUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, artwork.objectUrl.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.open_met_webpage))
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
        (artwork.additionalImages + artwork.primaryImage).filter { it.isNotBlank() }
            .forEach { url ->
                Card(
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = stringResource(R.string.more_images_of, artwork.title),
                        modifier = Modifier.fillMaxSize()
                    )
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

@Preview
@Composable
private fun LoadingDetailScreenPreview() {
    val navController = rememberNavController()
    LoadingScreen(
        navController = navController
    )
}

@Preview
@Composable
private fun DetailScreenPreview() {
    val navController = rememberNavController()
    DetailScreen(
        FakeDataSource.artworkOne,
        navController = navController
    )
}

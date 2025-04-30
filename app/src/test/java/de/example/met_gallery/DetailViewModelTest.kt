package de.example.met_gallery

import de.example.met_gallery.fake.FakeDataSource
import de.example.met_gallery.ui.screens.detail.DetailViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DetailViewModelTest {
    private val viewModel = DetailViewModel()

    @Test
    fun detailViewModel_setArtwork_verifyArtworkSet() {
        val artwork = FakeDataSource.artworkOne
        viewModel.setArtwork(artwork)

        assertEquals(
            viewModel.artwork.value,
            artwork
        )
    }

    @Test
    fun detailViewModel_setArtwork_verifyArtworkChanged() {
        val artworkOne = FakeDataSource.artworkOne
        viewModel.setArtwork(artworkOne)

        assertEquals(
            viewModel.artwork.value,
            artworkOne
        )

        val artworkTwo = FakeDataSource.artworkTwo
        viewModel.setArtwork(artworkTwo)

        assertEquals(
            viewModel.artwork.value,
            artworkTwo
        )
    }

    @Test
    fun detailViewModel_leaveDetailScreen_verifyArtworkNull() {
        val artwork = FakeDataSource.artworkOne
        viewModel.setArtwork(artwork)

        assertEquals(
            viewModel.artwork.value,
            artwork
        )

        viewModel.leaveDetailScreen()

        assertEquals(
            viewModel.artwork.value,
            null
        )
    }
}

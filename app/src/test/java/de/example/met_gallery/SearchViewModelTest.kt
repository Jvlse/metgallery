package de.example.met_gallery

import de.example.met_gallery.fake.FakeArtworkRepository
import de.example.met_gallery.fake.FakeDataSource
import de.example.met_gallery.network.SearchArtworksUseCase
import de.example.met_gallery.rules.TestDispatcherRule
import de.example.met_gallery.ui.screens.search.SearchViewModel
import de.example.met_gallery.ui.screens.search.state.ObjectListUiState
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule

class SearchViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    val repository = FakeArtworkRepository()

    private val viewModel = SearchViewModel(
        artworkRepository = repository,
        searchArtworks = SearchArtworksUseCase(repository)
    )

    @Test
    fun searchViewModel_getArtworks_verifyObjectListUiStateSuccess() = runTest {
        viewModel.getArtworks()

        assertEquals(
            ObjectListUiState.Success(FakeDataSource.objectList),
            viewModel.objectListUiState
        )
    }

    @Test
    fun searchViewModel_getLocalArtworkById_addsArtwork() = runTest {
        val artwork = FakeDataSource.artworkOne
        val id = artwork.id
        viewModel.getArtworkById(id)

        assertEquals(
            viewModel.getLocalArtworkById(id),
            artwork
        )
    }

    @Test
    fun searchViewModel_getLocalArtworkById_addMultipleArtwork() = runTest {
        val artworkOne = FakeDataSource.artworkOne
        val idOne = artworkOne.id
        viewModel.getArtworkById(idOne)

        val artworkTwo = FakeDataSource.artworkTwo
        val idTwo = artworkTwo.id
        viewModel.getArtworkById(idTwo)

        assertEquals(
            viewModel.getLocalArtworkById(idOne),
            artworkOne
        )
        assertEquals(
            viewModel.getLocalArtworkById(idTwo),
            artworkTwo
        )
    }

    @Test
    fun searchViewModel_getLocalArtworkById_removeArtworkWithNoUrl() = runTest {
        val artworkNoUrl = FakeDataSource.artworkNoUrl
        val idNoUrl = artworkNoUrl.id
        viewModel.getArtworkById(idNoUrl)

        val artworkOne = FakeDataSource.artworkOne
        val idOne = artworkOne.id
        viewModel.getArtworkById(idOne)

        assertEquals(
            viewModel.getLocalArtworkById(idOne),
            artworkOne
        )
        assertEquals(
            viewModel.getLocalArtworkById(idNoUrl),
            null
        )
    }
}
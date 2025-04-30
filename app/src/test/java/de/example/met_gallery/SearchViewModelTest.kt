package de.example.met_gallery

import de.example.met_gallery.fake.FakeArtworkRepository
import de.example.met_gallery.fake.FakeDataSource
import de.example.met_gallery.rules.TestDispatcherRule
import de.example.met_gallery.ui.screens.search.ObjectListUiState
import de.example.met_gallery.ui.screens.search.SearchViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule

class SearchViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private val viewModel = SearchViewModel(
        artworkRepository = FakeArtworkRepository()
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
    fun searchViewModel_getLocalArtworkById_findsCorrectArtwork() = runTest {
        val artwork = FakeDataSource.artworkOne
        val id = artwork.id
        viewModel.getArtworkById(FakeDataSource.artworkOne)

        assertEquals(
            viewModel.getLocalArtworkById(id),
            FakeDataSource.artworkOne
        )
    }
}
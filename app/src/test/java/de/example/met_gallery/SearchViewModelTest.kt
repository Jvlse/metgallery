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

/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SearchViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun searchViewModel_get_verifyMarsUiStateSuccess() =
        runTest {
            val viewModel = SearchViewModel(
                artworkRepository = FakeArtworkRepository()
            )

            viewModel.getArtworks()

            assertEquals(
                ObjectListUiState.Success(FakeDataSource.objectList),
                viewModel.objectListUiState
            )
        }
}
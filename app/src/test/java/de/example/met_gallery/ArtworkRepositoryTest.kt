package de.example.met_gallery

import de.example.met_gallery.fake.FakeArtworkApi
import de.example.met_gallery.fake.FakeDataSource
import de.example.met_gallery.network.ArtworkDataSourceImpl
import de.example.met_gallery.network.ArtworkRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtworkRepositoryTest {
    @Test
    fun ArtworkRepository_getArtworkss_verifyObjectList() =
        runTest {
            val repository = ArtworkRepositoryImpl(
                ArtworkDataSourceImpl (
                    api = FakeArtworkApi()
                )
            )
            assertEquals(
                FakeDataSource.objectList,
                repository.getArtworks("")
            )
        }
}
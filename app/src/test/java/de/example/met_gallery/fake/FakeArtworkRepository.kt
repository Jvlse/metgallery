package de.example.met_gallery.fake

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.network.ArtworkRepository

class FakeArtworkRepository : ArtworkRepository {
    override suspend fun getArtworks(query: String): ObjectList {
        return FakeDataSource.objectList
    }

    override suspend fun getArtworkById(id: Int): Result<Artwork> {
        return try {
            Result.success(FakeDataSource.artworkOne)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

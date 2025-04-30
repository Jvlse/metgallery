package de.example.met_gallery.fake

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import de.example.met_gallery.network.ArtworkApi
import retrofit2.Response

class FakeArtworkApi : ArtworkApi {
    override suspend fun searchArtworks(query: String, hasImages: Boolean): Response<ObjectList> {
        return Response.success(FakeDataSource.objectList)
    }

    override suspend fun getArtworkById(id: Int): Response<Artwork> {
        return Response.success(FakeDataSource.artworkOne)
    }
}

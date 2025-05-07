package de.example.met_gallery.fake

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ApiObjectList
import de.example.met_gallery.network.ArtworkApi
import retrofit2.Response

class FakeArtworkApi : ArtworkApi {
    override suspend fun searchArtworks(query: String, hasImages: Boolean): Response<ApiObjectList> {
        return Response.success(FakeDataSource.apiObjectList)
    }

    override suspend fun getArtworkById(id: Int): Response<Artwork> {
        return Response.success(FakeDataSource.artworkOne)
    }
}

package de.example.met_gallery.network

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtworkApi {
    @GET("search?q=&hasImages=true")
    suspend fun getArtworks(): Response<ObjectList>

    @GET("objects/{objectID}")
    suspend fun getArtworkById(@Path("objectID") id: Int): Response<Artwork>
}
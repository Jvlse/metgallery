package de.example.met_gallery.network

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtworkApi {
    @GET("search?q=&hasImages=true")
    suspend fun getArtworks(): Response<ObjectList>

    @GET("objects/{objectID}")
    suspend fun getArtworkById(@Path("objectID") id: Int): Response<Artwork>

    @GET("search")
    suspend fun searchArtworks(
        @Query("q") query: String,
        @Query("hasImages") hasImages: Boolean = true
    ): Response<ObjectList>
}
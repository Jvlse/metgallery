package de.example.met_gallery.network

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList

interface ArtworkDataSource {
    suspend fun getArtworkById(id: Int): Artwork
    suspend fun searchArtworks(query: String): ObjectList
}

class ArtworkDataSourceImpl (private val api: ArtworkApi) : ArtworkDataSource {
    override suspend fun searchArtworks(query: String): ObjectList {
        val response = api.searchArtworks("\"" + query + "\"")
        val responseBody = response.body()

        val artworks = if (response.isSuccessful && responseBody != null) {
            responseBody
        } else {
            throw Exception("Request failed")
        }

        return artworks
    }

    override suspend fun getArtworkById(id: Int): Artwork {
        val response = api.getArtworkById(id)
        val responseBody = response.body()

        val artwork = if (response.isSuccessful && responseBody != null) {
            responseBody
        } else {
            throw Exception("Artwork not found")
        }

        return artwork
    }
}
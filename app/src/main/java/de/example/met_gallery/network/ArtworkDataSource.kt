package de.example.met_gallery.network

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList

interface ArtworkDataSource {
    suspend fun getArtworkById(id: Int): Artwork
    suspend fun getArtworks(query: String): ObjectList
}

class RequestFailedException(message: String) : Exception(message)

class ArtworkDataSourceImpl (private val api: ArtworkApi) : ArtworkDataSource {
    override suspend fun getArtworks(query: String): ObjectList {
        val response = api.searchArtworks("\"" + query + "\"")
        val responseBody = response.body()

        val artworks = if (response.isSuccessful && responseBody != null) {
            responseBody
        } else {
            throw RequestFailedException("Could not find Artworks matching: $query")
        }

        return artworks
    }

    override suspend fun getArtworkById(id: Int): Artwork {
        val response = api.getArtworkById(id)
        val responseBody = response.body()

        val artwork = if (response.isSuccessful && responseBody != null) {
            responseBody
        } else {
            throw RequestFailedException("Could not find artwork with ID: $id")
        }

        return artwork
    }
}
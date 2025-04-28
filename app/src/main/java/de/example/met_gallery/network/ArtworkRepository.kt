package de.example.met_gallery.network

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.ObjectList


interface ArtworkRepository {
    suspend fun getArtworks(): ObjectList
    suspend fun getArtworkById(id: Int): Artwork?
    suspend fun searchArtworks(query: String): ObjectList
}

class ArtworkRepositoryImpl(private val dataSource: ArtworkDataSource) : ArtworkRepository {
    override suspend fun getArtworks(): ObjectList {
        return dataSource.getArtworks()
    }

    override suspend fun searchArtworks(query: String): ObjectList {
        return dataSource.searchArtworks(query)
    }

    override suspend fun getArtworkById(id: Int): Artwork? {
        return dataSource.getArtworkById(id)
    }
}
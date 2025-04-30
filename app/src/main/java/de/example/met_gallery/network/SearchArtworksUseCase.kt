package de.example.met_gallery.network

class SearchArtworksUseCase constructor(
    private val artworkRepository: ArtworkRepository,
) {

    operator suspend fun invoke(query: String) = artworkRepository.getArtworks(query)
}
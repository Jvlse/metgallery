package de.example.met_gallery.model

data class ObjectList(
    val total: Int,
    val artworks: Map<Int, Artwork?>,
)
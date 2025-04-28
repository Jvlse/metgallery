package de.example.met_gallery.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ObjectList (
    @SerialName(value = "total")
    val total: Int, // total number of objects
    @SerialName(value = "objectIDs")
    val objectIDs: List<Int>, // eachs objects id
)
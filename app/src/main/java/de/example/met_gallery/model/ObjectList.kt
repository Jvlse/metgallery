package de.example.met_gallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ObjectList (
    @Json(name = "total")
    val total: Int, // total number of objects
    @Json(name = "objectIDs")
    val objectIds: List<Int>, // eachs objects id
)
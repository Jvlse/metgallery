package de.example.met_gallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiObjectList (
    @Json(name = "total")
    val total: Int,
    @Json(name = "objectIDs")
    val objectIds: List<Int>?,
)

fun ApiObjectList.toEntity() : ObjectList {
    return ObjectList(
            total = total,
            artworks = objectIds.orEmpty().associateWith { null }
    )
}
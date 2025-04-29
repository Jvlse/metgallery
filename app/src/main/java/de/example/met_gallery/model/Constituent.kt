package de.example.met_gallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
data class Constituent (
    @Json(name = "constituentID")
    val constituentId: Int,
    val role: String,
    val name: String,
    @Json(name = "constituentULAN_URL")
    val constituentUlanUrl: String,
    @Json(name = "constituentWikidata_URL")
    val constituentWikidataUrl: String,
    val gender: String
)
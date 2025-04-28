package de.example.met_gallery.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Constituent (
    @SerialName(value = "constituentID")
    val constituentID: Int,
    val role: String,
    val name: String,
    @SerialName(value = "constituentULAN_URL")
    val constituentULAN_URL: String,
    @SerialName(value = "constituentWikidata_URL")
    val constituentWikidata_URL: String,
    val gender: String
)
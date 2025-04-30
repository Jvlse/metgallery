package de.example.met_gallery.fake

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.Constituent
import de.example.met_gallery.model.ObjectList

object FakeDataSource {
    private const val idOne = 1
    private const val idTwo = 2
    private const val imgOne = "url.one"
    private const val artistIdOne= 1
    private const val artistNameOne = "Artist One"
    private const val artistGenderOne = "Gender One"
    val objectList = ObjectList(
        total = 2,
        objectIds = listOf(idOne,idTwo)
    )

    val artwork = Artwork(
        id = idOne,
        primaryImage = imgOne,
        primaryImageSmall = "",
        additionalImages = emptyList(),
        constituents = listOf(
            Constituent(
                constituentId = artistIdOne,
                role = "",
                name = artistNameOne,
                constituentUlanUrl = "",
                constituentWikidataUrl = "",
                gender = artistGenderOne
            )
        ),
        department = "",
        objectName = "",
        title = "",
        culture = "",
        period = "",
        dynasty = "",
        reign = "",
        portfolio = "",
        rightsAndReproduction = "",
        objectUrl = ""
    )
}

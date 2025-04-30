package de.example.met_gallery.fake

import de.example.met_gallery.model.Artwork
import de.example.met_gallery.model.Constituent
import de.example.met_gallery.model.ObjectList

object FakeDataSource {

    private const val idOne = 1
    private const val imgOne = "url.one"
    private const val artistIdOne= 1
    private const val artistNameOne = "Artist One"
    private const val artistGenderOne = "Gender One"
    private const val idTwo = 2
    private const val artistIdTwo = 2
    private const val artistNameTwo = "Artist Two"
    private const val artistGenderTwo = "Gender Two"

    val blank = ""

    val objectList = ObjectList(
        total = 2,
        objectIds = listOf(idOne,idTwo)
    )

    val artworkOne = Artwork(
        id = idOne,
        primaryImage = imgOne,
        primaryImageSmall = blank,
        additionalImages = emptyList(),
        constituents = listOf(
            Constituent(
                constituentId = artistIdOne,
                role = blank,
                name = artistNameOne,
                constituentUlanUrl = blank,
                constituentWikidataUrl = blank,
                gender = artistGenderOne
            )
        ),
        department = blank,
        objectName = blank,
        title = blank,
        culture = blank,
        period = blank,
        dynasty = blank,
        reign = blank,
        portfolio = blank,
        rightsAndReproduction = blank,
        objectUrl = blank
    )

    val artworkTwo = Artwork(
        id = idTwo,
        primaryImage = blank,
        primaryImageSmall = blank,
        additionalImages = emptyList(),
        constituents = listOf(
            Constituent(
                constituentId = artistIdTwo,
                role = blank,
                name = artistNameTwo,
                constituentUlanUrl = blank,
                constituentWikidataUrl = blank,
                gender = artistGenderTwo
            )
        ),
        department = blank,
        objectName = blank,
        title = blank,
        culture = blank,
        period = blank,
        dynasty = blank,
        reign = blank,
        portfolio = blank,
        rightsAndReproduction = blank,
        objectUrl = blank
    )
}

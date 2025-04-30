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
    private const val imgTwo = "url.two"
    private const val artistIdTwo = 2
    private const val artistNameTwo = "Artist Two"
    private const val artistGenderTwo = "Gender Two"
    private const val idThree = 3
    private const val artistIdThree = 3
    private const val artistNameThree = "Artist Three"
    private const val artistGenderThree = "Gender Three"

    val blank = ""

    val objectList = ObjectList(
        total = 3,
        objectIds = listOf(idOne, idTwo, idThree)
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
        primaryImage = imgTwo,
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

    val artworkNoUrl = Artwork(
        id = idThree,
        primaryImage = blank,
        primaryImageSmall = blank,
        additionalImages = emptyList(),
        constituents = listOf(
            Constituent(
                constituentId = artistIdThree,
                role = blank,
                name = artistNameThree,
                constituentUlanUrl = blank,
                constituentWikidataUrl = blank,
                gender = artistGenderThree
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

package de.example.met_gallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artwork (
    @Json(name = "objectID")
    val id: Int,
    @Json(name = "isHighlight")
    val isHighlight: Boolean, // important in the collection
    @Json(name = "accessionNumber")
    val accesionNumber: String, // not always unique
    @Json(name = "accessionYear")
    val accesionYear: String, // year it was acquired
    @Json(name = "isPublicDomain")
    val isPublicDomain: Boolean,
    @Json(name = "primaryImage")
    val primaryImage: String,
    @Json(name = "primaryImageSmall")
    val primaryImageSmall: String,
    @Json(name = "additionalImages")
    val additionalImages: List<String>,
    @Json(name = "constituents")
    val constituents: List<Constituent>?, // associated artists
    @Json(name = "department")
    val department: String,
    @Json(name = "objectName")
    val objectName: String, // Describes Type of the object ("Dress", "Painting", "Photograph", or "Vase")
    @Json(name = "title")
    val title: String, // identifying phrase
    @Json(name = "culture")
    val culture: String, // ("Afghan", "British", "North African")
    @Json(name = "period")
    val period: String, // ("Edo period (1615–1868)")
    @Json(name = "dynasty")
    val dynasty: String, // ("Kingdom of Benin", "Dynasty 12")
    @Json(name = "reign")
    val reign: String, // monarch or ruler under which an object was created ("Amenhotep III", "Darius I", "Louis XVI")
    @Json(name = "portfolio")
    val portfolio: String, // "Birds of America", "The Hudson River Portfolio", "Speculum Romanae Magnificentiae"
    @Json(name = "artistPrefix")
    val artistPrefix: String, // "In the Style of", "Possibly by", "Written in French by"
    @Json(name = "creditLine")
    val creditLine: String, // Where and when accuired by museum
    @Json(name = "classification")
    val classification: String, // ("Paintings", "Ceramics")
    @Json(name = "rightsAndReproduction")
    val rightsAndReproduction: String, // "© 2018 Estate of Pablo Picasso / Artists Rights Society (ARS), New York"
    @Json(name = "objectURL")
    val objectUrl: String, // "https://www.metmuseum.org/art/collection/search/45734"
    /*@Json(name = "tags")
    val tags: List<String> */
)

/* "tags" = [
{
    "term": "Birds",
    "AAT_URL": "http://vocab.getty.edu/page/aat/300266506",
    "Wikidata_URL": "https://www.wikidata.org/wiki/Q5113"
}
],
*/
    /*
"repository": "Metropolitan Museum of Art, New York, NY",

"artistDisplayName": "Kiyohara Yukinobu",
"artistDisplayBio": "Japanese, 1643–1682",
"artistSuffix": "",
"artistAlphaSort": "Kiyohara Yukinobu",
"artistNationality": "Japanese",
"artistBeginDate": "1643",
"artistEndDate": "1682",
"artistGender": "Female",
"artistWikidata_URL": "https://www.wikidata.org/wiki/Q11560527",
"artistULAN_URL": "http://vocab.getty.edu/page/ulan/500034433",
"objectDate": "late 17th century",
"objectBeginDate": 1667,
"objectEndDate": 1682,
"medium": "Hanging scroll; ink and color on silk",
"dimensions": "46 5/8 x 18 3/4 in. (118.4 x 47.6 cm)",
"geographyType": "",
"city": "",
"state": "",
"county": "",
"country": "",
"region": "",
"subregion": "",
"locale": "",
"locus": "",
"rightsAndReproduction": "",
"linkResource": "",
"objectWikidata_URL": "https://www.wikidata.org/wiki/Q29910832",
*/
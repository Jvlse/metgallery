package de.example.met_gallery.navigation

object Routes {
    const val SEARCH = "search"
    const val DETAIL_ARG_OBJECT_ID = "objectId"
    const val DETAIL = "detail/{${DETAIL_ARG_OBJECT_ID}}"

    fun detailsScreen(objectId: Int): String {
        return "detail/$objectId"
    }
}
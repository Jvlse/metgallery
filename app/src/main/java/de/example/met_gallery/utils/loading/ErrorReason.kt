package de.example.met_gallery.utils.loading

sealed class ErrorReason {

    data object NetworkConnection : ErrorReason()

    data object NoArtworkFound : ErrorReason()

    data class Unspecified(val message: String?) : ErrorReason()
}

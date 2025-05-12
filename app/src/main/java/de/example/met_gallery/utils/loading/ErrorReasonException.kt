package de.example.met_gallery.utils.loading

data class ErrorReasonException(
    val reason: ErrorReason,
) : Exception()

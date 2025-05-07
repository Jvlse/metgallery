package de.af.template.utils.loading

data class ErrorReasonException(
    val reason: ErrorReason,
) : Exception()

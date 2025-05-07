package de.af.template.utils.loading

sealed class ErrorReason {

    data object NetworkConnection : ErrorReason()

    data class Unspecified(val message: String?) : ErrorReason()
}

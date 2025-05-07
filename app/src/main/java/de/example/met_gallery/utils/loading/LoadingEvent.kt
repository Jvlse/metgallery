package de.af.template.utils.loading

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformWhile

sealed interface LoadingEvent<out T> {

    data object Loading : LoadingEvent<Nothing>

    sealed interface Result<out T> : LoadingEvent<T>

    data class Success<out T>(val data: T) : Result<T>

    @JvmInline
    value class Error(val reason: ErrorReason) : Result<Nothing>
}

inline fun <T, R> LoadingEvent<T>.map(transform: (T) -> R): LoadingEvent<R> = when (this) {
    is LoadingEvent.Error -> this
    is LoadingEvent.Loading -> this
    is LoadingEvent.Success -> LoadingEvent.Success(transform(this.data))
}

/**
 * Emits all loading events from the upstream including the first [LoadingEvent.Result] and stops
 * collecting the upstream after the first [LoadingEvent.Result].
 */
fun <T> Flow<LoadingEvent<T>>.completeAfterFirstResult() = transformWhile {
    emit(it)
    it !is LoadingEvent.Result
}

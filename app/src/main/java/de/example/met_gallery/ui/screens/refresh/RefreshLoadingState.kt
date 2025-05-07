package de.af.template.ui.common.loading.refresh

import de.af.template.utils.loading.ErrorReason
import de.af.template.utils.loading.LoadingEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.runningFold

sealed interface RefreshLoadingState<out T> {

    sealed interface Initial : RefreshLoadingState<Nothing> {

        data object Loading : Initial

        @JvmInline
        value class Error(val reason: ErrorReason) : Initial
    }

    data class Data<T>(
        val data: T,
        val error: ErrorReason?,
        val isLoading: Boolean,
    ) : RefreshLoadingState<T>
}

/**
 * Combine the previous exclusive api state with the latest one with a runningFold.
 * Keeps the data when the Flow switches from data to loading again.
 */
internal fun <T> Flow<LoadingEvent<T>>.aggregateEventsToRefreshLoadingState(): Flow<RefreshLoadingState<T>> =
    runningFold<LoadingEvent<T>, RefreshLoadingState<T>>(
        initial = RefreshLoadingState.Initial.Loading,
    ) { accumulator, value ->
        accumulator + value
    }.drop(1) // Drop the initial empty value

// Detekt has a false-positive here because of disabled type resolution. It could possibly be
//  resolved later on by enabling it https://detekt.dev/type-resolution.html
@Suppress("UnusedPrivateMember")
private operator fun <T> RefreshLoadingState<T>.plus(loadingEvent: LoadingEvent<T>): RefreshLoadingState<T> {
    return when (this) {
        is RefreshLoadingState.Initial -> when (loadingEvent) {
            is LoadingEvent.Error -> RefreshLoadingState.Initial.Error(loadingEvent.reason)
            is LoadingEvent.Loading -> RefreshLoadingState.Initial.Loading
            is LoadingEvent.Success -> RefreshLoadingState.Data(
                data = loadingEvent.data,
                isLoading = false,
                error = null,
            )
        }
        is RefreshLoadingState.Data -> copy(
            data = if (loadingEvent is LoadingEvent.Success) loadingEvent.data else data,
            error = if (loadingEvent is LoadingEvent.Error) loadingEvent.reason else null,
            isLoading = loadingEvent is LoadingEvent.Loading,
        )
    }
}
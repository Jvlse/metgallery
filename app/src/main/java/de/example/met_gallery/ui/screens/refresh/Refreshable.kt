package de.af.template.ui.common.loading.refresh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.af.template.utils.loading.LoadingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface Refreshable<T> {

    val flow: Flow<RefreshLoadingState<T>>

    fun refresh()
}

private class RefreshableImpl<T>(
    private val scope: CoroutineScope,
    started: SharingStarted,
    initialValue: RefreshLoadingState<T>,
    createFlow: () -> Flow<LoadingEvent<T>>,
) : Refreshable<T> {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        // This emits, but has no effect until someone actually starts collecting
        tryEmit(Unit)
    }

    override val flow = refreshTrigger
        .flatMapLatest { createFlow() }
        .aggregateEventsToRefreshLoadingState()
        .stateIn(
            scope = scope,
            started = started,
            initialValue = initialValue,
        )

    override fun refresh() {
        scope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}

/**
 * Creates a refreshable out of the given flow of loading events.
 * The refreshable provides a Flow of RefreshLoadingStates (stated in the ViewModel) and a
 * `refresh()` method.
 * It will execute by default and can be re-executed when `refresh()` is called.
 *
 * Note that [SharingStarted.Lazily] will keep the upstream alive, even if the ViewModel is not
 * actively used, but currently only somewhere in the backstack. This is usually not problematic,
 * but can be if the upstream continues to produce values. In that case switch to a better suited
 * [SharingStarted].
 */
fun <T> ViewModel.refreshable(
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Lazily,
    initialValue: RefreshLoadingState<T> = RefreshLoadingState.Initial.Loading,
    createFlow: () -> Flow<LoadingEvent<T>>,
): Refreshable<T> = RefreshableImpl(
    scope = scope,
    started = started,
    initialValue = initialValue,
    createFlow = createFlow,
)
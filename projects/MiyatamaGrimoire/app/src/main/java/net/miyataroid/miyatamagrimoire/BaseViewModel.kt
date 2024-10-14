package net.miyataroid.miyatamagrimoire

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T>: ViewModel() {
    abstract val initialState: T
    val uiState: MutableStateFlow<T> by lazy {
        MutableStateFlow(initialState)
    }
    val state: StateFlow<T>
        get() = uiState
}
package net.miyataroid.miyatamagrimoire

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T>: ViewModel() {
    var _uiState = MutableStateFlow(T())
    var uiState: StateFlow<T> =_uiState.asStateFlow()

    init {
        _uiState = MutableStateFlow<T>(T())
    }
}
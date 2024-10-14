package net.miyataroid.miyatamagrimoire.home

import kotlinx.coroutines.flow.update
import net.miyataroid.miyatamagrimoire.BaseViewModel

class HomeViewModel : BaseViewModel<HomeUiState>() {
    fun select(selectedMode: HomeUiState.SelectedMode) {
        uiState.update {
            it.copy(
                selectedMode = selectedMode,
            )
        }
    }

    override val initialState: HomeUiState
        get() = HomeUiState()
}
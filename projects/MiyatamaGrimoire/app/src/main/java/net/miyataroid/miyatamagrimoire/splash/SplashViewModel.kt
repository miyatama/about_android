package net.miyataroid.miyatamagrimoire.splash

import kotlinx.coroutines.flow.update
import net.miyataroid.miyatamagrimoire.BaseViewModel
import net.miyataroid.miyatamagrimoire.home.HomeUiState

class SplashViewModel: BaseViewModel<SplashUiState>() {
    override val initialState: SplashUiState
        get() = SplashUiState()

    init {
        uiState.update {
            it.copy(
                splashProcessState = SplashUiState.SplashProcessState.FINISH,
            )
        }
    }
}

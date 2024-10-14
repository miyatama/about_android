package net.miyataroid.miyatamagrimoire.view

import kotlinx.coroutines.flow.update
import net.miyataroid.miyatamagrimoire.BaseViewModel
import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings

class GrimoireViewViewModel(
    val depthSettings: DepthSettings,
): BaseViewModel<GrimoireViewUiState>() {
    override val initialState: GrimoireViewUiState
        get() = GrimoireViewUiState()

    init {
        uiState.update {
            it.copy(
               shouldShowDepthEnableDialog = depthSettings.shouldShowDepthEnableDialog(),
            )
        }
    }

    fun setUseDepthForOcclusion(value: Boolean){
        depthSettings.useDepthForOcclusion = value
    }
}
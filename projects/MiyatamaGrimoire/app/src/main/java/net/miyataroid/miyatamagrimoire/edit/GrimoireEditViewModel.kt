package net.miyataroid.miyatamagrimoire.edit

import net.miyataroid.miyatamagrimoire.BaseViewModel
import net.miyataroid.miyatamagrimoire.view.GrimoireViewUiState

class GrimoireEditViewModel: BaseViewModel<GrimoireEditUiState>() {
    override val initialState: GrimoireEditUiState
        get() = GrimoireEditUiState()
}
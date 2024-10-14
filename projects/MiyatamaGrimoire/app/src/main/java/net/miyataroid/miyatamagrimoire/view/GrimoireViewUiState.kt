package net.miyataroid.miyatamagrimoire.view

import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings

data class GrimoireViewUiState (
    val isLoading: Boolean = true,
    val shouldShowDepthEnableDialog: Boolean = false,
)
package net.miyataroid.miyatamagrimoire.view

import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings
import com.google.ar.core.Session
import net.miyataroid.miyatamagrimoire.core.renderer.SampleRender

data class GrimoireViewUiState (
    val isLoading: Boolean = true,
    val shouldShowDepthEnableDialog: Boolean = false,
    val arCoreErrorMessage: String = "",
    val session: Session? = null,
    val renderer: GrimoireViewRenderer? = null,
    val message: String = "",
    val errorMessage: String = "",
)
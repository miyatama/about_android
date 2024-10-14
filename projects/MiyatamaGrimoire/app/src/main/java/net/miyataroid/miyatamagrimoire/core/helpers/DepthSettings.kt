package net.miyataroid.miyatamagrimoire.core.helpers

import android.content.Context
import android.content.SharedPreferences

/** Manages the Occlusion option setting and shared preferences. */
interface DepthSettings {
    var useDepthForOcclusion: Boolean
    var depthColorVisualizationEnabled: Boolean
    var sharedPreferences: SharedPreferences

    fun shouldShowDepthEnableDialog(): Boolean
}

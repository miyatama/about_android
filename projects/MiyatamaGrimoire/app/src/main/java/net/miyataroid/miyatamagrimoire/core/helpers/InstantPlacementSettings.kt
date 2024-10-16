package net.miyataroid.miyatamagrimoire.core.helpers

import android.content.SharedPreferences

interface InstantPlacementSettings {
    companion object {
        const val SHARED_PREFERENCES_ID: String = "SHARED_PREFERENCES_INSTANT_PLACEMENT_OPTIONS"
        const val SHARED_PREFERENCES_INSTANT_PLACEMENT_ENABLED: String = "instant_placement_enabled"
    }
    var isInstantPlacementEnabled: Boolean
}
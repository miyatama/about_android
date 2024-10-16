package net.miyataroid.miyatamagrimoire.core.helpers

import android.content.Context
import android.content.SharedPreferences
import net.miyataroid.miyatamagrimoire.core.helpers.InstantPlacementSettings.Companion.SHARED_PREFERENCES_ID
import net.miyataroid.miyatamagrimoire.core.helpers.InstantPlacementSettings.Companion.SHARED_PREFERENCES_INSTANT_PLACEMENT_ENABLED

class InstantPlacementSettingsImpl(val context: Context): InstantPlacementSettings {
    var sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_ID,
        Context.MODE_PRIVATE
    )

    override var isInstantPlacementEnabled: Boolean = false
        get() = sharedPreferences.getBoolean(
                SHARED_PREFERENCES_INSTANT_PLACEMENT_ENABLED,
                false
            )
        set(value) {
            if (value == field) {
                return
            }

            field = value
            val editor = sharedPreferences.edit()
            editor.putBoolean(SHARED_PREFERENCES_INSTANT_PLACEMENT_ENABLED, isInstantPlacementEnabled)
            editor.apply()
        }
}
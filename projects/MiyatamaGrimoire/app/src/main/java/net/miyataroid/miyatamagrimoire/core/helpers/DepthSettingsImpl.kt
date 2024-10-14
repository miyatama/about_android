package net.miyataroid.miyatamagrimoire.core.helpers

import android.content.Context
import android.content.SharedPreferences

class DepthSettingsImpl(context: Context): DepthSettings {
    companion object {
        const val SHARED_PREFERENCES_ID = "SHARED_PREFERENCES_OCCLUSION_OPTIONS"
        const val SHARED_PREFERENCES_SHOW_DEPTH_ENABLE_DIALOG_OOBE =
            "show_depth_enable_dialog_oobe"
        const val SHARED_PREFERENCES_USE_DEPTH_FOR_OCCLUSION = "use_depth_for_occlusion"
    }

    override var depthColorVisualizationEnabled = false
        get() {
            return field
        }
        set(value) {
            field = value
        }
    override var useDepthForOcclusion = false
        get() {
            return field
        }
        set(value) {
            if (field != value) {
                field = value
                val editor = sharedPreferences.edit()
                editor.putBoolean(SHARED_PREFERENCES_USE_DEPTH_FOR_OCCLUSION, useDepthForOcclusion)
                editor.apply()
            }
        }
    override var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_ID, Context.MODE_PRIVATE)

    init {
        this.useDepthForOcclusion =
            this.sharedPreferences.getBoolean(SHARED_PREFERENCES_USE_DEPTH_FOR_OCCLUSION, false);
    }

    /** Determines if the initial prompt to use depth-based occlusion should be shown. */
    override fun shouldShowDepthEnableDialog(): Boolean {
        // Checks if this dialog has been called before on this device.
        val showDialog = sharedPreferences.getBoolean(SHARED_PREFERENCES_SHOW_DEPTH_ENABLE_DIALOG_OOBE, true)

        if (showDialog) {
            // Only ever shows the dialog on the first time.  If the user wants to adjust these settings
            // again, they can use the gear icon to invoke the settings menu dialog.
            val editor = sharedPreferences.edit()
            editor.putBoolean(SHARED_PREFERENCES_SHOW_DEPTH_ENABLE_DIALOG_OOBE, false)
            editor.apply()
        }

        return showDialog
    }
}
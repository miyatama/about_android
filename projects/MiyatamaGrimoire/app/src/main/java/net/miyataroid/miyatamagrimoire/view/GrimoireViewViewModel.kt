package net.miyataroid.miyatamagrimoire.view

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import com.google.ar.core.Config
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import kotlinx.coroutines.flow.update
import net.miyataroid.miyatamagrimoire.BaseViewModel
import net.miyataroid.miyatamagrimoire.core.helpers.ARCoreSessionLifecycleHelper
import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings
import net.miyataroid.miyatamagrimoire.core.helpers.DisplayRotationHelper
import net.miyataroid.miyatamagrimoire.core.helpers.InstantPlacementSettings
import net.miyataroid.miyatamagrimoire.core.helpers.TrackingStateHelper
import net.miyataroid.miyatamagrimoire.core.renderer.SampleRender
import net.miyataroid.miyatamagrimoire.core.renderer.setupRndering

class GrimoireViewViewModel(
    val appContext: Context,
    val arCoreSessionLifecycleHelper: ARCoreSessionLifecycleHelper,
    val depthSettings: DepthSettings,
    val displayRotationHelper: DisplayRotationHelper,
    val trackingStateHelper: TrackingStateHelper,
    val instantPlacementSettings: InstantPlacementSettings,
) : BaseViewModel<GrimoireViewUiState>() {
    override val initialState: GrimoireViewUiState
        get() = GrimoireViewUiState()

    init {
        val sampleRender = SampleRender(appContext.assets)
        uiState.update {
            it.copy(
                shouldShowDepthEnableDialog = depthSettings.shouldShowDepthEnableDialog(),
                session = arCoreSessionLifecycleHelper.session,
                sampleRender = sampleRender,
            )
        }
        // HelloArActivity#onCreate()
        arCoreSessionLifecycleHelper.exceptionCallback = { exception ->
            val message =
                when (exception) {
                    is UnavailableUserDeclinedInstallationException ->
                        "Please install Google Play Services for AR"

                    is UnavailableApkTooOldException -> "Please update ARCore"
                    is UnavailableSdkTooOldException -> "Please update this app"
                    is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
                    is CameraNotAvailableException -> "Camera not available. Try restarting the app."
                    else -> "Failed to create AR session: $exception"
                }
            uiState.update {
                it.copy(
                    arCoreErrorMessage = message
                )
            }
        }
        arCoreSessionLifecycleHelper.beforeSessionResume = { session ->
            session.configure(
                session.config.apply {
                    lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    depthMode =
                        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            Config.DepthMode.AUTOMATIC
                        } else {
                            Config.DepthMode.DISABLED
                        }
                    instantPlacementMode = InstantPlacementMode.LOCAL_Y_UP
                }
            )
        }

        // injectionにて対応済み
        // depthSettings.onCreate(this)
        // instantPlacementSettings.onCreate(this)
        uiState.update {
            it.copy(
                renderer = GrimoireViewRenderer(
                    depthSettings = depthSettings,
                    instantPlacementSettings = instantPlacementSettings,
                    arCoreSessionHelper = arCoreSessionLifecycleHelper,
                    displayRotationHelper = displayRotationHelper,
                    trackingStateHelper = trackingStateHelper,
                    sampleRender = sampleRender,
                    assetManager = appContext.assets,
                    resources = appContext.resources,
                )
            )
        }
    }

    fun setUseDepthForOcclusion(value: Boolean) {
        depthSettings.useDepthForOcclusion = value
    }

    fun setArCoreSessionLifecycleObserver(lifecycle: Lifecycle) {
        lifecycle.addObserver(arCoreSessionLifecycleHelper)
    }

    fun setArCoreSessionActivity(activity: Activity) {
        arCoreSessionLifecycleHelper.activity = activity
    }
}
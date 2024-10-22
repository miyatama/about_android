package net.miyataroid.miyatamagrimoire.core.helpers

import com.google.ar.core.Session

interface DisplayRotationHelper {
    fun onResume()
    fun onPause()

    fun onSurfaceChanged(width: Int, height: Int)
    fun updateSessionIfNeeded(session: Session)

    fun getCameraSensorRelativeViewportAspectRatio(cameraId: String?): Float

    fun getCameraSensorToDisplayRotation(cameraId: String?): Int
}
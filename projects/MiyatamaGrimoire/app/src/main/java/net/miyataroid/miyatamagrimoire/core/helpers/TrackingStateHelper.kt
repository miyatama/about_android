package net.miyataroid.miyatamagrimoire.core.helpers

import com.google.ar.core.TrackingState

interface TrackingStateHelper {
    fun updateKeepScreenOnFlag(trackingState: TrackingState)
}
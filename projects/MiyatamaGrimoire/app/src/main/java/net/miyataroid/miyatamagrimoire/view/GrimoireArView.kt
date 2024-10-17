package net.miyataroid.miyatamagrimoire.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import net.miyataroid.miyatamagrimoire.R

class GrimoireArView: RelativeLayout, DefaultLifecycleObserver {
    private lateinit var surfaceView: GLSurfaceView
    constructor(context: Context): super(context) {
        View.inflate(context, R.layout.grimoire_ar_view, this)
        surfaceView = this.findViewById(R.id.surfaceview)
    }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }
}
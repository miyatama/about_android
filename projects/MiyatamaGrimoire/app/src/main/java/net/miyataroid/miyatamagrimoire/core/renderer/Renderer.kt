package net.miyataroid.miyatamagrimoire.core.renderer

import android.opengl.GLSurfaceView

interface Renderer {
    /**
     * Called by [SampleRender] when the GL render surface is created.
     *
     *
     * See [GLSurfaceView.Renderer.onSurfaceCreated].
     */
    fun onSurfaceCreated(render: SampleRender?)

    /**
     * Called by [SampleRender] when the GL render surface dimensions are changed.
     *
     *
     * See [GLSurfaceView.Renderer.onSurfaceChanged].
     */
    fun onSurfaceChanged(render: SampleRender?, width: Int, height: Int)

    /**
     * Called by [SampleRender] when a GL frame is to be rendered.
     *
     *
     * See [GLSurfaceView.Renderer.onDrawFrame].
     */
    fun onDrawFrame(render: SampleRender?)
}
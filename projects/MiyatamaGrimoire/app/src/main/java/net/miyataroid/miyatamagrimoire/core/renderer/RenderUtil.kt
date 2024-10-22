package net.miyataroid.miyatamagrimoire.core.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

fun setupRndering(glSurfaceView: GLSurfaceView, renderer: Renderer, sampleRender: SampleRender) {
    glSurfaceView.preserveEGLContextOnPause = true
    glSurfaceView.setEGLContextClientVersion(3)
    glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    glSurfaceView.setRenderer(
        object : GLSurfaceView.Renderer {
            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                if (gl == null || config == null) {
                    return
                }
                GLES30.glEnable(GLES30.GL_BLEND);
                GLError.maybeThrowGLException("Failed to enable blending", "glEnable");
                renderer.onSurfaceCreated(sampleRender)
            }

            override fun onSurfaceChanged(gl:GL10,  w: Int, h: Int) {
                sampleRender.setViewport(w, h)
                renderer.onSurfaceChanged(sampleRender, w, h)
            }

            override fun onDrawFrame(gl: GL10 ) {
                sampleRender.clear(/*framebuffer=*/ null, 0f, 0f, 0f, 1f)
                renderer.onDrawFrame(sampleRender)
            }
        }
    )
    glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    glSurfaceView.setWillNotDraw(false)
}
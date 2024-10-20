package net.miyataroid.miyatamagrimoire.core.renderer

import android.content.res.AssetManager
import android.opengl.GLES30

class SampleRender(
    public val assetManager: AssetManager
) {
    var viewportWidth: Int = 0
    var viewportHeight: Int = 0

    fun setViewport(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
    }

    fun draw(mesh: Mesh , shader: Shader) {
        draw(mesh, shader, /*framebuffer=*/ null)
    }
    fun draw(mesh: Mesh , shader: Shader , framebuffer: Framebuffer?) {
        useFramebuffer(framebuffer)
        shader.lowLevelUse()
        mesh.lowLevelDraw()
    }

    fun clear(framebuffer: Framebuffer?, r: Float, g: Float, b: Float, a: Float) {
        useFramebuffer(framebuffer)
        GLES30.glClearColor(r, g, b, a)
        GLError.maybeThrowGLException("Failed to set clear color", "glClearColor")
        GLES30.glDepthMask(true)
        GLError.maybeThrowGLException("Failed to set depth write mask", "glDepthMask")
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLError.maybeThrowGLException("Failed to clear framebuffer", "glClear")
    }

    fun useFramebuffer(framebuffer: Framebuffer?) {
        var framebufferId: Int
        var viewportWidth: Int
        var viewportHeight: Int
        if (framebuffer == null) {
            framebufferId = 0;
            viewportWidth = this.viewportWidth;
            viewportHeight = this.viewportHeight;
        } else {
            framebufferId = framebuffer.getFramebufferId();
            viewportWidth = framebuffer.width;
            viewportHeight = framebuffer.height;
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, framebufferId);
        GLError.maybeThrowGLException("Failed to bind framebuffer", "glBindFramebuffer");
        GLES30.glViewport(0, 0, viewportWidth, viewportHeight);
        GLError.maybeThrowGLException("Failed to set viewport dimensions", "glViewport");
    }
}
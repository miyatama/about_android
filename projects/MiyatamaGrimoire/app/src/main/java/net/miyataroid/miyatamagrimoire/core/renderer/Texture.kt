/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.miyataroid.miyatamagrimoire.core.renderer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.util.Log
import net.miyataroid.miyatamagrimoire.core.renderer.GLError.maybeLogGLError
import net.miyataroid.miyatamagrimoire.core.renderer.GLError.maybeThrowGLException
import java.io.Closeable
import java.io.IOException
import java.nio.ByteBuffer

/** A GPU-side texture.  */
class Texture @JvmOverloads constructor(
    render: SampleRender?, /* package-private */
    val target: Target, wrapMode: WrapMode, useMipmaps: Boolean = true
) : Closeable {
    var textureIds: IntArray = intArrayOf(0)

    /**
     * Describes the way the texture's edges are rendered.
     *
     * @see [GL_TEXTURE_WRAP_S](https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glTexParameter.xhtml).
     */
    enum class WrapMode(/* package-private */val glesEnum: Int) {
        CLAMP_TO_EDGE(GLES30.GL_CLAMP_TO_EDGE),
        MIRRORED_REPEAT(GLES30.GL_MIRRORED_REPEAT),
        REPEAT(GLES30.GL_REPEAT)
    }

    /**
     * Describes the target this texture is bound to.
     *
     * @see [glBindTexture](https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glBindTexture.xhtml).
     */
    enum class Target(val glesEnum: Int) {
        TEXTURE_2D(GLES30.GL_TEXTURE_2D),
        TEXTURE_EXTERNAL_OES(GLES11Ext.GL_TEXTURE_EXTERNAL_OES),
        TEXTURE_CUBE_MAP(GLES30.GL_TEXTURE_CUBE_MAP)
    }

    /**
     * Describes the color format of the texture.
     *
     * @see [glTexImage2d](https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glTexImage2D.xhtml).
     */
    enum class ColorFormat(val glesEnum: Int) {
        LINEAR(GLES30.GL_RGBA8),
        SRGB(GLES30.GL_SRGB8_ALPHA8)
    }

    /**
     * Construct an empty [Texture].
     *
     *
     * Since [Texture]s created in this way are not populated with data, this method is
     * mostly only useful for creating [Target.TEXTURE_EXTERNAL_OES] textures. See [ ][.createFromAsset] if you want a texture with data.
     */
    init {
        GLES30.glGenTextures(1, textureIds, 0)
        maybeThrowGLException("Texture creation failed", "glGenTextures")

        val minFilter = if (useMipmaps) GLES30.GL_LINEAR_MIPMAP_LINEAR else GLES30.GL_LINEAR

        try {
            GLES30.glBindTexture(target.glesEnum, textureIds[0])
            maybeThrowGLException("Failed to bind texture", "glBindTexture")
            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_MIN_FILTER, minFilter)
            maybeThrowGLException("Failed to set texture parameter", "glTexParameteri")
            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
            maybeThrowGLException("Failed to set texture parameter", "glTexParameteri")

            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_WRAP_S, wrapMode.glesEnum)
            maybeThrowGLException("Failed to set texture parameter", "glTexParameteri")
            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_WRAP_T, wrapMode.glesEnum)
            maybeThrowGLException("Failed to set texture parameter", "glTexParameteri")
        } catch (t: Throwable) {
            close()
            throw t
        }
    }

    override fun close() {
        if (textureIds[0] != 0) {
            GLES30.glDeleteTextures(1, textureIds, 0)
            maybeLogGLError(Log.WARN, TAG, "Failed to free texture", "glDeleteTextures")
            textureIds[0] = 0
        }
    }

    /** Retrieve the native texture ID.  */
    fun getTextureId(): Int {
        return textureIds[0]
    }

    companion object {
        private val TAG: String = Texture::class.java.simpleName

        /** Create a texture from the given asset file name.  */
        @JvmStatic
        @Throws(IOException::class)
        fun createFromAsset(
            render: SampleRender,
            assetFileName: String?,
            wrapMode: WrapMode,
            colorFormat: ColorFormat
        ): Texture {
            val texture = Texture(render, Target.TEXTURE_2D, wrapMode)
            var bitmap: Bitmap? = null
            try {
                // The following lines up to glTexImage2D could technically be replaced with
                // GLUtils.texImage2d, but this method does not allow for loading sRGB images.

                // Load and convert the bitmap and copy its contents to a direct ByteBuffer. Despite its name,
                // the ARGB_8888 config is actually stored in RGBA order.

                bitmap =
                    convertBitmapToConfig(
                        BitmapFactory.decodeStream(render.assetManager.open(assetFileName!!)),
                        Bitmap.Config.ARGB_8888
                    )
                val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
                bitmap.copyPixelsToBuffer(buffer)
                buffer.rewind()

                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture.getTextureId())
                maybeThrowGLException("Failed to bind texture", "glBindTexture")
                GLES30.glTexImage2D(
                    GLES30.GL_TEXTURE_2D,  /*level=*/
                    0,
                    colorFormat.glesEnum,
                    bitmap.width,
                    bitmap.height,  /*border=*/
                    0,
                    GLES30.GL_RGBA,
                    GLES30.GL_UNSIGNED_BYTE,
                    buffer
                )
                maybeThrowGLException("Failed to populate texture data", "glTexImage2D")
                GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
                maybeThrowGLException("Failed to generate mipmaps", "glGenerateMipmap")
            } catch (t: Throwable) {
                texture.close()
                throw t
            } finally {
                bitmap?.recycle()
            }
            return texture
        }

        private fun convertBitmapToConfig(bitmap: Bitmap, config: Bitmap.Config): Bitmap {
            // We use this method instead of BitmapFactory.Options.outConfig to support a minimum of Android
            // API level 24.
            if (bitmap.config == config) {
                return bitmap
            }
            val result = bitmap.copy(config,  /*isMutable=*/false)
            bitmap.recycle()
            return result
        }
    }
}

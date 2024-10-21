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
package net.miyataroid.miyatamagrimoire.core.renderer.arcore

import android.media.Image
import android.opengl.GLES30
import android.util.Log
import com.google.ar.core.ImageFormat
import net.miyataroid.miyatamagrimoire.core.renderer.GLError.maybeLogGLError
import net.miyataroid.miyatamagrimoire.core.renderer.GLError.maybeThrowGLException
import net.miyataroid.miyatamagrimoire.core.renderer.Mesh
import net.miyataroid.miyatamagrimoire.core.renderer.SampleRender
import net.miyataroid.miyatamagrimoire.core.renderer.Shader
import net.miyataroid.miyatamagrimoire.core.renderer.Shader.Companion.createFromAssets
import net.miyataroid.miyatamagrimoire.core.renderer.Texture
import net.miyataroid.miyatamagrimoire.core.renderer.VertexBuffer
import net.miyataroid.miyatamagrimoire.core.renderer.arcore.SpecularCubemapFilter
import java.io.Closeable
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * Filters a provided cubemap into a cubemap lookup texture which is a function of the direction of
 * a reflected ray of light and material roughness, i.e. the LD term of the specular IBL
 * calculation.
 *
 *
 * See https://google.github.io/filament/Filament.md.html#lighting/imagebasedlights for a more
 * detailed explanation.
 */
class SpecularCubemapFilter(
    render: SampleRender?,
    private val resolution: Int,
    private val numberOfImportanceSamples: Int
) : Closeable {
    // We need to create enough shaders and framebuffers to encompass every face of the cubemap. Each
    // color attachment is used by the framebuffer to render to a different face of the cubemap, so we
    // use "chunks" which define as many color attachments as possible for each face. For example, if
    // we have a maximum of 3 color attachments, we must create two shaders with the following color
    // attachments:
    //
    // layout(location = 0) out vec4 o_FragColorPX;
    // layout(location = 1) out vec4 o_FragColorNX;
    // layout(location = 2) out vec4 o_FragColorPY;
    //
    // and
    //
    // layout(location = 0) out vec4 o_FragColorNY;
    // layout(location = 1) out vec4 o_FragColorPZ;
    // layout(location = 2) out vec4 o_FragColorNZ;
    private class Chunk(val chunkIndex: Int, maxChunkSize: Int) {
        val chunkSize: Int
        val firstFaceIndex: Int = chunkIndex * maxChunkSize

        init {
            this.chunkSize = min(
                maxChunkSize.toDouble(),
                (NUMBER_OF_CUBE_FACES - this.firstFaceIndex).toDouble()
            ).toInt()
        }
    }

    private class ChunkIterable(maxNumberOfColorAttachments: Int) : Iterable<Chunk?> {
        val maxChunkSize: Int = min(
            maxNumberOfColorAttachments.toDouble(),
            NUMBER_OF_CUBE_FACES.toDouble()
        ).toInt()
        val numberOfChunks: Int

        init {
            var numberOfChunks = NUMBER_OF_CUBE_FACES / this.maxChunkSize
            if (NUMBER_OF_CUBE_FACES % this.maxChunkSize != 0) {
                numberOfChunks++
            }
            this.numberOfChunks = numberOfChunks
        }

        override fun iterator(): MutableIterator<Chunk> {
            return object : MutableIterator<Chunk> {
                private var chunk = Chunk( /*chunkIndex=*/0, maxChunkSize)

                override fun hasNext(): Boolean {
                    return chunk.chunkIndex < numberOfChunks
                }

                override fun next(): Chunk {
                    val result = this.chunk
                    this.chunk = Chunk(result.chunkIndex + 1, maxChunkSize)
                    return result
                }

                override fun remove() {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    private class ImportanceSampleCacheEntry {
        lateinit var direction: FloatArray
        var contribution: Float = 0f
        var level: Float = 0f
    }

    /** Returns the number of mipmap levels in the filtered cubemap texture.  */
    val numberOfMipmapLevels: Int

    private var radianceCubemap: Texture? = null

    /**
     * Returns the filtered cubemap texture whose contents are updated with each call to [ ][.update].
     */
    var filteredCubemapTexture: Texture? = null

    // Indexed by attachment chunk.
    private val shaders: Array<Shader?>?
    private var mesh: Mesh? = null

    // Using OpenGL directly here since cubemap framebuffers are very involved. Indexed by
    // [mipmapLevel][attachmentChunk].
    private val framebuffers: Array<IntArray?>?

    override fun close() {
        if (framebuffers != null) {
            for (framebufferChunks in framebuffers) {
                GLES30.glDeleteFramebuffers(framebufferChunks!!.size, framebufferChunks, 0)
                maybeLogGLError(
                    Log.WARN, TAG, "Failed to free framebuffers", "glDeleteFramebuffers"
                )
            }
        }
        radianceCubemap?.close()
        if (filteredCubemapTexture != null) {
            filteredCubemapTexture!!.close()
        }
        if (shaders != null) {
            for (shader in shaders) {
                shader!!.close()
            }
        }
    }

    /**
     * Updates and filters the provided cubemap textures from ARCore.
     *
     *
     * This method should be called every frame with the result of {link
     * com.google.ar.core.LightEstimate.acquireEnvironmentalHdrCubeMap()} to update the filtered
     * cubemap texture, accessible via {link getFilteredCubemapTexture()}.
     * TODO com.google.ar.core.LightEstimate.acquireEnvironmentalHdrCubeMap() linkt
     * TODO getFilteredCubemapTexture() link
     *
     * The given [Image]s will be closed by this method, even if an exception occurs.
     */
    fun update(images: Array<Image>) {
        try {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, radianceCubemap!!.getTextureId())
            maybeThrowGLException("Failed to bind radiance cubemap texture", "glBindTexture")

            require(images.size == NUMBER_OF_CUBE_FACES) { "Number of images differs from the number of sides of a cube." }

            for (i in 0 until NUMBER_OF_CUBE_FACES) {
                val image = images[i]
                // Sanity check for the format of the cubemap.
                require(image.format == ImageFormat.RGBA_FP16) { "Unexpected image format for cubemap: " + image.format }
                require(image.height == image.width) { "Cubemap face is not square." }
                require(image.height == resolution) {
                    ("Cubemap face resolution ("
                            + image.height
                            + ") does not match expected value ("
                            + resolution
                            + ").")
                }

                GLES30.glTexImage2D(
                    GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,  /*level=*/
                    0,
                    GLES30.GL_RGBA16F,  /*width=*/
                    resolution,  /*height=*/
                    resolution,  /*border=*/
                    0,
                    GLES30.GL_RGBA,
                    GLES30.GL_HALF_FLOAT,
                    image.planes[0].buffer
                )
                maybeThrowGLException("Failed to populate cubemap face", "glTexImage2D")
            }

            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_CUBE_MAP)
            maybeThrowGLException("Failed to generate cubemap mipmaps", "glGenerateMipmap")

            // Do the filtering operation, filling the mipmaps of ldTexture with the roughness filtered
            // cubemap.
            for (level in 0 until numberOfMipmapLevels) {
                val mipmapResolution = resolution shr level
                GLES30.glViewport(0, 0, mipmapResolution, mipmapResolution)
                maybeThrowGLException("Failed to set viewport dimensions", "glViewport")
                for (chunkIndex in shaders!!.indices) {
                    GLES30.glBindFramebuffer(
                        GLES30.GL_FRAMEBUFFER,
                        framebuffers!![level]!![chunkIndex]
                    )
                    maybeThrowGLException("Failed to bind cubemap framebuffer", "glBindFramebuffer")
                    shaders[chunkIndex]!!.setInt("u_RoughnessLevel", level)
                    shaders[chunkIndex]!!.lowLevelUse()
                    mesh!!.lowLevelDraw()
                }
            }
        } finally {
            for (image in images) {
                image.close()
            }
        }
    }

    private fun initializeLdCubemap() {
        // Initialize mipmap levels of LD cubemap.
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, filteredCubemapTexture!!.getTextureId())
        maybeThrowGLException("Could not bind LD cubemap texture", "glBindTexture")
        for (level in 0 until numberOfMipmapLevels) {
            val mipmapResolution = resolution shr level
            for (face in 0 until NUMBER_OF_CUBE_FACES) {
                GLES30.glTexImage2D(
                    GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face,
                    level,
                    GLES30.GL_RGB16F,  /*width=*/
                    mipmapResolution,  /*height=*/
                    mipmapResolution,  /*border=*/
                    0,
                    GLES30.GL_RGB,
                    GLES30.GL_HALF_FLOAT,  /*data=*/
                    null
                )
                maybeThrowGLException("Could not initialize LD cubemap mipmap", "glTexImage2D")
            }
        }
    }

    @Throws(IOException::class)
    private fun createShaders(render: SampleRender?, chunks: ChunkIterable): Array<Shader?> {
        val importanceSampleCaches = generateImportanceSampleCaches()

        val commonDefines = HashMap<String, String>()
        commonDefines["NUMBER_OF_IMPORTANCE_SAMPLES"] = numberOfImportanceSamples.toString()
        commonDefines["NUMBER_OF_MIPMAP_LEVELS"] = numberOfMipmapLevels.toString()

        val shaders = arrayOfNulls<Shader>(chunks.numberOfChunks)
        for (chunk in chunks) {
            val defines = HashMap(commonDefines)
            for (location in 0 until chunk.chunkSize) {
                defines[ATTACHMENT_LOCATION_DEFINES[chunk.firstFaceIndex + location]] =
                    location.toString()
            }

            // Create the shader and populate its uniforms with the importance sample cache entries.
            shaders[chunk.chunkIndex] =
                createFromAssets(
                    render!!, "shaders/cubemap_filter.vert", "shaders/cubemap_filter.frag", defines
                )
                    .setTexture("u_Cubemap", radianceCubemap!!)
                    .setDepthTest(false)
                    .setDepthWrite(false)
        }

        for (shader in shaders) {
            for (i in importanceSampleCaches.indices) {
                val cache = importanceSampleCaches[i]
                val cacheName = "u_ImportanceSampleCaches[$i]"
                shader!!.setInt("$cacheName.number_of_entries", cache.size)
                for (j in cache.indices) {
                    val entry = cache[j]
                    val entryName = "$cacheName.entries[$j]"
                    shader
                        .setVec3("$entryName.direction", entry!!.direction)
                        .setFloat("$entryName.contribution", entry.contribution)
                        .setFloat("$entryName.level", entry.level)
                }
            }
        }

        return shaders
    }

    private fun createFramebuffers(chunks: ChunkIterable): Array<IntArray?> {
        // Create the framebuffers for each mipmap level.
        val framebuffers = arrayOfNulls<IntArray>(numberOfMipmapLevels)
        for (level in 0 until numberOfMipmapLevels) {
            val framebufferChunks = IntArray(chunks.numberOfChunks)
            GLES30.glGenFramebuffers(framebufferChunks.size, framebufferChunks, 0)
            maybeThrowGLException("Could not create cubemap framebuffers", "glGenFramebuffers")
            for (chunk in chunks) {
                // Set the drawbuffers
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, framebufferChunks[chunk.chunkIndex])
                maybeThrowGLException("Could not bind framebuffer", "glBindFramebuffer")
                GLES30.glDrawBuffers(chunk.chunkSize, ATTACHMENT_ENUMS, 0)
                maybeThrowGLException("Could not bind draw buffers", "glDrawBuffers")
                // Since GLES doesn't support glFramebufferTexture, we will use each cubemap face as a
                // different color attachment.
                for (attachment in 0 until chunk.chunkSize) {
                    GLES30.glFramebufferTexture2D(
                        GLES30.GL_FRAMEBUFFER,
                        GLES30.GL_COLOR_ATTACHMENT0 + attachment,
                        GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + chunk.firstFaceIndex + attachment,
                        filteredCubemapTexture!!.getTextureId(),
                        level
                    )
                    maybeThrowGLException(
                        "Could not attach LD cubemap mipmap to framebuffer", "glFramebufferTexture"
                    )
                }
            }
            framebuffers[level] = framebufferChunks
        }

        return framebuffers
    }

    /**
     * Generate a cache of importance sampling terms in tangent space, indexed by `[roughnessLevel-1][sampleIndex]`.
     */
    private fun generateImportanceSampleCaches(): Array<Array<ImportanceSampleCacheEntry?>> {
        val arrays = arrayOfNulls<ImportanceSampleCacheEntry?>(0)
        val result: Array<Array<ImportanceSampleCacheEntry?>> = Array(numberOfMipmapLevels - 1){arrays}
        for (i in 0 until numberOfMipmapLevels - 1) {
            val mipmapLevel = i + 1
            val perceptualRoughness = mipmapLevel / (numberOfMipmapLevels - 1).toFloat()
            val roughness = perceptualRoughness * perceptualRoughness
            val resolution = this.resolution shr mipmapLevel
            val log4omegaP = log4((4.0f * PI_F) / (6 * resolution * resolution))
            val inverseNumberOfSamples = 1f / numberOfImportanceSamples

            val cache = ArrayList<ImportanceSampleCacheEntry>(
                numberOfImportanceSamples
            )
            var weight = 0f
            for (sampleIndex in 0 until numberOfImportanceSamples) {
                val u = hammersley(sampleIndex, inverseNumberOfSamples)
                val h = hemisphereImportanceSampleDggx(u, roughness)
                val noh = h[2]
                val noh2 = noh * noh
                val nol = 2f * noh2 - 1f
                if (nol > 0) {
                    val entry = ImportanceSampleCacheEntry()
                    entry.direction = floatArrayOf(2f * noh * h[0], 2 * noh * h[1], nol)
                    val pdf = distributionGgx(noh, roughness) / 4f
                    val log4omegaS = log4(1f / (numberOfImportanceSamples * pdf))
                    // K is a LOD bias that allows a bit of overlapping between samples
                    val log4K = 1f // K = 4
                    val l = log4omegaS - log4omegaP + log4K
                    entry.level = min(
                        max(l.toDouble(), 0.0),
                        (numberOfMipmapLevels - 1).toFloat().toDouble()
                    )
                        .toFloat()
                    entry.contribution = nol

                    cache.add(entry)
                    weight += nol
                }
            }
            for (entry in cache) {
                entry.contribution /= weight
            }
            result[i] = arrayOfNulls(cache.size)
            cache.toArray(result[i])
        }
        return result
    }

    /**
     * Constructs a [SpecularCubemapFilter].
     *
     *
     * The provided resolution refers to both the width and height of the input resolution and the
     * resolution of the highest mipmap level of the filtered cubemap texture.
     *
     *
     * Ideally, the cubemap would need to be filtered by computing a function of every sample over
     * the hemisphere for every texel. Since this is not practical to compute, a limited, discrete
     * number of importance samples are selected instead. A larger number of importance samples will
     * generally provide more accurate results, but in the case of ARCore, the cubemap estimations are
     * already very low resolution, and higher values provide rapidly diminishing returns.
     */
    init {
        this.numberOfMipmapLevels = log2(resolution) + 1

        try {
            radianceCubemap =
                Texture(render, Texture.Target.TEXTURE_CUBE_MAP, Texture.WrapMode.CLAMP_TO_EDGE)
            filteredCubemapTexture =
                Texture(render, Texture.Target.TEXTURE_CUBE_MAP, Texture.WrapMode.CLAMP_TO_EDGE)

            val chunks = ChunkIterable(maxColorAttachments)
            initializeLdCubemap()
            shaders = createShaders(render, chunks)
            framebuffers = createFramebuffers(chunks)

            // Create the quad mesh that encompasses the entire view.
            val coordsBuffer = VertexBuffer(render, COMPONENTS_PER_VERTEX, COORDS_BUFFER)
            mesh =
                Mesh(
                    render,
                    Mesh.PrimitiveMode.TRIANGLE_STRIP,  /*indexBuffer=*/
                    null,
                    arrayOf(coordsBuffer)
                )
        } catch (t: Throwable) {
            close()
            throw t
        }
    }

    companion object {
        private val TAG: String = SpecularCubemapFilter::class.java.simpleName

        private const val COMPONENTS_PER_VERTEX = 2
        private const val NUMBER_OF_VERTICES = 4
        private const val FLOAT_SIZE = 4
        private const val COORDS_BUFFER_SIZE =
            COMPONENTS_PER_VERTEX * NUMBER_OF_VERTICES * FLOAT_SIZE

        private const val NUMBER_OF_CUBE_FACES = 6

        private val COORDS_BUFFER: FloatBuffer =
            ByteBuffer.allocateDirect(COORDS_BUFFER_SIZE).order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer()

        init {
            COORDS_BUFFER.put(
                floatArrayOf(
                    /*0:*/-1f, -1f,  /*1:*/+1f, -1f,  /*2:*/-1f, +1f,  /*3:*/+1f, +1f,
                )
            )
        }

        private val ATTACHMENT_LOCATION_DEFINES = arrayOf(
            "PX_LOCATION",
            "NX_LOCATION",
            "PY_LOCATION",
            "NY_LOCATION",
            "PZ_LOCATION",
            "NZ_LOCATION",
        )

        private val ATTACHMENT_ENUMS = intArrayOf(
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_COLOR_ATTACHMENT1,
            GLES30.GL_COLOR_ATTACHMENT2,
            GLES30.GL_COLOR_ATTACHMENT3,
            GLES30.GL_COLOR_ATTACHMENT4,
            GLES30.GL_COLOR_ATTACHMENT5,
        )

        private val maxColorAttachments: Int
            get() {
                val result = IntArray(1)
                GLES30.glGetIntegerv(GLES30.GL_MAX_COLOR_ATTACHMENTS, result, 0)
                maybeThrowGLException("Failed to get max color attachments", "glGetIntegerv")
                return result[0]
            }

        // Math!
        private const val PI_F = Math.PI.toFloat()

        private fun log2(value: Int): Int {
            var value = value
            require(value > 0) { "value must be positive" }
            value = value shr 1
            var result = 0
            while (value != 0) {
                ++result
                value = value shr 1
            }
            return result
        }

        private fun log4(value: Float): Float {
            return (ln(value.toDouble()) / ln(4.0)).toFloat()
        }

        private fun sqrt(value: Float): Float {
            return kotlin.math.sqrt(value.toDouble()).toFloat()
        }

        private fun sin(value: Float): Float {
            return kotlin.math.sin(value.toDouble()).toFloat()
        }

        private fun cos(value: Float): Float {
            return kotlin.math.cos(value.toDouble()).toFloat()
        }

        private fun hammersley(i: Int, iN: Float): FloatArray {
            val tof = 0.5f / 0x80000000L
            var bits = i.toLong()
            bits = (bits shl 16) or (bits ushr 16)
            bits = ((bits and 0x55555555L) shl 1) or ((bits and 0xAAAAAAAAL) ushr 1)
            bits = ((bits and 0x33333333L) shl 2) or ((bits and 0xCCCCCCCCL) ushr 2)
            bits = ((bits and 0x0F0F0F0FL) shl 4) or ((bits and 0xF0F0F0F0L) ushr 4)
            bits = ((bits and 0x00FF00FFL) shl 8) or ((bits and 0xFF00FF00L) ushr 8)
            return floatArrayOf(i * iN, bits * tof)
        }

        private fun hemisphereImportanceSampleDggx(u: FloatArray, a: Float): FloatArray {
            // GGX - Trowbridge-Reitz importance sampling
            val phi = 2.0f * PI_F * u[0]
            // NOTE: (aa-1) == (a-1)(a+1) produces better fp accuracy
            val cosTheta2 = (1f - u[1]) / (1f + (a + 1f) * ((a - 1f) * u[1]))
            val cosTheta = sqrt(cosTheta2)
            val sinTheta = sqrt(1f - cosTheta2)
            return floatArrayOf(sinTheta * cos(phi), sinTheta * sin(phi), cosTheta)
        }

        private fun distributionGgx(noh: Float, a: Float): Float {
            // NOTE: (aa-1) == (a-1)(a+1) produces better fp accuracy
            val f = (a - 1f) * ((a + 1f) * (noh * noh)) + 1f
            return (a * a) / (PI_F * f * f)
        }
    }
}

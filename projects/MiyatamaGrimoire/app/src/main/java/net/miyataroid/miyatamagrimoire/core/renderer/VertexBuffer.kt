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

import android.opengl.GLES30
import java.io.Closeable
import java.nio.FloatBuffer

/**
 * A list of vertex attribute data stored GPU-side.
 *
 *
 * One or more [VertexBuffer]s are used when constructing a [Mesh] to describe vertex
 * attribute data; for example, local coordinates, texture coordinates, vertex normals, etc.
 *
 * @see [glVertexAttribPointer](https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glVertexAttribPointer.xhtml)
 */
class VertexBuffer(render: SampleRender?, numberOfEntriesPerVertex: Int, entries: FloatBuffer?) :
    Closeable {
    private val buffer: GpuBuffer

    /* package-private */
    val numberOfEntriesPerVertex: Int

    /**
     * Construct a [VertexBuffer] populated with initial data.
     *
     *
     * The GPU buffer will be filled with the data in the *direct* buffer `entries`,
     * starting from the beginning of the buffer (not the current cursor position). The cursor will be
     * left in an undefined position after this function returns.
     *
     *
     * The number of vertices in the buffer can be expressed as `entries.limit() /
     * numberOfEntriesPerVertex`. Thus, The size of the buffer must be divisible by `numberOfEntriesPerVertex`.
     *
     *
     * The `entries` buffer may be null, in which case an empty buffer is constructed
     * instead.
     */
    init {
        require(!(entries != null && entries.limit() % numberOfEntriesPerVertex != 0)) {
            ("If non-null, vertex buffer data must be divisible by the number of data points per"
                    + " vertex")
        }

        this.numberOfEntriesPerVertex = numberOfEntriesPerVertex
        buffer = GpuBuffer(GLES30.GL_ARRAY_BUFFER, GpuBuffer.FLOAT_SIZE, entries)
    }

    /**
     * Populate with new data.
     *
     *
     * The entire buffer is replaced by the contents of the *direct* buffer `entries`
     * starting from the beginning of the buffer, not the current cursor position. The cursor will be
     * left in an undefined position after this function returns.
     *
     *
     * The GPU buffer is reallocated automatically if necessary.
     *
     *
     * The `entries` buffer may be null, in which case the buffer will become empty.
     * Otherwise, the size of `entries` must be divisible by the number of entries per vertex
     * specified during construction.
     */
    fun set(entries: FloatBuffer?) {
        require(!(entries != null && entries.limit() % numberOfEntriesPerVertex != 0)) {
            ("If non-null, vertex buffer data must be divisible by the number of data points per"
                    + " vertex")
        }
        buffer.set(entries)
    }

    override fun close() {
        buffer.free()
    }

    val bufferId: Int
        /* package-private */
        get() = buffer.getBufferId()

    val numberOfVertices: Int
        /* package-private */
        get() = buffer.size / numberOfEntriesPerVertex
}

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
import android.util.Log
import java.nio.Buffer

/* package-private */
internal class GpuBuffer(target: Int, numberOfBytesPerEntry: Int, entries: Buffer?) {
    private val target: Int
    private val numberOfBytesPerEntry: Int
    private val bufferId = intArrayOf(0)
    var size: Int = 0
        private set
    private var capacity = 0

    init {
        var entries = entries
        if (entries != null) {
            require(entries.isDirect) { "If non-null, entries buffer must be a direct buffer" }
            // Some GPU drivers will fail with out of memory errors if glBufferData or glBufferSubData is
            // called with a size of 0, so avoid this case.
            if (entries.limit() == 0) {
                entries = null
            }
        }

        this.target = target
        this.numberOfBytesPerEntry = numberOfBytesPerEntry
        if (entries == null) {
            this.size = 0
            this.capacity = 0
        } else {
            this.size = entries.limit()
            this.capacity = entries.limit()
        }

        try {
            // Clear VAO to prevent unintended state change.
            GLES30.glBindVertexArray(0)
            GLError.maybeThrowGLException("Failed to unbind vertex array", "glBindVertexArray")

            GLES30.glGenBuffers(1, bufferId, 0)
            GLError.maybeThrowGLException("Failed to generate buffers", "glGenBuffers")

            GLES30.glBindBuffer(target, bufferId[0])
            GLError.maybeThrowGLException("Failed to bind buffer object", "glBindBuffer")

            if (entries != null) {
                entries.rewind()
                GLES30.glBufferData(
                    target, entries.limit() * numberOfBytesPerEntry, entries, GLES30.GL_DYNAMIC_DRAW
                )
            }
            GLError.maybeThrowGLException("Failed to populate buffer object", "glBufferData")
        } catch (t: Throwable) {
            free()
            throw t
        }
    }

    fun set(entries: Buffer?) {
        // Some GPU drivers will fail with out of memory errors if glBufferData or glBufferSubData is
        // called with a size of 0, so avoid this case.
        if (entries == null || entries.limit() == 0) {
            size = 0
            return
        }
        require(entries.isDirect) { "If non-null, entries buffer must be a direct buffer" }
        GLES30.glBindBuffer(target, bufferId[0])
        GLError.maybeThrowGLException("Failed to bind vertex buffer object", "glBindBuffer")

        entries.rewind()

        if (entries.limit() <= capacity) {
            GLES30.glBufferSubData(target, 0, entries.limit() * numberOfBytesPerEntry, entries)
            GLError.maybeThrowGLException(
                "Failed to populate vertex buffer object",
                "glBufferSubData"
            )
            size = entries.limit()
        } else {
            GLES30.glBufferData(
                target, entries.limit() * numberOfBytesPerEntry, entries, GLES30.GL_DYNAMIC_DRAW
            )
            GLError.maybeThrowGLException("Failed to populate vertex buffer object", "glBufferData")
            size = entries.limit()
            capacity = entries.limit()
        }
    }

    fun free() {
        if (bufferId[0] != 0) {
            GLES30.glDeleteBuffers(1, bufferId, 0)
            GLError.maybeLogGLError(
                Log.WARN,
                TAG,
                "Failed to free buffer object",
                "glDeleteBuffers"
            )
            bufferId[0] = 0
        }
    }

    fun getBufferId(): Int {
        return bufferId[0]
    }

    companion object {
        private val TAG: String = GpuBuffer::class.java.simpleName

        // These values refer to the byte count of the corresponding Java datatypes.
        const val INT_SIZE: Int = 4
        const val FLOAT_SIZE: Int = 4
    }
}

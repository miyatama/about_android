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
import android.opengl.GLException
import android.opengl.GLU
import android.util.Log

/** Module for handling OpenGL errors.  */
object GLError {
    /** Throws a [GLException] if a GL error occurred.  */
    @JvmStatic
    fun maybeThrowGLException(reason: String, api: String) {
        val errorCodes = glErrors
        if (errorCodes != null) {
            throw GLException(errorCodes[0], formatErrorMessage(reason, api, errorCodes))
        }
    }

    /** Logs a message with the given logcat priority if a GL error occurred.  */
    @JvmStatic
    fun maybeLogGLError(priority: Int, tag: String?, reason: String, api: String) {
        val errorCodes = glErrors
        if (errorCodes != null) {
            Log.println(priority, tag, formatErrorMessage(reason, api, errorCodes))
        }
    }

    private fun formatErrorMessage(reason: String, api: String, errorCodes: List<Int>): String {
        val builder = StringBuilder(String.format("%s: %s: ", reason, api))
        val iterator = errorCodes.iterator()
        while (iterator.hasNext()) {
            val errorCode = iterator.next()
            builder.append(String.format("%s (%d)", GLU.gluErrorString(errorCode), errorCode))
            if (iterator.hasNext()) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    private val glErrors: List<Int>?
        get() {
            var errorCode = GLES30.glGetError()
            // Shortcut for no errors
            if (errorCode == GLES30.GL_NO_ERROR) {
                return null
            }
            val errorCodes: MutableList<Int> = ArrayList()
            errorCodes.add(errorCode)
            while (true) {
                errorCode = GLES30.glGetError()
                if (errorCode == GLES30.GL_NO_ERROR) {
                    break
                }
                errorCodes.add(errorCode)
            }
            return errorCodes
        }
}

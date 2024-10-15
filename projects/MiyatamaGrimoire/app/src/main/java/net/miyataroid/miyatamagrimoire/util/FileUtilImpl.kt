package net.miyataroid.miyatamagrimoire.util

import android.content.Context

/**
 * アプリ内のファイルストレージを管理する
 */
class FileUtilImpl(context: Context): FileUtil  {
    enum class Directories(directoryName: String) {
        EFFECT("effects")
    }


    override fun createDirectories() {
        TODO("Not yet implemented")
    }
}
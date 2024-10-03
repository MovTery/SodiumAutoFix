package com.movtery.sodiumautofix.fix

import android.content.Context
import com.movtery.sodiumautofix.R
import com.movtery.sodiumautofix.modloader.Mod
import com.movtery.sodiumautofix.sodium.SodiumVersionCheck
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ModifyJarFile {
    companion object {
        fun modifySodiumMixinsInJar(
            context: Context,
            file: File,
            mod: Mod,
            fixListener: OnFixListener,
            progressListener: OnProgressListener
        ) {
            fixListener.onFixStarted()

            val value = SodiumVersionCheck.checkVersion(mod.modVersion)
            if (value >= 1) {
                val tempFile = File(file.parent, "TEMP_${file.name}")
                runCatching {
                    ZipInputStream(FileInputStream(file)).use { zipInput ->
                        ZipOutputStream(FileOutputStream(tempFile)).use { zipOutput ->
                            var entry: ZipEntry?
                            while (zipInput.nextEntry.also { entry = it } != null) {
                                val name = entry!!.name
                                progressListener.onProgress(name)

                                if (mod.mixinFile.contains(name)) {
                                    println("Modifying: $name")

                                    val byteArrayOutputStream = ByteArrayOutputStream()
                                    zipInput.copyTo(byteArrayOutputStream)

                                    val newContent = modifyJsonContent(
                                        ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                                        progressListener
                                    )
                                    val newEntry = ZipEntry(name)

                                    zipOutput.putNextEntry(newEntry)
                                    zipOutput.write(newContent.toByteArray())
                                    zipOutput.closeEntry()
                                } else {
                                    zipOutput.putNextEntry(ZipEntry(name))
                                    zipInput.copyTo(zipOutput)
                                    zipOutput.closeEntry()
                                }
                            }
                            progressListener.onProgress(context.getString(R.string.fix_save))
                        }
                    }
                    tempFile.renameTo(file)
                    fixListener.onFixEnded()
                    progressListener.onProgress("")
                }.getOrElse { e ->
                    e.printStackTrace()
                    fixListener.onError(e)
                }
            }
        }

        private fun modifyJsonContent(inputStream: InputStream, listener: OnProgressListener): String {
            val modifiedLines = mutableListOf<String>()
            val requiredRemove = mutableListOf<String>()
            val removeLines = MixinValue.getMixins()

            inputStream.bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    listener.onProgress(line)
                    // 检测该行是否包含需要删除的mixin
                    modifiedLines.add(line)
                    if (!removeLines.none { line.contains(it) }) requiredRemove.add(line)
                }
            }

            println(requiredRemove.toString())
            modifiedLines.removeAll(requiredRemove)

            return modifiedLines.joinToString("\n")
        }
    }
}

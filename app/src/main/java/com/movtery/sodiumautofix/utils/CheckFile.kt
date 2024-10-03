package com.movtery.sodiumautofix.utils

import com.movtery.sodiumautofix.modloader.AbstractModChecker
import com.movtery.sodiumautofix.modloader.FabricModChecker
import com.movtery.sodiumautofix.modloader.ForgeModChecker
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.util.jar.JarEntry
import java.util.jar.JarInputStream

class CheckFile {
    companion object {
        fun checkFile(file: File, listener: OnCheckListener) {
            if (!file.name.endsWith(".jar")) listener.onFail()

            JarInputStream(FileInputStream(file)).use {
                if (findFabric(it, listener)) {
                    println("Find Fabric")
                    return
                }
            }

            JarInputStream(FileInputStream(file)).use {
                if (findForge(it, listener)) {
                    println("Find Forge/NeoForge")
                    return
                }
            }
        }

        //找到Fabric的特征
        private fun findFabric(jarInputStream: JarInputStream, listener: OnCheckListener): Boolean {
            var entry: JarEntry?
            while (jarInputStream.nextJarEntry.also { entry = it } != null) {
                entry?.let { file ->
                    if (file.name == "fabric.mod.json") {
                        println("Find fabric.mod.json")
                        val string = jarInputStream.bufferedReader().use(BufferedReader::readText)
                        getMod(string, FabricModChecker(), listener)

                        return true
                    }
                }
            }

            return false
        }

        //找到Forge或NeoForge的特征
        private fun findForge(jarInputStream: JarInputStream, listener: OnCheckListener): Boolean {
            var entry: JarEntry?
            while (jarInputStream.nextJarEntry.also { entry = it } != null) {
                entry?.let {
                    if (it.name.startsWith("META-INF/") &&
                        (it.name == "META-INF/neoforge.mods.toml" ||
                                it.name == "META-INF/mods.toml")
                    ) {
                        println("Find neoforge.mods.toml/mods.toml")
                        val string = jarInputStream.bufferedReader().use(BufferedReader::readText)
                        getMod(string, ForgeModChecker(), listener)

                        return true
                    }
                }
            }

            return false
        }

        private fun getMod(string: String, checker: AbstractModChecker, listener: OnCheckListener) {
            checker.checkFeatureFileContent(string)?.apply {
                listener.onEnd(this)
                return
            }
            listener.onFail()
        }
    }
}
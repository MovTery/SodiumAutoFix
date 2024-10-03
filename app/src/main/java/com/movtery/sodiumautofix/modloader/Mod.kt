package com.movtery.sodiumautofix.modloader

data class Mod(
    val modLoader: String,
    val modId: String,
    val modVersion: String,
    val mixinFile: Array<String>
) {
    private var fileName: String? = null

    fun setFileName(name: String) {
        fileName = name
    }

    fun getFileName() = fileName

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mod

        if (modLoader != other.modLoader) return false
        if (modId != other.modId) return false
        if (!mixinFile.contentEquals(other.mixinFile)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modLoader.hashCode()
        result = 31 * result + modId.hashCode()
        result = 31 * result + mixinFile.contentHashCode()
        return result
    }
}
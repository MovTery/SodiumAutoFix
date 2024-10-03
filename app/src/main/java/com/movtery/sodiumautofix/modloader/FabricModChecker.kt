package com.movtery.sodiumautofix.modloader

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FabricModChecker : AbstractModChecker() {
    private val json = Json { ignoreUnknownKeys = true }

    override fun checkFeatureFileContent(string: String): Mod? {
        runCatching {
            val jsonData = json.decodeFromString<FabricModJson>(string)
            jsonData.id.takeIf { it == "sodium" } ?: return null

            return Mod("Fabric", jsonData.id, jsonData.version, jsonData.mixins.toTypedArray())
        }.getOrElse { e ->
            e.printStackTrace()
        }

        return null
    }

    @Serializable
    class FabricModJson(
        val id: String,
        val version: String,
        val mixins: List<String>
    )
}

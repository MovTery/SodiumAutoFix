package com.movtery.sodiumautofix.modloader

import com.moandjiezana.toml.Toml

class ForgeModChecker: AbstractModChecker() {
    override fun checkFeatureFileContent(string: String): Mod? {
        runCatching {
            lateinit var modId: String
            lateinit var modVersion: String
            val mixinConfigs: MutableList<String> = ArrayList()

            val toml = Toml().read(string)

            val mods = toml.getTables("mods")
            for (mod in mods) {
                modId = mod.getString("modId")
                modVersion = mod.getString("version")
                break
            }

            modId.takeIf { it == "sodium" } ?: return null

            val mixins = toml.getTables("mixins")
            for (mixin in mixins) {
                mixinConfigs.add(mixin.getString("config"))
            }

            return Mod(
                "Forge/NeoForge",
                modId,
                modVersion,
                mixinConfigs.toTypedArray()
            )
        }.getOrElse { e ->
            e.printStackTrace()
        }

        return null
    }
}
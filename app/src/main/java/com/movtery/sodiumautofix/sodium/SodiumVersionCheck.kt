package com.movtery.sodiumautofix.sodium

class SodiumVersionCheck {
    companion object {
        const val NONE = -1
        const val CHANGE_MIXIN_CONFIG = 0
        const val CHANGE_MIXIN = 1
        const val CHANGE_MIXIN_AND_CHANGE_CONFIG = 2
        const val UNSUPPORTED = 3

        fun checkVersion(version: String): Int {
            //0.6.0-beta.2+mc1.21.1
            //0.4.10+build.27
            //0.2.0+build.4
            val sodiumVersion = extractMainVersion(version)
            println(sodiumVersion)

            var value = CHANGE_MIXIN_CONFIG
            if (compareVersion(sodiumVersion, "0.3.0") == -1) return NONE
            if (compareVersion(sodiumVersion, "0.5.0") >= 0) value = CHANGE_MIXIN
            if (compareVersion(sodiumVersion, "0.5.11") >= 0) value = CHANGE_MIXIN_AND_CHANGE_CONFIG
            if (compareVersion(sodiumVersion, "0.6.0") >= 0) value = UNSUPPORTED
            return value
        }

        private fun extractMainVersion(version: String): String {
            val dashIndex = version.indexOf('-')
            val plusIndex = version.indexOf('+')

            val endIndex = minOf(
                if (dashIndex != -1) dashIndex else Int.MAX_VALUE,
                if (plusIndex != -1) plusIndex else Int.MAX_VALUE
            )

            return version.substring(0, endIndex)
        }

        private fun compareVersion(version1: String, version2: String): Int {
            val parts1 = version1.split(".").map { it.toInt() }
            val parts2 = version2.split(".").map { it.toInt() }

            val maxLength = maxOf(parts1.size, parts2.size)

            //逐部分比较版本号
            for (i in 0 until maxLength) {
                val part1 = if (i < parts1.size) parts1[i] else 0
                val part2 = if (i < parts2.size) parts2[i] else 0

                if (part1 > part2) return 1
                if (part1 < part2) return -1
            }

            return 0
        }
    }
}
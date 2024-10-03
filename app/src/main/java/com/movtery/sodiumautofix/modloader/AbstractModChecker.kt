package com.movtery.sodiumautofix.modloader

abstract class AbstractModChecker {
    abstract fun checkFeatureFileContent(string: String): Mod?
}
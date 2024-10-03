package com.movtery.sodiumautofix.utils

import com.movtery.sodiumautofix.modloader.Mod

interface OnCheckListener {
    fun onEnd(mod: Mod)
    fun onFail()
}
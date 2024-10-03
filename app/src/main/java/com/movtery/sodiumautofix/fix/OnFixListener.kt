package com.movtery.sodiumautofix.fix

interface OnFixListener {
    fun onFixStarted()
    fun onFixEnded()
    fun onError(e: Throwable)
}
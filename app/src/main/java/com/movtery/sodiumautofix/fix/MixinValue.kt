package com.movtery.sodiumautofix.fix

class MixinValue {
    companion object {
        fun getMixins(): List<String> {
            return listOf(
                // 公用 Yarn
                "\"core.render.world.BufferBuilderStorageMixin\"",
                "\"core.render.world.WorldRendererMixin\"",
                "\"workarounds.event_loop.RenderSystemMixin\"",

                // 1.20.1 Yarn
                "\"core.render.world.ChunkBuilderMixin\"",
            )
        }
    }
}
# SodiumAutoFix

一个安卓应用，能够自动识别 Sodium 模组本体，并修改其 Mixin 配置，使它能够正常的在安卓设备上运行。

## 注意

- 当前的修复方式为，修改模组本体的 Mixin 配置，使其能够正常运行在安卓设备上
- 此方法仅适用于 0.5.0 - 0.6.0 版本，低于 0.5.0 版本无需修复，高于 0.6.0 版本此方法无效。

本应用会删除 Sodium 的以下 Mixin 配置：

```
公用
core.render.world.BufferBuilderStorageMixin
core.render.world.WorldRendererMixin
workarounds.event_loop.RenderSystemMixin

仅 1.20.1
core.render.world.ChunkBuilderMixin
```
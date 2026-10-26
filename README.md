# Stormforge

![build](https://github.com/Antoniscool123/stormforge/actions/workflows/build.yml/badge.svg)

A Fabric mod: relics forged from the heart of a storm.

| Item | Craft | What it does |
|---|---|---|
| **Storm Shard** | amethyst shard + lightning rod + gunpowder (makes 2) | Crafting material |
| **Stormcaller Staff** | 3 shards + lightning rod + 2 blaze rods | Right-click: 5-bolt lightning barrage + shockwave at your crosshair (range 64). Sneak + right-click: one precise bolt, short cooldown. |
| **Rift Anchor** | 4 shards + 4 ender pearls + crying obsidian | Sneak + right-click: plant anchor at your feet. Right-click: snap back to it (same dimension). |
| **Gale Feather** | feather + shard + breeze rod | Right-click: launch where you look, no fall damage. |
| **Storm Blade** | diamond sword + 4 shards + blaze rod | Diamond-tier sword. Each hit arcs lightning to up to 2 nearby creatures for 5 damage. Repairs with shards. |
| **Storm Ore** | found underground (y -32 to 96, all overworld biomes) | Needs an iron pickaxe. Drops 1-3 Storm Shards (Fortune applies); Silk Touch keeps the block. |
| **Block of Storm Shards** | 9 shards (and back into 9) | Glowing storage block. |

## Build

Requires JDK 25 and Gradle 9.6+ (or generate a wrapper with `gradle wrapper`).

```
gradle build
```

The jar is in `build/libs/stormforge-1.0.0.jar`. Drop it in your `mods` folder with Fabric Loader and Fabric API.

## Version notes

Targets Minecraft 26.3 with Fabric Loader 0.19.5, Fabric API 0.162.0+26.3 and Loom 1.17.
Since 26.1 the game is unobfuscated, so the code uses Mojang names (`Item.Properties`,
`InteractionResult`, `ServerLevel`, ...) and there is no mappings line or `modImplementation`.
CI builds the mod and boots a dedicated server with it on every push, so data errors are caught before release (see the Actions tab); download the jar from the latest run's
`stormforge-jar` artifact.

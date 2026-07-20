# Entangled Chests

A Fabric mod for Minecraft **1.21.11** that lets you link a bundle to a chest and
teleport items into that chest from anywhere.

> Looking for the player-facing description (features, recipes, how to play)? See
> [MODRINTH.md](MODRINTH.md).

## What it adds

| Item | Recipe | Behaviour |
| --- | --- | --- |
| **Entanglement Crystal** | Echo shard center, lapis lazuli around it | Crafting ingredient |
| **Entangled Chest** | Chest recipe with a crystal in the center | A single chest (27 slots) with a unique id; drops a pre-linked bundle when first placed |
| **Entangled Bundle** | Bundle recipe with a crystal in the middle | A real bundle; once linked to a chest, items put in are routed into that chest and can't be withdrawn |

Right-click a placed Entangled Chest with an unlinked bundle to link it. Breaking a
chest unlinks all bundles keyed to it (even for offline players). Linked items
carry an enchantment glint.

## Requirements

- Minecraft `1.21.11` · Fabric Loader `0.19.3`+ · [Fabric API](https://modrinth.com/mod/fabric-api) · Java `21`+

## Building & running

```bash
./gradlew build          # produce the mod jar in build/libs
./gradlew runClient      # launch a dev client with the mod loaded
./gradlew runServer      # launch a dev dedicated server
```

To regenerate decompiled Minecraft sources for reference: `./gradlew genSources`.

## Tooling / versions

| Component | Version |
| --- | --- |
| Minecraft | `1.21.11` |
| Fabric Loader | `0.19.3` |
| Fabric API | `0.141.5+1.21.11` |
| Fabric Loom | `1.18.0-alpha.9` |
| Gradle | `9.6.1` |
| Mappings | Official Mojang mappings |

This targets **1.21.11**, the latest currently-moddable release. The newer `26.x`
line (calendar-style versioning) has no obfuscation mappings published yet —
neither Mojang (its version manifest omits `client_mappings`) nor Yarn — so Loom
cannot build against it. Version pins live in [`gradle.properties`](gradle.properties).

## How it works

- **`EntangledChestRegistry`** (a `SavedData` on the overworld) maps each chest's
  UUID → dimension/pos/name and is the source of truth for whether a bundle is
  still "linked". Breaking a chest removes its entry, which is how bundles unlink
  even for offline players (checked the next time their bundle ticks).
- **`EntangledKey`** is a data component on the bundle holding the chest UUID +
  cached name (for the tooltip).
- **`EntangledBundleItem`** extends vanilla `BundleItem`, so item insertion uses
  vanilla's client-synced `BUNDLE_CONTENTS` logic; `inventoryTick` then drains the
  contents into the linked chest via **`EntangledTransfer`**.
- The chest is a `BaseEntityBlock` + `BaseContainerBlockEntity` with a custom
  block-entity renderer; the glint on both the item icon and the placed block is
  drawn through `submitModelPart(..., glint)` (vanilla chest renderers use
  `submitModel`, which has no foil flag).

## Layout

```
src/main/java/com/brendondugan/entangledchests/
  EntangledChests.java            # ModInitializer entrypoint + id() helper
  ModItems / ModBlocks / ModBlockEntities / ModComponents  # registration
  EntangledBundleItem.java        # bundle behaviour (extends BundleItem)
  component/EntangledKey.java     # bundle -> chest link component
  saveddata/EntangledChestRegistry.java  # global UUID -> chest registry
  util/EntangledTransfer.java     # deposit routing
  block/                          # EntangledChestBlock + BlockEntity
  client/                         # client init + block-entity & special item renderers
src/main/resources/
  fabric.mod.json
  assets/entangledchests/         # blockstates, models, items, textures, lang, icon
  data/entangledchests/recipe/    # the three crafting recipes
```

## License

MIT

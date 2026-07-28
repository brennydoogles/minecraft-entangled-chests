# Entangled Chests

A **Fabric + NeoForge** mod for Minecraft **26.1–26.2** that lets you link a bundle to
a chest and teleport items into that chest from anywhere.

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

- Minecraft `26.1`–`26.2` · Java `25`+ · [Architectury API](https://modrinth.com/mod/architectury-api) `20.0.9`+
- **Fabric**: Fabric Loader `0.19.3`+ and [Fabric API](https://modrinth.com/mod/fabric-api)
- **NeoForge**: `26.1.2`+

(For Minecraft `1.21.11`, use the `1.1.x` releases.)

## Building & running

```bash
./gradlew build                 # builds both loaders
./gradlew collectJars           # same, but only gathers the distributable jars

./gradlew :fabric:runClient     # dev client on Fabric
./gradlew :fabric:runServer
./gradlew :neoforge:runClient   # dev client on NeoForge
./gradlew :neoforge:runServer
```

The distributable jars for every platform are collected into the **root
`build/libs/`** directory, named `entangledchests-<version>-<platform>.jar`:

- `build/libs/entangledchests-<version>-fabric.jar`
- `build/libs/entangledchests-<version>-neoforge.jar`

(These are copies of each module's shadowed jar — the ones to upload. Ignore any
`-raw.jar` under `<platform>/build/libs/`; that's the pre-shadow intermediate.)

To regenerate decompiled Minecraft sources for reference: `./gradlew genSources`.

## Tooling / versions

| Component | Version |
| --- | --- |
| Minecraft (compile target) | `26.1.2` (runs on 26.1–26.2) |
| Architectury API / plugin / Loom | `20.0.9` / `3.5.169` / `loom-no-remap 1.17-SNAPSHOT` |
| Fabric Loader / API | `0.19.3` / `0.155.2+26.1.2` |
| NeoForge | `26.1.2.87` |
| Gradle / Java | `9.6.1` / `25` |
| Mappings | none — Minecraft is deobfuscated as of 26.1 |

**No obfuscation mappings.** As of Minecraft 26.1 the game ships with real class
names, so there are no Yarn/Mojang mappings and none are needed — Loom builds
directly against the jar via the **`loom-no-remap`** plugin (no `mappings` line, no
intermediary remap). Our code is already written in those names, which is why nearly
all of it lives in `common/` unchanged. The 26.x line is compiled against 26.1 and
declared to also run on 26.2. Version pins live in [`gradle.properties`](gradle.properties).

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

### Loader-specific bits

Almost everything is shared. Only three things differ per loader:

1. **Entrypoints** — `ModInitializer`/`ClientModInitializer` on Fabric, `@Mod`
   classes on NeoForge; both just call `EntangledChests.init()` /
   `EntangledChestsClient.init()`.
2. **`BlockEntityType` creation** (`platform/PlatformHelper`, `@ExpectPlatform`) —
   vanilla 1.21.11 exposes no constructor or builder, so Fabric uses
   `FabricBlockEntityTypeBuilder` while NeoForge uses the constructor its access
   transformer opens up.
3. **The chest's special item model** — vanilla's `SpecialModelRenderers.ID_MAPPER`
   is private; Fabric reaches it via Fabric API's transitive access wideners,
   NeoForge via `RegisterSpecialModelRendererEvent`.

## Layout

```
common/src/main/java/com/brendondugan/entangledchests/
  EntangledChests.java            # shared init() + id() helper
  ModItems / ModBlocks / ModBlockEntities / ModComponents  # Architectury DeferredRegister
  EntangledBundleItem.java        # bundle behaviour (extends BundleItem)
  component/EntangledKey.java     # bundle -> chest link component
  saveddata/EntangledChestRegistry.java  # global UUID -> chest registry
  util/EntangledTransfer.java     # deposit routing
  platform/PlatformHelper.java    # @ExpectPlatform shims
  block/                          # EntangledChestBlock + BlockEntity
  client/                         # shared client init + renderers
common/src/main/resources/
  assets/entangledchests/         # blockstates, models, items, textures, lang, icon
  data/entangledchests/recipe/    # the three crafting recipes

fabric/    fabric.mod.json, ModInitializers, PlatformHelperImpl
neoforge/  META-INF/neoforge.mods.toml, @Mod classes, PlatformHelperImpl
```

## License

MIT

# Changelog

All notable changes to Entangled Chests are documented here. This project adheres
to [Semantic Versioning](https://semver.org/).

## [2.0.0] - 2026-07-28

Minecraft **26.1–26.2** support (Fabric + NeoForge).

### Added

- **Support for Minecraft 26.1 and 26.2.** A single build now targets the
  deobfuscated 26.x line — compiled against 26.1 and compatible through 26.2.

### Changed

- Now requires **Minecraft 26.1+** and **Java 25**. (For Minecraft 1.21.11, use the
  1.1.x releases.)
- Updated to the deobfuscated-era toolchain (no obfuscation mappings — Minecraft
  ships real class names as of 26.1) and refreshed dependencies: Fabric API
  `0.155.2+26.1.2`, Architectury `20.0.9`, NeoForge `26.1.2.87`.

### Notes

No gameplay changes — mechanics, recipes, and behaviour are identical to the 1.1.x
line.

## [1.1.2] - 2026-07-28

### Fixed

- **Bounded Minecraft compatibility to the 1.21.x line.** The previous open-ended
  range let the mod load on Minecraft 26.x, where it is binary-incompatible and
  crashed the game on startup. It now declares support for `1.21.11` only (Fabric
  `~1.21.11`, NeoForge `[1.21.11,1.22)`), so incompatible versions get a clean
  "requires Minecraft 1.21.11" notice instead of a crash. (26.x support ships
  separately in 2.0.0.)

## [1.1.1] - 2026-07-28

### Changed

- Declared Minecraft compatibility is now open-ended — the mod loads on `1.21.11`
  and any newer version, on both loaders (previously limited to the `1.21.11`
  line). Note it is still built and tested only against `1.21.11`.

### Internal

- The build now collects the distributable jar for each platform into the root
  `build/libs/` directory, named `entangledchests-<version>-<platform>.jar`.

> No gameplay changes.

## [1.1.0] - 2026-07-21

### Added

- **NeoForge support.** The mod now ships for both Fabric and NeoForge from a
  single shared codebase, built with Architectury. Download the jar matching your
  loader.

### Changed

- **[Architectury API](https://modrinth.com/mod/architectury-api) is now a required
  dependency** on both loaders.

> No gameplay changes — mechanics, recipes and behaviour are identical to 1.0.0.

## [1.0.0] - 2026-07-20

Initial release for Minecraft 1.21.11 (Fabric).

### Added

- **Entanglement Crystal** — crafted from an echo shard surrounded by lapis lazuli.
- **Entangled Chest** — a single chest (27 slots) with a unique identity that
  bundles can link to. Placing one for the first time drops a bundle already
  linked to it. Name it on an anvil before placing to label it.
- **Entangled Bundle** — a real bundle that, once linked to a chest, teleports any
  items placed into it straight into that chest.
  - Link a bundle by right-clicking a placed Entangled Chest with it.
  - Link as many bundles as you like to a single chest.
  - Items can't be withdrawn from a linked bundle; if the chest is full they buffer
    in the bundle until space opens up.
  - Breaking or picking up a chest unlinks every bundle tied to it — even for
    offline players.
  - Tooltip shows the linked chest's name (or a short id).
- Enchantment glint on the crystal and chest (item and placed block), and on the
  bundle only while it is linked.
- Cross-dimension linking and persistence across world saves.

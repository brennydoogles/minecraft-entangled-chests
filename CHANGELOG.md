# Changelog

All notable changes to Entangled Chests are documented here. This project adheres
to [Semantic Versioning](https://semver.org/).

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

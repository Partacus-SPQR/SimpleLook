# Changelog

All notable changes to SimpleLook will be documented in this file.

## [1.3.0] - 2026-06-24

### Added
- Minecraft 26.2 support (unobfuscated builds, Java 25)

### Fixed
- Restored 1.21.9 / 1.21.10 / 1.21.11 builds, which had regressed in 1.2.0:
  - `MouseButtonEvent` mouse signatures and `options.controls.KeyBindsScreen` are now applied to all shipped versions (they were wrongly gated to `>=26.1`, leaving the 1.21.x path on pre-1.21.9 APIs)
  - Fallback config screen used `GuiGraphics.renderTooltip(...)` on 1.21.x → switched to `setTooltipForNextFrame(...)`
  - `Identifier` import and `KeyMapping.Category` are now gated `>=1.21.11` (were conflated with `KeyMappingHelper` at `>=26.1`)

### Changed
- 26.2 is now the primary build target; multi-version builds cover 1.21.9, 1.21.10, 1.21.11, 26.1, and 26.2
- Adapted to the 26.2 screen API change via a new `ScreenCompat` helper:
  - `Minecraft.setScreen()` → `Minecraft.setScreenAndShow()`
  - `Minecraft.screen` field → `Minecraft.gui.screen()`
- Switched the Java config to a toolchain so the 26.x (Java 25) builds no longer depend on the ambient JDK
- Updated dependencies for 26.2: Fabric API 0.153.0+26.2, Mod Menu 20.0.0-beta.4
- Clarified the look-mode option in both the Cloth and fallback config screens — the control is now labelled **Hold / Toggle** (the default is **Hold**: hold the key to look, release to revert) instead of an ambiguous on/off

---

## [1.2.0] - 2026-03-25

### Added
- Minecraft 26.1 support
- Java 25 toolchain support
- Unobfuscated jar handling for MC 26.1 (no mappings required)

### Changed
- Updated Gradle wrapper to 9.4.1
- Updated Fabric Loader to 0.18.4
- Adapted all source files with Stonecutter conditionals for 26.1 API changes:
  - `ResourceLocation` → `Identifier`
  - `GuiGraphics` → `GuiGraphicsExtractor`
  - `KeyBindingHelper` → `KeyMappingHelper`
  - `render()` → `extractRenderState()`
  - Mouse events now use `MouseButtonEvent`
  - `Camera.setup()` → `Camera.alignWithEntity()`
- Cloth Config excluded for 26.1 (no compatible version available yet)

### Technical
- Multi-version support expanded to 1.21.9, 1.21.10, 1.21.11, 26.1

---

## [1.0.0] - 2025-12-16

### Added
- Initial release
- Free look camera functionality (hold key to look around independently)
- Smooth camera movement with configurable smoothing
- Smooth return to center when key is released
- Configurable maximum yaw angle (10-180 degrees)
- Configurable maximum pitch angle (10-90 degrees)
- Configurable return speed (1-100%)
- Configurable smoothing factor (0-100%, lower = faster turning)
- Toggle mode option (press once to activate/deactivate)
- Keybind is unbound by default (recommended: Left Alt)
- Keybind visible in Options > Controls > Key Binds > SimpleLook category
- Full Mod Menu integration
- Cloth Config support for enhanced configuration UI (uses text fields due to Cloth Config slider bug)
- Fallback configuration screen when Cloth Config is not available
- Keybindings tab in configuration screen for quick rebinding

### Supported Activities
- Walking, running, sprinting
- Swimming and underwater movement
- Flying with elytra
- Riding mounts (horses, pigs, striders, happy ghasts, and all other rideable entities)

### Technical
- Multi-version support (1.21.9, 1.21.10, 1.21.11)
- Client-side only mod
- Fabric API integration
- Mixin-based camera and mouse handling
- Cloth Config and Mod Menu are truly optional dependencies

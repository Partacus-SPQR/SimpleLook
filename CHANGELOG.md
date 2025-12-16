# Changelog

All notable changes to SimpleLook will be documented in this file.

## [1.0.0] - 2025-01-01

### Added
- Initial release
- Free look camera functionality (hold key to look around independently)
- Smooth camera movement with configurable smoothing
- Smooth return to center when key is released
- Configurable maximum yaw angle (10-180 degrees)
- Configurable maximum pitch angle (10-90 degrees)
- Configurable return speed (1-100%)
- Configurable smoothing factor (0-100%)
- Toggle mode option (press once to activate/deactivate)
- Keybind is unbound by default (recommended: Left Alt)
- Keybind visible in Options > Controls > Key Binds > SimpleLook category
- Full Mod Menu integration
- Cloth Config support for enhanced configuration UI
- Fallback configuration screen when Cloth Config is not available
- Keybindings tab in configuration screen for quick rebinding

### Technical
- Multi-version support (1.21.9, 1.21.10, 1.21.11)
- Client-side only mod
- Fabric API integration
- Mixin-based camera and mouse handling
- Cloth Config and Mod Menu are truly optional dependencies

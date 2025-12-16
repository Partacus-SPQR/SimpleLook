# SimpleLook

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.9--1.21.11-green)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-blue)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-orange)](https://modrinth.com/project/simplelook)

**Look around freely while moving - inspired by Rust's free look feature.**

SimpleLook allows you to glance around without changing your movement direction. The keybind is **unbound by default** - we recommend binding it to **Left Alt**. Once bound, hold the key and move your mouse to look around independently while running, walking, flying with elytra, swimming, or doing anything else.

**Author:** Partacus-SPQR  
**Source:** [GitHub](https://github.com/Partacus-SPQR/SimpleLook)  
**Download:** [Modrinth](https://modrinth.com/project/simplelook)

---

## Features

- **Free Look Camera** - Look around independently from your movement direction
- **Smooth Camera Movement** - Configurable smoothing for natural-feeling camera motion
- **Smooth Return** - Camera smoothly returns to center when you release the key
- **Customizable Key Binding** - Rebind to any key or mouse button
- **Hold or Toggle Mode** - Choose between holding the key or pressing once to toggle
- **Configurable Limits** - Set maximum horizontal and vertical look angles
- **Adjustable Speed** - Configure how fast the camera returns to center
- **Works Everywhere** - Use while running, walking, swimming, flying, or riding

---

## How to Use

1. **Set Up**: Go to **Options > Controls > Key Binds** and find the **SimpleLook** category
2. **Bind Key**: Assign a key to "Free Look" (we recommend **Left Alt**)
3. **Use**: Hold your bound key and move your mouse to look around
4. **Release**: Let go and the camera smoothly returns to center

### Configuration

- Open **Options > Controls > Key Binds** and find the **SimpleLook** category to rebind
- For additional settings, install **Mod Menu** to access the configuration screen

---

## Configuration Options

All settings can be adjusted in-game with Mod Menu (and optionally Cloth Config for an enhanced UI):

| Setting | Default | Description |
|---------|---------|-------------|
| **Enabled** | On | Enable/disable the mod |
| **Max Yaw** | 135 degrees | Maximum horizontal look angle |
| **Max Pitch** | 90 degrees | Maximum vertical look angle |
| **Return Speed** | 25% | How fast the camera returns to center |
| **Smoothing** | 30% | Camera movement smoothing factor |
| **Toggle Mode** | Off | Use toggle instead of hold |

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download SimpleLook and place in your `mods` folder
4. (Optional) Install [Mod Menu](https://modrinth.com/mod/modmenu) for in-game configuration
5. Set your keybind in **Options > Controls > Key Binds > SimpleLook**

---

## Version Compatibility

| Minecraft | Mod Version | Fabric Loader |
|-----------|-------------|---------------|
| 1.21.11 | 1.0.0 | 0.18.2 or higher |
| 1.21.10 | 1.0.0 | 0.18.2 or higher |
| 1.21.9 | 1.0.0 | 0.18.2 or higher |

---

## Dependencies

**Required:**
- Fabric API

**Optional (Recommended):**
- Mod Menu - For in-game configuration access
- Cloth Config - For enhanced configuration UI

---

## FAQ

**Q: Does this work in multiplayer?**  
A: Yes. SimpleLook is completely client-side and works on any server.

**Q: Does this give any unfair advantage?**  
A: No. It is purely visual. Your character still faces the direction you are moving, and other players see you normally.

**Q: Can I use this while flying with elytra?**  
A: Yes. It works with all movement types including elytra flight, swimming, and riding.

**Q: Is this like the free look in Rust?**  
A: Exactly. It is inspired by Rust's free look feature that lets you look around while sprinting.

**Q: Why is the keybind unbound by default?**  
A: To avoid conflicts with other mods. We recommend binding it to Left Alt for the most natural experience.

---

## License

This mod is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

*Made by Partacus-SPQR*

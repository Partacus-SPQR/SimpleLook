# Contributing to SimpleLook

Thank you for your interest in contributing to SimpleLook!

## Getting Started

1. Fork the repository
2. Clone your fork
3. Set up the development environment:
   ```bash
   ./gradlew genSources
   ./gradlew build
   ```

## Making Changes

1. Create a new branch for your feature/fix
2. Make your changes
3. Test on all supported Minecraft versions using:
   ```bash
   ./gradlew :1.21.11:runClient
   ./gradlew :1.21.10:runClient
   ./gradlew :1.21.9:runClient
   ```
4. Submit a pull request

## Code Style

- Use consistent indentation (4 spaces)
- Follow existing naming conventions
- Add comments for complex logic
- Use `@Environment(EnvType.CLIENT)` for client-only classes
- Update documentation as needed

## Testing Checklist

Before submitting a PR:
- [ ] Test free look while standing
- [ ] Test free look while walking/running
- [ ] Test free look while swimming
- [ ] Test free look while flying with elytra
- [ ] Test toggle mode
- [ ] Test hold mode
- [ ] Test camera return animation
- [ ] Test with various config settings
- [ ] Test keybinding rebinding
- [ ] Check for console errors

## Reporting Issues

- Use the GitHub issue tracker
- Include Minecraft version and mod version
- Provide crash logs if applicable
- Describe steps to reproduce
- Include your config settings if relevant

## Pull Request Guidelines

- One feature/fix per PR
- Update CHANGELOG.md
- Test before submitting
- Reference related issues

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

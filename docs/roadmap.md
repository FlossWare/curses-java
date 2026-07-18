# curses-java Roadmap

## Vision
Make curses-java the premier Java terminal UI library with modern features, exceptional performance, and comprehensive platform support.

---

## Current Status: v1.x (Stable) ✅

### Core Features (Complete)
- ✅ 29 UI widgets (buttons, text fields, tables, etc.)
- ✅ Mouse support (click, drag, resize)
- ✅ Thread-safe architecture with ReentrantLock
- ✅ Color system (8 standard colors, 12 themes)
- ✅ Advanced text editing (selection, cut/copy/paste, undo/redo)
- ✅ Scrollable views (JScrollPane)
- ✅ Layout managers (BorderLayout, FlowLayout, GridLayout)
- ✅ Event system (sealed interfaces & records)
- ✅ Virtual Threads integration
- ✅ Foreign Function & Memory API for ncurses

### Theme Unification (In Progress)
- ✅ JSON theme schema (`themes/schema.json`)
- ✅ 12 themes exported as JSON (borland, borland3d, dark, dbase3, dbase4, dbase4-3d, default, dos, light, modern, ti994a, trs80)
- ✅ ThemeLoader (fromJson/toJson) for Java
- ✅ ThemeManager enhanced with loadThemesFromDirectory/useTheme/getAvailableThemes
- ✅ Architecture Decision Record ([ADR 0006](adr/0006-unification-strategy.md))
- ✅ Python consumer (`curses-themes`) reads shared JSON files
- 🎯 Rendering algorithm specification document
- ✅ JSON schema validation in CI (`validate-themes.yml`)
- 💡 Shared theme repository (`curses-theme-specs`)
- 💡 Rust/UniFFI shared rendering library (evaluated in #263)

### Quality Assurance (Complete)
- ✅ 1820 comprehensive tests (100% passing)
- ✅ 80%+ code coverage
- ✅ Mutation testing (PITest)
- ✅ Property-based testing (jqwik)
- ✅ CodeQL security scanning
- ✅ OWASP dependency checking
- ✅ Checkstyle & SpotBugs
- ✅ SBOM generation

### Documentation (Complete)
- ✅ Comprehensive README
- ✅ CONTRIBUTING guide
- ✅ SECURITY policy
- ✅ Architecture Decision Records
- ✅ CHANGELOG maintenance
- ✅ JavaDoc API documentation

---

## Next: v2.0 (2026 Q3-Q4) 🎯

### Phase 1: Abstract Native Bridge (Aug 2026) 🔧
- 🎯 **TerminalBridge Interface** - Decouple from ncurses-specific static class
- 🎯 **NcursesTerminalBridge** - Wrap existing NcursesBridge behind the interface
- 🎯 **MockTerminalBridge** - Replace MockNcursesBridge with interface-based mock
- 🎯 **Platform Detection Factory** - Auto-select backend by OS
- **ADR:** [0005-windows-unicode-roadmap](adr/0005-windows-unicode-roadmap.md)

### Phase 2: Windows Support (Sep 2026) 🪟
- 🎯 **PDCurses Backend** - PdcursesTerminalBridge for native Windows support
- 🎯 **ANSI Fallback Backend** - Pure-ANSI escape sequence bridge for minimal environments
- 🎯 **Bundled pdcurses.dll** - Pre-compiled native library in JAR resources
- 🎯 **Windows CI** - GitHub Actions windows-latest runner

### Phase 3: True Color (Oct 2026) 🎨
- 🎯 **TerminalColor Model** - Sealed interface: StandardColor (8), ExtendedColor (256), TrueColor (24-bit RGB)
- 🎯 **Backward-Compatible Themes** - Existing 8-color themes continue working unchanged
- 🎯 **Terminal Capability Detection** - Auto-detect MONOCHROME / 8 / 256 / TRUE_COLOR
- 🎯 **True Color Themes** - New themes exploiting 24-bit palette

### Phase 4: Unicode & Emoji (Nov 2026) 🌍
- 🎯 **TerminalCell Buffer** - Replace char[][] with grapheme-aware cell model
- 🎯 **East Asian Width** - Correct column-width calculation for CJK, emoji, combining marks
- 🎯 **Wide Character Bridge** - mvaddwstr() / ncursesw integration
- 🎯 **Component Width Fixes** - Update all 29 widgets to use display-width instead of String.length()

### Internationalization (deferred to v2.5)
- 🔜 **i18n Framework** - Internationalization support
- 🔜 **RTL Text Support** - Right-to-left languages (Arabic, Hebrew)
- 🔜 **Locale-aware Components** - Date/time/number formatting

### Advanced Features (deferred to v2.5)
- 🔜 **Plugin System** - Extensible architecture for custom widgets
- 🔜 **Animation Framework** - Smooth transitions and effects
- 🔜 **Drag & Drop** - Full drag-and-drop support between components
- 🔜 **Virtual Scrolling** - Handle millions of items efficiently

### Developer Experience (deferred to v2.5)
- 🔜 **Visual Designer** - GUI builder for layouts
- 🔜 **Hot Reload** - Live code updates during development
- 🔜 **Better Debugging** - Enhanced debugging tools
- 🔜 **IntelliJ Plugin** - IDE integration

**Target Release:** v2.0 in Q4 2026 (Phases 1-4), v2.5 in Q1 2027 (i18n, plugins, DX)

---

## Future: v3.0 (2027+) 💡

### Performance
- 💡 **GPU-Accelerated Rendering** - Hardware acceleration
- 💡 **Multi-threaded Rendering** - Parallel render pipeline
- 💡 **Render Caching** - Smart caching strategies

### Platform Innovation
- 💡 **WebAssembly Backend** - Run in browsers via WASM
- 💡 **Network Transparency** - X11-style remote rendering
- 💡 **Container Support** - Optimized for Docker/Kubernetes

### Advanced UI
- 💡 **Rich Text** - Markdown, HTML-like formatting
- 💡 **Charts & Graphs** - Built-in data visualization
- 💡 **Video Playback** - Terminal video support
- 💡 **Audio Integration** - Sound effects and notifications

### AI Integration
- 💡 **AI-Powered Layouts** - Automatic layout optimization
- 💡 **Accessibility AI** - Smart screen reader integration
- 💡 **Natural Language UI** - Voice command support

**Target Release:** 2027+

---

## Continuous Improvements

### Every Release
- 🔄 Security updates
- 🔄 Dependency updates
- 🔄 Performance optimizations
- 🔄 Bug fixes
- 🔄 Documentation improvements

### Ongoing
- 📚 Tutorial series
- 📚 Example applications
- 📚 Video demonstrations
- 📚 Conference talks

---

## Community Requested Features

Vote on features at [GitHub Discussions](https://github.com/FlossWare/curses-java/discussions)!

### Most Requested
1. Windows support (v2.0-beta, Phase 2)
2. Unicode/emoji (v2.0, Phase 4)
3. True Color (v2.0-rc, Phase 3)
4. Plugin system (v2.5)
5. Visual designer (v2.5)
6. Web backend (v3.0)

---

## Contributing

Want to help accelerate development? See [CONTRIBUTING.md](CONTRIBUTING.md)!

**High-impact areas:**
- TerminalBridge interface design (Phase 1 prerequisite)
- Windows PDCurses integration (Phase 2)
- True Color theme development (Phase 3)
- Unicode width calculation and TerminalCell buffer (Phase 4)
- Example applications
- Documentation & tutorials

---

## Milestones

| Version | Target | Key Features | Status |
|---------|--------|--------------|--------|
| v1.28 | 2026-05-24 | Quality & tooling | ✅ Complete |
| v1.29 | 2026-07 | Theme unification (JSON) | 🔄 In progress |
| v2.0-alpha | 2026-08 | TerminalBridge abstraction (Phase 1) | 🎯 Next |
| v2.0-beta | 2026-09 | Windows PDCurses support (Phase 2) | 🎯 Planned |
| v2.0-rc | 2026-10 | True Color 24-bit (Phase 3) | 🎯 Planned |
| v2.0 | 2026-11 | Unicode & emoji (Phase 4) | 🎯 Planned |
| v2.5 | 2027-Q1 | i18n, plugins, animations, DX | 🔜 Future |
| v3.0 | 2027-Q2 | GPU, WASM, network | 💡 Future |

---

**Updated:** 2026-07-18  
**Status:** Active Development  
**Next Review:** 2026-08-01

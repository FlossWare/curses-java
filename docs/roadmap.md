# curses-java Roadmap

## Vision
Make curses-java the premier Java terminal UI library with modern features, exceptional performance, and comprehensive platform support.

---

## Current Status: v1.x (Stable) ✅

### Core Features (Complete)
- ✅ 29 UI widgets (buttons, text fields, tables, etc.)
- ✅ Mouse support (click, drag, resize)
- ✅ Thread-safe architecture with ReentrantLock
- ✅ Color system (8 standard colors, 3 themes)
- ✅ Advanced text editing (selection, cut/copy/paste, undo/redo)
- ✅ Scrollable views (JScrollPane)
- ✅ Layout managers (BorderLayout, FlowLayout, GridLayout)
- ✅ Event system (sealed interfaces & records)
- ✅ Virtual Threads integration
- ✅ Foreign Function & Memory API for ncurses

### Quality Assurance (Complete)
- ✅ 399 comprehensive tests (100% passing)
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

## Next: v2.0 (2026 Q3) 🎯

### Platform Expansion
- 🎯 **Windows PDCurses Support** - Native Windows terminal support
- 🎯 **Unicode & Emoji Support** - Full UTF-8, emoji rendering
- 🎯 **True Color Support** - 24-bit color (16 million colors)

### Internationalization
- 🎯 **i18n Framework** - Internationalization support
- 🎯 **RTL Text Support** - Right-to-left languages (Arabic, Hebrew)
- 🎯 **Locale-aware Components** - Date/time/number formatting

### Advanced Features
- 🎯 **Plugin System** - Extensible architecture for custom widgets
- 🎯 **Animation Framework** - Smooth transitions and effects
- 🎯 **Drag & Drop** - Full drag-and-drop support between components
- 🎯 **Virtual Scrolling** - Handle millions of items efficiently

### Developer Experience
- 🎯 **Visual Designer** - GUI builder for layouts
- 🎯 **Hot Reload** - Live code updates during development
- 🎯 **Better Debugging** - Enhanced debugging tools
- 🎯 **IntelliJ Plugin** - IDE integration

**Target Release:** Q3 2026

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
1. Windows support (v2.0)
2. Unicode/emoji (v2.0)
3. Visual designer (v2.0)
4. Plugin system (v2.0)
5. Web backend (v3.0)

---

## Contributing

Want to help accelerate development? See [CONTRIBUTING.md](CONTRIBUTING.md)!

**High-impact areas:**
- Windows PDCurses integration
- Unicode rendering engine
- Plugin architecture design
- Example applications
- Documentation & tutorials

---

## Milestones

| Version | Target | Key Features | Status |
|---------|--------|--------------|--------|
| v1.28 | 2026-05-24 | Quality & tooling | ✅ Complete |
| v2.0 | 2026-09 | Windows, Unicode, i18n | 🎯 Planning |
| v2.5 | 2026-12 | Plugins, animations | 💡 Future |
| v3.0 | 2027 Q2 | GPU, WASM, network | 💡 Future |

---

**Updated:** 2026-05-24  
**Status:** Active Development  
**Next Review:** 2026-06-01

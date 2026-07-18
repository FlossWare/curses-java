# curses-java & curses-themes Alignment Project

**Last Updated**: July 18, 2026
**Status**: Phase 1 near-complete (~85%) | Phase 2 planning

**Goal**: Create a cohesive cross-language terminal UI ecosystem where themes, concepts, and developer experience are consistent between the full Java framework and the lightweight Python theming library.

## Current State (July 18, 2026)
- **curses-java**: Mature full TUI framework (29+ AWT-like widgets, mouse support, advanced rendering, Java 21 features). Strong theming subsystem with JSON export/import.
- **curses-themes**: Lightweight, zero-dep Python theming layer. Semantic colors, config support, retro/3D themes, and Java JSON format adapter.
- **Shared DNA**: 12 JSON theme files, semantic + component color APIs, 3D effects, `ThemeManager` patterns.
- **Gaps**: No CI schema validation, some ThemeManager API naming drift, no cross-repo tracking issues yet.

## Success Criteria
- Single source of truth for theme definitions (JSON in curses-java)
- Identical semantic + component color APIs (where possible)
- Easy theme sharing between Java and Python
- Unified documentation and examples
- Reduced duplication and maintenance overhead

## Phase 1: Foundation (Near Complete ~85%)

### 1.1 Common Theme Specification
- [x] Define core requirements (semantic colors, components, borders, 3D metadata)
- [x] Create `/themes/schema.json` (canonical) in `curses-java` (source of truth)
- [x] Migrate all 12 built-in themes to comply with schema (all validate)
- [x] Java validation via `ThemeLoader.fromJson()` (structural + color name validation)
- [ ] Python-side JSON Schema validation (currently validates own format, not schema.json)
- [ ] CI schema validation for both repos

### 1.2 Theme Data Synchronization
- [x] Export Java themes as JSON to `/themes/` directory (12 themes)
- [x] Python `curses-themes` loads Java JSON files natively (`_convert_java_to_python()` adapter)
- [x] `load_theme_from_file()` in Python + `ThemeLoader.fromJson()` in Java
- [x] ADR 0006: JSON-only unification strategy documented

**Phase 1 remaining**: CI schema validation in both repos.

## Phase 2: API & Behavior Parity

### 2.1 ThemeManager API Alignment

Current state:

| Capability | Java (`ThemeManager`) | Python (`ThemeManager`) | Status |
|---|---|---|---|
| Load by name | `useTheme(name)` | `load(name)` | Names differ |
| Register theme | implicit (constructor) | `register(cls)` | Pattern differs |
| List themes | `getAvailableThemes()` | `list_themes()` | Names differ |
| Create inline | N/A | `create(...)` | Python only |
| Load from file | `loadThemeFromJson(path)` | `load_from_file(path)` | Both exist |
| Load directory | `loadThemesFromDirectory(path)` | N/A | Java only |

- [ ] Document canonical API names and decide on alignment direction
- [ ] Add `load_themes_from_directory()` to Python
- [ ] Standardize naming conventions across languages

### 2.2 Colors & Semantics
- [x] 8 semantic colors defined (background, foreground, primary, success, error, warning, info, accent)
- [x] 7 component color pairs defined (background, button, button_focused, text_input, border, selection, disabled)
- [ ] Lock down exact fallback behavior when colors are missing
- [ ] Document color mapping rules (`color_pair()` in Python vs Java equivalents)

### 2.3 3D & Advanced Themes
- [x] 3D properties defined: shadow, highlight, lowlight, shadow_offset, rendering_style
- [x] Java 3D themes (borland3d, dbase4-3d) exported with full 3D metadata
- [x] Python adapter converts Java 3D keys to Python format
- [ ] Shared test suite for visual consistency
- [ ] Common border character set documentation

### 2.4 Color Format
- [x] Java uses ncurses color names (BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE)
- [x] Python uses RGB tuples with `NCURSES_COLOR_MAP` for conversion
- [ ] Decide: should schema allow RGB values directly for extended color support?

## Phase 3: DX & Ecosystem

### 3.1 Examples & Demos
- [ ] Shared example UIs (form, dashboard, theme switcher, retro app)
- [ ] Side-by-side code snippets in docs

### 3.2 Documentation
- [ ] Unified THEMES.md with language tabs or cross-references
- [ ] Cross-repository links in READMEs
- [ ] Migration guide (Java theme <-> Python)

### 3.3 Tooling & CI
- [ ] Theme validation script (run in CI for both repos)
- [ ] Sync check in GitHub workflows (detect schema drift)
- [ ] Optional theme converter utility

## Phase 4: Release & Maintenance
- [ ] Coordinated version bump + announcement
- [ ] Interoperability tests
- [ ] Long-term decision: mono-repo, shared submodule, or continued separate repos?

## Open Questions
- How much widget helper code should Python add to narrow the gap?
- Should we publish a shared `flossware-themes` data package (npm/PyPI)?
- Priority for Windows/Unicode support alignment?
- Should schema.json allow RGB values alongside ncurses color names?

## Architecture Decisions
- **ADR 0006**: JSON-only unification strategy (no YAML, no XML for shared themes)
- **ADR 0004**: Rust/UniFFI evaluated and rejected for shared rendering
- **ADR 0005**: Windows/Unicode 4-phase roadmap for curses-java v2.0

## Key Files

**curses-java** (source of truth):
- `themes/schema.json` — canonical JSON Schema
- `themes/*.json` — 12 theme definitions
- `src/.../theme/ThemeLoader.java` — JSON parser/serializer
- `src/.../theme/ThemeManager.java` — theme registry + lifecycle
- `docs/adr/0006-unification-strategy.md` — decision record

**curses-themes** (consumer):
- `curses_themes/config_theme.py` — Java format adapter (`_convert_java_to_python()`)
- `curses_themes/manager.py` — ThemeManager (load, register, list_themes, create)
- `tests/test_config_theme.py` — 201 tests including 48 Java format tests

**Tracking**: Use label `theme-alignment` in both repositories.

---
*This document is living. Update as work progresses.*

# curses-java & curses-themes Alignment Project

**Last Updated**: July 18, 2026
**Status**: Phase 1 complete | Phase 2 complete | Phase 3 in progress

**Goal**: Create a cohesive cross-language terminal UI ecosystem where themes, concepts, and developer experience are consistent between the full Java framework and the lightweight Python theming library.

## Current State (July 18, 2026)
- **curses-java**: Mature full TUI framework (29+ AWT-like widgets, mouse support, advanced rendering, Java 21 features). Strong theming subsystem with JSON export/import.
- **curses-themes**: Lightweight, zero-dep Python theming layer. Semantic colors, config support, retro/3D themes, and Java JSON format adapter.
- **Shared DNA**: 12 JSON theme files, semantic + component color APIs, 3D effects, `ThemeManager` patterns.
- **Gaps**: Remaining Phase 3 DX items (shared examples, theme converter utility).

## Success Criteria
- Single source of truth for theme definitions (JSON in curses-java)
- Identical semantic + component color APIs (where possible)
- Easy theme sharing between Java and Python
- Unified documentation and examples
- Reduced duplication and maintenance overhead

## Phase 1: Foundation (Complete)

### 1.1 Common Theme Specification
- [x] Define core requirements (semantic colors, components, borders, 3D metadata)
- [x] Create `/themes/schema.json` (canonical) in `curses-java` (source of truth)
- [x] Migrate all 12 built-in themes to comply with schema (all validate)
- [x] Java validation via `ThemeLoader.fromJson()` (structural + color name validation)
- [x] Python-side JSON Schema validation (CI job loads all Java themes via adapter)
- [x] CI schema validation for both repos (`validate-themes.yml` in Java, `validate-themes` job in Python)

### 1.2 Theme Data Synchronization
- [x] Export Java themes as JSON to `/themes/` directory (12 themes)
- [x] Python `curses-themes` loads Java JSON files natively (`_convert_java_to_python()` adapter)
- [x] `load_theme_from_file()` in Python + `ThemeLoader.fromJson()` in Java
- [x] ADR 0006: JSON-only unification strategy documented

## Phase 2: API & Behavior Parity (Complete)

### 2.1 ThemeManager API Alignment

| Capability | Java (`ThemeManager`) | Python (`ThemeManager`) | Status |
|---|---|---|---|
| Load by name | `useTheme(name)` | `load(name)` | Aligned (idiomatic per language) |
| Register theme | implicit (constructor) | `register(cls)` | Aligned (idiomatic per language) |
| List themes | `getAvailableThemes()` | `list_themes()` | Aligned (idiomatic per language) |
| Create inline | N/A | `create(...)` | Python only (not needed in Java) |
| Load from file | `loadThemeFromJson(path)` | `load_from_file(path)` | Both exist |
| Load directory | `loadThemesFromDirectory(path)` | `load_themes_from_directory(path)` | Both exist |

- [x] Document canonical API names (see `docs/themes.md` cross-language section and `docs/MIGRATION.md`)
- [x] Add `load_themes_from_directory()` to Python
- [x] API mapping documented — names differ by language convention (Java camelCase vs Python snake_case)

### 2.2 Colors & Semantics
- [x] 8 semantic colors defined (background, foreground, primary, success, error, warning, info, accent)
- [x] 7 component color pairs defined (background, button, button_focused, text_input, border, selection, disabled)
- [x] Color mapping rules documented in `docs/MIGRATION.md`

### 2.3 3D & Advanced Themes
- [x] 3D properties defined: shadow, highlight, lowlight, shadow_offset, rendering_style
- [x] Java 3D themes (borland3d, dbase4-3d) exported with full 3D metadata
- [x] Python adapter converts Java 3D keys to Python format
- [x] Shared test suite for visual consistency (58 tests in `test_3d_consistency.py`)
- [x] Border character sets documented in MIGRATION.md (Java vs Python ordering)

### 2.4 Color Format
- [x] Java uses ncurses color names (BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE)
- [x] Python uses RGB tuples with `NCURSES_COLOR_MAP` for conversion
- [x] Conversion is automatic — Python adapter handles ncurses→RGB at parse time

## Phase 3: DX & Ecosystem (In Progress)

### 3.1 Examples & Demos
- [ ] Shared example UIs (form, dashboard, theme switcher, retro app)
- [x] Side-by-side code snippets in docs (in `docs/themes.md` and `docs/MIGRATION.md`)

### 3.2 Documentation
- [x] Cross-language section in `docs/themes.md` with API mapping table
- [x] Cross-repository links in READMEs
- [x] Migration guide (`docs/MIGRATION.md` — Java↔Python theme conversion)

### 3.3 Tooling & CI
- [x] Theme validation in CI for both repos (schema validation + Python adapter tests)
- [ ] Sync check in GitHub workflows (detect schema drift between repos)
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
- `curses_themes/manager.py` — ThemeManager (load, register, list_themes, create, load_themes_from_directory)
- `tests/test_config_theme.py` — 201 tests including 48 Java format tests
- `tests/test_3d_consistency.py` — 58 cross-language 3D consistency tests
- `tests/test_manager.py` — 38 tests including load_themes_from_directory

**Tracking**: Use label `theme-alignment` in both repositories.

---
*This document is living. Update as work progresses.*

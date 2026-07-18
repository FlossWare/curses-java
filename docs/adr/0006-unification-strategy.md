# ADR 0006: Unification Strategy for curses-java and curses-themes

**Status:** Implemented
**Date:** 2026-07-18
**Deciders:** Project Maintainer
**Tags:** architecture, themes, cross-language, json

## Context

Two parallel implementations of curses-based theme systems exist:

- **curses-java** (Java 21, 75 source files) -- Uses the Foreign Function & Memory API to call ncurses. Defines themes via Java interfaces (`Theme`, `Theme3D`) with 12 concrete theme classes, managed by a singleton `ThemeManager`.
- **curses-themes** (Python) -- Pure Python calling ncurses via the standard library. Implements identical `Theme3D` interfaces with its own set of theme definitions.

Both projects share the same conceptual model:

- 7 required color pairs (background, button, button_focused, text_input, border, selection, disabled)
- 8-character border strings (TL, T, TR, L, R, BL, B, BR)
- Optional 3D rendering properties (shadow, highlight, lowlight colors; shadow offsets; rendering style)
- ~125 lines of border/shadow rendering logic per implementation

The duplication creates a maintenance burden: adding or modifying a theme requires changes in both codebases. The rendering algorithms must also be kept in sync manually.

### Solutions Evaluated

A multi-AI analysis (issue #233) identified three approaches:

1. **Rust/UniFFI shared library** -- Write theme logic once in Rust, generate bindings for Java and Python via UniFFI. Eliminates all duplication but adds a native build dependency, complicates CI/CD, and requires Rust expertise.

2. **JSON-only shared data** -- Define themes as JSON files validated against a shared schema. Each language implements its own loader (~50-80 lines) and keeps its rendering code. Eliminates theme data duplication (~400 lines) while accepting rendering logic duplication (~125 lines per language).

3. **Code generation** -- Generate theme classes from a shared specification. Eliminates data duplication but requires a build-time generator, and the rendering code must still be maintained separately.

## Decision

Adopt the **JSON-only approach (Solution #2)** as the primary unification strategy.

Theme data is defined once in JSON files under `themes/` and consumed by both implementations. Rendering logic (~125 lines per language) remains duplicated, which is acceptable given its stability -- the border-drawing and shadow-rendering algorithms have not changed since initial implementation.

The Rust/UniFFI approach (Solution #1) is evaluated separately in issue #263 as a potential future optimization, not a prerequisite for unification.

### Design

```
themes/                         Shared theme data (language-neutral)
  schema.json                   JSON Schema (draft-07) for validation
  borland.json                  12 theme definitions
  borland3d.json                  (including 2 with 3D properties)
  dark.json
  ...

curses-java/                    Java consumer
  ThemeLoader.java              fromJson() / toJson() conversion
  ThemeManager.java             Singleton registry with useTheme(name)

curses-themes/                  Python consumer (planned)
  theme_loader.py               from_json() / to_json() conversion
```

### JSON Schema

The shared schema (`themes/schema.json`) defines:

- **Required fields:** `name`, `version`, `colors`, `borders`
- **Color format:** Named strings (`"WHITE"`, `"BLUE"`) or RGB arrays (`[255, 255, 255]`)
- **Border format:** 8-character string in order TL-T-TR-L-R-BL-B-BR
- **3D properties (optional):** `shadow_color`, `highlight_color`, `lowlight_color`, `shadow_offset`, `rendering_style`

## What Is Implemented

Completed in issue #264:

- **JSON schema** (`themes/schema.json`) -- Formal specification for theme files
- **12 JSON theme files** -- All existing themes exported: borland, borland3d, dark, dbase3, dbase4, dbase4-3d, default, dos, light, modern, ti994a, trs80
- **ThemeLoader (Java)** -- `fromJson(Path)` reads a JSON file and returns a `Theme` or `Theme3D` object; `toJson(Theme)` serializes a theme to JSON
- **ThemeManager enhancements** -- `loadThemesFromDirectory(Path)` scans a directory for `.json` files and registers them; `useTheme(String name)` switches by name; `getAvailableThemes()` lists registered theme names
- **themes/README.md** -- Documents the shared format, usage in both languages, and custom theme creation

## What Remains

1. **Shared theme repository** -- The canonical JSON files currently live in `curses-java/themes/`. A dedicated repository (e.g., `curses-theme-specs`) or git submodule could hold them so neither project is the "owner." This is optional -- both projects can also simply copy the files, since the schema enforces compatibility. (Evaluated in ADR 0007; decision: keep in curses-java repo.)

2. **Rendering algorithm documentation** -- The ~125 lines of border-drawing and 3D shadow rendering logic are duplicated. A specification document describing the algorithm (edge coloring order, shadow offset calculation, character placement) would help keep the implementations in sync during future changes.

3. **Theme contribution workflow** -- Document the process for adding a new theme: create the JSON file, validate against schema, and it works in both languages without code changes.

### Completed Since Initial Writing

- **Python consumer** -- `curses-themes` now loads Java JSON files natively via `_convert_java_to_python()` adapter, with `load_from_file()` and `load_themes_from_directory()`.
- **Schema validation in CI** -- `validate-themes.yml` validates all JSON files in `themes/` against `schema.json` on every commit.

## Consequences

### Positive

- **Theme data defined once.** Adding or modifying a theme is a single JSON file edit, not parallel changes across two codebases.
- **No new build dependencies.** JSON parsing is available in both Java (jakarta.json or built-in) and Python (stdlib `json`) with zero additional dependencies.
- **User-extensible.** End users can create custom themes by writing JSON files -- no compilation, no code changes, no language knowledge required.
- **Schema-validated.** The JSON Schema catches errors at validation time rather than at runtime.
- **Incremental adoption.** Each implementation can migrate to JSON-based themes at its own pace. Existing hardcoded theme classes continue to work alongside JSON-loaded themes.
- **Low risk.** The approach builds on standard, well-understood technologies (JSON, JSON Schema). No novel tooling to maintain.

### Negative

- **Rendering logic remains duplicated (~125 lines per language).** If the rendering algorithm changes, both implementations must be updated. This is mitigated by the algorithm's stability and by documenting it explicitly.
- **No compile-time type safety for theme data.** JSON is validated at load time, not at compile time. Schema validation in CI mitigates this.
- **Two sources of theme definitions during migration.** Until all hardcoded Java theme classes are removed (or made to delegate to JSON), themes exist in both formats. This is a transitional state.

### Comparison with Rust/UniFFI (Solution #1)

| Criterion | JSON-only (adopted) | Rust/UniFFI (#263) |
|-----------|--------------------|--------------------|
| Theme data duplication | Eliminated | Eliminated |
| Rendering logic duplication | ~125 lines/language | Eliminated |
| Build complexity | None (JSON parsing is stdlib) | Rust toolchain + UniFFI + native libs |
| CI/CD impact | Minimal (schema validation) | Significant (cross-compile matrix) |
| New language required | No | Yes (Rust) |
| User-defined themes | JSON file drop-in | Requires Rust rebuild or JSON layer |
| Risk | Low | Medium-high |

The JSON-only approach was chosen because it eliminates the primary source of duplication (theme data, ~400 lines) with minimal complexity. The remaining rendering duplication (~125 lines per language) is small, stable, and well-tested. If rendering logic divergence becomes a problem in the future, the Rust/UniFFI approach from #263 can be adopted incrementally.

## References

- [Issue #233](https://github.com/FlossWare/curses-java/issues/233) -- Parent architecture issue: "Unify curses-java and curses-themes with shared implementation"
- [Issue #264](https://github.com/FlossWare/curses-java/issues/264) -- ThemeLoader + JSON theme support (completed); implements the shared JSON data layer
- [Issue #263](https://github.com/FlossWare/curses-java/issues/263) -- Rust/UniFFI spike (rejected per ADR 0004); evaluated whether a shared native library is worthwhile
- `themes/schema.json` -- JSON Schema (draft-07) defining the theme file format for cross-language validation
- `themes/README.md` -- End-user documentation for the shared theme format, including examples and custom theme creation
- `src/main/java/org/flossware/curses/theme/Theme.java` -- Base theme interface defining 7 color pairs and border characters
- `src/main/java/org/flossware/curses/theme/Theme3D.java` -- Extended theme interface adding shadow, highlight, and lowlight properties
- `src/main/java/org/flossware/curses/theme/ThemeManager.java` -- Singleton theme registry with runtime theme switching

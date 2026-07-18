# ADR 0005: Windows Support, True Color, and Unicode Roadmap

**Status:** Accepted  
**Date:** 2026-07-18  
**Deciders:** Core Team  
**Tags:** architecture, platform, color, unicode, windows

## Context

curses-java is currently Linux-only, hardcoded to ncurses via the Foreign Function & Memory API (FFM). The `NcursesBridge` class is a static utility with no abstraction layer, the `Color` enum defines only the 8 standard ncurses colors (0-7), and the `DiffEngine` rendering pipeline uses `char[][]` buffers that assume one character per cell. Issue #236 requests prioritizing Windows support and modern terminal features (True Color, Unicode, emoji).

### Current Limitations

1. **Platform lock-in**: `NcursesBridge` loads `libncurses.so` / `libncurses.dylib` directly. Windows hits an explicit `UnsupportedOperationException`.
2. **8-color ceiling**: `Color` is an enum of 8 values. `ColorPair` is a record of two `Color` values. The entire theme system (`Theme` interface, 10+ theme classes, `ThemeManager`) builds on this enum. There is no path to 256-color or 24-bit color without changing the color model.
3. **No Unicode awareness**: `DiffEngine` uses `char[][]` (16-bit Java chars). CJK wide characters, combining marks, and emoji (which are surrogate pairs or multi-codepoint sequences) will render incorrectly because each occupies two or more terminal columns but is stored as a single cell.
4. **No bridge abstraction**: `NcursesBridge` is a concrete static class. `MockNcursesBridge` exists for testing but does not implement a shared interface -- it is a separate parallel class.

## Decision

We will implement cross-platform support, True Color, and Unicode in four sequential phases, each producing a shippable increment.

## Phase 1: Abstract the Native Bridge (v2.0-alpha)

**Goal**: Decouple all application code from ncurses specifics so that alternative backends can be plugged in.

### Architecture Changes

```
Before:
  Component -> NcursesBridge (static, ncurses-specific)

After:
  Component -> TerminalBridge (interface)
                  |
                  +-- NcursesTerminalBridge (Linux/macOS, FFM to ncurses)
                  +-- MockTerminalBridge (testing, replaces MockNcursesBridge)
                  +-- [future: PdcursesTerminalBridge, AnsiTerminalBridge]
```

**Key interface methods** (derived from current NcursesBridge public API):

```java
public interface TerminalBridge {
    void init();
    void stop();
    void refresh();
    int getChar();
    void moveCursor(int y, int x, char ch);
    void clear();
    void setNonBlocking(boolean nonBlocking);
    long enableMouse(long mask);
    MouseEventData getMouseEvent();
    void startColor();
    void initColorPair(short pair, short fg, short bg);
    int getColorPair(int pairNumber);
    void enableAttribute(int attr);
    void disableAttribute(int attr);
    int getTerminalHeight();
    int getTerminalWidth();
    boolean isAvailable();
}
```

**Platform detection** via a factory:

```java
public class TerminalBridgeFactory {
    public static TerminalBridge create() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux") || os.contains("mac")) {
            return new NcursesTerminalBridge();
        } else if (os.contains("windows")) {
            return new PdcursesTerminalBridge(); // Phase 2
        }
        throw new UnsupportedOperationException("Unsupported OS: " + os);
    }
}
```

**Migration path**: `NcursesBridge` static methods become instance methods on `NcursesTerminalBridge`. Existing callers are updated to go through the interface. `MockNcursesBridge` implements `TerminalBridge` directly.

**Estimated effort**: 2-3 weeks. This is the critical-path prerequisite for all subsequent phases.

### Risks

- Regressions in rendering during the static-to-instance migration.
- Performance: virtual dispatch overhead is negligible for terminal I/O (microseconds per call vs. milliseconds for screen refresh).

### Mitigations

- Full test suite (799 tests) must remain green throughout.
- `MockTerminalBridge` replaces `MockNcursesBridge`, exercising the same interface the real bridge uses.

---

## Phase 2: Windows Support via PDCurses (v2.0-beta)

**Goal**: Run curses-java on Windows natively.

### Options Evaluated

| Approach | Pros | Cons | Verdict |
|----------|------|------|---------|
| **PDCurses** (via FFM) | ncurses-compatible API; minimal code changes; mature C library | Requires shipping a `.dll`; feature parity varies | **Selected** |
| **Windows Console API** (via FFM) | No external dependency; full Windows feature access | Completely different API surface; large implementation effort | Deferred |
| **crossterm** (Rust, via FFM or JNI) | Modern, cross-platform, True Color built in | Rust FFI complexity; non-Java dependency chain | Rejected |
| **Pure Java ANSI** (no native library) | Zero native dependencies; works everywhere | Limited mouse support; no access to terminal size without native calls; fragile | Fallback option |

### PDCurses Strategy

1. **FFM binding**: `PdcursesTerminalBridge` implements `TerminalBridge`, loading `pdcurses.dll` via `SymbolLookup.libraryLookup()`. The function signatures are intentionally compatible with ncurses, so most method handles will be identical.
2. **Distribution**: Bundle `pdcurses.dll` (pre-compiled for x64) in `src/main/resources/native/windows-x64/`. Extract to temp directory at runtime. This adds approximately 200KB to the JAR.
3. **Mouse differences**: PDCurses uses different mouse event constants. The bridge normalizes these to the existing `MouseEventData` contract.
4. **Testing**: CI adds a Windows runner (GitHub Actions `windows-latest`). Integration tests run against the mock bridge on all platforms; native tests run on their respective OSes.

### ANSI Fallback

For terminals where neither ncurses nor PDCurses is available (e.g., Windows Terminal without PDCurses, SSH sessions), provide `AnsiTerminalBridge` using raw ANSI escape sequences:

- Output: `\033[y;xH` for cursor positioning, `\033[m` for attributes.
- Input: Parse ANSI escape sequences for arrow keys and mouse (xterm mouse protocol).
- Limitation: No reliable terminal-size detection without native calls; fall back to 80x24.

**Estimated effort**: 3-4 weeks after Phase 1.

---

## Phase 3: True Color (24-bit) Support (v2.0-rc)

**Goal**: Expand from 8 colors to 16 million while maintaining backward compatibility with existing themes.

### Color Model Changes

```
Before:
  Color (enum, 8 values) -> ColorPair (record) -> Theme (interface)

After:
  TerminalColor (sealed interface)
    +-- StandardColor (enum, 8 values -- backward compatible)
    +-- ExtendedColor (record, 0-255 index)
    +-- TrueColor (record, int r, int g, int b)
```

```java
public sealed interface TerminalColor
        permits StandardColor, ExtendedColor, TrueColor {
    int toNcursesCode();  // For backward compatibility
}

public enum StandardColor implements TerminalColor {
    BLACK(0), RED(1), GREEN(2), YELLOW(3),
    BLUE(4), MAGENTA(5), CYAN(6), WHITE(7);
    // ...
}

public record ExtendedColor(int index) implements TerminalColor {
    // 256-color xterm palette (indices 0-255)
}

public record TrueColor(int r, int g, int b) implements TerminalColor {
    // 24-bit RGB
}
```

### Theme System Updates

- `Theme` interface methods change return type from `ColorPair` to a new `TerminalColorPair` that accepts `TerminalColor` instead of `Color`.
- Existing themes continue to work: `StandardColor` maps 1:1 to current `Color` enum values.
- New themes can use `TrueColor` for rich gradients, subtle highlights, etc.
- JSON theme files (already present in `themes/` directory) gain optional `"rgb"` fields alongside existing color names.

### Terminal Capability Detection

```java
public enum ColorCapability {
    MONOCHROME,     // No color
    STANDARD_8,     // 8 colors (current)
    EXTENDED_256,   // 256 colors (xterm)
    TRUE_COLOR      // 24-bit (16M colors)
}
```

Detection strategy:
1. Check `COLORTERM=truecolor` or `COLORTERM=24bit` environment variable.
2. Check `TERM` for `256color` suffix.
3. Query terminal with `\033[48;2;1;2;3m` and check response (where feasible).
4. Fall back to 8-color mode.

### Bridge Interface Extensions

```java
// Added to TerminalBridge
ColorCapability getColorCapability();
void initTrueColorPair(short pair, int fgR, int fgG, int fgB, int bgR, int bgG, int bgB);
```

For ncurses: uses `init_extended_color()` (ncurses 6.1+) or ANSI escape sequences (`\033[38;2;r;g;bm`).
For PDCurses: True Color support depends on the PDCurses build; fall back to nearest 8-color match.

### Backward Compatibility

- `Color` enum is deprecated but retained. A `StandardColor.fromLegacy(Color c)` conversion method bridges old and new code.
- All 10+ existing themes continue to compile and render identically.
- The `DiffEngine` color tracking (`int[][] currentColors / backColors`) already uses int pair numbers and does not need structural changes.

**Estimated effort**: 3-4 weeks after Phase 2.

---

## Phase 4: Unicode and Emoji Support (v2.0)

**Goal**: Correctly render wide characters, combining marks, and emoji in the terminal.

### Problem Analysis

Terminal rendering of Unicode has three distinct challenges:

1. **Wide characters** (CJK ideographs, some symbols): Occupy 2 terminal columns. The rendering engine must track column width, not character count.
2. **Combining marks** (accents, diacritics): Zero terminal width, combine with preceding character.
3. **Emoji** (often surrogate pairs or multi-codepoint ZWJ sequences): Occupy 2 terminal columns, may be represented as multiple Java `char` values.

### Rendering Pipeline Changes

**DiffEngine buffer**: Change from `char[][]` to `TerminalCell[][]`:

```java
public record TerminalCell(
    String grapheme,     // One or more code points forming a visual unit
    int displayWidth,    // 1 for narrow, 2 for wide, 0 for combining
    int colorPairNum
) {
    public static final TerminalCell EMPTY = new TerminalCell(" ", 1, 0);
    public static final TerminalCell CONTINUATION = new TerminalCell("", 0, 0);
}
```

Wide characters occupy their primary cell plus a `CONTINUATION` cell to the right. The diff engine skips continuation cells during comparison.

**Width calculation**: Use `java.lang.Character.UnicodeBlock` and East Asian Width properties. For robust width detection, consult the Unicode `EastAsianWidth.txt` data (available via ICU4J or a bundled lookup table).

```java
public final class UnicodeWidth {
    public static int displayWidth(int codePoint) {
        // 0 for combining marks
        // 2 for CJK, fullwidth, wide emoji
        // 1 for everything else
    }

    public static int displayWidth(String grapheme) {
        return grapheme.codePoints()
            .map(UnicodeWidth::displayWidth)
            .max().orElse(1);
    }
}
```

### Bridge Interface Extensions

```java
// Replace moveCursor(int y, int x, char ch) with:
void putString(int y, int x, String grapheme);
```

For ncurses: calls `mvaddwstr()` or `mvaddnwstr()` (wide-character ncurses functions, requires linking `libncursesw`).
For PDCurses: PDCurses has `PDC_mbstowcs()` for wide character support.

### Component Updates

Every component that measures text or positions content needs updating:

- `JLabel`, `JButton`, `JTextField`, `JTextArea`: Use `UnicodeWidth.displayWidth()` instead of `String.length()` for layout calculations.
- `JTable`: Column width calculations must account for wide characters.
- `JComboBox`, `JList`: Item rendering must handle variable-width text.
- `Theme.getBorderChars()` already supports Unicode box-drawing characters -- no changes needed there.

### RTL and Bidirectional Text

Deferred to a separate effort (i18n framework, listed in the roadmap as a separate v2.0 item). This phase handles character width only, not text direction.

**Estimated effort**: 4-6 weeks after Phase 3.

---

## Error Handling and Graceful Degradation

Each phase must handle failure gracefully so that the library remains usable even when optimal features are unavailable:

- **Bridge loading failure**: If the preferred `TerminalBridge` (e.g., PDCurses on Windows) fails to load, fall back to `AnsiTerminalBridge`. If that also fails, throw a clear `UnsupportedOperationException` with install instructions.
- **Color capability mismatch**: If a theme specifies `TrueColor` values but the terminal only supports 8 colors, the bridge maps each `TrueColor` to the nearest `StandardColor` using Euclidean distance in RGB space. Themes render correctly (if less vibrantly) on limited terminals.
- **Unicode width errors**: If `UnicodeWidth.displayWidth()` returns an incorrect value for a rare character, the worst case is a column alignment glitch -- not a crash. The `TerminalCell` model tolerates width miscalculations gracefully by clamping to available screen width.
- **Native library extraction failure**: If the bundled `pdcurses.dll` cannot be extracted to a temp directory (permissions, disk space), the factory logs a warning and attempts the ANSI fallback.

## Summary Timeline

| Phase | Target | Deliverable | Key Risk |
|-------|--------|-------------|----------|
| Phase 1 | 2026 Q3 (Aug) | `TerminalBridge` interface, refactored `NcursesTerminalBridge` | Regression in existing tests |
| Phase 2 | 2026 Q3 (Sep) | Windows support via PDCurses, ANSI fallback | PDCurses feature parity gaps |
| Phase 3 | 2026 Q4 (Oct) | True Color with backward-compatible color model | Terminal capability detection reliability |
| Phase 4 | 2026 Q4 (Nov) | Unicode/emoji rendering with `TerminalCell` buffer | Width calculation accuracy across terminals |

## Alternatives Considered

### Lanterna (Java terminal library)

**Pros:** Pure Java, cross-platform, already handles Unicode and colors.  
**Cons:** Would replace curses-java entirely rather than extend it. Different API design philosophy (Lanterna is screen-buffer oriented, curses-java is component/widget oriented like AWT).  
**Verdict:** Rejected. curses-java's value is its AWT-compatible API and modern Java 21 features.

### Abandoning ncurses for pure ANSI

**Pros:** No native dependencies at all; maximum portability.  
**Cons:** Loses ncurses features (alternate character set, terminfo database, reliable terminal detection). Mouse support becomes fragile. Performance degrades without ncurses optimization.  
**Verdict:** Rejected as primary strategy. ANSI provided as a fallback only.

### Single monolithic rewrite

**Pros:** Clean architecture from the start.  
**Cons:** Months of work before any deliverable. Risk of scope creep. Existing 799 tests would need rewriting.  
**Verdict:** Rejected. Phased approach delivers incremental value and reduces risk.

## References

- [PDCurses](https://pdcurses.org/) -- Public Domain Curses (Windows-compatible)
- [ncurses wide-character support](https://invisible-island.net/ncurses/ncurses-intro.html#wide) -- ncursesw documentation
- [Unicode East Asian Width](https://www.unicode.org/reports/tr11/) -- UAX #11
- [JEP 454: Foreign Function & Memory API](https://openjdk.org/jeps/454) -- FFM specification
- [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code) -- Terminal control sequences
- [xterm True Color](https://gist.github.com/XVilka/8346728) -- Terminal True Color support tracking

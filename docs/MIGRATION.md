# Theme Migration Guide: Java <-> Python

This guide covers converting themes between [curses-java](https://github.com/FlossWare/curses-java) (Java) and [curses-themes](https://github.com/FlossWare/curses-themes) (Python).

## Java Theme -> Python

**No manual work required.** Python auto-detects Java JSON format and converts on load:

```python
from curses_themes import ThemeManager

# Load any Java-exported JSON theme directly
theme = ThemeManager.load_from_file("themes/borland3d.json")
theme.apply(stdscr)
```

The adapter handles all format differences (color names, border ordering, 3D keys) transparently.

## Python Theme -> Java

Manual conversion is needed. Create a JSON file matching the [schema](../themes/schema.json):

```json
{
  "name": "My Theme",
  "version": "1.0",
  "description": "Theme description",
  "colors": {
    "background":     { "fg": "WHITE", "bg": "BLACK" },
    "button":         { "fg": "CYAN",  "bg": "BLACK" },
    "button_focused": { "fg": "BLACK", "bg": "CYAN"  },
    "text_input":     { "fg": "GREEN", "bg": "BLACK" },
    "border":         { "fg": "WHITE", "bg": "BLACK" },
    "selection":      { "fg": "BLACK", "bg": "WHITE" },
    "disabled":       { "fg": "BLACK", "bg": "BLACK" }
  },
  "borders": {
    "single": "+-+||+-+"
  }
}
```

Then load in Java:

```java
ThemeManager.getInstance().loadThemeFromJson("path/to/my-theme.json");
```

### Converting Python RGB to Java Color Names

Map your Python RGB values to the nearest ncurses name:

| ncurses Name | RGB Equivalent |
|-------------|----------------|
| `BLACK`     | (0, 0, 0)      |
| `RED`       | (255, 0, 0)    |
| `GREEN`     | (0, 255, 0)    |
| `YELLOW`    | (255, 255, 0)  |
| `BLUE`      | (0, 0, 255)    |
| `MAGENTA`   | (255, 0, 255)  |
| `CYAN`      | (0, 255, 255)  |
| `WHITE`     | (255, 255, 255)|

## Key Differences

| Aspect | Java (curses-java) | Python (curses-themes) |
|--------|-------------------|----------------------|
| **Color values** | ncurses names: `"CYAN"`, `"BLUE"` | RGB tuples: `(0, 255, 255)` |
| **Border char order** | TL, T, TR, L, **BL, B, BR**, R | TL, T, TR, L, **R, BL, B**, BR |
| **3D shadow key** | `shadow_color` | `shadow` |
| **3D highlight key** | `highlight_color` | `highlight` |
| **3D lowlight key** | `lowlight_color` | `lowlight` |
| **3D offset** | `shadow_offset: { "x": 2, "y": 1 }` | `shadow_offset_x: 2`, `shadow_offset_y: 1` |
| **Color structure** | `colors` object with component pairs | `colors` (semantic) + `components` (pairs) |

### Border Character Order Detail

Java uses an 8-character string: `TL T TR L BL B BR R`

```
Position:  0  1  2  3  4  5  6  7
Java:      TL T  TR L  BL B  BR R
Python:    TL T  TR L  R  BL B  BR
```

Example -- the string `"+-+||+-+"`:
- **Java reads:** `+` `-` `+` `|` `|` `+` `-` `+` (positions 4-5 = BL, B)
- **Python reads:** `+` `-` `+` `|` `|` `+` `-` `+` (positions 4-5 = R, BL)

The Python adapter swaps positions 4/7 and 5/6 automatically when loading Java JSON.

## Creating Themes That Work in Both

Write your theme in **Java JSON format** (the source-of-truth format). Both libraries can consume it:

- **Java**: loads it natively via `loadThemeFromJson()`
- **Python**: auto-detects and converts via `load_from_file()`

Minimal example:

```json
{
  "name": "Shared Theme",
  "version": "1.0",
  "colors": {
    "background":     { "fg": "WHITE",  "bg": "BLACK" },
    "button":         { "fg": "CYAN",   "bg": "BLACK" },
    "button_focused": { "fg": "BLACK",  "bg": "CYAN"  },
    "text_input":     { "fg": "GREEN",  "bg": "BLACK" },
    "border":         { "fg": "WHITE",  "bg": "BLACK" },
    "selection":      { "fg": "BLACK",  "bg": "WHITE" },
    "disabled":       { "fg": "BLACK",  "bg": "BLACK" }
  },
  "borders": {
    "single": "+-+||+-+"
  }
}
```

For 3D support, add the `3d` block:

```json
{
  "3d": {
    "shadow_color":    { "fg": "BLACK", "bg": "BLACK" },
    "highlight_color": { "fg": "WHITE", "bg": "CYAN"  },
    "lowlight_color":  { "fg": "BLACK", "bg": "CYAN"  },
    "shadow_offset":   { "x": 2, "y": 1 },
    "rendering_style": "RAISED"
  }
}
```

## Common Pitfalls

1. **Border char ordering** -- If writing Java JSON manually, remember the order is TL, T, TR, L, BL, B, BR, R (position 4 is BL). Python uses TL, T, TR, L, R, BL, B, BR (position 4 is R). The adapter handles this automatically, but manual JSON must use Java ordering.

2. **Color name case sensitivity** -- Java's `Color.valueOf()` is case-sensitive and requires uppercase: `"CYAN"` not `"cyan"`. The Python adapter is case-insensitive, so `"cyan"` works in Python but would fail if loaded by Java directly.

3. **Semantic color defaults** -- When Python loads a Java JSON file, only 3 of 8 semantic colors (background, foreground, primary) are derived from component colors. The other 5 (success, error, warning, info, accent) use hardcoded defaults (`GREEN`, `RED`, `YELLOW`, foreground, foreground). If your app relies on specific semantic colors, define them in a Python-native JSON file instead.

4. **`color_pair()` wrapping** -- In Python curses, you wrap color pair numbers with `curses.color_pair(n)`. In Java, `ColorPair` objects are used directly. Don't confuse the two when porting code.

5. **3D theme detection** -- Java determines 3D vs flat by the presence of a `"3d"` key in JSON. Python determines it by the presence of `"shadow"`, `"highlight"`, `"lowlight"` keys (after conversion). If you manually construct theme dicts, include the right keys for your target.

6. **`schema.json` validates structure, not visuals** -- A theme can be schema-valid but look terrible. Always preview themes in both libraries before shipping.

## Schema Reference

The shared JSON schema is at [`themes/schema.json`](../themes/schema.json). It defines:

- **Required fields:** `name`, `version`, `colors`, `borders`
- **Color values:** ncurses name strings or RGB arrays `[r, g, b]`
- **Border strings:** exactly 8 characters (TL, T, TR, L, BL, B, BR, R)
- **3D block:** optional, requires `shadow_color`, `highlight_color`, `lowlight_color`, `shadow_offset`

Validate a theme file against the schema:

```bash
# Using ajv-cli
npx ajv validate -s themes/schema.json -d themes/my-theme.json

# Using Python jsonschema
python3 -c "
import json, jsonschema
schema = json.load(open('themes/schema.json'))
theme = json.load(open('themes/my-theme.json'))
jsonschema.validate(theme, schema)
print('Valid')
"
```

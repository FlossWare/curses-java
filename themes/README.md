# Shared Theme Specifications

**Single source of truth** for themes in both `curses-java` and `curses-themes`.

## Architecture

```
themes/                    ← This directory (shared data)
├── schema.json            ← JSON Schema for validation
├── borland3d.json         ← Theme definitions
├── dbase4-3d.json
├── dark.json
└── ...

curses-java/               ← Java implementation
└── ThemeLoader.java       ← Reads JSON, creates Theme objects

curses-themes/             ← Python implementation  
└── theme_loader.py        ← Reads JSON, creates Theme objects
```

## Benefits

✅ **Define once** - Add a theme by creating one JSON file  
✅ **Zero duplication** - Theme data shared between languages  
✅ **Touchless** - No compilation, no build steps  
✅ **User-friendly** - Users can create custom themes easily  
✅ **Validated** - JSON Schema ensures correctness  

## Theme Format

See `schema.json` for complete specification.

### Example (Minimal):

```json
{
  "name": "My Theme",
  "version": "1.0",
  "description": "A custom theme",
  "colors": {
    "background": { "fg": "WHITE", "bg": "BLACK" },
    "button": { "fg": "CYAN", "bg": "BLACK" },
    "button_focused": { "fg": "BLACK", "bg": "CYAN" },
    "text_input": { "fg": "GREEN", "bg": "BLACK" },
    "border": { "fg": "WHITE", "bg": "BLACK" },
    "selection": { "fg": "BLACK", "bg": "WHITE" },
    "disabled": { "fg": "BLACK", "bg": "BLACK" }
  },
  "borders": {
    "single": "+-+||+-+"
  }
}
```

### With 3D Support:

```json
{
  "3d": {
    "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
    "highlight_color": { "fg": "WHITE", "bg": "CYAN" },
    "lowlight_color": { "fg": "BLACK", "bg": "CYAN" },
    "shadow_offset": { "x": 2, "y": 1 },
    "rendering_style": "RAISED"
  }
}
```

## Color Formats

**Named colors** (8-color terminals):
```json
{ "fg": "WHITE", "bg": "BLUE" }
```

**RGB values** (256-color/TrueColor terminals):
```json
{ "fg": [255, 255, 255], "bg": [0, 0, 255] }
```

## Border Characters

8-character string in order: **TL T TR L R BL B BR**

Examples:
- ASCII: `"+-+||+-+"`
- Single-line: `"┌─┐│└─┘│"`
- Double-line: `"╔═╗║╚═╝║"`
- Rounded: `"╭─╮│╰─╯│"`

## Available Themes

- `borland3d.json` - Borland Turbo Vision (3D)
- `dbase4-3d.json` - dBASE IV Control Center (3D)
- `dark.json` - Modern dark mode
- *(More to be added)*

## Usage

### Java:
```java
Theme theme = ThemeLoader.fromJson("themes/borland3d.json");
ThemeManager.getInstance().setTheme(theme);
```

### Python:
```python
from curses_themes import ThemeLoader
theme = ThemeLoader.from_json("themes/borland3d.json")
theme.apply(stdscr)
```

## Validation

```bash
# Validate a theme file
jsonschema -i themes/borland3d.json themes/schema.json
```

## Creating Custom Themes

1. Copy an existing theme JSON file
2. Modify colors and borders
3. Validate against schema
4. Use in your application!

No compilation, no code changes needed. Just drop the JSON file in the `themes/` directory.

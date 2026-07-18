# Theme Screenshots

Visual examples of all available themes in the curses-java library.

## 3D Themes

### Borland 3D Theme

Classic Borland Turbo Vision aesthetic with 3D borders and drop shadows.

```
╔════════ Dialog Box ═══════════╗╌╌
║ Yellow on BLUE background     ║╌╌
║                               ║╌╌
║ ╭─────────────────────────╮   ║╌╌
║ │ Raised Button           │   ║╌╌
║ ╰─────────────────────────╯   ║╌╌
║                               ║╌╌
║ Input: [                 ]    ║╌╌
║        └─────────────────┘    ║╌╌
║        Sunken text field      ║╌╌
║                               ║╌╌
║ [ OK ]        [ Cancel ]      ║╌╌
╚═══════════════════════════════╝╌╌
 ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌
    ^^ L-shaped drop shadow
```

**Colors:**
- Background: YELLOW on BLUE
- Buttons: CYAN on BLUE
- Selected: BLACK on CYAN
- Borders: WHITE on BLUE
- Shadows: BLACK on BLACK (bold = gray)

**3D Features:**
- Drop shadows (2 right, 1 down)
- Raised button borders (bright top-left, dark bottom-right)
- Sunken input fields (reversed shading)
- Double-line window borders

### dBASE IV 3D Theme

Authentic dBASE IV Control Center with professional business aesthetic.

```
╔═══════ Database Manager ══════╗╌╌
║ WHITE on BLUE background      ║╌╌
║                               ║╌╌
║ Menu: File Edit View Help     ║╌╌
║       ^^^^                    ║╌╌
║       Yellow highlights       ║╌╌
║                               ║╌╌
║ ┌─────────────────────────┐   ║╌╌
║ │ Record Entry            │   ║╌╌
║ │                         │   ║╌╌
║ │ Name: [             ]   │   ║╌╌
║ │       └─────────────┘   │   ║╌╌
║ │       Cyan input field  │   ║╌╌
║ └─────────────────────────┘   ║╌╌
║                               ║╌╌
║ [ Save ]      [ Cancel ]      ║╌╌
╚═══════════════════════════════╝╌╌
 ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌
```

**Colors:**
- Background: WHITE on BLUE
- Menus: YELLOW on BLUE
- Selected: BLUE on YELLOW
- Input: CYAN on BLUE
- Borders: WHITE on BLUE
- Shadows: BLACK on BLACK (bold = gray)

**3D Features:**
- L-shaped drop shadows (2 right, 1 down)
- Raised menu bar buttons
- Sunken data entry fields
- Double-line dialog borders
- Single-line panel borders

**Differences from Borland 3D:**
- WHITE background text (vs. YELLOW)
- YELLOW menus (vs. CYAN)
- BLUE on WHITE selection (vs. BLACK on CYAN)
- More businesslike, less developer-focused

## Comparison: Borland 3D vs. dBASE IV 3D

### Side-by-Side

```
┌─────────────────────────────┐   ┌─────────────────────────────┐
│ BORLAND 3D                  │   │ dBASE IV 3D                 │
├─────────────────────────────┤   ├─────────────────────────────┤
│                             │   │                             │
│ YELLOW on BLUE              │   │ WHITE on BLUE               │
│ Developer aesthetic         │   │ Business aesthetic          │
│                             │   │                             │
│ Buttons: CYAN on BLUE       │   │ Menus: YELLOW on BLUE       │
│ Selection: BLACK on CYAN    │   │ Selection: BLUE on YELLOW   │
│                             │   │                             │
│ Turbo Pascal/C++ IDEs       │   │ dBASE IV Control Center     │
│ (1989-1995)                 │   │ (1988-1993)                 │
└─────────────────────────────┘   └─────────────────────────────┘
```


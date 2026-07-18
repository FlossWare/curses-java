# curses-java Theme Gallery

> 📸 **[View Theme Screenshots](docs/screenshots/README.md)** - Visual examples with ASCII art demonstrations


A comprehensive guide to the visual themes available in the curses-java library. Each theme recreates the distinctive aesthetic of classic computing systems and development environments, bringing authentic retro computing experiences to modern terminal applications.

## Table of Contents

- [Overview](#overview)
- [Quick Reference Table](#quick-reference-table)
- [Modern Themes](#modern-themes)
- [Classic IDE Themes](#classic-ide-themes)
- [Retro Computer Themes](#retro-computer-themes)
- [Business Software Themes](#business-software-themes)
- [3D Theme System](#3d-theme-system)
  - [Architecture Overview](#architecture-overview)
  - [The Theme3D Interface](#the-theme3d-interface)
  - [RenderingStyle Enum](#renderingstyle-enum)
  - [Multi-Pass Rendering Pipeline](#multi-pass-rendering-pipeline)
  - [Shadow Rendering Details](#shadow-rendering-details)
  - [Enabling 3D on Components](#enabling-3d-on-components)
  - [Asymmetric Border Coloring](#asymmetric-border-coloring)
  - [Borland 3D Theme](#borland-3d-theme)
  - [dBASE IV 3D Theme](#dbase-iv-3d-theme)
  - [Creating a Custom 3D Theme](#creating-a-custom-3d-theme)
  - [Terminal Compatibility for 3D Themes](#terminal-compatibility-for-3d-themes)
- [Usage Guide](#usage-guide)
- [Contributing](#contributing)

## Overview

curses-java includes 10 carefully crafted themes that span four decades of computing history. Each theme authentically recreates the color schemes, border styles, and visual aesthetics of its inspiration while providing a consistent interface for modern terminal applications.

### Theme Categories

- **Modern Themes** (4): Contemporary terminal aesthetics optimized for readability
- **Classic IDE Themes** (1): Iconic development environments from the golden age of programming
- **Retro Computer Themes** (2): Home computers that defined the personal computing revolution
- **Business Software Themes** (3): Database and productivity applications from the PC era

## Quick Reference Table

| Theme | Era | Background | Border Style | Primary Colors | Best For |
|-------|-----|------------|--------------|----------------|----------|
| **Default** | Classic | Black | ASCII | White, Cyan, Green | General purpose, traditional terminals |
| **Dark** | 2010s | Black | Unicode | Cyan, Blue | Modern dark mode applications |
| **Light** | 2010s | White | Unicode (double) | Blue, Black, Cyan | High contrast light mode |
| **Modern** | 2020s | Black | Unicode | White, Cyan, Yellow | Contemporary terminals, accessibility |
| **Borland** | 1985-1995 | Blue | Unicode (rounded) | Yellow, Cyan, White | Turbo Pascal/C++ nostalgia |
| **TI-99/4A** | 1981-1984 | Blue | ASCII | Cyan, White | Texas Instruments home computer |
| **TRS-80** | 1980-1983 | Black | ASCII | White (monochrome) | Radio Shack business applications |
| **DOS** | 1981-1995 | Black | ASCII | White, Yellow, Cyan | MS-DOS command-line utilities |
| **dBASE III** | 1984-1985 | Black | ASCII | White, Cyan, Green | Classic database applications |
| **dBASE IV** | 1988-1993 | Blue | ASCII | White, Yellow, Cyan | Windowed database interfaces |

## Modern Themes

### Default Theme

**Era:** Classic Terminal (1970s-present)  
**Inspiration:** Traditional UNIX/Linux terminal default appearance

#### Color Scheme
- **Background:** White text on black
- **Buttons:** Cyan on black
- **Focused Elements:** Black on cyan (inverted)
- **Text Input:** Green on black
- **Borders:** White on black
- **Selection:** Black on white (inverted)
- **Disabled:** White on black (dimmed)

#### Border Style
```
+-+||+-+
```
Classic ASCII box drawing using basic characters (`+`, `-`, `|`).

#### Usage Recommendations
- General-purpose applications
- Traditional terminal utilities
- Users expecting classic terminal appearance
- Maximum compatibility across all terminal emulators

---

### Dark Theme

**Era:** Modern (2010s-present)  
**Inspiration:** Contemporary dark mode aesthetic

#### Color Scheme
- **Background:** Cyan text on black
- **Buttons:** Blue on black
- **Focused Elements:** Black on blue (inverted)
- **Text Input:** White on black
- **Borders:** Blue on black
- **Selection:** Black on cyan
- **Disabled:** Blue on black (muted)

#### Border Style
```
┌─┐│└─┘│
```
Single-line Unicode box-drawing characters for clean, modern appearance.

#### Usage Recommendations
- Modern terminal applications
- Dark mode enthusiasts
- Applications requiring softer contrast than pure white
- Terminal emulators with full Unicode support

---

### Light Theme

**Era:** Modern (2010s-present)  
**Inspiration:** High-contrast light mode interfaces

#### Color Scheme
- **Background:** Black text on white
- **Buttons:** Blue on white
- **Focused Elements:** White on blue (inverted)
- **Text Input:** Black on light cyan
- **Borders:** Black on white
- **Selection:** White on blue
- **Disabled:** Cyan on white (light gray effect)

#### Border Style
```
╔═╗║╚═╝║
```
Double-line Unicode box-drawing characters for enhanced visual weight.

#### Usage Recommendations
- Bright environment usage
- Users preferring light backgrounds
- High-contrast accessibility requirements
- Professional business applications

---

### Modern Theme

**Era:** Contemporary (2020s)  
**Inspiration:** Modern IDEs and terminal applications with accessibility focus

#### Color Scheme
- **Background:** White text on black
- **Buttons:** Bright cyan on black
- **Focused Elements:** Yellow on black (high contrast)
- **Text Input:** White on black
- **Borders:** White on black
- **Selection:** Black on yellow (bold highlight)
- **Disabled:** Black on black (dark gray effect)

#### Border Style
```
┌─┐│└─┘│
```
Single-line Unicode box-drawing characters.

#### Design Philosophy
The Modern theme emphasizes clarity, readability, and visual hierarchy through strategic use of high-contrast color combinations. It draws inspiration from contemporary dark mode interfaces found in modern IDEs and terminal applications.

#### Accessibility
High-contrast color combinations (white on black, yellow on black, black on yellow) meet WCAG AAA standards for text contrast, making this theme suitable for users with visual impairments or those working in varying lighting conditions.

#### Usage Recommendations
- Terminal applications requiring maximum readability
- Developer tools and command-line interfaces
- Applications where clear focus indication is critical
- Modern terminal emulators with full Unicode support
- Accessibility-focused applications

---

## Classic IDE Themes

### Borland Theme

**Era:** 1985-1995  
**Inspiration:** Borland's Turbo Pascal, Turbo C++, and Borland C++ integrated development environments

#### Historical Context
Recreates the iconic blue background with yellow text and cyan highlights that defined the Borland IDE experience. These development environments were revolutionary for their speed, integrated debuggers, and distinctive visual style. The Borland color scheme became synonymous with serious programming in the late 1980s and early 1990s.

#### Color Scheme
- **Background:** Yellow text on blue
- **Buttons:** Cyan on blue
- **Focused Elements:** Black on cyan (inverted)
- **Text Input:** White on blue
- **Borders:** White on blue
- **Selection:** Black on cyan (bright highlight)
- **Disabled:** Black on blue (dimmed)

#### Border Style
```
╭─╮│╰─╯│
```
Unicode rounded box-drawing characters for smooth, modern appearance.

#### Usage Recommendations
- Programming tools and IDEs
- Applications targeting developers with nostalgia for the Borland era
- Educational software teaching programming fundamentals
- Projects celebrating 1980s/90s computing culture

---

## Retro Computer Themes

### TI-99/4A Theme

**Era:** 1981-1984  
**Inspiration:** Texas Instruments TI-99/4A home computer

#### Historical Context
The TI-99/4A was Texas Instruments' entry into the home computer market and the first 16-bit home computer. It featured the TMS9918A video display processor with a characteristic cyan-on-blue color palette. The cyan text on medium blue background was warmer and more inviting than the stark white-on-black of many competitors, making it distinctive in the early home computer market.

The TI-99/4A competed with the Commodore 64, Apple II, and Atari 8-bit computers. Its distinctive color scheme became iconic, particularly in the BASIC programming environment and title screens.

#### Color Scheme
- **Background:** Cyan on blue
- **Buttons:** White on blue
- **Focused Elements:** Blue on cyan (inverted)
- **Text Input:** Cyan on blue
- **Borders:** Cyan on blue
- **Selection:** Blue on white (high contrast)
- **Disabled:** Blue on blue (muted)

#### Border Style
```
+-+||+-+
```
ASCII borders for 1981-era authenticity (the TI-99/4A predated widespread Unicode).

#### Usage Recommendations
- Retro gaming applications
- Educational software with vintage computer themes
- Projects celebrating early home computer history
- Applications targeting a warm, inviting aesthetic
- Nostalgia-focused terminal applications

---

### TRS-80 Theme

**Era:** 1980-1983  
**Inspiration:** Tandy/Radio Shack TRS-80 Model III and Model 4

#### Historical Context
The TRS-80 line was one of the "1977 Trinity" of home computers (along with the Apple II and Commodore PET). The Model III improved upon the original Model I with an integrated design and cleaner display. The Model 4 added backward compatibility and improved graphics capabilities.

Both models used monochrome displays with exceptional clarity - white (or green, depending on the monitor) phosphor on black. Radio Shack's business-focused marketing emphasized the professional appearance of the monochrome display, contrasting it with the "toy-like" color displays of competitors. The crisp white-on-black text was ideal for word processing and spreadsheet applications.

#### Color Scheme
- **Background:** White on black (pure monochrome)
- **Buttons:** White on black
- **Focused Elements:** Black on white (high-contrast inversion)
- **Text Input:** White on black
- **Borders:** White on black
- **Selection:** Black on white (maximum contrast)
- **Disabled:** Black on black (completely hidden)

#### Border Style
```
+-+||+-+
```
ASCII block-style characters appropriate for the TRS-80's character-cell display.

#### Technical Notes
The TRS-80 Model III and 4 displayed 64×16 characters (expandable to 80×24 on Model 4). The monochrome display was praised for its clarity and lack of color fringing, making it superior for text-heavy applications compared to composite color monitors of the era.

#### Usage Recommendations
- Business applications requiring maximum text clarity
- Word processors and text editors
- Applications emphasizing minimalist design
- Projects celebrating early personal computer history
- Professional-looking monochrome interfaces

---

## Business Software Themes

### DOS Theme

**Era:** 1981-1995  
**Inspiration:** MS-DOS and PC-DOS command-line interface

#### Historical Context
MS-DOS powered the IBM PC and compatibles from 1981 through the mid-1990s. Its text-mode interface, typically running in 80×25 character mode with 16 colors, became the de facto standard for PC software. The default color scheme was simple: white (or light gray) text on a black background, with occasional use of bright colors for emphasis.

Key DOS-era applications like WordPerfect, Lotus 1-2-3, dBASE, and countless utilities all shared this visual language. Even early versions of Windows (1.x-3.x) were launched from this interface. The DOS aesthetic represents the foundation of PC computing.

#### Color Scheme
- **Background:** White on black
- **Buttons:** Yellow on black (bright menu items)
- **Focused Elements:** Black on yellow (inverted)
- **Text Input:** Cyan on black (distinguishes input fields)
- **Borders:** White on black
- **Selection:** Black on white (high-contrast inverted)
- **Disabled:** Black on black (hidden)

#### Border Style
```
+-+||+-+
```
ASCII characters compatible with all terminals (DOS would use extended ASCII: `═║╔╗╚╝` from code page 437).

#### Technical Notes
DOS text mode used the IBM PC's 16-color palette derived from CGA (1981). The 8 base colors could be displayed in normal or bright (high-intensity) variants. This theme maps to the standard 8-color ncurses palette while maintaining the DOS aesthetic through careful color selection.

#### Usage Recommendations
- Command-line utilities and system tools
- File managers and system administration tools
- Applications targeting DOS nostalgia
- Cross-platform tools with DOS heritage
- Utilities requiring maximum terminal compatibility

---

### dBASE III Theme

**Era:** 1984-1985  
**Inspiration:** Ashton-Tate dBASE III and dBASE III Plus

#### Historical Context
dBASE III revolutionized database management on personal computers, bringing mainframe-style database capabilities to the IBM PC. Its distinctive interface featured a black background with white text for the command line, and cyan text for menus and prompts. The program's ".dbf" file format became an industry standard, still used today.

By 1985, dBASE III Plus had become the best-selling database software, powering thousands of custom business applications. Its programming language (xBase) spawned numerous clones including Clipper, FoxPro, and others. The cyan-on-black color scheme became synonymous with database applications.

#### Color Scheme
- **Background:** White on black (the classic dot prompt)
- **Buttons/Menus:** Cyan on black (distinctive menu highlighting)
- **Focused Elements:** Black on cyan (inverted selection)
- **Text Input:** Green on black (data entry fields)
- **Borders:** White on black
- **Selection:** Black on cyan (highlighted database records)
- **Disabled:** Black on black (hidden)

#### Border Style
```
+-+||+-+
```
ASCII borders matching dBASE's simple box-drawing style.

#### Technical Notes
dBASE III ran in DOS text mode (80×25) and used the standard CGA/EGA color palette. The cyan-on-black color scheme was chosen for its high readability on composite monitors and became synonymous with database applications of the era.

#### Usage Recommendations
- Database management applications
- Data entry and form-based interfaces
- Business applications with record browsing
- Projects with xBase or FoxPro heritage
- Applications requiring cyan menu highlighting

---

### dBASE IV Theme

**Era:** 1988-1993  
**Inspiration:** Ashton-Tate/Borland dBASE IV Control Center

#### Historical Context
dBASE IV was released in 1988 with great fanfare, introducing a revolutionary menu-driven interface with multiple windows, pull-down menus, and mouse support. This was a significant departure from dBASE III's command-line focus. The new interface used a blue background with white and yellow text, creating a more modern and accessible appearance.

Despite initial bugs that hurt its reputation, dBASE IV eventually became stable and was acquired by Borland in 1991. Its windowed interface and menu system influenced database tools throughout the 1990s. The Control Center's blue-and-white color scheme became iconic and represented the transition from command-line to GUI-inspired interfaces.

#### Color Scheme
- **Background:** White on blue (Control Center main interface)
- **Buttons/Menus:** Yellow on blue (menu bar and options)
- **Focused Elements:** Blue on yellow (inverted menu selection)
- **Text Input:** Cyan on blue (data entry fields)
- **Borders:** White on blue (window frames)
- **Selection:** Blue on white (highlighted records)
- **Disabled:** Blue on blue (dimmed options)

#### Border Style
```
+-+||+-+
```
ASCII borders (authentic dBASE IV would use extended ASCII for double-line boxes).

#### Technical Notes
dBASE IV required EGA or better graphics (VGA recommended) and took advantage of the expanded color palette. The Control Center interface ran in 80×25 or 80×43/50 text modes, using box-drawing characters for window frames and menus.

The shift from black to blue backgrounds was part of a broader trend in late-1980s software design, also seen in Lotus 1-2-3 Release 3 and other applications moving toward GUI-inspired interfaces.

#### Usage Recommendations
- Windowed database applications
- Menu-driven business software
- Applications with multiple panels or windows
- Projects celebrating late-1980s/early-1990s database evolution
- Software requiring a professional blue-background aesthetic


## 3D Theme System

JCurses provides an optional 3D rendering system that adds depth effects to components through drop shadows, asymmetric border coloring, and rendering style management. The system is fully backward compatible -- themes and components that do not use 3D continue to work unchanged.

### Architecture Overview

The 3D rendering system consists of four key elements:

1. **`Theme3D` interface** -- extends `Theme` with shadow, highlight, and lowlight color definitions
2. **`RenderingStyle` enum** -- controls per-component depth appearance (FLAT, RAISED, SUNKEN, CUSTOM)
3. **`Component` 3D methods** -- `set3DEnabled()`, `setRenderingStyle()`, `paintShadow()`
4. **`Container.drawBorder()`** -- applies asymmetric border coloring based on rendering style

### The Theme3D Interface

`Theme3D` extends the base `Theme` interface with methods that define 3D visual properties. Any theme that implements `Theme3D` automatically reports `supports3D() == true`.

| Method | Purpose | Typical Value |
|--------|---------|---------------|
| `getShadowColor()` | Color pair for drop shadow fill | BLACK/BLACK (+ A_BOLD for gray) |
| `getHighlightColor()` | Color pair for bright (top-left) edges | WHITE/CYAN |
| `getLowlightColor()` | Color pair for dark (bottom-right) edges | BLACK/CYAN |
| `getShadowOffsetX()` | Horizontal shadow displacement in columns | 1-2 |
| `getShadowOffsetY()` | Vertical shadow displacement in rows | 1 |
| `getShadowChar()` | Character used to fill shadow region | `' '` (space) or `'░'` |
| `useGradientShadow()` | Whether to use shade characters for gradient shadows | `false` |
| `getDoubleBorderChars()` | 8-char string for double-line borders (emphasized dialogs) | `"╔═╗║║╚═╝"` |
| `getDefaultRenderingStyle()` | Theme-wide default rendering style for components | `RAISED` |

The base `Theme` interface provides `supports3D()` which returns `false` by default. `Theme3D` overrides this to return `true`, enabling a clean capability check without `instanceof`:

```java
Theme theme = ThemeManager.getInstance().getCurrentTheme();
if (theme.supports3D()) {
    Theme3D theme3d = (Theme3D) theme;
    ColorPair shadow = theme3d.getShadowColor();
    int offsetX = theme3d.getShadowOffsetX();
}
```

### RenderingStyle Enum

The `RenderingStyle` enum controls how highlight and lowlight colors are applied to each component's borders:

| Style | Top-Left Edges | Bottom-Right Edges | Use Case |
|-------|---------------|-------------------|----------|
| `FLAT` | Uniform border color | Uniform border color | Modern flat design; no 3D beveling |
| `RAISED` | Highlight (bright) | Lowlight (dark) | Buttons, toolbars, interactive elements |
| `SUNKEN` | Lowlight (dark) | Highlight (bright) | Input fields, list boxes, recessed panels |
| `CUSTOM` | Component-defined | Component-defined | Complex or non-standard rendering |

Visual representation of RAISED vs SUNKEN:

```
RAISED                          SUNKEN
┌──────────┐  <- bright         ┌──────────┐  <- dark
│          │  <- bright/dark    │          │  <- dark/bright
└──────────┘  <- dark           └──────────┘  <- bright
  ░░░░░░░░░   <- shadow
```

Buttons have special interaction with rendering styles: when a RAISED button is pressed, the rendering pipeline temporarily switches to SUNKEN and shifts the button content 1 character right, creating tactile "pressed down" feedback.

### Multi-Pass Rendering Pipeline

3D rendering uses a strict three-pass pipeline enforced by `Container.paint()`:

```
Pass 1 - Shadow Layer (Component.paintShadow):
  - Renders L-shaped shadow at offset position
  - Uses theme's shadow color and shadow character
  - MUST execute before border rendering

Pass 2 - Border Layer (Container.drawBorder):
  - Applies asymmetric coloring based on RenderingStyle
  - RAISED: highlight on top/left, lowlight on bottom/right
  - SUNKEN: lowlight on top/left, highlight on bottom/right
  - FLAT: uniform border color on all edges

Pass 3 - Content Layer (child painting):
  - Children painted in back-to-front order
  - Each child repeats the shadow -> border -> content cycle
```

The rendering order is critical. Shadows must be painted first so that borders are drawn on top of them, producing correct visual z-ordering. `Container.paint()` enforces this automatically:

```java
// From Container.paint() - order is guaranteed
paintShadow(buffer, null);     // Pass 1: shadow behind everything
drawBorder(buffer, null);       // Pass 2: border with 3D coloring
// Pass 3: iterate children...
for (Component child : snapshot) {
    child.paint(buffer);        // Each child repeats the cycle
}
```

### Shadow Rendering Details

The `paintShadow()` method in `Component` renders an L-shaped shadow consisting of two parts:

- **Vertical part (right edge):** Extends from the component's right edge downward for the component's height, `shadowOffsetX` columns wide (typically 2)
- **Horizontal part (bottom edge):** Extends from the component's left edge rightward for `width + shadowOffsetX` columns, connecting with the vertical shadow

The shadow character is configurable per theme:
- Space (`' '`) produces solid shadows using color attributes only
- Shade characters (`░▒▓`) produce textured or gradient shadows
- When `useGradientShadow()` returns `true`, gradient shading from light to dark is applied

Shadow rendering only occurs when both conditions are met:
1. The component has `is3DEnabled() == true`
2. The current theme returns `supports3D() == true`

### Enabling 3D on Components

Any component can opt into 3D rendering with two method calls:

```java
// Enable 3D rendering (adds shadow, enables styled borders)
component.set3DEnabled(true);

// Set the rendering style (controls border coloring)
component.setRenderingStyle(RenderingStyle.RAISED);
```

Both methods are thread-safe and trigger an automatic repaint. Components default to `enabled3D = false` and `renderingStyle = FLAT`.

Typical patterns by component type:

```java
// Dialogs and windows: raised with shadow
Dialog dialog = new Dialog("Options");
dialog.set3DEnabled(true);
dialog.setRenderingStyle(RenderingStyle.RAISED);

// Input fields: sunken (recessed appearance)
TextField nameField = new TextField(20);
nameField.set3DEnabled(true);
nameField.setRenderingStyle(RenderingStyle.SUNKEN);

// Buttons: raised (pressed state auto-inverts to SUNKEN)
Button okButton = new Button("OK");
okButton.set3DEnabled(true);
okButton.setRenderingStyle(RenderingStyle.RAISED);

// Flat with shadow only (modern floating card look)
Panel card = new Panel();
card.set3DEnabled(true);
card.setRenderingStyle(RenderingStyle.FLAT);
```

### Asymmetric Border Coloring

When 3D rendering is active, `Container.drawBorder()` applies different colors to different border edges. The algorithm in `drawBorder(char[][] buffer, int[][] colorBuffer)`:

1. Checks if the current theme supports 3D via `theme.supports3D()`
2. Retrieves the component's `RenderingStyle`
3. For RAISED style: top and left edges use `getHighlightColor()`, bottom and right edges use `getLowlightColor()`
4. For SUNKEN style: colors are inverted (top/left use lowlight, bottom/right use highlight)
5. For FLAT or CUSTOM: all edges use the uniform `theme.getBorder()` color
6. Corner characters follow their respective edge (top-left corner uses the top-left color, bottom-right corner uses the bottom-right color)

This asymmetric coloring simulates a light source positioned above-left, consistent with the Borland Turbo Vision convention that later influenced Windows 95 UI design.

---

### Borland 3D Theme

**Era:** 1990-1995
**Inspiration:** Borland's Turbo Vision framework (Turbo Pascal 6.0+, Turbo C++ 3.0+, Borland C++ 4.0)

#### Historical Context

Borland's Turbo Vision framework (1990) pioneered the "raised button" look in DOS text-mode applications, achieving a sophisticated 3D rendering system within the constraints of 16-color CGA/EGA/VGA palettes and CP437 extended ASCII. The asymmetric border coloring and drop shadow techniques it introduced later influenced Windows 95 UI design.

#### Color Scheme

| Element | Foreground | Background | Notes |
|---------|-----------|------------|-------|
| Background | Yellow | Blue | Classic Borland desktop (0x1E) |
| Buttons | Cyan | Blue | Menu bar and toolbar |
| Focused | Black | Cyan | Inverted selection (0x30) |
| Text Input | White | Blue | High contrast data entry (0x1F) |
| Borders | White | Blue | Active window frames |
| Selection | Black | Cyan | Bright highlight |
| Disabled | Black | Blue | Dimmed appearance |
| Shadow | Black | Black | + A_BOLD for gray simulation |
| Highlight | White | Cyan | Top-left edges (light reflection) |
| Lowlight | Black | Cyan | Bottom-right edges (shadow) |

#### 3D Configuration

- **Shadow offset:** 2 columns right, 1 row down (adaptive: reduces to 1x1 for terminals >40 lines)
- **Shadow character:** Space (solid) or `'░'` when gradient shadows enabled
- **Border style:** Single-line Unicode by default, with DOUBLE_LINE, ROUNDED, and ASCII fallback options
- **Default rendering style:** RAISED
- **A_BOLD intensity:** Enabled by default to simulate 16-color palette on 8-color terminals

#### Constructor Options

```java
// Default: single-line borders, adaptive shadow, no gradient, bold enabled
Theme theme = new Borland3DTheme();

// Custom: double-line borders, fixed offset, gradient shadows
Theme theme = new Borland3DTheme(
    Borland3DTheme.BorderStyle.DOUBLE_LINE,  // border style
    false,                                     // adaptive shadow offset
    true,                                      // gradient shadows (░▒▓)
    true                                       // bold intensity
);
```

#### Usage

```java
ThemeManager.getInstance().setTheme(new Borland3DTheme());

Dialog dialog = new Dialog("Turbo Pascal 7.0");
dialog.set3DEnabled(true);
dialog.setRenderingStyle(RenderingStyle.RAISED);
```

**When to Use:** Developer tools, IDEs, authentic Turbo Vision recreation.

---

### dBASE IV 3D Theme

**Era:** 1988-1993
**Inspiration:** Ashton-Tate/Borland dBASE IV Control Center

#### Historical Context

dBASE IV (1988) introduced a revolutionary menu-driven interface (the Control Center) with multiple windows, pull-down menus, and mouse support. After Borland acquired Ashton-Tate in 1991, the interface gained more pronounced 3D effects aligned with the Turbo Vision aesthetic. The combination of blue backgrounds, white text, yellow highlights, and gray shadows became the defining look of professional database applications.

#### Color Scheme

| Element | Foreground | Background | Notes |
|---------|-----------|------------|-------|
| Background | White | Blue | Control Center desktop |
| Buttons | Yellow | Blue | Menu bar and options |
| Focused | Blue | Yellow | Inverted selection |
| Text Input | Cyan | Blue | Sunken data entry fields |
| Borders | White | Blue | Single-line window frames |
| Selection | Blue | White | Browse mode highlights |
| Disabled | Blue | Blue | Dimmed options |
| Shadow | Black | Black | + A_BOLD for gray simulation |
| Highlight | White | Cyan | Top-left edges (raised elements) |
| Lowlight | Black | Cyan | Bottom-right edges (recessed elements) |

#### 3D Configuration

- **Shadow offset:** 2 columns right, 1 row down
- **Shadow character:** Space (solid shadows)
- **Border style:** Single-line for windows, double-line (`╔═╗║║╚═╝`) for emphasized/modal dialogs
- **Default rendering style:** RAISED

#### Usage

```java
ThemeManager.getInstance().setTheme(new DBase4_3DTheme());

Dialog dialog = new Dialog("Database Configuration");
dialog.set3DEnabled(true);
dialog.setRenderingStyle(RenderingStyle.RAISED);

// Sunken input field for data entry
TextField nameField = new TextField();
nameField.set3DEnabled(true);
nameField.setRenderingStyle(RenderingStyle.SUNKEN);
```

**When to Use:** Professional database applications, business software, authentic dBASE IV recreation.

**Visual Comparison:** More businesslike than Borland3DTheme (white vs yellow background), reflecting database application heritage vs developer tool aesthetic.

---

### Creating a Custom 3D Theme

Implement the `Theme3D` interface to create a theme with 3D rendering support. You must implement all methods from both `Theme` (7 color pairs + border chars + name) and `Theme3D` (shadow/highlight/lowlight colors + offsets):

```java
package com.example.mythemes;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;
import org.flossware.curses.theme.Theme3D;

public class My3DTheme implements Theme3D {

    // --- Theme interface (base colors) ---

    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.GREEN, Color.BLACK);
    }

    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    @Override
    public String getBorderChars() {
        return "┌─┐│└─┘│";
    }

    @Override
    public String getName() {
        return "My Custom 3D";
    }

    // --- Theme3D interface (3D-specific properties) ---

    @Override
    public ColorPair getShadowColor() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    @Override
    public ColorPair getHighlightColor() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getLowlightColor() {
        return new ColorPair(Color.BLACK, Color.BLUE);
    }

    @Override
    public int getShadowOffsetX() {
        return 2;
    }

    @Override
    public int getShadowOffsetY() {
        return 1;
    }

    // Optional overrides (defaults shown):
    // getDoubleBorderChars() -> "╔═╗║║╚═╝"
    // getShadowChar()        -> ' '
    // useGradientShadow()    -> false
    // getDefaultRenderingStyle() -> RAISED
}
```

Apply your custom 3D theme:

```java
ThemeManager.getInstance().setTheme(new My3DTheme());

// All components with 3D enabled will now use your theme's colors
Button btn = new Button("Save");
btn.set3DEnabled(true);
btn.setRenderingStyle(RenderingStyle.RAISED);
```

### Terminal Compatibility for 3D Themes

3D rendering works across terminals but visual fidelity varies:

| Terminal | Unicode | A_BOLD Behavior | Recommended |
|----------|---------|-----------------|-------------|
| xterm | Full | Brightens foreground | Single-line Unicode borders |
| GNOME Terminal | Full | Brightens foreground | Single-line Unicode borders |
| Konsole | Full | Brightens foreground | Single-line Unicode borders |
| rxvt | Limited | A_BLINK brightens background | ASCII fallback |
| Linux console | CP437 only | Brightens foreground | CP437 numeric codes |

Shadow rendering uses `A_BOLD` on `COLOR_BLACK` to simulate dark gray (color 8) on 8-color terminals. This works reliably on xterm, GNOME Terminal, and Konsole. On terminals where `A_BOLD` does not brighten foreground colors, shadows may appear as solid black rather than gray.

---

## Usage Guide

### Basic Theme Switching

The simplest way to apply a theme is using the `ThemeManager` singleton:

```java
import org.flossware.curses.theme.ThemeManager;

// Switch to a specific theme
ThemeManager.getInstance().useBorlandTheme();

// Or set a custom theme
ThemeManager.getInstance().setTheme(new ModernTheme());
```

### Available Theme Methods

The `ThemeManager` provides convenience methods for all built-in themes:

```java
ThemeManager manager = ThemeManager.getInstance();

// Modern themes
manager.useDefaultTheme();
manager.useDarkTheme();
manager.useLightTheme();
manager.useModernTheme();

// Classic IDE
manager.useBorlandTheme();

// Retro computers
manager.useTI994ATheme();
manager.useTRS80Theme();

// Business software
manager.useDOSTheme();
manager.useDBase3Theme();
manager.useDBase4Theme();
```

### Using Themes in Components

All curses-java components automatically use the current theme from the `ThemeManager`:

```java
// Create a button - it will use the current theme's button colors
Button myButton = new Button("Click Me");

// Create a text input - it will use the current theme's text input colors
TextField textField = new TextField(20);

// When you switch themes, all components update automatically
ThemeManager.getInstance().useModernTheme();
```

### Accessing Theme Properties Directly

You can access individual theme properties for custom rendering:

```java
Theme theme = ThemeManager.getInstance().getCurrentTheme();

// Get color pairs
ColorPair bg = theme.getBackground();
ColorPair button = theme.getButton();
ColorPair focused = theme.getButtonFocused();
ColorPair input = theme.getTextInput();
ColorPair border = theme.getBorder();
ColorPair selection = theme.getSelection();
ColorPair disabled = theme.getDisabled();

// Get border characters
String borderChars = theme.getBorderChars();
// Format: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right
// Example ASCII: "+-+||+-+"
// Example Unicode: "┌─┐│└─┘│"

// Get theme name
String name = theme.getName();
```

### Creating a Custom Theme

Implement the `Theme` interface to create your own custom theme:

```java
package com.example.mythemes;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.flossware.curses.theme.Theme;

public class MyCustomTheme implements Theme {
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.GREEN, Color.BLACK);
    }

    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.YELLOW, Color.BLACK);
    }

    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.YELLOW);
    }

    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.GREEN);
    }

    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    @Override
    public String getBorderChars() {
        return "┌─┐│└─┘│";  // Unicode or "+-+||+-+" for ASCII
    }

    @Override
    public String getName() {
        return "My Custom Theme";
    }
}

// Use your custom theme
ThemeManager.getInstance().setTheme(new MyCustomTheme());
```

### Runtime Theme Selection

Allow users to select themes at runtime:

```java
public void selectTheme(String themeName) {
    ThemeManager manager = ThemeManager.getInstance();
    
    switch (themeName.toLowerCase()) {
        case "default":
            manager.useDefaultTheme();
            break;
        case "dark":
            manager.useDarkTheme();
            break;
        case "light":
            manager.useLightTheme();
            break;
        case "modern":
            manager.useModernTheme();
            break;
        case "borland":
            manager.useBorlandTheme();
            break;
        case "ti-99/4a":
        case "ti994a":
            manager.useTI994ATheme();
            break;
        case "trs-80":
        case "trs80":
            manager.useTRS80Theme();
            break;
        case "dos":
            manager.useDOSTheme();
            break;
        case "dbase3":
        case "dbase iii":
            manager.useDBase3Theme();
            break;
        case "dbase4":
        case "dbase iv":
            manager.useDBase4Theme();
            break;
        default:
            manager.useDefaultTheme();
    }
}
```

### Theme Configuration

You can store theme preferences and restore them:

```java
// Save current theme name
String savedTheme = ThemeManager.getInstance().getCurrentTheme().getName();

// Later, restore the theme
selectTheme(savedTheme);
```

## Contributing

We welcome contributions of new themes that celebrate computing history or provide modern aesthetics. When creating a new theme, please:

### Guidelines for New Themes

1. **Historical Themes**: Research the original system to ensure authentic color schemes
2. **Documentation**: Include historical context, era, and usage recommendations
3. **Border Style**: Choose border characters appropriate to the era (ASCII for vintage, Unicode for modern)
4. **Color Choices**: Ensure sufficient contrast for readability
5. **Naming**: Use clear, recognizable names that reflect the inspiration

### Theme Checklist

- [ ] Implements the `Theme` interface
- [ ] All seven color pairs defined (background, button, buttonFocused, textInput, border, selection, disabled)
- [ ] Border characters specified (8 characters: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right)
- [ ] Meaningful theme name returned by `getName()`
- [ ] Added to `ThemeManager` with convenience method
- [ ] Unit tests created (see existing theme tests for examples)
- [ ] Documentation added to this THEMES.md file
- [ ] Historical context provided for retro themes
- [ ] Usage recommendations included

### Border Character Reference

**ASCII Box Drawing** (for vintage/compatible themes):
```
+-+||+-+
(top-left, top, top-right, left, right, bottom-left, bottom, bottom-right)
```

**Unicode Single Line** (for modern themes):
```
┌─┐│└─┘│
Characters: ┌ ─ ┐ │ └ ─ ┘ │
```

**Unicode Double Line** (for emphasis):
```
╔═╗║╚═╝║
Characters: ╔ ═ ╗ ║ ╚ ═ ╝ ║
```

**Unicode Rounded** (for smooth appearance):
```
╭─╮│╰─╯│
Characters: ╭ ─ ╮ │ ╰ ─ ╯ │
```

### Submitting a New Theme

1. Create the theme class in `src/main/java/org/flossware/curses/theme/`
2. Add convenience method to `ThemeManager`
3. Create unit tests in `src/test/java/org/flossware/curses/theme/`
4. Document the theme in this file with full details
5. Submit a pull request with your changes

### Theme Ideas

Some potential themes for future development:

- **Apple II**: Green or amber monochrome
- **Commodore 64**: Petscii blue background with light blue text
- **VT100**: Classic terminal green phosphor
- **Amiga Workbench**: Blue/orange/white GUI aesthetic
- **Norton Commander**: Cyan and blue dual-pane file manager
- **WordPerfect**: Blue function key bar
- **Lotus 1-2-3**: Green spreadsheet cells
- **GEM Desktop**: Black/white/cyan Atari ST interface
- **Mac System 1**: Black and white vintage Mac
- **OS/2 Workplace Shell**: Gradient blue desktop
- **Solarized**: Popular modern color scheme (light and dark)
- **Dracula**: Popular dark theme
- **Nord**: Cool arctic color palette
- **Gruvbox**: Retro groove color scheme

## Screenshots

Coming soon: Visual examples of each theme in action.

## Version History

- **1.0.0** (2024): Initial release with 10 themes
  - Modern: Default, Dark, Light, Modern
  - Classic IDE: Borland
  - Retro Computers: TI-99/4A, TRS-80
  - Business Software: DOS, dBASE III, dBASE IV

## License

Themes are part of the curses-java library and are distributed under the same license as the main project.

---

For more information about curses-java, visit the main project documentation.

## Cross-Language Theme Sharing

curses-java themes are portable. The Java library exports all 12 themes as JSON files in `/themes/`, and the Python [curses-themes](https://github.com/FlossWare/curses-themes) library can consume them directly.

### How It Works

1. Java is the **source of truth** -- themes are defined as Java classes and exported to JSON via `ThemeLoader`
2. Python's `ThemeManager.load_from_file()` auto-detects the Java JSON format and converts it at load time
3. No manual conversion step is needed

### Using Java Themes in Python

```python
from curses_themes import ThemeManager

# Load a single Java-exported theme
theme = ThemeManager.load_from_file("path/to/dark.json")
theme.apply(stdscr)

# Load all themes from a directory
ThemeManager.load_themes_from_directory("path/to/themes/")
```

### Format Differences Handled Automatically

The Python adapter transparently handles these conversions when loading Java JSON:

| Aspect | Java JSON Format | Python Native Format |
|--------|-----------------|---------------------|
| Colors | ncurses names (`"CYAN"`, `"BLUE"`) | RGB tuples (`(0, 255, 255)`) |
| Border char order | TL, T, TR, L, **BL, B, BR**, R | TL, T, TR, L, **R, BL, B**, BR |
| 3D keys | `shadow_color`, `highlight_color`, `lowlight_color` | `shadow`, `highlight`, `lowlight` |
| 3D offsets | `shadow_offset: { x, y }` | `shadow_offset_x`, `shadow_offset_y` |
| Color structure | `colors` (component pairs only) | `colors` (semantic) + `components` (pairs) |

### API Mapping

| Java | Python | Description |
|------|--------|-------------|
| `ThemeManager.getInstance().useTheme(name)` | `ThemeManager.load(name)` | Load built-in theme by name |
| `ThemeManager.getInstance().getAvailableThemes()` | `ThemeManager.list_themes()` | List available theme names |
| `ThemeManager.getInstance().loadThemeFromJson(path)` | `ThemeManager.load_from_file(path)` | Load theme from JSON file |
| `ThemeManager.getInstance().loadThemesFromDirectory(path)` | `ThemeManager.load_themes_from_directory(path)` | Load all themes in a directory |

### See Also

- [curses-themes (Python)](https://github.com/FlossWare/curses-themes) -- Python curses theme library with Java theme auto-detection
- [Migration Guide](MIGRATION.md) -- detailed Java-to-Python and Python-to-Java theme conversion
- [Theme JSON Schema](../themes/schema.json) -- shared schema for cross-language themes

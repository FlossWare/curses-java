# JCurses Theme Gallery

> 📸 **[View Theme Screenshots](docs/screenshots/README.md)** - Visual examples with ASCII art demonstrations


A comprehensive guide to the visual themes available in the JCurses library. Each theme recreates the distinctive aesthetic of classic computing systems and development environments, bringing authentic retro computing experiences to modern terminal applications.

## Table of Contents

- [Overview](#overview)
- [Quick Reference Table](#quick-reference-table)
- [Modern Themes](#modern-themes)
- [Classic IDE Themes](#classic-ide-themes)
- [Retro Computer Themes](#retro-computer-themes)
- [Business Software Themes](#business-software-themes)
- [Usage Guide](#usage-guide)
- [Contributing](#contributing)

## Overview

JCurses includes 10 carefully crafted themes that span four decades of computing history. Each theme authentically recreates the color schemes, border styles, and visual aesthetics of its inspiration while providing a consistent interface for modern terminal applications.

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


## 3D Themes

### Borland 3D Theme

**Historical Context:** Building on the iconic Borland IDE color scheme, this theme adds authentic 3D visual effects inspired by Borland's Turbo Vision framework (1990-1995). Turbo Vision brought windowed interfaces with raised buttons, sunken input fields, and drop shadows to DOS applications.

**Color Scheme:**
- Background: Yellow on blue (classic Borland desktop)
- Menus: Cyan on blue (menu bar and buttons)
- Focused: Black on cyan (inverted selection)
- Input Fields: White on blue (sunken data entry)
- Selection: Black on cyan (bright highlight)
- Shadow: Black on black with A_BOLD (gray simulation)
- Highlight: White on cyan (raised element top-left edges)
- Lowlight: Black on cyan (recessed element bottom-right edges)

**3D Features:**
- Drop shadows (1 column right, 1 row down)
- Raised buttons and menus
- Sunken input fields
- Single-line borders for dialogs
- Asymmetric border coloring

**Usage:**
```java
ThemeManager.useBorland3DTheme();

Dialog dialog = new Dialog("Turbo Pascal 7.0");
dialog.set3DEnabled(true);
dialog.setRenderingStyle(RenderingStyle.RAISED);
```

**When to Use:** Developer tools, IDEs, authentic Turbo Vision recreation.

---

### dBASE IV 3D Theme

**Historical Context:** dBASE IV (1988-1993) revolutionized database software with the Control Center, a graphical menu system featuring 3D-style UI elements similar to Borland's Turbo Vision framework. After Borland acquired Ashton-Tate in 1991, the interface gained even more pronounced 3D effects.

**Color Scheme:**
- Background: White on blue (Control Center desktop)
- Menus: Yellow on blue (menu bar and buttons)
- Focused: Blue on yellow (inverted selection)
- Input Fields: Cyan on blue (sunken data entry)
- Selection: Blue on white (browse mode highlights)
- Shadow: Black on black with A_BOLD (gray simulation)
- Highlight: White on cyan (raised element top-left edges)
- Lowlight: Black on cyan (recessed element bottom-right edges)

**3D Features:**
- Drop shadows (2 columns right, 1 row down)
- Raised buttons and menus
- Sunken input fields
- Double-line borders for modal dialogs
- Asymmetric border coloring

**Usage:**
```java
ThemeManager.useDBase4_3DTheme();

Dialog dialog = new Dialog("Database Configuration");
dialog.set3DEnabled(true);
dialog.setRenderingStyle(RenderingStyle.RAISED);
```

**When to Use:** Professional database applications, business software, authentic dBASE IV recreation.

**Visual Comparison:** More businesslike than Borland3DTheme (white vs yellow background), reflecting database application heritage vs developer tool aesthetic.

---

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

All JCurses components automatically use the current theme from the `ThemeManager`:

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

Themes are part of the JCurses library and are distributed under the same license as the main project.

---

For more information about JCurses, visit the main project documentation.

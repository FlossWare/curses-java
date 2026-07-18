package org.flossware.curses.theme;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton manager for application theme configuration and switching.
 *
 * <p>ThemeManager provides centralized control over the visual appearance of jcurses
 * components through a collection of predefined themes. Each theme defines color pairs
 * and border characters for all UI elements, allowing applications to switch between
 * different visual styles at runtime.</p>
 *
 * <h2>Available Themes</h2>
 * <ul>
 *   <li><b>Default</b> - Classic terminal appearance with white text on black background,
 *       cyan buttons, and ASCII borders</li>
 *   <li><b>Dark</b> - Modern dark mode with muted colors, blue accents, and Unicode borders</li>
 *   <li><b>Light</b> - High contrast light mode with dark text on white background and
 *       double-line Unicode borders</li>
 *   <li><b>Modern</b> - Contemporary high-contrast theme with yellow focus indicators,
 *       optimized for accessibility (WCAG AAA compliant)</li>
 *   <li><b>Borland</b> - Iconic blue background with yellow text recreating the
 *       Turbo Pascal/C++ IDE experience (late 1980s-early 1990s)</li>
 *   <li><b>Borland 3D</b> - Enhanced Borland theme with sophisticated 3D-style borders
 *       using extended box-drawing characters for depth and dimension</li>
 *   <li><b>TI-99/4A</b> - Texas Instruments home computer aesthetic with distinctive
 *       cyan-on-blue color scheme (1981-1984)</li>
 *   <li><b>TRS-80</b> - Tandy/Radio Shack monochrome display with pure white-on-black
 *       (1980-1983)</li>
 *   <li><b>DOS</b> - Classic MS-DOS interface with yellow menus and cyan input fields
 *       (1981-1995)</li>
 *   <li><b>dBASE III</b> - Ashton-Tate database management system with cyan menus and
 *       green input fields (1984-1985)</li>
 *   <li><b>dBASE IV</b> - Modernized dBASE with blue background, yellow menus, and
 *       windowed interface (1988-1993)</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Get the singleton instance
 * ThemeManager manager = ThemeManager.getInstance();
 *
 * // Switch to a specific theme
 * manager.useBorlandTheme();
 *
 * // Get the current theme for rendering
 * Theme theme = manager.getCurrentTheme();
 * ColorPair buttonColor = theme.getButton();
 *
 * // Set a custom theme
 * manager.setTheme(new CustomTheme());
 *
 * // Switch themes based on user preference
 * String preference = userConfig.getTheme();
 * switch (preference) {
 *     case "dark":
 *         manager.useDarkTheme();
 *         break;
 *     case "light":
 *         manager.useLightTheme();
 *         break;
 *     default:
 *         manager.useDefaultTheme();
 * }
 * }</pre>
 *
 * <h2>Adding Custom Themes</h2>
 * <p>To add a custom theme to your application:</p>
 * <ol>
 *   <li>Create a class that implements the {@link Theme} interface</li>
 *   <li>Implement all required methods to define color pairs and border characters</li>
 *   <li>Apply the theme using {@link #setTheme(Theme)}</li>
 * </ol>
 *
 * <pre>{@code
 * public class CustomTheme implements Theme {
 *     @Override
 *     public ColorPair getBackground() {
 *         return new ColorPair(Color.GREEN, Color.BLACK);
 *     }
 *
 *     @Override
 *     public ColorPair getButton() {
 *         return new ColorPair(Color.YELLOW, Color.BLACK);
 *     }
 *
 *     // Implement remaining methods...
 *
 *     @Override
 *     public String getBorderChars() {
 *         return "╔═╗║╚═╝║";  // Double-line Unicode
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return "Custom";
 *     }
 * }
 *
 * // Apply the custom theme
 * ThemeManager.getInstance().setTheme(new CustomTheme());
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>ThemeManager is thread-safe for reading the current theme. However, theme switching
 * should typically be performed during application initialization or in response to user
 * actions, not concurrently from multiple threads.</p>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class ThemeManager {
    private static final ThemeManager INSTANCE = new ThemeManager();
    private Theme currentTheme = new DefaultTheme();
    private final Map<String, Theme> themes = new LinkedHashMap<>();

    private ThemeManager() {
        registerBuiltInThemes();
    }

    private void registerBuiltInThemes() {
        registerTheme(new DefaultTheme());
        registerTheme(new DarkTheme());
        registerTheme(new LightTheme());
        registerTheme(new ModernTheme());
        registerTheme(new BorlandTheme());
        registerTheme(new Borland3DTheme());
        registerTheme(new TI994ATheme());
        registerTheme(new TRS80Theme());
        registerTheme(new DOSTheme());
        registerTheme(new DBase3Theme());
        registerTheme(new DBase4Theme());
        registerTheme(new DBase4_3DTheme());
    }

    private void registerTheme(Theme theme) {
        themes.put(theme.getName(), theme);
    }

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(Theme theme) {
        if (theme != null) {
            this.currentTheme = theme;
        }
    }

    // Convenience methods for switching themes

    /**
     * Switches to the Default theme.
     *
     * <p>Classic terminal appearance with white text on black background. Features cyan
     * buttons with inverted colors when focused, and green text input fields. Uses ASCII
     * box-drawing characters for maximum compatibility.</p>
     *
     * <p><b>Color characteristics:</b> White/cyan/green on black with simple ASCII borders</p>
     */
    public void useDefaultTheme() {
        setTheme(new DefaultTheme());
    }

    /**
     * Switches to the Dark theme.
     *
     * <p>Modern dark mode aesthetic with muted colors and dark background. Features
     * blue buttons and borders with cyan accents. Uses Unicode box-drawing characters
     * for a smooth, contemporary appearance.</p>
     *
     * <p><b>Color characteristics:</b> Cyan/blue/white on black with Unicode borders (┌─┐│└─┘│)</p>
     */
    public void useDarkTheme() {
        setTheme(new DarkTheme());
    }

    /**
     * Switches to the Light theme.
     *
     * <p>High contrast light mode with dark text on light background. Features blue
     * buttons on white with cyan input field backgrounds. Uses double-line Unicode
     * borders for an elegant appearance.</p>
     *
     * <p><b>Color characteristics:</b> Black/blue on white with double-line Unicode borders (╔═╗║╚═╝║)</p>
     */
    public void useLightTheme() {
        setTheme(new LightTheme());
    }

    /**
     * Switches to the Borland theme.
     *
     * <p>Recreates the iconic blue background with yellow text and cyan highlights
     * that defined the Borland IDE experience (Turbo Pascal, Turbo C++, Borland C++)
     * of the late 1980s and early 1990s. Features rounded Unicode borders for a
     * smooth, polished appearance.</p>
     *
     * <p><b>Historical context:</b> The Borland IDE's distinctive blue interface became
     * synonymous with professional programming tools in the DOS era, influencing IDE
     * design for years to come.</p>
     *
     * <p><b>Color characteristics:</b> Yellow/cyan/white on blue with rounded Unicode borders (╭─╮│╰─╯│)</p>
     */
    public void useBorlandTheme() {
        setTheme(new BorlandTheme());
    }

    /**
     * Switches to the Borland 3D theme.
     *
     * <p>Enhanced version of the classic Borland theme featuring sophisticated 3D-style
     * borders that create visual depth and dimension. Maintains the iconic blue background
     * with yellow text and cyan highlights while adding extended box-drawing characters
     * for a more polished, professional appearance.</p>
     *
     * <p><b>Historical context:</b> Later versions of Borland's IDEs (particularly Turbo
     * Vision-based applications) enhanced the traditional blue interface with shadow effects
     * and 3D-style borders, creating a sense of depth even in text mode. This theme captures
     * that evolution, representing the transition from simple ASCII boxes to more visually
     * sophisticated terminal interfaces of the early 1990s.</p>
     *
     * <p><b>Color characteristics:</b> Yellow/cyan/white on blue with extended 3D Unicode
     * borders featuring multiple line styles for depth</p>
     */
    public void useBorland3DTheme() {
        setTheme(new Borland3DTheme());
    }

    /**
     * Sets the current theme to dBASE IV 3D theme.
     *
     * <p>This theme recreates the sophisticated windowed interface of dBASE IV 
     * (1988-1993) with authentic 3D visual effects including drop shadows, raised 
     * buttons, and sunken input fields. The Control Center's white on blue color 
     * scheme with yellow menus creates a professional database application aesthetic.</p>
     *
     * @see DBase4_3DTheme
     * @see useDBase4Theme()
     */
    public static void useDBase4_3DTheme() {
        getInstance().setTheme(new DBase4_3DTheme());
    }

    /**
     * Switches to the Modern theme.
     *
     * <p>Contemporary high-contrast theme optimized for modern terminal aesthetics
     * and accessibility. Features bright cyan buttons and yellow focus indicators
     * on black background. Meets WCAG AAA standards for text contrast, making it
     * suitable for users with visual impairments.</p>
     *
     * <p><b>Historical context:</b> Inspired by contemporary dark mode interfaces
     * found in modern IDEs and terminal applications, emphasizing clarity and
     * readability in low-light environments.</p>
     *
     * <p><b>Color characteristics:</b> White/cyan/yellow on black with single-line Unicode borders (┌─┐│└─┘│)</p>
     */
    public void useModernTheme() {
        setTheme(new ModernTheme());
    }

    /**
     * Switches to the TI-99/4A theme.
     *
     * <p>Recreates the distinctive cyan-on-blue aesthetic of the Texas Instruments
     * TI-99/4A home computer (1981-1984). The TI-99/4A was notable for being the
     * first 16-bit home computer and featured the TMS9918A video display processor
     * with its characteristic color palette.</p>
     *
     * <p><b>Historical context:</b> The TI-99/4A competed with the Commodore 64,
     * Apple II, and Atari 8-bit computers. Its warm cyan-on-blue color scheme was
     * more inviting than the stark white-on-black of many competitors, particularly
     * in the BASIC programming environment and title screens.</p>
     *
     * <p><b>Color characteristics:</b> Cyan/white on blue with ASCII borders for 1981-era authenticity</p>
     */
    public void useTI994ATheme() {
        setTheme(new TI994ATheme());
    }

    /**
     * Switches to the TRS-80 theme.
     *
     * <p>Recreates the distinctive white-on-black monochrome display of the Tandy/Radio
     * Shack TRS-80 Model III (1980) and Model 4 (1983). These machines featured crisp,
     * high-contrast displays that made them popular for business applications and word
     * processing.</p>
     *
     * <p><b>Historical context:</b> The TRS-80 line was one of the "1977 Trinity" of
     * home computers (along with the Apple II and Commodore PET). Radio Shack's
     * business-focused marketing emphasized the professional appearance of the monochrome
     * display, contrasting it with the "toy-like" color displays of competitors. The crisp
     * white-on-black text was ideal for word processing and spreadsheet applications.</p>
     *
     * <p><b>Color characteristics:</b> Pure monochrome white on black with ASCII borders
     * for early 1980s authenticity</p>
     */
    public void useTRS80Theme() {
        setTheme(new TRS80Theme());
    }

    /**
     * Switches to the DOS theme.
     *
     * <p>Recreates the iconic white-on-black text mode interface of MS-DOS and PC-DOS
     * (1981-1995), the dominant operating system of the PC era. Features yellow buttons
     * for menu items and cyan text input fields, matching common DOS application
     * conventions.</p>
     *
     * <p><b>Historical context:</b> MS-DOS powered the IBM PC and compatibles from 1981
     * through the mid-1990s. Its text-mode interface, typically running in 80×25 character
     * mode with 16 colors, became the de facto standard for PC software. Key DOS-era
     * applications like WordPerfect, Lotus 1-2-3, dBASE, and countless utilities all
     * shared this visual language.</p>
     *
     * <p><b>Color characteristics:</b> White/yellow/cyan on black with ASCII borders
     * (simplified IBM extended ASCII)</p>
     */
    public void useDOSTheme() {
        setTheme(new DOSTheme());
    }

    /**
     * Switches to the dBASE III theme.
     *
     * <p>Recreates the utilitarian interface of dBASE III (1984) and dBASE III Plus (1985),
     * the database management system that dominated the PC database market in the mid-1980s.
     * Features cyan menu highlighting and green data entry fields on black background.</p>
     *
     * <p><b>Historical context:</b> dBASE III revolutionized database management on
     * personal computers, bringing mainframe-style database capabilities to the IBM PC.
     * Its distinctive interface featured a black background with white text for the command
     * line (the "dot prompt"), and cyan text for menus and prompts. By 1985, dBASE III Plus
     * had become the best-selling database software, and its .dbf file format became an
     * industry standard still used today.</p>
     *
     * <p><b>Color characteristics:</b> White/cyan/green on black with ASCII borders
     * for mid-1980s authenticity</p>
     */
    public void useDBase3Theme() {
        setTheme(new DBase3Theme());
    }

    /**
     * Switches to the dBASE IV theme.
     *
     * <p>Recreates the more sophisticated windowed interface of dBASE IV (1988-1993),
     * which introduced a revolutionary menu-driven interface (the Control Center) with
     * multiple windows, pull-down menus, and mouse support. Features white and yellow
     * text on blue background.</p>
     *
     * <p><b>Historical context:</b> dBASE IV was released in 1988 with a graphical menu
     * system that replaced the traditional dot prompt as the default interface. The new
     * interface used a blue background with white and yellow text, creating a more modern
     * and accessible appearance. Despite initial bugs, it was eventually acquired by
     * Borland in 1991, and its windowed interface influenced database tools throughout
     * the 1990s. The shift from black to blue backgrounds was part of a broader trend
     * in late-1980s software design.</p>
     *
     * <p><b>Color characteristics:</b> White/yellow/cyan on blue with ASCII borders</p>
     */
    public void useDBase4Theme() {
        setTheme(new DBase4Theme());
    }

    // ========================================================================
    // JSON theme loading and theme registry
    // ========================================================================

    /**
     * Loads a theme from a JSON file and sets it as the current theme.
     *
     * <p>The loaded theme is also registered in the theme registry, making it
     * available through {@link #useTheme(String)} and {@link #getAvailableThemes()}.</p>
     *
     * @param path the path to the JSON theme file
     * @throws ThemeLoader.ThemeLoadException if the file cannot be read or parsed
     */
    public void loadThemeFromJson(Path path) {
        Theme theme = ThemeLoader.fromJson(path);
        registerTheme(theme);
        setTheme(theme);
    }

    /**
     * Scans a directory for JSON theme files and registers all found themes.
     *
     * <p>Files named {@code schema.json} are skipped. Only files with a {@code .json}
     * extension are processed. Themes that fail to parse are silently skipped.</p>
     *
     * @param directory the directory to scan for JSON theme files
     * @return the number of themes successfully loaded
     * @throws ThemeLoader.ThemeLoadException if the directory cannot be read
     */
    public int loadThemesFromDirectory(Path directory) {
        int loaded = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json")) {
            for (Path file : stream) {
                String filename = file.getFileName().toString();
                if ("schema.json".equals(filename)) {
                    continue;
                }
                try {
                    Theme theme = ThemeLoader.fromJson(file);
                    registerTheme(theme);
                    loaded++;
                } catch (ThemeLoader.ThemeLoadException e) {
                    // Skip files that fail to parse
                }
            }
        } catch (IOException e) {
            throw new ThemeLoader.ThemeLoadException(
                    "Failed to read theme directory: " + directory, e);
        }
        return loaded;
    }

    /**
     * Returns an unmodifiable map of all registered themes keyed by name.
     *
     * @return map of theme name to Theme instance
     */
    public Map<String, Theme> getAvailableThemes() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(themes));
    }

    /**
     * Switches to a registered theme by name.
     *
     * @param name the theme name (case-sensitive)
     * @return {@code true} if the theme was found and activated, {@code false} otherwise
     */
    public boolean useTheme(String name) {
        Theme theme = themes.get(name);
        if (theme != null) {
            setTheme(theme);
            return true;
        }
        return false;
    }
}

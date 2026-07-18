package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Classic MS-DOS and PC-DOS theme.
 *
 * Recreates the iconic white-on-black text mode interface of MS-DOS and PC-DOS
 * (1981-1995), the dominant operating system of the PC era. This theme captures
 * the utilitarian aesthetic of command-line computing that defined an entire
 * generation of personal computer use.
 *
 * <h2>Historical Context</h2>
 * <p>MS-DOS powered the IBM PC and compatibles from 1981 through the mid-1990s.
 * Its text-mode interface, typically running in 80×25 character mode with 16 colors,
 * became the de facto standard for PC software. The default color scheme was simple:
 * white (or light gray) text on a black background, with occasional use of bright
 * colors for emphasis.</p>
 *
 * <p>Key DOS-era applications like WordPerfect, Lotus 1-2-3, dBASE, and countless
 * utilities all shared this visual language. Even early versions of Windows (1.x-3.x)
 * were launched from this interface.</p>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> White on black - the standard DOS text mode palette</li>
 *   <li><b>Buttons:</b> Yellow on black - bright color for interactive elements (common in DOS menus)</li>
 *   <li><b>Focused Elements:</b> Black on yellow - inverted for clear visibility</li>
 *   <li><b>Text Input:</b> Cyan on black - distinguishes input fields (common DOS convention)</li>
 *   <li><b>Borders:</b> White on black - simple box-drawing characters</li>
 *   <li><b>Selection:</b> Black on white - high-contrast inverted selection</li>
 *   <li><b>Disabled:</b> Black on black - hidden elements (DOS convention)</li>
 * </ul>
 *
 * <h2>Visual Identity</h2>
 * <p>This theme distinguishes itself from other monochrome themes through:</p>
 * <ul>
 *   <li>Strategic use of yellow for interactive elements (matching DOS menu conventions)</li>
 *   <li>Cyan text input fields (a common DOS application pattern)</li>
 *   <li>ASCII box-drawing characters (IBM extended ASCII, code page 437)</li>
 *   <li>High-contrast color choices optimized for CGA/EGA/VGA displays</li>
 * </ul>
 *
 * <h2>Technical Notes</h2>
 * <p>DOS text mode used the IBM PC's 16-color palette derived from CGA (1981).
 * The 8 base colors could be displayed in normal or bright (high-intensity) variants.
 * This theme maps to the standard 8-color ncurses palette while maintaining the
 * DOS aesthetic through careful color selection.</p>
 *
 * <p>The ASCII box-drawing characters used here (+-+||+-+) are simplified versions
 * of the IBM extended ASCII characters (═║╔╗╚╝) that were standard in DOS applications.
 * For maximum compatibility, we use the simpler ASCII set.</p>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class DOSTheme implements Theme {

    /**
     * Returns the background color scheme.
     * White text on black background - the standard DOS palette.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the button color scheme.
     * Yellow text on black background - bright color for menu items and buttons,
     * a common DOS convention for highlighting interactive elements.
     *
     * @return ColorPair with yellow foreground and black background
     */
    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.YELLOW, Color.BLACK);
    }

    /**
     * Returns the focused button color scheme.
     * Black text on yellow background - inverted colors for clear focus indication,
     * matching the DOS convention for selected menu items.
     *
     * @return ColorPair with black foreground and yellow background
     */
    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.YELLOW);
    }

    /**
     * Returns the text input color scheme.
     * Cyan text on black background - distinguishes input fields from regular text,
     * a common pattern in DOS applications for form fields and user input areas.
     *
     * @return ColorPair with cyan foreground and black background
     */
    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    /**
     * Returns the border color scheme.
     * White borders on black background for clean visual separation.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the selection color scheme.
     * Black text on white background for maximum contrast selection,
     * matching DOS's inverted selection highlighting.
     *
     * @return ColorPair with black foreground and white background
     */
    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    /**
     * Returns the disabled element color scheme.
     * Black on black - invisible disabled items, matching DOS behavior where
     * unavailable menu items were typically hidden rather than grayed out.
     *
     * @return ColorPair with black foreground and black background
     */
    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    /**
     * Returns the border characters for drawing component boundaries.
     * Uses ASCII characters compatible with all terminals. In authentic DOS,
     * these would be the IBM extended ASCII box-drawing characters from
     * code page 437.
     *
     * @return String containing ASCII box-drawing characters
     */
    @Override
    public String getBorderChars() {
        // ASCII borders for universal compatibility
        // (DOS would use extended ASCII: ═║╔╗╚╝)
        return "+-+|+-+|";
    }

    /**
     * Returns the name of this theme.
     *
     * @return "DOS"
     */
    @Override
    public String getName() {
        return "DOS";
    }
}

package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Ashton-Tate/Borland dBASE IV theme.
 *
 * Recreates the more sophisticated windowed interface of dBASE IV (1988-1993),
 * which introduced a revolutionary menu-driven interface with multiple windows,
 * pull-down menus, and mouse support. This was a significant departure from
 * dBASE III's command-line focus.
 *
 * <h2>Historical Context</h2>
 * <p>dBASE IV was released in 1988 with great fanfare, introducing a graphical
 * menu system (the Control Center) that replaced the traditional dot prompt as
 * the default interface. The new interface used a blue background with white and
 * yellow text, creating a more modern and accessible appearance.</p>
 *
 * <p>Despite initial bugs that hurt its reputation, dBASE IV eventually became
 * stable and was acquired by Borland in 1991. Its windowed interface and menu
 * system influenced database tools throughout the 1990s. The Control Center's
 * blue-and-white color scheme became iconic.</p>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> White on blue - the Control Center's main interface</li>
 *   <li><b>Buttons/Menus:</b> Yellow on blue - menu bar and highlighted options</li>
 *   <li><b>Focused Elements:</b> Blue on yellow - inverted menu selection</li>
 *   <li><b>Text Input:</b> Cyan on blue - data entry fields in forms</li>
 *   <li><b>Borders:</b> White on blue - window frames and separators</li>
 *   <li><b>Selection:</b> Blue on white - highlighted records in browse mode</li>
 *   <li><b>Disabled:</b> Blue on blue - dimmed unavailable options</li>
 * </ul>
 *
 * <h2>Visual Identity</h2>
 * <p>This theme captures dBASE IV's modernized aesthetic through:</p>
 * <ul>
 *   <li>Blue background (versus dBASE III's black) for a softer, more professional look</li>
 *   <li>Yellow menu highlighting (a departure from cyan) for better visibility</li>
 *   <li>White borders and text matching the Control Center windows</li>
 *   <li>Cyan input fields distinguishing data entry from display text</li>
 * </ul>
 *
 * <h2>Technical Notes</h2>
 * <p>dBASE IV required EGA or better graphics (VGA recommended) and took advantage
 * of the expanded color palette. The Control Center interface ran in 80×25 or
 * 80×43/50 text modes, using box-drawing characters for window frames and menus.</p>
 *
 * <p>The shift from black to blue backgrounds was part of a broader trend in
 * late-1980s software design, also seen in Lotus 1-2-3 Release 3 and other
 * applications moving toward GUI-inspired interfaces.</p>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class DBase4Theme implements Theme {

    /**
     * Returns the background color scheme.
     * White text on blue background - the Control Center's main interface.
     *
     * @return ColorPair with white foreground and blue background
     */
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    /**
     * Returns the button color scheme.
     * Yellow text on blue background - menu bar and menu items.
     *
     * @return ColorPair with yellow foreground and blue background
     */
    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.YELLOW, Color.BLUE);
    }

    /**
     * Returns the focused button color scheme.
     * Blue text on yellow background - inverted menu selection.
     *
     * @return ColorPair with blue foreground and yellow background
     */
    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLUE, Color.YELLOW);
    }

    /**
     * Returns the text input color scheme.
     * Cyan text on blue background - data entry fields in forms and dialogs.
     *
     * @return ColorPair with cyan foreground and blue background
     */
    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.CYAN, Color.BLUE);
    }

    /**
     * Returns the border color scheme.
     * White borders on blue background - window frames and separators.
     *
     * @return ColorPair with white foreground and blue background
     */
    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    /**
     * Returns the selection color scheme.
     * Blue text on white background - highlighted records in browse mode.
     *
     * @return ColorPair with blue foreground and white background
     */
    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLUE, Color.WHITE);
    }

    /**
     * Returns the disabled element color scheme.
     * Blue on blue - dimmed unavailable menu items.
     *
     * @return ColorPair with blue foreground and blue background
     */
    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLUE, Color.BLUE);
    }

    /**
     * Returns the border characters for drawing component boundaries.
     * Uses ASCII characters for maximum compatibility.
     *
     * @return String containing ASCII box-drawing characters
     */
    @Override
    public String getBorderChars() {
        // ASCII borders (dBASE IV would use extended ASCII for double-line boxes)
        return "+-+||+-+";
    }

    /**
     * Returns the name of this theme.
     *
     * @return "dBASE IV"
     */
    @Override
    public String getName() {
        return "dBASE IV";
    }
}

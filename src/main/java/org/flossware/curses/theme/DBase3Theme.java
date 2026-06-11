package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Ashton-Tate dBASE III and dBASE III Plus theme.
 *
 * Recreates the utilitarian interface of dBASE III (1984) and dBASE III Plus (1985),
 * the database management system that dominated the PC database market in the mid-1980s.
 * dBASE III's command-line "dot prompt" and simple menu system became the standard
 * interface pattern for business database applications.
 *
 * <h2>Historical Context</h2>
 * <p>dBASE III revolutionized database management on personal computers, bringing
 * mainframe-style database capabilities to the IBM PC. Its distinctive interface
 * featured a black background with white text for the command line, and cyan text
 * for menus and prompts. The program's ".dbf" file format became an industry standard,
 * still used today.</p>
 *
 * <p>By 1985, dBASE III Plus had become the best-selling database software, powering
 * thousands of custom business applications. Its programming language (xBase) spawned
 * numerous clones including Clipper, FoxPro, and others.</p>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> White on black - the classic dot prompt interface</li>
 *   <li><b>Buttons/Menus:</b> Cyan on black - distinctive menu highlighting</li>
 *   <li><b>Focused Elements:</b> Black on cyan - inverted selection</li>
 *   <li><b>Text Input:</b> Green on black - data entry fields (common in dBASE apps)</li>
 *   <li><b>Borders:</b> White on black - simple box drawing</li>
 *   <li><b>Selection:</b> Black on cyan - highlighted database records</li>
 *   <li><b>Disabled:</b> Black on black - hidden unavailable options</li>
 * </ul>
 *
 * <h2>Visual Identity</h2>
 * <p>This theme captures dBASE III's distinctive aesthetic through:</p>
 * <ul>
 *   <li>Cyan highlighting for menus and interactive elements (dBASE's signature color)</li>
 *   <li>Green text for data entry fields (common in custom dBASE applications)</li>
 *   <li>White command-line text on black background (the iconic dot prompt)</li>
 *   <li>ASCII borders matching dBASE's simple box-drawing style</li>
 * </ul>
 *
 * <h2>Technical Notes</h2>
 * <p>dBASE III ran in DOS text mode (80×25) and used the standard CGA/EGA color
 * palette. The cyan-on-black color scheme was chosen for its high readability on
 * composite monitors and became synonymous with database applications of the era.</p>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class DBase3Theme implements Theme {

    /**
     * Returns the background color scheme.
     * White text on black background - the classic dBASE dot prompt.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the button color scheme.
     * Cyan text on black background - dBASE's distinctive menu color.
     *
     * @return ColorPair with cyan foreground and black background
     */
    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    /**
     * Returns the focused button color scheme.
     * Black text on cyan background - inverted selection highlighting.
     *
     * @return ColorPair with black foreground and cyan background
     */
    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    /**
     * Returns the text input color scheme.
     * Green text on black background - distinguishes data entry fields,
     * a common pattern in custom dBASE III applications.
     *
     * @return ColorPair with green foreground and black background
     */
    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.GREEN, Color.BLACK);
    }

    /**
     * Returns the border color scheme.
     * White borders on black background for simple visual separation.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the selection color scheme.
     * Black text on cyan background - highlighted database records and menu items.
     *
     * @return ColorPair with black foreground and cyan background
     */
    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    /**
     * Returns the disabled element color scheme.
     * Black on black - invisible disabled menu items.
     *
     * @return ColorPair with black foreground and black background
     */
    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    /**
     * Returns the border characters for drawing component boundaries.
     * Uses ASCII characters matching dBASE III's simple box-drawing style.
     *
     * @return String containing ASCII box-drawing characters
     */
    @Override
    public String getBorderChars() {
        // ASCII borders for mid-1980s authenticity
        return "+-+||+-+";
    }

    /**
     * Returns the name of this theme.
     *
     * @return "dBASE III"
     */
    @Override
    public String getName() {
        return "dBASE III";
    }
}

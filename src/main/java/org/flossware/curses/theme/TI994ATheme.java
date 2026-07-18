package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Texas Instruments TI-99/4A home computer theme.
 *
 * Recreates the distinctive cyan-on-blue aesthetic of the TI-99/4A (1981-1984),
 * Texas Instruments' entry into the home computer market. The TI-99/4A was notable
 * for being the first 16-bit home computer and featured the TMS9918A video display
 * processor with its characteristic color palette.
 *
 * <h2>Historical Context</h2>
 * <p>The TI-99/4A competed with the Commodore 64, Apple II, and Atari 8-bit computers.
 * Its distinctive cyan text on medium blue background became iconic, particularly in
 * the BASIC programming environment and title screens. The cyan-on-blue color scheme
 * was warmer and more inviting than the stark white-on-black of many competitors.</p>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> Cyan on blue - the signature TI-99/4A screen appearance</li>
 *   <li><b>Buttons:</b> White on blue - enhanced visibility for interactive elements</li>
 *   <li><b>Focused Elements:</b> Blue on cyan - inverted for clear focus indication</li>
 *   <li><b>Borders:</b> Cyan on blue - consistent with the primary aesthetic</li>
 *   <li><b>Selection:</b> Blue on white - high contrast selection</li>
 *   <li><b>Disabled:</b> Blue on blue - muted appearance</li>
 * </ul>
 *
 * <h2>Visual Identity</h2>
 * <p>While this theme shares cyan-on-blue with other retro themes (C64, Commander),
 * it distinguishes itself through:</p>
 * <ul>
 *   <li>Primary use of cyan for all text elements (matching TI-99/4A's consistent palette)</li>
 *   <li>White accents for buttons (reflecting the TI's menu systems)</li>
 *   <li>Blue-on-white selection (high contrast for the era's CRT displays)</li>
 *   <li>ASCII borders for period authenticity (the TI-99/4A predated widespread Unicode)</li>
 * </ul>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class TI994ATheme implements Theme {

    /**
     * Returns the background color scheme.
     * Cyan text on blue background - the signature TI-99/4A appearance.
     *
     * @return ColorPair with cyan foreground and blue background
     */
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.CYAN, Color.BLUE);
    }

    /**
     * Returns the button color scheme.
     * White text on blue background for enhanced visibility.
     *
     * @return ColorPair with white foreground and blue background
     */
    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    /**
     * Returns the focused button color scheme.
     * Blue text on cyan background - inverted color scheme for clear focus.
     *
     * @return ColorPair with blue foreground and cyan background
     */
    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLUE, Color.CYAN);
    }

    /**
     * Returns the text input color scheme.
     * Cyan text on blue background, matching the overall TI-99/4A aesthetic.
     *
     * @return ColorPair with cyan foreground and blue background
     */
    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.CYAN, Color.BLUE);
    }

    /**
     * Returns the border color scheme.
     * Cyan borders on blue background for visual consistency.
     *
     * @return ColorPair with cyan foreground and blue background
     */
    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.CYAN, Color.BLUE);
    }

    /**
     * Returns the selection color scheme.
     * Blue text on white background for maximum contrast selection indication.
     *
     * @return ColorPair with blue foreground and white background
     */
    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLUE, Color.WHITE);
    }

    /**
     * Returns the disabled element color scheme.
     * Blue on blue for a muted, barely-visible disabled appearance.
     *
     * @return ColorPair with blue foreground and blue background
     */
    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLUE, Color.BLUE);
    }

    /**
     * Returns the border characters for drawing component boundaries.
     * Uses ASCII characters for period authenticity - the TI-99/4A era predated
     * widespread Unicode adoption.
     *
     * @return String containing ASCII box-drawing characters
     */
    @Override
    public String getBorderChars() {
        // ASCII borders for 1981-era authenticity
        return "+-+|+-+|";
    }

    /**
     * Returns the name of this theme.
     *
     * @return "TI-99/4A"
     */
    @Override
    public String getName() {
        return "TI-99/4A";
    }
}

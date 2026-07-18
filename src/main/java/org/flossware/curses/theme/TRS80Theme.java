package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Tandy/Radio Shack TRS-80 Model III and Model 4 theme.
 *
 * Recreates the distinctive white-on-black monochrome display of the TRS-80 Model III
 * (1980) and Model 4 (1983). These machines featured crisp, high-contrast displays that
 * made them popular for business applications and word processing.
 *
 * <h2>Historical Context</h2>
 * <p>The TRS-80 line was one of the "1977 Trinity" of home computers (along with the
 * Apple II and Commodore PET). The Model III improved upon the original Model I with
 * an integrated design and cleaner display. The Model 4 added backward compatibility
 * and improved graphics capabilities. Both used monochrome displays with exceptional
 * clarity - white (or green, depending on the monitor) phosphor on black.</p>
 *
 * <p>Radio Shack's business-focused marketing emphasized the professional appearance
 * of the monochrome display, contrasting it with the "toy-like" color displays of
 * competitors. The crisp white-on-black text was ideal for word processing and
 * spreadsheet applications.</p>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> White on black - classic monochrome terminal aesthetic</li>
 *   <li><b>Buttons:</b> White on black - consistent monochrome appearance</li>
 *   <li><b>Focused Elements:</b> Black on white - high-contrast inversion for focus</li>
 *   <li><b>Borders:</b> White on black - crisp boundary definition</li>
 *   <li><b>Selection:</b> Black on white - maximum contrast selection</li>
 *   <li><b>Disabled:</b> Black on black - completely hidden (period-accurate behavior)</li>
 * </ul>
 *
 * <h2>Visual Identity</h2>
 * <p>While this theme uses the same white-on-black as the Default theme, it distinguishes
 * itself through its historical authenticity and focus on maximum readability:</p>
 * <ul>
 *   <li>Pure monochrome palette (white/black only, no color accents)</li>
 *   <li>Block-style ASCII borders reflecting the TRS-80's character-cell display</li>
 *   <li>Sharp contrast ratios optimized for the P4 white phosphor CRT monitors</li>
 *   <li>Minimalist aesthetic matching Radio Shack's business-oriented design philosophy</li>
 * </ul>
 *
 * <h2>Technical Notes</h2>
 * <p>The TRS-80 Model III and 4 displayed 64×16 characters (expandable to 80×24 on
 * Model 4). The monochrome display was praised for its clarity and lack of color fringing,
 * making it superior for text-heavy applications compared to composite color monitors
 * of the era.</p>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class TRS80Theme implements Theme {

    /**
     * Returns the background color scheme.
     * White text on black background - the classic TRS-80 monochrome display.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the button color scheme.
     * White text on black background for consistent monochrome appearance.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the focused button color scheme.
     * Black text on white background - high-contrast inversion.
     *
     * @return ColorPair with black foreground and white background
     */
    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    /**
     * Returns the text input color scheme.
     * White text on black background, maintaining the monochrome aesthetic.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the border color scheme.
     * White borders on black background for crisp visual boundaries.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the selection color scheme.
     * Black text on white background for maximum contrast.
     *
     * @return ColorPair with black foreground and white background
     */
    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    /**
     * Returns the disabled element color scheme.
     * Black on black - completely invisible, matching the TRS-80's behavior
     * where disabled menu items were simply not displayed.
     *
     * @return ColorPair with black foreground and black background
     */
    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    /**
     * Returns the border characters for drawing component boundaries.
     * Uses ASCII block-style characters appropriate for the TRS-80's
     * character-cell display (1980-1983 era).
     *
     * @return String containing ASCII box-drawing characters
     */
    @Override
    public String getBorderChars() {
        // ASCII borders for early 1980s authenticity
        return "+-+|+-+|";
    }

    /**
     * Returns the name of this theme.
     *
     * @return "TRS-80"
     */
    @Override
    public String getName() {
        return "TRS-80";
    }
}

package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Modern theme with high-contrast colors optimized for contemporary terminal aesthetics.
 *
 * <p>This theme embodies a sleek, modern design philosophy with the following characteristics:</p>
 *
 * <h2>Design Philosophy</h2>
 * <p>The Modern theme emphasizes clarity, readability, and visual hierarchy through strategic
 * use of high-contrast color combinations. It draws inspiration from contemporary dark mode
 * interfaces found in modern IDEs and terminal applications.</p>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> Black background with white text - provides maximum readability
 *       and reduces eye strain in low-light environments</li>
 *   <li><b>Buttons:</b> Bright cyan on black - offers vibrant, attention-grabbing interactive
 *       elements that stand out clearly against the background</li>
 *   <li><b>Focused Elements:</b> Yellow on black - delivers exceptional high-contrast focus
 *       indication, making keyboard navigation intuitive and accessible</li>
 *   <li><b>Borders:</b> White on black - creates clean, crisp boundaries that define
 *       component structure without visual clutter</li>
 *   <li><b>Selection:</b> Black on yellow - reverses the focus colors to create a bold,
 *       unmistakable selection indicator</li>
 *   <li><b>Disabled:</b> Dark gray effect - subtly communicates unavailable functionality
 *       while maintaining visual consistency</li>
 * </ul>
 *
 * <h2>Border Style</h2>
 * <p>Utilizes single-line Unicode box-drawing characters (┌─┐│└─┘│) for a clean,
 * professional appearance that works well in modern terminal emulators with Unicode support.</p>
 *
 * <h2>Use Cases</h2>
 * <p>This theme is ideal for:</p>
 * <ul>
 *   <li>Terminal applications requiring high readability in dark environments</li>
 *   <li>Developer tools and command-line interfaces</li>
 *   <li>Applications where clear focus indication is critical for accessibility</li>
 *   <li>Modern terminal emulators with full Unicode support</li>
 * </ul>
 *
 * <h2>Accessibility Considerations</h2>
 * <p>The high-contrast color combinations (white on black, yellow on black, black on yellow)
 * meet WCAG AAA standards for text contrast, making this theme suitable for users with
 * visual impairments or those working in varying lighting conditions.</p>
 *
 * @see Theme
 * @see ColorPair
 * @see Color
 */
public class ModernTheme implements Theme {

    /**
     * Returns the background color scheme.
     * Uses white text on black background for maximum readability and modern aesthetics.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the button color scheme.
     * Uses bright cyan on black to create vibrant, eye-catching interactive elements.
     *
     * @return ColorPair with cyan foreground and black background
     */
    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    /**
     * Returns the focused button color scheme.
     * Uses yellow on black for exceptional high-contrast focus indication.
     *
     * @return ColorPair with yellow foreground and black background
     */
    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.YELLOW, Color.BLACK);
    }

    /**
     * Returns the text input color scheme.
     * Uses white text on black background for consistency with the general background.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the border color scheme.
     * Uses white on black to create clean, crisp component boundaries.
     *
     * @return ColorPair with white foreground and black background
     */
    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    /**
     * Returns the selection color scheme.
     * Uses black on yellow for bold, unmistakable selection indication.
     * This inverts the focus colors to create a distinct visual state.
     *
     * @return ColorPair with black foreground and yellow background
     */
    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.YELLOW);
    }

    /**
     * Returns the disabled element color scheme.
     * Uses dark gray effect to subtly indicate unavailable functionality.
     *
     * @return ColorPair with black foreground and black background (dark gray effect)
     */
    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    /**
     * Returns the Unicode border characters for drawing component boundaries.
     * Uses single-line box-drawing characters for a clean, professional appearance.
     *
     * <p>Character sequence: ┌─┐│└─┘│</p>
     * <ul>
     *   <li>┌ - top-left corner</li>
     *   <li>─ - horizontal line</li>
     *   <li>┐ - top-right corner</li>
     *   <li>│ - vertical line</li>
     *   <li>└ - bottom-left corner</li>
     *   <li>─ - horizontal line</li>
     *   <li>┘ - bottom-right corner</li>
     *   <li>│ - vertical line</li>
     * </ul>
     *
     * @return String containing Unicode box-drawing characters
     */
    @Override
    public String getBorderChars() {
        return "┌─┐│└─┘│";
    }

    /**
     * Returns the name of this theme.
     *
     * @return "Modern"
     */
    @Override
    public String getName() {
        return "Modern";
    }
}

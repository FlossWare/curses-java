package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;

/**
 * Ashton-Tate/Borland dBASE IV theme with 3D rendering effects.
 *
 * <p>Recreates the sophisticated windowed interface of dBASE IV (1988-1993) with
 * authentic 3D visual effects including drop shadows, raised buttons, and sunken
 * input fields that were characteristic of the Control Center interface.</p>
 *
 * <h2>Historical Context</h2>
 * <p>dBASE IV was released in 1988 with a revolutionary graphical menu system
 * (the Control Center) that featured 3D-style UI elements similar to those
 * popularized by Borland's Turbo Vision framework. The interface used subtle
 * shading and shadows to create depth, making menus and dialogs appear to float
 * above the blue desktop background.</p>
 *
 * <p>After Borland acquired Ashton-Tate in 1991, dBASE IV's interface was
 * enhanced with even more pronounced 3D effects, bringing it closer to the
 * Turbo Pascal/C++ IDE aesthetic. The Control Center's combination of blue
 * backgrounds, white text, yellow highlights, and subtle gray shadows became
 * the defining look of professional database applications in the early 1990s.</p>
 *
 * <h2>3D Visual Design</h2>
 * <p>The 3D rendering in dBASE IV emphasized:</p>
 * <ul>
 *   <li><b>Raised Buttons:</b> Menu items and toolbar buttons appeared elevated
 *       with bright highlights on top-left edges and dark shadows on bottom-right</li>
 *   <li><b>Sunken Fields:</b> Input fields and browse windows were recessed,
 *       creating the illusion they were "carved into" the interface</li>
 *   <li><b>Drop Shadows:</b> Dialog boxes and popup menus cast L-shaped shadows
 *       on the blue background, enhancing the sense of layering</li>
 *   <li><b>Subtle Shading:</b> Gray shadows simulated with dithering on 16-color
 *       VGA systems, giving depth without requiring higher color modes</li>
 * </ul>
 *
 * <h2>Color Scheme</h2>
 * <ul>
 *   <li><b>Background:</b> White on blue - the Control Center desktop</li>
 *   <li><b>Buttons:</b> Yellow on blue - menu bar and raised buttons</li>
 *   <li><b>Focused Elements:</b> Blue on yellow - inverted selection</li>
 *   <li><b>Text Input:</b> Cyan on blue - sunken data entry fields</li>
 *   <li><b>Borders:</b> White on blue - double-line window frames</li>
 *   <li><b>Selection:</b> Blue on white - highlighted browse records</li>
 *   <li><b>Disabled:</b> Blue on blue - dimmed unavailable options</li>
 *   <li><b>Shadow:</b> Black on black (with A_BOLD) - simulates gray drop shadows</li>
 *   <li><b>Highlight:</b> White on cyan - bright top-left edges of raised elements</li>
 *   <li><b>Lowlight:</b> Black on cyan - dark bottom-right edges</li>
 * </ul>
 *
 * <h2>Technical Implementation</h2>
 * <p>This theme implements the {@link Theme3D} interface to provide:</p>
 * <ul>
 *   <li><b>Multi-pass Rendering:</b> Shadow → Border → Content rendering order
 *       ensures shadows appear behind UI elements</li>
 *   <li><b>Asymmetric Border Coloring:</b> RAISED style uses white (highlight)
 *       for top-left borders and black (lowlight) for bottom-right, creating
 *       the classic embossed look</li>
 *   <li><b>L-Shaped Drop Shadows:</b> Shadows offset 2 columns right and 1 row
 *       down, matching the original dBASE IV shadow positioning</li>
 *   <li><b>Double-Line Borders:</b> Dialog boxes use double-line characters
 *       (╔═╗║╚═╝║) for emphasis, matching dBASE IV's window chrome</li>
 *   <li><b>Gray Shadow Simulation:</b> On 8-color terminals, BLACK on BLACK with
 *       A_BOLD attribute approximates the gray shadows used by dBASE IV on VGA</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Enable dBASE IV 3D theme
 * ThemeManager.setTheme(new DBase4_3DTheme());
 *
 * // Create a dialog with 3D effects
 * Dialog dialog = new Dialog("Database Configuration");
 * dialog.set3DEnabled(true);              // Enable drop shadow
 * dialog.setRenderingStyle(RenderingStyle.RAISED);  // Raised appearance
 *
 * // Create a sunken input field (typical for data entry)
 * TextField nameField = new TextField();
 * nameField.set3DEnabled(true);
 * nameField.setRenderingStyle(RenderingStyle.SUNKEN);  // Recessed appearance
 *
 * // Buttons appear raised by default
 * Button saveButton = new Button("Save");
 * saveButton.set3DEnabled(true);  // Inherits RAISED from theme default
 * }</pre>
 *
 * <h2>Visual Comparison</h2>
 * <p>This theme differs from {@link Borland3DTheme} in subtle but important ways:</p>
 * <ul>
 *   <li><b>Background Color:</b> White on blue (vs. yellow on blue for Borland)</li>
 *   <li><b>Menu Colors:</b> Yellow on blue (vs. cyan on blue for Borland)</li>
 *   <li><b>Selection Style:</b> Blue on white (vs. black on cyan for Borland)</li>
 *   <li><b>Overall Tone:</b> More businesslike and professional, reflecting
 *       dBASE IV's database application heritage vs. Turbo Vision's developer
 *       tool aesthetic</li>
 * </ul>
 *
 * <h2>Historical Authenticity</h2>
 * <p>This theme recreates the exact visual appearance of dBASE IV 1.5 (1991-1992)
 * running on VGA hardware in 80×25 text mode. The shadow offset (2 right, 1 down)
 * matches the original implementation, and the use of double-line borders for
 * dialogs reflects the distinction dBASE IV made between regular windows and
 * modal dialogs.</p>
 *
 * @see Theme3D
 * @see Borland3DTheme
 * @see DBase4Theme
 * @see RenderingStyle
 * @since 1.0
 */
public class DBase4_3DTheme implements Theme3D {

    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.YELLOW, Color.BLUE);
    }

    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLUE, Color.YELLOW);
    }

    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.CYAN, Color.BLUE);
    }

    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLUE, Color.WHITE);
    }

    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLUE, Color.BLUE);
    }

    @Override
    public String getBorderChars() {
        return "┌─┐│└─┘│";
    }

    @Override
    public String getDoubleBorderChars() {
        return "╔═╗║╚═╝║";
    }

    @Override
    public ColorPair getShadowColor() {
        return new ColorPair(Color.BLACK, Color.BLACK);
    }

    @Override
    public ColorPair getHighlightColor() {
        return new ColorPair(Color.WHITE, Color.CYAN);
    }

    @Override
    public ColorPair getLowlightColor() {
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    @Override
    public int getShadowOffsetX() {
        return 2;
    }

    @Override
    public int getShadowOffsetY() {
        return 1;
    }

    @Override
    public RenderingStyle getDefaultRenderingStyle() {
        return RenderingStyle.RAISED;
    }

    @Override
    public String getName() {
        return "dBASE IV 3D";
    }
}

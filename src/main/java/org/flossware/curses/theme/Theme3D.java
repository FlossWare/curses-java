package org.flossware.curses.theme;

import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;

/**
 * Extended theme interface providing 3D rendering capabilities for curses-java components.
 *
 * <p>This interface extends the base {@link Theme} interface with additional methods
 * that define visual properties for three-dimensional rendering effects, including
 * shadows, highlights, and specialized border styles.</p>
 *
 * <h2>3D Rendering Concepts</h2>
 * <p>The 3D effect is achieved through asymmetric coloring and shadow rendering:</p>
 * <ul>
 *   <li><b>Raised components</b>: Top-left edges use highlight color (bright),
 *       bottom-right edges use lowlight color (dark), creating a light-from-above effect</li>
 *   <li><b>Sunken components</b>: Color scheme is inverted - dark top-left, bright bottom-right,
 *       creating a recessed appearance</li>
 *   <li><b>Shadows</b>: L-shaped shadow rendered at offset position using shadow color,
 *       simulating depth and elevation</li>
 * </ul>
 *
 * <h2>Usage Pattern</h2>
 * <pre>{@code
 * // Check if current theme supports 3D
 * Theme theme = ThemeManager.getCurrentTheme();
 * if (theme.supports3D()) {
 *     Theme3D theme3d = (Theme3D) theme;
 *
 *     // Enable 3D rendering for a component
 *     component.set3DEnabled(true);
 *     component.setRenderingStyle(RenderingStyle.RAISED);
 *
 *     // Access 3D-specific theme properties
 *     ColorPair shadow = theme3d.getShadowColor();
 *     int offsetX = theme3d.getShadowOffsetX();
 * }
 * }</pre>
 *
 * <h2>Backward Compatibility</h2>
 * <p>This interface is completely optional. Themes that do not implement {@code Theme3D}
 * will continue to work unchanged, rendering components in traditional flat style.
 * Components check {@link Theme#supports3D()} before attempting 3D rendering, ensuring
 * graceful fallback to 2D mode.</p>
 *
 * <h2>Terminal Requirements</h2>
 * <p>3D rendering works best with Unicode-capable terminals that support box-drawing
 * characters. For terminals without Unicode support, implementations should fall back
 * to ASCII characters. Color support is assumed to be at least 8 colors (standard
 * ncurses color pairs).</p>
 *
 * @see Theme
 * @see org.flossware.curses.component.Component#set3DEnabled(boolean)
 * @see org.flossware.curses.component.RenderingStyle
 */
public interface Theme3D extends Theme {

    /**
     * Get the color pair used for rendering drop shadows.
     *
     * <p>Shadows are typically rendered as an L-shaped region offset from the component's
     * border, using either:</p>
     * <ul>
     *   <li>Space characters with a dark color attribute (solid shadow)</li>
     *   <li>Shade characters (░▒▓) for gradient shadow effects</li>
     * </ul>
     *
     * <p>Common shadow colors:</p>
     * <ul>
     *   <li>BLACK/BLACK with BOLD attribute for solid shadows</li>
     *   <li>DARK_GRAY/BLACK for softer shadows (if terminal supports extended colors)</li>
     * </ul>
     *
     * @return color pair for shadow rendering, never {@code null}
     */
    ColorPair getShadowColor();

    /**
     * Get the color pair used for highlighting raised surfaces.
     *
     * <p>The highlight color is applied to top and left edges of raised components,
     * simulating light reflection from a light source positioned above-left.
     * For sunken components, this color is used on bottom and right edges (inverted).</p>
     *
     * <p>Common highlight colors:</p>
     * <ul>
     *   <li>WHITE/CYAN with BOLD attribute for bright highlights (Borland style)</li>
     *   <li>BRIGHT_WHITE/BACKGROUND for subtle highlights</li>
     * </ul>
     *
     * @return color pair for highlight rendering, never {@code null}
     */
    ColorPair getHighlightColor();

    /**
     * Get the color pair used for lowlighting recessed surfaces.
     *
     * <p>The lowlight color is applied to bottom and right edges of raised components,
     * creating shadow edges. For sunken components, this color is used on top and left edges.</p>
     *
     * <p>Lowlight is typically darker than the base border color but lighter than the
     * drop shadow color, creating a middle tone for edge definition.</p>
     *
     * <p>Common lowlight colors:</p>
     * <ul>
     *   <li>BLACK/CYAN for dark edges (Borland style)</li>
     *   <li>DARK_GRAY/BACKGROUND for subtle recession</li>
     * </ul>
     *
     * @return color pair for lowlight rendering, never {@code null}
     */
    ColorPair getLowlightColor();

    /**
     * Get the horizontal offset for shadow rendering.
     *
     * <p>This value determines how many characters to the right the shadow is displaced
     * from the component's border. Typical values:</p>
     * <ul>
     *   <li>{@code 1} - subtle shadow, minimal depth</li>
     *   <li>{@code 2} - standard shadow, moderate depth (recommended)</li>
     *   <li>{@code 3+} - pronounced shadow, exaggerated depth</li>
     * </ul>
     *
     * <p>The shadow offset should account for terminal character aspect ratio. Since
     * terminal characters are typically taller than wide (approximately 2:1), horizontal
     * offsets often need to be larger than vertical offsets to create a 45-degree shadow angle.</p>
     *
     * @return horizontal shadow offset in character cells, typically 1-3
     */
    int getShadowOffsetX();

    /**
     * Get the vertical offset for shadow rendering.
     *
     * <p>This value determines how many characters down the shadow is displaced from
     * the component's border. Typical values:</p>
     * <ul>
     *   <li>{@code 1} - subtle shadow, minimal depth (recommended)</li>
     *   <li>{@code 2} - standard shadow, moderate depth</li>
     * </ul>
     *
     * <p>Vertical offsets are usually smaller than horizontal offsets due to character
     * aspect ratio (characters are taller than wide). A common aesthetic choice is
     * shadowOffsetX = 2, shadowOffsetY = 1, creating approximately 45-degree shadow angle.</p>
     *
     * @return vertical shadow offset in character cells, typically 1-2
     */
    int getShadowOffsetY();

    /**
     * Indicates whether this theme has 3D rendering capabilities.
     *
     * <p>This method always returns {@code true} for themes implementing {@code Theme3D}.
     * It provides a convenient check without requiring {@code instanceof} or type casting.</p>
     *
     * <p>This method overrides the default implementation in {@link Theme#supports3D()}.</p>
     *
     * @return always {@code true} for {@code Theme3D} implementations
     */
    @Override
    default boolean supports3D() {
        return true;
    }

    /**
     * Get the border characters used for double-line borders in emphasized dialogs.
     *
     * <p>Double-line borders provide visual emphasis for important dialogs or modal
     * windows. The character set follows the same 8-character format as
     * {@link Theme#getBorderChars()}:</p>
     * <ul>
     *   <li>Index 0: top-left corner</li>
     *   <li>Index 1: top horizontal line</li>
     *   <li>Index 2: top-right corner</li>
     *   <li>Index 3: left vertical line</li>
     *   <li>Index 4: right vertical line</li>
     *   <li>Index 5: bottom-left corner</li>
     *   <li>Index 6: bottom horizontal line</li>
     *   <li>Index 7: bottom-right corner</li>
     * </ul>
     *
     * <p>Common character sets:</p>
     * <ul>
     *   <li>Unicode double-line: {@code "╔═╗║║╚═╝"}</li>
     *   <li>ASCII fallback: {@code "+=+||+=+"}</li>
     * </ul>
     *
     * <p>Default implementation returns Unicode double-line characters. Implementations
     * should detect terminal capabilities and fall back to ASCII if Unicode is not supported.</p>
     *
     * @return string of 8 characters defining double-line border, never {@code null}
     */
    default String getDoubleBorderChars() {
        return "╔═╗║║╚═╝";
    }

    /**
     * Get the character used for solid shadow fill.
     *
     * <p>This character fills the L-shaped shadow region. Options include:</p>
     * <ul>
     *   <li>Space {@code ' '} - solid shadow using color attributes only</li>
     *   <li>Light shade {@code '░'} - subtle shadow pattern</li>
     *   <li>Medium shade {@code '▒'} - moderate shadow pattern</li>
     *   <li>Dark shade {@code '▓'} - strong shadow pattern</li>
     * </ul>
     *
     * <p>Default implementation returns space character for clean, solid shadows.
     * Shade characters (░▒▓) create texture but may not render consistently across
     * all terminals.</p>
     *
     * @return character for shadow fill, defaults to {@code ' '}
     */
    default char getShadowChar() {
        return ' ';
    }

    /**
     * Determines whether shadows should use gradient shading characters.
     *
     * <p>When {@code true}, shadows use a gradient of shade characters (░▒▓) from
     * light to dark, creating a soft shadow effect. When {@code false}, shadows use
     * a solid fill character (see {@link #getShadowChar()}).</p>
     *
     * <p>Gradient shadows look more realistic but require Unicode support and may
     * not render consistently. Solid shadows work reliably across all terminals.</p>
     *
     * <p>Default implementation returns {@code false} for maximum compatibility.</p>
     *
     * @return {@code true} to use gradient shadows, {@code false} for solid shadows
     */
    default boolean useGradientShadow() {
        return false;
    }

    /**
     * Get the default rendering style for components using this theme.
     *
     * <p>This provides a theme-wide default that components can override individually.
     * Common defaults:</p>
     * <ul>
     *   <li>{@link RenderingStyle#RAISED} - most components appear raised (typical for Borland style)</li>
     *   <li>{@link RenderingStyle#FLAT} - minimal 3D effects, modern flat design</li>
     *   <li>{@link RenderingStyle#SUNKEN} - depressed appearance for input fields</li>
     * </ul>
     *
     * <p>Default implementation returns {@code RAISED}.</p>
     *
     * @return default rendering style for this theme, never {@code null}
     */
    default RenderingStyle getDefaultRenderingStyle() {
        return RenderingStyle.RAISED;
    }
}

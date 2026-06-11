package org.flossware.curses.api;

/**
 * Enumeration of 3D rendering styles for jcurses components.
 *
 * <p>This enum defines the visual appearance of components when 3D rendering is enabled.
 * Each style determines how highlight and lowlight colors are applied to component borders
 * to create different depth effects.</p>
 *
 * <h2>Rendering Behavior</h2>
 * <p>The rendering style controls edge coloring patterns:</p>
 * <ul>
 *   <li><b>RAISED</b>: Top-left edges bright (highlight), bottom-right edges dark (lowlight),
 *       simulating a surface elevated toward the viewer with light from above-left</li>
 *   <li><b>SUNKEN</b>: Top-left edges dark (lowlight), bottom-right edges bright (highlight),
 *       simulating a recessed surface or depression</li>
 *   <li><b>FLAT</b>: No highlight/lowlight asymmetry, uniform border color (2D appearance)</li>
 *   <li><b>CUSTOM</b>: Application-defined rendering, allows component-specific effects</li>
 * </ul>
 *
 * <h2>Usage Pattern</h2>
 * <pre>{@code
 * // Configure component rendering style
 * button.set3DEnabled(true);
 * button.setRenderingStyle(RenderingStyle.RAISED);
 *
 * // Input fields typically use SUNKEN
 * textField.set3DEnabled(true);
 * textField.setRenderingStyle(RenderingStyle.SUNKEN);
 *
 * // Modern flat design
 * dialog.set3DEnabled(true);
 * dialog.setRenderingStyle(RenderingStyle.FLAT);
 * }</pre>
 *
 * <h2>Theme Integration</h2>
 * <p>When 3D rendering is enabled, the rendering pipeline queries the component's
 * {@code RenderingStyle} and applies colors from the {@link org.flossware.curses.theme.Theme3D}
 * interface accordingly. Components without an explicit style use the theme's default
 * style from {@link org.flossware.curses.theme.Theme3D#getDefaultRenderingStyle()}.</p>
 *
 * <h2>Button State Interaction</h2>
 * <p>Buttons have special interaction with rendering styles:</p>
 * <ul>
 *   <li>Normal state: Renders with configured style (typically RAISED)</li>
 *   <li>Pressed state: Automatically inverts to SUNKEN and shifts content 1 character right,
 *       creating a "pressed down" effect</li>
 * </ul>
 *
 * @see org.flossware.curses.theme.Theme3D
 * @see Component#set3DEnabled(boolean)
 * @see Component#setRenderingStyle(RenderingStyle)
 */
public enum RenderingStyle {

    /**
     * Flat rendering with uniform border color.
     *
     * <p>No 3D effects are applied - all border edges use the same color from
     * {@link org.flossware.curses.theme.Theme#getBorder()}. This style is appropriate
     * for modern flat design aesthetics while still allowing shadow rendering if enabled.</p>
     *
     * <p>Shadows may still be rendered if the component has 3D enabled and the theme
     * provides shadow configuration. This creates a "floating" effect without the
     * raised/sunken border beveling.</p>
     *
     * <h3>Visual Appearance</h3>
     * <pre>
     * ┌──────────┐
     * │  FLAT    │  ← All edges same color
     * │  BUTTON  │
     * └──────────┘
     * </pre>
     */
    FLAT,

    /**
     * Raised rendering with light top-left, dark bottom-right edges.
     *
     * <p>Creates the illusion of a surface elevated toward the viewer with lighting
     * from the upper-left. Top and left border edges use the highlight color
     * ({@link org.flossware.curses.theme.Theme3D#getHighlightColor()}), while bottom
     * and right edges use the lowlight color
     * ({@link org.flossware.curses.theme.Theme3D#getLowlightColor()}).</p>
     *
     * <p>This is the most common style for buttons, toolbars, and interactive elements
     * in classic UI designs (e.g., Borland-style interfaces).</p>
     *
     * <h3>Visual Appearance</h3>
     * <pre>
     * ┌──────────┐  ← Top: bright (highlight)
     * │  RAISED  │  ← Left: bright
     * │  BUTTON  │  ← Right: dark (lowlight)
     * └──────────┘  ← Bottom: dark
     *   ░░░░░░░░░   ← Optional shadow
     * </pre>
     *
     * <h3>Color Application</h3>
     * <ul>
     *   <li>Top-left corner: highlight color</li>
     *   <li>Top edge: highlight color</li>
     *   <li>Left edge: highlight color</li>
     *   <li>Right edge: lowlight color</li>
     *   <li>Bottom edge: lowlight color</li>
     *   <li>Bottom-right corner: lowlight color</li>
     * </ul>
     */
    RAISED,

    /**
     * Sunken rendering with dark top-left, light bottom-right edges.
     *
     * <p>Creates the illusion of a recessed surface or depression by inverting the
     * raised color scheme. Top and left border edges use the lowlight color, while
     * bottom and right edges use the highlight color.</p>
     *
     * <p>This style is commonly used for:</p>
     * <ul>
     *   <li>Text input fields (suggesting a "well" to type into)</li>
     *   <li>Pressed buttons (temporary inversion when user presses the button)</li>
     *   <li>List boxes and scrollable areas</li>
     *   <li>Display panels showing read-only data</li>
     * </ul>
     *
     * <h3>Visual Appearance</h3>
     * <pre>
     * ┌──────────┐  ← Top: dark (lowlight)
     * │  SUNKEN  │  ← Left: dark
     * │  INPUT   │  ← Right: bright (highlight)
     * └──────────┘  ← Bottom: bright
     * </pre>
     *
     * <h3>Color Application</h3>
     * <ul>
     *   <li>Top-left corner: lowlight color</li>
     *   <li>Top edge: lowlight color</li>
     *   <li>Left edge: lowlight color</li>
     *   <li>Right edge: highlight color</li>
     *   <li>Bottom edge: highlight color</li>
     *   <li>Bottom-right corner: highlight color</li>
     * </ul>
     *
     * <h3>Button Interaction</h3>
     * <p>When a button with {@code RAISED} style is pressed, the rendering pipeline
     * temporarily switches to {@code SUNKEN} and shifts the button content 1 character
     * right, creating tactile feedback that the button has been "pushed down".</p>
     */
    SUNKEN,

    /**
     * Custom rendering deferred to component-specific implementation.
     *
     * <p>This style indicates that the component provides its own 3D rendering logic
     * beyond the standard raised/sunken/flat patterns. The rendering pipeline skips
     * standard highlight/lowlight application and delegates to the component's custom
     * paint method.</p>
     *
     * <p>Use cases for custom rendering:</p>
     * <ul>
     *   <li>Components with complex multi-part borders (e.g., titled panels with breaks in the border)</li>
     *   <li>Components with gradient or non-uniform shading</li>
     *   <li>Components that need to render beveled corners or rounded 3D edges</li>
     *   <li>Components with state-dependent rendering beyond simple raised/sunken toggling</li>
     * </ul>
     *
     * <h3>Implementation Requirements</h3>
     * <p>Components using {@code CUSTOM} style must override their paint method to
     * explicitly handle 3D rendering. The theme's 3D color methods
     * ({@link org.flossware.curses.theme.Theme3D#getHighlightColor()}, etc.) are
     * available but application is component-defined.</p>
     *
     * <h3>Example Custom Rendering</h3>
     * <pre>{@code
     * public class TitledPanel extends Container {
     *     @Override
     *     protected void paintBorder(char[][] buffer) {
     *         if (is3DEnabled() && getRenderingStyle() == RenderingStyle.CUSTOM) {
     *             // Custom rendering: draw raised border with title break
     *             drawRaisedBorderWithTitleGap(buffer);
     *             drawEmbossedTitle(buffer);
     *         } else {
     *             super.paintBorder(buffer);
     *         }
     *     }
     * }
     * }</pre>
     *
     * <p>If a component specifies {@code CUSTOM} but does not override paint behavior,
     * it falls back to {@code FLAT} rendering.</p>
     */
    CUSTOM
}

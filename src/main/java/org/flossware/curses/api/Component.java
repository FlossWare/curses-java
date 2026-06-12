package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Base class for all terminal UI components.
 *
 * <p>Component provides the fundamental functionality shared by all UI elements:
 * position, size, rendering, color, parent-child relationships, and mouse event handling.
 * All component state modifications are thread-safe using {@link ReentrantLock}.
 *
 * <p>Subclasses must implement {@link #paint(char[][])} to define their visual appearance.
 * The component tree is maintained through parent-child relationships, with {@link RootPane}
 * as the root. Changes to a component automatically trigger repainting through the
 * {@link #repaint()} method.
 *
 * @author FlossWare
 * @since 1.0
 * @see Container
 * @see RootPane
 */
public abstract class Component {
    protected int x, y, width, height;
    protected final ReentrantLock renderLock = new ReentrantLock();
    protected Component parent;
    protected final List<MouseListener> mouseListeners = new ArrayList<>();
    protected ColorPair colorPair = ColorPair.DEFAULT;

    // Border customization
    private String customBorderChars;

    // Accessibility support
    private String accessibleName;
    private String accessibleRole;
    private String accessibleDescription;

    // Visibility support
    private boolean visible = true;

    // 3D rendering support
    private RenderingStyle renderingStyle = RenderingStyle.FLAT;
    private boolean enabled3D = false;

    // Preferred size for layout managers (issue #237)
    protected Dimension preferredSize = new Dimension(10, 1);

    /**
     * Renders this component into the provided character buffer.
     *
     * <p>Implementations should draw their visual representation starting at
     * ({@link #getX()}, {@link #getY()}) with dimensions ({@link #getWidth()},
     * {@link #getHeight()}). All rendering must be thread-safe and should acquire
     * {@link #renderLock} before accessing component state.
     *
     * @param buffer the character buffer to render into
     */
    public abstract void paint(char[][] buffer);

    public void setLocation(int x, int y) {
        renderLock.lock();
        try {
            this.x = x;
            this.y = y;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void setSize(int width, int height) {
        renderLock.lock();
        try {
            this.width = width;
            this.height = height;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getX() {
        renderLock.lock();
        try {
            return x;
        } finally {
            renderLock.unlock();
        }
    }

    public int getY() {
        renderLock.lock();
        try {
            return y;
        } finally {
            renderLock.unlock();
        }
    }

    public int getWidth() {
        renderLock.lock();
        try {
            return width;
        } finally {
            renderLock.unlock();
        }
    }

    public int getHeight() {
        renderLock.lock();
        try {
            return height;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Returns the preferred size of this component for layout managers.
     * The preferred size represents the component's ideal dimensions.
     *
     * @return the preferred size as a Dimension
     * @since 1.1
     */
    public Dimension getPreferredSize() {
        renderLock.lock();
        try {
            return preferredSize;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Sets the preferred size of this component for layout managers.
     * This does not change the actual size, only what the component
     * requests from layout managers.
     *
     * @param width the preferred width
     * @param height the preferred height
     * @since 1.1
     */
    public void setPreferredSize(int width, int height) {
        renderLock.lock();
        try {
            this.preferredSize = new Dimension(width, height);
        } finally {
            renderLock.unlock();
        }
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public ColorPair getColorPair() {
        return colorPair;
    }

    public void setColorPair(ColorPair colorPair) {
        renderLock.lock();
        try {
            this.colorPair = colorPair;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Sets custom border characters for this component.
     *
     * <p>The border character string must contain exactly 8 characters representing
     * border drawing characters in this order:
     * <ol>
     *   <li>[0] = top-left corner</li>
     *   <li>[1] = top horizontal line</li>
     *   <li>[2] = top-right corner</li>
     *   <li>[3] = left vertical line</li>
     *   <li>[4] = right vertical line</li>
     *   <li>[5] = bottom-left corner</li>
     *   <li>[6] = bottom horizontal line</li>
     *   <li>[7] = bottom-right corner</li>
     * </ol>
     *
     * <p>Examples:
     * <ul>
     *   <li>ASCII box: "+-+||+-+"</li>
     *   <li>Unicode single: "┌─┐││└─┘"</li>
     *   <li>Unicode double: "╔═╗║║╚═╝"</li>
     * </ul>
     *
     * <p>Pass null to use the theme's default border characters.
     * This method is thread-safe.
     *
     * @param borderChars the 8-character border string, or null to use theme default
     * @throws IllegalArgumentException if borderChars is not null and length is not 8
     * @since 1.29
     */
    public void setBorderChars(String borderChars) {
        if (borderChars != null && borderChars.length() != 8) {
            throw new IllegalArgumentException(
                "borderChars must be exactly 8 characters (top-left, top, top-right, left, right, bottom-left, bottom, bottom-right), got: "
                + borderChars.length()
            );
        }
        renderLock.lock();
        try {
            this.customBorderChars = borderChars;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Returns the border characters for this component.
     *
     * <p>This method implements a three-tier fallback strategy:
     * <ol>
     *   <li>If custom border characters are set via {@link #setBorderChars(String)}, return those</li>
     *   <li>Otherwise, delegate to the current theme's {@link org.flossware.curses.theme.Theme#getBorderChars()}</li>
     *   <li>If theme returns null, return hardcoded ASCII default "+-+||+-+"</li>
     * </ol>
     *
     * <p>This method is thread-safe.
     *
     * @return the 8-character border string for this component, never null
     * @since 1.29
     */
    public String getBorderChars() {
        renderLock.lock();
        try {
            if (customBorderChars != null) {
                return customBorderChars;
            }

            // Delegate to theme
            try {
                String themeBorderChars = org.flossware.curses.theme.ThemeManager
                    .getInstance()
                    .getCurrentTheme()
                    .getBorderChars();
                if (themeBorderChars != null) {
                    return themeBorderChars;
                }
            } catch (Exception e) {
                // Theme access failed, fall through to default
            }

            // Hardcoded fallback
            return "+-+||+-+";
        } finally {
            renderLock.unlock();
        }
    }

    public void repaint() {
        Component root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        if (root instanceof RootPane) {
            ((RootPane) root).markDirty();
        }
    }

    protected void writeStringToBuffer(char[][] buffer, String text, int x, int y) {
        if (y < 0 || y >= buffer.length || text == null) {
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            int targetX = x + i;
            if (targetX >= 0 && targetX < buffer[y].length) {
                buffer[y][targetX] = text.charAt(i);
            }
        }
    }

    /**
     * Safely writes a character to the buffer at the specified position with bounds checking.
     * This method prevents ArrayIndexOutOfBoundsException by validating both row and column
     * indices before writing to the 2D character array. Designed for widgets that render
     * character-by-character, such as progress bars and sliders.
     *
     * @param buffer the character buffer to write to
     * @param x the column position (0-based)
     * @param y the row position (0-based)
     * @param c the character to write
     * @since 1.28
     */
    protected void writeCharToBuffer(char[][] buffer, int x, int y, char c) {
        if (y < 0 || y >= buffer.length || x < 0 || x >= buffer[y].length) {
            return;
        }
        buffer[y][x] = c;
    }

    /**
     * Renders a 3D drop shadow for this component if 3D rendering is supported and enabled.
     *
     * <p>This method draws an L-shaped shadow offset to the right and below the component,
     * creating a depth illusion. The shadow consists of two parts:
     * <ul>
     *   <li><b>Vertical part</b>: Right edge shadow, starting at the shadow offset and
     *       extending down for (height - shadowOffsetY) rows, 2 columns wide</li>
     *   <li><b>Horizontal part</b>: Bottom edge shadow, starting at the shadow offset and
     *       extending right for width columns, 1 row tall</li>
     * </ul>
     *
     * <p>The shadow is rendered using the theme's shadow character (typically space for
     * solid shadows, or shade characters ░▒▓ for gradient shadows). Shadow color and
     * offset values are obtained from the {@link org.flossware.curses.theme.Theme3D} interface.
     *
     * <p><b>CRITICAL RENDERING ORDER:</b> This method MUST be called BEFORE {@link Container#drawBorder(char[][], int[][])}
     * to ensure proper visual z-ordering. Shadows appear <i>behind</i> borders in the visual stack, so the
     * shadow must be painted to the buffer first, then the border is painted on top. If called after
     * border rendering, the shadow will incorrectly overlap the border pixels, producing visual artifacts.
     * {@link Container#paint(char[][])} enforces this order automatically by calling {@code paintShadow()}
     * before {@code drawBorder()}.
     *
     * <p>This method only renders if the current theme supports 3D via
     * {@link org.flossware.curses.theme.Theme#supports3D()} and 3D is enabled for this component
     * via {@link #is3DEnabled()}.
     *
     * <p>Thread-safe: acquires renderLock before accessing component dimensions.
     *
     * @param buffer the character buffer to render the shadow into
     * @param colorBuffer color pair buffer for per-character coloring (may be null for character-only rendering)
     * @since 1.31
     * @see org.flossware.curses.theme.Theme3D#getShadowColor()
     * @see org.flossware.curses.theme.Theme3D#getShadowOffsetX()
     * @see org.flossware.curses.theme.Theme3D#getShadowOffsetY()
     * @see org.flossware.curses.theme.Theme3D#getShadowChar()
     * @see #is3DEnabled()
     * @see Container#drawBorder(char[][], int[][])
     * @see Container#paint(char[][])
     */
    protected void paintShadow(char[][] buffer, int[][] colorBuffer) {
        // Check if 3D rendering is enabled for this component
        if (!is3DEnabled()) {
            return;
        }

        // Check if current theme supports 3D rendering
        org.flossware.curses.theme.Theme theme;
        try {
            theme = org.flossware.curses.theme.ThemeManager.getInstance().getCurrentTheme();
        } catch (Exception e) {
            // Theme access failed, skip shadow rendering
            return;
        }

        if (!theme.supports3D()) {
            // Theme doesn't support 3D, skip shadow rendering
            return;
        }

        // Safe to cast to Theme3D
        org.flossware.curses.theme.Theme3D theme3d = (org.flossware.curses.theme.Theme3D) theme;

        // Get shadow properties from theme
        int shadowOffsetX = theme3d.getShadowOffsetX();
        int shadowOffsetY = theme3d.getShadowOffsetY();
        char shadowChar = theme3d.getShadowChar();
        int shadowColorNum = theme3d.getShadowColor().pairNumber();

        // Lock to safely read component dimensions
        renderLock.lock();
        try {
            // Render vertical shadow part (right edge)
            // Starts at x = component.x + component.width (right edge)
            // Extends shadowOffsetX columns wide (typically 2)
            // Begins at y = component.y (aligned with top edge)
            // Extends for component.height rows
            int verticalStartX = x + width;
            int verticalStartY = y;
            int verticalHeight = height;

            for (int row = 0; row < verticalHeight; row++) {
                for (int col = 0; col < shadowOffsetX; col++) {
                    int shadowX = verticalStartX + col;
                    int shadowY = verticalStartY + row;
                    writeCharToBuffer(buffer, shadowX, shadowY, shadowChar);
                    if (colorBuffer != null && shadowY >= 0 && shadowY < colorBuffer.length &&
                        shadowX >= 0 && shadowX < colorBuffer[shadowY].length) {
                        colorBuffer[shadowY][shadowX] = shadowColorNum;
                    }
                }
            }

            // Render horizontal shadow part (bottom edge)
            // Starts at x = component.x (aligned with left edge)
            // Extends for (component.width + shadowOffsetX) columns to connect with vertical shadow
            // Located at y = component.y + component.height + shadowOffsetY (below bottom edge with offset)
            int horizontalStartX = x;
            int horizontalY = y + height + shadowOffsetY;

            for (int col = 0; col < width + shadowOffsetX; col++) {
                int shadowX = horizontalStartX + col;
                writeCharToBuffer(buffer, shadowX, horizontalY, shadowChar);
                if (colorBuffer != null && horizontalY >= 0 && horizontalY < colorBuffer.length &&
                    shadowX >= 0 && shadowX < colorBuffer[horizontalY].length) {
                    colorBuffer[horizontalY][shadowX] = shadowColorNum;
                }
            }
        } finally {
            renderLock.unlock();
        }
    }

    public void addMouseListener(MouseListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        renderLock.lock();
        try {
            mouseListeners.add(listener);
        } finally {
            renderLock.unlock();
        }
    }

    public void removeMouseListener(MouseListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        renderLock.lock();
        try {
            mouseListeners.remove(listener);
        } finally {
            renderLock.unlock();
        }
    }

    public boolean handleMouseEvent(MouseEvent event) {
        renderLock.lock();
        try {
            // Check if the click is within this component's bounds
            if (event.x() >= x && event.x() < x + width &&
                event.y() >= y && event.y() < y + height) {

                // Copy listener list to avoid ConcurrentModificationException
                List<MouseListener> listeners = new ArrayList<>(mouseListeners);

                renderLock.unlock();
                try {
                    // Notify all mouse listeners outside lock
                    for (MouseListener listener : listeners) {
                        listener.onMouseEvent(event);
                    }
                    return true;
                } finally {
                    renderLock.lock();
                }
            }
            return false;
        } finally {
            renderLock.unlock();
        }
    }

    // Accessibility Support

    /**
     * Sets the accessible name for this component.
     *
     * <p>The accessible name is a short, user-friendly label that identifies
     * the component for assistive technologies like screen readers.
     * This method is thread-safe.
     *
     * @param name the accessible name, or null to clear
     * @since 1.26
     */
    public void setAccessibleName(String name) {
        renderLock.lock();
        try {
            this.accessibleName = name;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Returns the accessible name of this component.
     * This method is thread-safe.
     *
     * @return the accessible name, or null if not set
     * @since 1.26
     */
    public String getAccessibleName() {
        renderLock.lock();
        try {
            return accessibleName;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Sets the role of this component for accessibility purposes.
     *
     * <p>Common roles include: "button", "label", "textfield", "checkbox",
     * "progressbar", "dialog", etc.
     * This method is thread-safe.
     *
     * @param role the ARIA-like role name
     * @since 1.26
     */
    public void setAccessibleRole(String role) {
        renderLock.lock();
        try {
            this.accessibleRole = role;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Returns the accessibility role of this component.
     * This method is thread-safe.
     *
     * @return the role name, or null if not set
     * @since 1.26
     */
    public String getAccessibleRole() {
        renderLock.lock();
        try {
            return accessibleRole;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Sets a detailed description for accessibility.
     *
     * <p>This provides additional context beyond the name, such as
     * instructions or state information.
     * This method is thread-safe.
     *
     * @param description the accessible description
     * @since 1.26
     */
    public void setAccessibleDescription(String description) {
        renderLock.lock();
        try {
            this.accessibleDescription = description;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Returns the accessible description.
     * This method is thread-safe.
     *
     * @return the description, or null if not set
     * @since 1.26
     */
    public String getAccessibleDescription() {
        renderLock.lock();
        try {
            return accessibleDescription;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Returns a complete accessibility summary of this component.
     *
     * <p>This combines role, name, and description into a format suitable
     * for screen readers or accessibility logging.
     * This method is thread-safe.
     *
     * @return formatted accessibility information
     * @since 1.26
     */
    public String getAccessibilitySummary() {
        renderLock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            if (accessibleRole != null) {
                sb.append(accessibleRole);
            } else {
                sb.append(getClass().getSimpleName());
            }

            if (accessibleName != null) {
                sb.append(": ").append(accessibleName);
            }

            if (accessibleDescription != null) {
                sb.append(" - ").append(accessibleDescription);
            }

            sb.append(String.format(" at (%d, %d) size (%d, %d)", x, y, width, height));

            return sb.toString();
        } finally {
            renderLock.unlock();
        }
    }

    // Visibility Support

    /**
     * Sets the visibility state of this component.
     *
     * <p>When a component is set to invisible ({@code visible = false}), it will not
     * be rendered during paint operations. The component still exists in the component
     * tree and maintains its position and size, but it will not appear on screen.
     *
     * <p>Visibility changes trigger a repaint of the component hierarchy to ensure
     * the display is updated immediately. This method is thread-safe.
     *
     * <p><strong>Note:</strong> Subclasses that override {@link #paint(char[][])} should
     * check {@link #isVisible()} at the start of their paint method and return early if
     * the component is not visible.
     *
     * @param visible {@code true} to make the component visible, {@code false} to hide it
     * @since 1.30
     * @see #isVisible()
     */
    public void setVisible(boolean visible) {
        renderLock.lock();
        try {
            this.visible = visible;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Returns the visibility state of this component.
     *
     * <p>A component is visible if this method returns {@code true}. Invisible components
     * should not be rendered during paint operations. This method is thread-safe.
     *
     * @return {@code true} if the component is visible, {@code false} otherwise
     * @since 1.30
     * @see #setVisible(boolean)
     */
    public boolean isVisible() {
        renderLock.lock();
        try {
            return visible;
        } finally {
            renderLock.unlock();
        }
    }

    // 3D Rendering Support

    /**
     * Sets the rendering style for this component's 3D appearance.
     *
     * <p>The rendering style determines how highlight and lowlight colors are applied
     * to component borders when 3D rendering is enabled. This creates visual depth effects
     * such as raised buttons or sunken input fields.
     *
     * <p>Common styles include:
     * <ul>
     *   <li>{@link RenderingStyle#FLAT} - No 3D effect, uniform border color</li>
     *   <li>{@link RenderingStyle#RAISED} - Elevated appearance with light top-left edges</li>
     *   <li>{@link RenderingStyle#SUNKEN} - Recessed appearance with dark top-left edges</li>
     *   <li>{@link RenderingStyle#CUSTOM} - Component-specific rendering logic</li>
     * </ul>
     *
     * <p>Changing the rendering style triggers a repaint to update the component's appearance.
     * This method is thread-safe.
     *
     * @param style the rendering style to apply, must not be null
     * @throws IllegalArgumentException if style is null
     * @since 1.31
     * @see #getRenderingStyle()
     * @see RenderingStyle
     */
    public void setRenderingStyle(RenderingStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("RenderingStyle cannot be null");
        }
        renderLock.lock();
        try {
            this.renderingStyle = style;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Returns the rendering style of this component.
     *
     * <p>The rendering style controls how 3D effects are applied during rendering.
     * Components default to {@link RenderingStyle#FLAT} which provides no 3D effect.
     * This method is thread-safe.
     *
     * @return the current rendering style, never null
     * @since 1.31
     * @see #setRenderingStyle(RenderingStyle)
     * @see RenderingStyle
     */
    public RenderingStyle getRenderingStyle() {
        renderLock.lock();
        try {
            return renderingStyle;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Enables or disables 3D rendering effects for this component.
     *
     * <p>When 3D rendering is enabled, the component will render with depth effects
     * such as drop shadows (via {@link #paintShadow(char[][], int[][])}) and styled
     * borders based on the {@link RenderingStyle}. These effects create visual depth
     * and improve the component's appearance on terminals that support them.
     *
     * <p>3D rendering requires:
     * <ul>
     *   <li>The current theme must support 3D via {@link org.flossware.curses.theme.Theme#supports3D()}</li>
     *   <li>This component's 3D must be explicitly enabled via this method</li>
     *   <li>The terminal must support the necessary characters and colors</li>
     * </ul>
     *
     * <p>Enabling or disabling 3D triggers a repaint to update the component's appearance.
     * This method is thread-safe.
     *
     * @param enabled {@code true} to enable 3D rendering effects, {@code false} to disable
     * @since 1.31
     * @see #is3DEnabled()
     * @see #paintShadow(char[][], int[][])
     * @see #setRenderingStyle(RenderingStyle)
     */
    public synchronized void set3DEnabled(boolean enabled) {
        renderLock.lock();
        try {
            this.enabled3D = enabled;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Returns whether 3D rendering is enabled for this component.
     *
     * <p>When 3D rendering is enabled, the component may render with depth effects
     * such as shadows and styled borders, provided the current theme supports 3D.
     * Components default to 3D disabled ({@code false}).
     *
     * <p>This method is thread-safe.
     *
     * @return {@code true} if 3D rendering is enabled, {@code false} otherwise
     * @since 1.31
     * @see #set3DEnabled(boolean)
     * @see #paintShadow(char[][], int[][])
     */
    public synchronized boolean is3DEnabled() {
        renderLock.lock();
        try {
            return enabled3D;
        } finally {
            renderLock.unlock();
        }
    }
}

package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;
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

    // Accessibility support
    private String accessibleName;
    private String accessibleRole;
    private String accessibleDescription;

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
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        // Check if the click is within this component's bounds
        if (event.x() >= x && event.x() < x + width &&
            event.y() >= y && event.y() < y + height) {

            // Copy listener list to avoid ConcurrentModificationException
            List<MouseListener> listeners;
            renderLock.lock();
            try {
                listeners = new ArrayList<>(mouseListeners);
            } finally {
                renderLock.unlock();
            }

            // Notify all mouse listeners outside lock
            for (MouseListener listener : listeners) {
                listener.onMouseEvent(event);
            }
            return true;
        }
        return false;
    }

    // Accessibility Support

    /**
     * Sets the accessible name for this component.
     *
     * <p>The accessible name is a short, user-friendly label that identifies
     * the component for assistive technologies like screen readers.
     *
     * @param name the accessible name, or null to clear
     * @since 1.26
     */
    public void setAccessibleName(String name) {
        this.accessibleName = name;
    }

    /**
     * Returns the accessible name of this component.
     *
     * @return the accessible name, or null if not set
     * @since 1.26
     */
    public String getAccessibleName() {
        return accessibleName;
    }

    /**
     * Sets the role of this component for accessibility purposes.
     *
     * <p>Common roles include: "button", "label", "textfield", "checkbox",
     * "progressbar", "dialog", etc.
     *
     * @param role the ARIA-like role name
     * @since 1.26
     */
    public void setAccessibleRole(String role) {
        this.accessibleRole = role;
    }

    /**
     * Returns the accessibility role of this component.
     *
     * @return the role name, or null if not set
     * @since 1.26
     */
    public String getAccessibleRole() {
        return accessibleRole;
    }

    /**
     * Sets a detailed description for accessibility.
     *
     * <p>This provides additional context beyond the name, such as
     * instructions or state information.
     *
     * @param description the accessible description
     * @since 1.26
     */
    public void setAccessibleDescription(String description) {
        this.accessibleDescription = description;
    }

    /**
     * Returns the accessible description.
     *
     * @return the description, or null if not set
     * @since 1.26
     */
    public String getAccessibleDescription() {
        return accessibleDescription;
    }

    /**
     * Returns a complete accessibility summary of this component.
     *
     * <p>This combines role, name, and description into a format suitable
     * for screen readers or accessibility logging.
     *
     * @return formatted accessibility information
     * @since 1.26
     */
    public String getAccessibilitySummary() {
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
    }
}

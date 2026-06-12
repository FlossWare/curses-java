package org.flossware.curses.api;

/**
 * Represents a dimension with width and height.
 * This is an immutable class used to specify preferred sizes for components.
 *
 * @param width the width dimension
 * @param height the height dimension
 * @author FlossWare
 * @since 1.1
 */
public record Dimension(int width, int height) {
    /**
     * Creates a new Dimension with the specified width and height.
     *
     * @param width the width (must be non-negative)
     * @param height the height (must be non-negative)
     * @throws IllegalArgumentException if width or height is negative
     */
    public Dimension {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must be non-negative");
        }
    }

    /**
     * Returns a string representation of this Dimension.
     *
     * @return a string in the format "Dimension[width=W, height=H]"
     */
    @Override
    public String toString() {
        return "Dimension[width=" + width + ", height=" + height + "]";
    }
}

package org.flossware.jcurses.api;

/**
 * Marker interface for windows that support drag-to-move and resize operations.
 *
 * <p>Components implementing this interface can be moved by dragging their title bar
 * and resized by dragging their edges or corners. The {@link WindowDragManager} class
 * handles the drag logic and consults these methods to determine permissions and constraints.
 *
 * <p>Default implementations provide standard behavior:
 * <ul>
 *   <li>Draggable and resizable by default</li>
 *   <li>Minimum size of 10x3 (enough for borders and minimal content)</li>
 *   <li>No maximum size (unconstrained)</li>
 * </ul>
 *
 * @since 1.6
 */
public interface DraggableWindow {

    /**
     * Check if this window allows drag-to-move operations.
     *
     * @return true if the window can be moved by dragging its title bar
     */
    default boolean isDraggable() {
        return true;
    }

    /**
     * Check if this window allows resize operations.
     *
     * @return true if the window can be resized by dragging its edges or corners
     */
    default boolean isResizable() {
        return true;
    }

    /**
     * Get minimum width for resize operations.
     * Windows cannot be resized smaller than this width.
     *
     * @return minimum width in characters (default: 10)
     */
    default int getMinWidth() {
        return 10;  // Enough for border + minimal content
    }

    /**
     * Get minimum height for resize operations.
     * Windows cannot be resized smaller than this height.
     *
     * @return minimum height in characters (default: 3)
     */
    default int getMinHeight() {
        return 3;   // Top border + content + bottom border
    }

    /**
     * Get maximum width for resize operations.
     * Windows cannot be resized larger than this width.
     *
     * @return maximum width in characters, or 0 for no limit (default: 0)
     */
    default int getMaxWidth() {
        return 0;  // 0 = no limit
    }

    /**
     * Get maximum height for resize operations.
     * Windows cannot be resized larger than this height.
     *
     * @return maximum height in characters, or 0 for no limit (default: 0)
     */
    default int getMaxHeight() {
        return 0;  // 0 = no limit
    }
}

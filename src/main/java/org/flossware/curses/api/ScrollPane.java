package org.flossware.curses.api;

/**
 * Provides a scrollable view of a lightweight component.
 */
public class ScrollPane extends Container {
    private int offsetX = 0;
    private int offsetY = 0;
    private ScrollBar horizontalScrollBar;
    private ScrollBar verticalScrollBar;

    public ScrollPane() {
        super();
    }

    public ScrollPane(Component view) {
        super();
        if (view != null) {
            add(view);
        }
    }

    // Scroll control methods
    public void scrollTo(int x, int y) {
        renderLock.lock();
        try {
            offsetX = Math.max(0, x);
            offsetY = Math.max(0, y);
            updateScrollBars();
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void scroll(int dx, int dy) {
        scrollTo(offsetX + dx, offsetY + dy);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    // Viewport size
    public int getViewportWidth() {
        return width;
    }

    public int getViewportHeight() {
        return height;
    }

    // Content size (max bounds of all children)
    public int getContentWidth() {
        renderLock.lock();
        try {
            int maxWidth = 0;
            for (Component child : children) {
                maxWidth = Math.max(maxWidth, child.getX() + child.getWidth());
            }
            return maxWidth;
        } finally {
            renderLock.unlock();
        }
    }

    public int getContentHeight() {
        renderLock.lock();
        try {
            int maxHeight = 0;
            for (Component child : children) {
                maxHeight = Math.max(maxHeight, child.getY() + child.getHeight());
            }
            return maxHeight;
        } finally {
            renderLock.unlock();
        }
    }

    // Scrollbar integration
    public void setHorizontalScrollBar(ScrollBar scrollBar) {
        this.horizontalScrollBar = scrollBar;
        updateScrollBars();
    }

    public void setVerticalScrollBar(ScrollBar scrollBar) {
        this.verticalScrollBar = scrollBar;
        updateScrollBars();
    }

    public ScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }

    public ScrollBar getVerticalScrollBar() {
        return verticalScrollBar;
    }

    private void updateScrollBars() {
        // Update scrollbar positions based on current offsets
        // This would be called when scrolling or when content changes
        if (horizontalScrollBar != null) {
            // Calculate scrollbar value based on offsetX and content width
            int contentWidth = getContentWidth();
            int viewportWidth = getViewportWidth();
            if (contentWidth > viewportWidth) {
                // Scrollbar value represents position
            }
        }

        if (verticalScrollBar != null) {
            // Calculate scrollbar value based on offsetY and content height
            int contentHeight = getContentHeight();
            int viewportHeight = getViewportHeight();
            if (contentHeight > viewportHeight) {
                // Scrollbar value represents position
            }
        }
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            // Create a virtual buffer for children, then clip to viewport
            // This is a simplified implementation
            for (Component child : children) {
                // Translate child coordinates by scroll offset
                int childX = child.getX() - offsetX;
                int childY = child.getY() - offsetY;

                // Check if child is visible in viewport
                if (childX + child.getWidth() >= 0 && childX < width &&
                    childY + child.getHeight() >= 0 && childY < height) {

                    // Save original position
                    int origX = child.getX();
                    int origY = child.getY();

                    // Temporarily set translated position
                    child.setLocation(x + childX, y + childY);

                    // Paint child (it will be clipped by buffer bounds)
                    child.paint(buffer);

                    // Restore original position
                    child.setLocation(origX, origY);
                }
            }
        } finally {
            renderLock.unlock();
        }
    }
}
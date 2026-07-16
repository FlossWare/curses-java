package org.flossware.curses.api;

import java.util.List;

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
            // Clamp lower bound to 0
            offsetX = Math.max(0, x);
            offsetY = Math.max(0, y);

            // Clamp upper bound to content dimensions to prevent scrolling
            // past content, which would cause excessively negative translated
            // positions for children (Issue #193)
            int contentWidth = getContentWidth();
            int contentHeight = getContentHeight();
            if (contentWidth > 0) {
                offsetX = Math.min(offsetX, contentWidth);
            }
            if (contentHeight > 0) {
                offsetY = Math.min(offsetY, contentHeight);
            }

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
        renderLock.lock();
        try {
            return offsetX;
        } finally {
            renderLock.unlock();
        }
    }

    public int getOffsetY() {
        renderLock.lock();
        try {
            return offsetY;
        } finally {
            renderLock.unlock();
        }
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
        List<Component> snapshot = getChildrenSnapshot();
        int maxWidth = 0;
        for (Component child : snapshot) {
            maxWidth = Math.max(maxWidth, child.getX() + child.getWidth());
        }
        return maxWidth;
    }

    public int getContentHeight() {
        List<Component> snapshot = getChildrenSnapshot();
        int maxHeight = 0;
        for (Component child : snapshot) {
            maxHeight = Math.max(maxHeight, child.getY() + child.getHeight());
        }
        return maxHeight;
    }

    // Scrollbar integration
    public void setHorizontalScrollBar(ScrollBar scrollBar) {
        renderLock.lock();
        try {
            this.horizontalScrollBar = scrollBar;
            updateScrollBars();
        } finally {
            renderLock.unlock();
        }
    }

    public void setVerticalScrollBar(ScrollBar scrollBar) {
        renderLock.lock();
        try {
            this.verticalScrollBar = scrollBar;
            updateScrollBars();
        } finally {
            renderLock.unlock();
        }
    }

    public ScrollBar getHorizontalScrollBar() {
        renderLock.lock();
        try {
            return horizontalScrollBar;
        } finally {
            renderLock.unlock();
        }
    }

    public ScrollBar getVerticalScrollBar() {
        renderLock.lock();
        try {
            return verticalScrollBar;
        } finally {
            renderLock.unlock();
        }
    }

    private void updateScrollBars() {
        // Update scrollbar positions based on current offsets
        // This is called when scrolling or when content changes
        if (horizontalScrollBar != null) {
            int contentWidth = getContentWidth();
            int viewportWidth = getViewportWidth();
            if (contentWidth > viewportWidth) {
                horizontalScrollBar.setMaximum(contentWidth);
                horizontalScrollBar.setVisibleAmount(viewportWidth);
                horizontalScrollBar.setValue(offsetX);
            }
        }

        if (verticalScrollBar != null) {
            int contentHeight = getContentHeight();
            int viewportHeight = getViewportHeight();
            if (contentHeight > viewportHeight) {
                verticalScrollBar.setMaximum(contentHeight);
                verticalScrollBar.setVisibleAmount(viewportHeight);
                verticalScrollBar.setValue(offsetY);
            }
        }
    }

    @Override
    public void paint(char[][] buffer) {
        // Guard: validate buffer before any access (Issue #193)
        if (buffer == null || buffer.length == 0) {
            return;
        }

        int bufferHeight = buffer.length;
        int bufferWidth = buffer[0].length;

        // Use snapshot to safely iterate children while performing temporary mutations.
        // This preserves the Container's caching optimization (Issue #74).
        List<Component> snapshot = getChildrenSnapshot();
        for (Component child : snapshot) {
            // Translate child coordinates by scroll offset
            int childX = child.getX() - offsetX;
            int childY = child.getY() - offsetY;

            // Check if child is visible in viewport
            if (childX + child.getWidth() < 0 || childX >= width ||
                childY + child.getHeight() < 0 || childY >= height) {
                continue;
            }

            // Compute absolute buffer position for the translated child
            int absX = x + childX;
            int absY = y + childY;

            // Comprehensive bounds validation: ensure the translated child
            // position overlaps with the buffer area (Issue #193).
            // Skip children that are entirely outside the buffer to prevent
            // ArrayIndexOutOfBoundsException when content buffer is smaller
            // than the viewport or child extends past buffer edges.
            if (absX + child.getWidth() < 0 || absX >= bufferWidth ||
                absY + child.getHeight() < 0 || absY >= bufferHeight) {
                continue;
            }

            // Save original position
            int origX = child.getX();
            int origY = child.getY();

            try {
                // Temporarily set translated position
                child.setLocation(absX, absY);

                // Paint child - safe buffer writes are enforced by
                // Component.writeCharToBuffer/writeStringToBuffer bounds checks
                child.paint(buffer);
            } finally {
                // Restore original position even if paint throws
                child.setLocation(origX, origY);
            }
        }
    }
}
package org.flossware.curses.api;

import java.util.SequencedCollection;
import java.util.LinkedHashSet;
import org.flossware.curses.events.MouseEvent;

/**
 * A dialog window component with optional title and status bar support.
 *
 * <p>Dialogs can be modal or non-modal and appear on top of other windows.
 * They support an optional title bar and status bar for better user experience.
 *
 * <p>Dialogs implement {@link DraggableWindow}, allowing users to move and resize
 * them by dragging the title bar, edges, and corners.
 *
 * @since 1.0
 */
public class JDialog extends Container implements DraggableWindow {
    private boolean modal = true;
    private String title = "";
    private JStatusBar statusBar = null;
    private boolean draggable = true;
    private boolean resizable = true;
    private int minWidth = 10;
    private int minHeight = 3;

    /**
     * Create a dialog with no title.
     */
    public JDialog() {
        this("");
    }

    /**
     * Create a dialog with the specified title.
     *
     * @param title the dialog title
     */
    public JDialog(String title) {
        this.title = title;
    }

    /**
     * Set the dialog title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        renderLock.lock();
        try {
            this.title = title;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get the dialog title.
     *
     * @return the current title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set whether this dialog is modal.
     *
     * @param modal true for modal, false for non-modal
     */
    public void setModal(boolean modal) {
        renderLock.lock();
        try {
            this.modal = modal;
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Check if this dialog is modal.
     *
     * @return true if modal, false otherwise
     */
    public boolean isModal() {
        return modal;
    }

    /**
     * Set the status bar for this dialog.
     * The status bar will be automatically positioned at the bottom of the dialog.
     *
     * @param statusBar the status bar to use, or null to remove it
     */
    public void setStatusBar(JStatusBar statusBar) {
        renderLock.lock();
        try {
            // Remove old status bar if it exists
            if (this.statusBar != null) {
                children.remove(this.statusBar);
            }

            this.statusBar = statusBar;

            // Add and position new status bar
            if (this.statusBar != null) {
                this.statusBar.setLocation(x + 1, y + height - 2);
                this.statusBar.setSize(width - 2, 1);
                children.add(this.statusBar);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get the current status bar.
     *
     * @return the status bar, or null if none is set
     */
    public JStatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * Convenience method to set status bar text.
     * Creates a status bar if one doesn't exist.
     *
     * @param text the status text to display
     */
    public void setStatusText(String text) {
        if (statusBar == null) {
            setStatusBar(new JStatusBar(text));
        } else {
            statusBar.setText(text);
        }
    }

    public void show() {
        // In FlossWare architecture, adding to the end of a SequencedCollection
        // represents the "top" of the visual stack[cite: 5, 46].
        RootPane.getInstance().getChildren().addLast(this);
        repaint();
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        // Update status bar position if it exists
        if (statusBar != null) {
            statusBar.setLocation(x + 1, y + height - 2);
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        // Update status bar size and position if it exists
        if (statusBar != null) {
            statusBar.setLocation(x + 1, y + height - 2);
            statusBar.setSize(width - 2, 1);
        }
    }

    @Override
    public void paint(char[][] buffer) {
        drawBorder(buffer);

        // Draw title if present
        if (title != null && !title.isEmpty() && width > 2) {
            String titleDisplay = "[ " + title + " ]";
            int titleX = getX() + (width - titleDisplay.length()) / 2;
            writeStringToBuffer(buffer, titleDisplay, titleX, getY());
        }

        super.paint(buffer);
    }

    // DraggableWindow interface implementation

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    /**
     * Set whether this dialog can be dragged.
     *
     * @param draggable true to enable dragging, false to disable
     */
    public void setDraggable(boolean draggable) {
        renderLock.lock();
        try {
            this.draggable = draggable;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Set whether this dialog can be resized.
     *
     * @param resizable true to enable resizing, false to disable
     */
    public void setResizable(boolean resizable) {
        renderLock.lock();
        try {
            this.resizable = resizable;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * Set the minimum width for this dialog.
     *
     * @param minWidth the minimum width (clamped to at least 5)
     */
    public void setMinWidth(int minWidth) {
        renderLock.lock();
        try {
            this.minWidth = Math.max(5, minWidth);
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public int getMinHeight() {
        return minHeight;
    }

    /**
     * Set the minimum height for this dialog.
     *
     * @param minHeight the minimum height (clamped to at least 3)
     */
    public void setMinHeight(int minHeight) {
        renderLock.lock();
        try {
            this.minHeight = Math.max(3, minHeight);
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public boolean handleMouseEvent(MouseEvent event) {
        // Try drag/resize on dialog borders/title bar first
        if (WindowDragManager.getInstance().handleMouseEvent(event, this)) {
            return true;  // Consumed by drag operation
        }

        // Otherwise, delegate to children (existing Container behavior)
        return super.handleMouseEvent(event);
    }
}

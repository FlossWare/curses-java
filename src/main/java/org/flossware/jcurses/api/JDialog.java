package org.flossware.jcurses.api;

import java.util.SequencedCollection;
import java.util.LinkedHashSet;

/**
 * A dialog window component with optional title and status bar support.
 *
 * <p>Dialogs can be modal or non-modal and appear on top of other windows.
 * They support an optional title bar and status bar for better user experience.
 *
 * @since 1.0
 */
public class JDialog extends Container {
    private boolean modal = true;
    private String title = "";
    private JStatusBar statusBar = null;

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
}

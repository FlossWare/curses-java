package org.flossware.jcurses.api;

/**
 * An indeterminate progress indicator (goalless meter) that shows ongoing activity
 * without a specific completion percentage. Useful for operations of unknown duration.
 *
 * <p>The indicator displays an animated moving block that bounces back and forth
 * across the width of the component, giving visual feedback that work is in progress.
 *
 * <p>Call {@link #start()} to begin animation and {@link #stop()} to end it.
 * Animation state is tracked internally and can be updated by calling {@link #tick()}.
 *
 * @since 1.13
 */
public class JIndeterminateProgress extends Component {
    private boolean active = false;
    private int position = 0;
    private int direction = 1;  // 1 = right, -1 = left
    private int blockSize = 3;  // Size of the moving block

    private final char fillChar = '=';
    private final char emptyChar = ' ';

    /**
     * Start the indeterminate progress animation.
     * Call {@link #tick()} periodically to update the animation.
     */
    public void start() {
        renderLock.lock();
        try {
            active = true;
            position = 0;
            direction = 1;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Stop the indeterminate progress animation.
     */
    public void stop() {
        renderLock.lock();
        try {
            active = false;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Check if the progress indicator is currently active/animating.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the size of the moving block.
     *
     * @param size the block size in characters (default: 3)
     */
    public void setBlockSize(int size) {
        renderLock.lock();
        try {
            this.blockSize = Math.max(1, Math.min(size, width / 2));
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get the current block size.
     *
     * @return the block size in characters
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Advance the animation by one step.
     * Call this method periodically (e.g., every 100-200ms) to animate the progress indicator.
     */
    public void tick() {
        if (!active || width <= 0) {
            return;
        }

        renderLock.lock();
        try {
            // Move the position
            position += direction;

            // Bounce at edges
            if (position + blockSize >= width) {
                position = width - blockSize;
                direction = -1;
            } else if (position <= 0) {
                position = 0;
                direction = 1;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        if (width <= 0) return;

        if (!active) {
            // When inactive, show empty bar
            for (int i = 0; i < width; i++) {
                buffer[getY()][getX() + i] = emptyChar;
            }
            return;
        }

        // Draw the animated progress bar
        for (int i = 0; i < width; i++) {
            if (i >= position && i < position + blockSize) {
                buffer[getY()][getX() + i] = fillChar;
            } else {
                buffer[getY()][getX() + i] = emptyChar;
            }
        }
    }
}

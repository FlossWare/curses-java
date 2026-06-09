package org.flossware.curses.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for proper bounds checking in widget rendering to prevent ArrayIndexOutOfBoundsException.
 * Verifies that widgets gracefully handle situations where they exceed terminal boundaries.
 *
 * @since 1.28
 * @see Issue #64
 */
@DisplayName("Bounds Checking Tests - Issue #64")
class BoundsCheckingTest {

    @Test
    @DisplayName("ProgressBar should not crash when positioned beyond buffer bounds")
    void testProgressBarOutOfBounds() {
        int bufferWidth = 80;
        int bufferHeight = 24;
        char[][] buffer = new char[bufferHeight][bufferWidth];

        // Initialize buffer with spaces
        for (int y = 0; y < bufferHeight; y++) {
            for (int x = 0; x < bufferWidth; x++) {
                buffer[y][x] = ' ';
            }
        }

        // Test 1: ProgressBar positioned beyond buffer height
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(0, 100);  // y is beyond buffer
        progressBar.setSize(10, 1);
        progressBar.setPercent(0.5);

        // Should not throw ArrayIndexOutOfBoundsException
        assertDoesNotThrow(() -> progressBar.paint(buffer));

        // Test 2: ProgressBar positioned with x beyond buffer width
        progressBar.setLocation(200, 0);  // x is beyond buffer
        progressBar.setSize(10, 1);

        assertDoesNotThrow(() -> progressBar.paint(buffer));

        // Test 3: ProgressBar partially extends beyond buffer
        progressBar.setLocation(75, 0);  // x + width extends beyond buffer
        progressBar.setSize(10, 1);

        assertDoesNotThrow(() -> progressBar.paint(buffer));
    }

    @Test
    @DisplayName("IndeterminateProgress should not crash when positioned beyond buffer bounds")
    void testIndeterminateProgressOutOfBounds() {
        int bufferWidth = 80;
        int bufferHeight = 24;
        char[][] buffer = new char[bufferHeight][bufferWidth];

        // Initialize buffer with spaces
        for (int y = 0; y < bufferHeight; y++) {
            for (int x = 0; x < bufferWidth; x++) {
                buffer[y][x] = ' ';
            }
        }

        // Test 1: IndeterminateProgress positioned beyond buffer height
        IndeterminateProgress progress = new IndeterminateProgress();
        progress.setLocation(0, 100);  // y is beyond buffer
        progress.setSize(10, 1);
        progress.start();
        progress.tick();

        // Should not throw ArrayIndexOutOfBoundsException
        assertDoesNotThrow(() -> progress.paint(buffer));

        // Test 2: IndeterminateProgress positioned with x beyond buffer width
        progress.setLocation(200, 0);  // x is beyond buffer
        progress.setSize(10, 1);

        assertDoesNotThrow(() -> progress.paint(buffer));

        // Test 3: IndeterminateProgress partially extends beyond buffer
        progress.setLocation(75, 0);  // x + width extends beyond buffer
        progress.setSize(10, 1);

        assertDoesNotThrow(() -> progress.paint(buffer));

        // Test 4: Inactive progress should also handle bounds correctly
        progress.stop();
        assertDoesNotThrow(() -> progress.paint(buffer));
    }

    @Test
    @DisplayName("Widgets with negative positions should not crash")
    void testNegativePositions() {
        int bufferWidth = 80;
        int bufferHeight = 24;
        char[][] buffer = new char[bufferHeight][bufferWidth];

        // Initialize buffer
        for (int y = 0; y < bufferHeight; y++) {
            for (int x = 0; x < bufferWidth; x++) {
                buffer[y][x] = ' ';
            }
        }

        // ProgressBar with negative position
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(-10, -5);
        progressBar.setSize(10, 1);

        assertDoesNotThrow(() -> progressBar.paint(buffer));

        // IndeterminateProgress with negative position
        IndeterminateProgress progress = new IndeterminateProgress();
        progress.setLocation(-10, -5);
        progress.setSize(10, 1);
        progress.start();

        assertDoesNotThrow(() -> progress.paint(buffer));
    }

    @Test
    @DisplayName("Widgets should render correctly when fully within bounds")
    void testWidgetsWithinBounds() {
        int bufferWidth = 80;
        int bufferHeight = 24;
        char[][] buffer = new char[bufferHeight][bufferWidth];

        // Initialize buffer
        for (int y = 0; y < bufferHeight; y++) {
            for (int x = 0; x < bufferWidth; x++) {
                buffer[y][x] = ' ';
            }
        }

        // ProgressBar fully within bounds
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(10, 5);
        progressBar.setSize(40, 1);
        progressBar.setPercent(0.75);

        assertDoesNotThrow(() -> progressBar.paint(buffer));
        // Verify some characters were written
        boolean foundFilled = false;
        for (int i = 10; i < 40; i++) {
            if (buffer[5][i] == '#') {
                foundFilled = true;
                break;
            }
        }
        assertTrue(foundFilled, "ProgressBar should have rendered filled characters");

        // IndeterminateProgress fully within bounds
        IndeterminateProgress progress = new IndeterminateProgress();
        progress.setLocation(10, 10);
        progress.setSize(40, 1);
        progress.start();
        progress.tick();

        assertDoesNotThrow(() -> progress.paint(buffer));
    }

    @Test
    @DisplayName("Terminal resize scenario - widget larger than new terminal size")
    void testTerminalResize() {
        // Simulate initial terminal size
        int initialWidth = 120;
        int initialHeight = 40;
        char[][] buffer1 = new char[initialHeight][initialWidth];

        // Create widget that fits in initial size
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(100, 35);
        progressBar.setSize(15, 1);
        progressBar.setPercent(0.5);

        assertDoesNotThrow(() -> progressBar.paint(buffer1));

        // Simulate terminal resize to smaller size
        int newWidth = 80;
        int newHeight = 24;
        char[][] buffer2 = new char[newHeight][newWidth];

        // Widget position/size haven't changed, but buffer is smaller
        // Should handle gracefully without crashing
        assertDoesNotThrow(() -> progressBar.paint(buffer2));
    }

    @Test
    @DisplayName("Component.writeCharToBuffer should handle all boundary conditions")
    void testWriteCharToBuffer() {
        int width = 10;
        int height = 5;
        char[][] buffer = new char[height][width];

        // Initialize buffer
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = ' ';
            }
        }

        // Create a test component to access protected method
        Button testComponent = new Button("test");

        // Valid position
        assertDoesNotThrow(() -> testComponent.writeCharToBuffer(buffer, 0, 0, 'X'));
        assertEquals('X', buffer[0][0]);

        // Out of bounds - negative x
        assertDoesNotThrow(() -> testComponent.writeCharToBuffer(buffer, -1, 0, 'A'));

        // Out of bounds - negative y
        assertDoesNotThrow(() -> testComponent.writeCharToBuffer(buffer, 0, -1, 'B'));

        // Out of bounds - x beyond width
        assertDoesNotThrow(() -> testComponent.writeCharToBuffer(buffer, width, 0, 'C'));

        // Out of bounds - y beyond height
        assertDoesNotThrow(() -> testComponent.writeCharToBuffer(buffer, 0, height, 'D'));

        // Edge case - last valid position
        assertDoesNotThrow(() -> testComponent.writeCharToBuffer(buffer, width - 1, height - 1, 'Z'));
        assertEquals('Z', buffer[height - 1][width - 1]);
    }
}

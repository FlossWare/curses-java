package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JIndeterminateProgress Tests")
class JIndeterminateProgressTest extends ComponentTestBase {

    private JIndeterminateProgress progress;

    @BeforeEach
    void setUp() {
        progress = new JIndeterminateProgress();
        progress.setLocation(5, 5);
        progress.setSize(20, 1);
    }

    @Test
    @DisplayName("should start inactive")
    void testStartsInactive() {
        assertFalse(progress.isActive());
    }

    @Test
    @DisplayName("should activate when started")
    void testStartActivates() {
        progress.start();
        assertTrue(progress.isActive());
    }

    @Test
    @DisplayName("should deactivate when stopped")
    void testStopDeactivates() {
        progress.start();
        progress.stop();
        assertFalse(progress.isActive());
    }

    @Test
    @DisplayName("should have default block size of 3")
    void testDefaultBlockSize() {
        assertEquals(3, progress.getBlockSize());
    }

    @Test
    @DisplayName("should allow setting block size")
    void testSetBlockSize() {
        progress.setBlockSize(5);
        assertEquals(5, progress.getBlockSize());
    }

    @Test
    @DisplayName("should clamp block size to minimum 1")
    void testBlockSizeMinimum() {
        progress.setBlockSize(0);
        assertEquals(1, progress.getBlockSize());
    }

    @Test
    @DisplayName("should clamp block size to half of width")
    void testBlockSizeMaximum() {
        progress.setBlockSize(15);  // width is 20, so max should be 10
        assertEquals(10, progress.getBlockSize());
    }

    @Test
    @DisplayName("should animate position when ticking while active")
    void testTickAnimatesWhenActive() {
        progress.start();

        // Render initial state
        char[][] buffer = createBuffer();
        progress.paint(buffer);

        // Tick and check that something changed
        progress.tick();
        char[][] buffer2 = createBuffer();
        progress.paint(buffer2);

        // At least one position should have changed
        boolean changed = false;
        for (int x = 0; x < 20; x++) {
            if (buffer[5][5 + x] != buffer2[5][5 + x]) {
                changed = true;
                break;
            }
        }
        assertTrue(changed, "Animation should change the display");
    }

    @Test
    @DisplayName("should not animate when inactive")
    void testNoAnimationWhenInactive() {
        // Don't start, just tick
        char[][] buffer1 = createBuffer();
        progress.paint(buffer1);

        progress.tick();

        char[][] buffer2 = createBuffer();
        progress.paint(buffer2);

        // Should be identical (all empty)
        for (int x = 0; x < 20; x++) {
            assertEquals(buffer1[5][5 + x], buffer2[5][5 + x]);
        }
    }

    @Test
    @DisplayName("should show empty bar when inactive")
    void testShowsEmptyWhenInactive() {
        char[][] buffer = createBuffer();
        progress.paint(buffer);

        // All positions should be empty (space character)
        for (int x = 0; x < 20; x++) {
            assertEquals(' ', buffer[5][5 + x]);
        }
    }

    @Test
    @DisplayName("should show moving block when active")
    void testShowsBlockWhenActive() {
        progress.start();
        char[][] buffer = createBuffer();
        progress.paint(buffer);

        // Should have some filled characters (=) for the block
        int filledCount = 0;
        for (int x = 0; x < 20; x++) {
            if (buffer[5][5 + x] == '=') {
                filledCount++;
            }
        }

        assertEquals(3, filledCount, "Should have 3 filled characters for default block size");
    }

    @Test
    @DisplayName("should bounce at right edge")
    void testBouncesAtRightEdge() {
        progress.start();

        // Tick many times to reach the right edge
        for (int i = 0; i < 50; i++) {
            progress.tick();
        }

        // Should still be active and displaying
        assertTrue(progress.isActive());

        char[][] buffer = createBuffer();
        progress.paint(buffer);

        // Should have filled characters somewhere (proving it bounced back)
        int filledCount = 0;
        for (int x = 0; x < 20; x++) {
            if (buffer[5][5 + x] == '=') {
                filledCount++;
            }
        }

        assertTrue(filledCount > 0, "Should still have filled characters after bouncing");
    }

    @Test
    @DisplayName("should handle zero width gracefully")
    void testZeroWidth() {
        progress.setSize(0, 1);
        progress.start();

        assertDoesNotThrow(() -> progress.tick());
        assertDoesNotThrow(() -> progress.paint(createBuffer()));
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        progress.start();

        // Run multiple threads ticking and checking state
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                progress.tick();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                progress.isActive();
                char[][] buffer = createBuffer();
                progress.paint(buffer);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Should still be active
        assertTrue(progress.isActive());
    }

    private char[][] createBuffer() {
        char[][] buffer = new char[20][40];
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 40; x++) {
                buffer[y][x] = ' ';
            }
        }
        return buffer;
    }
}

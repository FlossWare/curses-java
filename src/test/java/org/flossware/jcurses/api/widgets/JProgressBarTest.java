package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JProgressBar;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JProgressBar Tests")
class JProgressBarTest extends ComponentTestBase {
    private JProgressBar progressBar;

    @BeforeEach
    void setUp() {
        progressBar = new JProgressBar();
        progressBar.setSize(20, 1);
        progressBar.setLocation(0, 0);
    }

    @Test
    @DisplayName("should initialize with 0% progress")
    void testInitialization() {
        assertEquals(0.0, progressBar.getPercent());
    }

    @Test
    @DisplayName("should set and get percent")
    void testSetPercent() {
        progressBar.setPercent(0.5);
        assertEquals(0.5, progressBar.getPercent());
    }

    @Test
    @DisplayName("should clamp percent to 0.0-1.0 range")
    void testPercentClamping() {
        progressBar.setPercent(1.5);
        assertEquals(1.0, progressBar.getPercent());

        progressBar.setPercent(-0.5);
        assertEquals(0.0, progressBar.getPercent());

        progressBar.setPercent(0.5);
        assertEquals(0.5, progressBar.getPercent());
    }

    @Test
    @DisplayName("should render 0% progress correctly")
    void testZeroPercentRendering() {
        progressBar.setPercent(0.0);
        progressBar.paint(buffer);

        // All should be empty chars
        for (int i = 0; i < 20; i++) {
            assertEquals('.', buffer[0][i]);
        }
    }

    @Test
    @DisplayName("should render 100% progress correctly")
    void testFullPercentRendering() {
        progressBar.setPercent(1.0);
        progressBar.paint(buffer);

        // All should be fill chars
        for (int i = 0; i < 20; i++) {
            assertEquals('#', buffer[0][i]);
        }
    }

    @Test
    @DisplayName("should render 50% progress correctly")
    void testHalfPercentRendering() {
        progressBar.setPercent(0.5);
        progressBar.paint(buffer);

        // First 10 should be filled, last 10 empty
        for (int i = 0; i < 10; i++) {
            assertEquals('#', buffer[0][i], "Position " + i + " should be filled");
        }
        for (int i = 10; i < 20; i++) {
            assertEquals('.', buffer[0][i], "Position " + i + " should be empty");
        }
    }

    @Test
    @DisplayName("should trigger repaint when percent changes")
    void testRepaintOnPercentChange() {
        root.add(progressBar);
        clearDirtyFlag();

        progressBar.setPercent(0.75);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should handle exact boundary values")
    void testBoundaryValues() {
        progressBar.setPercent(0.0);
        assertEquals(0.0, progressBar.getPercent());

        progressBar.setPercent(1.0);
        assertEquals(1.0, progressBar.getPercent());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent percent updates")
    void testConcurrentPercentUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i <= 100; i++) {
                progressBar.setPercent(i / 100.0);
            }
        });

        assertTrue(progressBar.getPercent() >= 0.0);
        assertTrue(progressBar.getPercent() <= 1.0);
    }

    @Test
    @DisplayName("should support Virtual Threads")
    void testVirtualThreadSupport() throws InterruptedException {
        root.add(progressBar);

        ThreadSafetyTestHelper.runWithVirtualThreads(20, () -> {
            progressBar.setPercent(Math.random());
            progressBar.paint(buffer);
        });

        assertTrue(root.isDirty());
    }

    @Test
    @DisplayName("should handle various width values")
    void testVariousWidths() {
        progressBar.setSize(10, 1);
        progressBar.setPercent(0.5);
        assertDoesNotThrow(() -> progressBar.paint(buffer));

        progressBar.setSize(100, 1);
        progressBar.setPercent(0.5);
        assertDoesNotThrow(() -> progressBar.paint(buffer));

        progressBar.setSize(1, 1);
        progressBar.setPercent(0.5);
        assertDoesNotThrow(() -> progressBar.paint(buffer));
    }
}

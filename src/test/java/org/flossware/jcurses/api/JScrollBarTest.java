package org.flossware.jcurses.api;

import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for JScrollBar class.
 */
@DisplayName("JScrollBar Comprehensive Tests")
class JScrollBarTest extends ComponentTestBase {

    @Test
    @DisplayName("Constructor should create horizontal scrollbar")
    void testConstructorHorizontal() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        assertNotNull(scrollBar);
        assertEquals(0, scrollBar.getValue());
    }

    @Test
    @DisplayName("Constructor should create vertical scrollbar")
    void testConstructorVertical() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        assertNotNull(scrollBar);
        assertEquals(0, scrollBar.getValue());
    }

    @Test
    @DisplayName("setValue should set value within range")
    void testSetValue() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);

        scrollBar.setValue(50);
        assertEquals(50, scrollBar.getValue());
    }

    @Test
    @DisplayName("setValue should clamp to minimum")
    void testSetValueClampMinimum() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(10);
        scrollBar.setMaximum(100);

        scrollBar.setValue(5);
        assertEquals(10, scrollBar.getValue());
    }

    @Test
    @DisplayName("setValue should clamp to maximum minus visible amount")
    void testSetValueClampMaximum() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(100);
        scrollBar.setVisibleAmount(10);

        scrollBar.setValue(95);
        assertEquals(90, scrollBar.getValue());
    }

    @Test
    @DisplayName("setMinimum should update minimum")
    void testSetMinimum() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);

        scrollBar.setMinimum(20);
        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("setMaximum should update maximum")
    void testSetMaximum() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);

        scrollBar.setMaximum(200);
        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("setVisibleAmount should update visible amount")
    void testSetVisibleAmount() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);

        scrollBar.setVisibleAmount(20);
        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("paint should render horizontal scrollbar")
    void testPaintHorizontal() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBar.setSize(20, 1);
        scrollBar.setLocation(0, 0);
        scrollBar.setValue(50);

        scrollBar.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("#"), "Should contain thumb indicator");
        assertTrue(row.contains("."), "Should contain track");
    }

    @Test
    @DisplayName("paint should render vertical scrollbar")
    void testPaintVertical() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setValue(50);

        scrollBar.paint(buffer);

        boolean hasThumb = false;
        boolean hasTrack = false;
        for (int i = 0; i < 20; i++) {
            char c = buffer[i][0];
            if (c == '#') hasThumb = true;
            if (c == '.') hasTrack = true;
        }

        assertTrue(hasThumb, "Should contain thumb indicator");
        assertTrue(hasTrack, "Should contain track");
    }

    @Test
    @DisplayName("paint horizontal should handle zero range")
    void testPaintHorizontalZeroRange() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBar.setSize(20, 1);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(50);
        scrollBar.setMaximum(50);

        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("paint vertical should handle zero range")
    void testPaintVerticalZeroRange() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(50);
        scrollBar.setMaximum(50);

        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("paint horizontal should handle small width")
    void testPaintHorizontalSmallWidth() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBar.setSize(1, 1);
        scrollBar.setLocation(0, 0);

        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("paint vertical should handle small height")
    void testPaintVerticalSmallHeight() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 1);
        scrollBar.setLocation(0, 0);

        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("thumb position should change with value")
    void testThumbPositionChanges() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBar.setSize(20, 1);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(100);
        scrollBar.setVisibleAmount(10);

        // Value at 0
        scrollBar.setValue(0);
        scrollBar.paint(buffer);
        String row1 = new String(buffer[0]);
        int thumbPos1 = row1.indexOf('#');

        // Clear buffer
        buffer = new char[80][24];

        // Value at 50
        scrollBar.setValue(50);
        scrollBar.paint(buffer);
        String row2 = new String(buffer[0]);
        int thumbPos2 = row2.indexOf('#');

        assertTrue(thumbPos2 > thumbPos1, "Thumb should move right as value increases");
    }

    @Test
    @DisplayName("vertical scrollbar thumb should move with value")
    void testVerticalThumbPositionChanges() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(100);
        scrollBar.setVisibleAmount(10);

        // Value at 0
        scrollBar.setValue(0);
        scrollBar.paint(buffer);
        int thumbPos1 = -1;
        for (int i = 0; i < 20; i++) {
            if (buffer[i][0] == '#') {
                thumbPos1 = i;
                break;
            }
        }

        // Clear buffer
        buffer = new char[80][24];

        // Value at 50
        scrollBar.setValue(50);
        scrollBar.paint(buffer);
        int thumbPos2 = -1;
        for (int i = 0; i < 20; i++) {
            if (buffer[i][0] == '#') {
                thumbPos2 = i;
                break;
            }
        }

        assertTrue(thumbPos2 > thumbPos1, "Thumb should move down as value increases");
    }

    @Test
    @DisplayName("should handle value at boundaries")
    void testValueAtBoundaries() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(100);
        scrollBar.setVisibleAmount(10);

        // Minimum
        scrollBar.setValue(0);
        assertEquals(0, scrollBar.getValue());
        assertDoesNotThrow(() -> scrollBar.paint(buffer));

        // Maximum (clamped to max - visibleAmount)
        scrollBar.setValue(100);
        assertEquals(90, scrollBar.getValue());
        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    scrollBar.setValue(threadId * 10 + j);
                    scrollBar.setMinimum(threadId);
                    scrollBar.setMaximum(100 + threadId);
                    scrollBar.setVisibleAmount(10 + threadId);
                    scrollBar.paint(buffer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(scrollBar.getValue() >= 0);
    }

    @Test
    @DisplayName("Constants should have expected values")
    void testConstants() {
        assertEquals(0, JScrollBar.HORIZONTAL);
        assertEquals(1, JScrollBar.VERTICAL);
    }

    @Test
    @DisplayName("should handle negative range gracefully")
    void testNegativeRange() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(100);
        scrollBar.setMaximum(50);  // max < min

        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }

    @Test
    @DisplayName("should handle large visible amount")
    void testLargeVisibleAmount() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setSize(1, 20);
        scrollBar.setLocation(0, 0);
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(100);
        scrollBar.setVisibleAmount(200);  // Larger than range

        assertDoesNotThrow(() -> scrollBar.paint(buffer));
    }
}

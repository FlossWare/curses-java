package org.flossware.curses.api;

import org.flossware.curses.testutil.BufferAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional coverage tests for ScrollBar.
 * Targets: getMinimum, getMaximum, getVisibleAmount return values,
 * setMaximum with negative value, paint edge cases.
 */
@DisplayName("ScrollBar Coverage Tests")
class ScrollBarCoverageTest {

    @Test
    @DisplayName("should return minimum value")
    void testGetMinimum() {
        ScrollBar sb = new ScrollBar(ScrollBar.HORIZONTAL);
        sb.setMinimum(5);
        assertEquals(5, sb.getMinimum());
    }

    @Test
    @DisplayName("should return maximum value")
    void testGetMaximum() {
        ScrollBar sb = new ScrollBar(ScrollBar.HORIZONTAL);
        sb.setMaximum(200);
        assertEquals(200, sb.getMaximum());
    }

    @Test
    @DisplayName("should return visible amount")
    void testGetVisibleAmount() {
        ScrollBar sb = new ScrollBar(ScrollBar.VERTICAL);
        sb.setVisibleAmount(25);
        assertEquals(25, sb.getVisibleAmount());
    }

    @Test
    @DisplayName("should clamp negative maximum to zero")
    void testSetNegativeMaximum() {
        ScrollBar sb = new ScrollBar(ScrollBar.HORIZONTAL);
        sb.setMaximum(-10);
        assertEquals(0, sb.getMaximum());
    }

    @Test
    @DisplayName("should clamp value when maximum reduced below value")
    void testSetMaximumClampsValue() {
        ScrollBar sb = new ScrollBar(ScrollBar.HORIZONTAL);
        sb.setMaximum(100);
        sb.setValue(80);
        sb.setMaximum(50);
        // Value should be clamped to new maximum
        assertTrue(sb.getValue() <= 50);
    }

    @Test
    @DisplayName("should paint vertical scrollbar with width 3")
    void testPaintVerticalNarrow() {
        ScrollBar sb = new ScrollBar(ScrollBar.VERTICAL);
        sb.setLocation(0, 0);
        sb.setSize(1, 3);
        sb.setMaximum(100);
        sb.setVisibleAmount(10);
        sb.setValue(0);

        char[][] buffer = BufferAssertions.createBuffer(10, 10);
        assertDoesNotThrow(() -> sb.paint(buffer));
    }

    @Test
    @DisplayName("should paint horizontal scrollbar at specific position")
    void testPaintHorizontalWithValue() {
        ScrollBar sb = new ScrollBar(ScrollBar.HORIZONTAL);
        sb.setLocation(0, 0);
        sb.setSize(20, 1);
        sb.setMaximum(100);
        sb.setVisibleAmount(10);
        sb.setValue(50);

        char[][] buffer = BufferAssertions.createBuffer(30, 5);
        assertDoesNotThrow(() -> sb.paint(buffer));
    }
}

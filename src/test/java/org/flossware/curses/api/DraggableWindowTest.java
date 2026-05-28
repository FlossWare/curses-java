package org.flossware.curses.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DraggableWindow Interface Tests")
class DraggableWindowTest {

    /**
     * Minimal implementation of DraggableWindow that uses all default methods.
     * This allows us to test the interface's default behavior.
     */
    private static class MinimalDraggableWindow implements DraggableWindow {
        // Uses all default methods from interface
    }

    @Test
    @DisplayName("should have draggable default to true")
    void testDefaultIsDraggable() {
        DraggableWindow window = new MinimalDraggableWindow();
        assertTrue(window.isDraggable());
    }

    @Test
    @DisplayName("should have resizable default to true")
    void testDefaultIsResizable() {
        DraggableWindow window = new MinimalDraggableWindow();
        assertTrue(window.isResizable());
    }

    @Test
    @DisplayName("should have default min width of 10")
    void testDefaultMinWidth() {
        DraggableWindow window = new MinimalDraggableWindow();
        assertEquals(10, window.getMinWidth());
    }

    @Test
    @DisplayName("should have default min height of 3")
    void testDefaultMinHeight() {
        DraggableWindow window = new MinimalDraggableWindow();
        assertEquals(3, window.getMinHeight());
    }

    @Test
    @DisplayName("should have default max width of 0 (unlimited)")
    void testDefaultMaxWidth() {
        DraggableWindow window = new MinimalDraggableWindow();
        assertEquals(0, window.getMaxWidth());
    }

    @Test
    @DisplayName("should have default max height of 0 (unlimited)")
    void testDefaultMaxHeight() {
        DraggableWindow window = new MinimalDraggableWindow();
        assertEquals(0, window.getMaxHeight());
    }
}

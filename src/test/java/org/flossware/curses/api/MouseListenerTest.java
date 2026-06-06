package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MouseListener Tests")
class MouseListenerTest extends ComponentTestBase {
    private Button button;

    @BeforeEach
    void setUp() {
        button = new Button("Test Button");
        button.setLocation(10, 10);
        button.setSize(15, 1);
    }

    @Test
    @DisplayName("should add mouse listener")
    void testAddMouseListener() {
        AtomicBoolean called = new AtomicBoolean(false);

        button.addMouseListener(e -> called.set(true));

        MouseEvent event = new MouseEvent(12, 10, 1);
        button.handleMouseEvent(event);

        assertTrue(called.get());
    }

    @Test
    @DisplayName("should remove mouse listener")
    void testRemoveMouseListener() {
        AtomicBoolean called = new AtomicBoolean(false);
        MouseListener listener = e -> called.set(true);

        button.addMouseListener(listener);
        button.removeMouseListener(listener);

        MouseEvent event = new MouseEvent(12, 10, 1);
        button.handleMouseEvent(event);

        assertFalse(called.get());
    }

    @Test
    @DisplayName("should handle mouse event within bounds")
    void testHandleMouseEventWithinBounds() {
        AtomicReference<MouseEvent> capturedEvent = new AtomicReference<>();

        button.addMouseListener(capturedEvent::set);

        MouseEvent event = new MouseEvent(12, 10, 1);
        boolean handled = button.handleMouseEvent(event);

        assertTrue(handled);
        assertNotNull(capturedEvent.get());
        assertEquals(12, capturedEvent.get().x());
        assertEquals(10, capturedEvent.get().y());
    }

    @Test
    @DisplayName("should not handle mouse event outside bounds")
    void testHandleMouseEventOutsideBounds() {
        AtomicBoolean called = new AtomicBoolean(false);

        button.addMouseListener(e -> called.set(true));

        MouseEvent event = new MouseEvent(5, 5, 1);  // Outside button bounds
        boolean handled = button.handleMouseEvent(event);

        assertFalse(handled);
        assertFalse(called.get());
    }

    @Test
    @DisplayName("should handle multiple mouse listeners")
    void testMultipleMouseListeners() {
        AtomicBoolean called1 = new AtomicBoolean(false);
        AtomicBoolean called2 = new AtomicBoolean(false);

        button.addMouseListener(e -> called1.set(true));
        button.addMouseListener(e -> called2.set(true));

        MouseEvent event = new MouseEvent(12, 10, 1);
        button.handleMouseEvent(event);

        assertTrue(called1.get());
        assertTrue(called2.get());
    }

    @Test
    @DisplayName("should propagate mouse event to children in container")
    void testMouseEventPropagationToChildren() {
        Panel panel = new Panel();
        panel.setLocation(0, 0);
        panel.setSize(50, 20);

        AtomicBoolean buttonClicked = new AtomicBoolean(false);
        button.addMouseListener(e -> buttonClicked.set(true));

        panel.add(button);

        MouseEvent event = new MouseEvent(12, 10, 1);
        boolean handled = panel.handleMouseEvent(event);

        assertTrue(handled);
        assertTrue(buttonClicked.get());
    }

    @Test
    @DisplayName("should stop propagation when child handles event")
    void testEventPropagationStopsWhenHandled() {
        Panel panel = new Panel();
        panel.setLocation(0, 0);
        panel.setSize(50, 20);

        AtomicBoolean panelClicked = new AtomicBoolean(false);
        AtomicBoolean buttonClicked = new AtomicBoolean(false);

        button.addMouseListener(e -> buttonClicked.set(true));
        panel.addMouseListener(e -> panelClicked.set(true));

        panel.add(button);

        MouseEvent event = new MouseEvent(12, 10, 1);  // Within button bounds
        panel.handleMouseEvent(event);

        assertTrue(buttonClicked.get());
        assertFalse(panelClicked.get());  // Panel listener should not be called
    }
}

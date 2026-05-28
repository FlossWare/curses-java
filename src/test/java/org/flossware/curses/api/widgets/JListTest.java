package org.flossware.curses.api.widgets;

import org.flossware.curses.api.JList;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JList Tests")
class JListTest extends ComponentTestBase {
    private JList widget;

    @BeforeEach
    void setUp() {
        widget = new JList();
        widget.setSize(20, 5);
        widget.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(widget);
    }

    @Test
    @DisplayName("should render without errors")
    void testRendering() {
        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should be added to parent")
    void testAddToParent() {
        root.add(widget);
        assertTrue(root.getChildren().contains(widget));
    }
}

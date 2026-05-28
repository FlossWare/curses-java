package org.flossware.curses.api.widgets;

import org.flossware.curses.api.*;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JSplitPane Tests")
class JSplitPaneTest extends ComponentTestBase {
    private JSplitPane widget;

    @BeforeEach
    void setUp() {
        Component left = new JLabel("Left");
        Component right = new JLabel("Right");
        widget = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        widget.setSize(40, 10);
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
}

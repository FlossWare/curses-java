package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JMenuItem;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JMenuItem Tests")
class JMenuItemTest extends ComponentTestBase {
    private JMenuItem widget;

    @BeforeEach
    void setUp() {
        widget = new JMenuItem("Menu Item");
        widget.setSize(20, 1);
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

package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JScrollBar;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JScrollBar Tests")
class JScrollBarTest extends ComponentTestBase {
    private JScrollBar widget;

    @BeforeEach
    void setUp() {
        widget = new JScrollBar(JScrollBar.VERTICAL);
        widget.setSize(2, 20);
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

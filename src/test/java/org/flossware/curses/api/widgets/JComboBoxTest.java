package org.flossware.curses.api.widgets;

import org.flossware.curses.api.JComboBox;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JComboBox Tests")
class JComboBoxTest extends ComponentTestBase {
    private JComboBox<String> widget;

    @BeforeEach
    void setUp() {
        widget = new JComboBox<>();
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
}

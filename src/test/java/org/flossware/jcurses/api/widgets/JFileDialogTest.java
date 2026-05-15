package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JFileDialog;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JFileDialog Tests")
class JFileDialogTest extends ComponentTestBase {
    private JFileDialog widget;

    @BeforeEach
    void setUp() {
        widget = new JFileDialog("Select File", JFileDialog.LOAD);
        widget.setSize(40, 20);
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

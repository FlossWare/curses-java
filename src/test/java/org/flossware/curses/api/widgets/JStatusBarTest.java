package org.flossware.curses.api.widgets;

import org.flossware.curses.api.StatusBar;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusBar Tests")
class JStatusBarTest extends ComponentTestBase {
    private StatusBar widget;

    @BeforeEach
    void setUp() {
        widget = new StatusBar();
        widget.setSize(20, 1);
        widget.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(widget);
        assertEquals("", widget.getText());
    }

    @Test
    @DisplayName("should create with initial text")
    void testCreationWithText() {
        StatusBar statusBar = new StatusBar("Ready");
        assertEquals("Ready", statusBar.getText());
    }

    @Test
    @DisplayName("should set and get text")
    void testSetGetText() {
        widget.setText("Processing...");
        assertEquals("Processing...", widget.getText());

        widget.setText("Done");
        assertEquals("Done", widget.getText());
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

    @Test
    @DisplayName("should render text in buffer")
    void testRenderText() {
        widget.setText("Status");
        widget.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("Status"));
    }

    @Test
    @DisplayName("should truncate long text to width")
    void testTruncateLongText() {
        widget.setSize(10, 1);
        widget.setText("This is a very long status message");
        widget.paint(buffer);

        String row = new String(buffer[0], 0, 10);
        assertEquals("This is a ", row);
    }

    @Test
    @DisplayName("should handle null text")
    void testNullText() {
        widget.setText(null);
        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should handle empty text")
    void testEmptyText() {
        widget.setText("");
        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should handle zero width")
    void testZeroWidth() {
        widget.setSize(0, 1);
        widget.setText("Test");

        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should clear background before rendering text")
    void testClearBackground() {
        widget.setLocation(0, 0);
        widget.setSize(10, 1);
        widget.setText("Hi");
        widget.paint(buffer);

        // Positions beyond "Hi" should be spaces (cleared)
        for (int x = 2; x < 10; x++) {
            assertEquals(' ', buffer[0][x], "Position " + x + " should be cleared");
        }
    }

    @Test
    @DisplayName("should trigger repaint when text changes")
    void testRepaintOnTextChange() {
        root.add(widget);
        clearDirtyFlag();

        widget.setText("New text");

        assertDirtyFlagSet();
    }
}

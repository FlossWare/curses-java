package org.flossware.jcurses.api.containers;

import org.flossware.jcurses.api.JFrame;
import org.flossware.jcurses.api.JLabel;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JFrame Tests")
class JFrameTest extends ComponentTestBase {
    private JFrame frame;

    @BeforeEach
    void setUp() {
        frame = new JFrame("Test Frame");
    }

    @Test
    @DisplayName("should create frame with title")
    void testCreation() {
        assertEquals("Test Frame", frame.getTitle());
        assertFalse(frame.isVisible());
    }

    @Test
    @DisplayName("should set and get title")
    void testSetTitle() {
        frame.setTitle("New Title");
        assertEquals("New Title", frame.getTitle());
    }

    @Test
    @DisplayName("should set and get visible state")
    void testSetVisible() {
        frame.setVisible(true);
        assertTrue(frame.isVisible());

        frame.setVisible(false);
        assertFalse(frame.isVisible());
    }

    @Test
    @DisplayName("should add to RootPane when set visible")
    void testVisibleAddsToRoot() {
        int initialCount = root.getChildren().size();

        frame.setVisible(true);

        assertEquals(initialCount + 1, root.getChildren().size());
        assertTrue(root.getChildren().contains(frame));
    }

    @Test
    @DisplayName("should remove from RootPane when set invisible")
    void testInvisibleRemovesFromRoot() {
        frame.setVisible(true);
        frame.setVisible(false);

        assertFalse(root.getChildren().contains(frame));
    }

    @Test
    @DisplayName("should trigger repaint when title changes")
    void testRepaintOnTitleChange() {
        root.add(frame);
        clearDirtyFlag();

        frame.setTitle("Changed");

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should trigger repaint when visibility changes")
    void testRepaintOnVisibilityChange() {
        clearDirtyFlag();

        frame.setVisible(true);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should render with border and title")
    void testRendering() {
        frame.setLocation(0, 0);
        frame.setSize(30, 10);
        frame.setVisible(true);

        assertDoesNotThrow(() -> frame.paint(buffer));
    }

    @Test
    @DisplayName("should act as container for children")
    void testChildManagement() {
        JLabel child = new JLabel("Test");
        frame.add(child);

        assertEquals(1, frame.getChildren().size());
        assertTrue(frame.getChildren().contains(child));
    }
}

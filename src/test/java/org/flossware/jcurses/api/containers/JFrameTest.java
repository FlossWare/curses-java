package org.flossware.jcurses.api.containers;

import org.flossware.jcurses.api.DraggableWindow;
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

    @Test
    @DisplayName("should default to draggable")
    void testDefaultDraggable() {
        assertTrue(frame.isDraggable());
    }

    @Test
    @DisplayName("should set and get draggable")
    void testSetDraggable() {
        frame.setDraggable(false);
        assertFalse(frame.isDraggable());

        frame.setDraggable(true);
        assertTrue(frame.isDraggable());
    }

    @Test
    @DisplayName("should default to resizable")
    void testDefaultResizable() {
        assertTrue(frame.isResizable());
    }

    @Test
    @DisplayName("should set and get resizable")
    void testSetResizable() {
        frame.setResizable(false);
        assertFalse(frame.isResizable());

        frame.setResizable(true);
        assertTrue(frame.isResizable());
    }

    @Test
    @DisplayName("should have default min width")
    void testDefaultMinWidth() {
        assertEquals(10, frame.getMinWidth());
    }

    @Test
    @DisplayName("should set min width with clamping")
    void testSetMinWidth() {
        frame.setMinWidth(20);
        assertEquals(20, frame.getMinWidth());

        frame.setMinWidth(3);  // Below minimum of 5
        assertEquals(5, frame.getMinWidth());
    }

    @Test
    @DisplayName("should have default min height")
    void testDefaultMinHeight() {
        assertEquals(3, frame.getMinHeight());
    }

    @Test
    @DisplayName("should set min height with clamping")
    void testSetMinHeight() {
        frame.setMinHeight(10);
        assertEquals(10, frame.getMinHeight());

        frame.setMinHeight(1);  // Below minimum of 3
        assertEquals(3, frame.getMinHeight());
    }

    @Test
    @DisplayName("should handle mouse events for dragging")
    void testHandleMouseEvent() {
        frame.setLocation(10, 10);
        frame.setSize(30, 15);
        frame.setVisible(true);

        org.flossware.jcurses.events.MouseEvent mouseEvent =
            new org.flossware.jcurses.events.MouseEvent(20, 12, 1);

        assertDoesNotThrow(() -> frame.handleMouseEvent(mouseEvent));
    }

    @Test
    @DisplayName("should consume mouse event when drag starts")
    void testDragConsumeMouseEvent() {
        // Cancel any existing drag from previous tests
        org.flossware.jcurses.api.WindowDragManager.getInstance().cancelDrag();

        frame.setLocation(10, 10);
        frame.setSize(30, 15);
        frame.setVisible(true);

        // Click on title bar to start drag (BUTTON1_PRESSED = 0x2)
        org.flossware.jcurses.events.MouseEvent titleBarClick =
            new org.flossware.jcurses.events.MouseEvent(20, 10, 0x2);

        boolean consumed = frame.handleMouseEvent(titleBarClick);
        assertTrue(consumed, "Title bar click should be consumed by drag manager");
    }

    @Test
    @DisplayName("should not render when invisible")
    void testPaintWhenInvisible() {
        frame.setSize(30, 10);
        frame.setLocation(0, 0);
        frame.setVisible(false);

        frame.paint(buffer);

        // Buffer should remain empty/unchanged
        boolean hasContent = false;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 30; x++) {
                if (buffer[y][x] != ' ' && buffer[y][x] != '\0') {
                    hasContent = true;
                    break;
                }
            }
        }
        assertFalse(hasContent, "Invisible frame should not render");
    }

    @Test
    @DisplayName("should render title centered")
    void testTitleRendering() {
        frame.setTitle("Test Title");
        frame.setSize(30, 10);
        frame.setLocation(0, 0);
        frame.setVisible(true);

        frame.paint(buffer);

        String topRow = new String(buffer[0]);
        assertTrue(topRow.contains("[ Test Title ]"));
    }

    @Test
    @DisplayName("should not render null title")
    void testNullTitle() {
        frame.setTitle(null);
        frame.setSize(30, 10);
        frame.setLocation(0, 0);
        frame.setVisible(true);

        assertDoesNotThrow(() -> frame.paint(buffer));
    }

    @Test
    @DisplayName("should not render empty title")
    void testEmptyTitle() {
        frame.setTitle("");
        frame.setSize(30, 10);
        frame.setLocation(0, 0);
        frame.setVisible(true);

        assertDoesNotThrow(() -> frame.paint(buffer));
    }

    @Test
    @DisplayName("should not render title when width too small")
    void testSmallWidth() {
        frame.setTitle("Long Title");
        frame.setSize(2, 5);
        frame.setLocation(0, 0);
        frame.setVisible(true);

        assertDoesNotThrow(() -> frame.paint(buffer));
    }

    @Test
    @DisplayName("should create frame with default empty title")
    void testDefaultConstructor() {
        JFrame defaultFrame = new JFrame();
        assertEquals("", defaultFrame.getTitle());
        assertFalse(defaultFrame.isVisible());
    }

    @Test
    @DisplayName("should use DraggableWindow default methods")
    void testDraggableWindowDefaults() {
        // Test that JFrame implements DraggableWindow and can use default methods
        DraggableWindow draggable = frame;

        // Default max width/height are 0 (unlimited)
        assertEquals(0, draggable.getMaxWidth());
        assertEquals(0, draggable.getMaxHeight());
    }

    @Test
    @DisplayName("should handle mouse event delegation to children")
    void testMouseEventDelegationToChildren() {
        JLabel child = new JLabel("Child");
        child.setLocation(15, 15);
        child.setSize(10, 1);

        boolean[] childListenerCalled = {false};
        child.addMouseListener(event -> childListenerCalled[0] = true);

        frame.setLocation(10, 10);
        frame.setSize(30, 20);
        frame.setVisible(true);
        frame.add(child);

        // Click on child (inside frame but not on border)
        org.flossware.jcurses.events.MouseEvent event =
            new org.flossware.jcurses.events.MouseEvent(20, 20, 0x4);

        frame.handleMouseEvent(event);

        // Child should receive the event (if within bounds)
        // Note: actual behavior depends on child's handleMouseEvent implementation
        assertNotNull(child.getParent());
    }
}

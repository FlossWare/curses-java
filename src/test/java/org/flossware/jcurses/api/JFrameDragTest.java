package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;
import org.flossware.jcurses.ffi.NcursesBridge;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JFrame Mouse Drag Integration Tests")
class JFrameDragTest extends ComponentTestBase {

    private JFrame frame;

    @BeforeEach
    void setUp() {
        frame = new JFrame("Test Frame");
        frame.setLocation(20, 10);
        frame.setSize(40, 20);
        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should move frame when dragging title bar")
    void testTitleBarDragMovesFrame() {
        int startX = frame.getX();
        int startY = frame.getY();

        // Start drag on title bar (top edge)
        MouseEvent press = new MouseEvent(30, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(press));

        // Continue drag (move 5 right, 3 down)
        MouseEvent move = new MouseEvent(35, 13, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(move));

        // Verify frame moved
        assertEquals(startX + 5, frame.getX());
        assertEquals(startY + 3, frame.getY());
        assertEquals(40, frame.getWidth());  // Size unchanged
        assertEquals(20, frame.getHeight());

        // End drag
        MouseEvent release = new MouseEvent(35, 13, (int) NcursesBridge.BUTTON1_RELEASED);
        assertTrue(frame.handleMouseEvent(release));
    }

    @Test
    @DisplayName("should resize frame width when dragging right edge")
    void testRightEdgeDragResizesWidth() {
        int startWidth = frame.getWidth();
        int startX = frame.getX();
        int startY = frame.getY();

        // Start drag on right edge (x = frame.x + width - 1)
        MouseEvent press = new MouseEvent(59, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(press));

        // Continue drag (move 7 right)
        MouseEvent move = new MouseEvent(66, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(move));

        // Verify width increased, position unchanged
        assertEquals(startX, frame.getX());
        assertEquals(startY, frame.getY());
        assertEquals(startWidth + 7, frame.getWidth());
        assertEquals(20, frame.getHeight());  // Height unchanged

        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should resize frame height when dragging bottom edge")
    void testBottomEdgeDragResizesHeight() {
        int startHeight = frame.getHeight();
        int startX = frame.getX();
        int startY = frame.getY();

        // Start drag on bottom edge (y = frame.y + height - 1)
        MouseEvent press = new MouseEvent(30, 29, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(press));

        // Continue drag (move 4 down)
        MouseEvent move = new MouseEvent(30, 33, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(move));

        // Verify height increased, position unchanged
        assertEquals(startX, frame.getX());
        assertEquals(startY, frame.getY());
        assertEquals(40, frame.getWidth());  // Width unchanged
        assertEquals(startHeight + 4, frame.getHeight());

        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should resize both dimensions when dragging bottom-right corner")
    void testCornerDragResizesBothDimensions() {
        int startWidth = frame.getWidth();
        int startHeight = frame.getHeight();
        int startX = frame.getX();
        int startY = frame.getY();

        // Start drag on bottom-right corner
        MouseEvent press = new MouseEvent(59, 29, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(press));

        // Continue drag (move 5 right, 3 down)
        MouseEvent move = new MouseEvent(64, 32, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(move));

        // Verify both dimensions changed, position unchanged
        assertEquals(startX, frame.getX());
        assertEquals(startY, frame.getY());
        assertEquals(startWidth + 5, frame.getWidth());
        assertEquals(startHeight + 3, frame.getHeight());

        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should not allow drag when draggable is false")
    void testDraggableFalsePreventsDrag() {
        frame.setDraggable(false);
        int startX = frame.getX();
        int startY = frame.getY();

        // Try to drag title bar
        MouseEvent press = new MouseEvent(30, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(frame.handleMouseEvent(press));

        // Verify frame did not move
        assertEquals(startX, frame.getX());
        assertEquals(startY, frame.getY());
    }

    @Test
    @DisplayName("should not allow resize when resizable is false")
    void testResizableFalsePreventsResize() {
        frame.setResizable(false);
        int startWidth = frame.getWidth();
        int startHeight = frame.getHeight();

        // Try to resize by dragging right edge
        MouseEvent press = new MouseEvent(59, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(frame.handleMouseEvent(press));

        // Verify size did not change
        assertEquals(startWidth, frame.getWidth());
        assertEquals(startHeight, frame.getHeight());
    }

    @Test
    @DisplayName("should respect minimum width constraint")
    void testMinimumWidthConstraint() {
        frame.setMinWidth(25);
        frame.setSize(30, 20);

        // Start drag on right edge
        MouseEvent press = new MouseEvent(49, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(press);

        // Try to shrink below minimum (drag 10 left)
        MouseEvent move = new MouseEvent(39, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(move);

        // Should be clamped to minimum
        assertEquals(25, frame.getWidth());

        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should respect minimum height constraint")
    void testMinimumHeightConstraint() {
        frame.setMinHeight(12);
        frame.setSize(40, 15);

        // Start drag on bottom edge
        MouseEvent press = new MouseEvent(30, 24, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(press);

        // Try to shrink below minimum (drag 8 up)
        MouseEvent move = new MouseEvent(30, 16, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(move);

        // Should be clamped to minimum
        assertEquals(12, frame.getHeight());

        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should enforce absolute minimum width of 5")
    void testAbsoluteMinimumWidth() {
        // setMinWidth clamps to 5
        frame.setMinWidth(3);
        assertEquals(5, frame.getMinWidth());
    }

    @Test
    @DisplayName("should enforce absolute minimum height of 3")
    void testAbsoluteMinimumHeight() {
        // setMinHeight clamps to 3
        frame.setMinHeight(1);
        assertEquals(3, frame.getMinHeight());
    }

    @Test
    @DisplayName("should dispatch mouse events to children when clicking content area")
    void testChildrenReceiveEventsInContentArea() {
        AtomicBoolean buttonClicked = new AtomicBoolean(false);

        // Add a button in the content area
        JButton button = new JButton("Click Me") {
            @Override
            public boolean handleMouseEvent(MouseEvent event) {
                if ((event.button() & NcursesBridge.BUTTON1_PRESSED) != 0) {
                    buttonClicked.set(true);
                    return true;
                }
                return false;
            }
        };
        button.setLocation(25, 15);
        button.setSize(10, 3);
        frame.add(button);

        // Click inside content area (on the button)
        MouseEvent press = new MouseEvent(30, 16, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(press));

        // Button should have received the event
        assertTrue(buttonClicked.get());
    }

    @Test
    @DisplayName("should not dispatch to children when dragging borders")
    void testChildrenDoNotReceiveEventsOnBorders() {
        AtomicBoolean buttonClicked = new AtomicBoolean(false);

        // Add a button that overlaps the right edge (contrived scenario)
        JButton button = new JButton("Edge Button") {
            @Override
            public boolean handleMouseEvent(MouseEvent event) {
                buttonClicked.set(true);
                return true;
            }
        };
        button.setLocation(55, 15);  // Near right edge
        button.setSize(10, 3);
        frame.add(button);

        // Click on right edge (should start resize, not click button)
        MouseEvent press = new MouseEvent(59, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(frame.handleMouseEvent(press));

        // Button should NOT have received the event (drag manager consumed it)
        assertFalse(buttonClicked.get());

        WindowDragManager.getInstance().cancelDrag();
    }

    @Test
    @DisplayName("should allow draggable and resizable to be toggled")
    void testDraggableResizableToggle() {
        // Initially draggable and resizable
        assertTrue(frame.isDraggable());
        assertTrue(frame.isResizable());

        // Disable both
        frame.setDraggable(false);
        frame.setResizable(false);
        assertFalse(frame.isDraggable());
        assertFalse(frame.isResizable());

        // Re-enable both
        frame.setDraggable(true);
        frame.setResizable(true);
        assertTrue(frame.isDraggable());
        assertTrue(frame.isResizable());
    }

    @Test
    @DisplayName("should maintain drag state across multiple move events")
    void testContinuousDrag() {
        int startX = frame.getX();
        int startY = frame.getY();

        // Start drag
        MouseEvent press = new MouseEvent(30, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(press);

        // Move in steps
        MouseEvent move1 = new MouseEvent(32, 12, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(move1);
        assertEquals(startX + 2, frame.getX());
        assertEquals(startY + 2, frame.getY());

        MouseEvent move2 = new MouseEvent(35, 14, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(move2);
        assertEquals(startX + 5, frame.getX());
        assertEquals(startY + 4, frame.getY());

        MouseEvent move3 = new MouseEvent(40, 18, (int) NcursesBridge.BUTTON1_PRESSED);
        frame.handleMouseEvent(move3);
        assertEquals(startX + 10, frame.getX());
        assertEquals(startY + 8, frame.getY());

        // End drag
        MouseEvent release = new MouseEvent(40, 18, (int) NcursesBridge.BUTTON1_RELEASED);
        frame.handleMouseEvent(release);
    }
}

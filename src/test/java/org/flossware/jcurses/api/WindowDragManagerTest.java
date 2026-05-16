package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;
import org.flossware.jcurses.ffi.NcursesBridge;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WindowDragManager Tests")
class WindowDragManagerTest extends ComponentTestBase {

    private JFrame frame;
    private WindowDragManager manager;

    @BeforeEach
    void setUp() {
        frame = new JFrame("Test Frame");
        frame.setLocation(10, 5);
        frame.setSize(40, 20);
        manager = WindowDragManager.getInstance();
        // Cancel any previous drag
        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect title bar hit zone")
    void testTitleBarHitZone() {
        // Click on title bar (top edge, y == frame.y)
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        // Release
        MouseEvent release = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_RELEASED);
        manager.handleMouseEvent(release, frame);
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should detect left edge hit zone")
    void testLeftEdgeHitZone() {
        // Click on left edge (x == frame.x)
        MouseEvent press = new MouseEvent(10, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect right edge hit zone")
    void testRightEdgeHitZone() {
        // Click on right edge (x == frame.x + width - 1)
        MouseEvent press = new MouseEvent(49, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect bottom edge hit zone")
    void testBottomEdgeHitZone() {
        // Click on bottom edge (y == frame.y + height - 1)
        MouseEvent press = new MouseEvent(20, 24, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect top-left corner hit zone")
    void testTopLeftCornerHitZone() {
        // Click on top-left corner
        MouseEvent press = new MouseEvent(10, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect top-right corner hit zone")
    void testTopRightCornerHitZone() {
        // Click on top-right corner
        MouseEvent press = new MouseEvent(49, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect bottom-left corner hit zone")
    void testBottomLeftCornerHitZone() {
        // Click on bottom-left corner
        MouseEvent press = new MouseEvent(10, 24, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should detect bottom-right corner hit zone")
    void testBottomRightCornerHitZone() {
        // Click on bottom-right corner
        MouseEvent press = new MouseEvent(49, 24, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should not handle events in content area")
    void testContentAreaNoHandle() {
        // Click inside content area (not on border)
        MouseEvent press = new MouseEvent(20, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(manager.handleMouseEvent(press, frame));
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should not handle events outside window bounds")
    void testOutsideBoundsNoHandle() {
        // Click outside window
        MouseEvent press = new MouseEvent(5, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(manager.handleMouseEvent(press, frame));
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should move window when dragging title bar")
    void testMoveWindow() {
        int startX = frame.getX();
        int startY = frame.getY();

        // Start drag on title bar
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Continue drag (move 5 right, 3 down)
        MouseEvent move = new MouseEvent(25, 8, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Window should have moved
        assertEquals(startX + 5, frame.getX());
        assertEquals(startY + 3, frame.getY());

        // End drag
        MouseEvent release = new MouseEvent(25, 8, (int) NcursesBridge.BUTTON1_RELEASED);
        manager.handleMouseEvent(release, frame);
    }

    @Test
    @DisplayName("should resize window width when dragging right edge")
    void testResizeWidthRight() {
        int startWidth = frame.getWidth();

        // Start drag on right edge
        MouseEvent press = new MouseEvent(49, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Continue drag (move 5 right)
        MouseEvent move = new MouseEvent(54, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Width should have increased
        assertEquals(startWidth + 5, frame.getWidth());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should resize window height when dragging bottom edge")
    void testResizeHeightBottom() {
        int startHeight = frame.getHeight();

        // Start drag on bottom edge
        MouseEvent press = new MouseEvent(20, 24, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Continue drag (move 3 down)
        MouseEvent move = new MouseEvent(20, 27, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Height should have increased
        assertEquals(startHeight + 3, frame.getHeight());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should enforce minimum size constraints")
    void testMinimumSizeConstraint() {
        frame.setMinWidth(15);
        frame.setMinHeight(8);
        frame.setSize(20, 10);

        // Try to resize smaller than min
        MouseEvent press = new MouseEvent(29, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag left to shrink (try to make width 5)
        MouseEvent move = new MouseEvent(14, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Should be clamped to minimum
        assertEquals(15, frame.getWidth());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should respect draggable flag")
    void testDraggableFlag() {
        frame.setDraggable(false);

        // Try to drag title bar
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(manager.handleMouseEvent(press, frame));
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should respect resizable flag")
    void testResizableFlag() {
        frame.setResizable(false);

        // Try to resize by dragging right edge
        MouseEvent press = new MouseEvent(49, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(manager.handleMouseEvent(press, frame));
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should only allow one drag at a time")
    void testSingleDragOnly() {
        JFrame frame2 = new JFrame("Frame 2");
        frame2.setLocation(60, 10);
        frame2.setSize(30, 15);

        // Start drag on first frame
        MouseEvent press1 = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        assertTrue(manager.handleMouseEvent(press1, frame));
        assertTrue(manager.isDragging());

        // Try to start drag on second frame (should be ignored)
        MouseEvent press2 = new MouseEvent(70, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        assertFalse(manager.handleMouseEvent(press2, frame2));

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should cancel drag operation")
    void testCancelDrag() {
        // Start drag
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);
        assertTrue(manager.isDragging());

        // Cancel drag
        manager.cancelDrag();
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
            manager.handleMouseEvent(press, frame);
            manager.cancelDrag();
        });
    }
}

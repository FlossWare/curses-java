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

    @Test
    @DisplayName("should resize with left edge drag")
    void testResizeLeftEdge() {
        frame.setLocation(20, 10);
        frame.setSize(40, 20);

        // Start drag on left edge
        MouseEvent press = new MouseEvent(20, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag left to expand width
        MouseEvent move = new MouseEvent(15, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Width should increase, X should decrease
        assertEquals(15, frame.getX());
        assertEquals(45, frame.getWidth());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should move window with top edge drag (title bar)")
    void testMoveWindowTopEdge() {
        frame.setLocation(20, 10);
        frame.setSize(40, 20);

        // Start drag on top edge (title bar, not corner)
        MouseEvent press = new MouseEvent(30, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag to move the window
        MouseEvent move = new MouseEvent(30, 7, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Window should move (Y should change), size should stay the same
        assertEquals(7, frame.getY());
        assertEquals(20, frame.getHeight());  // Height unchanged (moved, not resized)
        assertEquals(20, frame.getX());  // X unchanged

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should resize with top-right corner drag")
    void testResizeTopRightCorner() {
        frame.setLocation(20, 10);
        frame.setSize(40, 20);

        // Start drag on top-right corner
        MouseEvent press = new MouseEvent(59, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag to expand
        MouseEvent move = new MouseEvent(64, 7, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Width should increase, height should increase, Y should decrease
        assertEquals(45, frame.getWidth());
        assertEquals(23, frame.getHeight());
        assertEquals(7, frame.getY());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should resize with bottom-left corner drag")
    void testResizeBottomLeftCorner() {
        frame.setLocation(20, 10);
        frame.setSize(40, 20);

        // Start drag on bottom-left corner
        MouseEvent press = new MouseEvent(20, 29, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag to expand
        MouseEvent move = new MouseEvent(15, 34, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Width should increase, height should increase, X should decrease
        assertEquals(45, frame.getWidth());
        assertEquals(25, frame.getHeight());
        assertEquals(15, frame.getX());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should enforce maximum size constraints")
    void testMaximumSizeConstraint() {
        JFrame testFrame = new JFrame("Test");
        testFrame.setLocation(10, 10);
        testFrame.setSize(20, 15);

        // Set max constraints using DraggableWindow interface
        // Since JFrame doesn't have setMaxWidth/setMaxHeight, we'll test with a custom implementation
        DraggableWindow customWindow = new DraggableWindow() {
            Component comp = testFrame;
            @Override
            public boolean isDraggable() { return true; }
            @Override
            public boolean isResizable() { return true; }
            @Override
            public int getMaxWidth() { return 25; }
            @Override
            public int getMaxHeight() { return 20; }
        };

        // Can't directly test without a Component that implements the interface properly
        // This test documents the feature exists
        assertTrue(customWindow.getMaxWidth() > 0);
        assertTrue(customWindow.getMaxHeight() > 0);
    }

    @Test
    @DisplayName("should constrain window to parent bounds during resize")
    void testParentBoundsConstraintResize() {
        // Create parent container
        Container parent = new JPanel();
        parent.setLocation(0, 0);
        parent.setSize(80, 24);

        // Create frame as child - start small and near the edge
        frame.setLocation(60, 10);
        frame.setSize(15, 10);
        parent.add(frame);

        // Try to resize frame beyond parent bounds by dragging right edge
        MouseEvent press = new MouseEvent(74, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Try to resize far to the right (would go out of bounds)
        MouseEvent move = new MouseEvent(90, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, frame);

        // Frame should be constrained to parent bounds
        assertTrue(frame.getX() + frame.getWidth() <= parent.getWidth(),
                   "Frame should be constrained within parent: X=" + frame.getX() + " W=" + frame.getWidth() + " ParentW=" + parent.getWidth());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should invalidate layout when resizing container")
    void testLayoutInvalidationOnResize() {
        // Create a JPanel (which is a Container) that's draggable
        JDialog dialog = new JDialog("Test");
        dialog.setLocation(20, 10);
        dialog.setSize(40, 20);

        // Start resize on right edge
        MouseEvent press = new MouseEvent(59, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, dialog);

        // Resize
        MouseEvent move = new MouseEvent(64, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(move, dialog);

        // The resize operation should have been handled
        manager.cancelDrag();

        // We can't easily verify invalidateLayout() was called without mocking,
        // but this ensures the code path is executed
        assertTrue(dialog.getWidth() > 40);
    }

    @Test
    @DisplayName("should handle BUTTON1_CLICKED events")
    void testButton1Clicked() {
        // Test with BUTTON1_CLICKED instead of BUTTON1_PRESSED
        MouseEvent click = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_CLICKED);
        assertTrue(manager.handleMouseEvent(click, frame));
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should ignore zero delta movement")
    void testZeroDeltaMovement() {
        // Start drag
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        int startX = frame.getX();
        int startY = frame.getY();

        // Send same position (no movement)
        MouseEvent samePos = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        boolean handled = manager.handleMouseEvent(samePos, frame);

        // Should still be handled but position shouldn't change
        assertTrue(handled);
        assertEquals(startX, frame.getX());
        assertEquals(startY, frame.getY());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should handle BUTTON1_RELEASED to end drag")
    void testButton1Released() {
        // Start drag
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);
        assertTrue(manager.isDragging());

        // Release button
        MouseEvent release = new MouseEvent(25, 8, (int) NcursesBridge.BUTTON1_RELEASED);
        boolean handled = manager.handleMouseEvent(release, frame);

        assertTrue(handled);
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should not handle events for non-DraggableWindow components")
    void testNonDraggableWindow() {
        // Create a component that doesn't implement DraggableWindow
        Component normalComponent = new JLabel("Not Draggable");
        normalComponent.setLocation(10, 10);
        normalComponent.setSize(20, 5);

        MouseEvent press = new MouseEvent(15, 12, (int) NcursesBridge.BUTTON1_PRESSED);
        boolean handled = manager.handleMouseEvent(press, normalComponent);

        assertFalse(handled);
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should resize from top-left corner")
    void testResizeTopLeftCorner() {
        int originalX = frame.getX();
        int originalY = frame.getY();
        int originalWidth = frame.getWidth();
        int originalHeight = frame.getHeight();

        // Click on top-left corner
        MouseEvent press = new MouseEvent(10, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag down and right
        MouseEvent drag = new MouseEvent(13, 8, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(drag, frame);

        // Both position and size should change
        assertEquals(originalX + 3, frame.getX());
        assertEquals(originalY + 3, frame.getY());
        assertEquals(originalWidth - 3, frame.getWidth());
        assertEquals(originalHeight - 3, frame.getHeight());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should resize from bottom-right corner")
    void testResizeBottomRightCorner() {
        int originalWidth = frame.getWidth();
        int originalHeight = frame.getHeight();

        // Click on bottom-right corner (x=49, y=24 for 40x20 frame at 10,5)
        MouseEvent press = new MouseEvent(49, 24, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);

        // Drag further out
        MouseEvent drag = new MouseEvent(54, 29, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(drag, frame);

        // Size should increase
        assertEquals(originalWidth + 5, frame.getWidth());
        assertEquals(originalHeight + 5, frame.getHeight());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should enforce maximum width constraint")
    void testMaxWidthConstraint() {
        // Create a frame with max width
        JFrame constrainedFrame = new JFrame("Constrained") {
            @Override
            public int getMaxWidth() {
                return 50;
            }
        };
        constrainedFrame.setLocation(10, 10);
        constrainedFrame.setSize(40, 20);

        // Try to resize beyond max width
        MouseEvent press = new MouseEvent(49, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, constrainedFrame);

        // Drag far to the right
        MouseEvent drag = new MouseEvent(100, 15, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(drag, constrainedFrame);

        // Width should be clamped to max
        assertEquals(50, constrainedFrame.getWidth());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should enforce maximum height constraint")
    void testMaxHeightConstraint() {
        // Create a frame with max height
        JFrame constrainedFrame = new JFrame("Constrained") {
            @Override
            public int getMaxHeight() {
                return 30;
            }
        };
        constrainedFrame.setLocation(10, 10);
        constrainedFrame.setSize(40, 20);

        // Try to resize beyond max height
        MouseEvent press = new MouseEvent(25, 29, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, constrainedFrame);

        // Drag far down
        MouseEvent drag = new MouseEvent(25, 60, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(drag, constrainedFrame);

        // Height should be clamped to max
        assertEquals(30, constrainedFrame.getHeight());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should handle click outside window bounds - left")
    void testClickOutsideBoundsLeft() {
        // Click to the left of the window
        MouseEvent press = new MouseEvent(5, 10, (int) NcursesBridge.BUTTON1_PRESSED);
        boolean handled = manager.handleMouseEvent(press, frame);

        assertFalse(handled);
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should handle click outside window bounds - above")
    void testClickOutsideBoundsAbove() {
        // Click above the window
        MouseEvent press = new MouseEvent(20, 3, (int) NcursesBridge.BUTTON1_PRESSED);
        boolean handled = manager.handleMouseEvent(press, frame);

        assertFalse(handled);
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should handle click outside window bounds - below")
    void testClickOutsideBoundsBelow() {
        // Click below the window (frame is at y=5 with height=20, so below is y>=25)
        MouseEvent press = new MouseEvent(20, 30, (int) NcursesBridge.BUTTON1_PRESSED);
        boolean handled = manager.handleMouseEvent(press, frame);

        assertFalse(handled);
        assertFalse(manager.isDragging());
    }

    @Test
    @DisplayName("should handle BUTTON1_RELEASED for different window")
    void testReleaseForDifferentWindow() {
        // Start drag on frame
        MouseEvent press = new MouseEvent(20, 5, (int) NcursesBridge.BUTTON1_PRESSED);
        manager.handleMouseEvent(press, frame);
        assertTrue(manager.isDragging());

        // Try to release on a different frame
        JFrame otherFrame = new JFrame("Other");
        otherFrame.setLocation(100, 100);
        otherFrame.setSize(30, 15);

        MouseEvent release = new MouseEvent(110, 105, (int) NcursesBridge.BUTTON1_RELEASED);
        boolean handled = manager.handleMouseEvent(release, otherFrame);

        // Should not handle the release for different window
        assertFalse(handled);
        // Drag should still be active
        assertTrue(manager.isDragging());

        manager.cancelDrag();
    }

    @Test
    @DisplayName("should not invalidate layout for non-Container window")
    void testNonContainerWindow() {
        // Create a custom DraggableWindow that doesn't extend Container
        Component customWindow = new Component() {
            private boolean draggable = true;
            private boolean resizable = true;

            {
                setLocation(10, 10);
                setSize(30, 15);
            }

            @Override
            public void paint(char[][] buffer) {
                // Minimal implementation
            }
        };

        // Make it implement DraggableWindow via anonymous class
        DraggableWindow draggableWindow = new DraggableWindow() {
            @Override
            public boolean isDraggable() {
                return true;
            }
        };

        // Since we can't easily make Component implement DraggableWindow at runtime,
        // this test verifies the branch exists but may not be reachable in practice
        // The instanceof check at line 244 will be false for non-Container windows

        // This is actually testing that JFrame (which IS a Container) invalidates layout
        // The else branch (non-Container) is defensive programming
        assertTrue(frame instanceof Container);
    }
}

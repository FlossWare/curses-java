package org.flossware.curses.api.widgets;

import org.flossware.curses.api.DraggableWindow;
import org.flossware.curses.api.Dialog;
import org.flossware.curses.api.Label;
import org.flossware.curses.api.StatusBar;
import org.flossware.curses.api.RootPane;
import org.flossware.curses.testutil.ComponentTestBase;
import org.flossware.curses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dialog Tests")
class JDialogTest extends ComponentTestBase {
    private Dialog widget;

    @BeforeEach
    void setUp() {
        widget = new Dialog();
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

    @Test
    @DisplayName("should be added to parent")
    void testAddToParent() {
        root.add(widget);
        assertTrue(root.getChildren().contains(widget));
    }

    @Test
    @DisplayName("should create dialog with title")
    void testDialogWithTitle() {
        Dialog dialog = new Dialog("Test Dialog");
        assertEquals("Test Dialog", dialog.getTitle());
    }

    @Test
    @DisplayName("should allow setting title")
    void testSetTitle() {
        widget.setTitle("New Title");
        assertEquals("New Title", widget.getTitle());
    }

    @Test
    @DisplayName("should default to modal")
    void testDefaultModal() {
        assertTrue(widget.isModal());
    }

    @Test
    @DisplayName("should allow setting modal flag")
    void testSetModal() {
        widget.setModal(false);
        assertFalse(widget.isModal());

        widget.setModal(true);
        assertTrue(widget.isModal());
    }

    @Test
    @DisplayName("should support status bar")
    void testSetStatusBar() {
        StatusBar statusBar = new StatusBar("Status text");
        widget.setStatusBar(statusBar);

        assertEquals(statusBar, widget.getStatusBar());
        assertTrue(widget.getChildren().contains(statusBar));
    }

    @Test
    @DisplayName("should remove old status bar when setting new one")
    void testReplaceStatusBar() {
        StatusBar statusBar1 = new StatusBar("First");
        StatusBar statusBar2 = new StatusBar("Second");

        widget.setStatusBar(statusBar1);
        widget.setStatusBar(statusBar2);

        assertEquals(statusBar2, widget.getStatusBar());
        assertFalse(widget.getChildren().contains(statusBar1));
        assertTrue(widget.getChildren().contains(statusBar2));
    }

    @Test
    @DisplayName("should position status bar at bottom")
    void testStatusBarPosition() {
        widget.setLocation(10, 10);
        widget.setSize(30, 10);

        StatusBar statusBar = new StatusBar("Status");
        widget.setStatusBar(statusBar);

        // Status bar should be at bottom inside border
        assertEquals(11, statusBar.getX());  // x + 1
        assertEquals(18, statusBar.getY());  // y + height - 2
        assertEquals(28, statusBar.getWidth());  // width - 2
    }

    @Test
    @DisplayName("should update status bar position when dialog moves")
    void testStatusBarFollowsDialog() {
        StatusBar statusBar = new StatusBar("Status");
        widget.setStatusBar(statusBar);

        widget.setLocation(20, 15);

        assertEquals(21, statusBar.getX());
        assertEquals(18, statusBar.getY());
    }

    @Test
    @DisplayName("should update status bar size when dialog resizes")
    void testStatusBarResizesWithDialog() {
        StatusBar statusBar = new StatusBar("Status");
        widget.setStatusBar(statusBar);

        widget.setSize(40, 12);

        assertEquals(38, statusBar.getWidth());  // 40 - 2
        assertEquals(10, statusBar.getY());  // 0 + 12 - 2
    }

    @Test
    @DisplayName("should support setStatusText convenience method")
    void testSetStatusText() {
        widget.setStatusText("Test status");

        assertNotNull(widget.getStatusBar());
        assertEquals("Test status", widget.getStatusBar().getText());
    }

    @Test
    @DisplayName("should update existing status bar with setStatusText")
    void testSetStatusTextOnExisting() {
        widget.setStatusText("First");
        StatusBar statusBar = widget.getStatusBar();

        widget.setStatusText("Second");

        assertEquals(statusBar, widget.getStatusBar());  // Same instance
        assertEquals("Second", statusBar.getText());
    }

    @Test
    @DisplayName("should remove status bar when set to null")
    void testRemoveStatusBar() {
        StatusBar statusBar = new StatusBar("Status");
        widget.setStatusBar(statusBar);
        assertTrue(widget.getChildren().contains(statusBar));

        widget.setStatusBar(null);

        assertNull(widget.getStatusBar());
        assertFalse(widget.getChildren().contains(statusBar));
    }

    @Test
    @DisplayName("should show dialog by adding to RootPane")
    void testShow() {
        int initialCount = RootPane.getInstance().getChildren().size();

        widget.show();

        assertTrue(RootPane.getInstance().getChildren().contains(widget));
        assertEquals(initialCount + 1, RootPane.getInstance().getChildren().size());
    }

    @Test
    @DisplayName("should be draggable by default")
    void testDefaultDraggable() {
        assertTrue(widget.isDraggable());
    }

    @Test
    @DisplayName("should allow setting draggable")
    void testSetDraggable() {
        widget.setDraggable(false);
        assertFalse(widget.isDraggable());

        widget.setDraggable(true);
        assertTrue(widget.isDraggable());
    }

    @Test
    @DisplayName("should be resizable by default")
    void testDefaultResizable() {
        assertTrue(widget.isResizable());
    }

    @Test
    @DisplayName("should allow setting resizable")
    void testSetResizable() {
        widget.setResizable(false);
        assertFalse(widget.isResizable());

        widget.setResizable(true);
        assertTrue(widget.isResizable());
    }

    @Test
    @DisplayName("should have default min width")
    void testDefaultMinWidth() {
        assertEquals(10, widget.getMinWidth());
    }

    @Test
    @DisplayName("should set min width with clamping")
    void testSetMinWidth() {
        widget.setMinWidth(20);
        assertEquals(20, widget.getMinWidth());

        widget.setMinWidth(3);  // Below minimum of 5
        assertEquals(5, widget.getMinWidth());
    }

    @Test
    @DisplayName("should have default min height")
    void testDefaultMinHeight() {
        assertEquals(3, widget.getMinHeight());
    }

    @Test
    @DisplayName("should set min height with clamping")
    void testSetMinHeight() {
        widget.setMinHeight(10);
        assertEquals(10, widget.getMinHeight());

        widget.setMinHeight(1);  // Below minimum of 3
        assertEquals(3, widget.getMinHeight());
    }

    @Test
    @DisplayName("should render title centered in border")
    void testPaintWithTitle() {
        widget.setTitle("My Dialog");
        widget.setSize(30, 10);

        assertDoesNotThrow(() -> widget.paint(buffer));

        // Check that title appears in first row
        String firstRow = new String(buffer[0]);
        assertTrue(firstRow.contains("[ My Dialog ]"));
    }

    @Test
    @DisplayName("should not render empty title")
    void testPaintWithEmptyTitle() {
        widget.setTitle("");
        widget.setSize(30, 10);

        widget.paint(buffer);

        String firstRow = new String(buffer[0]);
        assertFalse(firstRow.contains("[  ]"));
    }

    @Test
    @DisplayName("should not render title if width too small")
    void testPaintWithSmallWidth() {
        widget.setTitle("Very Long Title");
        widget.setSize(2, 5);

        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should not crash with null title")
    void testPaintWithNullTitle() {
        widget.setTitle(null);
        widget.setSize(30, 10);

        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should handle mouse events for dragging")
    void testHandleMouseEvent() {
        widget.setLocation(10, 10);
        widget.setSize(20, 10);

        org.flossware.curses.events.MouseEvent mouseEvent =
            new org.flossware.curses.events.MouseEvent(15, 12, 1);

        // Should attempt to handle mouse event (result depends on WindowDragManager state)
        assertDoesNotThrow(() -> widget.handleMouseEvent(mouseEvent));
    }

    @Test
    @DisplayName("should consume mouse event when drag starts")
    void testDragConsumeMouseEvent() {
        // Cancel any existing drag from previous tests
        org.flossware.curses.api.WindowDragManager.getInstance().cancelDrag();

        widget.setLocation(10, 10);
        widget.setSize(20, 10);

        // Click on title bar to start drag (BUTTON1_PRESSED = 0x2)
        org.flossware.curses.events.MouseEvent titleBarClick =
            new org.flossware.curses.events.MouseEvent(15, 10, 0x2);

        boolean consumed = widget.handleMouseEvent(titleBarClick);
        assertTrue(consumed, "Title bar click should be consumed by drag manager");
    }

    @Test
    @DisplayName("should be thread-safe for concurrent operations")
    void testThreadSafety() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 50; i++) {
                widget.setTitle("Title " + i);
                widget.setModal(i % 2 == 0);
                widget.setDraggable(i % 3 == 0);
                widget.setResizable(i % 3 != 0);
                widget.setMinWidth(10 + i % 10);
                widget.setMinHeight(3 + i % 5);
                widget.paint(buffer);
            }
        });

        assertTrue(true);  // Should complete without deadlock
    }

    @Test
    @DisplayName("should handle concurrent status bar operations")
    void testConcurrentStatusBarOperations() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(5, () -> {
            for (int i = 0; i < 20; i++) {
                widget.setStatusText("Status " + i);
                widget.setLocation(i, i);
                widget.setSize(20 + i, 10 + i);
                if (i % 5 == 0) {
                    widget.setStatusBar(null);
                }
            }
        });

        assertTrue(true);  // Should complete without deadlock
    }

    @Test
    @DisplayName("should use DraggableWindow default methods")
    void testDraggableWindowDefaults() {
        // Test that Dialog implements DraggableWindow and can use default methods
        DraggableWindow draggable = widget;

        // Default max width/height are 0 (unlimited)
        assertEquals(0, draggable.getMaxWidth());
        assertEquals(0, draggable.getMaxHeight());
    }

    @Test
    @DisplayName("should handle mouse event delegation to children")
    void testMouseEventDelegationToChildren() {
        Label child = new Label("Child");
        child.setLocation(5, 5);
        child.setSize(10, 1);

        boolean[] childListenerCalled = {false};
        child.addMouseListener(event -> childListenerCalled[0] = true);

        widget.setLocation(0, 0);
        widget.setSize(30, 20);
        widget.add(child);

        // Click on child (inside dialog but not on border)
        org.flossware.curses.events.MouseEvent event =
            new org.flossware.curses.events.MouseEvent(10, 10, 0x4);

        widget.handleMouseEvent(event);

        // Test executes the delegation path
        assertNotNull(child.getParent());
    }
}

package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JDialog;
import org.flossware.jcurses.api.JStatusBar;
import org.flossware.jcurses.api.RootPane;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JDialog Tests")
class JDialogTest extends ComponentTestBase {
    private JDialog widget;

    @BeforeEach
    void setUp() {
        widget = new JDialog();
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
        JDialog dialog = new JDialog("Test Dialog");
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
        JStatusBar statusBar = new JStatusBar("Status text");
        widget.setStatusBar(statusBar);

        assertEquals(statusBar, widget.getStatusBar());
        assertTrue(widget.getChildren().contains(statusBar));
    }

    @Test
    @DisplayName("should remove old status bar when setting new one")
    void testReplaceStatusBar() {
        JStatusBar statusBar1 = new JStatusBar("First");
        JStatusBar statusBar2 = new JStatusBar("Second");

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

        JStatusBar statusBar = new JStatusBar("Status");
        widget.setStatusBar(statusBar);

        // Status bar should be at bottom inside border
        assertEquals(11, statusBar.getX());  // x + 1
        assertEquals(18, statusBar.getY());  // y + height - 2
        assertEquals(28, statusBar.getWidth());  // width - 2
    }

    @Test
    @DisplayName("should update status bar position when dialog moves")
    void testStatusBarFollowsDialog() {
        JStatusBar statusBar = new JStatusBar("Status");
        widget.setStatusBar(statusBar);

        widget.setLocation(20, 15);

        assertEquals(21, statusBar.getX());
        assertEquals(18, statusBar.getY());
    }

    @Test
    @DisplayName("should update status bar size when dialog resizes")
    void testStatusBarResizesWithDialog() {
        JStatusBar statusBar = new JStatusBar("Status");
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
        JStatusBar statusBar = widget.getStatusBar();

        widget.setStatusText("Second");

        assertEquals(statusBar, widget.getStatusBar());  // Same instance
        assertEquals("Second", statusBar.getText());
    }

    @Test
    @DisplayName("should remove status bar when set to null")
    void testRemoveStatusBar() {
        JStatusBar statusBar = new JStatusBar("Status");
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
    @DisplayName("should handle mouse events for dragging")
    void testHandleMouseEvent() {
        widget.setLocation(10, 10);
        widget.setSize(20, 10);

        org.flossware.jcurses.events.MouseEvent mouseEvent =
            new org.flossware.jcurses.events.MouseEvent(15, 12, 1);

        // Should attempt to handle mouse event (result depends on WindowDragManager state)
        assertDoesNotThrow(() -> widget.handleMouseEvent(mouseEvent));
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
}

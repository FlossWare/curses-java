package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JDialog;
import org.flossware.jcurses.api.JStatusBar;
import org.flossware.jcurses.testutil.ComponentTestBase;
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
}

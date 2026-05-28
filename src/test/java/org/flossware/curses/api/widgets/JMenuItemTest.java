package org.flossware.curses.api.widgets;

import org.flossware.curses.api.JMenuItem;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JMenuItem Tests")
class JMenuItemTest extends ComponentTestBase {
    private JMenuItem widget;

    @BeforeEach
    void setUp() {
        widget = new JMenuItem("Menu Item");
        widget.setSize(20, 1);
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
    @DisplayName("should render label")
    void testRenderLabel() {
        widget.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("Menu Item"));
    }

    @Test
    @DisplayName("should set action")
    void testSetAction() {
        boolean[] actionCalled = {false};

        widget.setAction(() -> actionCalled[0] = true);
        widget.activate();

        assertTrue(actionCalled[0]);
    }

    @Test
    @DisplayName("should activate without action set")
    void testActivateWithoutAction() {
        assertDoesNotThrow(() -> widget.activate());
    }

    @Test
    @DisplayName("should execute action when activated")
    void testActionExecution() {
        int[] counter = {0};

        widget.setAction(() -> counter[0]++);
        widget.activate();
        widget.activate();
        widget.activate();

        assertEquals(3, counter[0]);
    }

    @Test
    @DisplayName("should replace action")
    void testReplaceAction() {
        boolean[] action1Called = {false};
        boolean[] action2Called = {false};

        widget.setAction(() -> action1Called[0] = true);
        widget.setAction(() -> action2Called[0] = true);
        widget.activate();

        assertFalse(action1Called[0]);
        assertTrue(action2Called[0]);
    }
}

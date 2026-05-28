package org.flossware.curses.api.widgets;

import org.flossware.curses.api.JMenu;
import org.flossware.curses.api.JMenuItem;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JMenu Tests")
class JMenuTest extends ComponentTestBase {
    private JMenu widget;

    @BeforeEach
    void setUp() {
        widget = new JMenu("File");
        widget.setSize(20, 5);
        widget.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(widget);
    }

    @Test
    @DisplayName("should have label")
    void testGetLabel() {
        assertEquals("File", widget.getLabel());
    }

    @Test
    @DisplayName("should add menu items")
    void testAddItem() {
        JMenuItem item1 = new JMenuItem("Open");
        JMenuItem item2 = new JMenuItem("Save");

        widget.addItem(item1);
        widget.addItem(item2);

        assertEquals(2, widget.getChildren().size());
        assertTrue(widget.getChildren().contains(item1));
        assertTrue(widget.getChildren().contains(item2));
    }

    @Test
    @DisplayName("should set parent when adding item")
    void testAddItemSetsParent() {
        JMenuItem item = new JMenuItem("Exit");

        widget.addItem(item);

        assertSame(widget, item.getParent());
    }

    @Test
    @DisplayName("should render without errors")
    void testRendering() {
        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should render with menu items")
    void testRenderingWithItems() {
        JMenuItem item1 = new JMenuItem("New");
        JMenuItem item2 = new JMenuItem("Open");

        item1.setSize(10, 1);
        item1.setLocation(0, 0);
        item2.setSize(10, 1);
        item2.setLocation(0, 1);

        widget.addItem(item1);
        widget.addItem(item2);

        assertDoesNotThrow(() -> widget.paint(buffer));
    }
}

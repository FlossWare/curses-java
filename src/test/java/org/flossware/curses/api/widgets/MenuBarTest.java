package org.flossware.curses.api.widgets;

import org.flossware.curses.api.Menu;
import org.flossware.curses.api.MenuBar;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuBar Tests")
class MenuBarTest extends ComponentTestBase {
    private MenuBar widget;

    @BeforeEach
    void setUp() {
        widget = new MenuBar();
        widget.setSize(80, 1);
        widget.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(widget);
    }

    @Test
    @DisplayName("should have default position and height")
    void testDefaultValues() {
        MenuBar menuBar = new MenuBar();

        assertEquals(0, menuBar.getX());
        assertEquals(0, menuBar.getY());
        assertEquals(1, menuBar.getHeight());
    }

    @Test
    @DisplayName("should add menu")
    void testAddMenu() {
        Menu fileMenu = new Menu("File");

        widget.addMenu("File", fileMenu);

        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should render multiple menus")
    void testRenderMultipleMenus() {
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");

        widget.addMenu("File", fileMenu);
        widget.addMenu("Edit", editMenu);
        widget.addMenu("View", viewMenu);

        widget.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("File"));
        assertTrue(row.contains("Edit"));
        assertTrue(row.contains("View"));
    }

    @Test
    @DisplayName("should preserve menu order")
    void testMenuOrder() {
        Menu menu1 = new Menu("First");
        Menu menu2 = new Menu("Second");
        Menu menu3 = new Menu("Third");

        widget.addMenu("First", menu1);
        widget.addMenu("Second", menu2);
        widget.addMenu("Third", menu3);

        widget.paint(buffer);

        String row = new String(buffer[0]);
        int firstPos = row.indexOf("First");
        int secondPos = row.indexOf("Second");
        int thirdPos = row.indexOf("Third");

        assertTrue(firstPos < secondPos && secondPos < thirdPos,
                   "Menus should appear in the order they were added");
    }

    @Test
    @DisplayName("should space menus appropriately")
    void testMenuSpacing() {
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");

        widget.addMenu("File", fileMenu);
        widget.addMenu("Edit", editMenu);

        widget.paint(buffer);

        String row = new String(buffer[0], 0, 20);
        // "File" (4 chars) + 2 spaces = 6, then "Edit" starts
        assertTrue(row.indexOf("File") == 0);
        assertTrue(row.indexOf("Edit") == 6);
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
}

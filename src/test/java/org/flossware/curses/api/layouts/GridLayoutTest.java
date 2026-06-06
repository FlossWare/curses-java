package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GridLayout Tests")
class GridLayoutTest {
    private Container container;
    private GridLayout layout;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(100, 60);
    }

    @Test
    @DisplayName("should create grid with specified rows and columns")
    void testCreation() {
        layout = new GridLayout(3, 3);
        assertNotNull(layout);
    }

    @Test
    @DisplayName("should layout children in grid")
    void testGridLayout() {
        layout = new GridLayout(2, 2);
        container.setLayout(layout);

        Component child1 = new Label("1");
        Component child2 = new Label("2");
        Component child3 = new Label("3");
        Component child4 = new Label("4");

        container.add(child1);
        container.add(child2);
        container.add(child3);
        container.add(child4);

        container.doLayout();

        // Cell size: 100/2 = 50 width, 60/2 = 30 height
        assertEquals(0, child1.getX());
        assertEquals(0, child1.getY());
        assertEquals(50, child1.getWidth());
        assertEquals(30, child1.getHeight());

        assertEquals(50, child2.getX());
        assertEquals(0, child2.getY());

        assertEquals(0, child3.getX());
        assertEquals(30, child3.getY());

        assertEquals(50, child4.getX());
        assertEquals(30, child4.getY());
    }

    @Test
    @DisplayName("should handle empty grid")
    void testEmptyGrid() {
        layout = new GridLayout(2, 2);
        container.setLayout(layout);

        assertDoesNotThrow(() -> container.doLayout());
    }

    @Test
    @DisplayName("should handle more children than grid cells")
    void testExtraChildren() {
        layout = new GridLayout(2, 2);
        container.setLayout(layout);

        for (int i = 0; i < 6; i++) {
            container.add(new Label("" + i));
        }

        assertDoesNotThrow(() -> container.doLayout());
    }

    @Test
    @DisplayName("should calculate uniform cell sizes")
    void testUniformCellSizes() {
        layout = new GridLayout(3, 4);
        container.setLayout(layout);

        Component child = new Label("Test");
        container.add(child);
        container.doLayout();

        // Width: 100/4 = 25, Height: 60/3 = 20
        assertEquals(25, child.getWidth());
        assertEquals(20, child.getHeight());
    }
}

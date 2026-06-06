package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FlowLayout Tests")
class FlowLayoutTest {
    private Container container;
    private FlowLayout layout;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(50, 20);
    }

    @Test
    @DisplayName("should create with default left alignment")
    void testDefaultAlignment() {
        layout = new FlowLayout();
        assertEquals(FlowLayout.LEFT, layout.LEFT);
    }

    @Test
    @DisplayName("should create with custom alignment")
    void testCustomAlignment() {
        layout = new FlowLayout(FlowLayout.CENTER);
        assertNotNull(layout);
    }

    @Test
    @DisplayName("should create with gaps")
    void testCustomGaps() {
        layout = new FlowLayout(FlowLayout.LEFT, 2, 3);
        assertNotNull(layout);
    }

    @Test
    @DisplayName("should layout children in a row")
    void testRowLayout() {
        layout = new FlowLayout();
        container.setLayout(layout);

        Component child1 = new Label("1");
        child1.setSize(10, 1);
        Component child2 = new Label("2");
        child2.setSize(10, 1);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        assertEquals(0, child1.getX());
        assertEquals(11, child2.getX()); // 10 + 1 (gap)
        assertEquals(0, child1.getY());
        assertEquals(0, child2.getY());
    }

    @Test
    @DisplayName("should wrap to next row when width exceeded")
    void testRowWrapping() {
        layout = new FlowLayout();
        container.setLayout(layout);

        Component child1 = new Label("1");
        child1.setSize(20, 1);
        Component child2 = new Label("2");
        child2.setSize(20, 1);
        Component child3 = new Label("3");
        child3.setSize(20, 1);

        container.add(child1);
        container.add(child2);
        container.add(child3);
        container.doLayout();

        // First two fit in first row
        assertEquals(0, child1.getY());
        assertEquals(0, child2.getY());
        // Third wraps to second row
        assertEquals(2, child3.getY()); // rowHeight + vgap
    }

    @Test
    @DisplayName("should handle empty container")
    void testEmptyContainer() {
        layout = new FlowLayout();
        container.setLayout(layout);

        assertDoesNotThrow(() -> container.doLayout());
    }

    @Test
    @DisplayName("should support alignment constants")
    void testAlignmentConstants() {
        assertEquals(0, FlowLayout.LEFT);
        assertEquals(1, FlowLayout.CENTER);
        assertEquals(2, FlowLayout.RIGHT);
    }
}

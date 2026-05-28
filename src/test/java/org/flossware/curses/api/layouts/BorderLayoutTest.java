package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BorderLayout Tests")
class BorderLayoutTest {
    private Container container;
    private BorderLayout layout;
    private Component north, south, east, west, center;

    @BeforeEach
    void setUp() {
        container = new JPanel();
        container.setLocation(0, 0);
        container.setSize(100, 50);

        layout = new BorderLayout();
        container.setLayout(layout);

        north = new JLabel("North");
        south = new JLabel("South");
        east = new JLabel("East");
        west = new JLabel("West");
        center = new JLabel("Center");
    }

    @Test
    @DisplayName("should layout north component at top")
    void testNorthLayout() {
        layout.addLayoutComponent(north, BorderLayout.NORTH);
        north.setSize(100, 5);
        container.add(north);
        container.doLayout();

        assertEquals(0, north.getY());
    }

    @Test
    @DisplayName("should layout south component at bottom")
    void testSouthLayout() {
        layout.addLayoutComponent(south, BorderLayout.SOUTH);
        south.setSize(100, 5);
        container.add(south);
        container.doLayout();

        assertEquals(45, south.getY()); // 50 - 5
    }

    @Test
    @DisplayName("should layout all five regions")
    void testAllRegions() {
        layout.addLayoutComponent(north, BorderLayout.NORTH);
        layout.addLayoutComponent(south, BorderLayout.SOUTH);
        layout.addLayoutComponent(east, BorderLayout.EAST);
        layout.addLayoutComponent(west, BorderLayout.WEST);
        layout.addLayoutComponent(center, BorderLayout.CENTER);

        north.setSize(100, 5);
        south.setSize(100, 5);
        east.setSize(20, 40);
        west.setSize(20, 40);

        container.add(north);
        container.add(south);
        container.add(east);
        container.add(west);
        container.add(center);

        container.doLayout();

        // Verify positions
        assertEquals(0, north.getY(), "North should be at top");
        assertEquals(45, south.getY(), "South should be at bottom");
        assertEquals(0, west.getX(), "West should be at left");
    }

    @Test
    @DisplayName("should handle empty layout")
    void testEmptyLayout() {
        assertDoesNotThrow(() -> container.doLayout());
    }

    @Test
    @DisplayName("should handle null constraints")
    void testNullConstraints() {
        layout.addLayoutComponent(center, null);
        container.add(center);
        assertDoesNotThrow(() -> container.doLayout());
    }

    @Test
    @DisplayName("should support all constraint constants")
    void testConstraintConstants() {
        assertEquals("North", BorderLayout.NORTH);
        assertEquals("South", BorderLayout.SOUTH);
        assertEquals("East", BorderLayout.EAST);
        assertEquals("West", BorderLayout.WEST);
        assertEquals("Center", BorderLayout.CENTER);
    }
}

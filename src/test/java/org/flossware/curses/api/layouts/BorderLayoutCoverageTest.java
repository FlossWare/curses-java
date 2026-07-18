package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional coverage tests for BorderLayout.
 * Targets: preferredLayoutSize, removeLayoutComponent, non-string constraints,
 * subset region combinations, width/height clamping.
 */
@DisplayName("BorderLayout Coverage Tests")
class BorderLayoutCoverageTest {

    private Container container;
    private BorderLayout layout;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(80, 24);
        layout = new BorderLayout();
        container.setLayout(layout);
    }

    @Nested
    @DisplayName("preferredLayoutSize")
    class PreferredLayoutSizeTests {

        @Test
        @DisplayName("should return zero for empty container")
        void testEmptyContainer() {
            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(0, pref.width());
            assertEquals(0, pref.height());
        }

        @Test
        @DisplayName("should include north height")
        void testNorthOnly() {
            Label north = new Label("N");
            north.setPreferredSize(40, 3);
            container.add(north, BorderLayout.NORTH);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(40, pref.width());
            assertEquals(3, pref.height());
        }

        @Test
        @DisplayName("should include south height")
        void testSouthOnly() {
            Label south = new Label("S");
            south.setPreferredSize(50, 2);
            container.add(south, BorderLayout.SOUTH);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(50, pref.width());
            assertEquals(2, pref.height());
        }

        @Test
        @DisplayName("should include east width in middle section")
        void testEastOnly() {
            Label east = new Label("E");
            east.setPreferredSize(15, 10);
            container.add(east, BorderLayout.EAST);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(15, pref.width());
            assertEquals(10, pref.height());
        }

        @Test
        @DisplayName("should include west width in middle section")
        void testWestOnly() {
            Label west = new Label("W");
            west.setPreferredSize(10, 8);
            container.add(west, BorderLayout.WEST);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(10, pref.width());
            assertEquals(8, pref.height());
        }

        @Test
        @DisplayName("should include center in middle section")
        void testCenterOnly() {
            Label center = new Label("C");
            center.setPreferredSize(30, 12);
            container.add(center, BorderLayout.CENTER);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(30, pref.width());
            assertEquals(12, pref.height());
        }

        @Test
        @DisplayName("should combine all five regions correctly")
        void testAllFiveRegions() {
            Label north = new Label("N");
            north.setPreferredSize(60, 3);
            Label south = new Label("S");
            south.setPreferredSize(60, 2);
            Label east = new Label("E");
            east.setPreferredSize(15, 10);
            Label west = new Label("W");
            west.setPreferredSize(10, 10);
            Label center = new Label("C");
            center.setPreferredSize(30, 12);

            container.add(north, BorderLayout.NORTH);
            container.add(south, BorderLayout.SOUTH);
            container.add(east, BorderLayout.EAST);
            container.add(west, BorderLayout.WEST);
            container.add(center, BorderLayout.CENTER);

            Dimension pref = layout.preferredLayoutSize(container);

            // Width: max(north=60, south=60, west+center+east=55) = 60
            assertEquals(60, pref.width());
            // Height: north(3) + south(2) + max(middle heights=12) = 17
            assertEquals(17, pref.height());
        }

        @Test
        @DisplayName("should handle middle section wider than north/south")
        void testMiddleWiderThanNorthSouth() {
            Label north = new Label("N");
            north.setPreferredSize(20, 2);
            Label west = new Label("W");
            west.setPreferredSize(30, 5);
            Label east = new Label("E");
            east.setPreferredSize(30, 5);

            container.add(north, BorderLayout.NORTH);
            container.add(west, BorderLayout.WEST);
            container.add(east, BorderLayout.EAST);

            Dimension pref = layout.preferredLayoutSize(container);
            // Middle width: 30 + 30 = 60, north width: 20 -> max = 60
            assertEquals(60, pref.width());
        }
    }

    @Nested
    @DisplayName("removeLayoutComponent")
    class RemoveLayoutComponentTests {

        @Test
        @DisplayName("should remove component constraint")
        void testRemoveComponent() {
            Label north = new Label("N");
            north.setPreferredSize(40, 3);
            container.add(north, BorderLayout.NORTH);

            layout.removeLayoutComponent(north);

            // After removal, preferredLayoutSize should not include north
            Dimension pref = layout.preferredLayoutSize(container);
            // The child is still in the container but has no constraint
            assertEquals(0, pref.width());
        }
    }

    @Nested
    @DisplayName("addLayoutComponent")
    class AddLayoutComponentTests {

        @Test
        @DisplayName("should handle non-string, non-null constraint")
        void testNonStringConstraint() {
            Label comp = new Label("Test");
            // Passing an Integer constraint -- should be silently ignored
            assertDoesNotThrow(() -> layout.addLayoutComponent(comp, Integer.valueOf(42)));
        }

        @Test
        @DisplayName("should treat null constraint as CENTER")
        void testNullConstraintTreatedAsCenter() {
            Label center = new Label("Center");
            center.setPreferredSize(20, 10);
            container.add(center, null);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(20, pref.width());
            assertEquals(10, pref.height());
        }
    }

    @Nested
    @DisplayName("layoutContainer edge cases")
    class LayoutContainerEdgeCases {

        @Test
        @DisplayName("should clamp remaining dimensions when regions overflow")
        void testNegativeRemainingDimensions() {
            // West and East wider than parent
            Label west = new Label("W");
            west.setSize(50, 10);
            Label east = new Label("E");
            east.setSize(50, 10);
            Label center = new Label("C");

            container.add(west, BorderLayout.WEST);
            container.add(east, BorderLayout.EAST);
            container.add(center, BorderLayout.CENTER);

            // Should not crash, center gets Math.max(0, remaining)
            assertDoesNotThrow(() -> container.doLayout());
            assertTrue(center.getWidth() >= 0);
        }

        @Test
        @DisplayName("should layout with only east and center")
        void testEastAndCenter() {
            Label east = new Label("E");
            east.setSize(20, 24);
            Label center = new Label("C");

            container.add(east, BorderLayout.EAST);
            container.add(center, BorderLayout.CENTER);

            container.doLayout();

            assertEquals(80 - 20, east.getX());
        }

        @Test
        @DisplayName("should skip children with no constraint")
        void testChildWithNoConstraint() {
            Label unconstrained = new Label("No Constraint");
            // Add directly without constraint
            container.add(unconstrained);

            Label center = new Label("C");
            center.setPreferredSize(20, 10);
            container.add(center, BorderLayout.CENTER);

            // Should not crash
            assertDoesNotThrow(() -> container.doLayout());
        }
    }
}

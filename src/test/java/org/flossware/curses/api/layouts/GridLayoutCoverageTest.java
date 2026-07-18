package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional coverage tests for GridLayout.
 * Targets: preferredLayoutSize, addLayoutComponent, removeLayoutComponent,
 * zero rows/cols, 1x1 grid.
 */
@DisplayName("GridLayout Coverage Tests")
class GridLayoutCoverageTest {

    private Container container;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(60, 30);
    }

    @Nested
    @DisplayName("preferredLayoutSize")
    class PreferredLayoutSizeTests {

        @Test
        @DisplayName("should return zero for empty container")
        void testEmptyContainer() {
            GridLayout layout = new GridLayout(2, 2);
            container.setLayout(layout);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(0, pref.width());
            assertEquals(0, pref.height());
        }

        @Test
        @DisplayName("should return zero for zero rows")
        void testZeroRows() {
            GridLayout layout = new GridLayout(0, 2);
            container.setLayout(layout);
            container.add(new Label("A"));

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(0, pref.width());
            assertEquals(0, pref.height());
        }

        @Test
        @DisplayName("should return zero for zero cols")
        void testZeroCols() {
            GridLayout layout = new GridLayout(2, 0);
            container.setLayout(layout);
            container.add(new Label("A"));

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(0, pref.width());
            assertEquals(0, pref.height());
        }

        @Test
        @DisplayName("should compute preferred size based on max child size")
        void testNormalGrid() {
            GridLayout layout = new GridLayout(2, 3);
            container.setLayout(layout);

            Label child1 = new Label("A");
            child1.setPreferredSize(10, 5);
            Label child2 = new Label("B");
            child2.setPreferredSize(15, 3);
            container.add(child1);
            container.add(child2);

            Dimension pref = layout.preferredLayoutSize(container);
            // max cell width = 15, cols = 3 -> 45
            assertEquals(45, pref.width());
            // max cell height = 5, rows = 2 -> 10
            assertEquals(10, pref.height());
        }
    }

    @Nested
    @DisplayName("layoutContainer edge cases")
    class LayoutContainerEdgeCases {

        @Test
        @DisplayName("should handle zero rows")
        void testZeroRowsLayout() {
            GridLayout layout = new GridLayout(0, 3);
            container.setLayout(layout);
            container.add(new Label("A"));

            // Should not crash
            assertDoesNotThrow(() -> container.doLayout());
        }

        @Test
        @DisplayName("should handle zero cols")
        void testZeroColsLayout() {
            GridLayout layout = new GridLayout(3, 0);
            container.setLayout(layout);
            container.add(new Label("A"));

            // Should not crash
            assertDoesNotThrow(() -> container.doLayout());
        }

        @Test
        @DisplayName("should layout 1x1 grid")
        void test1x1Grid() {
            GridLayout layout = new GridLayout(1, 1);
            container.setLayout(layout);

            Label child = new Label("A");
            container.add(child);
            container.doLayout();

            assertEquals(0, child.getX());
            assertEquals(0, child.getY());
            assertEquals(60, child.getWidth());
            assertEquals(30, child.getHeight());
        }

        @Test
        @DisplayName("should stop at rows*cols children")
        void testExcessChildrenStopped() {
            GridLayout layout = new GridLayout(1, 2);
            container.setLayout(layout);

            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C"); // Excess
            container.add(child1);
            container.add(child2);
            container.add(child3);

            container.doLayout();

            // child1 and child2 should be positioned
            assertEquals(0, child1.getX());
            assertEquals(30, child2.getX()); // 60/2 = 30
            // child3 should not be positioned (no change from default 0,0)
        }
    }

    @Nested
    @DisplayName("no-op methods")
    class NoOpMethodTests {

        @Test
        @DisplayName("should not throw on addLayoutComponent")
        void testAddLayoutComponent() {
            GridLayout layout = new GridLayout(2, 2);
            assertDoesNotThrow(() -> layout.addLayoutComponent(new Label("Test"), null));
        }

        @Test
        @DisplayName("should not throw on removeLayoutComponent")
        void testRemoveLayoutComponent() {
            GridLayout layout = new GridLayout(2, 2);
            assertDoesNotThrow(() -> layout.removeLayoutComponent(new Label("Test")));
        }
    }
}

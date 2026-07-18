package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional coverage tests for FlowLayout.
 * Targets: preferredLayoutSize, addLayoutComponent, removeLayoutComponent, custom gaps.
 */
@DisplayName("FlowLayout Coverage Tests")
class FlowLayoutCoverageTest {

    private Container container;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(50, 20);
    }

    @Nested
    @DisplayName("preferredLayoutSize")
    class PreferredLayoutSizeTests {

        @Test
        @DisplayName("should return zero for empty container")
        void testEmptyContainer() {
            FlowLayout layout = new FlowLayout();
            container.setLayout(layout);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(0, pref.width());
            assertEquals(0, pref.height());
        }

        @Test
        @DisplayName("should return single child preferred size")
        void testSingleChild() {
            FlowLayout layout = new FlowLayout();
            container.setLayout(layout);

            Label child = new Label("Test");
            child.setPreferredSize(15, 3);
            container.add(child);

            Dimension pref = layout.preferredLayoutSize(container);
            assertEquals(15, pref.width());
            assertEquals(3, pref.height());
        }

        @Test
        @DisplayName("should compute size with multiple children in single row")
        void testMultipleChildrenOneRow() {
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 2, 1);
            container.setLayout(layout);

            Label child1 = new Label("A");
            child1.setPreferredSize(10, 3);
            Label child2 = new Label("B");
            child2.setPreferredSize(10, 5);
            container.add(child1);
            container.add(child2);

            Dimension pref = layout.preferredLayoutSize(container);
            // 10 + 2(gap) + 10 = 22
            assertEquals(22, pref.width());
            assertEquals(5, pref.height());
        }

        @Test
        @DisplayName("should compute size with wrapping to multiple rows")
        void testWrapping() {
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 1, 2);
            container.setLayout(layout);

            // Three children, each 20 wide -- container is 50 wide
            // First row: child1(20) + gap(1) + child2(20) = 41, fits
            // Second row: child3(20)
            Label child1 = new Label("A");
            child1.setPreferredSize(20, 3);
            Label child2 = new Label("B");
            child2.setPreferredSize(20, 3);
            Label child3 = new Label("C");
            child3.setPreferredSize(20, 4);
            container.add(child1);
            container.add(child2);
            container.add(child3);

            Dimension pref = layout.preferredLayoutSize(container);
            // Width: max(41, 20) = 41
            assertEquals(41, pref.width());
            // Height: row1(3) + vgap(2) + row2(4) = 9
            assertEquals(9, pref.height());
        }
    }

    @Nested
    @DisplayName("no-op methods")
    class NoOpMethodTests {

        @Test
        @DisplayName("should not throw on addLayoutComponent")
        void testAddLayoutComponent() {
            FlowLayout layout = new FlowLayout();
            assertDoesNotThrow(() -> layout.addLayoutComponent(new Label("Test"), null));
        }

        @Test
        @DisplayName("should not throw on removeLayoutComponent")
        void testRemoveLayoutComponent() {
            FlowLayout layout = new FlowLayout();
            assertDoesNotThrow(() -> layout.removeLayoutComponent(new Label("Test")));
        }
    }

    @Nested
    @DisplayName("layout with custom gaps")
    class CustomGapTests {

        @Test
        @DisplayName("should layout with custom hgap and vgap")
        void testCustomGaps() {
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 3, 5);
            container.setLayout(layout);

            Label child1 = new Label("A");
            child1.setSize(10, 2);
            Label child2 = new Label("B");
            child2.setSize(10, 2);
            container.add(child1);
            container.add(child2);

            container.doLayout();

            assertEquals(0, child1.getX());
            // second child offset by width + hgap
            assertEquals(13, child2.getX()); // 10 + 3
        }

        @Test
        @DisplayName("should wrap with correct vgap")
        void testWrappingWithVgap() {
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 1, 4);
            container.setLayout(layout);

            // Two children that each take 30 wide in a 50-wide container
            Label child1 = new Label("A");
            child1.setSize(30, 3);
            Label child2 = new Label("B");
            child2.setSize(30, 3);
            container.add(child1);
            container.add(child2);

            container.doLayout();

            assertEquals(0, child1.getY());
            assertEquals(7, child2.getY()); // 3 + 4 (vgap)
        }
    }
}

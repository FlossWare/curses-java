package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;
import org.flossware.curses.testutil.BufferAssertions;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for Container to cover previously untested methods and branches.
 * Targets: add(child, constraints), pack(), MutationTrackingList methods,
 * drawBorder edge cases, handleMouseEvent fallback to super.
 */
@DisplayName("Container Coverage Tests")
class ContainerCoverageTest extends ComponentTestBase {

    private Container container;

    @BeforeEach
    void setUp() {
        container = new Panel();
    }

    @Nested
    @DisplayName("add with constraints")
    class AddWithConstraintsTests {

        @Test
        @DisplayName("should add child with string constraint and layout manager")
        void testAddWithConstraintsAndLayout() {
            BorderLayout layout = new BorderLayout();
            container.setLayout(layout);

            Label north = new Label("North");
            container.add(north, BorderLayout.NORTH);

            assertEquals(1, container.getChildren().size());
            assertSame(container, north.getParent());
        }

        @Test
        @DisplayName("should add child with null constraint")
        void testAddWithNullConstraint() {
            BorderLayout layout = new BorderLayout();
            container.setLayout(layout);

            Label center = new Label("Center");
            container.add(center, null);

            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should add child with constraint but no layout manager")
        void testAddWithConstraintNoLayout() {
            Label child = new Label("Test");
            // No layout manager set
            container.add(child, "North");

            assertEquals(1, container.getChildren().size());
            assertSame(container, child.getParent());
        }

        @Test
        @DisplayName("should trigger repaint when adding with constraints")
        void testAddWithConstraintsTriggersRepaint() {
            root.add(container);
            clearDirtyFlag();

            container.add(new Label("Test"), "Center");

            assertDirtyFlagSet();
        }
    }

    @Nested
    @DisplayName("pack")
    class PackTests {

        @Test
        @DisplayName("should resize container to preferred layout size")
        void testPackWithLayout() {
            TestLayoutManager layout = new TestLayoutManager(30, 20);
            container.setLayout(layout);

            container.pack();

            assertEquals(30, container.getWidth());
            assertEquals(20, container.getHeight());
        }

        @Test
        @DisplayName("should be no-op when no layout manager")
        void testPackWithoutLayout() {
            container.setSize(10, 10);

            container.pack();

            // Size unchanged
            assertEquals(10, container.getWidth());
            assertEquals(10, container.getHeight());
        }
    }

    @Nested
    @DisplayName("revalidate")
    class RevalidateTests {

        @Test
        @DisplayName("should be no-op when no layout manager")
        void testRevalidateNoLayout() {
            // No layout manager set
            assertDoesNotThrow(() -> container.revalidate());
        }
    }

    @Nested
    @DisplayName("drawBorder edge cases")
    class DrawBorderTests {

        @Test
        @DisplayName("should skip drawing when width is zero")
        void testDrawBorderZeroWidth() {
            Panel panel = new Panel();
            panel.setBordered(true);
            panel.setLocation(0, 0);
            panel.setSize(0, 5);

            assertDoesNotThrow(() -> panel.paint(buffer));
        }

        @Test
        @DisplayName("should skip drawing when height is zero")
        void testDrawBorderZeroHeight() {
            Panel panel = new Panel();
            panel.setBordered(true);
            panel.setLocation(0, 0);
            panel.setSize(10, 0);

            assertDoesNotThrow(() -> panel.paint(buffer));
        }

        @Test
        @DisplayName("should call drawBorder single-arg overload")
        void testDrawBorderSingleArg() {
            Panel panel = new Panel();
            panel.setBordered(true);
            panel.setLocation(0, 0);
            panel.setSize(5, 5);

            assertDoesNotThrow(() -> panel.paint(buffer));
        }
    }

    @Nested
    @DisplayName("handleMouseEvent")
    class HandleMouseEventTests {

        @Test
        @DisplayName("should fall back to super when no child handles event")
        void testFallbackToSuperHandler() {
            container.setLocation(0, 0);
            container.setSize(50, 50);

            boolean[] containerListenerCalled = {false};
            container.addMouseListener(event -> containerListenerCalled[0] = true);

            MouseEvent event = new MouseEvent(5, 5, 1);
            boolean handled = container.handleMouseEvent(event);

            assertTrue(handled);
            assertTrue(containerListenerCalled[0]);
        }

        @Test
        @DisplayName("should not invoke container listener when child handles event")
        void testChildHandlesEvent() {
            container.setLocation(0, 0);
            container.setSize(50, 50);

            Label child = new Label("Test");
            child.setLocation(0, 0);
            child.setSize(20, 20);
            boolean[] childCalled = {false};
            child.addMouseListener(event -> childCalled[0] = true);
            container.add(child);

            boolean[] containerCalled = {false};
            container.addMouseListener(event -> containerCalled[0] = true);

            MouseEvent event = new MouseEvent(5, 5, 1);
            boolean handled = container.handleMouseEvent(event);

            assertTrue(handled);
            assertTrue(childCalled[0]);
            // Container listener should not be called since child handled it
            assertFalse(containerCalled[0]);
        }
    }

    @Nested
    @DisplayName("MutationTrackingList methods")
    class MutationTrackingListTests {

        /** Helper to get children as List (the runtime type is MutationTrackingList extends AbstractList). */
        private List<Component> childrenAsList() {
            return (List<Component>) container.getChildren();
        }

        @Test
        @DisplayName("should support set() on children list")
        void testSetOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C");
            container.add(child1);
            container.add(child2);

            // Replace child at index 0
            childrenAsList().set(0, child3);

            assertEquals(child3, container.getChildren().iterator().next());
        }

        @Test
        @DisplayName("should support addAll() on children list")
        void testAddAllOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");

            container.getChildren().addAll(List.of(child1, child2));

            assertEquals(2, container.getChildren().size());
        }

        @Test
        @DisplayName("should handle addAll() with empty collection")
        void testAddAllEmptyCollection() {
            container.add(new Label("A"));

            container.getChildren().addAll(List.of());

            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should support addAll(index) on children list")
        void testAddAllAtIndexOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C");
            container.add(child1);

            childrenAsList().addAll(1, List.of(child2, child3));

            assertEquals(3, container.getChildren().size());
        }

        @Test
        @DisplayName("should handle addAll(index) with empty collection")
        void testAddAllAtIndexEmptyCollection() {
            container.add(new Label("A"));

            childrenAsList().addAll(0, List.of());

            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should support removeAll() on children list")
        void testRemoveAllOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C");
            container.add(child1);
            container.add(child2);
            container.add(child3);

            boolean changed = container.getChildren().removeAll(List.of(child1, child3));

            assertTrue(changed);
            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should return false from removeAll() when nothing removed")
        void testRemoveAllNoChange() {
            Label child1 = new Label("A");
            container.add(child1);

            boolean changed = container.getChildren().removeAll(List.of(new Label("Z")));

            assertFalse(changed);
            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should support retainAll() on children list")
        void testRetainAllOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C");
            container.add(child1);
            container.add(child2);
            container.add(child3);

            boolean changed = container.getChildren().retainAll(List.of(child2));

            assertTrue(changed);
            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should return false from retainAll() when nothing changed")
        void testRetainAllNoChange() {
            Label child1 = new Label("A");
            container.add(child1);

            boolean changed = container.getChildren().retainAll(List.of(child1));

            assertFalse(changed);
            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should support remove(index) on children list")
        void testRemoveByIndexOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            container.add(child1);
            container.add(child2);

            childrenAsList().remove(0);

            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should support getFirst() on children list")
        void testGetFirstOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            container.add(child1);
            container.add(child2);

            assertEquals(child1, container.getChildren().getFirst());
        }

        @Test
        @DisplayName("should support getLast() on children list")
        void testGetLastOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            container.add(child1);
            container.add(child2);

            assertEquals(child2, container.getChildren().getLast());
        }

        @Test
        @DisplayName("should support addFirst() on children list")
        void testAddFirstOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            container.add(child1);

            container.getChildren().addFirst(child2);

            assertEquals(child2, container.getChildren().getFirst());
            assertEquals(2, container.getChildren().size());
        }

        @Test
        @DisplayName("should support removeLast() on children list")
        void testRemoveLastOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            container.add(child1);
            container.add(child2);

            Component removed = container.getChildren().removeLast();

            assertEquals(child2, removed);
            assertEquals(1, container.getChildren().size());
        }

        @Test
        @DisplayName("should support reversed() on children list")
        void testReversedOnChildrenList() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C");
            container.add(child1);
            container.add(child2);
            container.add(child3);

            var reversed = container.getChildren().reversed();

            assertEquals(3, reversed.size());
            // Check via iterator
            var iter = reversed.iterator();
            assertEquals(child3, iter.next());
            assertEquals(child2, iter.next());
            assertEquals(child1, iter.next());
        }

        @Test
        @DisplayName("should handle clear() on empty children list")
        void testClearEmptyList() {
            // Clear on already-empty list should not fire mutation
            assertDoesNotThrow(() -> container.getChildren().clear());
            assertEquals(0, container.getChildren().size());
        }

        @Test
        @DisplayName("should detect mutation after set()")
        void testSetInvalidatesSnapshot() {
            Label child1 = new Label("A");
            Label child2 = new Label("B");
            Label child3 = new Label("C");
            container.add(child1);
            container.add(child2);

            // Get initial snapshot
            List<Component> snap1 = container.getChildrenSnapshot();
            assertEquals(2, snap1.size());

            // Mutate via set
            childrenAsList().set(0, child3);

            // New snapshot should reflect the change
            List<Component> snap2 = container.getChildrenSnapshot();
            assertEquals(child3, snap2.get(0));
        }
    }

    /**
     * Test layout manager with configurable preferred size.
     */
    private static class TestLayoutManager implements LayoutManager {
        private final int prefWidth;
        private final int prefHeight;

        TestLayoutManager(int prefWidth, int prefHeight) {
            this.prefWidth = prefWidth;
            this.prefHeight = prefHeight;
        }

        @Override
        public void layoutContainer(Container parent) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(prefWidth, prefHeight);
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }
    }
}

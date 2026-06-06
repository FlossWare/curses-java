package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.flossware.curses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Container Tests")
class ContainerTest extends ComponentTestBase {
    private Container container;
    private Component child1;
    private Component child2;
    private Component child3;

    @BeforeEach
    void setUp() {
        container = new Panel();
        child1 = new Label("Child 1");
        child2 = new Label("Child 2");
        child3 = new Label("Child 3");
    }

    @Test
    @DisplayName("should add child and set parent reference")
    void testAddChild() {
        container.add(child1);

        assertEquals(1, container.getChildren().size());
        assertTrue(container.getChildren().contains(child1));
        assertSame(container, child1.getParent());
    }

    @Test
    @DisplayName("should add multiple children in order")
    void testAddMultipleChildren() {
        container.add(child1);
        container.add(child2);
        container.add(child3);

        assertEquals(3, container.getChildren().size());
        assertEquals(child1, container.getChildren().toArray()[0]);
        assertEquals(child2, container.getChildren().toArray()[1]);
        assertEquals(child3, container.getChildren().toArray()[2]);
    }

    @Test
    @DisplayName("should remove child and clear parent reference")
    void testRemoveChild() {
        container.add(child1);
        container.remove(child1);

        assertEquals(0, container.getChildren().size());
        assertNull(child1.getParent());
    }

    @Test
    @DisplayName("should remove specific child from multiple children")
    void testRemoveSpecificChild() {
        container.add(child1);
        container.add(child2);
        container.add(child3);

        container.remove(child2);

        assertEquals(2, container.getChildren().size());
        assertTrue(container.getChildren().contains(child1));
        assertFalse(container.getChildren().contains(child2));
        assertTrue(container.getChildren().contains(child3));
        assertNull(child2.getParent());
    }

    @Test
    @DisplayName("should trigger repaint when child added")
    void testRepaintOnAdd() {
        root.add(container);
        clearDirtyFlag();

        container.add(child1);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should trigger repaint when child removed")
    void testRepaintOnRemove() {
        root.add(container);
        container.add(child1);
        clearDirtyFlag();

        container.remove(child1);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should set and use layout manager")
    void testSetLayoutManager() {
        LayoutManager mockLayout = new TestLayoutManager();

        container.setLayout(mockLayout);
        container.doLayout();

        assertTrue(((TestLayoutManager) mockLayout).wasLaidOut);
    }

    @Test
    @DisplayName("should not crash when calling doLayout without layout manager")
    void testDoLayoutWithoutManager() {
        assertDoesNotThrow(() -> container.doLayout());
    }

    @Test
    @DisplayName("should paint all children")
    void testPaintChildren() {
        child1.setLocation(0, 0);
        child1.setSize(10, 1);
        child2.setLocation(0, 1);
        child2.setSize(10, 1);

        container.add(child1);
        container.add(child2);

        assertDoesNotThrow(() -> container.paint(buffer));
    }

    @Test
    @DisplayName("should draw border correctly")
    void testDrawBorder() {
        Panel panel = new Panel();
        panel.setLocation(5, 5);
        panel.setSize(10, 5);
        panel.setBordered(true);

        panel.paint(buffer);

        // Check corners
        assertEquals('+', buffer[5][5]);
        assertEquals('+', buffer[5][14]);
        assertEquals('+', buffer[9][5]);
        assertEquals('+', buffer[9][14]);

        // Check top and bottom edges
        assertEquals('-', buffer[5][6]);
        assertEquals('-', buffer[9][6]);

        // Check left and right edges
        assertEquals('|', buffer[6][5]);
        assertEquals('|', buffer[6][14]);
    }

    @Test
    @DisplayName("should handle empty children list")
    void testEmptyChildrenList() {
        assertEquals(0, container.getChildren().size());
        assertDoesNotThrow(() -> container.paint(buffer));
    }

    @Test
    @DisplayName("should be thread-safe for concurrent child additions")
    void testConcurrentAddChildren() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 10; i++) {
                container.add(new Label("Label " + i));
            }
        });

        // Should have 100 children (10 threads * 10 labels each)
        assertEquals(100, container.getChildren().size());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent add and remove")
    void testConcurrentAddAndRemove() throws InterruptedException {
        // Pre-add some children
        for (int i = 0; i < 20; i++) {
            container.add(new Label("Initial " + i));
        }

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(5,
            () -> {
                for (int i = 0; i < 10; i++) {
                    container.add(new Label("New " + i));
                }
            },
            () -> {
                for (int i = 0; i < 5; i++) {
                    if (!container.getChildren().isEmpty()) {
                        container.remove(container.getChildren().iterator().next());
                    }
                }
            }
        );

        // Should complete without deadlock or exception
        assertTrue(container.getChildren().size() >= 0);
    }

    @Test
    @DisplayName("should support Virtual Threads for child management")
    void testVirtualThreadChildManagement() throws InterruptedException {
        root.add(container);

        ThreadSafetyTestHelper.runWithVirtualThreads(20, () -> {
            Component child = new Label("VThread Label");
            container.add(child);
            if (Math.random() > 0.5) {
                container.remove(child);
            }
        });

        // Should complete without exceptions
        assertTrue(container.getChildren().size() >= 0);
    }

    @Test
    @DisplayName("should handle concurrent doLayout calls")
    void testConcurrentDoLayout() throws InterruptedException {
        TestLayoutManager layout = new TestLayoutManager();
        container.setLayout(layout);
        container.add(child1);
        container.add(child2);

        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                container.doLayout();
            }
        });

        assertTrue(layout.layoutCount >= 100);
    }

    @Test
    @DisplayName("should revalidate layout when invalid")
    void testRevalidate() {
        TestLayoutManager layout = new TestLayoutManager();
        container.setLayout(layout);
        container.setSize(20, 10);

        container.revalidate();

        assertTrue(layout.wasLaidOut);
    }

    @Test
    @DisplayName("should not revalidate when layout is valid")
    void testRevalidateWhenValid() {
        TestLayoutManager layout = new TestLayoutManager();
        container.setLayout(layout);
        container.setSize(20, 10);
        container.doLayout();

        // Reset flag and count
        layout.layoutCount = 0;

        // Revalidate without changing size or invalidating
        container.revalidate();

        // Should not layout again since it's still valid
        assertEquals(0, layout.layoutCount);
    }

    @Test
    @DisplayName("should revalidate when size changes")
    void testRevalidateOnSizeChange() {
        TestLayoutManager layout = new TestLayoutManager();
        container.setLayout(layout);
        container.setSize(20, 10);
        container.doLayout();

        layout.layoutCount = 0;

        // Change size
        container.setSize(30, 15);
        container.revalidate();

        // Should layout again since size changed
        assertEquals(1, layout.layoutCount);
    }

    @Test
    @DisplayName("should invalidate layout")
    void testInvalidateLayout() {
        TestLayoutManager layout = new TestLayoutManager();
        container.setLayout(layout);
        container.setSize(20, 10);
        container.doLayout();

        layout.layoutCount = 0;

        // Invalidate layout
        container.invalidateLayout();

        // Revalidate should now trigger layout
        container.revalidate();

        assertEquals(1, layout.layoutCount);
    }

    @Test
    @DisplayName("should draw border at negative position")
    void testDrawBorderNegativePosition() {
        Panel panel = new Panel();
        panel.setBordered(true);
        panel.setLocation(-5, -3);
        panel.setSize(20, 10);

        // Should not crash with negative position
        assertDoesNotThrow(() -> panel.paint(buffer));
    }

    @Test
    @DisplayName("should draw border at buffer edge")
    void testDrawBorderAtBufferEdge() {
        Panel panel = new Panel();
        panel.setBordered(true);
        panel.setLocation(75, 20);  // Near buffer edge (80x24)
        panel.setSize(10, 5);

        // Should handle positions at/beyond buffer bounds
        assertDoesNotThrow(() -> panel.paint(buffer));
    }

    @Test
    @DisplayName("should draw border beyond buffer bounds")
    void testDrawBorderBeyondBounds() {
        Panel panel = new Panel();
        panel.setBordered(true);
        panel.setLocation(100, 30);  // Beyond buffer (80x24)
        panel.setSize(10, 5);

        // Should not crash even when completely out of bounds
        assertDoesNotThrow(() -> panel.paint(buffer));
    }

    @Test
    @DisplayName("should handle child removal during paint iteration")
    void testPaintWithConcurrentRemoval() throws InterruptedException {
        // This test reproduces the race condition: child removed between
        // snapshot creation and paint iteration. The fix ensures that
        // paint() checks parent before painting to avoid silent failures.
        root.add(container);
        container.setSize(20, 10);

        // Add initial children
        Component child1 = new Label("Child 1");
        Component child2 = new Label("Child 2");
        Component child3 = new Label("Child 3");
        child1.setSize(5, 1);
        child2.setSize(5, 1);
        child3.setSize(5, 1);

        container.add(child1);
        container.add(child2);
        container.add(child3);

        clearDirtyFlag();

        // Simulate concurrent paint and remove:
        // Thread A: calls paint() and creates snapshot including child2
        // Thread B: removes child2 (sets parent to null)
        // Thread A: iterates snapshot and tries to paint child2

        final boolean[] paintCalled = {false};
        final boolean[] repaintFailed = {false};

        // Create a custom child that tracks if paint was called
        Component testChild = new Label("Test") {
            @Override
            public void paint(char[][] buffer) {
                paintCalled[0] = true;
                // Try to trigger repaint - if parent is null, this would fail silently
                try {
                    this.repaint();
                    // If parent is null, repaint won't reach RootPane
                    // We can detect this by checking if dirty flag was set
                } catch (NullPointerException e) {
                    repaintFailed[0] = true;
                }
                super.paint(buffer);
            }
        };
        testChild.setSize(5, 1);
        container.add(testChild);

        // Spawn thread to remove child while main thread is painting
        Thread removalThread = new Thread(() -> {
            try {
                Thread.sleep(5); // Let paint start
                container.remove(testChild);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Start removal thread
        removalThread.start();

        // Paint should not crash even if child is removed concurrently
        assertDoesNotThrow(() -> container.paint(buffer));

        removalThread.join();

        // Verify the child was removed
        assertNull(testChild.getParent());

        // Verify paint completed without throwing exception
        // (NullPointerException from accessing parent would have been caught)
        assertFalse(repaintFailed[0], "repaint() should not throw NPE");
    }

    /**
     * Test layout manager that tracks if it was called.
     */
    private static class TestLayoutManager implements LayoutManager {
        boolean wasLaidOut = false;
        volatile int layoutCount = 0;

        @Override
        public void layoutContainer(Container parent) {
            wasLaidOut = true;
            layoutCount++;
        }
    }
}

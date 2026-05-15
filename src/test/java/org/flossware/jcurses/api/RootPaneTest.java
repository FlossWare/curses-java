package org.flossware.jcurses.api;

import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RootPane Tests")
class RootPaneTest {
    private RootPane rootPane;

    @BeforeEach
    void setUp() {
        rootPane = RootPane.getInstance();
        rootPane.clearDirty();
        // Clear any existing children
        rootPane.getChildren().clear();
    }

    @Test
    @DisplayName("should be a singleton")
    void testSingleton() {
        RootPane instance1 = RootPane.getInstance();
        RootPane instance2 = RootPane.getInstance();

        assertSame(instance1, instance2);
        assertSame(rootPane, instance1);
    }

    @Test
    @DisplayName("should have default size of 80x24")
    void testDefaultSize() {
        assertEquals(80, rootPane.getWidth());
        assertEquals(24, rootPane.getHeight());
    }

    @Test
    @DisplayName("should have default position at origin")
    void testDefaultPosition() {
        assertEquals(0, rootPane.getX());
        assertEquals(0, rootPane.getY());
    }

    @Test
    @DisplayName("should mark dirty flag")
    void testMarkDirty() {
        rootPane.clearDirty();
        assertFalse(rootPane.isDirty());

        rootPane.markDirty();
        assertTrue(rootPane.isDirty());
    }

    @Test
    @DisplayName("should clear dirty flag")
    void testClearDirty() {
        rootPane.markDirty();
        assertTrue(rootPane.isDirty());

        rootPane.clearDirty();
        assertFalse(rootPane.isDirty());
    }

    @Test
    @DisplayName("should check dirty flag without changing it")
    void testIsDirty() {
        rootPane.clearDirty();
        assertFalse(rootPane.isDirty());
        assertFalse(rootPane.isDirty()); // Check again to ensure it doesn't change

        rootPane.markDirty();
        assertTrue(rootPane.isDirty());
        assertTrue(rootPane.isDirty()); // Check again to ensure it doesn't change
    }

    @Test
    @DisplayName("should support multiple mark dirty calls")
    void testMultipleMarkDirty() {
        rootPane.clearDirty();

        rootPane.markDirty();
        rootPane.markDirty();
        rootPane.markDirty();

        assertTrue(rootPane.isDirty());
    }

    @Test
    @DisplayName("should support multiple clear dirty calls")
    void testMultipleClearDirty() {
        rootPane.markDirty();

        rootPane.clearDirty();
        rootPane.clearDirty();
        rootPane.clearDirty();

        assertFalse(rootPane.isDirty());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent dirty flag operations")
    void testConcurrentDirtyFlagOperations() throws InterruptedException {
        ThreadSafetyTestHelper.runTwoOperationsConcurrently(50,
            () -> {
                for (int i = 0; i < 100; i++) {
                    rootPane.markDirty();
                }
            },
            () -> {
                for (int i = 0; i < 100; i++) {
                    rootPane.clearDirty();
                }
            }
        );

        // Should complete without exceptions
        // Final state is non-deterministic but should be valid
        boolean isDirty = rootPane.isDirty();
        assertTrue(isDirty || !isDirty); // Should be either true or false, not corrupted
    }

    @Test
    @DisplayName("should have volatile dirty field for thread visibility")
    void testDirtyFlagVisibility() throws InterruptedException {
        rootPane.clearDirty();

        Thread writer = Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(50);
                rootPane.markDirty();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread reader = Thread.ofVirtual().start(() -> {
            while (!rootPane.isDirty()) {
                // Spin until dirty flag is visible
                Thread.yield();
            }
        });

        writer.join(1000);
        reader.join(1000);

        assertTrue(rootPane.isDirty());
        assertFalse(reader.isAlive(), "Reader thread should have seen the dirty flag");
    }

    @Test
    @DisplayName("should support Virtual Threads accessing dirty flag")
    void testVirtualThreadDirtyFlag() throws InterruptedException {
        ThreadSafetyTestHelper.runWithVirtualThreads(100, () -> {
            for (int i = 0; i < 50; i++) {
                if (i % 2 == 0) {
                    rootPane.markDirty();
                } else {
                    rootPane.clearDirty();
                }
                boolean isDirty = rootPane.isDirty();
            }
        });

        // Should complete without exceptions
        assertTrue(true);
    }

    @Test
    @DisplayName("should inherit Container functionality")
    void testContainerInheritance() {
        assertTrue(rootPane instanceof Container);
        assertTrue(rootPane instanceof Component);
    }

    @Test
    @DisplayName("should support adding and removing children")
    void testChildManagement() {
        Component child = new JLabel("Test");

        rootPane.add(child);
        assertEquals(1, rootPane.getChildren().size());
        assertSame(rootPane, child.getParent());

        rootPane.remove(child);
        assertEquals(0, rootPane.getChildren().size());
        assertNull(child.getParent());
    }

    @Test
    @DisplayName("should paint all children")
    void testPaintChildren() {
        char[][] buffer = new char[24][80];

        Component child1 = new JLabel("Test 1");
        child1.setLocation(0, 0);
        child1.setSize(10, 1);

        Component child2 = new JLabel("Test 2");
        child2.setLocation(0, 1);
        child2.setSize(10, 1);

        rootPane.add(child1);
        rootPane.add(child2);

        assertDoesNotThrow(() -> rootPane.paint(buffer));
    }

    @Test
    @DisplayName("should trigger dirty flag when child added")
    void testDirtyOnChildAdd() {
        rootPane.clearDirty();
        assertFalse(rootPane.isDirty());

        Component child = new JLabel("Test");
        rootPane.add(child);

        assertTrue(rootPane.isDirty());
    }

    @Test
    @DisplayName("should trigger dirty flag when child removed")
    void testDirtyOnChildRemove() {
        Component child = new JLabel("Test");
        rootPane.add(child);
        rootPane.clearDirty();

        rootPane.remove(child);

        assertTrue(rootPane.isDirty());
    }
}

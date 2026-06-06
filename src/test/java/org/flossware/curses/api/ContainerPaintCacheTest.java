package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Container paint() snapshot caching optimization (Issue #71).
 * Verifies that the snapshot cache reduces GC pressure by reusing snapshots
 * when the children list hasn't changed.
 */
@DisplayName("Container Paint Cache Optimization Tests (Issue #71)")
class ContainerPaintCacheTest extends ComponentTestBase {
    private Container container;
    private Component child1;
    private Component child2;

    @BeforeEach
    void setUp() {
        container = new Panel();
        child1 = new Label("Child 1");
        child2 = new Label("Child 2");
    }

    @Test
    @DisplayName("should reuse cached snapshot when children list hasn't changed")
    void testCachedSnapshotReuse() throws Exception {
        // Add children
        container.add(child1);
        container.add(child2);

        // First paint creates snapshot
        container.paint(buffer);

        // Get reference to cached snapshot
        List<Component> firstSnapshot = getCachedSnapshot();
        assertNotNull(firstSnapshot);
        assertEquals(2, firstSnapshot.size());

        // Second paint should reuse the same snapshot
        container.paint(buffer);
        List<Component> secondSnapshot = getCachedSnapshot();
        assertSame(firstSnapshot, secondSnapshot);
    }

    @Test
    @DisplayName("should invalidate snapshot when child added")
    void testSnapshotInvalidationOnAdd() throws Exception {
        container.add(child1);
        container.paint(buffer);

        List<Component> firstSnapshot = getCachedSnapshot();
        assertNotNull(firstSnapshot);
        assertEquals(1, firstSnapshot.size());

        // Add another child
        container.add(child2);

        // Snapshot should be invalidated
        List<Component> invalidatedSnapshot = getCachedSnapshot();
        assertNull(invalidatedSnapshot, "Snapshot should be null after add");

        // After paint, new snapshot should be created
        container.paint(buffer);
        List<Component> newSnapshot = getCachedSnapshot();
        assertNotNull(newSnapshot);
        assertEquals(2, newSnapshot.size());
        assertNotSame(firstSnapshot, newSnapshot);
    }

    @Test
    @DisplayName("should invalidate snapshot when child removed")
    void testSnapshotInvalidationOnRemove() throws Exception {
        container.add(child1);
        container.add(child2);
        container.paint(buffer);

        List<Component> firstSnapshot = getCachedSnapshot();
        assertEquals(2, firstSnapshot.size());

        // Remove a child
        container.remove(child1);

        // Snapshot should be invalidated
        List<Component> invalidatedSnapshot = getCachedSnapshot();
        assertNull(invalidatedSnapshot, "Snapshot should be null after remove");

        // After paint, new snapshot should be created with 1 child
        container.paint(buffer);
        List<Component> newSnapshot = getCachedSnapshot();
        assertNotNull(newSnapshot);
        assertEquals(1, newSnapshot.size());
        assertNotSame(firstSnapshot, newSnapshot);
    }

    @Test
    @DisplayName("should correctly handle multiple consecutive paints with stable children")
    void testMultipleConsecutivePaintsStable() throws Exception {
        container.add(child1);
        container.add(child2);

        // Paint 100 times (simulating 60 FPS over ~1.67 seconds)
        List<Component> firstSnapshot = null;
        for (int i = 0; i < 100; i++) {
            container.paint(buffer);
            List<Component> currentSnapshot = getCachedSnapshot();
            if (i == 0) {
                firstSnapshot = currentSnapshot;
                assertNotNull(firstSnapshot);
            } else {
                // All subsequent paints should reuse the same snapshot
                assertSame(firstSnapshot, currentSnapshot);
            }
        }
    }

    @Test
    @DisplayName("should handle add/remove/paint cycle correctly")
    void testAddRemovePaintCycle() throws Exception {
        // Paint with empty container
        container.paint(buffer);
        List<Component> emptySnapshot = getCachedSnapshot();
        assertNotNull(emptySnapshot);
        assertEquals(0, emptySnapshot.size());

        // Add child and paint
        container.add(child1);
        container.paint(buffer);
        List<Component> oneChildSnapshot = getCachedSnapshot();
        assertNotNull(oneChildSnapshot);
        assertEquals(1, oneChildSnapshot.size());
        assertNotSame(emptySnapshot, oneChildSnapshot);

        // Paint again - should reuse snapshot
        container.paint(buffer);
        assertSame(oneChildSnapshot, getCachedSnapshot());

        // Remove child and paint
        container.remove(child1);
        container.paint(buffer);
        List<Component> emptyAgainSnapshot = getCachedSnapshot();
        assertNotNull(emptyAgainSnapshot);
        assertEquals(0, emptyAgainSnapshot.size());
        assertNotSame(oneChildSnapshot, emptyAgainSnapshot);
    }

    @Test
    @DisplayName("should paint all children correctly even with cached snapshot")
    void testPaintCorrectnessWithCache() throws Exception {
        child1.setLocation(0, 0);
        child1.setSize(10, 1);
        child2.setLocation(0, 2);
        child2.setSize(10, 1);

        container.add(child1);
        container.add(child2);

        // First paint should work
        assertDoesNotThrow(() -> container.paint(buffer));

        // Second paint should also work (using cached snapshot)
        assertDoesNotThrow(() -> container.paint(buffer));

        // Verify snapshot contains both children
        List<Component> snapshot = getCachedSnapshot();
        assertEquals(2, snapshot.size());
        assertTrue(snapshot.contains(child1));
        assertTrue(snapshot.contains(child2));
    }

    /**
     * Helper method to access the private cachedSnapshot field using reflection.
     */
    @SuppressWarnings("unchecked")
    private List<Component> getCachedSnapshot() throws Exception {
        Field field = Container.class.getDeclaredField("cachedSnapshot");
        field.setAccessible(true);
        return (List<Component>) field.get(container);
    }
}

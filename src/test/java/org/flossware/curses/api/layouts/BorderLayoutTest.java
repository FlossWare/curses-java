package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BorderLayout Tests")
class BorderLayoutTest {
    private Container container;
    private BorderLayout layout;
    private Component north, south, east, west, center;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(100, 50);

        layout = new BorderLayout();
        container.setLayout(layout);

        north = new Label("North");
        south = new Label("South");
        east = new Label("East");
        west = new Label("West");
        center = new Label("Center");
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

    @Test
    @DisplayName("should handle concurrent addLayoutComponent and layoutContainer without ConcurrentModificationException")
    void testThreadSafetyConcurrentAddAndLayout() throws InterruptedException {
        int numThreads = 10;
        int numOperations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicBoolean exceptionOccurred = new AtomicBoolean(false);

        container.setSize(100, 50);

        // Create multiple threads: some adding components, others laying out
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < numOperations; i++) {
                        if (threadId % 2 == 0) {
                            // Add threads: add layout components with different constraints
                            Component comp = new Label("Component-" + threadId + "-" + i);
                            String[] constraints = {BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.EAST, BorderLayout.WEST, BorderLayout.CENTER};
                            layout.addLayoutComponent(comp, constraints[i % constraints.length]);
                        } else {
                            // Layout threads: call layoutContainer
                            container.doLayout();
                        }
                    }
                } catch (Exception e) {
                    exceptionOccurred.set(true);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Threads did not complete in time");
        assertFalse(exceptionOccurred.get(), "ConcurrentModificationException or other exception occurred during concurrent access");
    }

    @Test
    @DisplayName("should handle rapid concurrent reads and writes without corruption")
    void testThreadSafetyRapidConcurrency() throws InterruptedException {
        int numThreads = 20;
        int numOperations = 500;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicBoolean exceptionOccurred = new AtomicBoolean(false);

        container.setSize(100, 50);

        // Add initial components
        for (int i = 0; i < 5; i++) {
            Component comp = new Label("Initial-" + i);
            layout.addLayoutComponent(comp, BorderLayout.CENTER);
            container.add(comp);
        }

        // High-contention scenario: many threads reading and writing simultaneously
        for (int t = 0; t < numThreads; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < numOperations; i++) {
                        if (Math.random() < 0.6) {
                            // 60% reads
                            container.doLayout();
                        } else {
                            // 40% writes
                            Component comp = new Label("Concurrent-" + System.nanoTime());
                            layout.addLayoutComponent(comp, BorderLayout.CENTER);
                        }
                    }
                } catch (Exception e) {
                    exceptionOccurred.set(true);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Threads did not complete in time");
        assertFalse(exceptionOccurred.get(), "Exception occurred during rapid concurrent access");
    }
}

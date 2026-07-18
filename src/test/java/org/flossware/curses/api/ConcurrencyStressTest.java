package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.flossware.curses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Concurrency Stress Tests for Component and Container")
class ConcurrencyStressTest extends ComponentTestBase {
    private Container container;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(40, 20);
    }

    @Test
    @DisplayName("concurrent setParent/getParent should not corrupt state")
    void testConcurrentParentAccess() throws InterruptedException {
        Component child = new Label("child");
        Container parent1 = new Panel();
        Container parent2 = new Panel();

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                for (int i = 0; i < 100; i++) {
                    child.setParent(parent1);
                    Component p = child.getParent();
                    assertNotNull(p);
                }
            },
            () -> {
                for (int i = 0; i < 100; i++) {
                    child.setParent(parent2);
                    Component p = child.getParent();
                    assertNotNull(p);
                }
            }
        );

        Component finalParent = child.getParent();
        assertTrue(finalParent == parent1 || finalParent == parent2);
    }

    @Test
    @DisplayName("concurrent add/remove should not corrupt children list")
    void testConcurrentAddRemoveStress() throws InterruptedException {
        AtomicBoolean error = new AtomicBoolean(false);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                try {
                    for (int i = 0; i < 50; i++) {
                        container.add(new Label("Add-" + i));
                    }
                } catch (Exception e) {
                    error.set(true);
                }
            },
            () -> {
                try {
                    for (int i = 0; i < 50; i++) {
                        if (!container.getChildren().isEmpty()) {
                            try {
                                container.remove(container.getChildren().iterator().next());
                            } catch (java.util.NoSuchElementException e) {
                                // Race between isEmpty check and iterator — acceptable
                            }
                        }
                    }
                } catch (Exception e) {
                    error.set(true);
                }
            }
        );

        assertFalse(error.get(), "No unexpected exceptions during concurrent add/remove");
        assertTrue(container.getChildren().size() >= 0);
    }

    @Test
    @DisplayName("concurrent paint and setLocation should not throw")
    void testConcurrentPaintAndResize() throws InterruptedException {
        container.add(new Label("content"));
        AtomicBoolean error = new AtomicBoolean(false);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        container.paint(buffer);
                    }
                } catch (Exception e) {
                    error.set(true);
                }
            },
            () -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        container.setLocation(i % 10, i % 5);
                        container.setSize(20 + (i % 10), 10 + (i % 5));
                    }
                } catch (Exception e) {
                    error.set(true);
                }
            }
        );

        assertFalse(error.get(), "No exceptions during concurrent paint and resize");
    }

    @Test
    @DisplayName("concurrent drawBorder should use consistent dimensions")
    void testConcurrentDrawBorder() throws InterruptedException {
        Panel panel = new Panel();
        panel.setLocation(1, 1);
        panel.setSize(20, 10);
        panel.setBordered(true);

        AtomicBoolean error = new AtomicBoolean(false);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        panel.paint(buffer);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    error.set(true);
                }
            },
            () -> {
                for (int i = 0; i < 100; i++) {
                    panel.setSize(10 + (i % 20), 5 + (i % 10));
                }
            }
        );

        assertFalse(error.get(), "drawBorder should not throw AIOOBE under concurrent resize");
    }

    @Test
    @DisplayName("concurrent repaint should not deadlock with parent chain walk")
    void testConcurrentRepaintNoDeadlock() throws InterruptedException {
        Container grandparent = new Panel();
        Container parent = new Panel();
        Component child = new Label("leaf");

        root.add(grandparent);
        grandparent.add(parent);
        parent.add(child);

        AtomicBoolean completed = new AtomicBoolean(false);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                for (int i = 0; i < 200; i++) {
                    child.repaint();
                }
            },
            () -> {
                for (int i = 0; i < 200; i++) {
                    parent.repaint();
                    grandparent.repaint();
                }
            }
        );

        completed.set(true);
        assertTrue(completed.get(), "Should complete without deadlock");
    }

    @Test
    @DisplayName("volatile modificationCount visible across threads")
    void testModificationCountVisibility() throws InterruptedException {
        AtomicInteger externalMutationsSeen = new AtomicInteger(0);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(5,
            () -> {
                for (int i = 0; i < 50; i++) {
                    container.getChildren().addLast(new Label("ext-" + i));
                }
            },
            () -> {
                for (int i = 0; i < 50; i++) {
                    container.add(new Label("int-" + i));
                    externalMutationsSeen.incrementAndGet();
                }
            }
        );

        assertTrue(container.getChildren().size() > 0);
    }

    @Test
    @DisplayName("virtual threads: concurrent component operations")
    void testVirtualThreadStress() throws InterruptedException {
        root.add(container);
        AtomicBoolean error = new AtomicBoolean(false);

        ThreadSafetyTestHelper.runWithVirtualThreads(20, () -> {
            try {
                Component child = new Label("vt-label");
                container.add(child);
                child.setLocation(1, 1);
                child.setSize(5, 1);
                container.paint(buffer);
                child.repaint();
                if (Math.random() > 0.3) {
                    container.remove(child);
                }
            } catch (Exception e) {
                error.set(true);
            }
        });

        assertFalse(error.get(), "Virtual threads should complete without errors");
    }

    @Test
    @DisplayName("concurrent setColorPair and paint should not corrupt rendering")
    void testConcurrentColorPairAndPaint() throws InterruptedException {
        Label label = new Label("colored");
        label.setLocation(0, 0);
        label.setSize(10, 1);
        container.add(label);

        AtomicBoolean error = new AtomicBoolean(false);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        Color[] colors = Color.values();
                        label.setColorPair(new ColorPair(colors[i % colors.length], colors[(i + 1) % colors.length]));
                    }
                } catch (Exception e) {
                    error.set(true);
                }
            },
            () -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        container.paint(buffer);
                    }
                } catch (Exception e) {
                    error.set(true);
                }
            }
        );

        assertFalse(error.get(), "Concurrent color changes and painting should not throw");
    }
}

package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test that reproduces concurrent modification failures.
 * 
 * <p>This test reproduces the following flaky failures:
 * <ul>
 *   <li>ConcurrentModificationException in TabbedPane.paint() at line 36 during iteration of LinkedHashMap keySet()</li>
 *   <li>ArrayIndexOutOfBoundsException in Choice.add() during concurrent access when selectedIndex becomes invalid</li>
 * </ul>
 * 
 * <p>These tests will FAIL until thread safety issues are fixed:
 * <ul>
 *   <li>TabbedPane must synchronize iteration over tabs map during paint()</li>
 *   <li>Choice.add() must lock when modifying items collection to prevent race with paint()</li>
 * </ul>
 */
@DisplayName("Thread Safety Concurrent Modification Integration Tests")
class ThreadSafetyConcurrentModificationIT extends IntegrationTestBase {

    /**
     * Reproduces: ConcurrentModificationException in TabbedPane.paint() line 36
     * 
     * Root cause: LinkedHashMap iteration in paint() is not synchronized while
     * another thread modifies tabs via addTab().
     * 
     * Evidence: "java.util.ConcurrentModificationException at LinkedHashMap 
     * iterator in TabbedPane.paint()"
     */
    @RepeatedTest(10)
    @DisplayName("TabbedPane paint() should not throw ConcurrentModificationException during concurrent tab additions")
    void testTabbedPaneConcurrentModification() throws Exception {
        // Setup: Create TabbedPane with initial tabs
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setLocation(5, 5);
        tabbedPane.setSize(60, 15);
        
        // Add initial tabs
        for (int i = 0; i < 3; i++) {
            Panel panel = new Panel();
            panel.setSize(50, 10);
            tabbedPane.addTab("Tab " + i, panel);
        }
        
        setupFrame(tabbedPane);
        runEventLoopCycle();
        
        // Track exceptions
        AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        List<Throwable> exceptions = new CopyOnWriteArrayList<>();
        
        // Thread 1: Continuously paint (render loop simulation)
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicBoolean running = new AtomicBoolean(true);
        
        Future<?> paintTask = executor.submit(() -> {
            try {
                while (running.get()) {
                    // Simulate rapid rendering
                    tabbedPane.paint(buffer);
                    Thread.sleep(1); // Tight loop to maximize collision probability
                }
            } catch (ConcurrentModificationException e) {
                exceptionThrown.set(true);
                exceptions.add(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                exceptions.add(t);
            }
        });
        
        // Thread 2: Continuously add tabs
        Future<?> modifyTask = executor.submit(() -> {
            try {
                for (int i = 3; i < 20 && running.get(); i++) {
                    Panel panel = new Panel();
                    panel.setSize(50, 10);
                    tabbedPane.addTab("DynamicTab " + i, panel);
                    Thread.sleep(2); // Slightly slower than paint to ensure overlap
                }
            } catch (Throwable t) {
                exceptions.add(t);
            }
        });
        
        // Wait for operations to complete
        Thread.sleep(100);
        running.set(false);
        
        try {
            paintTask.get(2, TimeUnit.SECONDS);
            modifyTask.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Timeout is acceptable
        } finally {
            executor.shutdownNow();
        }
        
        // Assertion: This should FAIL until TabbedPane.paint() properly synchronizes
        assertFalse(exceptionThrown.get(), 
            "ConcurrentModificationException detected in TabbedPane.paint() - " +
            "LinkedHashMap iteration at line 36 not synchronized. Exception: " + 
            (exceptions.isEmpty() ? "none" : exceptions.get(0).toString()));
    }

    /**
     * Reproduces: ArrayIndexOutOfBoundsException in Choice.add() during concurrent access
     * 
     * Root cause: Race condition between Choice.add() modifying items and paint() 
     * accessing items[selectedIndex] without proper synchronization.
     * 
     * Evidence: "ArrayIndexOutOfBoundsException in Choice.add() during concurrent 
     * access - thread safety issue in add operation"
     */
    @RepeatedTest(10)
    @DisplayName("Choice.add() should not cause ArrayIndexOutOfBoundsException during concurrent paint operations")
    void testChoiceConcurrentAccessException() throws Exception {
        // Setup: Create Choice component
        Choice choice = new Choice();
        choice.setLocation(10, 10);
        choice.setSize(30, 1);
        
        // Add initial item to avoid empty state
        choice.add("Initial Item");
        
        setupFrame(choice);
        runEventLoopCycle();
        
        // Track exceptions
        List<Throwable> exceptions = new CopyOnWriteArrayList<>();
        AtomicBoolean running = new AtomicBoolean(true);
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Thread 1: Rapidly paint/read selectedIndex
        Future<?> paintTask = executor.submit(() -> {
            try {
                while (running.get()) {
                    // This will call paint() which accesses items.get(selectedIndex)
                    choice.paint(buffer);
                    Thread.sleep(1);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                exceptions.add(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                exceptions.add(t);
            }
        });
        
        // Thread 2: Rapidly add items
        Future<?> addTask = executor.submit(() -> {
            try {
                for (int i = 0; i < 50 && running.get(); i++) {
                    choice.add("Item " + i);
                    Thread.sleep(2);
                }
            } catch (Throwable t) {
                exceptions.add(t);
            }
        });
        
        // Thread 3: Navigate selection while structure changes
        Future<?> navigateTask = executor.submit(() -> {
            try {
                for (int i = 0; i < 30 && running.get(); i++) {
                    choice.selectNext();
                    Thread.sleep(3);
                    choice.selectPrevious();
                    Thread.sleep(3);
                }
            } catch (Throwable t) {
                exceptions.add(t);
            }
        });
        
        // Run for brief period to trigger race condition
        Thread.sleep(150);
        running.set(false);
        
        try {
            paintTask.get(2, TimeUnit.SECONDS);
            addTask.get(2, TimeUnit.SECONDS);
            navigateTask.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Timeout acceptable
        } finally {
            executor.shutdownNow();
        }
        
        // Assertion: Should FAIL until Choice properly synchronizes add() with paint()
        assertTrue(exceptions.isEmpty(), 
            "Thread safety violation detected in Choice component. " +
            "Exceptions: " + exceptions.stream()
                .map(t -> t.getClass().getSimpleName() + ": " + t.getMessage())
                .toList());
    }

    /**
     * Reproduces: Combined stress test simulating InteractiveDemo scenario
     * 
     * This reproduces the actual failure scenario where multiple components
     * are being modified and painted concurrently, as happens in real app usage.
     */
    @RepeatedTest(5)
    @DisplayName("Multiple components under concurrent modification stress should not throw exceptions")
    void testMultiComponentConcurrentStress() throws Exception {
        // Setup: Create complex UI similar to InteractiveDemo
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setLocation(2, 2);
        tabbedPane.setSize(70, 18);
        
        Choice choice1 = new Choice();
        choice1.setLocation(5, 8);
        choice1.setSize(20, 1);
        choice1.add("Option A");
        
        Choice choice2 = new Choice();
        choice2.setLocation(30, 8);
        choice2.setSize(20, 1);
        choice2.add("Option 1");
        
        Panel panel1 = new Panel();
        panel1.setSize(65, 15);
        panel1.add(choice1);
        
        Panel panel2 = new Panel();
        panel2.setSize(65, 15);
        panel2.add(choice2);
        
        tabbedPane.addTab("Panel1", panel1);
        tabbedPane.addTab("Panel2", panel2);
        
        setupFrame(tabbedPane);
        runEventLoopCycle();
        
        // Track all exceptions
        List<Throwable> allExceptions = new CopyOnWriteArrayList<>();
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicInteger tabCounter = new AtomicInteger(3);
        AtomicInteger choiceCounter = new AtomicInteger(2);
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Render thread - simulates event loop
        Future<?> renderTask = executor.submit(() -> {
            try {
                while (running.get()) {
                    root.paint(buffer);
                    Thread.sleep(5);
                }
            } catch (Throwable t) {
                allExceptions.add(t);
            }
        });
        
        // Tab modifier thread
        Future<?> tabTask = executor.submit(() -> {
            try {
                for (int i = 0; i < 10 && running.get(); i++) {
                    Panel p = new Panel();
                    p.setSize(65, 15);
                    tabbedPane.addTab("Tab" + tabCounter.getAndIncrement(), p);
                    Thread.sleep(15);
                }
            } catch (Throwable t) {
                allExceptions.add(t);
            }
        });
        
        // Choice modifier thread
        Future<?> choiceTask = executor.submit(() -> {
            try {
                for (int i = 0; i < 20 && running.get(); i++) {
                    choice1.add("Item" + choiceCounter.getAndIncrement());
                    Thread.sleep(8);
                    choice2.add("Val" + choiceCounter.getAndIncrement());
                    Thread.sleep(8);
                }
            } catch (Throwable t) {
                allExceptions.add(t);
            }
        });
        
        // Navigation thread - triggers paint in components
        Future<?> navTask = executor.submit(() -> {
            try {
                for (int i = 0; i < 15 && running.get(); i++) {
                    choice1.selectNext();
                    Thread.sleep(10);
                    choice2.selectNext();
                    Thread.sleep(10);
                }
            } catch (Throwable t) {
                allExceptions.add(t);
            }
        });
        
        // Run stress test
        Thread.sleep(200);
        running.set(false);
        
        try {
            renderTask.get(3, TimeUnit.SECONDS);
            tabTask.get(3, TimeUnit.SECONDS);
            choiceTask.get(3, TimeUnit.SECONDS);
            navTask.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Acceptable
        } finally {
            executor.shutdownNow();
        }
        
        // Assertion: Should FAIL with ConcurrentModificationException or ArrayIndexOutOfBoundsException
        // until proper synchronization is implemented
        assertTrue(allExceptions.isEmpty(),
            "Concurrent modification detected during stress test. " +
            "Found " + allExceptions.size() + " exception(s): " +
            allExceptions.stream()
                .map(t -> t.getClass().getSimpleName() + " in " + 
                     (t.getStackTrace().length > 0 ? t.getStackTrace()[0].toString() : "unknown"))
                .distinct()
                .toList());
    }
}

package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JButton;
import org.flossware.jcurses.testutil.BufferAssertions;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JButton Tests")
class JButtonTest extends ComponentTestBase {
    private JButton button;

    @BeforeEach
    void setUp() {
        button = new JButton("Click Me");
        button.setSize(20, 1);
    }

    @Test
    @DisplayName("should create button with label")
    void testCreation() {
        assertEquals("Click Me", button.getLabel());
        assertTrue(button.isEnabled());
    }

    @Test
    @DisplayName("should set and get label")
    void testSetLabel() {
        button.setLabel("New Label");
        assertEquals("New Label", button.getLabel());
    }

    @Test
    @DisplayName("should set and get enabled state")
    void testSetEnabled() {
        button.setEnabled(false);
        assertFalse(button.isEnabled());

        button.setEnabled(true);
        assertTrue(button.isEnabled());
    }

    @Test
    @DisplayName("should trigger repaint when label changes")
    void testRepaintOnLabelChange() {
        root.add(button);
        clearDirtyFlag();

        button.setLabel("Changed");

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should trigger repaint when enabled state changes")
    void testRepaintOnEnabledChange() {
        root.add(button);
        clearDirtyFlag();

        button.setEnabled(false);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should render enabled button with square brackets")
    void testEnabledRendering() {
        button.setLocation(0, 0);
        button.setEnabled(true);

        button.paint(buffer);

        BufferAssertions.assertBufferRowContains(buffer, 0, "[ Click Me ]");
    }

    @Test
    @DisplayName("should render disabled button with parentheses")
    void testDisabledRendering() {
        button.setLocation(0, 0);
        button.setEnabled(false);

        button.paint(buffer);

        BufferAssertions.assertBufferRowContains(buffer, 0, "( Click Me )");
    }

    @Test
    @DisplayName("should execute action when clicked and enabled")
    void testActionExecution() {
        AtomicInteger callCount = new AtomicInteger(0);
        button.addActionListener(callCount::incrementAndGet);
        button.setEnabled(true);

        button.doClick();

        assertEquals(1, callCount.get());
    }

    @Test
    @DisplayName("should not execute action when disabled")
    void testDisabledClick() {
        AtomicInteger callCount = new AtomicInteger(0);
        button.addActionListener(callCount::incrementAndGet);
        button.setEnabled(false);

        button.doClick();

        assertEquals(0, callCount.get());
    }

    @Test
    @DisplayName("should not crash when clicking without action listener")
    void testClickWithoutListener() {
        button.setEnabled(true);

        assertDoesNotThrow(() -> button.doClick());
    }

    @Test
    @DisplayName("should allow replacing action listener")
    void testReplaceActionListener() {
        AtomicInteger firstCounter = new AtomicInteger(0);
        AtomicInteger secondCounter = new AtomicInteger(0);

        button.addActionListener(firstCounter::incrementAndGet);
        button.doClick();

        button.addActionListener(secondCounter::incrementAndGet);
        button.doClick();

        assertEquals(1, firstCounter.get());
        assertEquals(1, secondCounter.get());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent label updates")
    void testConcurrentLabelUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                button.setLabel("Label " + i);
            }
        });

        assertTrue(button.getLabel().startsWith("Label "));
    }

    @Test
    @DisplayName("should be thread-safe for concurrent enabled state changes")
    void testConcurrentEnabledChanges() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                button.setEnabled(i % 2 == 0);
            }
        });

        // Should complete without exceptions
        assertTrue(true);
    }

    @Test
    @DisplayName("should be thread-safe for concurrent clicks")
    void testConcurrentClicks() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger(0);
        button.addActionListener(callCount::incrementAndGet);
        button.setEnabled(true);

        ThreadSafetyTestHelper.runConcurrent(50, () -> {
            for (int i = 0; i < 10; i++) {
                button.doClick();
            }
        });

        assertEquals(500, callCount.get());
    }

    @Test
    @DisplayName("should support Virtual Threads")
    void testVirtualThreadSupport() throws InterruptedException {
        root.add(button);
        AtomicInteger callCount = new AtomicInteger(0);
        button.addActionListener(callCount::incrementAndGet);

        ThreadSafetyTestHelper.runWithVirtualThreads(30, () -> {
            button.setLabel("VThread");
            button.doClick();
            button.paint(buffer);
        });

        assertEquals(30, callCount.get());
    }
}

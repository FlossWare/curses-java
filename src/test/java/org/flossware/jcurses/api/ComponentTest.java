package org.flossware.jcurses.api;

import org.flossware.jcurses.testutil.BufferAssertions;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Component Tests")
class ComponentTest extends ComponentTestBase {
    private Component component;

    @BeforeEach
    void setUp() {
        // Use JLabel as a concrete implementation of Component for testing
        component = new JLabel("Test");
    }

    @Test
    @DisplayName("should set and get location")
    void testSetAndGetLocation() {
        component.setLocation(10, 20);

        assertEquals(10, component.getX());
        assertEquals(20, component.getY());
    }

    @Test
    @DisplayName("should set and get size")
    void testSetAndGetSize() {
        component.setSize(100, 50);

        assertEquals(100, component.getWidth());
        assertEquals(50, component.getHeight());
    }

    @Test
    @DisplayName("should initialize with default values")
    void testDefaultValues() {
        Component newComponent = new JLabel("New");

        assertEquals(0, newComponent.getX());
        assertEquals(0, newComponent.getY());
        assertEquals(0, newComponent.getWidth());
        assertEquals(0, newComponent.getHeight());
        assertNull(newComponent.getParent());
    }

    @Test
    @DisplayName("should set and get parent")
    void testSetAndGetParent() {
        Container parent = new JPanel();

        component.setParent(parent);

        assertSame(parent, component.getParent());
    }

    @Test
    @DisplayName("should propagate repaint to RootPane")
    void testRepaintPropagation() {
        root.add(component);
        clearDirtyFlag();

        component.repaint();

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should propagate repaint through hierarchy")
    void testRepaintThroughHierarchy() {
        Container panel = new JPanel();
        root.add(panel);
        panel.add(component);
        clearDirtyFlag();

        component.repaint();

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should write string to buffer at correct position")
    void testWriteStringToBuffer() {
        component.setLocation(5, 10);

        // Access protected method via reflection or use a test subclass
        // For this test, we'll create a test component that exposes the method
        TestComponent testComp = new TestComponent();
        testComp.testWriteString(buffer, "Hello", 5, 10);

        BufferAssertions.assertBufferContains(buffer, 5, 10, "Hello");
    }

    @Test
    @DisplayName("should handle buffer bounds when writing string")
    void testWriteStringToBufferBounds() {
        TestComponent testComp = new TestComponent();

        // Test writing outside buffer bounds (should not crash)
        assertDoesNotThrow(() -> {
            testComp.testWriteString(buffer, "Test", -1, 5);
            testComp.testWriteString(buffer, "Test", 5, -1);
            testComp.testWriteString(buffer, "Test", 5, 1000);
            testComp.testWriteString(buffer, "Test", 1000, 5);
        });
    }

    @Test
    @DisplayName("should be thread-safe for concurrent location updates")
    void testConcurrentLocationUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                component.setLocation(i, i * 2);
            }
        });

        // Should complete without deadlock or exception
        assertTrue(component.getX() >= 0);
        assertTrue(component.getY() >= 0);
    }

    @Test
    @DisplayName("should be thread-safe for concurrent size updates")
    void testConcurrentSizeUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                component.setSize(i + 10, i + 20);
            }
        });

        // Should complete without deadlock or exception
        assertTrue(component.getWidth() >= 10);
        assertTrue(component.getHeight() >= 20);
    }

    @Test
    @DisplayName("should support Virtual Threads")
    void testVirtualThreadCompatibility() throws InterruptedException {
        root.add(component);

        ThreadSafetyTestHelper.runWithVirtualThreads(50, () -> {
            component.setLocation((int) (Math.random() * 100), (int) (Math.random() * 100));
            component.setSize((int) (Math.random() * 100), (int) (Math.random() * 100));
            component.repaint();
        });

        // Should complete without exceptions
        assertTrue(root.isDirty());
    }

    @Test
    @DisplayName("should handle concurrent paint and state updates")
    void testConcurrentPaintAndStateUpdates() throws InterruptedException {
        component.setLocation(0, 0);
        component.setSize(20, 1);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(5,
            () -> component.setLocation((int) (Math.random() * 10), (int) (Math.random() * 10)),
            () -> component.paint(buffer)
        );

        // Should complete without deadlock or exception
        assertTrue(true);
    }

    /**
     * Test component that exposes protected writeStringToBuffer method for testing.
     */
    private static class TestComponent extends Component {
        @Override
        public void paint(char[][] buffer) {
            // No-op for testing
        }

        public void testWriteString(char[][] buffer, String text, int x, int y) {
            writeStringToBuffer(buffer, text, x, y);
        }
    }
}

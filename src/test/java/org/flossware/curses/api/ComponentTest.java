package org.flossware.curses.api;

import org.flossware.curses.testutil.BufferAssertions;
import org.flossware.curses.testutil.ComponentTestBase;
import org.flossware.curses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Component Tests")
class ComponentTest extends ComponentTestBase {
    private Component component;

    @BeforeEach
    void setUp() {
        // Use Label as a concrete implementation of Component for testing
        component = new Label("Test");
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
        Component newComponent = new Label("New");

        assertEquals(0, newComponent.getX());
        assertEquals(0, newComponent.getY());
        assertEquals(0, newComponent.getWidth());
        assertEquals(0, newComponent.getHeight());
        assertNull(newComponent.getParent());
    }

    @Test
    @DisplayName("should set and get parent")
    void testSetAndGetParent() {
        Container parent = new Panel();

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
        Container panel = new Panel();
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

    @Test
    @DisplayName("should set and get accessible name")
    void testAccessibleName() {
        component.setAccessibleName("Test Button");
        assertEquals("Test Button", component.getAccessibleName());
    }

    @Test
    @DisplayName("accessible name should be null by default")
    void testAccessibleNameDefault() {
        Component newComponent = new Label("Test");
        assertNull(newComponent.getAccessibleName());
    }

    @Test
    @DisplayName("should set and get accessible role")
    void testAccessibleRole() {
        component.setAccessibleRole("button");
        assertEquals("button", component.getAccessibleRole());
    }

    @Test
    @DisplayName("accessible role should be null by default")
    void testAccessibleRoleDefault() {
        Component newComponent = new Label("Test");
        assertNull(newComponent.getAccessibleRole());
    }

    @Test
    @DisplayName("should set and get accessible description")
    void testAccessibleDescription() {
        component.setAccessibleDescription("This is a test button that does something");
        assertEquals("This is a test button that does something", component.getAccessibleDescription());
    }

    @Test
    @DisplayName("accessible description should be null by default")
    void testAccessibleDescriptionDefault() {
        Component newComponent = new Label("Test");
        assertNull(newComponent.getAccessibleDescription());
    }

    @Test
    @DisplayName("getAccessibilitySummary should include role")
    void testAccessibilitySummaryWithRole() {
        component.setAccessibleRole("button");
        component.setAccessibleName("Submit");
        component.setAccessibleDescription("Submits the form");

        String summary = component.getAccessibilitySummary();

        assertTrue(summary.contains("button"));
        assertTrue(summary.contains("Submit"));
        assertTrue(summary.contains("Submits the form"));
    }

    @Test
    @DisplayName("getAccessibilitySummary should use class name if no role")
    void testAccessibilitySummaryWithoutRole() {
        component.setAccessibleName("Test Label");
        component.setLocation(5, 10);
        component.setSize(20, 1);

        String summary = component.getAccessibilitySummary();

        assertTrue(summary.contains("Label"));
        assertTrue(summary.contains("Test Label"));
        assertTrue(summary.contains("(5, 10)"));
        assertTrue(summary.contains("(20, 1)"));
    }

    @Test
    @DisplayName("getAccessibilitySummary should handle minimal info")
    void testAccessibilitySummaryMinimal() {
        Component newComponent = new Label("Test");
        newComponent.setLocation(0, 0);
        newComponent.setSize(10, 1);

        String summary = newComponent.getAccessibilitySummary();

        assertTrue(summary.contains("Label"));
        assertTrue(summary.contains("(0, 0)"));
        assertTrue(summary.contains("(10, 1)"));
    }

    @Test
    @DisplayName("should add mouse listener")
    void testAddMouseListener() {
        MouseListener listener = event -> {};

        assertDoesNotThrow(() -> component.addMouseListener(listener));
    }

    @Test
    @DisplayName("should throw when adding null mouse listener")
    void testAddNullMouseListener() {
        assertThrows(IllegalArgumentException.class, () -> {
            component.addMouseListener(null);
        });
    }

    @Test
    @DisplayName("should remove mouse listener")
    void testRemoveMouseListener() {
        MouseListener listener = event -> {};
        component.addMouseListener(listener);

        assertDoesNotThrow(() -> component.removeMouseListener(listener));
    }

    @Test
    @DisplayName("should throw when removing null mouse listener")
    void testRemoveNullMouseListener() {
        assertThrows(IllegalArgumentException.class, () -> {
            component.removeMouseListener(null);
        });
    }

    @Test
    @DisplayName("should handle mouse event within bounds")
    void testHandleMouseEventWithinBounds() {
        component.setLocation(10, 10);
        component.setSize(20, 5);

        boolean[] listenerCalled = {false};
        MouseListener listener = event -> listenerCalled[0] = true;
        component.addMouseListener(listener);

        org.flossware.curses.events.MouseEvent event =
            new org.flossware.curses.events.MouseEvent(15, 12, 1);

        boolean handled = component.handleMouseEvent(event);

        assertTrue(handled);
        assertTrue(listenerCalled[0]);
    }

    @Test
    @DisplayName("should not handle mouse event outside bounds")
    void testHandleMouseEventOutsideBounds() {
        component.setLocation(10, 10);
        component.setSize(20, 5);

        boolean[] listenerCalled = {false};
        MouseListener listener = event -> listenerCalled[0] = true;
        component.addMouseListener(listener);

        org.flossware.curses.events.MouseEvent event =
            new org.flossware.curses.events.MouseEvent(50, 50, 1);

        boolean handled = component.handleMouseEvent(event);

        assertFalse(handled);
        assertFalse(listenerCalled[0]);
    }

    @Test
    @DisplayName("should set and get color pair")
    void testSetAndGetColorPair() {
        ColorPair colorPair = new ColorPair(Color.CYAN, Color.BLACK);
        component.setColorPair(colorPair);

        assertEquals(colorPair, component.getColorPair());
    }

    @Test
    @DisplayName("should have default color pair")
    void testDefaultColorPair() {
        Component newComponent = new Label("Test");
        assertEquals(ColorPair.DEFAULT, newComponent.getColorPair());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent accessible name updates")
    void testConcurrentAccessibleNameUpdates() throws InterruptedException {
        String[] names = {"Name1", "Name2", "Name3", "Name4", "Name5"};

        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                String name = names[i % names.length];
                component.setAccessibleName(name);
                String retrieved = component.getAccessibleName();
                assertNotNull(retrieved, "Accessible name should not be null during concurrent writes");
            }
        });
    }

    @Test
    @DisplayName("should be thread-safe for concurrent accessible role updates")
    void testConcurrentAccessibleRoleUpdates() throws InterruptedException {
        String[] roles = {"button", "label", "dialog", "checkbox", "textfield"};

        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                String role = roles[i % roles.length];
                component.setAccessibleRole(role);
                String retrieved = component.getAccessibleRole();
                assertNotNull(retrieved, "Accessible role should not be null during concurrent writes");
            }
        });
    }

    @Test
    @DisplayName("should be thread-safe for concurrent accessible description updates")
    void testConcurrentAccessibleDescriptionUpdates() throws InterruptedException {
        String[] descriptions = {"Desc1", "Desc2", "Desc3", "Desc4", "Desc5"};

        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                String desc = descriptions[i % descriptions.length];
                component.setAccessibleDescription(desc);
                String retrieved = component.getAccessibleDescription();
                assertNotNull(retrieved, "Accessible description should not be null during concurrent writes");
            }
        });
    }

    @Test
    @DisplayName("should be thread-safe for concurrent accessibility summary reads and writes")
    void testConcurrentAccessibilitySummaryRaceCondition() throws InterruptedException {
        component.setLocation(10, 20);
        component.setSize(50, 30);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(5,
            () -> {
                component.setAccessibleName("TestName");
                component.setAccessibleRole("button");
                component.setAccessibleDescription("Test Description");
            },
            () -> {
                String summary = component.getAccessibilitySummary();
                assertNotNull(summary, "Accessibility summary should never be null");
                assertTrue(summary.contains("(10, 20)"), "Summary should contain position");
                assertTrue(summary.contains("(50, 30)"), "Summary should contain size");
            }
        );
    }

    @Test
    @DisplayName("should be thread-safe when reading all accessibility fields concurrently")
    void testConcurrentAccessibilityFieldReads() throws InterruptedException {
        component.setAccessibleName("TestName");
        component.setAccessibleRole("button");
        component.setAccessibleDescription("TestDescription");

        ThreadSafetyTestHelper.runConcurrent(20, () -> {
            for (int i = 0; i < 100; i++) {
                String name = component.getAccessibleName();
                String role = component.getAccessibleRole();
                String desc = component.getAccessibleDescription();

                assertEquals(name, "TestName");
                assertEquals(role, "button");
                assertEquals(desc, "TestDescription");
            }
        });
    }

    @Test
    @DisplayName("should handle rapid alternating accessibility updates")
    void testRapidAlternatingAccessibilityUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(5, () -> {
            for (int i = 0; i < 50; i++) {
                component.setAccessibleName("Name" + i);
                component.setAccessibleRole("role" + (i % 3));
                component.setAccessibleDescription("Desc" + i);

                String summary = component.getAccessibilitySummary();
                assertNotNull(summary);
            }
        });
    }

    @Test
    @DisplayName("should be thread-safe when reading position/size during concurrent updates")
    void testConcurrentPositionSizeReads() throws InterruptedException {
        component.setLocation(0, 0);
        component.setSize(100, 50);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(10,
            () -> {
                // Writers: continuously update position and size
                for (int i = 0; i < 100; i++) {
                    component.setLocation(i, i * 2);
                    component.setSize(100 + i, 50 + i);
                }
            },
            () -> {
                // Readers: continuously read position and size
                for (int i = 0; i < 100; i++) {
                    int x = component.getX();
                    int y = component.getY();
                    int width = component.getWidth();
                    int height = component.getHeight();

                    // All values should be non-negative and consistent
                    assertTrue(x >= 0, "X coordinate should not be negative");
                    assertTrue(y >= 0, "Y coordinate should not be negative");
                    assertTrue(width >= 100, "Width should be at least 100");
                    assertTrue(height >= 50, "Height should be at least 50");
                }
            }
        );
    }

    @Test
    @DisplayName("should be thread-safe when handling mouse events during concurrent updates")
    void testConcurrentMouseEventHandlingWithPositionUpdates() throws InterruptedException {
        component.setLocation(10, 10);
        component.setSize(50, 30);

        boolean[] listenerCalled = {false};
        MouseListener listener = event -> listenerCalled[0] = true;
        component.addMouseListener(listener);

        ThreadSafetyTestHelper.runTwoOperationsConcurrently(5,
            () -> {
                // Writers: continuously update position and size
                for (int i = 0; i < 50; i++) {
                    component.setLocation(10 + i % 20, 10 + i % 20);
                    component.setSize(50 + (i % 10), 30 + (i % 10));
                }
            },
            () -> {
                // Readers: handle mouse events within bounds
                for (int i = 0; i < 50; i++) {
                    org.flossware.curses.events.MouseEvent event =
                        new org.flossware.curses.events.MouseEvent(25, 25, 1);
                    // Should not throw or deadlock
                    component.handleMouseEvent(event);
                }
            }
        );
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

package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JLabel;
import org.flossware.jcurses.testutil.BufferAssertions;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JLabel Tests")
class JLabelTest extends ComponentTestBase {
    private JLabel label;

    @BeforeEach
    void setUp() {
        label = new JLabel("Test Label");
        label.setSize(20, 1);
    }

    @Test
    @DisplayName("should create label with text")
    void testCreation() {
        assertEquals("Test Label", label.getText());
    }

    @Test
    @DisplayName("should create empty label with default constructor")
    void testEmptyCreation() {
        JLabel emptyLabel = new JLabel();
        assertEquals("", emptyLabel.getText());
    }

    @Test
    @DisplayName("should set and get text")
    void testSetText() {
        label.setText("New Text");
        assertEquals("New Text", label.getText());
    }

    @Test
    @DisplayName("should trigger repaint when text changes")
    void testRepaintOnTextChange() {
        root.add(label);
        clearDirtyFlag();

        label.setText("Changed");

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should render left-aligned text")
    void testLeftAlignedRendering() {
        label.setLocation(5, 10);
        label.setAlignment(JLabel.ALIGN_LEFT);

        label.paint(buffer);

        BufferAssertions.assertBufferContains(buffer, 5, 10, "Test Label");
    }

    @Test
    @DisplayName("should render center-aligned text")
    void testCenterAlignedRendering() {
        label.setLocation(0, 0);
        label.setSize(20, 1);
        label.setText("Test");
        label.setAlignment(JLabel.ALIGN_CENTER);

        label.paint(buffer);

        // Text "Test" (4 chars) centered in width 20 = position 8
        BufferAssertions.assertBufferContains(buffer, 8, 0, "Test");
    }

    @Test
    @DisplayName("should render right-aligned text")
    void testRightAlignedRendering() {
        label.setLocation(0, 0);
        label.setSize(20, 1);
        label.setText("Test");
        label.setAlignment(JLabel.ALIGN_RIGHT);

        label.paint(buffer);

        // Text "Test" (4 chars) right-aligned in width 20 = position 16
        BufferAssertions.assertBufferContains(buffer, 16, 0, "Test");
    }

    @Test
    @DisplayName("should not crash when painting null text")
    void testNullTextPaint() {
        label.setText(null);
        label.setLocation(0, 0);

        assertDoesNotThrow(() -> label.paint(buffer));
    }

    @Test
    @DisplayName("should not crash when painting empty text")
    void testEmptyTextPaint() {
        label.setText("");
        label.setLocation(0, 0);

        assertDoesNotThrow(() -> label.paint(buffer));
    }

    @Test
    @DisplayName("should be thread-safe for concurrent text updates")
    void testConcurrentTextUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                label.setText("Text " + i);
            }
        });

        assertTrue(label.getText().startsWith("Text "));
    }

    @Test
    @DisplayName("should be thread-safe for concurrent alignment updates")
    void testConcurrentAlignmentUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                label.setAlignment(i % 3);
            }
        });

        // Should complete without exceptions
        assertTrue(true);
    }

    @Test
    @DisplayName("should support Virtual Threads")
    void testVirtualThreadSupport() throws InterruptedException {
        root.add(label);

        ThreadSafetyTestHelper.runWithVirtualThreads(20, () -> {
            label.setText("VThread" + Thread.currentThread().threadId());
            label.setAlignment((int) (Math.random() * 3));
            label.paint(buffer);
        });

        assertTrue(root.isDirty());
    }
}

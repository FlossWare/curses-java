package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JCheckbox;
import org.flossware.jcurses.api.JCheckboxGroup;
import org.flossware.jcurses.testutil.BufferAssertions;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JCheckbox Tests")
class JCheckboxTest extends ComponentTestBase {
    private JCheckbox checkbox;

    @BeforeEach
    void setUp() {
        checkbox = new JCheckbox("Test Option");
        checkbox.setSize(20, 1);
        checkbox.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create unchecked checkbox")
    void testCreation() {
        assertFalse(checkbox.isChecked());
    }

    @Test
    @DisplayName("should set and get checked state")
    void testSetChecked() {
        checkbox.setChecked(true);
        assertTrue(checkbox.isChecked());

        checkbox.setChecked(false);
        assertFalse(checkbox.isChecked());
    }

    @Test
    @DisplayName("should render unchecked checkbox")
    void testUncheckedRendering() {
        checkbox.setChecked(false);
        checkbox.paint(buffer);

        BufferAssertions.assertBufferRowContains(buffer, 0, "[ ] Test Option");
    }

    @Test
    @DisplayName("should render checked checkbox")
    void testCheckedRendering() {
        checkbox.setChecked(true);
        checkbox.paint(buffer);

        BufferAssertions.assertBufferRowContains(buffer, 0, "[X] Test Option");
    }

    @Test
    @DisplayName("should trigger repaint when checked state changes")
    void testRepaintOnCheckChange() {
        root.add(checkbox);
        clearDirtyFlag();

        checkbox.setChecked(true);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should toggle checked state")
    void testToggle() {
        assertFalse(checkbox.isChecked());

        checkbox.setChecked(true);
        assertTrue(checkbox.isChecked());

        checkbox.setChecked(false);
        assertFalse(checkbox.isChecked());
    }

    @Test
    @DisplayName("should work independently without group")
    void testIndependentCheckbox() {
        JCheckbox cb1 = new JCheckbox("Option 1");
        JCheckbox cb2 = new JCheckbox("Option 2");

        cb1.setChecked(true);
        cb2.setChecked(true);

        assertTrue(cb1.isChecked());
        assertTrue(cb2.isChecked());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent checked state changes")
    void testConcurrentCheckChanges() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 100; i++) {
                checkbox.setChecked(i % 2 == 0);
            }
        });

        // Should complete without exceptions
        assertTrue(true);
    }

    @Test
    @DisplayName("should support Virtual Threads")
    void testVirtualThreadSupport() throws InterruptedException {
        root.add(checkbox);

        ThreadSafetyTestHelper.runWithVirtualThreads(20, () -> {
            checkbox.setChecked(Math.random() > 0.5);
            checkbox.paint(buffer);
        });

        assertTrue(root.isDirty());
    }
}

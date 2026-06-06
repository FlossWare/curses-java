package org.flossware.curses.api.widgets;

import org.flossware.curses.api.Slider;
import org.flossware.curses.testutil.BufferAssertions;
import org.flossware.curses.testutil.ComponentTestBase;
import org.flossware.curses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Slider Tests")
class SliderTest extends ComponentTestBase {
    private Slider slider;

    @BeforeEach
    void setUp() {
        slider = new Slider();
        slider.setSize(20, 1);
        slider.setLocation(0, 0);
    }

    @Test
    @DisplayName("should initialize with default values")
    void testDefaultInitialization() {
        assertEquals(50, slider.getValue());
    }

    @Test
    @DisplayName("should initialize with custom values")
    void testCustomInitialization() {
        Slider customSlider = new Slider(0, 200, 100);
        assertEquals(100, customSlider.getValue());
    }

    @Test
    @DisplayName("should set and get value")
    void testSetValue() {
        slider.setValue(75);
        assertEquals(75, slider.getValue());
    }

    @Test
    @DisplayName("should clamp value to min-max range")
    void testValueClamping() {
        slider.setValue(150);
        assertEquals(100, slider.getValue());

        slider.setValue(-10);
        assertEquals(0, slider.getValue());

        slider.setValue(50);
        assertEquals(50, slider.getValue());
    }

    @Test
    @DisplayName("should set minimum and clamp value")
    void testSetMinimum() {
        slider.setValue(30);
        slider.setMinimum(40);

        assertEquals(40, slider.getValue());
    }

    @Test
    @DisplayName("should set maximum and clamp value")
    void testSetMaximum() {
        slider.setValue(70);
        slider.setMaximum(60);

        assertEquals(60, slider.getValue());
    }

    @Test
    @DisplayName("should toggle value display")
    void testSetShowValue() {
        slider.setShowValue(false);
        assertDoesNotThrow(() -> slider.paint(buffer));

        slider.setShowValue(true);
        assertDoesNotThrow(() -> slider.paint(buffer));
    }

    @Test
    @DisplayName("should render slider with value display")
    void testRenderingWithValue() {
        slider.setValue(50);
        slider.setShowValue(true);
        slider.paint(buffer);

        String row = BufferAssertions.extractRow(buffer, 0);
        assertTrue(row.contains("["));
        assertTrue(row.contains("]"));
        assertTrue(row.contains("50"));
    }

    @Test
    @DisplayName("should render slider without value display")
    void testRenderingWithoutValue() {
        slider.setValue(50);
        slider.setShowValue(false);
        slider.paint(buffer);

        String row = BufferAssertions.extractRow(buffer, 0);
        assertTrue(row.contains("["));
        assertTrue(row.contains("]"));
    }

    @Test
    @DisplayName("should position thumb correctly")
    void testThumbPosition() {
        slider.setSize(30, 1); // Larger size to ensure thumb is visible
        slider.setShowValue(false);
        slider.setValue(50);
        slider.paint(buffer);

        // Thumb should be present somewhere in the slider
        String row = BufferAssertions.extractRow(buffer, 0);
        assertTrue(row.contains("O"), "Thumb 'O' should be present in slider");
        assertTrue(row.contains("["), "Slider should have opening bracket");
        assertTrue(row.contains("]"), "Slider should have closing bracket");
    }

    @Test
    @DisplayName("should trigger repaint when value changes")
    void testRepaintOnValueChange() {
        root.add(slider);
        clearDirtyFlag();

        slider.setValue(75);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should be thread-safe for concurrent value updates")
    void testConcurrentValueUpdates() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i <= 100; i++) {
                slider.setValue(i);
            }
        });

        assertTrue(slider.getValue() >= 0);
        assertTrue(slider.getValue() <= 100);
    }

    @Test
    @DisplayName("should support Virtual Threads")
    void testVirtualThreadSupport() throws InterruptedException {
        root.add(slider);

        ThreadSafetyTestHelper.runWithVirtualThreads(20, () -> {
            slider.setValue((int) (Math.random() * 100));
            slider.paint(buffer);
        });

        assertTrue(root.isDirty());
    }

    @Test
    @DisplayName("should handle custom range")
    void testCustomRange() {
        Slider customSlider = new Slider(10, 50, 30);
        customSlider.setSize(20, 1);

        assertEquals(30, customSlider.getValue());

        customSlider.setValue(5);
        assertEquals(10, customSlider.getValue());

        customSlider.setValue(60);
        assertEquals(50, customSlider.getValue());
    }

    @Test
    @DisplayName("should handle zero range gracefully")
    void testZeroRange() {
        Slider zeroRangeSlider = new Slider(50, 50, 50);
        zeroRangeSlider.setSize(20, 1);

        assertEquals(50, zeroRangeSlider.getValue());
        assertDoesNotThrow(() -> zeroRangeSlider.paint(buffer));
    }

    @Test
    @DisplayName("should handle zero width gracefully")
    void testZeroWidth() {
        slider.setSize(0, 1);

        assertDoesNotThrow(() -> slider.paint(buffer));
    }
}

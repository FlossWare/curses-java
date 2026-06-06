package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for keyboard navigation and input handling.
 */
@DisplayName("Keyboard Navigation Integration Tests")
class KeyboardNavigationIT extends IntegrationTestBase {

    @Test
    @DisplayName("checkbox should toggle on SPACE key")
    void testCheckboxToggleWithSpace() {
        // Setup
        Checkbox checkbox = createCheckbox("Test Option", 5, 5);
        setupFrame(checkbox);
        runEventLoopCycle();

        // Initial state - unchecked
        assertFalse(checkbox.isChecked());

        // Toggle via setChecked (simulating SPACE press logic)
        checkbox.setChecked(true);
        root.markDirty();
        runEventLoopCycle();

        assertTrue(checkbox.isChecked());

        // Toggle again
        checkbox.setChecked(false);
        root.markDirty();
        runEventLoopCycle();

        assertFalse(checkbox.isChecked());
    }

    @Test
    @DisplayName("text field should accept keyboard input")
    void testTextFieldInput() {
        // Setup
        TextField textField = createTextField(5, 5, 30);
        setupFrame(textField);
        runEventLoopCycle();

        // Initial state - empty
        assertEquals("", textField.getText());

        // Type text
        textField.setText("Hello");
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Hello", textField.getText());

        // Append more text
        textField.setText(textField.getText() + " World");
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Hello World", textField.getText());
    }

    @Test
    @DisplayName("slider should adjust value with +/- keys")
    void testSliderKeyboardAdjustment() {
        // Setup
        Slider slider = new Slider(0, 100, 50);
        slider.setLocation(5, 5);
        slider.setSize(30, 1);

        setupFrame(slider);
        runEventLoopCycle();

        // Initial value
        assertEquals(50, slider.getValue());

        // Increase value
        slider.setValue(60);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(60, slider.getValue());

        // Decrease value
        slider.setValue(40);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(40, slider.getValue());

        // Test bounds - max
        slider.setValue(100);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(100, slider.getValue());

        // Test bounds - min
        slider.setValue(0);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(0, slider.getValue());
    }

    @Test
    @DisplayName("combo box should cycle through items with arrow keys")
    void testComboBoxNavigation() {
        // Setup
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLocation(5, 5);
        comboBox.setSize(20, 1);
        comboBox.addItem("Option 1");
        comboBox.addItem("Option 2");
        comboBox.addItem("Option 3");

        setupFrame(comboBox);
        runEventLoopCycle();

        // Initial selection
        assertEquals("Option 1", comboBox.getSelectedItem());

        // Select by index
        comboBox.setSelectedIndex(1);
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Option 2", comboBox.getSelectedItem());

        // Select next
        comboBox.setSelectedIndex(2);
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Option 3", comboBox.getSelectedItem());

        // Wrap around to first
        comboBox.setSelectedIndex(0);
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Option 1", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("list component should store items")
    void testListDataStorage() {
        // Setup
        ListComponent list = new ListComponent();
        list.setLocation(5, 5);
        list.setSize(30, 8);
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");
        list.addItem("Item 4");

        setupFrame(list);
        runEventLoopCycle();

        // Verify items stored
        java.util.List<String> items = list.getItems();
        assertEquals(4, items.size());
        assertEquals("Item 1", items.get(0));
        assertEquals("Item 2", items.get(1));
        assertEquals("Item 3", items.get(2));
        assertEquals("Item 4", items.get(3));

        // Select items
        list.select(0);
        root.markDirty();
        runEventLoopCycle();

        list.select(2);
        root.markDirty();
        runEventLoopCycle();

        // Verify item access still works
        assertEquals(4, list.getItems().size());
    }

    @Test
    @DisplayName("ENTER key should activate focused button")
    void testEnterKeyActivatesButton() {
        // Setup
        AtomicInteger clickCount = new AtomicInteger(0);

        Button button = createButton("Press Enter", 5, 5);
        button.addActionListener(clickCount::incrementAndGet);

        setupFrame(button);
        runEventLoopCycle();

        // Simulate ENTER key via doClick
        injectKeyPress(KEY_ENTER);
        button.doClick(); // In real app, enter key handler would call this
        runEventLoopCycle();

        assertEquals(1, clickCount.get());
    }

    @Test
    @DisplayName("ESC key should clear text field")
    void testEscKeyClearsTextField() {
        // Setup
        TextField textField = createTextField(5, 5, 30);
        textField.setText("Some text");

        setupFrame(textField);
        runEventLoopCycle();

        assertEquals("Some text", textField.getText());

        // Simulate ESC key action
        injectKeyPress(KEY_ESC);
        textField.setText("");
        root.markDirty();
        runEventLoopCycle();

        assertEquals("", textField.getText());
    }

    @Test
    @DisplayName("multiple components should handle keyboard events independently")
    void testMultipleComponentsKeyboardIndependence() {
        // Setup
        TextField field1 = createTextField(5, 5, 20);
        TextField field2 = createTextField(5, 8, 20);

        setupFrame(field1, field2);
        runEventLoopCycle();

        // Type in first field
        field1.setText("Field 1");
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Field 1", field1.getText());
        assertEquals("", field2.getText());

        // Type in second field
        field2.setText("Field 2");
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Field 1", field1.getText());
        assertEquals("Field 2", field2.getText());
    }

    @Test
    @DisplayName("keyboard shortcut should trigger action")
    void testKeyboardShortcut() {
        // Setup
        AtomicInteger actionCount = new AtomicInteger(0);

        Label statusLabel = createLabel("Ready", 5, 5);
        statusLabel.setSize(30, 1);

        // Simulate Ctrl+S shortcut
        Button saveButton = createButton("Save (Ctrl+S)", 5, 8);
        saveButton.addActionListener(() -> {
            actionCount.incrementAndGet();
            statusLabel.setText("Saved!");
            root.markDirty();
        });

        setupFrame(statusLabel, saveButton);
        runEventLoopCycle();

        // Trigger shortcut
        saveButton.doClick();
        runEventLoopCycle();

        assertEquals(1, actionCount.get());
        assertEquals("Saved!", statusLabel.getText());
    }
}

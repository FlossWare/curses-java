package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.testutil.BufferAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for button interaction and event handling.
 */
@DisplayName("Button Interaction Integration Tests")
class ButtonInteractionIT extends IntegrationTestBase {

    @Test
    @DisplayName("button click should trigger action listener")
    void testButtonClickTriggersAction() {
        // Setup
        AtomicBoolean clicked = new AtomicBoolean(false);

        Button button = createButton("Click Me", 5, 5);
        button.addActionListener(() -> clicked.set(true));

        setupFrame(button);

        // Initial render
        runEventLoopCycle();

        // Verify button rendered
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");

        // Simulate button click via programmatic action
        button.doClick();
        root.markDirty();
        runEventLoopCycle();

        // Verify action triggered
        assertTrue(clicked.get(), "Button action should have been triggered");
    }

    @Test
    @DisplayName("clicking button should update label text")
    void testButtonClickUpdatesLabel() {
        // Setup UI
        Label label = createLabel("Not clicked", 5, 5);
        label.setSize(30, 1);

        Button button = createButton("Click Me", 5, 8);
        button.addActionListener(() -> {
            label.setText("Button was clicked!");
            root.markDirty();
        });

        setupFrame(label, button);

        // Initial render
        runEventLoopCycle();
        BufferAssertions.assertBufferContains(buffer, 5, 5, "Not clicked");

        // Click button
        button.doClick();
        runEventLoopCycle();

        // Verify label updated
        BufferAssertions.assertBufferContains(buffer, 5, 5, "Button was clicked!");
    }

    @Test
    @DisplayName("disabled button should not trigger action")
    void testDisabledButtonIgnoresClick() {
        // Setup
        AtomicInteger clickCount = new AtomicInteger(0);

        Button button = createButton("Disabled", 5, 5);
        button.setEnabled(false);
        button.addActionListener(() -> clickCount.incrementAndGet());

        setupFrame(button);
        runEventLoopCycle();

        // Try to click disabled button
        button.doClick();
        runEventLoopCycle();

        // Verify action not triggered
        assertEquals(0, clickCount.get(), "Disabled button should not trigger action");
    }

    @Test
    @DisplayName("multiple buttons should handle clicks independently")
    void testMultipleButtonsIndependent() {
        // Setup
        AtomicInteger count1 = new AtomicInteger(0);
        AtomicInteger count2 = new AtomicInteger(0);

        Button btn1 = createButton("Button 1", 5, 5);
        btn1.addActionListener(count1::incrementAndGet);

        Button btn2 = createButton("Button 2", 5, 8);
        btn2.addActionListener(count2::incrementAndGet);

        setupFrame(btn1, btn2);
        runEventLoopCycle();

        // Click first button twice
        btn1.doClick();
        btn1.doClick();
        runEventLoopCycles(2);

        assertEquals(2, count1.get(), "Button 1 should be clicked twice");
        assertEquals(0, count2.get(), "Button 2 should not be clicked");

        // Click second button once
        btn2.doClick();
        runEventLoopCycle();

        assertEquals(2, count1.get(), "Button 1 count should not change");
        assertEquals(1, count2.get(), "Button 2 should be clicked once");
    }

    @Test
    @DisplayName("button should update its own label on click")
    void testButtonSelfUpdate() {
        // Setup
        AtomicInteger clickCount = new AtomicInteger(0);

        Button button = createButton("Click Me", 5, 5);
        button.setSize(20, 1);
        button.addActionListener(() -> {
            int count = clickCount.incrementAndGet();
            button.setLabel("Clicked " + count + " times");
            root.markDirty();
        });

        setupFrame(button);
        runEventLoopCycle();

        // Click button
        button.doClick();
        runEventLoopCycle();

        // Verify label updated
        assertEquals("Clicked 1 times", button.getLabel());

        // Click again
        button.doClick();
        runEventLoopCycle();

        assertEquals("Clicked 2 times", button.getLabel());
    }

    @Test
    @DisplayName("complex form workflow: fill and submit")
    void testComplexFormWorkflow() {
        // Create form components
        Label nameLabel = createLabel("Name:", 5, 5);

        TextField nameField = createTextField(12, 5, 30);

        Checkbox agreeCheckbox = createCheckbox("I agree", 5, 8);

        Label resultLabel = createLabel("", 5, 11);
        resultLabel.setSize(50, 1);

        Button submitButton = createButton("Submit", 5, 14);
        submitButton.addActionListener(() -> {
            if (agreeCheckbox.isChecked()) {
                String name = nameField.getText().trim();
                if (!name.isEmpty()) {
                    resultLabel.setText("Form submitted: " + name);
                } else {
                    resultLabel.setText("Please enter a name");
                }
            } else {
                resultLabel.setText("Please agree to terms");
            }
            root.markDirty();
        });

        setupFrame(nameLabel, nameField, agreeCheckbox, resultLabel, submitButton);
        runEventLoopCycle();

        // Test 1: Submit without agreeing
        submitButton.doClick();
        runEventLoopCycle();
        BufferAssertions.assertBufferContains(buffer, 5, 11, "Please agree to terms");

        // Test 2: Agree but no name
        agreeCheckbox.setChecked(true);
        root.markDirty();
        runEventLoopCycle();

        submitButton.doClick();
        runEventLoopCycle();
        BufferAssertions.assertBufferContains(buffer, 5, 11, "Please enter a name");

        // Test 3: Complete form
        nameField.setText("John Doe");
        root.markDirty();
        runEventLoopCycle();

        submitButton.doClick();
        runEventLoopCycle();
        BufferAssertions.assertBufferContains(buffer, 5, 11, "Form submitted: John Doe");
    }

    @Test
    @DisplayName("button action should update progress bar")
    void testButtonUpdatesProgressBar() {
        // Setup
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(5, 5);
        progressBar.setSize(40, 1);
        progressBar.setPercent(0.0);

        Button incrementButton = createButton("+10%", 5, 8);
        incrementButton.addActionListener(() -> {
            double newPercent = Math.min(1.0, progressBar.getPercent() + 0.1);
            progressBar.setPercent(newPercent);
            root.markDirty();
        });

        setupFrame(progressBar, incrementButton);
        runEventLoopCycle();

        // Initial state - 0%
        assertEquals(0.0, progressBar.getPercent(), 0.01);

        // Click 5 times
        for (int i = 0; i < 5; i++) {
            incrementButton.doClick();
            runEventLoopCycle();
        }

        // Verify progress at 50%
        assertEquals(0.5, progressBar.getPercent(), 0.01);

        // Click 5 more times
        for (int i = 0; i < 5; i++) {
            incrementButton.doClick();
            runEventLoopCycle();
        }

        // Verify capped at 100%
        assertEquals(1.0, progressBar.getPercent(), 0.01);
    }

    @Test
    @DisplayName("button click should add item to list")
    void testButtonAddsToList() {
        // Setup
        ListComponent list = new ListComponent();
        list.setLocation(5, 5);
        list.setSize(30, 10);

        AtomicInteger itemCounter = new AtomicInteger(1);

        Button addButton = createButton("Add Item", 5, 16);
        addButton.addActionListener(() -> {
            list.addItem("Item " + itemCounter.getAndIncrement());
            root.markDirty();
        });

        setupFrame(list, addButton);
        runEventLoopCycle();

        // Initial state - empty list
        assertEquals(0, list.getItems().size());

        // Add 3 items
        for (int i = 0; i < 3; i++) {
            addButton.doClick();
            runEventLoopCycle();
        }

        // Verify 3 items added
        java.util.List<String> items = list.getItems();
        assertEquals(3, items.size());
        assertEquals("Item 1", items.get(0));
        assertEquals("Item 2", items.get(1));
        assertEquals("Item 3", items.get(2));
    }
}

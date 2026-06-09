package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.testutil.BufferAssertions;
import org.flossware.curses.testutil.ComponentTestBase;
import org.flossware.curses.testutil.MockNcursesBridge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Base class for UI integration tests.
 * Extends ComponentTestBase and adds MockNcursesBridge integration,
 * event injection, and rendering helpers.
 */
public abstract class IntegrationTestBase extends ComponentTestBase {
    protected MockNcursesBridge mockBridge;
    protected EventLoopRunner eventLoop;

    // Common key codes
    protected static final int KEY_ESC = 27;
    protected static final int KEY_TAB = 9;
    protected static final int KEY_ENTER = 10;
    protected static final int KEY_SPACE = 32;
    protected static final int KEY_BACKSPACE = 127;

    // Mouse button states
    protected static final long BUTTON1_PRESSED = 0x0000001L;
    protected static final long BUTTON1_RELEASED = 0x0000002L;
    protected static final long BUTTON1_CLICKED = 0x0000004L;

    @BeforeEach
    void integrationSetUp() {
        // Base setup happens automatically via @BeforeEach in ComponentTestBase

        // Initialize mock bridge
        mockBridge = MockNcursesBridge.getInstance();
        mockBridge.init();

        // Re-create buffer to match mock terminal dimensions (base class creates 120x40,
        // but MockNcursesBridge screen is 80x24 — buffer must not exceed screen size
        // or EventLoopRunner.updateScreen() will write out-of-bounds coordinates)
        buffer = BufferAssertions.createBuffer(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());

        // Create event loop runner
        eventLoop = new EventLoopRunner(mockBridge, root, buffer);

        // Set root size to match mock terminal
        root.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
    }

    @AfterEach
    void integrationTearDown() {
        // Stop event loop and join async threads
        if (eventLoop != null) {
            eventLoop.stop();

            // Join any async threads spawned by runAsync() with a 5-second timeout
            // This prevents background threads from accessing cleaned-up resources
            try {
                eventLoop.joinAsyncThreads(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Log but continue cleanup
                System.err.println("Interrupted while joining async threads: " + e.getMessage());
            }
        }

        // Stop mock bridge
        if (mockBridge != null) {
            mockBridge.stop();
            mockBridge.clearInput();
        }

        MockNcursesBridge.reset();

        // Base teardown happens automatically via @AfterEach in ComponentTestBase
    }

    // ===== Event Injection Helpers =====

    /**
     * Inject a single key press.
     */
    protected void injectKeyPress(int keyCode) {
        mockBridge.injectKey(keyCode);
    }

    /**
     * Inject a sequence of key presses.
     */
    protected void injectKeySequence(int... keyCodes) {
        mockBridge.injectKeySequence(keyCodes);
    }

    /**
     * Inject a mouse click at (x, y).
     */
    protected void injectMouseClick(int x, int y) {
        mockBridge.injectMouse(x, y, BUTTON1_CLICKED);
    }

    /**
     * Inject a mouse press at (x, y).
     */
    protected void injectMousePress(int x, int y) {
        mockBridge.injectMouse(x, y, BUTTON1_PRESSED);
    }

    /**
     * Inject a mouse release at (x, y).
     */
    protected void injectMouseRelease(int x, int y) {
        mockBridge.injectMouse(x, y, BUTTON1_RELEASED);
    }

    /**
     * Inject text input character by character.
     */
    protected void injectTextInput(String text) {
        for (char ch : text.toCharArray()) {
            injectKeyPress(ch);
        }
    }

    // ===== Component Interaction Helpers =====

    /**
     * Click a button using mouse event injection.
     * This exercises the actual event dispatch path rather than calling doClick() directly.
     * The mouse click is injected into MockNcursesBridge and processed through the event loop,
     * ensuring the button's mouse event handlers are invoked.
     *
     * @param button The button to click
     */
    protected void clickButton(Button button) {
        int centerX = button.getX() + button.getWidth() / 2;
        int centerY = button.getY();
        injectMouseClick(centerX, centerY);
        runEventLoopCycle();
    }

    /**
     * Activate a button using SPACE key press.
     * This simulates keyboard-based activation (when the button is focused).
     * Applications must implement their own focus management and key handler
     * to route SPACE/ENTER to the focused button's doClick() method.
     *
     * @param button The button to activate
     */
    protected void activateButtonWithSpace(Button button) {
        injectKeyPress(KEY_SPACE);
        // Note: actual key handling must be implemented in the application's event processor
        // This just injects the key; the app is responsible for routing it to the focused button
        runEventLoopCycle();
    }

    /**
     * Activate a button using ENTER key press.
     * This simulates keyboard-based activation (when the button is focused).
     * Applications must implement their own focus management and key handler
     * to route SPACE/ENTER to the focused button's doClick() method.
     *
     * @param button The button to activate
     */
    protected void activateButtonWithEnter(Button button) {
        injectKeyPress(KEY_ENTER);
        // Note: actual key handling must be implemented in the application's event processor
        // This just injects the key; the app is responsible for routing it to the focused button
        runEventLoopCycle();
    }

    // ===== Rendering Helpers =====

    /**
     * Run one event loop cycle (render + process one input).
     */
    protected void runEventLoopCycle() {
        eventLoop.runCycle();
    }

    /**
     * Run event loop for N cycles.
     */
    protected void runEventLoopCycles(int cycles) {
        eventLoop.runForIterations(cycles);
    }

    /**
     * Run event loop until condition is met or timeout.
     */
    protected void waitForCondition(Supplier<Boolean> condition, Duration timeout) throws TimeoutException {
        eventLoop.runUntilCondition(condition, timeout);
    }

    /**
     * Wait for screen to render (dirty flag cleared).
     */
    protected void waitForRender(Duration timeout) throws TimeoutException {
        eventLoop.waitForRender(timeout);
    }

    /**
     * Run event loop until screen updates.
     */
    protected void waitForScreenUpdate() {
        try {
            waitForRender(Duration.ofSeconds(2));
        } catch (TimeoutException e) {
            // Already rendered or timeout
        }
    }

    // ===== UI Setup Helpers =====

    /**
     * Create a frame with given components.
     */
    protected Frame setupFrame(Component... components) {
        Frame frame = new Frame("Test Frame");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        for (Component component : components) {
            frame.add(component);
        }

        root.add(frame);
        root.markDirty();

        return frame;
    }

    /**
     * Create a button at specific location.
     */
    protected Button createButton(String label, int x, int y) {
        Button button = new Button(label);
        button.setLocation(x, y);
        button.setSize(label.length() + 4, 1);
        return button;
    }

    /**
     * Create a label at specific location.
     */
    protected Label createLabel(String text, int x, int y) {
        Label label = new Label(text);
        label.setLocation(x, y);
        label.setSize(text.length(), 1);
        return label;
    }

    /**
     * Create a checkbox at specific location.
     */
    protected Checkbox createCheckbox(String label, int x, int y) {
        Checkbox checkbox = new Checkbox(label);
        checkbox.setLocation(x, y);
        checkbox.setSize(label.length() + 4, 1);
        return checkbox;
    }

    /**
     * Create a text field at specific location.
     */
    protected TextField createTextField(int x, int y, int width) {
        TextField textField = new TextField();
        textField.setLocation(x, y);
        textField.setSize(width, 1);
        return textField;
    }

    // ===== Verification Helpers =====

    /**
     * Capture current screen state from mock bridge.
     */
    protected char[][] captureScreen() {
        return mockBridge.captureScreen();
    }

    /**
     * Get character at screen position.
     */
    protected char getScreenCharAt(int y, int x) {
        return mockBridge.getCharAt(y, x);
    }

    /**
     * Get text from screen row.
     */
    protected String getScreenRow(int y) {
        return mockBridge.getRow(y);
    }

    /**
     * Check if component has input pending.
     */
    protected boolean hasKeyInput() {
        return mockBridge.hasKeyInput();
    }
}

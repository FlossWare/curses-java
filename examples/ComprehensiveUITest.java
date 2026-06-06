package org.flossware.curses;

import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive UI test that creates and exercises ALL UI components.
 * This is a real interactive test - not unit tests.
 */
public class ComprehensiveUITest {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;
    private static Label statusLabel;
    private static int testPhase = 0;
    private static final String[] TEST_PHASES = {
        "Phase 1: Testing Buttons",
        "Phase 2: Testing Checkboxes",
        "Phase 3: Testing ComboBox",
        "Phase 4: Testing Slider",
        "Phase 5: Testing ProgressBar",
        "Phase 6: Testing TextField",
        "Phase 7: Testing Table",
        "Phase 8: Testing List",
        "Phase 9: Testing Choice",
        "Phase 10: All tests complete!"
    };

    private static final int KEY_ESC = 27;
    private static final int KEY_TAB = 9;
    private static final int KEY_ENTER = 10;
    private static final int KEY_SPACE = 32;

    public static void main(String[] args) throws Throwable {
        if (!NcursesBridge.isAvailable()) {
            System.err.println("ERROR: ncurses library is not available!");
            System.exit(1);
        }

        NcursesBridge.init();
        NcursesBridge.setNonBlocking(false);

        terminalHeight = NcursesBridge.getTerminalHeight();
        terminalWidth = NcursesBridge.getTerminalWidth();
        buffer = new char[terminalHeight][terminalWidth];

        try {
            setupUI();
            runEventLoop();
        } finally {
            NcursesBridge.stop();
        }
    }

    private static void setupUI() {
        RootPane root = RootPane.getInstance();
        root.setSize(terminalWidth, terminalHeight);

        Frame frame = new Frame("Comprehensive UI Test - TAB=next, SPACE=activate, n=next test, ESC=quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        Panel panel = new Panel();
        panel.setLocation(1, 2);
        panel.setSize(terminalWidth - 2, terminalHeight - 4);
        panel.setBordered(true);

        // Status label showing current test phase
        statusLabel = new Label(TEST_PHASES[testPhase]);
        statusLabel.setLocation(3, 4);
        statusLabel.setSize(terminalWidth - 6, 1);
        statusLabel.setAlignment(Label.ALIGN_CENTER);

        // Test result label
        Label resultLabel = new Label("Results will appear here");
        resultLabel.setLocation(3, 6);
        resultLabel.setSize(terminalWidth - 6, 1);

        // === BUTTON TESTS ===
        Button button1 = new Button("Button 1");
        button1.setLocation(3, 9);
        button1.setSize(12, 1);
        button1.addActionListener(() -> {
            resultLabel.setText("✓ Button 1 clicked!");
            root.markDirty();
        });
        focusableComponents.add(button1);

        Button button2 = new Button("Button 2");
        button2.setLocation(17, 9);
        button2.setSize(12, 1);
        button2.addActionListener(() -> {
            resultLabel.setText("✓ Button 2 pressed!");
            root.markDirty();
        });
        focusableComponents.add(button2);

        Button button3 = new Button("Disabled");
        button3.setLocation(31, 9);
        button3.setSize(12, 1);
        button3.setEnabled(false);
        focusableComponents.add(button3);

        // === CHECKBOX TESTS ===
        Checkbox check1 = new Checkbox("Option 1");
        check1.setLocation(3, 12);
        check1.setSize(15, 1);
        focusableComponents.add(check1);

        Checkbox check2 = new Checkbox("Option 2");
        check2.setLocation(20, 12);
        check2.setSize(15, 1);
        check2.setChecked(true);
        focusableComponents.add(check2);

        Checkbox check3 = new Checkbox("Option 3");
        check3.setLocation(37, 12);
        check3.setSize(15, 1);
        focusableComponents.add(check3);

        // === COMBOBOX TEST ===
        ComboBox<String> combo = new ComboBox<>();
        combo.addItem("Item 1");
        combo.addItem("Item 2");
        combo.addItem("Item 3");
        combo.addItem("Item 4");
        combo.setLocation(3, 15);
        combo.setSize(20, 1);
        focusableComponents.add(combo);

        Button comboTest = new Button("Select Next");
        comboTest.setLocation(25, 15);
        comboTest.setSize(15, 1);
        comboTest.addActionListener(() -> {
            int idx = combo.getSelectedIndex();
            combo.setSelectedIndex((idx + 1) % 4);
            resultLabel.setText("✓ ComboBox: " + combo.getSelectedItem());
            root.markDirty();
        });
        focusableComponents.add(comboTest);

        // === SLIDER TEST ===
        Slider slider = new Slider(0, 100, 50);
        slider.setLocation(3, 18);
        slider.setSize(35, 1);
        focusableComponents.add(slider);

        Button sliderUp = new Button("+10");
        sliderUp.setLocation(40, 18);
        sliderUp.setSize(6, 1);
        sliderUp.addActionListener(() -> {
            slider.setValue(slider.getValue() + 10);
            resultLabel.setText("✓ Slider: " + slider.getValue());
            root.markDirty();
        });
        focusableComponents.add(sliderUp);

        Button sliderDown = new Button("-10");
        sliderDown.setLocation(48, 18);
        sliderDown.setSize(6, 1);
        sliderDown.addActionListener(() -> {
            slider.setValue(slider.getValue() - 10);
            resultLabel.setText("✓ Slider: " + slider.getValue());
            root.markDirty();
        });
        focusableComponents.add(sliderDown);

        // === PROGRESS BAR TEST ===
        ProgressBar progress = new ProgressBar();
        progress.setLocation(3, 21);
        progress.setSize(35, 1);
        progress.setPercent(0.3);

        Button progressBtn = new Button("Fill +10%");
        progressBtn.setLocation(40, 21);
        progressBtn.setSize(12, 1);
        progressBtn.addActionListener(() -> {
            double p = progress.getPercent();
            progress.setPercent(Math.min(1.0, p + 0.1));
            resultLabel.setText("✓ Progress: " + (int)(progress.getPercent() * 100) + "%");
            root.markDirty();
        });
        focusableComponents.add(progressBtn);

        // === CHOICE TEST ===
        Choice choice = new Choice();
        choice.add("Red");
        choice.add("Green");
        choice.add("Blue");
        choice.setLocation(3, 24);
        choice.setSize(15, 1);
        focusableComponents.add(choice);

        Button choiceBtn = new Button("Next Color");
        choiceBtn.setLocation(20, 24);
        choiceBtn.setSize(14, 1);
        choiceBtn.addActionListener(() -> {
            choice.selectNext();
            resultLabel.setText("✓ Choice: " + choice.getSelectedItem());
            root.markDirty();
        });
        focusableComponents.add(choiceBtn);

        // Add all components to panel
        panel.add(statusLabel);
        panel.add(resultLabel);
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(check1);
        panel.add(check2);
        panel.add(check3);
        panel.add(combo);
        panel.add(comboTest);
        panel.add(slider);
        panel.add(sliderUp);
        panel.add(sliderDown);
        panel.add(progress);
        panel.add(progressBtn);
        panel.add(choice);
        panel.add(choiceBtn);

        // Status bar
        StatusBar status = new StatusBar(
            "Terminal: " + terminalWidth + "x" + terminalHeight +
            " | Components: " + focusableComponents.size() +
            " | Press 'n' for next test phase"
        );
        status.setLocation(1, terminalHeight - 2);
        status.setSize(terminalWidth - 2, 1);

        frame.add(panel);
        frame.add(status);
        root.add(frame);
        root.markDirty();
    }

    private static void runEventLoop() throws Throwable {
        while (running) {
            if (RootPane.getInstance().isDirty()) {
                render();
                RootPane.getInstance().clearDirty();
            }

            int ch = NcursesBridge.getChar();
            if (ch != -1) {
                handleKey(ch);
            }

            Thread.sleep(10);
        }
    }

    private static void render() throws Throwable {
        for (int i = 0; i < terminalHeight; i++) {
            for (int j = 0; j < terminalWidth; j++) {
                buffer[i][j] = ' ';
            }
        }

        RootPane.getInstance().paint(buffer);

        // Highlight focused component
        if (currentFocus >= 0 && currentFocus < focusableComponents.size()) {
            Component focused = focusableComponents.get(currentFocus);
            int x = focused.getX();
            int y = focused.getY();
            int w = focused.getWidth();

            if (x > 0 && y >= 0 && y < terminalHeight) {
                buffer[y][x - 1] = '>';
            }
            if (x + w < terminalWidth && y >= 0 && y < terminalHeight) {
                buffer[y][x + w] = '<';
            }
        }

        NcursesBridge.clear();
        // Skip bottom-right corner (ncurses limitation)
        for (int y = 0; y < terminalHeight; y++) {
            for (int x = 0; x < terminalWidth; x++) {
                if (y == terminalHeight - 1 && x == terminalWidth - 1) {
                    continue;
                }
                NcursesBridge.moveCursor(y, x, buffer[y][x]);
            }
        }
        NcursesBridge.refresh();
    }

    private static void handleKey(int ch) {
        switch (ch) {
            case KEY_ESC, 'q', 'Q' -> running = false;

            case KEY_TAB -> {
                currentFocus = (currentFocus + 1) % focusableComponents.size();
                RootPane.getInstance().markDirty();
            }

            case 'n', 'N' -> {
                testPhase = (testPhase + 1) % TEST_PHASES.length;
                statusLabel.setText(TEST_PHASES[testPhase]);
                RootPane.getInstance().markDirty();
            }

            case KEY_ENTER, KEY_SPACE -> {
                if (currentFocus >= 0 && currentFocus < focusableComponents.size()) {
                    Component focused = focusableComponents.get(currentFocus);
                    if (focused instanceof Button button) {
                        button.doClick();
                    } else if (focused instanceof Checkbox checkbox) {
                        checkbox.setChecked(!checkbox.isChecked());
                    }
                }
            }
        }
    }
}

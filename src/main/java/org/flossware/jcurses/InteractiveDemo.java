package org.flossware.jcurses;

import org.flossware.jcurses.api.*;
import org.flossware.jcurses.events.MouseEvent;
import org.flossware.jcurses.ffi.NcursesBridge;
import org.flossware.jcurses.render.DiffEngine;

import java.util.ArrayList;
import java.util.List;

public class InteractiveDemo {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static DiffEngine diffEngine;
    private static char[][] buffer;

    // Ncurses key codes
    private static final int KEY_UP = 259;
    private static final int KEY_DOWN = 258;
    private static final int KEY_LEFT = 260;
    private static final int KEY_RIGHT = 261;
    private static final int KEY_ENTER = 10;
    private static final int KEY_TAB = 9;
    private static final int KEY_ESC = 27;
    private static final int KEY_SPACE = 32;
    private static final int KEY_MOUSE = 409;  // ncurses KEY_MOUSE constant

    public static void main(String[] args) throws Throwable {
        if (!NcursesBridge.isAvailable()) {
            System.err.println("ERROR: ncurses library is not available!");
            System.err.println("Please install ncurses development library:");
            System.err.println("  - Ubuntu/Debian: sudo apt-get install libncurses-dev");
            System.err.println("  - Fedora/RHEL: sudo dnf install ncurses-devel");
            System.err.println("  - Arch: sudo pacman -S ncurses");
            System.exit(1);
        }

        NcursesBridge.init();
        NcursesBridge.setNonBlocking(false);
        NcursesBridge.enableMouse(NcursesBridge.ALL_MOUSE_EVENTS);

        // Initialize color support
        initializeColors();

        try {
            setupUI();
            runEventLoop();
        } finally {
            NcursesBridge.stop();
        }
    }

    private static void setupUI() {
        buffer = new char[40][120];
        diffEngine = new DiffEngine(120, 40);

        RootPane root = RootPane.getInstance();
        root.setSize(120, 40);

        // Create main frame
        JFrame frame = new JFrame("Interactive JCurses Demo - Press TAB to navigate, SPACE to activate, ESC to quit");
        frame.setLocation(0, 0);
        frame.setSize(120, 38);
        frame.setVisible(true);

        // Create a panel for widgets
        JPanel panel = new JPanel();
        panel.setLocation(2, 3);
        panel.setSize(116, 32);
        panel.setBordered(true);

        // Create interactive widgets
        JLabel label1 = new JLabel("Welcome to Interactive JCurses!");
        label1.setLocation(4, 5);
        label1.setSize(50, 1);
        label1.setAlignment(JLabel.ALIGN_LEFT);

        JLabel label2 = new JLabel("Use TAB to move between widgets, SPACE/ENTER to activate");
        label2.setLocation(4, 6);
        label2.setSize(60, 1);

        // Buttons
        JButton button1 = new JButton("Click Me!");
        button1.setLocation(4, 9);
        button1.setSize(15, 1);
        button1.addActionListener(() -> {
            label1.setText("Button 1 was clicked!");
            markDirty();
        });
        button1.addMouseListener(e -> {
            label1.setText("Button 1 MOUSE clicked at (" + e.x() + "," + e.y() + ")!");
            button1.doClick();
            markDirty();
        });
        focusableComponents.add(button1);

        JButton button2 = new JButton("Press Me!");
        button2.setLocation(22, 9);
        button2.setSize(15, 1);
        button2.addActionListener(() -> {
            label1.setText("Button 2 was pressed!");
            markDirty();
        });
        button2.addMouseListener(e -> {
            label1.setText("Button 2 MOUSE pressed at (" + e.x() + "," + e.y() + ")!");
            button2.doClick();
            markDirty();
        });
        focusableComponents.add(button2);

        JButton button3 = new JButton("Toggle Below");
        button3.setLocation(40, 9);
        button3.setSize(18, 1);
        focusableComponents.add(button3);

        // Checkboxes
        JCheckbox check1 = new JCheckbox("Enable feature 1");
        check1.setLocation(4, 12);
        check1.setSize(25, 1);
        focusableComponents.add(check1);

        JCheckbox check2 = new JCheckbox("Enable feature 2");
        check2.setLocation(4, 13);
        check2.setSize(25, 1);
        focusableComponents.add(check2);

        JCheckbox check3 = new JCheckbox("Enable feature 3");
        check3.setLocation(4, 14);
        check3.setSize(25, 1);
        focusableComponents.add(check3);

        button3.addActionListener(() -> {
            check1.setChecked(!check1.isChecked());
            check2.setChecked(!check2.isChecked());
            check3.setChecked(!check3.isChecked());
            label1.setText("Checkboxes toggled!");
            markDirty();
        });

        // Slider
        JSlider slider = new JSlider(0, 100, 50);
        slider.setLocation(4, 17);
        slider.setSize(40, 1);
        focusableComponents.add(slider);

        JButton sliderUp = new JButton("+");
        sliderUp.setLocation(46, 17);
        sliderUp.setSize(5, 1);
        sliderUp.addActionListener(() -> {
            slider.setValue(slider.getValue() + 10);
            label1.setText("Slider value: " + slider.getValue());
            markDirty();
        });
        focusableComponents.add(sliderUp);

        JButton sliderDown = new JButton("-");
        sliderDown.setLocation(52, 17);
        sliderDown.setSize(5, 1);
        sliderDown.addActionListener(() -> {
            slider.setValue(slider.getValue() - 10);
            label1.setText("Slider value: " + slider.getValue());
            markDirty();
        });
        focusableComponents.add(sliderDown);

        // Progress bar
        JProgressBar progress = new JProgressBar();
        progress.setLocation(4, 20);
        progress.setSize(40, 1);
        progress.setPercent(0.0);

        JButton progressBtn = new JButton("Fill Progress");
        progressBtn.setLocation(46, 20);
        progressBtn.setSize(18, 1);
        progressBtn.addActionListener(() -> {
            double current = progress.getPercent();
            progress.setPercent(Math.min(1.0, current + 0.1));
            label1.setText("Progress: " + (int)(progress.getPercent() * 100) + "%");
            markDirty();
        });
        focusableComponents.add(progressBtn);

        JButton resetBtn = new JButton("Reset Progress");
        resetBtn.setLocation(66, 20);
        resetBtn.setSize(18, 1);
        resetBtn.addActionListener(() -> {
            progress.setPercent(0.0);
            label1.setText("Progress reset!");
            markDirty();
        });
        focusableComponents.add(resetBtn);

        // Combo box
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Option 1");
        combo.addItem("Option 2");
        combo.addItem("Option 3");
        combo.addItem("Option 4");
        combo.setLocation(4, 23);
        combo.setSize(25, 1);
        focusableComponents.add(combo);

        JButton comboNext = new JButton("Next");
        comboNext.setLocation(31, 23);
        comboNext.setSize(8, 1);
        comboNext.addActionListener(() -> {
            int idx = combo.getSelectedIndex();
            combo.setSelectedIndex((idx + 1) % 4);
            label1.setText("Selected: " + combo.getSelectedItem());
            markDirty();
        });
        focusableComponents.add(comboNext);

        // Status bar
        JStatusBar status = new JStatusBar("Ready | TAB=Navigate | SPACE/ENTER=Activate | ESC=Quit");
        status.setLocation(2, 36);
        status.setSize(116, 1);

        // Add all to panel
        panel.add(label1);
        panel.add(label2);
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(check1);
        panel.add(check2);
        panel.add(check3);
        panel.add(slider);
        panel.add(sliderUp);
        panel.add(sliderDown);
        panel.add(progress);
        panel.add(progressBtn);
        panel.add(resetBtn);
        panel.add(combo);
        panel.add(comboNext);

        frame.add(panel);
        frame.add(status);

        root.add(frame);
        root.markDirty();
    }

    private static void markDirty() {
        RootPane.getInstance().markDirty();
    }

    private static void initializeColors() throws Throwable {
        // Start ncurses color support
        NcursesBridge.startColor();

        // Initialize standard color pairs (pair 0 is reserved for default)
        NcursesBridge.initColorPair((short) 1, (short) NcursesBridge.COLOR_WHITE, (short) NcursesBridge.COLOR_BLACK);
        NcursesBridge.initColorPair((short) 2, (short) NcursesBridge.COLOR_BLACK, (short) NcursesBridge.COLOR_WHITE);
        NcursesBridge.initColorPair((short) 3, (short) NcursesBridge.COLOR_RED, (short) NcursesBridge.COLOR_BLACK);
        NcursesBridge.initColorPair((short) 4, (short) NcursesBridge.COLOR_GREEN, (short) NcursesBridge.COLOR_BLACK);
        NcursesBridge.initColorPair((short) 5, (short) NcursesBridge.COLOR_YELLOW, (short) NcursesBridge.COLOR_BLACK);
        NcursesBridge.initColorPair((short) 6, (short) NcursesBridge.COLOR_BLUE, (short) NcursesBridge.COLOR_BLACK);
        NcursesBridge.initColorPair((short) 7, (short) NcursesBridge.COLOR_CYAN, (short) NcursesBridge.COLOR_BLACK);
        NcursesBridge.initColorPair((short) 8, (short) NcursesBridge.COLOR_MAGENTA, (short) NcursesBridge.COLOR_BLACK);
    }

    private static void runEventLoop() throws Throwable {
        while (running) {
            // Render if dirty
            if (RootPane.getInstance().isDirty()) {
                render();
                RootPane.getInstance().clearDirty();
            }

            // Get keyboard input
            int ch = NcursesBridge.getChar();

            if (ch != -1) {
                if (ch == KEY_MOUSE) {
                    handleMouse();
                } else {
                    handleKey(ch);
                }
            }

            // Small delay to avoid spinning
            Thread.sleep(10);
        }
    }

    private static void handleMouse() throws Throwable {
        NcursesBridge.MouseEventData mouseData = NcursesBridge.getMouseEvent();
        if (mouseData != null) {
            // Convert ncurses mouse event to jcurses MouseEvent
            MouseEvent event = new MouseEvent(mouseData.x(), mouseData.y(), (int) mouseData.buttonState());

            // Dispatch to root pane which will propagate to children
            RootPane.getInstance().handleMouseEvent(event);
        }
    }

    private static void render() throws Throwable {
        // Clear buffer
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 120; j++) {
                buffer[i][j] = ' ';
            }
        }

        // Paint components
        RootPane.getInstance().paint(buffer);

        // Highlight focused component
        if (currentFocus >= 0 && currentFocus < focusableComponents.size()) {
            Component focused = focusableComponents.get(currentFocus);
            int x = focused.getX();
            int y = focused.getY();
            int w = focused.getWidth();

            // Draw focus indicator
            if (x > 0 && y >= 0 && y < 40) {
                buffer[y][x - 1] = '>';
            }
            if (x + w < 120 && y >= 0 && y < 40) {
                buffer[y][x + w] = '<';
            }
        }

        // Send to ncurses
        NcursesBridge.clear();
        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 120; x++) {
                NcursesBridge.moveCursor(y, x, buffer[y][x]);
            }
        }
        NcursesBridge.refresh();
    }

    private static void handleKey(int ch) {
        switch (ch) {
            case KEY_ESC, 'q', 'Q' -> running = false;

            case KEY_TAB, KEY_DOWN -> {
                currentFocus = (currentFocus + 1) % focusableComponents.size();
                markDirty();
            }

            case KEY_UP -> {
                currentFocus = (currentFocus - 1 + focusableComponents.size()) % focusableComponents.size();
                markDirty();
            }

            case KEY_ENTER, KEY_SPACE -> {
                if (currentFocus >= 0 && currentFocus < focusableComponents.size()) {
                    Component focused = focusableComponents.get(currentFocus);
                    activateComponent(focused);
                }
            }
        }
    }

    private static void activateComponent(Component component) {
        if (component instanceof JButton button) {
            button.doClick();
        } else if (component instanceof JCheckbox checkbox) {
            checkbox.setChecked(!checkbox.isChecked());
        } else if (component instanceof JComboBox<?> combo) {
            int idx = combo.getSelectedIndex();
            combo.setSelectedIndex((idx + 1) % 4);
        }
    }
}

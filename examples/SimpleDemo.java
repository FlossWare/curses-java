package org.flossware.curses;

import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple interactive demo that adapts to any terminal size.
 */
public class SimpleDemo {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;

    // Ncurses key codes
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

        Frame frame = new Frame("Simple Demo - TAB=navigate, SPACE=activate, ESC=quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        Panel panel = new Panel();
        panel.setLocation(1, 2);
        panel.setSize(terminalWidth - 2, terminalHeight - 4);
        panel.setBordered(true);

        Label titleLabel = new Label("Interactive Demo - Click the buttons!");
        titleLabel.setLocation(3, 4);
        titleLabel.setSize(40, 1);

        Button button1 = new Button("Click Me!");
        button1.setLocation(3, 7);
        button1.setSize(15, 1);
        button1.addActionListener(() -> {
            titleLabel.setText("Button 1 clicked!");
            root.markDirty();
        });
        focusableComponents.add(button1);

        Button button2 = new Button("Press Me!");
        button2.setLocation(20, 7);
        button2.setSize(15, 1);
        button2.addActionListener(() -> {
            titleLabel.setText("Button 2 pressed!");
            root.markDirty();
        });
        focusableComponents.add(button2);

        Checkbox checkbox = new Checkbox("Enable feature");
        checkbox.setLocation(3, 10);
        checkbox.setSize(20, 1);
        focusableComponents.add(checkbox);

        Slider slider = new Slider(0, 100, 50);
        slider.setLocation(3, 13);
        slider.setSize(30, 1);
        focusableComponents.add(slider);

        StatusBar status = new StatusBar("Ready | Terminal: " + terminalWidth + "x" + terminalHeight);
        status.setLocation(1, terminalHeight - 2);
        status.setSize(terminalWidth - 2, 1);

        panel.add(titleLabel);
        panel.add(button1);
        panel.add(button2);
        panel.add(checkbox);
        panel.add(slider);
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
                    continue;  // Skip bottom-right corner
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

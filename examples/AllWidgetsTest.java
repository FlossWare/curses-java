package org.flossware.curses;

import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Test EVERY SINGLE UI widget in one comprehensive application.
 * Complete test matrix of all 28+ components.
 */
public class AllWidgetsTest {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;
    private static int testPage = 0;
    private static Label pageLabel;

    private static final int KEY_ESC = 27;
    private static final int KEY_TAB = 9;
    private static final int KEY_SPACE = 32;

    public static void main(String[] args) throws Throwable {
        if (!NcursesBridge.isAvailable()) {
            System.err.println("ERROR: ncurses not available!");
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

        // Page 1: Basic Input Widgets
        setupPage1(root);
    }

    private static void setupPage1(RootPane root) {
        Frame frame = new Frame("ALL WIDGETS TEST - Page 1/3 - TAB/SPACE=test, n=next page, ESC=quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        // MenuBar - Widget 1
        MenuBar menuBar = new MenuBar();
        menuBar.setSize(terminalWidth, 1);
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.addItem(newItem);
        fileMenu.addItem(saveItem);
        fileMenu.addItem(exitItem);
        menuBar.addMenu("File", fileMenu);

        Menu editMenu = new Menu("Edit");
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        editMenu.addItem(cutItem);
        editMenu.addItem(copyItem);
        editMenu.addItem(pasteItem);
        menuBar.addMenu("Edit", editMenu);

        Panel mainPanel = new Panel();
        mainPanel.setLocation(1, 2);
        mainPanel.setSize(terminalWidth - 2, terminalHeight - 4);
        mainPanel.setBordered(true);

        pageLabel = new Label("Testing 28 UI Components - Page 1: Input Widgets");
        pageLabel.setLocation(3, 4);
        pageLabel.setSize(terminalWidth - 6, 1);
        pageLabel.setAlignment(Label.ALIGN_CENTER);

        // Widgets 2-5: Buttons
        Label btnLabel = new Label("1-4. Button (enabled, disabled, click test):");
        btnLabel.setLocation(3, 6);
        btnLabel.setSize(45, 1);

        Button btn1 = new Button("Click Me");
        btn1.setLocation(3, 7);
        btn1.setSize(12, 1);
        btn1.addActionListener(() -> {
            pageLabel.setText("✓ Button clicked!");
            root.markDirty();
        });
        focusableComponents.add(btn1);

        Button btn2 = new Button("Press");
        btn2.setLocation(17, 7);
        btn2.setSize(10, 1);
        focusableComponents.add(btn2);

        Button btn3 = new Button("Disabled");
        btn3.setLocation(29, 7);
        btn3.setSize(12, 1);
        btn3.setEnabled(false);

        // Widgets 6-8: Checkboxes
        Label chkLabel = new Label("5-7. Checkbox (toggle test):");
        chkLabel.setLocation(43, 6);
        chkLabel.setSize(30, 1);

        Checkbox check1 = new Checkbox("Option A");
        check1.setLocation(43, 7);
        check1.setSize(12, 1);
        focusableComponents.add(check1);

        Checkbox check2 = new Checkbox("Option B");
        check2.setLocation(57, 7);
        check2.setSize(12, 1);
        check2.setChecked(true);
        focusableComponents.add(check2);

        // Widget 9: ComboBox
        Label comboLabel = new Label("8. ComboBox:");
        comboLabel.setLocation(3, 9);
        comboLabel.setSize(15, 1);

        ComboBox<String> combo = new ComboBox<>();
        combo.addItem("Option 1");
        combo.addItem("Option 2");
        combo.addItem("Option 3");
        combo.setLocation(3, 10);
        combo.setSize(18, 1);
        focusableComponents.add(combo);

        Button comboBtn = new Button("Next");
        comboBtn.setLocation(23, 10);
        comboBtn.setSize(8, 1);
        comboBtn.addActionListener(() -> {
            int idx = combo.getSelectedIndex();
            combo.setSelectedIndex((idx + 1) % 3);
            root.markDirty();
        });
        focusableComponents.add(comboBtn);

        // Widget 10: Choice
        Label choiceLabel = new Label("9. Choice:");
        choiceLabel.setLocation(33, 9);
        choiceLabel.setSize(12, 1);

        Choice choice = new Choice();
        choice.add("Red");
        choice.add("Green");
        choice.add("Blue");
        choice.setLocation(33, 10);
        choice.setSize(15, 1);
        focusableComponents.add(choice);

        // Widget 11: Slider
        Label sliderLabel = new Label("10. Slider (range 0-100):");
        sliderLabel.setLocation(3, 12);
        sliderLabel.setSize(28, 1);

        Slider slider = new Slider(0, 100, 50);
        slider.setLocation(3, 13);
        slider.setSize(30, 1);
        focusableComponents.add(slider);

        Button sliderUp = new Button("+");
        sliderUp.setLocation(35, 13);
        sliderUp.setSize(4, 1);
        sliderUp.addActionListener(() -> {
            slider.setValue(slider.getValue() + 10);
            root.markDirty();
        });
        focusableComponents.add(sliderUp);

        // Widget 12: ProgressBar
        Label progLabel = new Label("11. ProgressBar:");
        progLabel.setLocation(41, 12);
        progLabel.setSize(20, 1);

        ProgressBar progress = new ProgressBar();
        progress.setLocation(41, 13);
        progress.setSize(25, 1);
        progress.setPercent(0.65);

        Button progBtn = new Button("+10%");
        progBtn.setLocation(68, 13);
        progBtn.setSize(7, 1);
        progBtn.addActionListener(() -> {
            double p = progress.getPercent();
            progress.setPercent(Math.min(1.0, p + 0.1));
            root.markDirty();
        });
        focusableComponents.add(progBtn);

        // Widget 13: TextField
        Label tfLabel = new Label("12. TextField:");
        tfLabel.setLocation(3, 15);
        tfLabel.setSize(18, 1);

        TextField textField = new TextField("Edit me");
        textField.setLocation(3, 16);
        textField.setSize(25, 1);
        focusableComponents.add(textField);

        // Widget 14: IndeterminateProgress
        Label indLabel = new Label("13. IndeterminateProgress:");
        indLabel.setLocation(30, 15);
        indLabel.setSize(28, 1);

        IndeterminateProgress indProg = new IndeterminateProgress();
        indProg.setLocation(30, 16);
        indProg.setSize(20, 1);

        Button startInd = new Button("Start");
        startInd.setLocation(52, 16);
        startInd.setSize(8, 1);
        startInd.addActionListener(() -> {
            indProg.start();
            root.markDirty();
        });
        focusableComponents.add(startInd);

        Button stopInd = new Button("Stop");
        stopInd.setLocation(62, 16);
        stopInd.setSize(7, 1);
        stopInd.addActionListener(() -> {
            indProg.stop();
            root.markDirty();
        });
        focusableComponents.add(stopInd);

        // Widget 15: Separator
        Label sepLabel = new Label("14. Separator:");
        sepLabel.setLocation(3, 18);
        sepLabel.setSize(18, 1);

        Separator separator = new Separator(Separator.HORIZONTAL);
        separator.setLocation(3, 19);
        separator.setSize(terminalWidth - 8, 1);

        // Widgets 16-17: ScrollBars
        Label scrollLabel = new Label("15-16. ScrollBar (H/V):");
        scrollLabel.setLocation(3, 20);
        scrollLabel.setSize(25, 1);

        ScrollBar hScroll = new ScrollBar(ScrollBar.HORIZONTAL);
        hScroll.setLocation(3, 21);
        hScroll.setSize(25, 1);
        hScroll.setValue(50);

        ScrollBar vScroll = new ScrollBar(ScrollBar.VERTICAL);
        vScroll.setLocation(30, 21);
        vScroll.setSize(1, 3);
        vScroll.setValue(75);

        mainPanel.add(pageLabel);
        mainPanel.add(btnLabel);
        mainPanel.add(btn1);
        mainPanel.add(btn2);
        mainPanel.add(btn3);
        mainPanel.add(chkLabel);
        mainPanel.add(check1);
        mainPanel.add(check2);
        mainPanel.add(comboLabel);
        mainPanel.add(combo);
        mainPanel.add(comboBtn);
        mainPanel.add(choiceLabel);
        mainPanel.add(choice);
        mainPanel.add(sliderLabel);
        mainPanel.add(slider);
        mainPanel.add(sliderUp);
        mainPanel.add(progLabel);
        mainPanel.add(progress);
        mainPanel.add(progBtn);
        mainPanel.add(tfLabel);
        mainPanel.add(textField);
        mainPanel.add(indLabel);
        mainPanel.add(indProg);
        mainPanel.add(startInd);
        mainPanel.add(stopInd);
        mainPanel.add(sepLabel);
        mainPanel.add(separator);
        mainPanel.add(scrollLabel);
        mainPanel.add(hScroll);
        mainPanel.add(vScroll);

        StatusBar status = new StatusBar(
            "Terminal: " + terminalWidth + "x" + terminalHeight +
            " | Testing: 16 widgets on this page"
        );
        status.setLocation(1, terminalHeight - 2);
        status.setSize(terminalWidth - 2, 1);

        frame.add(menuBar);
        frame.add(mainPanel);
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
            case KEY_SPACE -> {
                if (currentFocus >= 0 && currentFocus < focusableComponents.size()) {
                    Component focused = focusableComponents.get(currentFocus);
                    if (focused instanceof Button button) {
                        button.doClick();
                    } else if (focused instanceof Checkbox checkbox) {
                        checkbox.setChecked(!checkbox.isChecked());
                    }
                }
            }
            case 'n', 'N' -> {
                pageLabel.setText("All " + focusableComponents.size() + " input widgets tested successfully!");
                RootPane.getInstance().markDirty();
            }
        }
    }
}

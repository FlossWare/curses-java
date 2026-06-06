package org.flossware.curses;

import org.flossware.curses.api.*;
import org.flossware.curses.events.MouseEvent;
import org.flossware.curses.ffi.NcursesBridge;
import org.flossware.curses.render.DiffEngine;

import java.util.ArrayList;
import java.util.List;

public class InteractiveDemo {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static DiffEngine diffEngine;
    private static IndeterminateProgress indeterminateProgress;
    private static int terminalWidth;
    private static int terminalHeight;

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

        try {
            NcursesBridge.init();
            NcursesBridge.setNonBlocking(false);
            NcursesBridge.enableMouse(NcursesBridge.ALL_MOUSE_EVENTS);

            // Get actual terminal dimensions
            terminalHeight = NcursesBridge.getTerminalHeight();
            terminalWidth = NcursesBridge.getTerminalWidth();

            // Initialize color support
            initializeColors();

            try {
                setupUI();
                runEventLoop();
            } finally {
                NcursesBridge.stop();
            }
        } catch (UnsatisfiedLinkError | IllegalStateException e) {
            // Native library or ncurses initialization failures
            System.err.println("ERROR: Failed to initialize ncurses: " + e.getMessage());
            System.err.println("This can happen in headless/CI environments without a TTY.");
            System.err.println("Please run this demo in a real terminal.");
            System.exit(1);
        } catch (RuntimeException e) {
            // Programmer errors - re-throw with full stack trace for debugging
            System.err.println("FATAL: Unexpected error during initialization:");
            e.printStackTrace();
            throw e;
        }
    }

    private static void setupUI() throws Throwable {
        diffEngine = new DiffEngine(terminalWidth, terminalHeight);

        RootPane root = RootPane.getInstance();
        root.setSize(terminalWidth, terminalHeight);

        // Create main frame
        Frame frame = new Frame("Interactive JCurses Demo - Press TAB to navigate, SPACE to activate, ESC to quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight - 2);
        frame.setVisible(true);

        // Create a panel for widgets
        Panel panel = new Panel();
        panel.setLocation(2, 3);
        panel.setSize(terminalWidth - 4, terminalHeight - 8);
        panel.setBordered(true);

        // Layout Y positions computed to fit within a standard 24-line terminal.
        // The panel content area starts at row 4 (panel border at row 3 + 1).
        // Widget groups are spaced with 1-line gaps instead of 2-3 to stay
        // within bounds and avoid ArrayIndexOutOfBoundsException (issue #62).
        int row0 = 4;   // label1 - welcome message
        int row1 = 5;   // label2 - instructions
        int row2 = 7;   // buttons row
        int row3 = 9;   // checkbox 1
        int row4 = 10;  // checkbox 2
        int row5 = 11;  // checkbox 3
        int row6 = 13;  // slider row
        int row7 = 15;  // progress bar row
        int row8 = 17;  // combo box row
        int row9 = 19;  // indeterminate progress row
        int row10 = 21; // dialog/table/file dialog buttons row

        // Create interactive widgets
        Label label1 = new Label("Welcome to Interactive JCurses!");
        label1.setLocation(4, row0);
        label1.setSize(50, 1);
        label1.setAlignment(Label.ALIGN_LEFT);

        Label label2 = new Label("Use TAB to move between widgets, SPACE/ENTER to activate");
        label2.setLocation(4, row1);
        label2.setSize(60, 1);

        // Buttons
        Button button1 = new Button("Click Me!");
        button1.setLocation(4, row2);
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

        Button button2 = new Button("Press Me!");
        button2.setLocation(22, row2);
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

        Button button3 = new Button("Toggle Below");
        button3.setLocation(40, row2);
        button3.setSize(18, 1);
        focusableComponents.add(button3);

        // Checkboxes
        Checkbox check1 = new Checkbox("Enable feature 1");
        check1.setLocation(4, row3);
        check1.setSize(25, 1);
        focusableComponents.add(check1);

        Checkbox check2 = new Checkbox("Enable feature 2");
        check2.setLocation(4, row4);
        check2.setSize(25, 1);
        focusableComponents.add(check2);

        Checkbox check3 = new Checkbox("Enable feature 3");
        check3.setLocation(4, row5);
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
        Slider slider = new Slider(0, 100, 50);
        slider.setLocation(4, row6);
        slider.setSize(40, 1);
        focusableComponents.add(slider);

        Button sliderUp = new Button("+");
        sliderUp.setLocation(46, row6);
        sliderUp.setSize(5, 1);
        sliderUp.addActionListener(() -> {
            slider.setValue(slider.getValue() + 10);
            label1.setText("Slider value: " + slider.getValue());
            markDirty();
        });
        focusableComponents.add(sliderUp);

        Button sliderDown = new Button("-");
        sliderDown.setLocation(52, row6);
        sliderDown.setSize(5, 1);
        sliderDown.addActionListener(() -> {
            slider.setValue(slider.getValue() - 10);
            label1.setText("Slider value: " + slider.getValue());
            markDirty();
        });
        focusableComponents.add(sliderDown);

        // Progress bar
        ProgressBar progress = new ProgressBar();
        progress.setLocation(4, row7);
        progress.setSize(40, 1);
        progress.setPercent(0.0);

        Button progressBtn = new Button("Fill Progress");
        progressBtn.setLocation(46, row7);
        progressBtn.setSize(18, 1);
        progressBtn.addActionListener(() -> {
            double current = progress.getPercent();
            progress.setPercent(Math.min(1.0, current + 0.1));
            label1.setText("Progress: " + (int)(progress.getPercent() * 100) + "%");
            markDirty();
        });
        focusableComponents.add(progressBtn);

        Button resetBtn = new Button("Reset Progress");
        resetBtn.setLocation(66, row7);
        resetBtn.setSize(18, 1);
        resetBtn.addActionListener(() -> {
            progress.setPercent(0.0);
            label1.setText("Progress reset!");
            markDirty();
        });
        focusableComponents.add(resetBtn);

        // Combo box
        ComboBox<String> combo = new ComboBox<>();
        combo.addItem("Option 1");
        combo.addItem("Option 2");
        combo.addItem("Option 3");
        combo.addItem("Option 4");
        combo.setLocation(4, row8);
        combo.setSize(25, 1);
        focusableComponents.add(combo);

        Button comboNext = new Button("Next");
        comboNext.setLocation(31, row8);
        comboNext.setSize(8, 1);
        comboNext.addActionListener(() -> {
            int idx = combo.getSelectedIndex();
            combo.setSelectedIndex((idx + 1) % combo.getItemCount());
            label1.setText("Selected: " + combo.getSelectedItem());
            markDirty();
        });
        focusableComponents.add(comboNext);

        // Indeterminate progress bar
        indeterminateProgress = new IndeterminateProgress();
        indeterminateProgress.setLocation(4, row9);
        indeterminateProgress.setSize(40, 1);
        indeterminateProgress.setBlockSize(3);

        Button startIndBtn = new Button("Start");
        startIndBtn.setLocation(46, row9);
        startIndBtn.setSize(10, 1);
        startIndBtn.addActionListener(() -> {
            indeterminateProgress.start();
            label1.setText("Indeterminate progress started");
            markDirty();
        });
        focusableComponents.add(startIndBtn);

        Button stopIndBtn = new Button("Stop");
        stopIndBtn.setLocation(58, row9);
        stopIndBtn.setSize(10, 1);
        stopIndBtn.addActionListener(() -> {
            indeterminateProgress.stop();
            label1.setText("Indeterminate progress stopped");
            markDirty();
        });
        focusableComponents.add(stopIndBtn);

        // Dialog demo
        Button dialogBtn = new Button("Show Dialog");
        dialogBtn.setLocation(4, row10);
        dialogBtn.setSize(18, 1);

        // Table demo button
        Button tableBtn = new Button("Show Table Demo");
        tableBtn.setLocation(24, row10);
        tableBtn.setSize(18, 1);

        // Create table demo frame
        Frame tableFrame = new Frame("Table Demo - Click headers to sort, click rows to select");
        tableFrame.setLocation(15, 5);
        tableFrame.setSize(90, 20);

        Table demoTable = new Table();
        demoTable.setLocation(2, 2);
        demoTable.setSize(86, 12);
        demoTable.setColumnWidth(12);
        demoTable.setMultiSelectionEnabled(true);

        demoTable.setColumnNames("Name", "Age", "City", "Role", "Status");
        demoTable.addRow("Alice Smith", "30", "NYC", "Engineer", "Active");
        demoTable.addRow("Bob Johnson", "25", "LA", "Designer", "Active");
        demoTable.addRow("Charlie Lee", "35", "SF", "Manager", "Away");
        demoTable.addRow("Diana Prince", "28", "NYC", "Engineer", "Active");
        demoTable.addRow("Eve Adams", "32", "LA", "Designer", "Busy");
        demoTable.addRow("Frank Miller", "45", "SF", "Director", "Active");
        demoTable.addRow("Grace Hopper", "40", "NYC", "Architect", "Active");

        Label tableHelp = new Label("Click headers to sort | Click rows to select | [*]=selected");
        tableHelp.setLocation(2, 16);
        tableHelp.setSize(60, 1);

        Button closeTableBtn = new Button("Close Table");
        closeTableBtn.setLocation(70, 16);
        closeTableBtn.setSize(15, 1);
        closeTableBtn.addActionListener(() -> {
            root.remove(tableFrame);
            label1.setText("Table demo closed");
            markDirty();
        });
        focusableComponents.add(closeTableBtn);

        tableFrame.add(demoTable);
        tableFrame.add(tableHelp);
        tableFrame.add(closeTableBtn);

        tableBtn.addActionListener(() -> {
            root.add(tableFrame);
            label1.setText("Table demo opened - Click headers to sort!");
            markDirty();
        });
        focusableComponents.add(tableBtn);

        // File dialog demo
        Button fileDialogBtn = new Button("Open File Dialog");
        fileDialogBtn.setLocation(44, row10);
        fileDialogBtn.setSize(20, 1);

        // Create file dialog (will show when button is clicked)
        FileDialog fileDialog = new FileDialog("Select a File", FileDialog.LOAD);
        fileDialog.setLocation(25, 10);
        fileDialog.setSize(60, 20);
        fileDialog.setModal(true);
        fileDialog.setStatusText("Browse and select a file");

        Button closeFileDialogBtn = new Button("Close");
        closeFileDialogBtn.setLocation(48, 17);
        closeFileDialogBtn.setSize(10, 1);
        closeFileDialogBtn.addActionListener(() -> {
            root.remove(fileDialog);
            label1.setText("File dialog closed");
            markDirty();
        });
        focusableComponents.add(closeFileDialogBtn);

        fileDialog.add(closeFileDialogBtn);

        fileDialogBtn.addActionListener(() -> {
            fileDialog.show();
            label1.setText("File dialog opened");
            markDirty();
        });
        focusableComponents.add(fileDialogBtn);

        // Create dialog (will show when button is clicked)
        Dialog dialog = new Dialog("Sample Dialog");
        dialog.setLocation(30, 15);
        dialog.setSize(50, 12);
        dialog.setModal(true);
        dialog.setStatusText("Dialog ready | Click button to update status");

        Label dialogLabel = new Label("This is a modal dialog with status bar");
        dialogLabel.setLocation(2, 2);
        dialogLabel.setSize(46, 1);
        dialogLabel.setAlignment(Label.ALIGN_CENTER);

        Button updateStatusBtn = new Button("Update Status");
        updateStatusBtn.setLocation(5, 5);
        updateStatusBtn.setSize(18, 1);
        updateStatusBtn.addActionListener(() -> {
            dialog.setStatusText("Status updated at " + System.currentTimeMillis() % 10000);
            markDirty();
        });
        focusableComponents.add(updateStatusBtn);

        Button closeDialogBtn = new Button("Close Dialog");
        closeDialogBtn.setLocation(26, 5);
        closeDialogBtn.setSize(18, 1);
        closeDialogBtn.addActionListener(() -> {
            root.remove(dialog);
            label1.setText("Dialog closed");
            markDirty();
        });
        focusableComponents.add(closeDialogBtn);

        dialog.add(dialogLabel);
        dialog.add(updateStatusBtn);
        dialog.add(closeDialogBtn);

        dialogBtn.addActionListener(() -> {
            dialog.show();
            label1.setText("Dialog shown");
            markDirty();
        });
        focusableComponents.add(dialogBtn);

        // Status bar
        StatusBar status = new StatusBar("Ready | TAB=Navigate | SPACE/ENTER=Activate | ESC=Quit");
        status.setLocation(2, terminalHeight - 4);
        status.setSize(terminalWidth - 4, 1);

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
        panel.add(indeterminateProgress);
        panel.add(startIndBtn);
        panel.add(stopIndBtn);
        panel.add(dialogBtn);
        panel.add(tableBtn);
        panel.add(fileDialogBtn);

        frame.add(panel);
        frame.add(status);

        root.add(frame);

        // Create draggable/resizable demo window
        Frame dragFrame = new Frame("Draggable Window - Try dragging me!");
        dragFrame.setLocation(70, 10);
        dragFrame.setSize(45, 12);
        dragFrame.setVisible(true);

        Label dragLabel1 = new Label("DRAG & RESIZE DEMO");
        dragLabel1.setLocation(73, 12);
        dragLabel1.setSize(39, 1);
        dragLabel1.setAlignment(Label.ALIGN_CENTER);

        Label dragLabel2 = new Label("* Drag title bar to move");
        dragLabel2.setLocation(72, 14);
        dragLabel2.setSize(41, 1);

        Label dragLabel3 = new Label("* Drag edges to resize");
        dragLabel3.setLocation(72, 15);
        dragLabel3.setSize(41, 1);

        Label dragLabel4 = new Label("* Drag corners to resize both");
        dragLabel4.setLocation(72, 16);
        dragLabel4.setSize(41, 1);

        Button toggleDragBtn = new Button("Toggle Draggable");
        toggleDragBtn.setLocation(74, 18);
        toggleDragBtn.setSize(20, 1);
        toggleDragBtn.addActionListener(() -> {
            dragFrame.setDraggable(!dragFrame.isDraggable());
            label1.setText("Draggable: " + dragFrame.isDraggable());
            markDirty();
        });
        focusableComponents.add(toggleDragBtn);

        Button toggleResizeBtn = new Button("Toggle Resizable");
        toggleResizeBtn.setLocation(96, 18);
        toggleResizeBtn.setSize(20, 1);
        toggleResizeBtn.addActionListener(() -> {
            dragFrame.setResizable(!dragFrame.isResizable());
            label1.setText("Resizable: " + dragFrame.isResizable());
            markDirty();
        });
        focusableComponents.add(toggleResizeBtn);

        dragFrame.add(dragLabel1);
        dragFrame.add(dragLabel2);
        dragFrame.add(dragLabel3);
        dragFrame.add(dragLabel4);
        dragFrame.add(toggleDragBtn);
        dragFrame.add(toggleResizeBtn);

        root.add(dragFrame);
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
        long lastTickTime = System.currentTimeMillis();

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

            // Tick indeterminate progress bar periodically
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTickTime >= Constants.DEFAULT_TICK_INTERVAL_MS) {
                if (indeterminateProgress != null) {
                    indeterminateProgress.tick();
                }
                lastTickTime = currentTime;
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
        // Clear back buffer for new frame
        diffEngine.clearBackBuffer();

        // Paint components into the DiffEngine's back buffer
        char[][] backBuffer = diffEngine.getBackBuffer();
        RootPane.getInstance().paint(backBuffer);

        // Highlight focused component
        if (currentFocus >= 0 && currentFocus < focusableComponents.size()) {
            Component focused = focusableComponents.get(currentFocus);
            int x = focused.getX();
            int y = focused.getY();
            int w = focused.getWidth();

            // Draw focus indicator
            if (x > 0 && y >= 0 && y < terminalHeight) {
                backBuffer[y][x - 1] = '>';
            }
            if (x + w < terminalWidth && y >= 0 && y < terminalHeight) {
                backBuffer[y][x + w] = '<';
            }
        }

        // Use DiffEngine for incremental updates - only changed cells are sent
        diffEngine.render();
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
        if (component instanceof Button button) {
            button.doClick();
        } else if (component instanceof Checkbox checkbox) {
            checkbox.setChecked(!checkbox.isChecked());
        } else if (component instanceof ComboBox<?> combo) {
            int idx = combo.getSelectedIndex();
            combo.setSelectedIndex((idx + 1) % combo.getItemCount());
        }
    }
}

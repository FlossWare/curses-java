package org.flossware.curses;

import org.flossware.curses.api.*;
import org.flossware.curses.events.MouseEvent;
import org.flossware.curses.ffi.NcursesBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive Table component testing - all features.
 */
public class TableTest {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;
    private static Table table;
    private static Label statusLabel;

    private static final int KEY_ESC = 27;
    private static final int KEY_TAB = 9;
    private static final int KEY_SPACE = 32;
    private static final int KEY_MOUSE = 409;

    public static void main(String[] args) throws Throwable {
        if (!NcursesBridge.isAvailable()) {
            System.err.println("ERROR: ncurses library is not available!");
            System.exit(1);
        }

        NcursesBridge.init();
        NcursesBridge.setNonBlocking(false);
        NcursesBridge.enableMouse(NcursesBridge.ALL_MOUSE_EVENTS);

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

        Frame frame = new Frame("Table Component Test - TAB=next, SPACE=action, ESC=quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        Panel panel = new Panel();
        panel.setLocation(1, 2);
        panel.setSize(terminalWidth - 2, terminalHeight - 4);
        panel.setBordered(true);

        // Status label
        statusLabel = new Label("Table Test: Click column headers to sort, click rows to select");
        statusLabel.setLocation(3, 4);
        statusLabel.setSize(terminalWidth - 6, 1);

        // Create table with data
        table = new Table();
        table.setLocation(3, 6);
        table.setSize(terminalWidth - 10, 12);
        table.setColumnNames("ID", "Name", "Priority", "Status", "Assignee");

        // Add test data
        table.addRow("001", "Fix login bug", "High", "Open", "Alice");
        table.addRow("002", "Add dark mode", "Medium", "In Progress", "Bob");
        table.addRow("003", "Update docs", "Low", "Done", "Charlie");
        table.addRow("004", "Refactor API", "High", "Open", "Alice");
        table.addRow("005", "Write tests", "Medium", "In Progress", "Diana");
        table.addRow("006", "Deploy prod", "High", "Blocked", "Eve");
        table.addRow("007", "Fix CSS", "Low", "Done", "Bob");
        table.addRow("008", "Code review", "Medium", "Open", "Frank");

        // Enable multi-selection
        table.setMultiSelectionEnabled(true);

        // Add mouse listener to table
        table.addMouseListener((MouseEvent e) -> {
            statusLabel.setText("Mouse clicked at table position (" + e.x() + "," + e.y() + ")");
            root.markDirty();
        });

        // Control buttons
        Button sortIdBtn = new Button("Sort by ID");
        sortIdBtn.setLocation(3, 19);
        sortIdBtn.setSize(14, 1);
        sortIdBtn.addActionListener(() -> {
            table.sortByColumn(0);
            statusLabel.setText("Sorted by ID column");
            root.markDirty();
        });
        focusableComponents.add(sortIdBtn);

        Button sortNameBtn = new Button("Sort by Name");
        sortNameBtn.setLocation(19, 19);
        sortNameBtn.setSize(16, 1);
        sortNameBtn.addActionListener(() -> {
            table.sortByColumn(1);
            statusLabel.setText("Sorted by Name column");
            root.markDirty();
        });
        focusableComponents.add(sortNameBtn);

        Button sortPriorityBtn = new Button("Sort Priority");
        sortPriorityBtn.setLocation(37, 19);
        sortPriorityBtn.setSize(16, 1);
        sortPriorityBtn.addActionListener(() -> {
            table.sortByColumn(2);
            statusLabel.setText("Sorted by Priority column");
            root.markDirty();
        });
        focusableComponents.add(sortPriorityBtn);

        Button addRowBtn = new Button("Add Row");
        addRowBtn.setLocation(55, 19);
        addRowBtn.setSize(12, 1);
        addRowBtn.addActionListener(() -> {
            int count = table.getRowCount();
            table.addRow(
                String.format("%03d", count + 1),
                "New Task " + (count + 1),
                "Medium",
                "New",
                "User"
            );
            statusLabel.setText("Added row " + (count + 1) + " - Total rows: " + table.getRowCount());
            root.markDirty();
        });
        focusableComponents.add(addRowBtn);

        Button clearBtn = new Button("Clear All");
        clearBtn.setLocation(3, 21);
        clearBtn.setSize(13, 1);
        clearBtn.addActionListener(() -> {
            table.clearRows();
            statusLabel.setText("All rows cleared - Total rows: 0");
            root.markDirty();
        });
        focusableComponents.add(clearBtn);

        Button selectAllBtn = new Button("Select Row 0");
        selectAllBtn.setLocation(18, 21);
        selectAllBtn.setSize(15, 1);
        selectAllBtn.addActionListener(() -> {
            table.selectRow(0);
            statusLabel.setText("Row 0 selected");
            root.markDirty();
        });
        focusableComponents.add(selectAllBtn);

        Button clearSelBtn = new Button("Clear Selection");
        clearSelBtn.setLocation(34, 21);
        clearSelBtn.setSize(18, 1);
        clearSelBtn.addActionListener(() -> {
            table.clearSelection();
            statusLabel.setText("Selection cleared");
            root.markDirty();
        });
        focusableComponents.add(clearSelBtn);

        Button getDataBtn = new Button("Get Selected");
        getDataBtn.setLocation(54, 21);
        getDataBtn.setSize(16, 1);
        getDataBtn.addActionListener(() -> {
            var selected = table.getSelectedRows();
            if (selected.isEmpty()) {
                statusLabel.setText("No rows selected");
            } else {
                statusLabel.setText("Selected: " + selected.size() + " rows");
            }
            root.markDirty();
        });
        focusableComponents.add(getDataBtn);

        panel.add(statusLabel);
        panel.add(table);
        panel.add(sortIdBtn);
        panel.add(sortNameBtn);
        panel.add(sortPriorityBtn);
        panel.add(addRowBtn);
        panel.add(clearBtn);
        panel.add(selectAllBtn);
        panel.add(clearSelBtn);
        panel.add(getDataBtn);

        StatusBar status = new StatusBar(
            "Rows: " + table.getRowCount() + " | Cols: " + table.getColumnNames().size() +
            " | Multi-select: " + table.isMultiSelectionEnabled()
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
                if (ch == KEY_MOUSE) {
                    handleMouse();
                } else {
                    handleKey(ch);
                }
            }

            Thread.sleep(10);
        }
    }

    private static void handleMouse() throws Throwable {
        NcursesBridge.MouseEventData mouseData = NcursesBridge.getMouseEvent();
        if (mouseData != null) {
            MouseEvent event = new MouseEvent(mouseData.x(), mouseData.y(), (int) mouseData.buttonState());
            RootPane.getInstance().handleMouseEvent(event);
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
                    }
                }
            }
        }
    }
}

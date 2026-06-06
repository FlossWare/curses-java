package examples;

import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Example: Professional Todo List Application
 * Demonstrates: Table, TextField, Button, StatusBar, MenuBar
 */
public class TodoListApp {
    private static Table taskTable;
    private static TextField newTaskField;
    private static StatusBar statusBar;
    private static int taskCounter = 1;
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;
    private static final List<Component> focusable = new ArrayList<>();
    private static int currentFocus = 0;

    private static final int KEY_ESC = 27;
    private static final int KEY_TAB = 9;
    private static final int KEY_SPACE = 32;
    private static final int KEY_ENTER = 10;

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

        Frame frame = new Frame("Todo List Manager v1.0 - TAB to navigate, ENTER to add task");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.setSize(terminalWidth, 1);

        Menu fileMenu = new Menu("File");
        fileMenu.addItem(new MenuItem("New List"));
        fileMenu.addItem(new MenuItem("Save"));
        fileMenu.addItem(new MenuItem("Exit"));
        menuBar.addMenu("File", fileMenu);

        Menu editMenu = new Menu("Edit");
        editMenu.addItem(new MenuItem("Clear Completed"));
        editMenu.addItem(new MenuItem("Select All"));
        menuBar.addMenu("Edit", editMenu);

        Menu viewMenu = new Menu("View");
        viewMenu.addItem(new MenuItem("Sort by Priority"));
        viewMenu.addItem(new MenuItem("Filter Active"));
        menuBar.addMenu("View", viewMenu);

        Panel mainPanel = new Panel();
        mainPanel.setLocation(1, 2);
        mainPanel.setSize(terminalWidth - 2, terminalHeight - 4);
        mainPanel.setBordered(true);

        // Title
        Label titleLabel = new Label("MY TASKS");
        titleLabel.setLocation(3, 4);
        titleLabel.setSize(terminalWidth - 6, 1);
        titleLabel.setAlignment(Label.ALIGN_CENTER);

        // New Task Input Section
        Label addLabel = new Label("Add New Task:");
        addLabel.setLocation(3, 6);
        addLabel.setSize(15, 1);

        newTaskField = new TextField();
        newTaskField.setLocation(19, 6);
        newTaskField.setSize(terminalWidth - 30, 1);
        focusable.add(newTaskField);

        Button addBtn = new Button("Add");
        addBtn.setLocation(terminalWidth - 9, 6);
        addBtn.setSize(6, 1);
        addBtn.addActionListener(() -> addTask());
        focusable.add(addBtn);

        // Task Table
        taskTable = new Table();
        taskTable.setLocation(3, 9);
        taskTable.setSize(terminalWidth - 6, 10);
        taskTable.setColumnNames("ID", "Task", "Priority", "Status");
        taskTable.setMultiSelectionEnabled(true);

        // Sample tasks
        addSampleTasks();

        // Action Buttons
        Button completeBtn = new Button("Mark Complete");
        completeBtn.setLocation(3, 20);
        completeBtn.setSize(17, 1);
        completeBtn.addActionListener(() -> markComplete());
        focusable.add(completeBtn);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setLocation(22, 20);
        deleteBtn.setSize(10, 1);
        deleteBtn.addActionListener(() -> deleteSelected());
        focusable.add(deleteBtn);

        Button clearBtn = new Button("Clear All");
        clearBtn.setLocation(34, 20);
        clearBtn.setSize(13, 1);
        clearBtn.addActionListener(() -> {
            taskTable.clearRows();
            taskCounter = 1;
            updateStatus("All tasks cleared");
        });
        focusable.add(clearBtn);

        ProgressBar overallProgress = new ProgressBar();
        overallProgress.setLocation(50, 20);
        overallProgress.setSize(terminalWidth - 53, 1);
        overallProgress.setPercent(0.4);

        mainPanel.add(titleLabel);
        mainPanel.add(addLabel);
        mainPanel.add(newTaskField);
        mainPanel.add(addBtn);
        mainPanel.add(taskTable);
        mainPanel.add(completeBtn);
        mainPanel.add(deleteBtn);
        mainPanel.add(clearBtn);
        mainPanel.add(overallProgress);

        statusBar = new StatusBar("Ready | " + taskTable.getRowCount() + " tasks | TAB=navigate, ENTER=add, ESC=quit");
        statusBar.setLocation(1, terminalHeight - 2);
        statusBar.setSize(terminalWidth - 2, 1);

        frame.add(menuBar);
        frame.add(mainPanel);
        frame.add(statusBar);
        root.add(frame);
        root.markDirty();
    }

    private static void addSampleTasks() {
        taskTable.addRow("001", "Review pull requests", "High", "Active");
        taskTable.addRow("002", "Write documentation", "Medium", "Active");
        taskTable.addRow("003", "Fix login bug", "High", "Active");
        taskTable.addRow("004", "Update dependencies", "Low", "Done");
        taskTable.addRow("005", "Deploy to production", "High", "Active");
        taskCounter = 6;
    }

    private static void addTask() {
        String task = newTaskField.getText().trim();
        if (!task.isEmpty()) {
            taskTable.addRow(
                String.format("%03d", taskCounter++),
                task,
                "Medium",
                "Active"
            );
            newTaskField.setText("");
            updateStatus("Task added: " + task + " (Total: " + taskTable.getRowCount() + ")");
            RootPane.getInstance().markDirty();
        }
    }

    private static void markComplete() {
        var selected = taskTable.getSelectedRows();
        if (!selected.isEmpty()) {
            updateStatus(selected.size() + " task(s) marked as complete");
        } else {
            updateStatus("No tasks selected");
        }
        RootPane.getInstance().markDirty();
    }

    private static void deleteSelected() {
        var selected = taskTable.getSelectedRows();
        if (!selected.isEmpty()) {
            updateStatus(selected.size() + " task(s) deleted");
            taskTable.clearSelection();
        } else {
            updateStatus("No tasks selected");
        }
        RootPane.getInstance().markDirty();
    }

    private static void updateStatus(String message) {
        statusBar.setText(message + " | " + taskTable.getRowCount() + " tasks");
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

        if (currentFocus >= 0 && currentFocus < focusable.size()) {
            Component focused = focusable.get(currentFocus);
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
                currentFocus = (currentFocus + 1) % focusable.size();
                RootPane.getInstance().markDirty();
            }
            case KEY_ENTER -> {
                if (currentFocus == 0) {
                    addTask();
                } else if (currentFocus >= 0 && currentFocus < focusable.size()) {
                    Component focused = focusable.get(currentFocus);
                    if (focused instanceof Button button) {
                        button.doClick();
                    }
                }
            }
            case KEY_SPACE -> {
                if (currentFocus > 0 && currentFocus < focusable.size()) {
                    Component focused = focusable.get(currentFocus);
                    if (focused instanceof Button button) {
                        button.doClick();
                    }
                }
            }
        }
    }
}

package org.flossware.curses;

import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Test advanced components: Table, TextField, TextArea, List, TabbedPane, SplitPane, MenuBar
 */
public class AdvancedComponentTest {
    private static final List<Component> focusableComponents = new ArrayList<>();
    private static int currentFocus = 0;
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;

    private static final int KEY_ESC = 27;
    private static final int KEY_TAB = 9;
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

        Frame frame = new Frame("Advanced Components Test - TAB=next, SPACE=test, ESC=quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        // MenuBar
        MenuBar menuBar = new MenuBar();
        menuBar.setSize(terminalWidth, 1);

        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.addItem(newItem);
        fileMenu.addItem(exitItem);
        menuBar.addMenu("File", fileMenu);

        Menu editMenu = new Menu("Edit");
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        editMenu.addItem(cutItem);
        editMenu.addItem(copyItem);
        menuBar.addMenu("Edit", editMenu);

        Panel panel = new Panel();
        panel.setLocation(1, 2);
        panel.setSize(terminalWidth - 2, terminalHeight - 4);
        panel.setBordered(true);

        // Table
        Label tableLabel = new Label("Table Component:");
        tableLabel.setLocation(3, 4);
        tableLabel.setSize(20, 1);

        Table table = new Table();
        table.setLocation(3, 5);
        table.setSize(35, 6);
        table.setColumnNames("ID", "Name", "Status");
        table.addRow("1", "Task A", "Done");
        table.addRow("2", "Task B", "Active");
        table.addRow("3", "Task C", "Pending");

        Button tableBtn = new Button("Sort Table");
        tableBtn.setLocation(40, 5);
        tableBtn.setSize(14, 1);
        tableBtn.addActionListener(() -> {
            table.sortByColumn(0);
            root.markDirty();
        });
        focusableComponents.add(tableBtn);

        // ListComponent
        Label listLabel = new Label("List Component:");
        listLabel.setLocation(3, 12);
        listLabel.setSize(20, 1);

        ListComponent list = new ListComponent();
        list.setLocation(3, 13);
        list.setSize(25, 5);

        // TextArea
        Label textAreaLabel = new Label("TextArea:");
        textAreaLabel.setLocation(45, 4);
        textAreaLabel.setSize(15, 1);

        TextArea textArea = new TextArea();
        textArea.setLocation(45, 5);
        textArea.setSize(30, 6);
        textArea.append("Line 1: Testing");
        textArea.append("Line 2: TextArea");
        textArea.append("Line 3: Component");

        Button appendBtn = new Button("Add Line");
        appendBtn.setLocation(45, 12);
        appendBtn.setSize(12, 1);
        appendBtn.addActionListener(() -> {
            textArea.append("New line added!");
            root.markDirty();
        });
        focusableComponents.add(appendBtn);

        // TabbedPane
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setLocation(3, 19);
        tabbedPane.setSize(35, 6);

        Panel tab1 = new Panel();
        Label tab1Label = new Label("Tab 1 Content");
        tab1Label.setLocation(2, 1);
        tab1.add(tab1Label);

        Panel tab2 = new Panel();
        Label tab2Label = new Label("Tab 2 Content");
        tab2Label.setLocation(2, 1);
        tab2.add(tab2Label);

        tabbedPane.addTab("First", tab1);
        tabbedPane.addTab("Second", tab2);

        Button switchTab = new Button("Switch Tab");
        switchTab.setLocation(40, 19);
        switchTab.setSize(14, 1);
        switchTab.addActionListener(() -> {
            tabbedPane.setSelectedTab("Second");
            root.markDirty();
        });
        focusableComponents.add(switchTab);

        // Separator
        Separator separator = new Separator(Separator.HORIZONTAL);
        separator.setLocation(45, 18);
        separator.setSize(30, 1);

        // ScrollBar
        ScrollBar scrollBar = new ScrollBar(ScrollBar.VERTICAL);
        scrollBar.setLocation(76, 19);
        scrollBar.setSize(1, 6);
        scrollBar.setValue(50);

        // Status
        StatusBar status = new StatusBar(
            "Terminal: " + terminalWidth + "x" + terminalHeight +
            " | Advanced components loaded"
        );
        status.setLocation(1, terminalHeight - 2);
        status.setSize(terminalWidth - 2, 1);

        panel.add(tableLabel);
        panel.add(table);
        panel.add(tableBtn);
        panel.add(listLabel);
        panel.add(list);
        panel.add(textAreaLabel);
        panel.add(textArea);
        panel.add(appendBtn);
        panel.add(tabbedPane);
        panel.add(switchTab);
        panel.add(separator);
        panel.add(scrollBar);

        frame.add(menuBar);
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

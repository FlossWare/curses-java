package examples;

import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

/**
 * Example: System Monitoring Dashboard
 * Demonstrates: ProgressBar, Label, Panel, Separator, IndeterminateProgress
 */
public class SystemDashboard {
    private static boolean running = true;
    private static char[][] buffer;
    private static int terminalWidth;
    private static int terminalHeight;
    private static ProgressBar cpuBar, memBar, diskBar, netBar;
    private static Label cpuLabel, memLabel, diskLabel, netLabel;
    private static Label uptimeLabel, processLabel;
    private static IndeterminateProgress loadingBar;
    private static int updateCounter = 0;

    private static final int KEY_ESC = 27;

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

        Frame frame = new Frame("System Monitor Dashboard - Press ESC to quit");
        frame.setLocation(0, 0);
        frame.setSize(terminalWidth, terminalHeight);
        frame.setVisible(true);

        Panel mainPanel = new Panel();
        mainPanel.setLocation(1, 2);
        mainPanel.setSize(terminalWidth - 2, terminalHeight - 4);
        mainPanel.setBordered(true);

        // Title
        Label titleLabel = new Label("SYSTEM PERFORMANCE MONITOR");
        titleLabel.setLocation(3, 4);
        titleLabel.setSize(terminalWidth - 6, 1);
        titleLabel.setAlignment(Label.ALIGN_CENTER);

        Separator sep1 = new Separator(Separator.HORIZONTAL);
        sep1.setLocation(3, 5);
        sep1.setSize(terminalWidth - 6, 1);

        // CPU Panel
        Label cpuTitle = new Label("CPU Usage:");
        cpuTitle.setLocation(3, 7);
        cpuTitle.setSize(15, 1);

        cpuLabel = new Label("45%");
        cpuLabel.setLocation(terminalWidth - 10, 7);
        cpuLabel.setSize(6, 1);
        cpuLabel.setAlignment(Label.ALIGN_RIGHT);

        cpuBar = new ProgressBar();
        cpuBar.setLocation(3, 8);
        cpuBar.setSize(terminalWidth - 6, 1);
        cpuBar.setPercent(0.45);

        // Memory Panel
        Label memTitle = new Label("Memory:");
        memTitle.setLocation(3, 10);
        memTitle.setSize(15, 1);

        memLabel = new Label("68%");
        memLabel.setLocation(terminalWidth - 10, 10);
        memLabel.setSize(6, 1);
        memLabel.setAlignment(Label.ALIGN_RIGHT);

        memBar = new ProgressBar();
        memBar.setLocation(3, 11);
        memBar.setSize(terminalWidth - 6, 1);
        memBar.setPercent(0.68);

        // Disk Panel
        Label diskTitle = new Label("Disk I/O:");
        diskTitle.setLocation(3, 13);
        diskTitle.setSize(15, 1);

        diskLabel = new Label("23%");
        diskLabel.setLocation(terminalWidth - 10, 13);
        diskLabel.setSize(6, 1);
        diskLabel.setAlignment(Label.ALIGN_RIGHT);

        diskBar = new ProgressBar();
        diskBar.setLocation(3, 14);
        diskBar.setSize(terminalWidth - 6, 1);
        diskBar.setPercent(0.23);

        // Network Panel
        Label netTitle = new Label("Network:");
        netTitle.setLocation(3, 16);
        netTitle.setSize(15, 1);

        netLabel = new Label("89%");
        netLabel.setLocation(terminalWidth - 10, 16);
        netLabel.setSize(6, 1);
        netLabel.setAlignment(Label.ALIGN_RIGHT);

        netBar = new ProgressBar();
        netBar.setLocation(3, 17);
        netBar.setSize(terminalWidth - 6, 1);
        netBar.setPercent(0.89);

        Separator sep2 = new Separator(Separator.HORIZONTAL);
        sep2.setLocation(3, 19);
        sep2.setSize(terminalWidth - 6, 1);

        // System Info
        uptimeLabel = new Label("Uptime: 3 days, 5 hours, 23 minutes");
        uptimeLabel.setLocation(3, 21);
        uptimeLabel.setSize(40, 1);

        processLabel = new Label("Processes: 247 running");
        processLabel.setLocation(45, 21);
        processLabel.setSize(25, 1);

        // Loading indicator
        loadingBar = new IndeterminateProgress();
        loadingBar.setLocation(3, 23);
        loadingBar.setSize(terminalWidth - 6, 1);
        loadingBar.start();

        mainPanel.add(titleLabel);
        mainPanel.add(sep1);
        mainPanel.add(cpuTitle);
        mainPanel.add(cpuLabel);
        mainPanel.add(cpuBar);
        mainPanel.add(memTitle);
        mainPanel.add(memLabel);
        mainPanel.add(memBar);
        mainPanel.add(diskTitle);
        mainPanel.add(diskLabel);
        mainPanel.add(diskBar);
        mainPanel.add(netTitle);
        mainPanel.add(netLabel);
        mainPanel.add(netBar);
        mainPanel.add(sep2);
        mainPanel.add(uptimeLabel);
        mainPanel.add(processLabel);
        mainPanel.add(loadingBar);

        StatusBar statusBar = new StatusBar("Refreshing every 2 seconds | Last update: " + System.currentTimeMillis());
        statusBar.setLocation(1, terminalHeight - 2);
        statusBar.setSize(terminalWidth - 2, 1);

        frame.add(mainPanel);
        frame.add(statusBar);
        root.add(frame);
        root.markDirty();
    }

    private static void runEventLoop() throws Throwable {
        while (running) {
            if (RootPane.getInstance().isDirty()) {
                render();
                RootPane.getInstance().clearDirty();
            }

            // Simulate updates
            if (updateCounter++ % 200 == 0) {
                updateMetrics();
            }

            // Tick loading bar
            if (updateCounter % 10 == 0) {
                loadingBar.tick();
                RootPane.getInstance().markDirty();
            }

            int ch = NcursesBridge.getChar();
            if (ch != -1 && (ch == KEY_ESC || ch == 'q')) {
                running = false;
            }

            Thread.sleep(10);
        }
    }

    private static void updateMetrics() {
        // Simulate metric updates
        double cpu = Math.min(0.99, cpuBar.getPercent() + (Math.random() - 0.5) * 0.1);
        double mem = Math.min(0.99, memBar.getPercent() + (Math.random() - 0.5) * 0.05);
        double disk = Math.min(0.99, diskBar.getPercent() + (Math.random() - 0.5) * 0.15);
        double net = Math.min(0.99, netBar.getPercent() + (Math.random() - 0.5) * 0.2);

        cpuBar.setPercent(Math.max(0.1, cpu));
        memBar.setPercent(Math.max(0.1, mem));
        diskBar.setPercent(Math.max(0.1, disk));
        netBar.setPercent(Math.max(0.1, net));

        cpuLabel.setText(String.format("%2.0f%%", cpuBar.getPercent() * 100));
        memLabel.setText(String.format("%2.0f%%", memBar.getPercent() * 100));
        diskLabel.setText(String.format("%2.0f%%", diskBar.getPercent() * 100));
        netLabel.setText(String.format("%2.0f%%", netBar.getPercent() * 100));

        RootPane.getInstance().markDirty();
    }

    private static void render() throws Throwable {
        for (int i = 0; i < terminalHeight; i++) {
            for (int j = 0; j < terminalWidth; j++) {
                buffer[i][j] = ' ';
            }
        }

        RootPane.getInstance().paint(buffer);

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
}

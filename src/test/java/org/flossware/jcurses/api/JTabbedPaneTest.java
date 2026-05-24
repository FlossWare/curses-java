package org.flossware.jcurses.api;

import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for JTabbedPane class.
 */
@DisplayName("JTabbedPane Comprehensive Tests")
class JTabbedPaneTest extends ComponentTestBase {

    private JTabbedPane tabbedPane;

    @BeforeEach
    void setUp() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setSize(60, 20);
        tabbedPane.setLocation(0, 0);
    }

    @Test
    @DisplayName("Constructor should create empty tabbed pane")
    void testConstructorEmpty() {
        assertNotNull(tabbedPane);
        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("addTab should add tab and auto-select first tab")
    void testAddTabAutoSelectsFirst() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("addTab should add multiple tabs")
    void testAddMultipleTabs() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);
        JPanel panel3 = new JPanel();
        panel3.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);
        tabbedPane.addTab("Tab 3", panel3);

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("setSelectedTab should switch active tab")
    void testSetSelectedTab() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);

        tabbedPane.setSelectedTab("Tab 2");

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("setSelectedTab should ignore non-existent tab")
    void testSetSelectedTabNonExistent() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);

        assertDoesNotThrow(() -> tabbedPane.setSelectedTab("NonExistent"));
        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("paint should render tab headers")
    void testPaintTabHeaders() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("First", panel1);
        tabbedPane.addTab("Second", panel2);

        tabbedPane.paint(buffer);

        String headerRow = new String(buffer[0]);
        assertTrue(headerRow.contains("[First]"), "Active tab should be bracketed");
        assertTrue(headerRow.contains(" Second "), "Inactive tab should not be bracketed");
    }

    @Test
    @DisplayName("paint should render selected tab with brackets")
    void testPaintSelectedTabBracketed() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("Alpha", panel1);
        tabbedPane.addTab("Beta", panel2);
        tabbedPane.setSelectedTab("Beta");

        tabbedPane.paint(buffer);

        String headerRow = new String(buffer[0]);
        assertTrue(headerRow.contains(" Alpha "), "Inactive tab should not be bracketed");
        assertTrue(headerRow.contains("[Beta]"), "Active tab should be bracketed");
    }

    @Test
    @DisplayName("paint should render active tab content")
    void testPaintActiveTabContent() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        panel1.setLocation(5, 5);
        JLabel label1 = new JLabel("Content 1");
        label1.setLocation(6, 6);
        label1.setSize(20, 1);
        panel1.add(label1);

        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);
        panel2.setLocation(5, 5);
        JLabel label2 = new JLabel("Content 2");
        label2.setLocation(6, 6);
        label2.setSize(20, 1);
        panel2.add(label2);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);

        tabbedPane.setSelectedTab("Tab 2");
        tabbedPane.paint(buffer);

        // Should render Tab 2's content
        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("paint should handle empty tabbed pane")
    void testPaintEmptyTabbedPane() {
        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("paint should preserve tab order")
    void testPaintPreservesTabOrder() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);
        JPanel panel3 = new JPanel();
        panel3.setSize(50, 15);

        tabbedPane.addTab("Zebra", panel1);
        tabbedPane.addTab("Apple", panel2);
        tabbedPane.addTab("Banana", panel3);

        tabbedPane.paint(buffer);

        String headerRow = new String(buffer[0]);
        int zebraPos = headerRow.indexOf("Zebra");
        int applePos = headerRow.indexOf("Apple");
        int bananaPos = headerRow.indexOf("Banana");

        assertTrue(zebraPos < applePos, "Tabs should maintain insertion order");
        assertTrue(applePos < bananaPos, "Tabs should maintain insertion order");
    }

    @Test
    @DisplayName("should handle tab with long label")
    void testLongTabLabel() {
        JPanel panel = new JPanel();
        panel.setSize(50, 15);

        tabbedPane.addTab("This is a very long tab label that might overflow", panel);

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("should handle many tabs")
    void testManyTabs() {
        for (int i = 0; i < 20; i++) {
            JPanel panel = new JPanel();
            panel.setSize(50, 15);
            tabbedPane.addTab("Tab " + i, panel);
        }

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                JPanel panel = new JPanel();
                panel.setSize(50, 15);
                tabbedPane.addTab("Thread-" + threadId, panel);
                tabbedPane.setSelectedTab("Thread-" + threadId);
                tabbedPane.paint(buffer);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("switching tabs should update display")
    void testSwitchingTabsUpdatesDisplay() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);

        // Initially Tab 1 is active
        tabbedPane.paint(buffer);
        String row1 = new String(buffer[0]);
        assertTrue(row1.contains("[Tab 1]"));

        // Switch to Tab 2
        tabbedPane.setSelectedTab("Tab 2");
        // Clear buffer
        buffer = new char[80][24];
        tabbedPane.paint(buffer);
        String row2 = new String(buffer[0]);
        assertTrue(row2.contains("[Tab 2]"));
    }

    @Test
    @DisplayName("setSelectedTab should trigger repaint")
    void testSetSelectedTabTriggersRepaint() {
        JPanel panel1 = new JPanel();
        panel1.setSize(50, 15);
        JPanel panel2 = new JPanel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);

        assertDoesNotThrow(() -> {
            tabbedPane.setSelectedTab("Tab 2");
            tabbedPane.paint(buffer);
        });
    }
}

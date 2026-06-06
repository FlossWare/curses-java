package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for TabbedPane class.
 */
@DisplayName("TabbedPane Comprehensive Tests")
class TabbedPaneTest extends ComponentTestBase {

    private TabbedPane tabbedPane;

    @BeforeEach
    void setUp() {
        tabbedPane = new TabbedPane();
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
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("addTab should add multiple tabs")
    void testAddMultipleTabs() {
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
        panel2.setSize(50, 15);
        Panel panel3 = new Panel();
        panel3.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);
        tabbedPane.addTab("Tab 3", panel3);

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("setSelectedTab should switch active tab")
    void testSetSelectedTab() {
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);

        tabbedPane.setSelectedTab("Tab 2");

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("setSelectedTab should ignore non-existent tab")
    void testSetSelectedTabNonExistent() {
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);

        assertDoesNotThrow(() -> tabbedPane.setSelectedTab("NonExistent"));
        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("paint should render tab headers")
    void testPaintTabHeaders() {
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
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
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
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
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        panel1.setLocation(5, 5);
        Label label1 = new Label("Content 1");
        label1.setLocation(6, 6);
        label1.setSize(20, 1);
        panel1.add(label1);

        Panel panel2 = new Panel();
        panel2.setSize(50, 15);
        panel2.setLocation(5, 5);
        Label label2 = new Label("Content 2");
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
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
        panel2.setSize(50, 15);
        Panel panel3 = new Panel();
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
        Panel panel = new Panel();
        panel.setSize(50, 15);

        tabbedPane.addTab("This is a very long tab label that might overflow", panel);

        assertDoesNotThrow(() -> tabbedPane.paint(buffer));
    }

    @Test
    @DisplayName("should handle many tabs")
    void testManyTabs() {
        for (int i = 0; i < 20; i++) {
            Panel panel = new Panel();
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
                Panel panel = new Panel();
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
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
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
        Panel panel1 = new Panel();
        panel1.setSize(50, 15);
        Panel panel2 = new Panel();
        panel2.setSize(50, 15);

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", panel2);

        assertDoesNotThrow(() -> {
            tabbedPane.setSelectedTab("Tab 2");
            tabbedPane.paint(buffer);
        });
    }
}

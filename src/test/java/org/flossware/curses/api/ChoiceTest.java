package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for Choice class.
 */
@DisplayName("Choice Comprehensive Tests")
class ChoiceTest extends ComponentTestBase {

    private Choice choice;

    @BeforeEach
    void setUp() {
        choice = new Choice();
        choice.setSize(20, 1);
        choice.setLocation(0, 0);
    }

    @Test
    @DisplayName("Constructor should create empty choice with index 0")
    void testConstructorEmptyChoice() {
        assertNotNull(choice);
        assertEquals(0, choice.getSelectedIndex());
        assertEquals(0, choice.getItemCount());
    }

    @Test
    @DisplayName("add should add items")
    void testAddItems() {
        choice.add("Option 1");
        choice.add("Option 2");
        choice.add("Option 3");

        assertEquals(3, choice.getItemCount());
    }

    @Test
    @DisplayName("add should trigger repaint")
    void testAddTriggersRepaint() {
        choice.add("Option 1");
        assertDoesNotThrow(() -> choice.paint(buffer));
    }

    @Test
    @DisplayName("getSelectedItem should return first item initially")
    void testGetSelectedItemInitial() {
        choice.add("First");
        choice.add("Second");

        assertEquals("First", choice.getSelectedItem());
    }

    @Test
    @DisplayName("getSelectedItem should return null when empty")
    void testGetSelectedItemEmpty() {
        assertNull(choice.getSelectedItem());
    }

    @Test
    @DisplayName("selectNext should advance selection")
    void testSelectNext() {
        choice.add("Option 1");
        choice.add("Option 2");
        choice.add("Option 3");

        choice.selectNext();
        assertEquals(1, choice.getSelectedIndex());
        assertEquals("Option 2", choice.getSelectedItem());

        choice.selectNext();
        assertEquals(2, choice.getSelectedIndex());
        assertEquals("Option 3", choice.getSelectedItem());
    }

    @Test
    @DisplayName("selectNext should clamp at last item")
    void testSelectNextClamp() {
        choice.add("Option 1");
        choice.add("Option 2");

        choice.selectNext();
        choice.selectNext();
        choice.selectNext(); // Should stay at last index

        assertEquals(1, choice.getSelectedIndex());
        assertEquals("Option 2", choice.getSelectedItem());
    }

    @Test
    @DisplayName("selectPrevious should go to previous selection")
    void testSelectPrevious() {
        choice.add("Option 1");
        choice.add("Option 2");
        choice.add("Option 3");

        choice.selectNext();
        choice.selectNext();
        assertEquals(2, choice.getSelectedIndex());

        choice.selectPrevious();
        assertEquals(1, choice.getSelectedIndex());
        assertEquals("Option 2", choice.getSelectedItem());

        choice.selectPrevious();
        assertEquals(0, choice.getSelectedIndex());
        assertEquals("Option 1", choice.getSelectedItem());
    }

    @Test
    @DisplayName("selectPrevious should clamp at first item")
    void testSelectPreviousClamp() {
        choice.add("Option 1");
        choice.add("Option 2");

        choice.selectPrevious();
        choice.selectPrevious(); // Should stay at first index

        assertEquals(0, choice.getSelectedIndex());
        assertEquals("Option 1", choice.getSelectedItem());
    }

    @Test
    @DisplayName("selectNext on empty choice should not crash")
    void testSelectNextEmpty() {
        assertDoesNotThrow(() -> choice.selectNext());
        assertEquals(0, choice.getSelectedIndex());
    }

    @Test
    @DisplayName("selectPrevious on empty choice should not crash")
    void testSelectPreviousEmpty() {
        assertDoesNotThrow(() -> choice.selectPrevious());
        assertEquals(0, choice.getSelectedIndex());
    }

    @Test
    @DisplayName("paint should render empty choice")
    void testPaintEmpty() {
        choice.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("[  v ]"), "Empty choice should show brackets");
    }

    @Test
    @DisplayName("paint should render selected item")
    void testPaintSelectedItem() {
        choice.add("Apple");
        choice.add("Banana");
        choice.add("Cherry");

        choice.selectNext();
        choice.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("[ Banana v ]"), "Should display selected item");
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    choice.add("Thread-" + threadId + "-Item-" + j);
                    choice.selectNext();
                    choice.selectPrevious();
                    choice.paint(buffer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(choice.getItemCount() > 0);
    }

    @Test
    @DisplayName("cycling through options should work")
    void testCyclingThroughOptions() {
        choice.add("One");
        choice.add("Two");
        choice.add("Three");

        assertEquals("One", choice.getSelectedItem());

        choice.selectNext();
        assertEquals("Two", choice.getSelectedItem());

        choice.selectNext();
        assertEquals("Three", choice.getSelectedItem());

        choice.selectPrevious();
        assertEquals("Two", choice.getSelectedItem());

        choice.selectPrevious();
        assertEquals("One", choice.getSelectedItem());
    }

    @Test
    @DisplayName("paint should update when selection changes")
    void testPaintUpdatesOnSelectionChange() {
        choice.add("First");
        choice.add("Second");

        choice.paint(buffer);
        String row1 = new String(buffer[0]);
        assertTrue(row1.contains("First"));

        choice.selectNext();
        buffer = new char[80][24]; // Clear buffer
        choice.paint(buffer);
        String row2 = new String(buffer[0]);
        assertTrue(row2.contains("Second"));
    }
}

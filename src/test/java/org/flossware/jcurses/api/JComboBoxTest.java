package org.flossware.jcurses.api;

import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for JComboBox class.
 */
@DisplayName("JComboBox Tests")
class JComboBoxTest extends ComponentTestBase {

    private JComboBox<String> comboBox;

    @BeforeEach
    void setUp() {
        comboBox = new JComboBox<>();
        comboBox.setSize(20, 5);
        comboBox.setLocation(0, 0);
    }

    @Test
    @DisplayName("Constructor should create empty combo box with no selection")
    void testConstructorEmptyState() {
        assertNotNull(comboBox);
        assertEquals(-1, comboBox.getSelectedIndex());
        assertNull(comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("addItem should add item and auto-select first item")
    void testAddItemAutoSelectsFirst() {
        comboBox.addItem("Item 1");

        assertEquals(0, comboBox.getSelectedIndex());
        assertEquals("Item 1", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("addItem should add multiple items")
    void testAddMultipleItems() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");
        comboBox.addItem("Item 3");

        assertEquals(0, comboBox.getSelectedIndex(), "First item should remain selected");
        assertEquals("Item 1", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("addItem should trigger repaint")
    void testAddItemTriggersRepaint() {
        comboBox.addItem("Item 1");
        assertDoesNotThrow(() -> comboBox.paint(buffer));
    }

    @Test
    @DisplayName("removeItem should remove item")
    void testRemoveItem() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");

        comboBox.removeItem("Item 1");

        assertEquals("Item 2", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("removeItem should adjust selection when removing selected item")
    void testRemoveItemAdjustsSelection() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");
        comboBox.addItem("Item 3");
        comboBox.setSelectedIndex(2);

        comboBox.removeItem("Item 3");

        assertEquals(1, comboBox.getSelectedIndex(), "Selection should adjust to last valid index");
        assertEquals("Item 2", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("removeItem should not fail for non-existent item")
    void testRemoveNonExistentItem() {
        comboBox.addItem("Item 1");

        assertDoesNotThrow(() -> comboBox.removeItem("Non-existent"));
        assertEquals("Item 1", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("removeItem should handle empty combo box")
    void testRemoveFromEmptyComboBox() {
        assertDoesNotThrow(() -> comboBox.removeItem("Item"));
    }

    @Test
    @DisplayName("setSelectedIndex should set selection")
    void testSetSelectedIndex() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");
        comboBox.addItem("Item 3");

        comboBox.setSelectedIndex(1);

        assertEquals(1, comboBox.getSelectedIndex());
        assertEquals("Item 2", comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("setSelectedIndex should ignore negative index")
    void testSetSelectedIndexNegative() {
        comboBox.addItem("Item 1");
        comboBox.setSelectedIndex(0);

        comboBox.setSelectedIndex(-1);

        assertEquals(0, comboBox.getSelectedIndex(), "Selection should not change");
    }

    @Test
    @DisplayName("setSelectedIndex should ignore out of bounds index")
    void testSetSelectedIndexOutOfBounds() {
        comboBox.addItem("Item 1");
        comboBox.setSelectedIndex(0);

        comboBox.setSelectedIndex(10);

        assertEquals(0, comboBox.getSelectedIndex(), "Selection should not change");
    }

    @Test
    @DisplayName("getSelectedItem should return null when no selection")
    void testGetSelectedItemNoSelection() {
        assertNull(comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("getSelectedItem should return null when index out of bounds")
    void testGetSelectedItemOutOfBounds() {
        comboBox.addItem("Item 1");
        comboBox.removeItem("Item 1");

        assertNull(comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("setExpanded should change expanded state")
    void testSetExpanded() {
        comboBox.setExpanded(true);
        assertDoesNotThrow(() -> comboBox.paint(buffer));

        comboBox.setExpanded(false);
        assertDoesNotThrow(() -> comboBox.paint(buffer));
    }

    @Test
    @DisplayName("paint should render empty combo box")
    void testPaintEmptyComboBox() {
        comboBox.paint(buffer);

        // Check that "[ <empty> v ]" is rendered
        String row = new String(buffer[0]);
        assertTrue(row.contains("[ <empty> v ]"), "Should display empty indicator");
    }

    @Test
    @DisplayName("paint should render selected item")
    void testPaintWithSelectedItem() {
        comboBox.addItem("Test Item");
        comboBox.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("Test Item"), "Should display selected item");
        assertTrue(row.contains("["), "Should have left bracket");
        assertTrue(row.contains("v ]"), "Should have dropdown indicator");
    }

    @Test
    @DisplayName("paint should render expanded list")
    void testPaintExpandedList() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");
        comboBox.addItem("Item 3");
        comboBox.setExpanded(true);

        comboBox.paint(buffer);

        // Check main display
        String row0 = new String(buffer[0]);
        assertTrue(row0.contains("Item 1"), "Should display selected item");

        // Check expanded list
        String row1 = new String(buffer[1]);
        String row2 = new String(buffer[2]);
        String row3 = new String(buffer[3]);

        assertTrue(row1.contains("> Item 1"), "Selected item should have >");
        assertTrue(row2.contains("  Item 2"), "Non-selected should have spacing");
        assertTrue(row3.contains("  Item 3"), "Non-selected should have spacing");
    }

    @Test
    @DisplayName("paint should render collapsed list")
    void testPaintCollapsedList() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");
        comboBox.setExpanded(false);

        comboBox.paint(buffer);

        String row0 = new String(buffer[0]);
        String row1 = new String(buffer[1]);

        assertTrue(row0.contains("Item 1"), "Should display selected item");
        assertFalse(row1.contains("Item 2"), "Should not show other items when collapsed");
    }

    @Test
    @DisplayName("paint should respect height limit when expanded")
    void testPaintExpandedHeightLimit() {
        comboBox.setSize(20, 3); // Only room for 2 items in list

        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");
        comboBox.addItem("Item 3");
        comboBox.addItem("Item 4");
        comboBox.setExpanded(true);

        assertDoesNotThrow(() -> comboBox.paint(buffer));
    }

    @Test
    @DisplayName("paint should handle different item types")
    void testPaintWithDifferentTypes() {
        JComboBox<Integer> intComboBox = new JComboBox<>();
        intComboBox.setSize(20, 5);
        intComboBox.setLocation(0, 0);

        intComboBox.addItem(100);
        intComboBox.addItem(200);
        intComboBox.addItem(300);

        assertDoesNotThrow(() -> intComboBox.paint(buffer));
    }

    @Test
    @DisplayName("should handle thread-safe operations")
    void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    comboBox.addItem("Thread-" + threadId + "-Item-" + j);
                    comboBox.setSelectedIndex(j % 5);
                    comboBox.paint(buffer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertNotNull(comboBox.getSelectedItem());
    }

    @Test
    @DisplayName("removeItem should handle removing all items")
    void testRemoveAllItems() {
        comboBox.addItem("Item 1");
        comboBox.addItem("Item 2");

        comboBox.removeItem("Item 1");
        comboBox.removeItem("Item 2");

        assertEquals(-1, comboBox.getSelectedIndex());
        assertNull(comboBox.getSelectedItem());

        assertDoesNotThrow(() -> comboBox.paint(buffer));
    }

    @Test
    @DisplayName("paint should handle null items gracefully")
    void testPaintWithNullItem() {
        comboBox.addItem(null);
        comboBox.addItem("Item 2");

        assertDoesNotThrow(() -> comboBox.paint(buffer));
    }

    @Test
    @DisplayName("should work with custom objects")
    void testWithCustomObjects() {
        record Person(String name, int age) {
            @Override
            public String toString() {
                return name + " (" + age + ")";
            }
        }

        JComboBox<Person> personCombo = new JComboBox<>();
        personCombo.setSize(30, 5);
        personCombo.setLocation(0, 0);

        personCombo.addItem(new Person("Alice", 30));
        personCombo.addItem(new Person("Bob", 25));

        assertEquals(0, personCombo.getSelectedIndex());
        assertNotNull(personCombo.getSelectedItem());
        assertEquals("Alice", personCombo.getSelectedItem().name());

        assertDoesNotThrow(() -> personCombo.paint(buffer));
    }
}

package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SequencedSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for ListComponent class.
 */
@DisplayName("ListComponent Comprehensive Tests")
class JListTest extends ComponentTestBase {

    private ListComponent list;

    @BeforeEach
    void setUp() {
        list = new ListComponent();
        list.setSize(30, 10);
        list.setLocation(0, 0);
    }

    @Test
    @DisplayName("Constructor should create empty list")
    void testConstructorEmptyList() {
        assertNotNull(list);
        assertTrue(list.getItems().isEmpty());
        assertTrue(list.getSelectedIndices().isEmpty());
    }

    @Test
    @DisplayName("addItem should add items to list")
    void testAddItem() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");

        List<String> items = list.getItems();
        assertEquals(3, items.size());
        assertEquals("Item 1", items.get(0));
        assertEquals("Item 2", items.get(1));
        assertEquals("Item 3", items.get(2));
    }

    @Test
    @DisplayName("addItem should trigger repaint")
    void testAddItemTriggersRepaint() {
        list.addItem("Test");
        assertDoesNotThrow(() -> list.paint(buffer));
    }

    @Test
    @DisplayName("removeItem should remove item at index")
    void testRemoveItem() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");

        list.removeItem(1);

        List<String> items = list.getItems();
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0));
        assertEquals("Item 3", items.get(1));
    }

    @Test
    @DisplayName("removeItem should remove selection when removing selected item")
    void testRemoveItemRemovesSelection() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");
        list.select(1);

        list.removeItem(1);

        assertFalse(list.getSelectedIndices().contains(1));
    }

    @Test
    @DisplayName("removeItem should handle invalid index")
    void testRemoveItemInvalidIndex() {
        list.addItem("Item 1");

        assertDoesNotThrow(() -> list.removeItem(-1));
        assertDoesNotThrow(() -> list.removeItem(10));

        assertEquals(1, list.getItems().size());
    }

    @Test
    @DisplayName("select should select item at valid index")
    void testSelect() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");

        list.select(1);

        assertTrue(list.getSelectedIndices().contains(1));
    }

    @Test
    @DisplayName("select should support multiple selections")
    void testSelectMultiple() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");

        list.select(0);
        list.select(2);

        SequencedSet<Integer> selected = list.getSelectedIndices();
        assertEquals(2, selected.size());
        assertTrue(selected.contains(0));
        assertTrue(selected.contains(2));
    }

    @Test
    @DisplayName("select should ignore invalid index")
    void testSelectInvalidIndex() {
        list.addItem("Item 1");

        list.select(-1);
        list.select(10);

        assertTrue(list.getSelectedIndices().isEmpty());
    }

    @Test
    @DisplayName("deselect should remove selection")
    void testDeselect() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.select(0);
        list.select(1);

        list.deselect(0);

        assertFalse(list.getSelectedIndices().contains(0));
        assertTrue(list.getSelectedIndices().contains(1));
    }

    @Test
    @DisplayName("clearSelection should remove all selections")
    void testClearSelection() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");
        list.select(0);
        list.select(1);
        list.select(2);

        list.clearSelection();

        assertTrue(list.getSelectedIndices().isEmpty());
    }

    @Test
    @DisplayName("getItems should return immutable copy")
    void testGetItemsImmutable() {
        list.addItem("Item 1");

        List<String> items = list.getItems();

        assertThrows(UnsupportedOperationException.class, () -> {
            items.add("Item 2");
        });
    }

    @Test
    @DisplayName("getSelectedIndices should return copy")
    void testGetSelectedIndicesCopy() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.select(0);

        SequencedSet<Integer> selected = list.getSelectedIndices();
        selected.add(1);

        assertEquals(1, list.getSelectedIndices().size());
    }

    @Test
    @DisplayName("paint should render empty list")
    void testPaintEmptyList() {
        assertDoesNotThrow(() -> list.paint(buffer));
    }

    @Test
    @DisplayName("paint should render items with selection indicators")
    void testPaintWithItems() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");
        list.select(1);

        list.paint(buffer);

        String row0 = new String(buffer[0]);
        String row1 = new String(buffer[1]);
        String row2 = new String(buffer[2]);

        assertTrue(row0.contains("[ ] Item 1"), "Unselected item should have [ ]");
        assertTrue(row1.contains("[X] Item 2"), "Selected item should have [X]");
        assertTrue(row2.contains("[ ] Item 3"), "Unselected item should have [ ]");
    }

    @Test
    @DisplayName("paint should respect height limit")
    void testPaintHeightLimit() {
        list.setSize(30, 3);

        for (int i = 0; i < 10; i++) {
            list.addItem("Item " + i);
        }

        assertDoesNotThrow(() -> list.paint(buffer));

        // Only first 3 items should be visible
        String row0 = new String(buffer[0]);
        String row1 = new String(buffer[1]);
        String row2 = new String(buffer[2]);
        String row3 = new String(buffer[3]);

        assertTrue(row0.contains("Item 0"));
        assertTrue(row1.contains("Item 1"));
        assertTrue(row2.contains("Item 2"));
        assertFalse(row3.contains("Item 3"), "Item beyond height should not render");
    }

    @Test
    @DisplayName("paint should handle multiple selections")
    void testPaintMultipleSelections() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");
        list.select(0);
        list.select(2);

        list.paint(buffer);

        String row0 = new String(buffer[0]);
        String row1 = new String(buffer[1]);
        String row2 = new String(buffer[2]);

        assertTrue(row0.contains("[X] Item 1"));
        assertTrue(row1.contains("[ ] Item 2"));
        assertTrue(row2.contains("[X] Item 3"));
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    list.addItem("Thread-" + threadId + "-Item-" + j);
                    list.select(j);
                    list.paint(buffer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(list.getItems().isEmpty());
    }

    @Test
    @DisplayName("selection order should be preserved")
    void testSelectionOrder() {
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");

        list.select(2);
        list.select(0);
        list.select(1);

        SequencedSet<Integer> selected = list.getSelectedIndices();
        var iterator = selected.iterator();
        assertEquals(2, iterator.next());
        assertEquals(0, iterator.next());
        assertEquals(1, iterator.next());
    }

    @Test
    @DisplayName("should handle selecting same index multiple times")
    void testSelectSameIndexMultipleTimes() {
        list.addItem("Item 1");
        list.select(0);
        list.select(0);
        list.select(0);

        assertEquals(1, list.getSelectedIndices().size());
    }
}

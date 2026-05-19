package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JTable;
import org.flossware.jcurses.events.MouseEvent;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JTable Tests")
class JTableTest extends ComponentTestBase {
    private JTable table;

    @BeforeEach
    void setUp() {
        table = new JTable();
        table.setSize(60, 10);
        table.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(table);
    }

    @Test
    @DisplayName("should render without errors")
    void testRendering() {
        assertDoesNotThrow(() -> table.paint(buffer));
    }

    @Test
    @DisplayName("should be added to parent")
    void testAddToParent() {
        root.add(table);
        assertTrue(root.getChildren().contains(table));
    }

    @Test
    @DisplayName("should set and get column names")
    void testColumnNames() {
        table.setColumnNames("Name", "Age", "City");
        assertEquals(3, table.getColumnNames().size());
        assertEquals("Name", table.getColumnNames().get(0));
        assertEquals("Age", table.getColumnNames().get(1));
        assertEquals("City", table.getColumnNames().get(2));
    }

    @Test
    @DisplayName("should add rows")
    void testAddRow() {
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");
        assertEquals(2, table.getRowCount());
    }

    @Test
    @DisplayName("should clear rows")
    void testClearRows() {
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");
        assertEquals(2, table.getRowCount());

        table.clearRows();
        assertEquals(0, table.getRowCount());
        assertTrue(table.getSelectedRows().isEmpty());
    }

    @Test
    @DisplayName("should support single row selection")
    void testSingleRowSelection() {
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");
        table.addRow("Charlie", "35", "SF");

        table.selectRow(1);
        assertTrue(table.isRowSelected(1));
        assertEquals(1, table.getSelectedRows().size());
    }

    @Test
    @DisplayName("should replace selection in single-selection mode")
    void testSingleSelectionReplacement() {
        table.setMultiSelectionEnabled(false);
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.selectRow(0);
        assertTrue(table.isRowSelected(0));

        table.selectRow(1);
        assertFalse(table.isRowSelected(0));
        assertTrue(table.isRowSelected(1));
        assertEquals(1, table.getSelectedRows().size());
    }

    @Test
    @DisplayName("should support multi-row selection")
    void testMultiRowSelection() {
        table.setMultiSelectionEnabled(true);
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");
        table.addRow("Charlie", "35", "SF");

        table.selectRow(0);
        table.selectRow(2);

        assertTrue(table.isRowSelected(0));
        assertTrue(table.isRowSelected(2));
        assertFalse(table.isRowSelected(1));
        assertEquals(2, table.getSelectedRows().size());
    }

    @Test
    @DisplayName("should toggle row selection")
    void testToggleRowSelection() {
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.toggleRowSelection(0);
        assertTrue(table.isRowSelected(0));

        table.toggleRowSelection(0);
        assertFalse(table.isRowSelected(0));
    }

    @Test
    @DisplayName("should deselect row")
    void testDeselectRow() {
        table.setMultiSelectionEnabled(true);
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.selectRow(0);
        table.selectRow(1);
        assertEquals(2, table.getSelectedRows().size());

        table.deselectRow(0);
        assertFalse(table.isRowSelected(0));
        assertTrue(table.isRowSelected(1));
        assertEquals(1, table.getSelectedRows().size());
    }

    @Test
    @DisplayName("should clear all selections")
    void testClearSelection() {
        table.setMultiSelectionEnabled(true);
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.selectRow(0);
        table.selectRow(1);
        assertEquals(2, table.getSelectedRows().size());

        table.clearSelection();
        assertEquals(0, table.getSelectedRows().size());
    }

    @Test
    @DisplayName("should sort by column ascending")
    void testSortAscending() {
        table.setColumnNames("Name", "Age");
        table.addRow("Charlie", "35");
        table.addRow("Alice", "30");
        table.addRow("Bob", "25");

        table.sortByColumn(0);
        assertEquals(JTable.SORT_ASCENDING, table.getSortDirection());
        assertEquals(0, table.getSortColumn());
    }

    @Test
    @DisplayName("should sort by column descending")
    void testSortDescending() {
        table.setColumnNames("Name", "Age");
        table.addRow("Charlie", "35");
        table.addRow("Alice", "30");
        table.addRow("Bob", "25");

        table.sortByColumn(0); // First click: ascending
        table.sortByColumn(0); // Second click: descending

        assertEquals(JTable.SORT_DESCENDING, table.getSortDirection());
        assertEquals(0, table.getSortColumn());
    }

    @Test
    @DisplayName("should reset sort on third click")
    void testSortReset() {
        table.setColumnNames("Name", "Age");
        table.addRow("Charlie", "35");
        table.addRow("Alice", "30");
        table.addRow("Bob", "25");

        table.sortByColumn(0); // Ascending
        table.sortByColumn(0); // Descending
        table.sortByColumn(0); // Reset

        assertEquals(JTable.SORT_NONE, table.getSortDirection());
        assertEquals(-1, table.getSortColumn());
    }

    @Test
    @DisplayName("should handle mouse click on column header")
    void testColumnHeaderClick() {
        table.setColumnNames("Name", "Age", "City");
        table.addRow("Alice", "30", "NYC");

        // Click on first column header (x=0, y=0 relative to table)
        MouseEvent clickEvent = new MouseEvent(0, 0, 0x4); // BUTTON1_CLICKED
        table.handleMouseEvent(clickEvent);

        assertEquals(0, table.getSortColumn());
        assertEquals(JTable.SORT_ASCENDING, table.getSortDirection());
    }

    @Test
    @DisplayName("should handle mouse click on data row")
    void testDataRowClick() {
        table.setColumnNames("Name", "Age");
        table.addRow("Alice", "30");
        table.addRow("Bob", "25");

        // Click on first data row (y=2 because header takes 2 rows)
        MouseEvent clickEvent = new MouseEvent(0, 2, 0x4); // BUTTON1_CLICKED
        table.handleMouseEvent(clickEvent);

        assertTrue(table.isRowSelected(0));
    }

    @Test
    @DisplayName("should set and get column width")
    void testColumnWidth() {
        assertEquals(15, table.getColumnWidth()); // Default

        table.setColumnWidth(20);
        assertEquals(20, table.getColumnWidth());
    }

    @Test
    @DisplayName("should enforce minimum column width")
    void testMinimumColumnWidth() {
        table.setColumnWidth(2);
        assertEquals(5, table.getColumnWidth()); // Minimum is 5
    }

    @Test
    @DisplayName("should disable multi-selection and keep only first selection")
    void testDisableMultiSelection() {
        table.setMultiSelectionEnabled(true);
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");
        table.addRow("Charlie", "35", "SF");

        table.selectRow(0);
        table.selectRow(1);
        table.selectRow(2);
        assertEquals(3, table.getSelectedRows().size());

        table.setMultiSelectionEnabled(false);
        assertEquals(1, table.getSelectedRows().size());
    }

    @Test
    @DisplayName("should check multi-selection enabled state")
    void testIsMultiSelectionEnabled() {
        assertFalse(table.isMultiSelectionEnabled()); // Default is false

        table.setMultiSelectionEnabled(true);
        assertTrue(table.isMultiSelectionEnabled());
    }

    @Test
    @DisplayName("should support deprecated getSelectedRow method")
    void testDeprecatedGetSelectedRow() {
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.selectRow(1);
        assertEquals(1, table.getSelectedRow());
    }

    @Test
    @DisplayName("should support deprecated setSelectedRow method")
    void testDeprecatedSetSelectedRow() {
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.setSelectedRow(1);
        assertTrue(table.isRowSelected(1));
    }

    @Test
    @DisplayName("should ignore invalid row selection")
    void testInvalidRowSelection() {
        table.addRow("Alice", "30", "NYC");

        table.selectRow(-1);
        assertFalse(table.isRowSelected(-1));

        table.selectRow(999);
        assertFalse(table.isRowSelected(999));
    }

    @Test
    @DisplayName("should ignore invalid column sort")
    void testInvalidColumnSort() {
        table.setColumnNames("Name", "Age");

        int beforeSortCol = table.getSortColumn();
        int beforeSortDir = table.getSortDirection();

        table.sortByColumn(-1);
        assertEquals(beforeSortCol, table.getSortColumn());
        assertEquals(beforeSortDir, table.getSortDirection());

        table.sortByColumn(999);
        assertEquals(beforeSortCol, table.getSortColumn());
        assertEquals(beforeSortDir, table.getSortDirection());
    }

    @Test
    @DisplayName("should render with selections and sort indicators")
    void testRenderingWithFeaturesEnabled() {
        table.setColumnNames("Name", "Age", "City");
        table.setMultiSelectionEnabled(true);
        table.addRow("Charlie", "35", "SF");
        table.addRow("Alice", "30", "NYC");
        table.addRow("Bob", "25", "LA");

        table.selectRow(0);
        table.selectRow(2);
        table.sortByColumn(0);

        assertDoesNotThrow(() -> table.paint(buffer));
    }
}

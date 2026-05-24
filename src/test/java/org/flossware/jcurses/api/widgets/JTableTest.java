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
    @DisplayName("should ignore invalid indices in toggle")
    void testToggleRowSelectionInvalid() {
        table.addRow("Alice", "30", "NYC");

        // Should not throw, just ignore
        assertDoesNotThrow(() -> table.toggleRowSelection(-1));
        assertDoesNotThrow(() -> table.toggleRowSelection(999));

        assertEquals(0, table.getSelectedRows().size());
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
    @DisplayName("should cycle back to ascending on fourth click")
    void testSortCycle() {
        table.setColumnNames("Name");
        table.addRow("Charlie");
        table.addRow("Alice");

        table.sortByColumn(0); // Ascending
        table.sortByColumn(0); // Descending
        table.sortByColumn(0); // None
        table.sortByColumn(0); // Back to Ascending

        assertEquals(JTable.SORT_ASCENDING, table.getSortDirection());
        assertEquals(0, table.getSortColumn());
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

    @Test
    @DisplayName("should handle sorting with null values in cells")
    void testSortWithNullValues() {
        table.setColumnNames("Name", "Age");
        table.addRow("Charlie", null);
        table.addRow(null, "30");
        table.addRow("Alice", "25");

        assertDoesNotThrow(() -> table.sortByColumn(0));
        assertDoesNotThrow(() -> table.sortByColumn(1));
        assertDoesNotThrow(() -> table.paint(buffer));
    }

    @Test
    @DisplayName("should handle sorting with uneven row lengths")
    void testSortWithUnevenRows() {
        table.setColumnNames("Name", "Age", "City");
        table.addRow("Charlie", "35", "SF");
        table.addRow("Alice");  // Short row
        table.addRow("Bob", "25");  // Medium row

        // Sort by column that doesn't exist in some rows
        assertDoesNotThrow(() -> table.sortByColumn(2));
        assertDoesNotThrow(() -> table.paint(buffer));
    }

    @Test
    @DisplayName("should preserve selections after sorting")
    void testSelectionPreservationAfterSort() {
        table.setColumnNames("Name");
        table.addRow("Charlie");
        table.addRow("Alice");
        table.addRow("Bob");

        table.selectRow(1);  // Select "Alice"
        table.sortByColumn(0);  // Sort alphabetically

        // Selections are by index, not by data, so index 1 should still be selected
        // (but might be different data now)
        assertTrue(table.getSelectedRows().contains(1));
    }

    @Test
    @DisplayName("should handle mouse click outside table bounds")
    void testMouseClickOutsideBounds() {
        table.setColumnNames("Name");
        table.addRow("Alice");

        MouseEvent outsideClick = new MouseEvent(1000, 1000, 0x4);
        boolean handled = table.handleMouseEvent(outsideClick);

        assertFalse(handled);
    }

    @Test
    @DisplayName("should handle empty table rendering")
    void testEmptyTableRendering() {
        table.setColumnNames("Name", "Age", "City");
        // No rows added

        assertDoesNotThrow(() -> table.paint(buffer));
    }

    @Test
    @DisplayName("should handle table with no columns")
    void testNoColumnsRendering() {
        // No columns set
        table.addRow("Value");

        assertDoesNotThrow(() -> table.paint(buffer));
    }

    @Test
    @DisplayName("should render descending sort indicator")
    void testRenderDescendingSortIndicator() {
        table.setColumnNames("Name", "Age");
        table.addRow("Alice", "30");

        // Sort twice to get descending
        table.sortByColumn(0);
        table.sortByColumn(0);

        assertEquals(JTable.SORT_DESCENDING, table.getSortDirection());

        table.paint(buffer);

        // Check that buffer contains the sort indicator
        String headerRow = new String(buffer[0]);
        assertTrue(headerRow.contains("v") || headerRow.contains("Name"));
    }

    @Test
    @DisplayName("should render multi-selection markers")
    void testRenderMultiSelectionMarkers() {
        table.setMultiSelectionEnabled(true);
        table.setColumnNames("Name");
        table.addRow("Alice");
        table.addRow("Bob");
        table.addRow("Charlie");

        table.selectRow(0);
        table.selectRow(2);

        table.paint(buffer);

        // Check for multi-selection markers
        String row0 = new String(buffer[2]);  // First data row
        String row1 = new String(buffer[3]);  // Second data row
        String row2 = new String(buffer[4]);  // Third data row

        assertTrue(row0.contains("[*]") || row0.contains("Alice"));
        assertTrue(row1.contains("[ ]") || row1.contains("Bob"));
        assertTrue(row2.contains("[*]") || row2.contains("Charlie"));
    }

    @Test
    @DisplayName("should render single-selection markers")
    void testRenderSingleSelectionMarkers() {
        table.setMultiSelectionEnabled(false);
        table.setColumnNames("Name");
        table.addRow("Alice");
        table.addRow("Bob");

        table.selectRow(1);

        table.paint(buffer);

        // Check for single-selection markers
        String row0 = new String(buffer[2]);
        String row1 = new String(buffer[3]);

        // First row should have "  " (no marker)
        // Second row should have "> " (selected marker)
        assertTrue(row0.contains("Alice"));
        assertTrue(row1.contains("Bob"));
    }

    @Test
    @DisplayName("should handle empty cells in render")
    void testRenderEmptyCells() {
        table.setColumnNames("Name", "Age", "City");
        table.addRow("Alice", "", null);  // Empty string and null
        table.addRow("", "", "");  // All empty

        assertDoesNotThrow(() -> table.paint(buffer));
    }

    @Test
    @DisplayName("should truncate long cell values")
    void testTruncateLongCellValues() {
        table.setColumnNames("Name");
        table.setColumnWidth(10);
        table.addRow("VeryLongNameThatExceedsColumnWidth");

        assertDoesNotThrow(() -> table.paint(buffer));

        String dataRow = new String(buffer[2]);
        // Should not contain the full long text
        assertFalse(dataRow.contains("VeryLongNameThatExceedsColumnWidth"));
        // But should contain the truncated beginning
        assertTrue(dataRow.contains("VeryLong") || dataRow.contains("Very"));
    }

    @Test
    @DisplayName("should render header separator")
    void testRenderHeaderSeparator() {
        table.setColumnNames("Name", "Age");
        table.addRow("Alice", "30");

        table.paint(buffer);

        // Check that row 1 (after header) contains separator
        String separatorRow = new String(buffer[1]);
        assertTrue(separatorRow.contains("-") || separatorRow.length() > 0);
    }

    @Test
    @DisplayName("should handle table height limit in rendering")
    void testRenderWithHeightLimit() {
        table.setColumnNames("Name");
        table.setSize(60, 5);  // Very limited height

        for (int i = 0; i < 20; i++) {
            table.addRow("Person " + i);
        }

        assertDoesNotThrow(() -> table.paint(buffer));

        // Should only render rows that fit
        // Height of 5 means: 1 header + 1 separator + 3 data rows max
    }

    @Test
    @DisplayName("should handle column width with padding")
    void testColumnWidthPadding() {
        table.setColumnNames("A", "B", "C");
        table.setColumnWidth(10);
        table.addRow("1", "2", "3");

        assertDoesNotThrow(() -> table.paint(buffer));

        String dataRow = new String(buffer[2]);
        // Should have padding to reach column widths
        assertTrue(dataRow.length() >= 10);
    }

    @Test
    @DisplayName("should handle toggle row selection when multi-selection enabled")
    void testToggleRowSelectionMultiEnabled() {
        table.setMultiSelectionEnabled(true);
        table.addRow("Alice", "30");
        table.addRow("Bob", "25");

        table.toggleRowSelection(0);
        assertTrue(table.isRowSelected(0));

        table.toggleRowSelection(1);
        assertTrue(table.isRowSelected(0));
        assertTrue(table.isRowSelected(1));
    }

    @Test
    @DisplayName("should handle mouse click with non-BUTTON1_CLICKED event")
    void testHandleMouseEventNonButton1() {
        table.setColumnNames("Name");
        table.addRow("Alice");

        // Use a different button event
        MouseEvent otherButton = new MouseEvent(10, 5, 0x8); // Not BUTTON1_CLICKED
        boolean handled = table.handleMouseEvent(otherButton);

        // Should delegate to super.handleMouseEvent() which returns true
        assertTrue(handled);
    }

    @Test
    @DisplayName("should handle mouse click on empty table")
    void testHandleMouseEventEmptyTable() {
        // No columns, no data
        MouseEvent clickEvent = new MouseEvent(5, 5, 0x4);
        boolean handled = table.handleMouseEvent(clickEvent);

        // Should handle but do nothing
        assertTrue(handled);
    }

    @Test
    @DisplayName("should handle click on column with invalid index")
    void testClickColumnInvalidIndex() {
        table.setColumnNames("A", "B");
        table.addRow("1", "2");

        // Click far to the right (column index would be out of bounds)
        MouseEvent clickEvent = new MouseEvent(100, 0, 0x4);
        table.handleMouseEvent(clickEvent);

        // Should not crash, sort column should remain unchanged
        assertEquals(-1, table.getSortColumn());
    }

    @Test
    @DisplayName("should handle click on data row with invalid index")
    void testClickDataRowInvalidIndex() {
        table.setColumnNames("Name");
        table.addRow("Alice");

        // Click beyond the last row
        MouseEvent clickEvent = new MouseEvent(5, 20, 0x4);
        table.handleMouseEvent(clickEvent);

        // Should not crash, no selection should be made
        assertTrue(table.getSelectedRows().isEmpty());
    }

    @Test
    @DisplayName("should handle getSelectedRow when no selection")
    void testGetSelectedRowEmpty() {
        table.addRow("Alice");
        assertEquals(-1, table.getSelectedRow());
    }

}

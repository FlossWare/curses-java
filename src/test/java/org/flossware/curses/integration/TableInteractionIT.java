package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for table component interactions.
 */
@DisplayName("Table Interaction Integration Tests")
class TableInteractionIT extends IntegrationTestBase {

    @Test
    @DisplayName("table should display rows and columns")
    void testTableBasicRendering() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name", "Status");

        table.addRow("001", "Task A", "Done");
        table.addRow("002", "Task B", "Active");
        table.addRow("003", "Task C", "Pending");

        setupFrame(table);
        runEventLoopCycle();

        // Verify table has data
        assertEquals(3, table.getRowCount());
        assertEquals(3, table.getColumnNames().size());
    }

    @Test
    @DisplayName("table should sort by column")
    void testTableSorting() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name", "Priority");

        table.addRow("003", "Task C", "Low");
        table.addRow("001", "Task A", "High");
        table.addRow("002", "Task B", "Medium");

        setupFrame(table);
        runEventLoopCycle();

        // Sort by ID (column 0)
        table.sortByColumn(0);
        root.markDirty();
        runEventLoopCycle();

        // Verify sort direction changed
        assertEquals(0, table.getSortColumn());
        assertEquals(Table.SORT_ASCENDING, table.getSortDirection());

        // Sort by Name (column 1)
        table.sortByColumn(1);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(1, table.getSortColumn());
    }

    @Test
    @DisplayName("table should handle row selection")
    void testTableRowSelection() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name");
        table.setMultiSelectionEnabled(false);

        table.addRow("001", "Task A");
        table.addRow("002", "Task B");
        table.addRow("003", "Task C");

        setupFrame(table);
        runEventLoopCycle();

        // Select row 0
        table.selectRow(0);
        root.markDirty();
        runEventLoopCycle();

        assertTrue(table.isRowSelected(0));
        assertFalse(table.isRowSelected(1));

        // Select row 1 (should deselect row 0 in single-selection mode)
        table.selectRow(1);
        root.markDirty();
        runEventLoopCycle();

        assertFalse(table.isRowSelected(0));
        assertTrue(table.isRowSelected(1));
    }

    @Test
    @DisplayName("table should support multi-selection")
    void testTableMultiSelection() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name");
        table.setMultiSelectionEnabled(true);

        table.addRow("001", "Task A");
        table.addRow("002", "Task B");
        table.addRow("003", "Task C");

        setupFrame(table);
        runEventLoopCycle();

        // Select multiple rows
        table.selectRow(0);
        table.selectRow(2);
        root.markDirty();
        runEventLoopCycle();

        assertTrue(table.isRowSelected(0));
        assertFalse(table.isRowSelected(1));
        assertTrue(table.isRowSelected(2));

        var selectedRows = table.getSelectedRows();
        assertEquals(2, selectedRows.size());
        assertTrue(selectedRows.contains(0));
        assertTrue(selectedRows.contains(2));
    }

    @Test
    @DisplayName("table should add rows dynamically")
    void testTableAddRows() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name");

        setupFrame(table);
        runEventLoopCycle();

        // Initial empty
        assertEquals(0, table.getRowCount());

        // Add rows
        table.addRow("001", "Task A");
        root.markDirty();
        runEventLoopCycle();

        assertEquals(1, table.getRowCount());

        table.addRow("002", "Task B");
        table.addRow("003", "Task C");
        root.markDirty();
        runEventLoopCycle();

        assertEquals(3, table.getRowCount());
    }

    @Test
    @DisplayName("table should clear all rows")
    void testTableClearRows() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name");

        table.addRow("001", "Task A");
        table.addRow("002", "Task B");
        table.addRow("003", "Task C");

        setupFrame(table);
        runEventLoopCycle();

        assertEquals(3, table.getRowCount());

        // Clear all rows
        table.clearRows();
        root.markDirty();
        runEventLoopCycle();

        assertEquals(0, table.getRowCount());
    }

    @Test
    @DisplayName("table should clear selection")
    void testTableClearSelection() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name");
        table.setMultiSelectionEnabled(true);

        table.addRow("001", "Task A");
        table.addRow("002", "Task B");

        setupFrame(table);
        runEventLoopCycle();

        // Select rows
        table.selectRow(0);
        table.selectRow(1);
        root.markDirty();
        runEventLoopCycle();

        assertTrue(table.isRowSelected(0));
        assertTrue(table.isRowSelected(1));

        // Clear selection
        table.clearSelection();
        root.markDirty();
        runEventLoopCycle();

        assertFalse(table.isRowSelected(0));
        assertFalse(table.isRowSelected(1));
        assertTrue(table.getSelectedRows().isEmpty());
    }

    @Test
    @DisplayName("table should store row data")
    void testTableDataStorage() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Name", "Status");

        table.addRow("001", "Task A", "Done");
        table.addRow("002", "Task B", "Active");

        setupFrame(table);
        runEventLoopCycle();

        // Verify data stored
        assertEquals(2, table.getRowCount());
        assertEquals(3, table.getColumnNames().size());
    }

    @Test
    @DisplayName("table workflow: add, select, sort, clear")
    void testTableComplexWorkflow() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 10);
        table.setColumnNames("ID", "Task", "Priority", "Status");
        table.setMultiSelectionEnabled(true);

        setupFrame(table);
        runEventLoopCycle();

        // Step 1: Add rows
        table.addRow("003", "Write docs", "Low", "Done");
        table.addRow("001", "Fix bug", "High", "Active");
        table.addRow("002", "Add feature", "Medium", "Pending");
        root.markDirty();
        runEventLoopCycle();

        assertEquals(3, table.getRowCount());

        // Step 2: Sort by priority
        table.sortByColumn(2);
        root.markDirty();
        runEventLoopCycle();

        // Verify sorted by priority
        assertEquals(2, table.getSortColumn());

        // Step 3: Select high priority tasks
        table.selectRow(0);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(1, table.getSelectedRows().size());

        // Step 4: Clear selection
        table.clearSelection();
        root.markDirty();
        runEventLoopCycle();

        assertTrue(table.getSelectedRows().isEmpty());

        // Step 5: Sort by ID
        table.sortByColumn(0);
        root.markDirty();
        runEventLoopCycle();

        assertEquals(0, table.getSortColumn());
    }
}

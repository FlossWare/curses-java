package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.flossware.curses.api.Constants.*;

public class Table extends Component {
    public static final int SORT_NONE = 0;
    public static final int SORT_ASCENDING = 1;
    public static final int SORT_DESCENDING = 2;

    private final List<String> columnNames = new ArrayList<>();
    private final List<List<String>> data = new ArrayList<>();
    private final List<List<String>> originalData = new ArrayList<>();
    private final Set<Integer> selectedRows = new HashSet<>();

    private int sortColumn = NO_INDEX;
    private int sortDirection = SORT_NONE;
    private boolean multiSelectionEnabled = false;
    private int columnWidth = 15;

    /**
     * Set column names for the table.
     */
    public void setColumnNames(String... names) {
        renderLock.lock();
        try {
            columnNames.clear();
            Collections.addAll(columnNames, names);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get column names.
     */
    public List<String> getColumnNames() {
        renderLock.lock();
        try {
            return new ArrayList<>(columnNames);
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Add a data row to the table.
     */
    public void addRow(String... values) {
        renderLock.lock();
        try {
            List<String> row = new ArrayList<>();
            Collections.addAll(row, values);
            data.add(row);
            originalData.add(new ArrayList<>(row));
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Clear all rows from the table.
     */
    public void clearRows() {
        renderLock.lock();
        try {
            data.clear();
            originalData.clear();
            selectedRows.clear();
            sortColumn = NO_INDEX;
            sortDirection = SORT_NONE;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get the number of rows in the table.
     */
    public int getRowCount() {
        renderLock.lock();
        try {
            return data.size();
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Enable or disable multi-row selection.
     */
    public void setMultiSelectionEnabled(boolean enabled) {
        renderLock.lock();
        try {
            this.multiSelectionEnabled = enabled;
            if (!enabled && selectedRows.size() > 1) {
                // Keep only the first selected row
                Integer first = selectedRows.iterator().next();
                selectedRows.clear();
                selectedRows.add(first);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Check if multi-selection is enabled.
     */
    public boolean isMultiSelectionEnabled() {
        return multiSelectionEnabled;
    }

    /**
     * Select a row. If multi-selection is enabled, adds to selection.
     * Otherwise replaces current selection.
     */
    public void selectRow(int row) {
        renderLock.lock();
        try {
            if (row < 0 || row >= data.size()) {
                return;
            }
            if (!multiSelectionEnabled) {
                selectedRows.clear();
            }
            selectedRows.add(row);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Deselect a row.
     */
    public void deselectRow(int row) {
        renderLock.lock();
        try {
            selectedRows.remove(row);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Toggle row selection.
     */
    public void toggleRowSelection(int row) {
        renderLock.lock();
        try {
            if (row < 0 || row >= data.size()) {
                return;
            }
            if (selectedRows.contains(row)) {
                selectedRows.remove(row);
            } else {
                if (!multiSelectionEnabled) {
                    selectedRows.clear();
                }
                selectedRows.add(row);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Clear all selections.
     */
    public void clearSelection() {
        renderLock.lock();
        try {
            selectedRows.clear();
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get selected row indices.
     */
    public Set<Integer> getSelectedRows() {
        renderLock.lock();
        try {
            return new HashSet<>(selectedRows);
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Check if a row is selected.
     */
    public boolean isRowSelected(int row) {
        renderLock.lock();
        try {
            return selectedRows.contains(row);
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Set column width for rendering.
     */
    public void setColumnWidth(int width) {
        renderLock.lock();
        try {
            this.columnWidth = Math.max(5, width);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Get column width.
     */
    public int getColumnWidth() {
        return columnWidth;
    }

    /**
     * Sort by column.
     */
    public void sortByColumn(int columnIndex) {
        renderLock.lock();
        try {
            if (columnIndex < 0 || columnIndex >= columnNames.size()) {
                return;
            }

            // Toggle sort direction
            if (sortColumn == columnIndex) {
                if (sortDirection == SORT_ASCENDING) {
                    sortDirection = SORT_DESCENDING;
                } else {
                    // Must be SORT_DESCENDING, toggle to SORT_NONE and reset column
                    sortDirection = SORT_NONE;
                    sortColumn = NO_INDEX;
                }
            } else {
                sortColumn = columnIndex;
                sortDirection = SORT_ASCENDING;
            }

            applySorting();
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    private void applySorting() {
        if (sortColumn == -1 || sortDirection == SORT_NONE) {
            // Restore original order
            data.clear();
            for (List<String> row : originalData) {
                data.add(new ArrayList<>(row));
            }
            return;
        }

        final int col = sortColumn;
        final boolean ascending = sortDirection == SORT_ASCENDING;

        data.sort(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> row1, List<String> row2) {
                String val1 = col < row1.size() ? row1.get(col) : "";
                String val2 = col < row2.size() ? row2.get(col) : "";

                if (val1 == null) val1 = "";
                if (val2 == null) val2 = "";

                int cmp = val1.compareTo(val2);
                return ascending ? cmp : -cmp;
            }
        });
    }

    /**
     * Get current sort column.
     */
    public int getSortColumn() {
        return sortColumn;
    }

    /**
     * Get current sort direction.
     */
    public int getSortDirection() {
        return sortDirection;
    }

    @Override
    public boolean handleMouseEvent(MouseEvent event) {
        if (event.button() == 0x4) { // BUTTON1_CLICKED
            int relativeX = event.x() - getX();
            int relativeY = event.y() - getY();

            // Check if clicked on header row
            if (relativeY == 0 && !columnNames.isEmpty()) {
                // Determine which column was clicked
                int clickedColumn = relativeX / columnWidth;
                if (clickedColumn >= 0 && clickedColumn < columnNames.size()) {
                    sortByColumn(clickedColumn);
                    return true;
                }
            }

            // Check if clicked on data row
            int headerOffset = columnNames.isEmpty() ? 0 : 2;
            int dataRow = relativeY - headerOffset;
            if (dataRow >= 0 && dataRow < data.size()) {
                toggleRowSelection(dataRow);
                return true;
            }
        }
        return super.handleMouseEvent(event);
    }

    /**
     * Legacy method for backward compatibility.
     *
     * @deprecated Since 1.18. Use {@link #selectRow(int)} instead for single selection,
     *             or {@link #selectRow(int, boolean)} with multiselect enabled.
     *             This method will be removed in version 2.0.
     */
    @Deprecated(since = "1.18", forRemoval = true)
    public void setSelectedRow(int row) {
        selectRow(row);
    }

    /**
     * Legacy method for backward compatibility.
     * Returns the first selected row or -1 if none selected.
     *
     * @return the first selected row index, or -1 if no row is selected
     * @deprecated Since 1.18. Use {@link #getSelectedRows()} instead which returns
     *             all selected rows as a Set, supporting multi-row selection.
     *             This method will be removed in version 2.0.
     */
    @Deprecated(since = "1.18", forRemoval = true)
    public int getSelectedRow() {
        renderLock.lock();
        try {
            return selectedRows.isEmpty() ? NO_INDEX : selectedRows.iterator().next();
        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Efficiently pad a string to the specified width.
     * Avoids String.format overhead by manually padding.
     */
    private void appendPadded(StringBuilder sb, String text, int width) {
        int len = Math.min(text.length(), width);
        sb.append(text, 0, len);
        for (int i = len; i < width; i++) {
            sb.append(' ');
        }
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int currentY = getY();
            int startX = getX();

            // Render column headers with sort indicators
            if (!columnNames.isEmpty()) {
                StringBuilder header = new StringBuilder(width);
                for (int i = 0; i < columnNames.size(); i++) {
                    String col = columnNames.get(i);
                    StringBuilder columnText = new StringBuilder(columnWidth);
                    columnText.append(col);

                    if (i == sortColumn) {
                        columnText.append(sortDirection == SORT_ASCENDING ? " ^" :
                                         sortDirection == SORT_DESCENDING ? " v" : "");
                    }

                    int maxLen = Math.min(columnText.length(), columnWidth - 1);
                    appendPadded(header, columnText.substring(0, maxLen), columnWidth);
                }

                int headerLen = Math.min(header.length(), width);
                writeStringToBuffer(buffer, header.substring(0, headerLen), startX, currentY);
                currentY++;

                // Render header separator - batch write instead of loop
                char[] separator = new char[headerLen];
                for (int i = 0; i < separator.length; i++) {
                    separator[i] = '-';
                }
                writeStringToBuffer(buffer, new String(separator), startX, currentY);
                currentY++;
            }

            // Render data rows with selection markers
            for (int i = 0; i < data.size() && currentY < getY() + height; i++) {
                List<String> row = data.get(i);
                StringBuilder rowStr = new StringBuilder(width);

                // Add selection marker
                rowStr.append(selectedRows.contains(i) ?
                             (multiSelectionEnabled ? "[*] " : "> ") :
                             (multiSelectionEnabled ? "[ ] " : "  "));

                // Render cells
                for (int j = 0; j < row.size(); j++) {
                    String cell = row.get(j);
                    String cellStr = (cell == null || cell.isEmpty()) ? "" : cell;
                    int maxLen = Math.min(cellStr.length(), columnWidth - 1);
                    appendPadded(rowStr, cellStr.substring(0, maxLen), columnWidth);
                }

                int rowLen = Math.min(rowStr.length(), width);
                writeStringToBuffer(buffer, rowStr.substring(0, rowLen), startX, currentY);
                currentY++;
            }
        } finally {
            renderLock.unlock();
        }
    }
}

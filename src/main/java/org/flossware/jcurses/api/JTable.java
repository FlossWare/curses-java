package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JTable extends Component {
    public static final int SORT_NONE = 0;
    public static final int SORT_ASCENDING = 1;
    public static final int SORT_DESCENDING = 2;

    private final List<String> columnNames = new ArrayList<>();
    private final List<List<String>> data = new ArrayList<>();
    private final List<List<String>> originalData = new ArrayList<>();
    private final Set<Integer> selectedRows = new HashSet<>();

    private int sortColumn = -1;
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
            sortColumn = -1;
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
                } else if (sortDirection == SORT_DESCENDING) {
                    sortDirection = SORT_NONE;
                    sortColumn = -1;
                } else {
                    sortDirection = SORT_ASCENDING;
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
     * @deprecated Use selectRow() instead
     */
    @Deprecated
    public void setSelectedRow(int row) {
        selectRow(row);
    }

    /**
     * Legacy method for backward compatibility.
     * @deprecated Use getSelectedRows() instead
     */
    @Deprecated
    public int getSelectedRow() {
        renderLock.lock();
        try {
            return selectedRows.isEmpty() ? -1 : selectedRows.iterator().next();
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int currentY = getY();

            // Render column headers with sort indicators
            if (!columnNames.isEmpty()) {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < columnNames.size(); i++) {
                    String col = columnNames.get(i);
                    String sortIndicator = "";
                    if (i == sortColumn) {
                        sortIndicator = sortDirection == SORT_ASCENDING ? " ^" :
                                       sortDirection == SORT_DESCENDING ? " v" : "";
                    }

                    String columnText = col + sortIndicator;
                    int maxLen = columnWidth - 1;
                    columnText = columnText.substring(0, Math.min(columnText.length(), maxLen));
                    header.append(String.format("%-" + columnWidth + "s", columnText));
                }
                writeStringToBuffer(buffer, header.toString().substring(0, Math.min(header.length(), width)),
                                   getX(), currentY);
                currentY++;

                // Render header separator
                for (int i = 0; i < Math.min(width, header.length()); i++) {
                    writeStringToBuffer(buffer, "-", getX() + i, currentY);
                }
                currentY++;
            }

            // Render data rows with selection markers
            for (int i = 0; i < data.size() && currentY < getY() + height; i++) {
                List<String> row = data.get(i);
                StringBuilder rowStr = new StringBuilder();

                // Add selection marker
                if (selectedRows.contains(i)) {
                    rowStr.append(multiSelectionEnabled ? "[*] " : "> ");
                } else {
                    rowStr.append(multiSelectionEnabled ? "[ ] " : "  ");
                }

                // Render cells
                for (int j = 0; j < row.size(); j++) {
                    String cell = row.get(j);
                    String cellStr = cell == null ? "" : cell;
                    int maxLen = columnWidth - 1;
                    cellStr = cellStr.substring(0, Math.min(cellStr.length(), maxLen));
                    rowStr.append(String.format("%-" + columnWidth + "s", cellStr));
                }

                writeStringToBuffer(buffer, rowStr.toString().substring(0, Math.min(rowStr.length(), width)),
                                   getX(), currentY);
                currentY++;
            }
        } finally {
            renderLock.unlock();
        }
    }
}

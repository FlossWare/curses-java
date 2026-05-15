package org.flossware.jcurses.api;

import java.util.ArrayList;
import java.util.List;

public class JTable extends Component {
    private final List<String> columnNames = new ArrayList<>();
    private final List<List<String>> data = new ArrayList<>();
    private int selectedRow = -1;

    public void setColumnNames(String... names) {
        renderLock.lock();
        try {
            columnNames.clear();
            for (String name : names) {
                columnNames.add(name);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void addRow(String... values) {
        renderLock.lock();
        try {
            List<String> row = new ArrayList<>();
            for (String value : values) {
                row.add(value);
            }
            data.add(row);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void clearRows() {
        renderLock.lock();
        try {
            data.clear();
            selectedRow = -1;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void setSelectedRow(int row) {
        renderLock.lock();
        try {
            this.selectedRow = Math.clamp(row, -1, data.size() - 1);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int currentY = getY();

            if (!columnNames.isEmpty()) {
                StringBuilder header = new StringBuilder();
                for (String col : columnNames) {
                    header.append(String.format("%-15s", col.substring(0, Math.min(col.length(), 15))));
                }
                writeStringToBuffer(buffer, header.toString(), getX(), currentY);
                currentY++;

                for (int i = 0; i < Math.min(width, header.length()); i++) {
                    writeStringToBuffer(buffer, "-", getX() + i, currentY);
                }
                currentY++;
            }

            for (int i = 0; i < data.size() && currentY < getY() + height; i++) {
                List<String> row = data.get(i);
                StringBuilder rowStr = new StringBuilder();
                if (i == selectedRow) {
                    rowStr.append("> ");
                } else {
                    rowStr.append("  ");
                }

                for (String cell : row) {
                    String cellStr = cell == null ? "" : cell;
                    rowStr.append(String.format("%-15s", cellStr.substring(0, Math.min(cellStr.length(), 15))));
                }

                writeStringToBuffer(buffer, rowStr.toString(), getX(), currentY);
                currentY++;
            }
        } finally {
            renderLock.unlock();
        }
    }
}

package org.flossware.curses.api;

import org.flossware.curses.api.Component;
import org.flossware.curses.api.Container;
import java.util.SequencedCollection;

public class GridLayout implements LayoutManager {
    private final int rows;
    private final int cols;

    public GridLayout(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        // GridLayout doesn't use constraints - components are laid out in order
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // GridLayout doesn't maintain component-specific state
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        SequencedCollection<Component> children = parent.getChildren();
        if (children.isEmpty() || rows <= 0 || cols <= 0) {
            return new Dimension(0, 0);
        }

        int maxCellWidth = 0;
        int maxCellHeight = 0;

        // Find the maximum preferred size among all children
        for (Component child : children) {
            Dimension d = child.getPreferredSize();
            maxCellWidth = Math.max(maxCellWidth, d.width());
            maxCellHeight = Math.max(maxCellHeight, d.height());
        }

        // Calculate total size based on grid dimensions
        int totalWidth = maxCellWidth * cols;
        int totalHeight = maxCellHeight * rows;

        return new Dimension(totalWidth, totalHeight);
    }

    @Override
    public void layoutContainer(Container parent) {
        SequencedCollection<Component> children = parent.getChildren();
        if (children.isEmpty() || rows <= 0 || cols <= 0) {
            return;
        }

        int cellWidth = parent.getWidth() / cols;
        int cellHeight = parent.getHeight() / rows;

        int index = 0;
        for (Component child : children) {
            int row = index / cols;
            int col = index % cols;

            child.setLocation(parent.getX() + col * cellWidth, parent.getY() + row * cellHeight);
            child.setSize(cellWidth, cellHeight);

            index++;
            if (index >= rows * cols) {
                break;
            }
        }
    }
}
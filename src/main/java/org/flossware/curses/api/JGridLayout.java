package org.flossware.curses.api;

import org.flossware.curses.api.Component;
import org.flossware.curses.api.Container;
import java.util.SequencedCollection;

public class JGridLayout implements LayoutManager {
    private final int rows;
    private final int cols;

    public JGridLayout(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
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
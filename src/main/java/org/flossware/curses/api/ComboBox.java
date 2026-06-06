package org.flossware.curses.api;

import java.util.ArrayList;
import java.util.List;

import static org.flossware.curses.api.Constants.*;

public class ComboBox<T> extends Component {
    private final List<T> items = new ArrayList<>();
    private int selectedIndex = NO_INDEX;
    private boolean expanded = false;

    public ComboBox() {
    }

    public void addItem(T item) {
        renderLock.lock();
        try {
            items.add(item);
            if (selectedIndex < 0 && !items.isEmpty()) {
                selectedIndex = 0;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void removeItem(T item) {
        renderLock.lock();
        try {
            int index = items.indexOf(item);
            if (index >= 0) {
                items.remove(index);
                if (selectedIndex >= items.size()) {
                    selectedIndex = items.size() - 1;
                }
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void setSelectedIndex(int index) {
        renderLock.lock();
        try {
            if (index >= 0 && index < items.size()) {
                this.selectedIndex = index;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public T getSelectedItem() {
        return selectedIndex >= 0 && selectedIndex < items.size() ? items.get(selectedIndex) : null;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            String selected = String.valueOf(items.get(selectedIndex));
            String display = "[ " + selected + " v ]";
            writeStringToBuffer(buffer, display, getX(), getY());

            if (expanded) {
                int currentY = getY() + 1;
                for (int i = 0; i < items.size() && currentY < getY() + height; i++) {
                    String prefix = i == selectedIndex ? "> " : "  ";
                    writeStringToBuffer(buffer, prefix + items.get(i), getX(), currentY);
                    currentY++;
                }
            }
        } else {
            writeStringToBuffer(buffer, "[ <empty> v ]", getX(), getY());
        }
    }
}

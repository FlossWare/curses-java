package org.flossware.curses.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        renderLock.lock();
        try {
            return selectedIndex >= 0 && selectedIndex < items.size() ? items.get(selectedIndex) : null;
        } finally {
            renderLock.unlock();
        }
    }

    public int getItemCount() {
        renderLock.lock();
        try {
            return items.size();
        } finally {
            renderLock.unlock();
        }
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        // Take snapshot of items and selectedIndex under lock to avoid
        // ConcurrentModificationException and ArrayIndexOutOfBoundsException
        // when addItem()/removeItem() modify the list on another thread.
        List<T> snapshot;
        int selIdx;
        boolean isExpanded;
        renderLock.lock();
        try {
            snapshot = items.stream().filter(Objects::nonNull).collect(Collectors.toList());
            selIdx = selectedIndex;
            isExpanded = expanded;
        } finally {
            renderLock.unlock();
        }

        if (selIdx >= 0 && selIdx < snapshot.size()) {
            String selected = String.valueOf(snapshot.get(selIdx));
            String display = "[ " + selected + " v ]";
            writeStringToBuffer(buffer, display, getX(), getY());

            if (isExpanded) {
                int currentY = getY() + 1;
                for (int i = 0; i < snapshot.size() && currentY < getY() + height; i++) {
                    String prefix = i == selIdx ? "> " : "  ";
                    writeStringToBuffer(buffer, prefix + snapshot.get(i), getX(), currentY);
                    currentY++;
                }
            }
        } else {
            writeStringToBuffer(buffer, "[ <empty> v ]", getX(), getY());
        }
    }
}

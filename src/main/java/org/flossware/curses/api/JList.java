package org.flossware.curses.api;

import java.util.SequencedSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;

public class JList extends Component {
    private final List<String> items = new ArrayList<>();
    private final SequencedSet<Integer> selectedIndices = new LinkedHashSet<>();

    public void addItem(String item) {
        renderLock.lock();
        try {
            items.add(item);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void removeItem(int index) {
        renderLock.lock();
        try {
            if (index >= 0 && index < items.size()) {
                items.remove(index);
                selectedIndices.remove(index);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public List<String> getItems() {
        renderLock.lock();
        try {
            return List.copyOf(items);
        } finally {
            renderLock.unlock();
        }
    }

    public void select(int index) {
        renderLock.lock();
        try {
            if (index >= 0 && index < items.size()) {
                selectedIndices.addLast(index);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void deselect(int index) {
        renderLock.lock();
        try {
            selectedIndices.remove(index);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void clearSelection() {
        renderLock.lock();
        try {
            selectedIndices.clear();
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public SequencedSet<Integer> getSelectedIndices() {
        renderLock.lock();
        try {
            return new LinkedHashSet<>(selectedIndices);
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int currentY = getY();
            for (int i = 0; i < items.size() && currentY < getY() + getHeight(); i++) {
                String prefix = selectedIndices.contains(i) ? "[X] " : "[ ] ";
                writeStringToBuffer(buffer, prefix + items.get(i), getX(), currentY);
                currentY++;
            }
        } finally {
            renderLock.unlock();
        }
    }
}
package org.flossware.curses.api;

import java.util.SequencedCollection;
import java.util.ArrayList;
import java.lang.Math;

public class Choice extends Component {
    private final SequencedCollection<String> items = new ArrayList<>();
    private int selectedIndex = 0;

    public void add(String item) {
        renderLock.lock();
        try {
            items.addLast(item); // [cite: 99]
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void selectNext() {
        renderLock.lock();
        try {
            if (items.isEmpty()) {
                return;
            }
            // Java 21 Math.clamp ensures index remains within bounds
            this.selectedIndex = Math.clamp(selectedIndex + 1, 0, items.size() - 1);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void selectPrevious() {
        renderLock.lock();
        try {
            if (items.isEmpty()) {
                return;
            }
            this.selectedIndex = Math.clamp(selectedIndex - 1, 0, items.size() - 1);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getSelectedIndex() {
        renderLock.lock();
        try {
            return selectedIndex;
        } finally {
            renderLock.unlock();
        }
    }

    public String getSelectedItem() {
        renderLock.lock();
        try {
            return items.isEmpty() ? null : ((ArrayList<String>)items).get(selectedIndex);
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

    @Override
    public void paint(char[][] buffer) {
        // Take snapshot of items and selectedIndex to avoid race condition
        // where add() modifies items while paint() is reading from selectedIndex
        String label;
        renderLock.lock();
        try {
            label = (items.isEmpty()) ? "" : ((ArrayList<String>)items).get(selectedIndex);
        } finally {
            renderLock.unlock();
        }
        writeStringToBuffer(buffer, "[ " + label + " v ]", getX(), getY());
    }
}
package org.flossware.curses.api;

import java.util.SequencedCollection;
import java.util.ArrayList;
import java.lang.Math;

public class JChoice extends Component {
    private final SequencedCollection<String> items = new ArrayList<>();
    private int selectedIndex = 0;

    public void add(String item) {
        items.addLast(item); // [cite: 99]
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
        return selectedIndex;
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
        return items.size();
    }

    @Override
    public void paint(char[][] buffer) {
        String label = (items.isEmpty()) ? "" : ((ArrayList<String>)items).get(selectedIndex);
        writeStringToBuffer(buffer, "[ " + label + " v ]", getX(), getY());
    }
}
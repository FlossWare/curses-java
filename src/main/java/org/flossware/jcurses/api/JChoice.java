package org.flossware.jcurses.api;

import java.util.SequencedCollection;
import java.util.ArrayList;
import java.lang.Math;

public class JChoice extends Component {
    private final SequencedCollection<String> items = new ArrayList<>();
    private int selectedIndex = 0;

    public void add(String item) {
        items.addLast(item); // [cite: 99]
    }

    public void selectNext() {
        renderLock.lock();
        try {
            // Java 21 Math.clamp ensures index remains within bounds
            this.selectedIndex = Math.clamp(selectedIndex + 1, 0, items.size() - 1);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        String label = (items.isEmpty()) ? "" : ((ArrayList<String>)items).get(selectedIndex);
        writeStringToBuffer(buffer, "[ " + label + " v ]", getX(), getY());
    }
}
package org.flossware.jcurses.api;

import java.util.SequencedSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;

public class JList extends Component {
    private final List<String> items = new ArrayList<>();
    private final SequencedSet<Integer> selectedIndices = new LinkedHashSet<>();

    public void select(int index) {
        renderLock.lock();
        try {
            selectedIndices.addLast(index);
        } finally {
            renderLock.unlock();
        }
        repaint();
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
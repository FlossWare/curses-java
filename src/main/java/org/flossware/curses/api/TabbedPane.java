package org.flossware.curses.api;

import java.util.SequencedMap;
import java.util.LinkedHashMap;
import java.util.SequencedCollection;
import java.util.ArrayList;
import java.util.List;

/**
 * A container that lets the user switch between groups of components by clicking on a tab.
 * Uses SequencedMap to ensure tab order is preserved.
 */
public class TabbedPane extends Container {
    private final SequencedMap<String, Container> tabs = new LinkedHashMap<>();
    private String activeTabLabel;

    // Snapshot cache to reduce GC pressure (Issue #77)
    private List<String> cachedTabList;
    private int modCount = 0;
    private int cachedModCount = -1;

    public void addTab(String label, Container contents) {
        renderLock.lock();
        try {
            tabs.putLast(label, contents);
            if (activeTabLabel == null) {
                activeTabLabel = label;
            }
        } finally {
            renderLock.unlock();
        }
    }

    public void setSelectedTab(String label) {
        if (tabs.containsKey(label)) {
            this.activeTabLabel = label;
            repaint();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        // Create snapshot of tab labels to avoid ConcurrentModificationException
        // when another thread adds/removes tabs during iteration
        List<String> tabLabels;
        renderLock.lock();
        try {
            tabLabels = new ArrayList<>(tabs.keySet());
        } finally {
            renderLock.unlock();
        }

        renderLock.lock();
        try {
            // 1. Render the Tab Headers at the top of the component area
            int currentX = getX();
            for (String label : tabLabels) {
                String decorator = label.equals(activeTabLabel) ? "[" + label + "]" : " " + label + " ";
                writeStringToBuffer(buffer, decorator, currentX, getY());
                currentX += decorator.length() + 1;
            }

            // 2. Render the Active Tab's Container
            Container activePane = tabs.get(activeTabLabel);
            if (activePane != null) {
                activePane.paint(buffer);
            }
        } finally {
            renderLock.unlock();
        }
    }
}
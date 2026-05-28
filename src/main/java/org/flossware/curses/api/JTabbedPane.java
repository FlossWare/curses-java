package org.flossware.curses.api;

import java.util.SequencedMap;
import java.util.LinkedHashMap;
import java.util.SequencedCollection;
import java.util.ArrayList;

/**
 * A container that lets the user switch between groups of components by clicking on a tab.
 * Uses SequencedMap to ensure tab order is preserved.
 */
public class JTabbedPane extends Container {
    private final SequencedMap<String, Container> tabs = new LinkedHashMap<>();
    private String activeTabLabel;

    public void addTab(String label, Container contents) {
        tabs.putLast(label, contents);
        if (activeTabLabel == null) {
            activeTabLabel = label;
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
        renderLock.lock();
        try {
            // 1. Render the Tab Headers at the top of the component area
            int currentX = getX();
            for (String label : tabs.keySet()) {
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
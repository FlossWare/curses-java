package org.flossware.curses.api;

import java.util.SequencedMap;
import java.util.LinkedHashMap;
import java.util.SequencedCollection;

/**
 * Top-level menu bar for the terminal interface.
 */
public class MenuBar extends Container {
    // SequencedMap ensures the order of menus from left to right.
    private final SequencedMap<String, Menu> menus = new LinkedHashMap<>();

    public MenuBar() {
        this.x = 0;
        this.y = 0;
        this.height = 1;
    }

    public void addMenu(String label, Menu menu) {
        menus.putLast(label, menu); // [cite: 99]
    }

    @Override
    public void paint(char[][] buffer) {
        int currentX = 0;
        for (String label : menus.keySet()) {
            writeStringToBuffer(buffer, label, currentX, 0);
            currentX += label.length() + 2;
        }
    }
}
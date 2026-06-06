package org.flossware.curses.api;

import java.util.ArrayList;
import java.util.List;

public class Menu extends Container {
    private String label;
    private final List<MenuItem> items = new ArrayList<>();

    public Menu(String label) {
        this.label = label;
    }

    public void addItem(MenuItem item) {
        items.add(item);
        add(item);
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void paint(char[][] buffer) {
        super.paint(buffer);
    }
}

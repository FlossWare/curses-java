package org.flossware.jcurses.api;

import java.util.ArrayList;
import java.util.List;

public class JMenu extends Container {
    private String label;
    private final List<JMenuItem> items = new ArrayList<>();

    public JMenu(String label) {
        this.label = label;
    }

    public void addItem(JMenuItem item) {
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

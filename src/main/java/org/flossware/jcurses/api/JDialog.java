package org.flossware.jcurses.api;

import java.util.SequencedCollection;
import java.util.LinkedHashSet;

public class JDialog extends Container {
    private boolean modal = true;

    public void show() {
        // In FlossWare architecture, adding to the end of a SequencedCollection
        // represents the "top" of the visual stack[cite: 5, 46].
        RootPane.getInstance().getChildren().addLast(this);
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        drawBorder(buffer);
        super.paint(buffer);
    }
}
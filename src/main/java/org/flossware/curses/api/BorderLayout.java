package org.flossware.curses.api;

import java.util.HashMap;
import java.util.Map;

public class BorderLayout implements LayoutManager {
    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West";
    public static final String CENTER = "Center";

    private final Map<Component, String> constraints = new HashMap<>();

    public synchronized void addLayoutComponent(Component comp, String constraint) {
        constraints.put(comp, constraint);
    }

    @Override
    public synchronized void layoutContainer(Container parent) {
        Component north = null, south = null, east = null, west = null, center = null;

        for (Component child : parent.getChildren()) {
            String constraint = constraints.get(child);
            if (constraint == null) continue;

            switch (constraint) {
                case NORTH -> north = child;
                case SOUTH -> south = child;
                case EAST -> east = child;
                case WEST -> west = child;
                case CENTER -> center = child;
            }
        }

        int currentY = parent.getY();
        int currentX = parent.getX();
        int remainingHeight = parent.getHeight();
        int remainingWidth = parent.getWidth();

        if (north != null) {
            north.setLocation(currentX, currentY);
            north.setSize(remainingWidth, north.getHeight());
            currentY += north.getHeight();
            remainingHeight -= north.getHeight();
        }

        if (south != null) {
            south.setLocation(currentX, parent.getY() + parent.getHeight() - south.getHeight());
            south.setSize(remainingWidth, south.getHeight());
            remainingHeight -= south.getHeight();
        }

        if (west != null) {
            west.setLocation(currentX, currentY);
            west.setSize(west.getWidth(), remainingHeight);
            currentX += west.getWidth();
            remainingWidth -= west.getWidth();
        }

        if (east != null) {
            east.setLocation(parent.getX() + parent.getWidth() - east.getWidth(), currentY);
            east.setSize(east.getWidth(), remainingHeight);
            remainingWidth -= east.getWidth();
        }

        // Ensure remainingWidth and remainingHeight are non-negative
        remainingWidth = Math.max(0, remainingWidth);
        remainingHeight = Math.max(0, remainingHeight);

        if (center != null) {
            center.setLocation(currentX, currentY);
            center.setSize(remainingWidth, remainingHeight);
        }
    }
}

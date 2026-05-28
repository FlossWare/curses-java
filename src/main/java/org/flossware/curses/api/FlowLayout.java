package org.flossware.curses.api;

public class FlowLayout implements LayoutManager {
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    private int align = LEFT;
    private int hgap = 1;
    private int vgap = 1;

    public FlowLayout() {
        this(LEFT);
    }

    public FlowLayout(int align) {
        this.align = align;
    }

    public FlowLayout(int align, int hgap, int vgap) {
        this.align = align;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    @Override
    public void layoutContainer(Container parent) {
        int currentX = parent.getX();
        int currentY = parent.getY();
        int rowHeight = 0;
        int rowWidth = 0;

        for (Component child : parent.getChildren()) {
            if (currentX + child.getWidth() > parent.getX() + parent.getWidth() && rowWidth > 0) {
                currentY += rowHeight + vgap;
                currentX = parent.getX();
                rowHeight = 0;
                rowWidth = 0;
            }

            child.setLocation(currentX, currentY);
            currentX += child.getWidth() + hgap;
            rowHeight = Math.max(rowHeight, child.getHeight());
            rowWidth += child.getWidth() + hgap;
        }
    }
}

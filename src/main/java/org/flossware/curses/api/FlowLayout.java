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
    public void addLayoutComponent(Component comp, Object constraints) {
        // FlowLayout doesn't use constraints
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // FlowLayout doesn't maintain component-specific state
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int maxWidth = 0;
        int totalHeight = 0;
        int rowWidth = 0;
        int rowHeight = 0;

        for (Component child : parent.getChildren()) {
            Dimension d = child.getPreferredSize();

            // Check if we need to wrap to a new row
            if (rowWidth > 0 && rowWidth + hgap + d.width() > parent.getWidth()) {
                maxWidth = Math.max(maxWidth, rowWidth);
                totalHeight += rowHeight + vgap;
                rowWidth = 0;
                rowHeight = 0;
            }

            rowWidth += d.width() + (rowWidth > 0 ? hgap : 0);
            rowHeight = Math.max(rowHeight, d.height());
        }

        // Add the last row
        if (rowWidth > 0) {
            maxWidth = Math.max(maxWidth, rowWidth);
            totalHeight += rowHeight;
        }

        return new Dimension(maxWidth, totalHeight);
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

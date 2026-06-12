package org.flossware.curses.api;

/**
 * A layout manager that arranges components vertically in a single column.
 * Components are stacked from top to bottom with optional spacing between them.
 * Each component uses its full container width and its preferred height.
 *
 * <p>This is similar to Swing's BoxLayout with vertical orientation.
 *
 * @author FlossWare
 * @since 1.1
 * @see HBoxLayout
 * @see LayoutManager
 */
public class VBoxLayout implements LayoutManager {
    private final int spacing;

    /**
     * Creates a VBoxLayout with no spacing between components.
     */
    public VBoxLayout() {
        this(0);
    }

    /**
     * Creates a VBoxLayout with the specified spacing between components.
     *
     * @param spacing the vertical spacing between components (in characters)
     */
    public VBoxLayout(int spacing) {
        this.spacing = Math.max(0, spacing);
    }

    @Override
    public void layoutContainer(Container parent) {
        int y = 0;
        int parentWidth = parent.getWidth();

        for (Component child : parent.getChildren()) {
            if (!child.isVisible()) {
                continue;
            }

            Dimension preferred = child.getPreferredSize();
            child.setLocation(0, y);
            child.setSize(parentWidth, preferred.height());
            y += preferred.height() + spacing;
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int totalHeight = 0;
        int maxWidth = 0;
        int visibleCount = 0;

        for (Component child : parent.getChildren()) {
            if (!child.isVisible()) {
                continue;
            }

            Dimension preferred = child.getPreferredSize();
            totalHeight += preferred.height();
            maxWidth = Math.max(maxWidth, preferred.width());
            visibleCount++;
        }

        // Add spacing between components (not after the last one)
        if (visibleCount > 1) {
            totalHeight += spacing * (visibleCount - 1);
        }

        return new Dimension(maxWidth, totalHeight);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        // VBoxLayout doesn't use constraints
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // VBoxLayout doesn't track components separately
    }
}

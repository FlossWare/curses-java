package org.flossware.curses.api;

/**
 * A layout manager that arranges components horizontally in a single row.
 * Components are placed from left to right with optional spacing between them.
 * Each component uses its full container height and its preferred width.
 *
 * <p>This is similar to Swing's BoxLayout with horizontal orientation.
 *
 * @author FlossWare
 * @since 1.1
 * @see VBoxLayout
 * @see LayoutManager
 */
public class HBoxLayout implements LayoutManager {
    private final int spacing;

    /**
     * Creates an HBoxLayout with no spacing between components.
     */
    public HBoxLayout() {
        this(0);
    }

    /**
     * Creates an HBoxLayout with the specified spacing between components.
     *
     * @param spacing the horizontal spacing between components (in characters)
     */
    public HBoxLayout(int spacing) {
        this.spacing = Math.max(0, spacing);
    }

    @Override
    public void layoutContainer(Container parent) {
        int x = 0;
        int parentHeight = parent.getHeight();

        for (Component child : parent.getChildren()) {
            if (!child.isVisible()) {
                continue;
            }

            Dimension preferred = child.getPreferredSize();
            child.setLocation(x, 0);
            child.setSize(preferred.width(), parentHeight);
            x += preferred.width() + spacing;
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int totalWidth = 0;
        int maxHeight = 0;
        int visibleCount = 0;

        for (Component child : parent.getChildren()) {
            if (!child.isVisible()) {
                continue;
            }

            Dimension preferred = child.getPreferredSize();
            totalWidth += preferred.width();
            maxHeight = Math.max(maxHeight, preferred.height());
            visibleCount++;
        }

        // Add spacing between components (not after the last one)
        if (visibleCount > 1) {
            totalWidth += spacing * (visibleCount - 1);
        }

        return new Dimension(totalWidth, maxHeight);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        // HBoxLayout doesn't use constraints
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // HBoxLayout doesn't track components separately
    }
}

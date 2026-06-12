package org.flossware.curses.api;

/**
 * Interface for terminal layout strategies.
 *
 * @author FlossWare
 * @since 1.0
 */
public interface LayoutManager {
    /**
     * Lays out the specified container's components according to this layout strategy.
     *
     * @param parent the container to layout
     */
    void layoutContainer(Container parent);

    /**
     * Calculates the preferred size dimensions for the specified container,
     * given the components it contains.
     *
     * @param parent the container to calculate preferred size for
     * @return the preferred dimensions to contain all child components
     * @since 1.1
     */
    Dimension preferredLayoutSize(Container parent);

    /**
     * Adds the specified component to the layout, associating it with
     * the specified constraints object.
     *
     * @param comp the component to add
     * @param constraints layout-specific constraints (may be null)
     * @since 1.1
     */
    void addLayoutComponent(Component comp, Object constraints);

    /**
     * Removes the specified component from the layout.
     *
     * @param comp the component to remove
     * @since 1.1
     */
    void removeLayoutComponent(Component comp);
}

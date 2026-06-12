package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.SequencedCollection;

import static org.flossware.curses.api.Constants.*;

public class Container extends Component {
    protected final List<Component> children;
    protected LayoutManager layoutManager;
    private boolean layoutValid = false;
    private int lastLayoutWidth = NO_INDEX;
    private int lastLayoutHeight = NO_INDEX;

    // Snapshot cache to reduce GC pressure (Issue #71)
    private List<Component> cachedSnapshot;
    private int lastSnapshotSize = -1;
    private long modificationCount = 0;
    private long cachedSnapshotModCount = -1;

    public Container() {
        // Wrap children list with mutation tracking to detect external modifications (Issue #207)
        this.children = new MutationTrackingList(new ArrayList<>(), () -> modificationCount++);
    }

    public SequencedCollection<Component> getChildren() {
        return children;
    }

    public void add(Component child) {
        renderLock.lock();
        try {
            children.add(child);
            child.setParent(this);
            layoutValid = false;  // Invalidate layout
            cachedSnapshot = null;  // Invalidate cached snapshot
            modificationCount++;  // Also increment for external mutation detection
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void remove(Component child) {
        if (child == null) {
            throw new NullPointerException("child cannot be null");
        }
        renderLock.lock();
        try {
            children.remove(child);
            child.setParent(null);
            layoutValid = false;  // Invalidate layout
            cachedSnapshot = null;  // Invalidate cached snapshot
            modificationCount++;  // Also increment for external mutation detection
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void setLayout(LayoutManager layout) {
        renderLock.lock();
        try {
            this.layoutManager = layout;
            layoutValid = false;  // Invalidate layout
        } finally {
            renderLock.unlock();
        }
    }

    public void doLayout() {
        if (layoutManager != null) {
            layoutManager.layoutContainer(this);
            layoutValid = true;
            lastLayoutWidth = width;
            lastLayoutHeight = height;
        }
    }

    /**
     * Revalidate will only re-layout if layout is invalid or size changed.
     * This is an optimization to avoid unnecessary layouts.
     */
    public void revalidate() {
        if (layoutManager != null && (!layoutValid || width != lastLayoutWidth || height != lastLayoutHeight)) {
            doLayout();
        }
    }

    public void invalidateLayout() {
        layoutValid = false;
    }

    /**
     * Detects external mutations to the children list by checking modification count.
     * This handles cases where external code mutates children via getChildren()
     * bypassing the add/remove methods (e.g., Dialog.show() calls getChildren().addLast()).
     *
     * Returns true if children list has been modified since last snapshot was cached.
     */
    private boolean detectExternalMutation() {
        return modificationCount != cachedSnapshotModCount;
    }

    /**
     * Returns a snapshot of the current children list.
     * This method is optimized to reuse the cached snapshot when the children list
     * hasn't changed, reducing GC pressure. Uses modification count to detect
     * external mutations via getChildren() that would bypass size-only checking.
     * Must be called by subclasses that need to iterate children while performing
     * temporary mutations (e.g., ScrollPane).
     *
     * @return a snapshot of the children list that is safe to iterate
     */
    protected List<Component> getChildrenSnapshot() {
        renderLock.lock();
        try {
            if (cachedSnapshot == null || detectExternalMutation()) {
                // Children list changed (size change or external mutation), create new snapshot
                cachedSnapshot = new ArrayList<>(children);
                cachedSnapshotModCount = modificationCount;
                lastSnapshotSize = children.size();
            }
            return cachedSnapshot;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        // Rendering order (critical for proper 3D appearance):
        // 1. paintShadow() FIRST (if 3D enabled) - creates depth effect behind component
        // 2. drawBorder() SECOND - frames the component with 3D coloring
        // 3. paint children THIRD - renders content inside the frame

        // Step 1: Paint shadow first (if 3D rendering is enabled)
        paintShadow(buffer, null);

        // Step 2: Draw border second (with 3D coloring if supported)
        drawBorder(buffer, null);

        // Step 3: Paint children third
        // Use cached snapshot to avoid ArrayList allocation on every frame (Issue #71)
        // Detects both size changes and external mutations via modification counter (Issue #207)
        // This optimization reduces GC pressure at high frame rates when children list is stable.
        List<Component> snapshot;
        renderLock.lock();
        try {
            if (cachedSnapshot == null || detectExternalMutation()) {
                // Children list changed (size change or external mutation), create new snapshot
                cachedSnapshot = new ArrayList<>(children);
                cachedSnapshotModCount = modificationCount;
                lastSnapshotSize = children.size();
            }
            snapshot = cachedSnapshot;
        } finally {
            renderLock.unlock();
        }
        for (Component child : snapshot) {
            // Only paint if child is still owned by this container (Issue #70).
            // A child may be removed between snapshot and iteration, causing
            // parent=null. If paint() triggers repaint(), the parent chain walk fails.
            if (child.getParent() == this) {
                child.paint(buffer);
            }
        }
    }

    /**
     * Safely retrieves a component at the specified index.
     * This method provides thread-safe access to the children list with bounds checking.
     * Issue #88: Ensures safe array access without IndexOutOfBoundsException.
     *
     * @param index the index of the component to retrieve
     * @return the component at the specified index, or null if index is out of bounds
     */
    public Component getComponentAt(int index) {
        renderLock.lock();
        try {
            if (index >= 0 && index < children.size()) {
                return children.get(index);
            }
            return null;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public boolean handleMouseEvent(MouseEvent event) {
        // First try to dispatch to children (in reverse order for z-ordering)
        renderLock.lock();
        try {
            int size = children.size();
            for (int i = size - 1; i >= 0; i--) {
                // Issue #88: Re-check bounds on each iteration to handle concurrent modifications
                // Although renderLock protects during iteration, we re-check for safety
                if (i >= 0 && i < children.size()) {
                    Component child = children.get(i);
                    if (child != null && child.handleMouseEvent(event)) {
                        return true;  // Event was handled by a child
                    }
                }
            }
        } finally {
            renderLock.unlock();
        }

        // If no child handled it, try handling it ourselves
        return super.handleMouseEvent(event);
    }

    /**
     * Draws border with basic character rendering only (no color support).
     * Delegates to the overloaded version with null color buffer.
     *
     * @param buffer character buffer to render into
     */
    protected void drawBorder(char[][] buffer) {
        drawBorder(buffer, null);
    }

    /**
     * Draws border with full 3D rendering support including asymmetric coloring.
     *
     * <p>This method implements the complete 3D border rendering pipeline:
     * <ul>
     *   <li>Checks if theme supports 3D via {@link org.flossware.curses.theme.Theme#supports3D()}</li>
     *   <li>Retrieves component's {@link RenderingStyle} (RAISED, SUNKEN, or FLAT)</li>
     *   <li>Applies asymmetric coloring based on style:
     *     <ul>
     *       <li>RAISED: top-left edges use highlight color (bright), bottom-right use lowlight (dark)</li>
     *       <li>SUNKEN: inverted - top-left use lowlight, bottom-right use highlight</li>
     *       <li>FLAT: uniform border color on all edges</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param buffer character buffer to render into
     * @param colorBuffer color pair buffer for per-character coloring (may be null for character-only rendering)
     */
    protected void drawBorder(char[][] buffer, int[][] colorBuffer) {
        if (width <= 0 || height <= 0) return;

        // Get border characters from theme (fallback to component's getBorderChars if theme unavailable)
        String borderChars = getBorderChars();

        // Check if theme supports 3D rendering
        org.flossware.curses.theme.Theme theme = null;
        boolean supports3D = false;
        org.flossware.curses.theme.Theme3D theme3D = null;
        RenderingStyle style = getRenderingStyle();

        try {
            theme = org.flossware.curses.theme.ThemeManager.getInstance().getCurrentTheme();
            supports3D = theme.supports3D();
            if (supports3D) {
                theme3D = (org.flossware.curses.theme.Theme3D) theme;
            }
        } catch (Exception e) {
            // Theme access failed, fall back to basic rendering
        }

        // Determine colors based on rendering style and 3D support
        ColorPair topLeftColor;
        ColorPair bottomRightColor;

        if (supports3D && theme3D != null && style != RenderingStyle.FLAT) {
            // 3D rendering with asymmetric coloring
            if (style == RenderingStyle.RAISED) {
                // RAISED: top-left bright, bottom-right dark
                topLeftColor = theme3D.getHighlightColor();
                bottomRightColor = theme3D.getLowlightColor();
            } else if (style == RenderingStyle.SUNKEN) {
                // SUNKEN: invert colors (top-left dark, bottom-right bright)
                topLeftColor = theme3D.getLowlightColor();
                bottomRightColor = theme3D.getHighlightColor();
            } else {
                // CUSTOM or other: use default border color
                topLeftColor = theme.getBorder();
                bottomRightColor = theme.getBorder();
            }
        } else {
            // FLAT style or non-3D theme: uniform coloring
            ColorPair borderColor = theme != null ? theme.getBorder() : ColorPair.DEFAULT;
            topLeftColor = borderColor;
            bottomRightColor = borderColor;
        }

        // Extract border characters (8-character format: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right)
        char topLeft = borderChars.charAt(0);
        char topEdge = borderChars.charAt(1);
        char topRight = borderChars.charAt(2);
        char leftEdge = borderChars.charAt(3);
        char rightEdge = borderChars.charAt(4);
        char bottomLeft = borderChars.charAt(5);
        char bottomEdge = borderChars.charAt(6);
        char bottomRight = borderChars.charAt(7);

        // Convert ColorPairs to color pair numbers for the color buffer
        int topLeftColorNum = topLeftColor.pairNumber();
        int bottomRightColorNum = bottomRightColor.pairNumber();

        // Draw top edge (highlight color for RAISED, lowlight for SUNKEN, border color for FLAT)
        for (int i = 1; i < width - 1; i++) {
            if (y >= 0 && y < buffer.length && x + i >= 0 && x + i < buffer[0].length) {
                buffer[y][x + i] = topEdge;
                if (colorBuffer != null) {
                    colorBuffer[y][x + i] = topLeftColorNum;
                }
            }
        }

        // Draw bottom edge (lowlight color for RAISED, highlight for SUNKEN, border color for FLAT)
        for (int i = 1; i < width - 1; i++) {
            if (y + height - 1 >= 0 && y + height - 1 < buffer.length && x + i >= 0 && x + i < buffer[0].length) {
                buffer[y + height - 1][x + i] = bottomEdge;
                if (colorBuffer != null) {
                    colorBuffer[y + height - 1][x + i] = bottomRightColorNum;
                }
            }
        }

        // Draw left edge (highlight color for RAISED, lowlight for SUNKEN, border color for FLAT)
        for (int i = 1; i < height - 1; i++) {
            if (y + i >= 0 && y + i < buffer.length && x >= 0 && x < buffer[0].length) {
                buffer[y + i][x] = leftEdge;
                if (colorBuffer != null) {
                    colorBuffer[y + i][x] = topLeftColorNum;
                }
            }
        }

        // Draw right edge (lowlight color for RAISED, highlight for SUNKEN, border color for FLAT)
        for (int i = 1; i < height - 1; i++) {
            if (y + i >= 0 && y + i < buffer.length && x + width - 1 >= 0 && x + width - 1 < buffer[0].length) {
                buffer[y + i][x + width - 1] = rightEdge;
                if (colorBuffer != null) {
                    colorBuffer[y + i][x + width - 1] = bottomRightColorNum;
                }
            }
        }

        // Draw corners with appropriate colors based on rendering style
        // Top-left corner (highlight color for RAISED, lowlight for SUNKEN)
        if (y >= 0 && y < buffer.length && x >= 0 && x < buffer[0].length) {
            buffer[y][x] = topLeft;
            if (colorBuffer != null) {
                colorBuffer[y][x] = topLeftColorNum;
            }
        }

        // Top-right corner (transition: primarily top edge for RAISED)
        if (y >= 0 && y < buffer.length && x + width - 1 >= 0 && x + width - 1 < buffer[0].length) {
            buffer[y][x + width - 1] = topRight;
            if (colorBuffer != null) {
                colorBuffer[y][x + width - 1] = topLeftColorNum;  // Top edge dominates
            }
        }

        // Bottom-left corner (transition: primarily left edge for RAISED)
        if (y + height - 1 >= 0 && y + height - 1 < buffer.length && x >= 0 && x < buffer[0].length) {
            buffer[y + height - 1][x] = bottomLeft;
            if (colorBuffer != null) {
                colorBuffer[y + height - 1][x] = topLeftColorNum;  // Left edge dominates
            }
        }

        // Bottom-right corner (lowlight color for RAISED, highlight for SUNKEN)
        if (y + height - 1 >= 0 && y + height - 1 < buffer.length && x + width - 1 >= 0 && x + width - 1 < buffer[0].length) {
            buffer[y + height - 1][x + width - 1] = bottomRight;
            if (colorBuffer != null) {
                colorBuffer[y + height - 1][x + width - 1] = bottomRightColorNum;
            }
        }
    }

    /**
     * Wrapper list that tracks mutations to detect external modifications via getChildren() (Issue #207).
     * Intercepts all modification methods and invokes a callback to increment modification counter.
     * Implements both List and SequencedCollection for full compatibility.
     */
    private static class MutationTrackingList extends AbstractList<Component> implements SequencedCollection<Component> {
        private final List<Component> delegate;
        private final Runnable onMutation;

        MutationTrackingList(List<Component> delegate, Runnable onMutation) {
            this.delegate = delegate;
            this.onMutation = onMutation;
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public Component get(int index) {
            return delegate.get(index);
        }

        @Override
        public Component set(int index, Component element) {
            onMutation.run();
            return delegate.set(index, element);
        }

        @Override
        public void add(int index, Component element) {
            onMutation.run();
            delegate.add(index, element);
        }

        @Override
        public Component remove(int index) {
            onMutation.run();
            return delegate.remove(index);
        }

        @Override
        public boolean addAll(Collection<? extends Component> c) {
            if (!c.isEmpty()) {
                onMutation.run();
            }
            return delegate.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Component> c) {
            if (!c.isEmpty()) {
                onMutation.run();
            }
            return delegate.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean changed = delegate.removeAll(c);
            if (changed) {
                onMutation.run();
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean changed = delegate.retainAll(c);
            if (changed) {
                onMutation.run();
            }
            return changed;
        }

        @Override
        public void clear() {
            if (!delegate.isEmpty()) {
                onMutation.run();
            }
            delegate.clear();
        }

        // SequencedCollection methods
        @Override
        public Component getFirst() {
            return delegate.getFirst();
        }

        @Override
        public Component getLast() {
            return delegate.getLast();
        }

        @Override
        public void addFirst(Component e) {
            onMutation.run();
            delegate.add(0, e);
        }

        @Override
        public void addLast(Component e) {
            onMutation.run();
            delegate.add(e);
        }

        @Override
        public Component removeFirst() {
            onMutation.run();
            return delegate.removeFirst();
        }

        @Override
        public Component removeLast() {
            onMutation.run();
            return delegate.removeLast();
        }

        @Override
        public List<Component> reversed() {
            return delegate.reversed();
        }
    }
}

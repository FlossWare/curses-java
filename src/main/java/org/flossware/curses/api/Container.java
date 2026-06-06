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

    @Override
    public boolean handleMouseEvent(MouseEvent event) {
        // First try to dispatch to children (in reverse order for z-ordering)
        renderLock.lock();
        try {
            for (int i = children.size() - 1; i >= 0; i--) {
                if (children.get(i).handleMouseEvent(event)) {
                    return true;  // Event was handled by a child
                }
            }
        } finally {
            renderLock.unlock();
        }

        // If no child handled it, try handling it ourselves
        return super.handleMouseEvent(event);
    }

    protected void drawBorder(char[][] buffer) {
        if (width <= 0 || height <= 0) return;

        // Use pure ASCII for better compatibility
        for (int i = 0; i < width; i++) {
            if (y >= 0 && y < buffer.length && x + i >= 0 && x + i < buffer[0].length) {
                buffer[y][x + i] = '-';
            }
            if (y + height - 1 >= 0 && y + height - 1 < buffer.length && x + i >= 0 && x + i < buffer[0].length) {
                buffer[y + height - 1][x + i] = '-';
            }
        }

        for (int i = 0; i < height; i++) {
            if (y + i >= 0 && y + i < buffer.length && x >= 0 && x < buffer[0].length) {
                buffer[y + i][x] = '|';
            }
            if (y + i >= 0 && y + i < buffer.length && x + width - 1 >= 0 && x + width - 1 < buffer[0].length) {
                buffer[y + i][x + width - 1] = '|';
            }
        }

        if (y >= 0 && y < buffer.length && x >= 0 && x < buffer[0].length) {
            buffer[y][x] = '+';
        }
        if (y >= 0 && y < buffer.length && x + width - 1 >= 0 && x + width - 1 < buffer[0].length) {
            buffer[y][x + width - 1] = '+';
        }
        if (y + height - 1 >= 0 && y + height - 1 < buffer.length && x >= 0 && x < buffer[0].length) {
            buffer[y + height - 1][x] = '+';
        }
        if (y + height - 1 >= 0 && y + height - 1 < buffer.length && x + width - 1 >= 0 && x + width - 1 < buffer[0].length) {
            buffer[y + height - 1][x + width - 1] = '+';
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
            int sizeBefore = delegate.size();
            boolean changed = delegate.removeAll(c);
            if (changed) {
                onMutation.run();
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            int sizeBefore = delegate.size();
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

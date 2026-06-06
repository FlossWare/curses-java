package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedCollection;

import static org.flossware.curses.api.Constants.*;

public class Container extends Component {
    protected final List<Component> children = new ArrayList<>();
    protected LayoutManager layoutManager;
    private boolean layoutValid = false;
    private int lastLayoutWidth = NO_INDEX;
    private int lastLayoutHeight = NO_INDEX;

    // Snapshot cache to reduce GC pressure (Issue #71)
    private List<Component> cachedSnapshot;
    private int lastSnapshotSize = -1;

    public SequencedCollection<Component> getChildren() {
        return children;
    }

    public void add(Component child) {
        renderLock.lock();
        try {
            children.add(child);
            child.setParent(this);
            layoutValid = false;  // Invalidate layout
            invalidateSnapshot();  // Invalidate cached snapshot
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
            invalidateSnapshot();  // Invalidate cached snapshot
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void setLayout(LayoutManager layout) {
        this.layoutManager = layout;
        layoutValid = false;  // Invalidate layout
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
     * Invalidates the cached children snapshot when the children list is modified.
     * This is called internally when add/remove operations occur.
     */
    private void invalidateSnapshot() {
        lastSnapshotSize = -1;
        cachedSnapshot = null;
    }

    @Override
    public void paint(char[][] buffer) {
        // Use cached snapshot to avoid ArrayList allocation on every frame (Issue #71)
        // Only allocate when children list has actually changed in size.
        // This optimization reduces GC pressure at high frame rates when children list is stable.
        List<Component> snapshot;
        renderLock.lock();
        try {
            int currentSize = children.size();
            if (cachedSnapshot == null || lastSnapshotSize != currentSize) {
                // Children list changed, create new snapshot
                cachedSnapshot = new ArrayList<>(children);
                lastSnapshotSize = currentSize;
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
}

package org.flossware.curses.testutil;

import org.flossware.curses.api.Component;
import org.flossware.curses.api.RootPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for component tests with common setup and teardown.
 */
public abstract class ComponentTestBase {
    protected char[][] buffer;
    protected RootPane root;

    @BeforeEach
    void baseSetUp() {
        buffer = BufferAssertions.createBuffer(120, 40);
        root = RootPane.getInstance();
        root.clearDirty();
    }

    @AfterEach
    void baseTearDown() {
        // Clean up any children from root
        for (Component child : new ArrayList<>(root.getChildren())) {
            root.remove(child);
        }
        root.clearDirty();
    }

    /**
     * Assert that the dirty flag is set on the RootPane.
     */
    protected void assertDirtyFlagSet() {
        assertTrue(root.isDirty(), "Expected dirty flag to be set");
    }

    /**
     * Assert that the dirty flag is not set on the RootPane.
     */
    protected void assertDirtyFlagNotSet() {
        assertFalse(root.isDirty(), "Expected dirty flag to be clear");
    }

    /**
     * Clear the dirty flag on the RootPane.
     */
    protected void clearDirtyFlag() {
        root.clearDirty();
    }
}

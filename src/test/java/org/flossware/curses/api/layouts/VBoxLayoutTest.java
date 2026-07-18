package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VBoxLayout Tests")
class VBoxLayoutTest {
    private Container container;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(80, 40);
    }

    // --- Constructor tests ---

    @Test
    @DisplayName("should create with default zero spacing")
    void testDefaultConstructor() {
        VBoxLayout layout = new VBoxLayout();
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(15, 8);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        // With zero spacing, child2 starts right below child1
        assertEquals(0, child1.getY());
        assertEquals(5, child2.getY());
    }

    @Test
    @DisplayName("should create with custom positive spacing")
    void testCustomSpacingConstructor() {
        VBoxLayout layout = new VBoxLayout(3);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(15, 8);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        // With spacing=3, child2 starts at 5 + 3 = 8
        assertEquals(0, child1.getY());
        assertEquals(8, child2.getY());
    }

    @Test
    @DisplayName("should clamp negative spacing to zero")
    void testNegativeSpacingClampedToZero() {
        VBoxLayout layout = new VBoxLayout(-7);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(15, 8);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        // Negative spacing clamped to 0, so same as default
        assertEquals(0, child1.getY());
        assertEquals(5, child2.getY());
    }

    // --- layoutContainer tests ---

    @Test
    @DisplayName("should layout multiple visible children vertically")
    void testLayoutMultipleVisibleChildren() {
        VBoxLayout layout = new VBoxLayout();
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(20, 8);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 3);

        container.add(child1);
        container.add(child2);
        container.add(child3);
        container.doLayout();

        // Verify y positions
        assertEquals(0, child1.getY());
        assertEquals(5, child2.getY());
        assertEquals(13, child3.getY());

        // Verify x positions (all at 0)
        assertEquals(0, child1.getX());
        assertEquals(0, child2.getX());
        assertEquals(0, child3.getX());

        // Verify heights match preferred heights
        assertEquals(5, child1.getHeight());
        assertEquals(8, child2.getHeight());
        assertEquals(3, child3.getHeight());

        // Verify widths match parent width
        assertEquals(80, child1.getWidth());
        assertEquals(80, child2.getWidth());
        assertEquals(80, child3.getWidth());
    }

    @Test
    @DisplayName("should skip invisible children during layout")
    void testLayoutSkipsInvisibleChildren() {
        VBoxLayout layout = new VBoxLayout();
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(20, 8);
        child2.setVisible(false);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 3);

        container.add(child1);
        container.add(child2);
        container.add(child3);
        container.doLayout();

        // child1 at y=0, child2 skipped, child3 at y=5 (right after child1)
        assertEquals(0, child1.getY());
        assertEquals(5, child3.getY());
    }

    @Test
    @DisplayName("should apply spacing between children during layout")
    void testLayoutWithSpacing() {
        VBoxLayout layout = new VBoxLayout(2);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(20, 8);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 3);

        container.add(child1);
        container.add(child2);
        container.add(child3);
        container.doLayout();

        // y positions: 0, 5+2=7, 7+8+2=17
        assertEquals(0, child1.getY());
        assertEquals(7, child2.getY());
        assertEquals(17, child3.getY());
    }

    @Test
    @DisplayName("should handle empty container in layoutContainer")
    void testLayoutEmptyContainer() {
        VBoxLayout layout = new VBoxLayout();
        container.setLayout(layout);

        assertDoesNotThrow(() -> container.doLayout());
    }

    // --- preferredLayoutSize tests ---

    @Test
    @DisplayName("should return zero dimensions for empty container")
    void testPreferredSizeEmptyContainer() {
        VBoxLayout layout = new VBoxLayout(5);
        container.setLayout(layout);

        Dimension preferred = layout.preferredLayoutSize(container);

        assertEquals(0, preferred.width());
        assertEquals(0, preferred.height());
    }

    @Test
    @DisplayName("should return single child dimensions for one child")
    void testPreferredSizeSingleChild() {
        VBoxLayout layout = new VBoxLayout(5);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(12, 7);
        container.add(child1);

        Dimension preferred = layout.preferredLayoutSize(container);

        // Single child: no spacing added
        assertEquals(12, preferred.width());
        assertEquals(7, preferred.height());
    }

    @Test
    @DisplayName("should sum heights and add spacing for multiple children")
    void testPreferredSizeMultipleChildren() {
        VBoxLayout layout = new VBoxLayout(4);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(20, 8);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 3);

        container.add(child1);
        container.add(child2);
        container.add(child3);

        Dimension preferred = layout.preferredLayoutSize(container);

        // Width: max(10, 20, 15) = 20
        assertEquals(20, preferred.width());
        // Height: 5 + 8 + 3 + 4*(3-1) = 16 + 8 = 24
        assertEquals(24, preferred.height());
    }

    @Test
    @DisplayName("should skip invisible children in preferred size calculation")
    void testPreferredSizeSkipsInvisibleChildren() {
        VBoxLayout layout = new VBoxLayout(4);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(30, 8);
        child2.setVisible(false);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 3);

        container.add(child1);
        container.add(child2);
        container.add(child3);

        Dimension preferred = layout.preferredLayoutSize(container);

        // Width: max(10, 15) = 15 (child2 skipped)
        assertEquals(15, preferred.width());
        // Height: 5 + 3 + 4*(2-1) = 8 + 4 = 12 (child2 skipped)
        assertEquals(12, preferred.height());
    }

    // --- No-op method tests ---

    @Test
    @DisplayName("should handle addLayoutComponent as no-op")
    void testAddLayoutComponentNoOp() {
        VBoxLayout layout = new VBoxLayout();
        Label label = new Label("test");

        assertDoesNotThrow(() -> layout.addLayoutComponent(label, "constraint"));
        assertDoesNotThrow(() -> layout.addLayoutComponent(label, null));
    }

    @Test
    @DisplayName("should handle removeLayoutComponent as no-op")
    void testRemoveLayoutComponentNoOp() {
        VBoxLayout layout = new VBoxLayout();
        Label label = new Label("test");

        assertDoesNotThrow(() -> layout.removeLayoutComponent(label));
    }
}

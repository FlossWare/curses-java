package org.flossware.curses.api.layouts;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HBoxLayout Tests")
class HBoxLayoutTest {
    private Container container;

    @BeforeEach
    void setUp() {
        container = new Panel();
        container.setLocation(0, 0);
        container.setSize(80, 20);
    }

    // --- Constructor tests ---

    @Test
    @DisplayName("should create with default zero spacing")
    void testDefaultConstructor() {
        HBoxLayout layout = new HBoxLayout();
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(15, 5);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        // With zero spacing, child2 starts right after child1
        assertEquals(0, child1.getX());
        assertEquals(10, child2.getX());
    }

    @Test
    @DisplayName("should create with custom positive spacing")
    void testCustomSpacingConstructor() {
        HBoxLayout layout = new HBoxLayout(5);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(15, 5);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        // With spacing=5, child2 starts at 10 + 5 = 15
        assertEquals(0, child1.getX());
        assertEquals(15, child2.getX());
    }

    @Test
    @DisplayName("should clamp negative spacing to zero")
    void testNegativeSpacingClampedToZero() {
        HBoxLayout layout = new HBoxLayout(-10);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 5);
        Label child2 = new Label("B");
        child2.setPreferredSize(15, 5);

        container.add(child1);
        container.add(child2);
        container.doLayout();

        // Negative spacing clamped to 0, so same as default
        assertEquals(0, child1.getX());
        assertEquals(10, child2.getX());
    }

    // --- layoutContainer tests ---

    @Test
    @DisplayName("should layout multiple visible children horizontally")
    void testLayoutMultipleVisibleChildren() {
        HBoxLayout layout = new HBoxLayout();
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

        // Verify x positions
        assertEquals(0, child1.getX());
        assertEquals(10, child2.getX());
        assertEquals(30, child3.getX());

        // Verify y positions (all at 0)
        assertEquals(0, child1.getY());
        assertEquals(0, child2.getY());
        assertEquals(0, child3.getY());

        // Verify widths match preferred widths
        assertEquals(10, child1.getWidth());
        assertEquals(20, child2.getWidth());
        assertEquals(15, child3.getWidth());

        // Verify heights match parent height
        assertEquals(20, child1.getHeight());
        assertEquals(20, child2.getHeight());
        assertEquals(20, child3.getHeight());
    }

    @Test
    @DisplayName("should skip invisible children during layout")
    void testLayoutSkipsInvisibleChildren() {
        HBoxLayout layout = new HBoxLayout();
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

        // child1 at x=0, child2 skipped, child3 at x=10 (right after child1)
        assertEquals(0, child1.getX());
        assertEquals(10, child3.getX());
    }

    @Test
    @DisplayName("should apply spacing between children during layout")
    void testLayoutWithSpacing() {
        HBoxLayout layout = new HBoxLayout(3);
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

        // x positions: 0, 10+3=13, 13+20+3=36
        assertEquals(0, child1.getX());
        assertEquals(13, child2.getX());
        assertEquals(36, child3.getX());
    }

    @Test
    @DisplayName("should handle empty container in layoutContainer")
    void testLayoutEmptyContainer() {
        HBoxLayout layout = new HBoxLayout();
        container.setLayout(layout);

        assertDoesNotThrow(() -> container.doLayout());
    }

    // --- preferredLayoutSize tests ---

    @Test
    @DisplayName("should return zero dimensions for empty container")
    void testPreferredSizeEmptyContainer() {
        HBoxLayout layout = new HBoxLayout(5);
        container.setLayout(layout);

        Dimension preferred = layout.preferredLayoutSize(container);

        assertEquals(0, preferred.width());
        assertEquals(0, preferred.height());
    }

    @Test
    @DisplayName("should return single child dimensions for one child")
    void testPreferredSizeSingleChild() {
        HBoxLayout layout = new HBoxLayout(5);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 7);
        container.add(child1);

        Dimension preferred = layout.preferredLayoutSize(container);

        // Single child: no spacing added
        assertEquals(10, preferred.width());
        assertEquals(7, preferred.height());
    }

    @Test
    @DisplayName("should sum widths and add spacing for multiple children")
    void testPreferredSizeMultipleChildren() {
        HBoxLayout layout = new HBoxLayout(5);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 7);
        Label child2 = new Label("B");
        child2.setPreferredSize(20, 3);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 9);

        container.add(child1);
        container.add(child2);
        container.add(child3);

        Dimension preferred = layout.preferredLayoutSize(container);

        // Width: 10 + 20 + 15 + 5*(3-1) = 45 + 10 = 55
        assertEquals(55, preferred.width());
        // Height: max(7, 3, 9) = 9
        assertEquals(9, preferred.height());
    }

    @Test
    @DisplayName("should skip invisible children in preferred size calculation")
    void testPreferredSizeSkipsInvisibleChildren() {
        HBoxLayout layout = new HBoxLayout(5);
        container.setLayout(layout);

        Label child1 = new Label("A");
        child1.setPreferredSize(10, 7);
        Label child2 = new Label("B");
        child2.setPreferredSize(20, 12);
        child2.setVisible(false);
        Label child3 = new Label("C");
        child3.setPreferredSize(15, 9);

        container.add(child1);
        container.add(child2);
        container.add(child3);

        Dimension preferred = layout.preferredLayoutSize(container);

        // Width: 10 + 15 + 5*(2-1) = 25 + 5 = 30 (child2 skipped)
        assertEquals(30, preferred.width());
        // Height: max(7, 9) = 9 (child2 skipped)
        assertEquals(9, preferred.height());
    }

    // --- No-op method tests ---

    @Test
    @DisplayName("should handle addLayoutComponent as no-op")
    void testAddLayoutComponentNoOp() {
        HBoxLayout layout = new HBoxLayout();
        Label label = new Label("test");

        assertDoesNotThrow(() -> layout.addLayoutComponent(label, "constraint"));
        assertDoesNotThrow(() -> layout.addLayoutComponent(label, null));
    }

    @Test
    @DisplayName("should handle removeLayoutComponent as no-op")
    void testRemoveLayoutComponentNoOp() {
        HBoxLayout layout = new HBoxLayout();
        Label label = new Label("test");

        assertDoesNotThrow(() -> layout.removeLayoutComponent(label));
    }
}

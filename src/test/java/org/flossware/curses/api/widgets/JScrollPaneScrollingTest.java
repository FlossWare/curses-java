package org.flossware.curses.api.widgets;

import org.flossware.curses.api.JLabel;
import org.flossware.curses.api.JScrollBar;
import org.flossware.curses.api.JScrollPane;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JScrollPane Scrolling Tests")
class JScrollPaneScrollingTest extends ComponentTestBase {
    private JScrollPane scrollPane;
    private JLabel content;

    @BeforeEach
    void setUp() {
        scrollPane = new JScrollPane();
        scrollPane.setLocation(0, 0);
        scrollPane.setSize(20, 10);

        content = new JLabel("This is scrollable content that extends beyond viewport");
        content.setLocation(0, 0);
        content.setSize(60, 1);

        scrollPane.add(content);
    }

    @Test
    @DisplayName("should create scroll pane with view component")
    void testCreationWithView() {
        JLabel view = new JLabel("Test");
        JScrollPane pane = new JScrollPane(view);

        assertEquals(1, pane.getChildren().size());
        assertTrue(pane.getChildren().contains(view));
    }

    @Test
    @DisplayName("should scroll to position")
    void testScrollTo() {
        scrollPane.scrollTo(10, 5);

        assertEquals(10, scrollPane.getOffsetX());
        assertEquals(5, scrollPane.getOffsetY());
    }

    @Test
    @DisplayName("should scroll by delta")
    void testScroll() {
        scrollPane.scrollTo(10, 5);
        scrollPane.scroll(5, 3);

        assertEquals(15, scrollPane.getOffsetX());
        assertEquals(8, scrollPane.getOffsetY());
    }

    @Test
    @DisplayName("should not scroll to negative offsets")
    void testNegativeScrollClamping() {
        scrollPane.scrollTo(-10, -5);

        assertEquals(0, scrollPane.getOffsetX());
        assertEquals(0, scrollPane.getOffsetY());
    }

    @Test
    @DisplayName("should get viewport dimensions")
    void testViewportDimensions() {
        assertEquals(20, scrollPane.getViewportWidth());
        assertEquals(10, scrollPane.getViewportHeight());
    }

    @Test
    @DisplayName("should calculate content width")
    void testContentWidth() {
        int contentWidth = scrollPane.getContentWidth();

        assertEquals(60, contentWidth);  // content is 60 wide
    }

    @Test
    @DisplayName("should calculate content height")
    void testContentHeight() {
        int contentHeight = scrollPane.getContentHeight();

        assertEquals(1, contentHeight);  // content is 1 tall
    }

    @Test
    @DisplayName("should handle multiple children in content calculation")
    void testMultipleChildrenContentSize() {
        JLabel secondLabel = new JLabel("Second");
        secondLabel.setLocation(0, 5);
        secondLabel.setSize(30, 1);

        scrollPane.add(secondLabel);

        assertEquals(60, scrollPane.getContentWidth());  // Max of 60 and 30
        assertEquals(6, scrollPane.getContentHeight());  // Max of 1 and 6 (5+1)
    }

    @Test
    @DisplayName("should set and get horizontal scrollbar")
    void testHorizontalScrollBar() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollPane.setHorizontalScrollBar(scrollBar);

        assertEquals(scrollBar, scrollPane.getHorizontalScrollBar());
    }

    @Test
    @DisplayName("should set and get vertical scrollbar")
    void testVerticalScrollBar() {
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollPane.setVerticalScrollBar(scrollBar);

        assertEquals(scrollBar, scrollPane.getVerticalScrollBar());
    }

    @Test
    @DisplayName("should trigger repaint when scrolling")
    void testScrollRepaint() {
        root.add(scrollPane);
        clearDirtyFlag();

        scrollPane.scrollTo(10, 5);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should paint children with offset translation")
    void testPaintWithOffset() {
        scrollPane.scrollTo(10, 0);

        // Paint should translate child coordinates
        assertDoesNotThrow(() -> scrollPane.paint(buffer));
    }

    @Test
    @DisplayName("should handle empty scroll pane")
    void testEmptyScrollPane() {
        JScrollPane emptyPane = new JScrollPane();
        emptyPane.setLocation(0, 0);
        emptyPane.setSize(20, 10);

        assertEquals(0, emptyPane.getContentWidth());
        assertEquals(0, emptyPane.getContentHeight());
        assertDoesNotThrow(() -> emptyPane.paint(buffer));
    }
}

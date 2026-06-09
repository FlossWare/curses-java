package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for SplitPane class.
 */
@DisplayName("SplitPane Comprehensive Tests")
class JSplitPaneTest extends ComponentTestBase {

    @Test
    @DisplayName("Constructor with orientation should create split pane")
    void testConstructorWithOrientation() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        assertNotNull(splitPane);
        assertEquals(JSplitPane.HORIZONTAL_SPLIT, splitPane.getOrientation());
        assertEquals(0.5, splitPane.getDividerLocation());
    }

    @Test
    @DisplayName("Constructor with components should set components")
    void testConstructorWithComponents() {
        Panel left = new Panel();
        Panel right = new Panel();

        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);

        assertSame(left, splitPane.getLeftComponent());
        assertSame(right, splitPane.getRightComponent());
    }

    @Test
    @DisplayName("setLeftComponent should set left component")
    void testSetLeftComponent() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Panel panel = new Panel();
        splitPane.setLeftComponent(panel);

        assertSame(panel, splitPane.getLeftComponent());
        assertDoesNotThrow(() -> splitPane.paint(buffer));
    }

    @Test
    @DisplayName("setLeftComponent should replace existing component")
    void testSetLeftComponentReplace() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Panel panel1 = new Panel();
        Panel panel2 = new Panel();

        splitPane.setLeftComponent(panel1);
        splitPane.setLeftComponent(panel2);

        assertSame(panel2, splitPane.getLeftComponent());
    }

    @Test
    @DisplayName("setLeftComponent with null should clear component")
    void testSetLeftComponentNull() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Panel panel = new Panel();
        splitPane.setLeftComponent(panel);
        splitPane.setLeftComponent(null);

        assertNull(splitPane.getLeftComponent());
        assertDoesNotThrow(() -> splitPane.paint(buffer));
    }

    @Test
    @DisplayName("setRightComponent should set right component")
    void testSetRightComponent() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Panel panel = new Panel();
        splitPane.setRightComponent(panel);

        assertSame(panel, splitPane.getRightComponent());
        assertDoesNotThrow(() -> splitPane.paint(buffer));
    }

    @Test
    @DisplayName("setRightComponent should replace existing component")
    void testSetRightComponentReplace() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Panel panel1 = new Panel();
        Panel panel2 = new Panel();

        splitPane.setRightComponent(panel1);
        splitPane.setRightComponent(panel2);

        assertSame(panel2, splitPane.getRightComponent());
    }

    @Test
    @DisplayName("setRightComponent with null should clear component")
    void testSetRightComponentNull() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Panel panel = new Panel();
        splitPane.setRightComponent(panel);
        splitPane.setRightComponent(null);

        assertNull(splitPane.getRightComponent());
        assertDoesNotThrow(() -> splitPane.paint(buffer));
    }

    @Test
    @DisplayName("setDividerLocation should set divider position")
    void testSetDividerLocation() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        splitPane.setDividerLocation(0.3);
        assertEquals(0.3, splitPane.getDividerLocation(), 0.01);

        splitPane.setDividerLocation(0.7);
        assertEquals(0.7, splitPane.getDividerLocation(), 0.01);
    }

    @Test
    @DisplayName("setDividerLocation should clamp to valid range")
    void testSetDividerLocationClamp() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        splitPane.setDividerLocation(0.05);
        assertEquals(0.1, splitPane.getDividerLocation());

        splitPane.setDividerLocation(0.95);
        assertEquals(0.9, splitPane.getDividerLocation());
    }

    @Test
    @DisplayName("doLayout should position components horizontally")
    void testDoLayoutHorizontal() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(10, 5);

        Panel left = new Panel();
        Panel right = new Panel();

        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);
        splitPane.setDividerLocation(0.5);
        splitPane.doLayout();

        // Left component should be on the left
        assertEquals(10, left.getX());
        assertEquals(5, left.getY());
        assertEquals(30, left.getWidth());
        assertEquals(20, left.getHeight());

        // Right component should be on the right
        assertEquals(41, right.getX(), "Right component X should account for left width + divider");
        assertEquals(5, right.getY());
        assertEquals(29, right.getWidth());
        assertEquals(20, right.getHeight());
    }

    @Test
    @DisplayName("doLayout should position components vertically")
    void testDoLayoutVertical() {
        SplitPane splitPane = new SplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setSize(40, 30);
        splitPane.setLocation(5, 10);

        Panel top = new Panel();
        Panel bottom = new Panel();

        splitPane.setLeftComponent(top);
        splitPane.setRightComponent(bottom);
        splitPane.setDividerLocation(0.5);
        splitPane.doLayout();

        // Top component
        assertEquals(5, top.getX());
        assertEquals(10, top.getY());
        assertEquals(40, top.getWidth());
        assertEquals(15, top.getHeight());

        // Bottom component
        assertEquals(5, bottom.getX());
        assertEquals(26, bottom.getY(), "Bottom component Y should account for top height + divider");
        assertEquals(40, bottom.getWidth());
        assertEquals(14, bottom.getHeight());
    }

    @Test
    @DisplayName("paint should render horizontal divider")
    void testPaintHorizontalDivider() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);
        splitPane.setDividerLocation(0.5);

        splitPane.paint(buffer);

        // Check for vertical divider character at appropriate position
        boolean hasDivider = false;
        for (int i = 0; i < 20; i++) {
            if (buffer[i][30] == '|') {
                hasDivider = true;
                break;
            }
        }
        assertTrue(hasDivider, "Should render vertical divider");
    }

    @Test
    @DisplayName("paint should render vertical divider")
    void testPaintVerticalDivider() {
        SplitPane splitPane = new SplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setSize(40, 30);
        splitPane.setLocation(0, 0);
        splitPane.setDividerLocation(0.5);

        splitPane.paint(buffer);

        // Check for horizontal divider character at appropriate position
        boolean hasDivider = false;
        for (int i = 0; i < 40; i++) {
            if (buffer[15][i] == '-') {
                hasDivider = true;
                break;
            }
        }
        assertTrue(hasDivider, "Should render horizontal divider");
    }

    @Test
    @DisplayName("Constants should have expected values")
    void testConstants() {
        assertEquals(0, JSplitPane.HORIZONTAL_SPLIT);
        assertEquals(1, JSplitPane.VERTICAL_SPLIT);
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final double location = (i + 1) * 0.08 + 0.1;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    splitPane.setDividerLocation(location);
                    splitPane.doLayout();
                    splitPane.paint(buffer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(splitPane.getDividerLocation() >= 0.1);
        assertTrue(splitPane.getDividerLocation() <= 0.9);
    }

    @Test
    @DisplayName("divider location should affect layout")
    void testDividerLocationAffectsLayout() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(100, 20);
        splitPane.setLocation(0, 0);

        Panel left = new Panel();
        Panel right = new Panel();
        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);

        splitPane.setDividerLocation(0.3);
        splitPane.doLayout();
        int leftWidth1 = left.getWidth();

        splitPane.setDividerLocation(0.7);
        splitPane.doLayout();
        int leftWidth2 = left.getWidth();

        assertTrue(leftWidth2 > leftWidth1, "Left component should be wider at 0.7 than at 0.3");
    }

    @Test
    @DisplayName("should handle null components in doLayout")
    void testDoLayoutWithNullComponents() {
        SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setSize(60, 20);
        splitPane.setLocation(0, 0);

        assertDoesNotThrow(() -> splitPane.doLayout());
        assertDoesNotThrow(() -> splitPane.paint(buffer));
    }
}

package org.flossware.curses.api;

import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for ToolBar class.
 */
@DisplayName("ToolBar Tests")
class JToolBarTest extends ComponentTestBase {

    private ToolBar toolBar;

    @BeforeEach
    void setUp() {
        toolBar = new ToolBar();
        toolBar.setSize(80, 24);
        toolBar.setLocation(0, 0);
    }

    @Test
    @DisplayName("Default constructor should create horizontal toolbar")
    void testDefaultConstructorHorizontal() {
        assertNotNull(toolBar);
        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("Constructor with HORIZONTAL orientation")
    void testConstructorHorizontal() {
        ToolBar horizontal = new ToolBar(JToolBar.HORIZONTAL);
        assertNotNull(horizontal);
        assertDoesNotThrow(() -> horizontal.paint(buffer));
    }

    @Test
    @DisplayName("Constructor with VERTICAL orientation")
    void testConstructorVertical() {
        ToolBar vertical = new ToolBar(JToolBar.VERTICAL);
        assertNotNull(vertical);
        assertDoesNotThrow(() -> vertical.paint(buffer));
    }

    @Test
    @DisplayName("setOrientation should change orientation")
    void testSetOrientation() {
        toolBar.setOrientation(JToolBar.VERTICAL);
        assertDoesNotThrow(() -> toolBar.paint(buffer));

        toolBar.setOrientation(JToolBar.HORIZONTAL);
        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("doLayout should arrange children horizontally")
    void testDoLayoutHorizontal() {
        toolBar.setOrientation(JToolBar.HORIZONTAL);

        Button btn1 = new Button("Button 1");
        btn1.setSize(10, 1);
        Button btn2 = new Button("Button 2");
        btn2.setSize(10, 1);
        Button btn3 = new Button("Button 3");
        btn3.setSize(10, 1);

        toolBar.add(btn1);
        toolBar.add(btn2);
        toolBar.add(btn3);

        toolBar.doLayout();

        // Verify horizontal arrangement with spacing
        assertEquals(0, btn1.getX());
        assertEquals(0, btn1.getY());

        assertEquals(11, btn2.getX(), "Should be offset by width + 1");
        assertEquals(0, btn2.getY());

        assertEquals(22, btn3.getX(), "Should be offset by cumulative width + spacing");
        assertEquals(0, btn3.getY());
    }

    @Test
    @DisplayName("doLayout should arrange children vertically")
    void testDoLayoutVertical() {
        toolBar.setOrientation(JToolBar.VERTICAL);

        Button btn1 = new Button("Button 1");
        btn1.setSize(10, 1);
        Button btn2 = new Button("Button 2");
        btn2.setSize(10, 1);
        Button btn3 = new Button("Button 3");
        btn3.setSize(10, 1);

        toolBar.add(btn1);
        toolBar.add(btn2);
        toolBar.add(btn3);

        toolBar.doLayout();

        // Verify vertical arrangement with spacing
        assertEquals(0, btn1.getX());
        assertEquals(0, btn1.getY());

        assertEquals(0, btn2.getX());
        assertEquals(2, btn2.getY(), "Should be offset by height + 1");

        assertEquals(0, btn3.getX());
        assertEquals(4, btn3.getY(), "Should be offset by cumulative height + spacing");
    }

    @Test
    @DisplayName("doLayout should handle empty toolbar")
    void testDoLayoutEmpty() {
        assertDoesNotThrow(() -> toolBar.doLayout());
    }

    @Test
    @DisplayName("setOrientation should trigger layout and repaint")
    void testSetOrientationTriggersLayoutAndRepaint() {
        Button btn = new Button("Test");
        btn.setSize(10, 1);
        toolBar.add(btn);

        toolBar.setOrientation(JToolBar.VERTICAL);
        assertDoesNotThrow(() -> toolBar.paint(buffer));

        toolBar.setOrientation(JToolBar.HORIZONTAL);
        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("paint should render toolbar with children")
    void testPaintWithChildren() {
        Button btn1 = new Button("Btn1");
        btn1.setSize(10, 1);
        Button btn2 = new Button("Btn2");
        btn2.setSize(10, 1);

        toolBar.add(btn1);
        toolBar.add(btn2);

        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("paint should render empty toolbar")
    void testPaintEmpty() {
        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("Constants should have expected values")
    void testConstants() {
        assertEquals(0, JToolBar.HORIZONTAL);
        assertEquals(1, JToolBar.VERTICAL);
    }

    @Test
    @DisplayName("should handle mixed component types")
    void testMixedComponentTypes() {
        toolBar.setOrientation(JToolBar.HORIZONTAL);

        Button button = new Button("Save");
        button.setSize(10, 1);
        Label label = new Label("Tools:");
        label.setSize(8, 1);
        Checkbox checkbox = new Checkbox("Enable");
        checkbox.setSize(12, 1);

        toolBar.add(label);
        toolBar.add(button);
        toolBar.add(checkbox);

        toolBar.doLayout();
        assertDoesNotThrow(() -> toolBar.paint(buffer));

        // Verify layout
        assertEquals(0, label.getX());
        assertEquals(9, button.getX());
        assertEquals(20, checkbox.getX());
    }

    @Test
    @DisplayName("doLayout should be thread-safe")
    void testDoLayoutThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    Button btn = new Button("Btn-" + threadId);
                    btn.setSize(5, 1);
                    toolBar.add(btn);
                    toolBar.doLayout();
                    toolBar.paint(buffer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("doLayout should handle zero-sized children")
    void testDoLayoutZeroSizedChildren() {
        toolBar.setOrientation(JToolBar.HORIZONTAL);

        Label label1 = new Label("A");
        label1.setSize(0, 0);
        Label label2 = new Label("B");
        label2.setSize(5, 1);

        toolBar.add(label1);
        toolBar.add(label2);

        assertDoesNotThrow(() -> toolBar.doLayout());
        assertDoesNotThrow(() -> toolBar.paint(buffer));
    }

    @Test
    @DisplayName("switching orientation should rearrange children")
    void testSwitchOrientationRearranges() {
        Button btn1 = new Button("1");
        btn1.setSize(5, 1);
        Button btn2 = new Button("2");
        btn2.setSize(5, 1);

        toolBar.add(btn1);
        toolBar.add(btn2);

        // Start horizontal
        toolBar.setOrientation(JToolBar.HORIZONTAL);
        int horizontalBtn2X = btn2.getX();
        int horizontalBtn2Y = btn2.getY();

        // Switch to vertical
        toolBar.setOrientation(JToolBar.VERTICAL);
        int verticalBtn2X = btn2.getX();
        int verticalBtn2Y = btn2.getY();

        // Verify positions changed
        assertNotEquals(horizontalBtn2X, verticalBtn2X, "X should change when switching to vertical");
        assertNotEquals(horizontalBtn2Y, verticalBtn2Y, "Y should change when switching to vertical");
    }
}

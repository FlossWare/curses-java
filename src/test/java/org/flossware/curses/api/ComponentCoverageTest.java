package org.flossware.curses.api;

import org.flossware.curses.testutil.BufferAssertions;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for Component to cover previously untested methods and branches.
 * Targets: setBorderChars, getBorderChars, paintShadow, setVisible/isVisible,
 * setRenderingStyle/getRenderingStyle, set3DEnabled/is3DEnabled,
 * setPreferredSize/getPreferredSize, writeCharToBuffer, writeStringToBuffer null text.
 */
@DisplayName("Component Coverage Tests")
class ComponentCoverageTest extends ComponentTestBase {

    private TestableComponent component;

    @BeforeEach
    void setUp() {
        component = new TestableComponent();
    }

    @Nested
    @DisplayName("Preferred Size")
    class PreferredSizeTests {

        @Test
        @DisplayName("should return default preferred size")
        void testDefaultPreferredSize() {
            Dimension pref = component.getPreferredSize();
            assertEquals(10, pref.width());
            assertEquals(1, pref.height());
        }

        @Test
        @DisplayName("should set and get preferred size")
        void testSetPreferredSize() {
            component.setPreferredSize(25, 15);
            Dimension pref = component.getPreferredSize();
            assertEquals(25, pref.width());
            assertEquals(15, pref.height());
        }
    }

    @Nested
    @DisplayName("Border Chars")
    class BorderCharsTests {

        @Test
        @DisplayName("should set valid 8-character border string")
        void testSetBorderCharsValid() {
            component.setBorderChars("+-+|+-+|");
            assertEquals("+-+|+-+|", component.getBorderChars());
        }

        @Test
        @DisplayName("should allow setting border chars to null for theme default")
        void testSetBorderCharsNull() {
            component.setBorderChars("╔═╗║╚═╝║");
            component.setBorderChars(null);
            // Should fall back to theme or hardcoded default
            assertNotNull(component.getBorderChars());
        }

        @Test
        @DisplayName("should throw for invalid border char length")
        void testSetBorderCharsInvalidLength() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                component.setBorderChars("abc"));
            assertTrue(ex.getMessage().contains("8 characters"));
        }

        @Test
        @DisplayName("should throw for too-long border chars")
        void testSetBorderCharsTooLong() {
            assertThrows(IllegalArgumentException.class, () ->
                component.setBorderChars("abcdefghij"));
        }

        @Test
        @DisplayName("should return custom border chars when set")
        void testGetBorderCharsCustom() {
            String custom = "ABCDEFGH";
            component.setBorderChars(custom);
            assertEquals(custom, component.getBorderChars());
        }

        @Test
        @DisplayName("should fall back to hardcoded default when no custom and no theme")
        void testGetBorderCharsFallback() {
            // No custom set, theme will provide default
            String borderChars = component.getBorderChars();
            assertNotNull(borderChars);
            assertEquals(8, borderChars.length());
        }
    }

    @Nested
    @DisplayName("Visibility")
    class VisibilityTests {

        @Test
        @DisplayName("should be visible by default")
        void testDefaultVisibility() {
            assertTrue(component.isVisible());
        }

        @Test
        @DisplayName("should set visible to false")
        void testSetInvisible() {
            component.setVisible(false);
            assertFalse(component.isVisible());
        }

        @Test
        @DisplayName("should set visible back to true")
        void testSetVisibleAgain() {
            component.setVisible(false);
            component.setVisible(true);
            assertTrue(component.isVisible());
        }

        @Test
        @DisplayName("should trigger repaint when visibility changes")
        void testVisibilityTriggersRepaint() {
            root.add(component);
            clearDirtyFlag();
            component.setVisible(false);
            assertDirtyFlagSet();
        }
    }

    @Nested
    @DisplayName("Rendering Style")
    class RenderingStyleTests {

        @Test
        @DisplayName("should default to FLAT rendering style")
        void testDefaultRenderingStyle() {
            assertEquals(RenderingStyle.FLAT, component.getRenderingStyle());
        }

        @Test
        @DisplayName("should set and get RAISED rendering style")
        void testSetRaised() {
            component.setRenderingStyle(RenderingStyle.RAISED);
            assertEquals(RenderingStyle.RAISED, component.getRenderingStyle());
        }

        @Test
        @DisplayName("should set and get SUNKEN rendering style")
        void testSetSunken() {
            component.setRenderingStyle(RenderingStyle.SUNKEN);
            assertEquals(RenderingStyle.SUNKEN, component.getRenderingStyle());
        }

        @Test
        @DisplayName("should set and get CUSTOM rendering style")
        void testSetCustom() {
            component.setRenderingStyle(RenderingStyle.CUSTOM);
            assertEquals(RenderingStyle.CUSTOM, component.getRenderingStyle());
        }

        @Test
        @DisplayName("should throw when setting null rendering style")
        void testSetNullRenderingStyle() {
            assertThrows(IllegalArgumentException.class, () ->
                component.setRenderingStyle(null));
        }

        @Test
        @DisplayName("should trigger repaint when rendering style changes")
        void testRenderingStyleTriggersRepaint() {
            root.add(component);
            clearDirtyFlag();
            component.setRenderingStyle(RenderingStyle.RAISED);
            assertDirtyFlagSet();
        }
    }

    @Nested
    @DisplayName("3D Enabled")
    class ThreeDTests {

        @Test
        @DisplayName("should default to 3D disabled")
        void testDefault3DDisabled() {
            assertFalse(component.is3DEnabled());
        }

        @Test
        @DisplayName("should enable 3D")
        void testEnable3D() {
            component.set3DEnabled(true);
            assertTrue(component.is3DEnabled());
        }

        @Test
        @DisplayName("should disable 3D after enabling")
        void testDisable3D() {
            component.set3DEnabled(true);
            component.set3DEnabled(false);
            assertFalse(component.is3DEnabled());
        }

        @Test
        @DisplayName("should trigger repaint when 3D state changes")
        void test3DTriggersRepaint() {
            root.add(component);
            clearDirtyFlag();
            component.set3DEnabled(true);
            assertDirtyFlagSet();
        }
    }

    @Nested
    @DisplayName("writeCharToBuffer")
    class WriteCharToBufferTests {

        @Test
        @DisplayName("should write character at valid position")
        void testWriteCharValid() {
            component.testWriteChar(buffer, 5, 3, 'X');
            assertEquals('X', buffer[3][5]);
        }

        @Test
        @DisplayName("should silently skip negative y")
        void testWriteCharNegativeY() {
            assertDoesNotThrow(() -> component.testWriteChar(buffer, 5, -1, 'X'));
        }

        @Test
        @DisplayName("should silently skip y beyond buffer")
        void testWriteCharYBeyondBuffer() {
            assertDoesNotThrow(() -> component.testWriteChar(buffer, 5, buffer.length, 'X'));
        }

        @Test
        @DisplayName("should silently skip negative x")
        void testWriteCharNegativeX() {
            assertDoesNotThrow(() -> component.testWriteChar(buffer, -1, 3, 'X'));
        }

        @Test
        @DisplayName("should silently skip x beyond buffer row")
        void testWriteCharXBeyondRow() {
            assertDoesNotThrow(() -> component.testWriteChar(buffer, buffer[0].length, 3, 'X'));
        }
    }

    @Nested
    @DisplayName("writeStringToBuffer")
    class WriteStringToBufferTests {

        @Test
        @DisplayName("should handle null text")
        void testWriteStringNull() {
            assertDoesNotThrow(() -> component.testWriteString(buffer, null, 5, 5));
        }

        @Test
        @DisplayName("should write string partially when x is negative")
        void testWriteStringNegativeX() {
            // String starts at x=-2, so first 2 chars are clipped
            component.testWriteString(buffer, "ABCD", -2, 5);
            assertEquals('C', buffer[5][0]);
            assertEquals('D', buffer[5][1]);
        }
    }

    @Nested
    @DisplayName("paintShadow")
    class PaintShadowTests {

        @Test
        @DisplayName("should not paint shadow when 3D is disabled")
        void testPaintShadowDisabled() {
            component.setLocation(5, 5);
            component.setSize(10, 5);
            // 3D disabled by default
            char[][] buf = BufferAssertions.createBuffer(40, 20);
            component.testPaintShadow(buf, null);
            // Buffer should be unchanged (all spaces)
            assertEquals(' ', buf[5][15]);
        }

        @Test
        @DisplayName("should not crash when 3D enabled but theme does not support 3D")
        void testPaintShadowNoTheme3D() {
            component.set3DEnabled(true);
            component.setLocation(5, 5);
            component.setSize(10, 5);
            char[][] buf = BufferAssertions.createBuffer(40, 20);
            // Default theme does not support 3D
            assertDoesNotThrow(() -> component.testPaintShadow(buf, null));
        }
    }

    @Nested
    @DisplayName("repaint")
    class RepaintTests {

        @Test
        @DisplayName("should handle repaint when root is not RootPane")
        void testRepaintNoRootPane() {
            // Component with a non-RootPane parent chain
            Container parent = new Panel();
            parent.add(component);
            // No RootPane in chain -- repaint should be a no-op, not crash
            assertDoesNotThrow(() -> component.repaint());
        }
    }

    @Nested
    @DisplayName("handleMouseEvent additional branches")
    class HandleMouseEventTests {

        @Test
        @DisplayName("should invoke all registered mouse listeners")
        void testMultipleListeners() {
            component.setLocation(0, 0);
            component.setSize(20, 20);
            int[] callCount = {0};
            MouseListener listener1 = event -> callCount[0]++;
            MouseListener listener2 = event -> callCount[0]++;
            component.addMouseListener(listener1);
            component.addMouseListener(listener2);

            org.flossware.curses.events.MouseEvent event =
                new org.flossware.curses.events.MouseEvent(5, 5, 1);
            component.handleMouseEvent(event);

            assertEquals(2, callCount[0]);
        }

        @Test
        @DisplayName("should handle event at exact boundary")
        void testBoundaryEvent() {
            component.setLocation(10, 10);
            component.setSize(5, 5);
            boolean[] called = {false};
            component.addMouseListener(event -> called[0] = true);

            // Last valid pixel is at (14, 14)
            org.flossware.curses.events.MouseEvent event =
                new org.flossware.curses.events.MouseEvent(14, 14, 1);
            assertTrue(component.handleMouseEvent(event));
            assertTrue(called[0]);
        }

        @Test
        @DisplayName("should not invoke removed listener")
        void testRemovedListenerNotInvoked() {
            component.setLocation(0, 0);
            component.setSize(20, 20);
            boolean[] called = {false};
            MouseListener listener = event -> called[0] = true;
            component.addMouseListener(listener);
            component.removeMouseListener(listener);

            org.flossware.curses.events.MouseEvent event =
                new org.flossware.curses.events.MouseEvent(5, 5, 1);
            boolean handled = component.handleMouseEvent(event);

            assertTrue(handled); // within bounds
            assertFalse(called[0]); // but listener was removed
        }
    }

    /**
     * Testable component that exposes protected methods for testing.
     */
    private static class TestableComponent extends Component {
        @Override
        public void paint(char[][] buffer) {
            // No-op
        }

        public void testWriteChar(char[][] buffer, int x, int y, char c) {
            writeCharToBuffer(buffer, x, y, c);
        }

        public void testWriteString(char[][] buffer, String text, int x, int y) {
            writeStringToBuffer(buffer, text, x, y);
        }

        public void testPaintShadow(char[][] buf, int[][] colorBuf) {
            paintShadow(buf, colorBuf);
        }
    }
}

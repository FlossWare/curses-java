package org.flossware.curses.api.widgets;

import org.flossware.curses.api.Separator;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Separator Tests")
class JSeparatorTest extends ComponentTestBase {
    private Separator widget;

    @BeforeEach
    void setUp() {
        widget = new Separator();
        widget.setSize(20, 5);
        widget.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(widget);
    }

    @Test
    @DisplayName("should default to horizontal orientation")
    void testDefaultOrientation() {
        Separator separator = new Separator();
        separator.setSize(10, 1);
        separator.setLocation(0, 0);

        assertDoesNotThrow(() -> separator.paint(buffer));

        // Check that horizontal line is drawn
        boolean hasHorizontalChar = false;
        for (int x = 0; x < 10; x++) {
            if (buffer[0][x] == '-') {
                hasHorizontalChar = true;
                break;
            }
        }
        assertTrue(hasHorizontalChar);
    }

    @Test
    @DisplayName("should create with vertical orientation")
    void testVerticalOrientation() {
        Separator separator = new Separator(Separator.VERTICAL);
        separator.setSize(1, 10);
        separator.setLocation(0, 0);

        separator.paint(buffer);

        // Check that vertical line is drawn
        boolean hasVerticalChar = false;
        for (int y = 0; y < 10; y++) {
            if (buffer[y][0] == '|') {
                hasVerticalChar = true;
                break;
            }
        }
        assertTrue(hasVerticalChar);
    }

    @Test
    @DisplayName("should render without errors")
    void testRendering() {
        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should be added to parent")
    void testAddToParent() {
        root.add(widget);
        assertTrue(root.getChildren().contains(widget));
    }

    @Test
    @DisplayName("should set orientation and repaint")
    void testSetOrientation() {
        root.add(widget);
        clearDirtyFlag();

        widget.setOrientation(Separator.VERTICAL);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should render horizontal line across width")
    void testHorizontalRendering() {
        widget.setOrientation(Separator.HORIZONTAL);
        widget.setLocation(5, 3);
        widget.setSize(10, 1);

        widget.paint(buffer);

        // Check all positions in the horizontal line
        for (int x = 0; x < 10; x++) {
            assertEquals('-', buffer[3][5 + x]);
        }
    }

    @Test
    @DisplayName("should render vertical line across height")
    void testVerticalRendering() {
        widget.setOrientation(Separator.VERTICAL);
        widget.setLocation(5, 3);
        widget.setSize(1, 10);

        widget.paint(buffer);

        // Check all positions in the vertical line
        for (int y = 0; y < 10; y++) {
            assertEquals('|', buffer[3 + y][5]);
        }
    }

    @Test
    @DisplayName("should have correct constant values")
    void testConstants() {
        assertEquals(0, Separator.HORIZONTAL);
        assertEquals(1, Separator.VERTICAL);
    }
}

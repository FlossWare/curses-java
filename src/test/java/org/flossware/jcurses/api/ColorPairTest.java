package org.flossware.jcurses.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ColorPair Tests")
class ColorPairTest {

    @Test
    @DisplayName("should create color pair with foreground and background")
    void testCreation() {
        ColorPair pair = new ColorPair(Color.RED, Color.BLACK);

        assertEquals(Color.RED, pair.foreground());
        assertEquals(Color.BLACK, pair.background());
        assertEquals(0, pair.pairNumber());
    }

    @Test
    @DisplayName("should create color pair with pair number")
    void testCreationWithPairNumber() {
        ColorPair pair = new ColorPair(Color.GREEN, Color.BLUE, 5);

        assertEquals(Color.GREEN, pair.foreground());
        assertEquals(Color.BLUE, pair.background());
        assertEquals(5, pair.pairNumber());
    }

    @Test
    @DisplayName("should indicate if initialized")
    void testIsInitialized() {
        ColorPair uninitialized = new ColorPair(Color.RED, Color.BLACK);
        ColorPair initialized = new ColorPair(Color.RED, Color.BLACK, 1);

        assertFalse(uninitialized.isInitialized());
        assertTrue(initialized.isInitialized());
    }

    @Test
    @DisplayName("should create new instance with pair number")
    void testWithPairNumber() {
        ColorPair original = new ColorPair(Color.YELLOW, Color.MAGENTA);
        ColorPair withNumber = original.withPairNumber(3);

        assertEquals(Color.YELLOW, withNumber.foreground());
        assertEquals(Color.MAGENTA, withNumber.background());
        assertEquals(3, withNumber.pairNumber());

        // Original should be unchanged
        assertEquals(0, original.pairNumber());
    }

    @Test
    @DisplayName("should have predefined color pairs")
    void testPredefinedPairs() {
        assertNotNull(ColorPair.DEFAULT);
        assertNotNull(ColorPair.INVERTED);
        assertNotNull(ColorPair.RED_ON_BLACK);
        assertNotNull(ColorPair.GREEN_ON_BLACK);
        assertNotNull(ColorPair.YELLOW_ON_BLACK);
        assertNotNull(ColorPair.BLUE_ON_BLACK);
        assertNotNull(ColorPair.CYAN_ON_BLACK);
        assertNotNull(ColorPair.MAGENTA_ON_BLACK);
    }

    @Test
    @DisplayName("should have correct colors for DEFAULT pair")
    void testDefaultPair() {
        assertEquals(Color.WHITE, ColorPair.DEFAULT.foreground());
        assertEquals(Color.BLACK, ColorPair.DEFAULT.background());
        assertEquals(0, ColorPair.DEFAULT.pairNumber());
    }

    @Test
    @DisplayName("should have correct colors for INVERTED pair")
    void testInvertedPair() {
        assertEquals(Color.BLACK, ColorPair.INVERTED.foreground());
        assertEquals(Color.WHITE, ColorPair.INVERTED.background());
    }

    @Test
    @DisplayName("should support record equality")
    void testEquality() {
        ColorPair pair1 = new ColorPair(Color.RED, Color.BLACK, 1);
        ColorPair pair2 = new ColorPair(Color.RED, Color.BLACK, 1);
        ColorPair pair3 = new ColorPair(Color.RED, Color.BLACK, 2);

        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
    }
}

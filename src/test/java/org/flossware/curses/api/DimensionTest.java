package org.flossware.curses.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dimension Tests")
class DimensionTest {

    @Test
    @DisplayName("should create with valid positive dimensions")
    void testValidPositiveDimensions() {
        Dimension dim = new Dimension(10, 20);

        assertEquals(10, dim.width());
        assertEquals(20, dim.height());
    }

    @Test
    @DisplayName("should allow zero width and height")
    void testZeroDimensions() {
        Dimension dim = new Dimension(0, 0);

        assertEquals(0, dim.width());
        assertEquals(0, dim.height());
    }

    @Test
    @DisplayName("should allow zero width with positive height")
    void testZeroWidthPositiveHeight() {
        Dimension dim = new Dimension(0, 5);

        assertEquals(0, dim.width());
        assertEquals(5, dim.height());
    }

    @Test
    @DisplayName("should allow positive width with zero height")
    void testPositiveWidthZeroHeight() {
        Dimension dim = new Dimension(5, 0);

        assertEquals(5, dim.width());
        assertEquals(0, dim.height());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException for negative width")
    void testNegativeWidthThrows() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new Dimension(-1, 10)
        );
        assertEquals("Width and height must be non-negative", ex.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException for negative height")
    void testNegativeHeightThrows() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new Dimension(10, -1)
        );
        assertEquals("Width and height must be non-negative", ex.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when both dimensions are negative")
    void testBothNegativeThrows() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new Dimension(-5, -3)
        );
        assertEquals("Width and height must be non-negative", ex.getMessage());
    }

    @Test
    @DisplayName("should return correct toString format")
    void testToStringFormat() {
        Dimension dim = new Dimension(42, 17);

        assertEquals("Dimension[width=42, height=17]", dim.toString());
    }

    @Test
    @DisplayName("should return correct toString for zero dimensions")
    void testToStringZeroDimensions() {
        Dimension dim = new Dimension(0, 0);

        assertEquals("Dimension[width=0, height=0]", dim.toString());
    }

    @Test
    @DisplayName("should be equal to another Dimension with same values")
    void testEqualsWithSameValues() {
        Dimension dim1 = new Dimension(10, 20);
        Dimension dim2 = new Dimension(10, 20);

        assertEquals(dim1, dim2);
    }

    @Test
    @DisplayName("should not be equal to Dimension with different width")
    void testNotEqualsWithDifferentWidth() {
        Dimension dim1 = new Dimension(10, 20);
        Dimension dim2 = new Dimension(11, 20);

        assertNotEquals(dim1, dim2);
    }

    @Test
    @DisplayName("should not be equal to Dimension with different height")
    void testNotEqualsWithDifferentHeight() {
        Dimension dim1 = new Dimension(10, 20);
        Dimension dim2 = new Dimension(10, 21);

        assertNotEquals(dim1, dim2);
    }

    @Test
    @DisplayName("should have same hashCode for equal Dimensions")
    void testHashCodeConsistency() {
        Dimension dim1 = new Dimension(10, 20);
        Dimension dim2 = new Dimension(10, 20);

        assertEquals(dim1.hashCode(), dim2.hashCode());
    }

    @Test
    @DisplayName("should have different hashCode for different Dimensions")
    void testHashCodeDifference() {
        Dimension dim1 = new Dimension(10, 20);
        Dimension dim2 = new Dimension(20, 10);

        assertNotEquals(dim1.hashCode(), dim2.hashCode());
    }
}

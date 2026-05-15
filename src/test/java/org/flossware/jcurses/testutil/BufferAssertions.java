package org.flossware.jcurses.testutil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Custom assertions and utilities for testing character buffer rendering.
 */
public class BufferAssertions {

    /**
     * Assert that the buffer contains the expected string at the given coordinates.
     */
    public static void assertBufferContains(char[][] buffer, int x, int y, String expected) {
        if (y >= buffer.length) {
            fail("Y coordinate " + y + " is out of bounds (buffer height: " + buffer.length + ")");
        }
        if (x >= buffer[y].length) {
            fail("X coordinate " + x + " is out of bounds (buffer width: " + buffer[y].length + ")");
        }

        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < expected.length(); i++) {
            if (x + i < buffer[y].length) {
                actual.append(buffer[y][x + i]);
            }
        }
        assertEquals(expected, actual.toString(),
            "Expected '" + expected + "' at (" + x + "," + y + ") but found '" + actual + "'");
    }

    /**
     * Assert that a buffer row equals the expected string (trimmed).
     */
    public static void assertBufferRowEquals(char[][] buffer, int row, String expected) {
        if (row >= buffer.length) {
            fail("Row " + row + " is out of bounds (buffer height: " + buffer.length + ")");
        }
        String actual = new String(buffer[row]).trim();
        assertEquals(expected, actual, "Row " + row + " does not match");
    }

    /**
     * Assert that a buffer row contains the expected string.
     */
    public static void assertBufferRowContains(char[][] buffer, int row, String expected) {
        if (row >= buffer.length) {
            fail("Row " + row + " is out of bounds (buffer height: " + buffer.length + ")");
        }
        String actual = new String(buffer[row]);
        assertTrue(actual.contains(expected),
            "Row " + row + " does not contain '" + expected + "'. Actual: '" + actual + "'");
    }

    /**
     * Create a buffer with the specified dimensions, filled with spaces.
     */
    public static char[][] createBuffer(int width, int height) {
        char[][] buffer = new char[height][width];
        clearBuffer(buffer);
        return buffer;
    }

    /**
     * Clear a buffer by filling it with spaces.
     */
    public static void clearBuffer(char[][] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                buffer[i][j] = ' ';
            }
        }
    }

    /**
     * Extract a rectangular region from the buffer as a string.
     */
    public static String extractRegion(char[][] buffer, int x, int y, int width, int height) {
        StringBuilder sb = new StringBuilder();
        for (int row = y; row < y + height && row < buffer.length; row++) {
            for (int col = x; col < x + width && col < buffer[row].length; col++) {
                sb.append(buffer[row][col]);
            }
            if (row < y + height - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Extract a single row from the buffer.
     */
    public static String extractRow(char[][] buffer, int row) {
        if (row >= buffer.length) {
            return "";
        }
        return new String(buffer[row]);
    }

    /**
     * Print the buffer to stdout for debugging purposes.
     */
    public static void printBuffer(char[][] buffer) {
        System.out.println("=== Buffer Contents ===");
        for (int i = 0; i < buffer.length; i++) {
            System.out.printf("%2d: [%s]\n", i, new String(buffer[i]));
        }
        System.out.println("======================");
    }
}

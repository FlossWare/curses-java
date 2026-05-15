package org.flossware.jcurses.api;

/**
 * Record representing a foreground/background color pair.
 *
 * @param foreground The foreground color
 * @param background The background color
 * @param pairNumber The ncurses pair number (1-based, 0 is reserved for default)
 */
public record ColorPair(Color foreground, Color background, int pairNumber) {

    /**
     * Creates a color pair with automatic pair numbering.
     */
    public ColorPair(Color foreground, Color background) {
        this(foreground, background, 0);  // 0 means not yet initialized
    }

    /**
     * Returns true if this color pair has been initialized with ncurses.
     */
    public boolean isInitialized() {
        return pairNumber > 0;
    }

    /**
     * Creates a new ColorPair with the specified pair number (used after ncurses initialization).
     */
    public ColorPair withPairNumber(int pairNumber) {
        return new ColorPair(foreground, background, pairNumber);
    }

    // Standard color pairs
    public static final ColorPair DEFAULT = new ColorPair(Color.WHITE, Color.BLACK, 0);
    public static final ColorPair INVERTED = new ColorPair(Color.BLACK, Color.WHITE);
    public static final ColorPair RED_ON_BLACK = new ColorPair(Color.RED, Color.BLACK);
    public static final ColorPair GREEN_ON_BLACK = new ColorPair(Color.GREEN, Color.BLACK);
    public static final ColorPair YELLOW_ON_BLACK = new ColorPair(Color.YELLOW, Color.BLACK);
    public static final ColorPair BLUE_ON_BLACK = new ColorPair(Color.BLUE, Color.BLACK);
    public static final ColorPair CYAN_ON_BLACK = new ColorPair(Color.CYAN, Color.BLACK);
    public static final ColorPair MAGENTA_ON_BLACK = new ColorPair(Color.MAGENTA, Color.BLACK);
}

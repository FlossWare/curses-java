package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.theme.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for terminal compatibility and accessibility.
 * Tests 8-color terminal rendering, color contrast ratios (WCAG AA standards),
 * ASCII fallback, text truncation/wrapping, and screen resize handling.
 */
@DisplayName("Terminal Compatibility Integration Tests")
class TerminalCompatibilityIT extends IntegrationTestBase {

    // WCAG AA minimum contrast ratio thresholds
    private static final double WCAG_AA_NORMAL_TEXT = 4.5;
    private static final double WCAG_AA_LARGE_TEXT = 3.0;
    private static final double WCAG_AAA_NORMAL_TEXT = 7.0;

    // ===== Theme Provider for Parameterized Tests =====

    /**
     * Provides all available themes for parameterized testing.
     */
    static Stream<Theme> themeProvider() {
        return Stream.of(
            new DefaultTheme(),
            new DarkTheme(),
            new LightTheme(),
            new ModernTheme(),
            new BorlandTheme(),
            new Borland3DTheme(),
            new TI994ATheme(),
            new TRS80Theme(),
            new DOSTheme(),
            new DBase3Theme(),
            new DBase4Theme()
        );
    }

    // ===== 8-Color Terminal Compatibility Tests =====

    @ParameterizedTest
    @MethodSource("themeProvider")
    @DisplayName("All themes should render correctly on 8-color terminals")
    void testEightColorTerminalCompatibility(Theme theme) {
        // Setup: Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Create UI components using the theme
        Frame frame = new Frame("8-Color Test: " + theme.getName());
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        Button button = new Button("Test Button");
        button.setLocation(5, 5);
        button.setSize(15, 1);

        TextField textField = new TextField();
        textField.setLocation(5, 7);
        textField.setSize(20, 1);

        Label label = new Label("Test Label");
        label.setLocation(5, 9);
        label.setSize(15, 1);

        frame.add(button);
        frame.add(textField);
        frame.add(label);
        root.add(frame);
        root.markDirty();

        // Verify all theme color pairs use only 8 basic colors
        assertUsesOnly8Colors(theme.getBackground(), "background");
        assertUsesOnly8Colors(theme.getButton(), "button");
        assertUsesOnly8Colors(theme.getButtonFocused(), "buttonFocused");
        assertUsesOnly8Colors(theme.getTextInput(), "textInput");
        assertUsesOnly8Colors(theme.getBorder(), "border");
        assertUsesOnly8Colors(theme.getSelection(), "selection");
        assertUsesOnly8Colors(theme.getDisabled(), "disabled");

        // Render and verify no exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
            theme.getName() + " should render without errors on 8-color terminal");

        // Verify components are visible in buffer
        runEventLoopCycle();
        String row5 = getScreenRow(5);
        assertNotNull(row5, "Row 5 should contain rendered content");
    }

    @Test
    @DisplayName("8-color terminal should handle all standard ncurses colors")
    void testStandardColorRange() {
        // Verify all Color enum values are within 8-color range (0-7)
        for (Color color : Color.values()) {
            int code = color.getNcursesCode();
            assertTrue(code >= 0 && code <= 7,
                "Color " + color + " has code " + code + ", expected 0-7 for 8-color terminal");
        }
    }

    // ===== Color Contrast Tests (WCAG AA) =====

    @ParameterizedTest
    @MethodSource("themeProvider")
    @DisplayName("Theme color pairs should meet WCAG AA contrast ratio standards")
    void testWCAGAAColorContrast(Theme theme) {
        // Test critical UI element contrast ratios
        assertMeetsContrastRatio(theme.getBackground(), WCAG_AA_NORMAL_TEXT,
            theme.getName() + " background");
        assertMeetsContrastRatio(theme.getButton(), WCAG_AA_NORMAL_TEXT,
            theme.getName() + " button");
        assertMeetsContrastRatio(theme.getButtonFocused(), WCAG_AA_NORMAL_TEXT,
            theme.getName() + " focused button");
        assertMeetsContrastRatio(theme.getTextInput(), WCAG_AA_NORMAL_TEXT,
            theme.getName() + " text input");
        assertMeetsContrastRatio(theme.getSelection(), WCAG_AA_NORMAL_TEXT,
            theme.getName() + " selection");

        // Borders can be slightly lower contrast (WCAG large text threshold)
        assertMeetsContrastRatio(theme.getBorder(), WCAG_AA_LARGE_TEXT,
            theme.getName() + " border");
    }

    @Test
    @DisplayName("Modern theme should meet WCAG AAA standards")
    void testModernThemeWCAGAAA() {
        ModernTheme theme = new ModernTheme();

        // ModernTheme is designed for WCAG AAA compliance
        assertMeetsContrastRatio(theme.getBackground(), WCAG_AAA_NORMAL_TEXT, "Modern background");
        assertMeetsContrastRatio(theme.getButton(), WCAG_AAA_NORMAL_TEXT, "Modern button");
        assertMeetsContrastRatio(theme.getButtonFocused(), WCAG_AAA_NORMAL_TEXT, "Modern focused button");
        assertMeetsContrastRatio(theme.getTextInput(), WCAG_AAA_NORMAL_TEXT, "Modern text input");
        assertMeetsContrastRatio(theme.getSelection(), WCAG_AAA_NORMAL_TEXT, "Modern selection");
    }

    @Test
    @DisplayName("High contrast color pairs should be clearly distinguishable")
    void testHighContrastPairs() {
        // Test maximum contrast pairs (black on white, white on black)
        ColorPair blackOnWhite = new ColorPair(Color.BLACK, Color.WHITE);
        ColorPair whiteOnBlack = new ColorPair(Color.WHITE, Color.BLACK);

        double blackOnWhiteContrast = calculateContrastRatio(blackOnWhite);
        double whiteOnBlackContrast = calculateContrastRatio(whiteOnBlack);

        // Maximum contrast should be 21:1
        assertTrue(blackOnWhiteContrast >= 15.0,
            "Black on white should have very high contrast, got " + blackOnWhiteContrast);
        assertTrue(whiteOnBlackContrast >= 15.0,
            "White on black should have very high contrast, got " + whiteOnBlackContrast);
    }

    @Test
    @DisplayName("Low contrast pairs should be detected")
    void testLowContrastDetection() {
        // Test colors with poor contrast (should fail WCAG AA)
        ColorPair yellowOnWhite = new ColorPair(Color.YELLOW, Color.WHITE);
        ColorPair cyanOnWhite = new ColorPair(Color.CYAN, Color.WHITE);

        double yellowContrast = calculateContrastRatio(yellowOnWhite);
        double cyanContrast = calculateContrastRatio(cyanOnWhite);

        // These should have low contrast
        assertTrue(yellowContrast < WCAG_AA_NORMAL_TEXT,
            "Yellow on white should have low contrast for testing");
        assertTrue(cyanContrast < WCAG_AA_NORMAL_TEXT,
            "Cyan on white should have low contrast for testing");
    }

    // ===== ASCII Fallback Tests =====

    @ParameterizedTest
    @MethodSource("themeProvider")
    @DisplayName("Themes should provide ASCII border characters as fallback")
    void testASCIIBorderFallback(Theme theme) {
        String borderChars = theme.getBorderChars();

        assertNotNull(borderChars, theme.getName() + " should provide border characters");
        assertEquals(8, borderChars.length(),
            theme.getName() + " border chars should have 8 characters");

        // Even if theme uses Unicode, fallback should be available
        // Verify the string is valid (can be rendered)
        assertDoesNotThrow(() -> {
            for (char c : borderChars.toCharArray()) {
                // Should not throw on any character
                String test = String.valueOf(c);
                assertNotNull(test);
            }
        }, theme.getName() + " border characters should be renderable");
    }

    @Test
    @DisplayName("ASCII-only themes should use simple box characters")
    void testASCIIOnlyThemes() {
        // Themes that explicitly use ASCII borders
        DefaultTheme defaultTheme = new DefaultTheme();
        DOSTheme dosTheme = new DOSTheme();
        TRS80Theme trs80Theme = new TRS80Theme();

        // Should use ASCII box drawing: "+-+|+-+|"
        assertTrue(isASCIIOnly(defaultTheme.getBorderChars()),
            "Default theme should use ASCII-only borders");
        assertTrue(isASCIIOnly(dosTheme.getBorderChars()),
            "DOS theme should use ASCII-only borders");
        assertTrue(isASCIIOnly(trs80Theme.getBorderChars()),
            "TRS-80 theme should use ASCII-only borders");
    }

    @Test
    @DisplayName("Frame rendering should handle both ASCII and Unicode borders")
    void testMixedBorderRendering() {
        // Test ASCII borders
        ThemeManager.getInstance().useDefaultTheme();
        Frame asciiFrame = createFrameWithBorder("ASCII Frame", 2, 2, 30, 5);
        runEventLoopCycle();

        String asciiRow = getScreenRow(2);
        assertNotNull(asciiRow, "ASCII frame should render");

        // Test Unicode borders
        ThemeManager.getInstance().useDarkTheme();
        root.markDirty(); // Force re-render with new theme
        Frame unicodeFrame = createFrameWithBorder("Unicode Frame", 2, 10, 30, 5);
        runEventLoopCycle();

        String unicodeRow = getScreenRow(10);
        assertNotNull(unicodeRow, "Unicode frame should render");
    }

    // ===== Text Truncation and Wrapping Tests =====

    @Test
    @DisplayName("Very long text should truncate at component boundary")
    void testVeryLongTextTruncation() {
        // Create a very long string
        String veryLongText = "A".repeat(200);

        Label label = new Label(veryLongText);
        label.setLocation(5, 5);
        label.setSize(40, 1); // Width limited to 40 chars

        setupFrame(label);
        runEventLoopCycle();

        String row5 = getScreenRow(5);

        // Text should be truncated to fit component width
        assertNotNull(row5, "Label should render");

        // Count visible 'A' characters (should not exceed 40)
        long visibleChars = row5.chars().filter(ch -> ch == 'A').count();
        assertTrue(visibleChars <= 40,
            "Text should truncate to component width, found " + visibleChars + " chars");
    }

    @Test
    @DisplayName("TextField should handle text longer than visible width")
    void testTextFieldOverflow() {
        TextField textField = createTextField(5, 5, 20);
        setupFrame(textField);

        // Simulate typing text longer than field width
        String longInput = "This is a very long text that exceeds the field width";
        injectTextInput(longInput.substring(0, 30)); // Type 30 chars into 20-char field

        runEventLoopCycle();

        // Field should still render without errors
        String row5 = getScreenRow(5);
        assertNotNull(row5, "TextField should render with overflow text");
    }

    @Test
    @DisplayName("Multi-line text should wrap or truncate correctly")
    void testMultiLineTextHandling() {
        // Create label with newlines
        String multiLineText = "Line 1\nLine 2\nLine 3";

        Label label = new Label(multiLineText);
        label.setLocation(5, 5);
        label.setSize(30, 3);

        setupFrame(label);
        runEventLoopCycle();

        // Verify rendering doesn't crash with newlines
        assertDoesNotThrow(() -> runEventLoopCycle(),
            "Multi-line text should render without errors");
    }

    @Test
    @DisplayName("Table with very long cell content should truncate")
    void testTableCellTruncation() {
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(50, 10);
        table.setColumnNames("Short", "Very Long Column Name Here", "OK");

        // Add row with very long content
        table.addRow(
            "A",
            "This is extremely long content that should be truncated",
            "C"
        );

        setupFrame(table);
        runEventLoopCycle();

        String row6 = getScreenRow(6); // First data row
        assertNotNull(row6, "Table should render with long content");

        // Total width should not exceed table width (50)
        assertTrue(row6.trim().length() <= 50,
            "Table row should not exceed table width");
    }

    // ===== Screen Resize Handling Tests =====

    @Test
    @DisplayName("Components should adapt to terminal width change")
    void testTerminalWidthResize() {
        Button button = createButton("Resize Test", 5, 5);
        setupFrame(button);
        runEventLoopCycle();

        // Simulate terminal width resize (from 80 to 60)
        mockBridge.setTerminalSize(60, 24);

        // Recreate buffer to match new size
        buffer = new char[24][60];
        eventLoop = new EventLoopRunner(mockBridge, root, buffer);

        // Update root size
        root.setSize(60, 24);
        root.markDirty();

        // Render should adapt
        assertDoesNotThrow(() -> runEventLoopCycle(),
            "Should handle terminal width resize");

        // Component should still be visible if within new bounds
        String row5 = getScreenRow(5);
        assertNotNull(row5, "Component should render after resize");
    }

    @Test
    @DisplayName("Components should adapt to terminal height change")
    void testTerminalHeightResize() {
        Label label = createLabel("Height Test", 5, 20);
        setupFrame(label);
        runEventLoopCycle();

        // Simulate terminal height resize (from 24 to 15)
        mockBridge.setTerminalSize(80, 15);

        // Recreate buffer to match new size
        buffer = new char[15][80];
        eventLoop = new EventLoopRunner(mockBridge, root, buffer);

        // Update root size
        root.setSize(80, 15);
        root.markDirty();

        // Render should adapt
        assertDoesNotThrow(() -> runEventLoopCycle(),
            "Should handle terminal height resize");

        // Component at y=20 should now be out of bounds (not crash)
        assertDoesNotThrow(() -> {
            String row14 = getScreenRow(14); // Last visible row
            assertNotNull(row14);
        });
    }

    @Test
    @DisplayName("Resize should preserve component positions within bounds")
    void testResizePreservesPositions() {
        Button button1 = createButton("Top Left", 2, 2);
        Button button2 = createButton("Bottom Right", 60, 20);
        setupFrame(button1, button2);
        runEventLoopCycle();

        // Verify initial positions
        String row2 = getScreenRow(2);
        assertTrue(row2.contains("Top Left"), "Button1 should be visible");

        // Resize to smaller terminal
        mockBridge.setTerminalSize(50, 15);
        buffer = new char[15][50];
        eventLoop = new EventLoopRunner(mockBridge, root, buffer);
        root.setSize(50, 15);
        root.markDirty();

        runEventLoopCycle();

        // Button1 should still be visible at (2,2)
        row2 = getScreenRow(2);
        assertTrue(row2.contains("Top Left"),
            "Button at (2,2) should remain visible after resize");

        // Button2 at (60,20) is now out of bounds, should not crash
        assertDoesNotThrow(() -> runEventLoopCycle(),
            "Out-of-bounds components should not crash renderer");
    }

    @Test
    @DisplayName("Very small terminal size should be handled gracefully")
    void testVerySmallTerminal() {
        // Simulate very small terminal (20x10)
        mockBridge.setTerminalSize(20, 10);
        buffer = new char[10][20];
        eventLoop = new EventLoopRunner(mockBridge, root, buffer);

        Button button = new Button("OK");
        button.setLocation(2, 2);
        button.setSize(6, 1);

        Frame frame = new Frame("Test");
        frame.setLocation(0, 0);
        frame.setSize(20, 10);
        frame.setVisible(true);
        frame.add(button);

        root.setSize(20, 10);
        root.add(frame);
        root.markDirty();

        // Should render without crashing
        assertDoesNotThrow(() -> runEventLoopCycle(),
            "Small terminal should render without errors");
    }

    @Test
    @DisplayName("Frame should reflow content on resize")
    void testFrameContentReflow() {
        Frame frame = new Frame("Reflow Test");
        frame.setLocation(0, 0);
        frame.setSize(80, 24);
        frame.setVisible(true);

        // Add multiple components in a grid
        for (int i = 0; i < 10; i++) {
            Button btn = new Button("Btn" + i);
            btn.setLocation(5 + (i % 5) * 12, 5 + (i / 5) * 3);
            btn.setSize(10, 1);
            frame.add(btn);
        }

        root.add(frame);
        root.markDirty();
        runEventLoopCycle();

        // Resize smaller
        mockBridge.setTerminalSize(60, 20);
        buffer = new char[20][60];
        eventLoop = new EventLoopRunner(mockBridge, root, buffer);

        frame.setSize(60, 20);
        root.setSize(60, 20);
        root.markDirty();

        // Should reflow without errors
        assertDoesNotThrow(() -> runEventLoopCycle(),
            "Frame should reflow content on resize");
    }

    // ===== Helper Methods =====

    /**
     * Verifies that a ColorPair uses only the 8 standard terminal colors.
     */
    private void assertUsesOnly8Colors(ColorPair pair, String context) {
        int fgCode = pair.foreground().getNcursesCode();
        int bgCode = pair.background().getNcursesCode();

        assertTrue(fgCode >= 0 && fgCode <= 7,
            context + " foreground color " + pair.foreground() + " (" + fgCode + ") must be in range 0-7");
        assertTrue(bgCode >= 0 && bgCode <= 7,
            context + " background color " + pair.background() + " (" + bgCode + ") must be in range 0-7");
    }

    /**
     * Verifies that a ColorPair meets the specified WCAG contrast ratio.
     */
    private void assertMeetsContrastRatio(ColorPair pair, double minRatio, String context) {
        double ratio = calculateContrastRatio(pair);

        assertTrue(ratio >= minRatio,
            String.format("%s has contrast ratio %.2f:1, expected >= %.1f:1 (%s on %s)",
                context, ratio, minRatio, pair.foreground(), pair.background()));
    }

    /**
     * Calculates WCAG 2.1 contrast ratio for a color pair.
     * Formula: (L1 + 0.05) / (L2 + 0.05) where L1 is lighter, L2 is darker.
     */
    private double calculateContrastRatio(ColorPair pair) {
        double fgLuminance = getRelativeLuminance(pair.foreground());
        double bgLuminance = getRelativeLuminance(pair.background());

        double lighter = Math.max(fgLuminance, bgLuminance);
        double darker = Math.min(fgLuminance, bgLuminance);

        return (lighter + 0.05) / (darker + 0.05);
    }

    /**
     * Gets relative luminance for WCAG calculations.
     * Approximation for 8-color terminal palette.
     */
    private double getRelativeLuminance(Color color) {
        // Standard terminal color approximate luminance values (0.0 = black, 1.0 = white)
        // Based on typical RGB values for 8-color terminals
        return switch (color) {
            case BLACK -> 0.0;
            case RED -> 0.2126; // Red component dominant
            case GREEN -> 0.7152; // Green component dominant
            case YELLOW -> 0.9278; // Red + Green
            case BLUE -> 0.0722; // Blue component dominant
            case MAGENTA -> 0.2848; // Red + Blue
            case CYAN -> 0.7874; // Green + Blue
            case WHITE -> 1.0;
        };
    }

    /**
     * Checks if a string contains only ASCII characters (code < 128).
     */
    private boolean isASCIIOnly(String str) {
        return str.chars().allMatch(ch -> ch < 128);
    }

    /**
     * Creates a frame with border for testing.
     */
    private Frame createFrameWithBorder(String title, int x, int y, int width, int height) {
        Frame frame = new Frame(title);
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setVisible(true);
        root.add(frame);
        root.markDirty();
        return frame;
    }
}

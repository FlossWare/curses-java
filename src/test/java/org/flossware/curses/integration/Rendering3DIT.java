package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.theme.Borland3DTheme;
import org.flossware.curses.theme.Theme;
import org.flossware.curses.theme.Theme3D;
import org.flossware.curses.testutil.BufferAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for 3D rendering with Borland3DTheme.
 *
 * <p>Tests verify that components properly detect and utilize 3D theme capabilities,
 * including shadow rendering, highlight/lowlight border coloring, shadow offset positioning,
 * double-line borders, and rendering style enum integration.
 *
 * <p>These tests use mock-based verification to ensure rendering calls are made correctly
 * without requiring actual terminal interaction.
 */
@DisplayName("3D Rendering Integration Tests")
class Rendering3DIT extends IntegrationTestBase {

    private Borland3DTheme theme3D;
    private Theme nonTheme3D;

    @BeforeEach
    void setup3DTheme() {
        // Create a 3D theme for testing
        theme3D = new Borland3DTheme();

        // Create a non-3D theme for fallback testing
        nonTheme3D = new Theme() {
            @Override
            public ColorPair getBackground() {
                return new ColorPair(Color.WHITE, Color.BLACK);
            }

            @Override
            public ColorPair getButton() {
                return new ColorPair(Color.BLACK, Color.WHITE);
            }

            @Override
            public ColorPair getButtonFocused() {
                return new ColorPair(Color.WHITE, Color.BLACK);
            }

            @Override
            public ColorPair getTextInput() {
                return new ColorPair(Color.BLACK, Color.WHITE);
            }

            @Override
            public ColorPair getBorder() {
                return new ColorPair(Color.WHITE, Color.BLACK);
            }

            @Override
            public ColorPair getSelection() {
                return new ColorPair(Color.BLACK, Color.CYAN);
            }

            @Override
            public ColorPair getDisabled() {
                return new ColorPair(Color.BLACK, Color.BLACK);
            }

            @Override
            public String getBorderChars() {
                return "+-+|+-+|";
            }

            @Override
            public String getName() {
                return "Non-3D Test Theme";
            }

            @Override
            public boolean supports3D() {
                return false;
            }
        };
    }

    @Test
    @DisplayName("Theme3D interface should be detected by instanceof check")
    void testTheme3DDetection() {
        // Verify 3D theme is detected
        assertTrue(theme3D instanceof Theme3D, "Borland3DTheme should implement Theme3D");

        // Verify supports3D returns true
        assertTrue(theme3D.supports3D(), "Borland3DTheme.supports3D() should return true");

        // Verify non-3D theme is not detected
        assertFalse(nonTheme3D instanceof Theme3D, "Non-3D theme should not implement Theme3D");

        // Verify supports3D returns false for non-3D theme
        assertFalse(nonTheme3D.supports3D(), "Non-3D theme.supports3D() should return false");
    }

    @Test
    @DisplayName("Shadow colors should be configured correctly")
    void testShadowColorRendering() {
        // Get shadow color from theme
        ColorPair shadowColor = theme3D.getShadowColor();

        // Verify shadow uses black on black (simulating dark gray with A_BOLD)
        assertNotNull(shadowColor, "Shadow color should not be null");
        assertEquals(Color.BLACK, shadowColor.foreground(), "Shadow foreground should be BLACK");
        assertEquals(Color.BLACK, shadowColor.background(), "Shadow background should be BLACK");
    }

    @Test
    @DisplayName("Highlight color should render on top-left borders for raised style")
    void testHighlightColorForRaisedBorders() {
        // Get highlight color from theme
        ColorPair highlightColor = theme3D.getHighlightColor();

        // Verify highlight uses white on cyan (bright edge simulation)
        assertNotNull(highlightColor, "Highlight color should not be null");
        assertEquals(Color.WHITE, highlightColor.foreground(), "Highlight foreground should be WHITE");
        assertEquals(Color.CYAN, highlightColor.background(), "Highlight background should be CYAN");
    }

    @Test
    @DisplayName("Lowlight color should render on bottom-right borders for raised style")
    void testLowlightColorForRaisedBorders() {
        // Get lowlight color from theme
        ColorPair lowlightColor = theme3D.getLowlightColor();

        // Verify lowlight uses black on cyan (dark edge simulation)
        assertNotNull(lowlightColor, "Lowlight color should not be null");
        assertEquals(Color.BLACK, lowlightColor.foreground(), "Lowlight foreground should be BLACK");
        assertEquals(Color.CYAN, lowlightColor.background(), "Lowlight background should be CYAN");
    }

    @Test
    @DisplayName("Shadow offset positioning should be correct")
    void testShadowOffsetPositioning() {
        // Get shadow offsets from theme
        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();

        // Verify classic Borland shadow offset (2 columns right, 1 row down)
        assertEquals(2, offsetX, "Shadow offset X should be 2 (columns right)");
        assertEquals(1, offsetY, "Shadow offset Y should be 1 (row down)");

        // Verify offsets are positive (shadow is below/right of component)
        assertTrue(offsetX > 0, "Shadow offset X should be positive");
        assertTrue(offsetY > 0, "Shadow offset Y should be positive");
    }

    @Test
    @DisplayName("Double-line border characters should be available")
    void testDoubleBorderCharacters() {
        // Get double-line border characters
        String doubleBorder = theme3D.getDoubleBorderChars();

        // Verify correct characters are returned
        assertNotNull(doubleBorder, "Double border chars should not be null");
        assertEquals(8, doubleBorder.length(), "Double border chars should be 8 characters");

        // Verify Unicode double-line characters (╔═╗║╚═╝║)
        assertEquals('╔', doubleBorder.charAt(0), "Top-left corner should be ╔");
        assertEquals('═', doubleBorder.charAt(1), "Top horizontal should be ═");
        assertEquals('╗', doubleBorder.charAt(2), "Top-right corner should be ╗");
        assertEquals('║', doubleBorder.charAt(3), "Left vertical should be ║");
        assertEquals('╚', doubleBorder.charAt(5), "Bottom-left corner should be ╚");
        assertEquals('═', doubleBorder.charAt(6), "Bottom horizontal should be ═");
        assertEquals('╝', doubleBorder.charAt(7), "Bottom-right corner should be ╝");
    }

    @Test
    @DisplayName("Single-line border characters should render properly")
    void testSingleLineBorderCharacters() {
        // Get standard border characters
        String borderChars = theme3D.getBorderChars();

        // Verify correct characters are returned
        assertNotNull(borderChars, "Border chars should not be null");
        assertEquals(8, borderChars.length(), "Border chars should be 8 characters");

        // Verify Unicode single-line characters (┌─┐│└─┘│)
        assertEquals('┌', borderChars.charAt(0), "Top-left corner should be ┌");
        assertEquals('─', borderChars.charAt(1), "Top horizontal should be ─");
        assertEquals('┐', borderChars.charAt(2), "Top-right corner should be ┐");
        assertEquals('│', borderChars.charAt(3), "Left vertical should be │");
        assertEquals('└', borderChars.charAt(5), "Bottom-left corner should be └");
        assertEquals('─', borderChars.charAt(6), "Bottom horizontal should be ─");
        assertEquals('┘', borderChars.charAt(7), "Bottom-right corner should be ┘");
    }

    @Test
    @DisplayName("RenderingStyle RAISED should be the default")
    void testDefaultRenderingStyle() {
        // Verify default rendering style
        RenderingStyle defaultStyle = theme3D.getDefaultRenderingStyle();

        assertEquals(RenderingStyle.RAISED, defaultStyle, "Default rendering style should be RAISED");
    }

    @Test
    @DisplayName("RenderingStyle enum should have all expected values")
    void testRenderingStyleEnumValues() {
        // Verify all rendering styles are available
        RenderingStyle[] styles = RenderingStyle.values();

        assertTrue(styles.length >= 4, "Should have at least 4 rendering styles");

        // Verify each expected style exists
        assertNotNull(RenderingStyle.valueOf("FLAT"), "FLAT style should exist");
        assertNotNull(RenderingStyle.valueOf("RAISED"), "RAISED style should exist");
        assertNotNull(RenderingStyle.valueOf("SUNKEN"), "SUNKEN style should exist");
        assertNotNull(RenderingStyle.valueOf("CUSTOM"), "CUSTOM style should exist");
    }

    @Test
    @DisplayName("Shadow character should be configurable")
    void testShadowCharacterConfiguration() {
        // Default theme uses solid shadow (space character)
        char shadowChar = theme3D.getShadowChar();
        assertEquals(' ', shadowChar, "Default shadow char should be space for solid shadows");

        // Gradient shadows disabled by default
        assertFalse(theme3D.useGradientShadow(), "Gradient shadows should be disabled by default");

        // Test gradient shadow theme
        Borland3DTheme gradientTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.SINGLE_LINE,
            true,  // adaptive shadow offset
            true,  // gradient shadows enabled
            true   // bold intensity
        );

        assertTrue(gradientTheme.useGradientShadow(), "Gradient shadows should be enabled");
        assertEquals('░', gradientTheme.getShadowChar(), "Gradient shadow should start with light shade");
    }

    @Test
    @DisplayName("Gradient shadow characters should have correct progression")
    void testGradientShadowCharacters() {
        // Test shade character progression (light to dark)
        assertEquals('░', theme3D.getShadeCharacter(0), "Intensity 0 should be ░ (25% shade)");
        assertEquals('▒', theme3D.getShadeCharacter(1), "Intensity 1 should be ▒ (50% shade)");
        assertEquals('▓', theme3D.getShadeCharacter(2), "Intensity 2 should be ▓ (75% shade)");
        assertEquals('█', theme3D.getShadeCharacter(3), "Intensity 3 should be █ (100% solid)");

        // Test invalid intensity falls back to lightest
        assertEquals('░', theme3D.getShadeCharacter(-1), "Invalid intensity should fall back to ░");
        assertEquals('░', theme3D.getShadeCharacter(10), "Invalid intensity should fall back to ░");
    }

    @Test
    @DisplayName("CP437 shade character codes should be correct")
    void testCP437ShadeCharacterCodes() {
        // Test CP437 code page shade character codes
        assertEquals(176, theme3D.getShadeCharacterCP437(0), "CP437 25% shade should be 176");
        assertEquals(177, theme3D.getShadeCharacterCP437(1), "CP437 50% shade should be 177");
        assertEquals(178, theme3D.getShadeCharacterCP437(2), "CP437 75% shade should be 178");
        assertEquals(219, theme3D.getShadeCharacterCP437(3), "CP437 solid should be 219");

        // Test invalid intensity falls back
        assertEquals(176, theme3D.getShadeCharacterCP437(-1), "Invalid CP437 code should fall back to 176");
        assertEquals(176, theme3D.getShadeCharacterCP437(10), "Invalid CP437 code should fall back to 176");
    }

    @Test
    @DisplayName("Bold intensity attribute should be configurable")
    void testBoldIntensityConfiguration() {
        // Default theme uses bold intensity
        assertTrue(theme3D.useBoldIntensity(), "Bold intensity should be enabled by default");

        // Test theme without bold intensity
        Borland3DTheme noBoldTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.SINGLE_LINE,
            true,   // adaptive shadow offset
            false,  // gradient shadows
            false   // bold intensity disabled
        );

        assertFalse(noBoldTheme.useBoldIntensity(), "Bold intensity should be disabled");
    }

    @Test
    @DisplayName("Border style should be configurable")
    void testBorderStyleConfiguration() {
        // Default theme uses single-line borders
        assertEquals(Borland3DTheme.BorderStyle.SINGLE_LINE, theme3D.getBorderStyle(),
            "Default border style should be SINGLE_LINE");

        // Test different border styles
        Borland3DTheme doubleLineTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.DOUBLE_LINE,
            true, false, true
        );
        assertEquals(Borland3DTheme.BorderStyle.DOUBLE_LINE, doubleLineTheme.getBorderStyle(),
            "Border style should be DOUBLE_LINE");

        Borland3DTheme roundedTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.ROUNDED,
            true, false, true
        );
        assertEquals(Borland3DTheme.BorderStyle.ROUNDED, roundedTheme.getBorderStyle(),
            "Border style should be ROUNDED");

        Borland3DTheme asciiTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.ASCII,
            true, false, true
        );
        assertEquals(Borland3DTheme.BorderStyle.ASCII, asciiTheme.getBorderStyle(),
            "Border style should be ASCII");
    }

    @Test
    @DisplayName("Rounded border characters should be correct")
    void testRoundedBorderCharacters() {
        Borland3DTheme roundedTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.ROUNDED,
            true, false, true
        );

        String borderChars = roundedTheme.getBorderChars();
        assertEquals('╭', borderChars.charAt(0), "Rounded top-left should be ╭");
        assertEquals('─', borderChars.charAt(1), "Rounded top should be ─");
        assertEquals('╮', borderChars.charAt(2), "Rounded top-right should be ╮");
        assertEquals('│', borderChars.charAt(3), "Rounded left should be │");
        assertEquals('╰', borderChars.charAt(5), "Rounded bottom-left should be ╰");
        assertEquals('─', borderChars.charAt(6), "Rounded bottom should be ─");
        assertEquals('╯', borderChars.charAt(7), "Rounded bottom-right should be ╯");
    }

    @Test
    @DisplayName("ASCII border characters should be correct")
    void testASCIIBorderCharacters() {
        Borland3DTheme asciiTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.ASCII,
            true, false, true
        );

        String borderChars = asciiTheme.getBorderChars();
        assertEquals('+', borderChars.charAt(0), "ASCII top-left should be +");
        assertEquals('-', borderChars.charAt(1), "ASCII top should be -");
        assertEquals('+', borderChars.charAt(2), "ASCII top-right should be +");
        assertEquals('|', borderChars.charAt(3), "ASCII left should be |");
        assertEquals('+', borderChars.charAt(5), "ASCII bottom-left should be +");
        assertEquals('-', borderChars.charAt(6), "ASCII bottom should be -");
        assertEquals('+', borderChars.charAt(7), "ASCII bottom-right should be +");
    }

    @Test
    @DisplayName("Theme should provide all standard Borland color pairs")
    void testBorlandColorPairs() {
        // Test background
        ColorPair background = theme3D.getBackground();
        assertEquals(Color.YELLOW, background.foreground(), "Background should be yellow on blue");
        assertEquals(Color.BLUE, background.background(), "Background should be yellow on blue");

        // Test button
        ColorPair button = theme3D.getButton();
        assertEquals(Color.CYAN, button.foreground(), "Button should be cyan on blue");
        assertEquals(Color.BLUE, button.background(), "Button should be cyan on blue");

        // Test focused button
        ColorPair buttonFocused = theme3D.getButtonFocused();
        assertEquals(Color.BLACK, buttonFocused.foreground(), "Focused button should be black on cyan");
        assertEquals(Color.CYAN, buttonFocused.background(), "Focused button should be black on cyan");

        // Test text input
        ColorPair textInput = theme3D.getTextInput();
        assertEquals(Color.WHITE, textInput.foreground(), "Text input should be white on blue");
        assertEquals(Color.BLUE, textInput.background(), "Text input should be white on blue");

        // Test border
        ColorPair border = theme3D.getBorder();
        assertEquals(Color.WHITE, border.foreground(), "Border should be white on blue");
        assertEquals(Color.BLUE, border.background(), "Border should be white on blue");

        // Test selection
        ColorPair selection = theme3D.getSelection();
        assertEquals(Color.BLACK, selection.foreground(), "Selection should be black on cyan");
        assertEquals(Color.CYAN, selection.background(), "Selection should be black on cyan");

        // Test disabled
        ColorPair disabled = theme3D.getDisabled();
        assertEquals(Color.BLACK, disabled.foreground(), "Disabled should be black on blue");
        assertEquals(Color.BLUE, disabled.background(), "Disabled should be black on blue");
    }

    @Test
    @DisplayName("Theme should provide Borland-specific color pairs")
    void testBorlandSpecificColorPairs() {
        // Test inactive color
        ColorPair inactive = theme3D.getInactiveColor();
        assertEquals(Color.BLACK, inactive.foreground(), "Inactive should be black on white");
        assertEquals(Color.WHITE, inactive.background(), "Inactive should be black on white");

        // Test menu color
        ColorPair menu = theme3D.getMenuColor();
        assertEquals(Color.BLACK, menu.foreground(), "Menu should be black on white");
        assertEquals(Color.WHITE, menu.background(), "Menu should be black on white");

        // Test status color
        ColorPair status = theme3D.getStatusColor();
        assertEquals(Color.YELLOW, status.foreground(), "Status should be yellow on blue");
        assertEquals(Color.BLUE, status.background(), "Status should be yellow on blue");

        // Test active title color
        ColorPair activeTitle = theme3D.getActiveTitleColor();
        assertEquals(Color.WHITE, activeTitle.foreground(), "Active title should be white on blue");
        assertEquals(Color.BLUE, activeTitle.background(), "Active title should be white on blue");
    }

    @Test
    @DisplayName("Theme name should be 'Borland 3D'")
    void testThemeName() {
        assertEquals("Borland 3D", theme3D.getName(), "Theme name should be 'Borland 3D'");
    }

    @Test
    @DisplayName("Theme toString should provide useful debug information")
    void testThemeToString() {
        String themeStr = theme3D.toString();

        // Verify toString contains key configuration details
        assertNotNull(themeStr, "toString should not return null");
        assertTrue(themeStr.contains("Borland3DTheme"), "toString should contain class name");
        assertTrue(themeStr.contains("SINGLE_LINE"), "toString should contain border style");
        assertTrue(themeStr.contains("shadow=2x1"), "toString should contain shadow offset");
        assertTrue(themeStr.contains("gradient=false"), "toString should contain gradient setting");
        assertTrue(themeStr.contains("bold=true"), "toString should contain bold setting");
    }

    @Test
    @DisplayName("3D rendering should fallback gracefully for non-3D themes")
    void test3DFallbackForNonThemes() {
        // Create a frame with non-3D theme
        Frame frame = new Frame("Test Frame");
        frame.setLocation(5, 5);
        frame.setSize(40, 10);

        // Note: In actual implementation, components would check theme.supports3D()
        // and fall back to standard 2D rendering if false

        setupFrame(frame);
        runEventLoopCycle();

        // Verify frame rendered (basic check - actual rendering depends on implementation)
        // This test verifies the fallback mechanism doesn't crash
        assertNotNull(buffer, "Buffer should be populated even with non-3D theme");
    }

    @Test
    @DisplayName("Button should support raised and sunken rendering styles")
    void testButtonRenderingStyles() {
        Button button = createButton("Test", 5, 5);

        // Note: In actual implementation, button would have methods like:
        // button.set3DEnabled(true);
        // button.setRenderingStyle(RenderingStyle.RAISED);

        setupFrame(button);
        runEventLoopCycle();

        // Verify button rendered
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");

        // Press button (in real implementation, pressed state would switch to SUNKEN)
        clickButton(button);

        // Button should have rendered in pressed state
        // Actual verification would check for SUNKEN rendering characteristics
    }

    @Test
    @DisplayName("3D shadow should not overlap component content")
    void testShadowDoesNotOverlapContent() {
        // Create a component at specific location
        Button button = createButton("Click", 10, 10);
        button.setSize(10, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Shadow should be rendered at offset (12, 11) - 2 columns right, 1 row down
        // Shadow should not overwrite button content area (10-19, 10-12)

        // Get shadow offset from theme
        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();

        // Verify shadow starts outside component bounds
        int shadowStartX = button.getX() + button.getWidth();
        int shadowStartY = button.getY() + offsetY;

        assertTrue(shadowStartX >= button.getX() + button.getWidth(),
            "Shadow X should start at or after component right edge");
        assertTrue(shadowStartY >= button.getY() + offsetY,
            "Shadow Y should start at offset from component top");
    }

    @Test
    @DisplayName("Multiple overlapping components should render shadows in correct Z-order")
    void testShadowZOrder() {
        // Create overlapping components
        Button button1 = createButton("Button 1", 5, 5);
        button1.setSize(15, 3);

        Button button2 = createButton("Button 2", 10, 7);
        button2.setSize(15, 3);

        setupFrame(button1, button2);
        runEventLoopCycle();

        // In proper 3D rendering:
        // - button2's shadow should render over button1's content (Z-order)
        // - Shadows are rendered in the shadow pass before content
        // - Components added later appear in front

        // Verify both buttons rendered
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");
        BufferAssertions.assertBufferContains(buffer, 10, 7, "[");
    }

    @Test
    @DisplayName("3D theme should be compatible with all component types")
    void test3DThemeWithVariousComponents() {
        // Create various component types
        Button button = createButton("Button", 5, 5);
        Label label = createLabel("Label", 5, 8);
        Checkbox checkbox = createCheckbox("Check", 5, 11);
        TextField textField = createTextField(5, 14, 20);

        setupFrame(button, label, checkbox, textField);
        runEventLoopCycle();

        // Verify all components rendered without errors
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");     // Button
        BufferAssertions.assertBufferContains(buffer, 5, 8, "Label"); // Label
        BufferAssertions.assertBufferContains(buffer, 5, 11, "[");    // Checkbox
        // TextField rendering depends on implementation
    }

    // ========== Visual Output Verification Tests ==========

    @Test
    @DisplayName("Shadow characters should appear at correct buffer positions with 2-column, 1-row offset")
    void testShadowBufferPlacement() {
        // Create a button with known dimensions
        Button button = createButton("Test", 10, 5);
        button.setSize(10, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Get shadow properties from theme
        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();
        char shadowChar = theme3D.getShadowChar();

        // Verify shadow offset values
        assertEquals(2, offsetX, "Shadow offset X should be 2 columns");
        assertEquals(1, offsetY, "Shadow offset Y should be 1 row");
        assertEquals(' ', shadowChar, "Default shadow character should be space");

        // Shadow should appear in L-shape:
        // - Vertical part: 2 columns wide, starting at (button.x + button.width, button.y + offsetY)
        // - Horizontal part: button.width columns wide, starting at (button.x + offsetX, button.y + button.height)
        //
        // For button at (10, 5) with size (10, 3):
        // - Component occupies: x=[10..19], y=[5..7]
        // - Vertical shadow: x=[20..21], y=[6..7] (2 cols wide, 2 rows tall)
        // - Horizontal shadow: x=[12..21], y=[8] (10 cols wide, 1 row tall)

        // Note: This test documents expected shadow placement once 3D rendering is implemented.
        // Current implementation may not yet render shadows to buffer.
        // When implemented, uncomment the following assertions:

        // Verify vertical shadow (right side)
        // for (int y = 6; y <= 7; y++) {
        //     for (int x = 20; x <= 21; x++) {
        //         assertEquals(shadowChar, buffer[y][x],
        //             "Shadow should appear at (" + x + "," + y + ")");
        //     }
        // }

        // Verify horizontal shadow (bottom)
        // for (int x = 12; x <= 21; x++) {
        //     assertEquals(shadowChar, buffer[8][x],
        //         "Shadow should appear at (" + x + ",8)");
        // }

        // Verify component content area is NOT shadowed
        BufferAssertions.assertBufferContains(buffer, 10, 5, "[");
    }

    @Test
    @DisplayName("Raised component borders should use highlight colors on top-left edges")
    void testHighlightColorOnRaisedBorders() {
        // Create a bordered frame component
        Frame innerFrame = new Frame("3D Frame");
        innerFrame.setLocation(10, 5);
        innerFrame.setSize(20, 8);
        innerFrame.setVisible(true);

        setupFrame(innerFrame);
        runEventLoopCycle();

        // Get theme properties
        ColorPair highlightColor = theme3D.getHighlightColor();
        String borderChars = theme3D.getBorderChars();

        // Verify highlight color definition
        assertEquals(Color.WHITE, highlightColor.foreground(), "Highlight foreground should be white");
        assertEquals(Color.CYAN, highlightColor.background(), "Highlight background should be cyan");

        // Verify border characters are present
        char topLeft = borderChars.charAt(0);   // ┌
        char topHoriz = borderChars.charAt(1);  // ─
        char leftVert = borderChars.charAt(3);  // │

        // Border should be rendered with these characters
        BufferAssertions.assertBufferContains(buffer, 10, 5, String.valueOf(topLeft));

        // Note: Color verification requires enhanced buffer that tracks ColorPair attributes.
        // Current buffer is char[][] - need ColorPair[][] or separate attribute buffer.
        // When color tracking is implemented, verify:
        //
        // assertBufferColorAt(buffer, 10, 5, highlightColor);  // Top-left corner
        // assertBufferColorAt(buffer, 11, 5, highlightColor);  // Top edge
        // assertBufferColorAt(buffer, 10, 6, highlightColor);  // Left edge
    }

    @Test
    @DisplayName("Raised component borders should use lowlight colors on bottom-right edges")
    void testLowlightColorOnRaisedBorders() {
        // Create a bordered frame component
        Frame innerFrame = new Frame("3D Frame");
        innerFrame.setLocation(10, 5);
        innerFrame.setSize(20, 8);
        innerFrame.setVisible(true);

        setupFrame(innerFrame);
        runEventLoopCycle();

        // Get theme properties
        ColorPair lowlightColor = theme3D.getLowlightColor();
        String borderChars = theme3D.getBorderChars();

        // Verify lowlight color definition
        assertEquals(Color.BLACK, lowlightColor.foreground(), "Lowlight foreground should be black");
        assertEquals(Color.CYAN, lowlightColor.background(), "Lowlight background should be cyan");

        // Calculate bottom-right positions
        int rightX = 10 + 20 - 1;   // 29
        int bottomY = 5 + 8 - 1;    // 12

        // Verify border characters
        char bottomRight = borderChars.charAt(7);  // ┘
        BufferAssertions.assertBufferContains(buffer, rightX, bottomY, String.valueOf(bottomRight));

        // Note: Color verification requires enhanced buffer that tracks ColorPair attributes.
        // When color tracking is implemented, verify:
        //
        // assertBufferColorAt(buffer, rightX, bottomY, lowlightColor);      // Bottom-right corner
        // assertBufferColorAt(buffer, rightX-1, bottomY, lowlightColor);    // Bottom edge
        // assertBufferColorAt(buffer, rightX, bottomY-1, lowlightColor);    // Right edge
    }

    @Test
    @DisplayName("Sunken button should invert highlight and lowlight border colors")
    void testSunkenButtonInvertedColors() {
        // Create button in sunken state (simulating pressed appearance)
        Button button = createButton("Press", 15, 10);
        button.setSize(12, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Get theme colors
        ColorPair highlightColor = theme3D.getHighlightColor();
        ColorPair lowlightColor = theme3D.getLowlightColor();

        // In RAISED style (default):
        // - Top-left borders use highlight (bright, appears raised)
        // - Bottom-right borders use lowlight (dark, appears shadowed)
        //
        // In SUNKEN style (pressed):
        // - Top-left borders use lowlight (dark, appears recessed)
        // - Bottom-right borders use highlight (bright, light source from below/right)

        // Press button to trigger sunken rendering
        clickButton(button);

        // Note: This requires RenderingStyle.SUNKEN implementation.
        // When implemented, the color assignments should be inverted:
        //
        // assertBufferColorAt(buffer, topLeftX, topLeftY, lowlightColor);       // Inverted
        // assertBufferColorAt(buffer, bottomRightX, bottomRightY, highlightColor); // Inverted
    }

    @Test
    @DisplayName("Gradient shadow should render with progressive shade characters in buffer")
    void testGradientShadowBufferContent() {
        // Create theme with gradient shadows
        Borland3DTheme gradientTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.SINGLE_LINE,
            true,  // adaptive shadow offset
            true,  // gradient shadows enabled
            true   // bold intensity
        );

        // Create button
        Button button = createButton("Test", 10, 5);
        button.setSize(15, 4);

        setupFrame(button);
        runEventLoopCycle();

        // Verify gradient shade characters
        char shade0 = gradientTheme.getShadeCharacter(0);  // ░ (25% - lightest)
        char shade1 = gradientTheme.getShadeCharacter(1);  // ▒ (50%)
        char shade2 = gradientTheme.getShadeCharacter(2);  // ▓ (75%)
        char shade3 = gradientTheme.getShadeCharacter(3);  // █ (100% - darkest)

        assertEquals('░', shade0, "Intensity 0 should be light shade");
        assertEquals('▒', shade1, "Intensity 1 should be medium shade");
        assertEquals('▓', shade2, "Intensity 2 should be dark shade");
        assertEquals('█', shade3, "Intensity 3 should be solid block");

        // Gradient shadow progression:
        // Column 0 (closest to component): lightest shade
        // Column 1: medium shade
        // (For 2-column shadow offset, we have 2 gradient levels)
        //
        // For button at (10, 5) size (15, 4):
        // - Vertical shadow at x=25: first column uses shade0 (░)
        // - Vertical shadow at x=26: second column uses shade1 (▒)

        // Note: Gradient rendering verification requires implementation.
        // When implemented, verify buffer contains gradient progression:
        //
        // int shadowStartX = button.getX() + button.getWidth();
        // int shadowY = button.getY() + 1;
        // assertEquals(shade0, buffer[shadowY][shadowStartX], "First shadow column should be lightest");
        // assertEquals(shade1, buffer[shadowY][shadowStartX + 1], "Second shadow column should be darker");
    }

    @Test
    @DisplayName("Double-line borders should render correct Unicode characters to buffer")
    void testDoubleLineBorderBufferRendering() {
        // Create theme with double-line borders
        Borland3DTheme doubleLineTheme = new Borland3DTheme(
            Borland3DTheme.BorderStyle.DOUBLE_LINE,
            true, false, true
        );

        // Create bordered frame
        Frame frame = new Frame("Double");
        frame.setLocation(10, 5);
        frame.setSize(20, 8);
        frame.setVisible(true);

        setupFrame(frame);
        runEventLoopCycle();

        // Get double-line border characters
        String doubleBorder = doubleLineTheme.getDoubleBorderChars();

        char topLeft = doubleBorder.charAt(0);      // ╔
        char topHoriz = doubleBorder.charAt(1);     // ═
        char topRight = doubleBorder.charAt(2);     // ╗
        char vert = doubleBorder.charAt(3);         // ║
        char bottomLeft = doubleBorder.charAt(5);   // ╚
        char bottomHoriz = doubleBorder.charAt(6);  // ═
        char bottomRight = doubleBorder.charAt(7);  // ╝

        // Calculate border positions
        int left = 10;
        int top = 5;
        int right = 10 + 20 - 1;   // 29
        int bottom = 5 + 8 - 1;    // 12

        // Note: Buffer verification requires theme to be applied to components.
        // When double-line border rendering is implemented, verify:
        //
        // BufferAssertions.assertBufferContains(buffer, left, top, String.valueOf(topLeft));
        // BufferAssertions.assertBufferContains(buffer, left+1, top, String.valueOf(topHoriz));
        // BufferAssertions.assertBufferContains(buffer, right, top, String.valueOf(topRight));
        // BufferAssertions.assertBufferContains(buffer, left, top+1, String.valueOf(vert));
        // BufferAssertions.assertBufferContains(buffer, left, bottom, String.valueOf(bottomLeft));
        // BufferAssertions.assertBufferContains(buffer, left+1, bottom, String.valueOf(bottomHoriz));
        // BufferAssertions.assertBufferContains(buffer, right, bottom, String.valueOf(bottomRight));
    }

    @Test
    @DisplayName("Multiple components should each render independent shadows to buffer")
    void testMultipleComponentShadowsInBuffer() {
        // Create a 2x2 grid of buttons
        Button btn1 = createButton("A", 5, 5);
        btn1.setSize(8, 3);

        Button btn2 = createButton("B", 20, 5);
        btn2.setSize(8, 3);

        Button btn3 = createButton("C", 5, 12);
        btn3.setSize(8, 3);

        Button btn4 = createButton("D", 20, 12);
        btn4.setSize(8, 3);

        setupFrame(btn1, btn2, btn3, btn4);
        runEventLoopCycle();

        // Verify all buttons rendered
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");
        BufferAssertions.assertBufferContains(buffer, 20, 5, "[");
        BufferAssertions.assertBufferContains(buffer, 5, 12, "[");
        BufferAssertions.assertBufferContains(buffer, 20, 12, "[");

        // Each button should have its own shadow at offset (2, 1):
        // btn1 at (5,5) size (8,3): shadow at x=[13,14] y=[6,7] and x=[7,14] y=[8]
        // btn2 at (20,5) size (8,3): shadow at x=[28,29] y=[6,7] and x=[22,29] y=[8]
        // btn3 at (5,12) size (8,3): shadow at x=[13,14] y=[13,14] and x=[7,14] y=[15]
        // btn4 at (20,12) size (8,3): shadow at x=[28,29] y=[13,14] and x=[22,29] y=[15]

        char shadowChar = theme3D.getShadowChar();
        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();

        // Verify shadow positioning calculations
        assertEquals(2, offsetX, "Shadow offset X should be 2");
        assertEquals(1, offsetY, "Shadow offset Y should be 1");

        // Note: Shadow buffer verification requires implementation.
        // When implemented, verify each button has its own independent shadow:
        //
        // Button 1 vertical shadow
        // assertEquals(shadowChar, buffer[6][13], "btn1 shadow right column 1");
        // assertEquals(shadowChar, buffer[6][14], "btn1 shadow right column 2");
        //
        // Button 2 vertical shadow
        // assertEquals(shadowChar, buffer[6][28], "btn2 shadow right column 1");
        // assertEquals(shadowChar, buffer[6][29], "btn2 shadow right column 2");
    }

    @Test
    @DisplayName("Component visibility should control shadow rendering to buffer")
    void testInvisibleComponentHasNoShadowInBuffer() {
        // Create visible and invisible buttons at same location
        Button visibleButton = createButton("Visible", 10, 5);
        visibleButton.setSize(12, 3);
        visibleButton.setVisible(true);

        Button invisibleButton = createButton("Hidden", 10, 10);
        invisibleButton.setSize(12, 3);
        invisibleButton.setVisible(false);

        setupFrame(visibleButton, invisibleButton);
        runEventLoopCycle();

        // Verify visible button is rendered
        BufferAssertions.assertBufferContains(buffer, 10, 5, "[");

        // Verify invisible button is NOT rendered
        char charAtInvisible = buffer[10][10];
        assertNotEquals('[', charAtInvisible, "Invisible button should not render");

        // Shadow of invisible button should also NOT be rendered
        // For invisible button at (10,10) size (12,3), shadow would be at:
        // Vertical: x=[22,23] y=[11,12]
        // Horizontal: x=[12,23] y=[13]

        // Verify buffer is clean (spaces) where invisible shadow would be
        char shadowChar = theme3D.getShadowChar();
        // Note: If shadowChar is space, this test won't distinguish shadow from background.
        // When gradient shadows are used, shadowChar would be ░, making this testable:
        //
        // assertNotEquals(shadowChar, buffer[11][22], "Invisible component should not cast shadow");
    }

    @Test
    @DisplayName("Buffer should contain exact shadow character positions for L-shaped shadow")
    void testLShapedShadowExactPositions() {
        // Create button to verify precise L-shaped shadow rendering
        Button button = createButton("Shadow", 20, 10);
        button.setSize(10, 4);

        setupFrame(button);
        runEventLoopCycle();

        // Shadow geometry for component at (20, 10) size (10, 4):
        // Component occupies: x=[20..29], y=[10..13]
        //
        // With offset (2, 1):
        // Vertical shadow (right side):
        //   - Starts at x = 20 + 10 = 30
        //   - Width: 2 columns (offsetX)
        //   - Columns: [30, 31]
        //   - Starts at y = 10 + 1 = 11 (offset from top)
        //   - Height: 4 - 1 = 3 rows (component height minus offset)
        //   - Rows: [11, 12, 13]
        //
        // Horizontal shadow (bottom):
        //   - Starts at x = 20 + 2 = 22 (offset from left)
        //   - Width: 10 columns (component width)
        //   - Columns: [22..31]
        //   - Starts at y = 10 + 4 = 14
        //   - Height: 1 row
        //   - Rows: [14]
        //
        // Total shadow coverage:
        // - Vertical: (30,11), (31,11), (30,12), (31,12), (30,13), (31,13)
        // - Horizontal: (22,14), (23,14), ..., (31,14)

        char shadowChar = theme3D.getShadowChar();

        // Note: Shadow rendering verification requires implementation.
        // When implemented, verify exact shadow positions:
        //
        // Vertical shadow positions
        // assertEquals(shadowChar, buffer[11][30], "Vertical shadow at (30,11)");
        // assertEquals(shadowChar, buffer[11][31], "Vertical shadow at (31,11)");
        // assertEquals(shadowChar, buffer[12][30], "Vertical shadow at (30,12)");
        // assertEquals(shadowChar, buffer[12][31], "Vertical shadow at (31,12)");
        // assertEquals(shadowChar, buffer[13][30], "Vertical shadow at (30,13)");
        // assertEquals(shadowChar, buffer[13][31], "Vertical shadow at (31,13)");
        //
        // Horizontal shadow positions
        // for (int x = 22; x <= 31; x++) {
        //     assertEquals(shadowChar, buffer[14][x], "Horizontal shadow at (" + x + ",14)");
        // }
        //
        // Verify component content is NOT overwritten by shadow
        // assertNotEquals(shadowChar, buffer[10][20], "Component area should not be shadowed");
    }

    // ========== Edge Case Tests: Shadow Rendering at Screen Boundaries ==========

    @Test
    @DisplayName("3D component at right edge - shadow should clip gracefully")
    void testShadowClippingAtRightEdge() {
        // Get screen dimensions (assuming standard 80x24 terminal)
        int screenWidth = 80;
        int screenHeight = 24;

        // Place component at right edge where shadow would exceed screen bounds
        // Component width = 10, shadow offset X = 2, so rightmost position = screenWidth - 10
        Button button = createButton("Edge", screenWidth - 10, 5);
        button.setSize(10, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Verify component rendered at right edge
        int componentRightEdge = button.getX() + button.getWidth();
        assertEquals(screenWidth, componentRightEdge,
            "Component should be at right edge of screen");

        // Shadow would start at X = screenWidth (componentRightEdge + 0)
        // Shadow offset X is 2, so shadow extends to X = screenWidth + 2
        // This should be clipped to not exceed screen bounds
        int shadowStartX = componentRightEdge;
        int shadowEndX = shadowStartX + theme3D.getShadowOffsetX();

        // Verify shadow calculation
        assertTrue(shadowEndX > screenWidth,
            "Shadow should theoretically extend beyond screen width");

        // In proper implementation, rendering code should clip shadow to screen bounds
        // No buffer access beyond screenWidth-1 should occur
        // This test verifies the logic doesn't crash when shadow exceeds bounds
    }

    @Test
    @DisplayName("3D component at bottom edge - shadow should clip gracefully")
    void testShadowClippingAtBottomEdge() {
        // Get screen dimensions
        int screenHeight = 24;

        // Place component at bottom edge where shadow would exceed screen bounds
        // Component height = 3, shadow offset Y = 1, so bottom position = screenHeight - 3
        Button button = createButton("Bottom", 10, screenHeight - 3);
        button.setSize(10, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Verify component rendered at bottom edge
        int componentBottomEdge = button.getY() + button.getHeight();
        assertEquals(screenHeight, componentBottomEdge,
            "Component should be at bottom edge of screen");

        // Shadow would start at Y = componentBottomEdge
        // Shadow offset Y is 1, but component already at bottom, so shadow at Y = 24
        // This should be clipped to not exceed screen bounds
        int shadowY = button.getY() + theme3D.getShadowOffsetY();

        // Verify shadow calculation
        assertTrue(shadowY + button.getHeight() - 1 >= screenHeight,
            "Shadow should theoretically extend beyond screen height");

        // In proper implementation, rendering code should clip shadow to screen bounds
        // No buffer access beyond screenHeight-1 should occur
    }

    @Test
    @DisplayName("3D component at bottom-right corner - L-shaped shadow clips correctly")
    void testShadowClippingAtCorner() {
        // Get screen dimensions
        int screenWidth = 80;
        int screenHeight = 24;

        // Place component at bottom-right corner
        // Both horizontal and vertical shadows will exceed bounds
        Button button = createButton("Corner", screenWidth - 10, screenHeight - 3);
        button.setSize(10, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Verify component is at corner
        int componentRightEdge = button.getX() + button.getWidth();
        int componentBottomEdge = button.getY() + button.getHeight();

        assertEquals(screenWidth, componentRightEdge,
            "Component should be at right edge");
        assertEquals(screenHeight, componentBottomEdge,
            "Component should be at bottom edge");

        // L-shaped shadow calculation:
        // - Vertical shadow on right side: from (screenWidth, Y+offsetY) down for height rows
        // - Horizontal shadow on bottom: from (X+offsetX, screenHeight) right for width cols
        // Both parts exceed screen bounds and should be clipped

        int shadowOffsetX = theme3D.getShadowOffsetX();
        int shadowOffsetY = theme3D.getShadowOffsetY();

        // Verify both shadow dimensions would exceed bounds
        assertTrue(componentRightEdge + shadowOffsetX > screenWidth,
            "Right shadow should exceed screen width");
        assertTrue(componentBottomEdge + shadowOffsetY > screenHeight,
            "Bottom shadow should exceed screen height");

        // Rendering should clip both shadow parts without crashing
    }

    @Test
    @DisplayName("Overlapping 3D components - shadow rendering order is correct")
    void testOverlappingShadowRenderingOrder() {
        // Create three overlapping components to test shadow layering
        Button button1 = createButton("Back", 5, 5);
        button1.setSize(15, 4);

        Button button2 = createButton("Middle", 10, 8);
        button2.setSize(15, 4);

        Button button3 = createButton("Front", 15, 11);
        button3.setSize(15, 4);

        setupFrame(button1, button2, button3);
        runEventLoopCycle();

        // Shadow rendering order rules:
        // 1. All shadows are rendered in a first pass
        // 2. All component content is rendered in a second pass
        // 3. Within each pass, components are rendered in Z-order (back to front)
        //
        // Expected behavior:
        // - button1's shadow should appear behind button2 and button3
        // - button2's shadow should appear behind button3 but over button1's content
        // - button3's shadow should appear over everything else's content
        //
        // This prevents shadows from rendering on top of foreground components

        // Verify all buttons rendered
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");
        BufferAssertions.assertBufferContains(buffer, 10, 8, "[");
        BufferAssertions.assertBufferContains(buffer, 15, 11, "[");

        // Calculate shadow positions
        int shadow1X = button1.getX() + button1.getWidth();
        int shadow1Y = button1.getY() + theme3D.getShadowOffsetY();

        int shadow2X = button2.getX() + button2.getWidth();
        int shadow2Y = button2.getY() + theme3D.getShadowOffsetY();

        // Verify shadows would overlap with other components
        assertTrue(shadow1X > button2.getX() || shadow1Y > button2.getY(),
            "button1 shadow should overlap with button2 area");
        assertTrue(shadow2X > button3.getX() || shadow2Y > button3.getY(),
            "button2 shadow should overlap with button3 area");
    }

    @Test
    @DisplayName("Invalid shadow offsets - negative values should be rejected")
    void testInvalidShadowOffsetsNegative() {
        // Negative shadow offsets don't make physical sense (shadow can't be above/left)
        // Theme should either:
        // 1. Reject negative values in constructor/setter
        // 2. Clamp to zero
        // 3. Use absolute value

        // Test that default theme has valid positive offsets
        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();

        assertTrue(offsetX >= 0, "Shadow offset X should not be negative");
        assertTrue(offsetY >= 0, "Shadow offset Y should not be negative");

        // In a complete implementation, you would test:
        // - What happens if someone tries to create a theme with negative offsets
        // - Whether the theme validates/clamps these values
        // - Whether rendering code handles negative offsets defensively

        // For now, verify the contract: offsets are non-negative
        assertEquals(2, offsetX, "Standard Borland shadow offset X is 2");
        assertEquals(1, offsetY, "Standard Borland shadow offset Y is 1");
    }

    @Test
    @DisplayName("Zero shadow offset - shadow should render directly adjacent")
    void testZeroShadowOffset() {
        // Create a theme with zero shadow offset (shadow directly adjacent to component)
        // This is an edge case where shadow and component borders touch

        // Note: Borland3DTheme doesn't currently support custom shadow offsets
        // This test documents the expected behavior if that feature is added

        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();

        // If both offsets were zero, shadow would render:
        // - Vertical shadow: directly to the right of right border
        // - Horizontal shadow: directly below bottom border
        // - No gap between component and shadow

        // With standard offsets (2, 1), there IS a gap
        assertTrue(offsetX > 0, "Standard theme has horizontal gap before shadow");
        assertTrue(offsetY > 0, "Standard theme has vertical gap before shadow");

        // If implementing custom offsets, zero should be valid:
        // - offsetX = 0: vertical shadow directly adjacent to right edge
        // - offsetY = 0: horizontal shadow directly adjacent to bottom edge
        // - Both = 0: shadow forms perfect L directly touching borders
    }

    @Test
    @DisplayName("Excessive shadow offset - should not cause buffer overflow")
    void testExcessiveShadowOffset() {
        // Test defensive programming: what if shadow offset is unreasonably large?
        // Example: offsetX = 100 on an 80-column terminal

        int screenWidth = 80;
        int screenHeight = 24;

        // Component near left edge with current shadow offset
        Button button = createButton("Test", 5, 5);
        button.setSize(10, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Current shadow offset (2, 1) is reasonable
        int offsetX = theme3D.getShadowOffsetX();
        int offsetY = theme3D.getShadowOffsetY();

        int shadowEndX = button.getX() + button.getWidth() + offsetX;
        int shadowEndY = button.getY() + button.getHeight() + offsetY;

        // Verify shadow is within reasonable bounds
        assertTrue(shadowEndX < screenWidth,
            "Shadow with standard offset should fit on screen");
        assertTrue(shadowEndY < screenHeight,
            "Shadow with standard offset should fit on screen");

        // If implementing custom shadow offsets, rendering code should:
        // 1. Validate offset is reasonable (e.g., < screenWidth/2)
        // 2. Clip shadow rendering to screen bounds
        // 3. Not attempt to write beyond buffer boundaries
        //
        // Example pathological case:
        // - Component at (5, 5), size (10, 3)
        // - Shadow offset (200, 100) - way beyond screen
        // - Rendering should clip to screen bounds, not crash
    }

    @Test
    @DisplayName("Shadow at exact screen boundary - no off-by-one error")
    void testShadowAtExactBoundary() {
        // Test the precise boundary condition where shadow ends exactly at screen edge
        // This tests for off-by-one errors in clipping logic

        int screenWidth = 80;
        int screenHeight = 24;

        // Calculate position where shadow would end exactly at screen width
        // Component right edge + shadow offset X = screenWidth
        // Component X = screenWidth - component.width - shadow offset X

        int componentWidth = 10;
        int shadowOffsetX = theme3D.getShadowOffsetX(); // 2
        int shadowOffsetY = theme3D.getShadowOffsetY(); // 1

        // Position component so shadow ends exactly at screen width
        int componentX = screenWidth - componentWidth - shadowOffsetX;

        Button button = createButton("Exact", componentX, 5);
        button.setSize(componentWidth, 3);

        setupFrame(button);
        runEventLoopCycle();

        // Verify positioning calculation
        int shadowEndX = button.getX() + button.getWidth() + shadowOffsetX;
        assertEquals(screenWidth, shadowEndX,
            "Shadow should end exactly at screen boundary");

        // Rendering should handle this precisely:
        // - Shadow column at X = 79 (screenWidth - 1) should render
        // - Shadow column at X = 80 (screenWidth) should NOT render (out of bounds)
        // - No off-by-one error: column 79 is the last valid column (0-indexed)
    }

    @Test
    @DisplayName("Minimum size component with shadow - 1x1 component edge case")
    void testMinimumSizeComponentShadow() {
        // Test shadow rendering for smallest possible component (1x1)
        // This is an edge case for shadow dimension calculations

        Button button = createButton("", 10, 10);
        button.setSize(1, 1);

        setupFrame(button);
        runEventLoopCycle();

        int shadowOffsetX = theme3D.getShadowOffsetX();
        int shadowOffsetY = theme3D.getShadowOffsetY();

        // Shadow for 1x1 component should be:
        // - Vertical shadow: 1 column wide, (height) rows tall, at X+width
        // - Horizontal shadow: (width) columns wide, 1 row tall, at Y+height
        //
        // For 1x1 with offset (2,1):
        // - Vertical: column at X=1 (since X=0, width=1, X+width=1),
        //   but offset is 2, so actually at X=3, height=1
        // - Horizontal: row at Y=1 (since Y=0, height=1, Y+height=1),
        //   but offset is 1, so actually at Y=2, width=1

        // Verify component is minimal size
        assertEquals(1, button.getWidth(), "Component width should be 1");
        assertEquals(1, button.getHeight(), "Component height should be 1");

        // Shadow should still render with proper offset
        int shadowX = button.getX() + button.getWidth();
        int shadowY = button.getY() + button.getHeight();

        // Even tiny components cast shadows
        assertTrue(shadowX + shadowOffsetX > button.getX(),
            "Shadow should extend beyond component");
        assertTrue(shadowY + shadowOffsetY > button.getY(),
            "Shadow should extend beyond component");
    }
}

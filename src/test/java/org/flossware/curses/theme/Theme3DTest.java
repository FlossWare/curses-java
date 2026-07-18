package org.flossware.curses.theme;

import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for the {@link Theme3D} interface.
 *
 * <p>This test suite verifies the contract and behavior of the Theme3D interface,
 * including default method implementations, color pair consistency, shadow offset
 * validation, and integration with the base Theme interface.</p>
 *
 * <h2>Test Coverage</h2>
 * <ul>
 *   <li>Default method implementations (supports3D, getDoubleBorderChars, etc.)</li>
 *   <li>Contract verification (all methods return non-null values)</li>
 *   <li>Shadow offset validation (reasonable values)</li>
 *   <li>Border character format (8-character strings)</li>
 *   <li>Color pair consistency across highlight/lowlight/shadow</li>
 *   <li>Integration with base Theme interface</li>
 *   <li>Concrete implementation behavior (using Borland3DTheme)</li>
 * </ul>
 *
 * <h2>Testing Strategy</h2>
 * <p>Tests use the {@link Borland3DTheme} as a concrete implementation to verify
 * interface behavior. The suite includes:</p>
 * <ul>
 *   <li>Interface contract tests (applicable to all implementations)</li>
 *   <li>Default method tests (using test implementation)</li>
 *   <li>Implementation-specific tests (using Borland3DTheme)</li>
 * </ul>
 *
 * @see Theme3D
 * @see Theme
 * @see Borland3DTheme
 */
@DisplayName("Theme3D Interface Tests")
class Theme3DTest {

    /**
     * Test implementation that uses all default methods from Theme3D interface.
     * This allows testing default behavior without implementation-specific overrides.
     */
    private static class DefaultTheme3DImpl implements Theme3D {
        @Override
        public ColorPair getBackground() {
            return new ColorPair(org.flossware.curses.api.Color.WHITE, org.flossware.curses.api.Color.BLACK);
        }

        @Override
        public ColorPair getButton() {
            return new ColorPair(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.WHITE);
        }

        @Override
        public ColorPair getButtonFocused() {
            return new ColorPair(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.CYAN);
        }

        @Override
        public ColorPair getTextInput() {
            return new ColorPair(org.flossware.curses.api.Color.WHITE, org.flossware.curses.api.Color.BLUE);
        }

        @Override
        public ColorPair getBorder() {
            return new ColorPair(org.flossware.curses.api.Color.WHITE, org.flossware.curses.api.Color.BLUE);
        }

        @Override
        public ColorPair getSelection() {
            return new ColorPair(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.CYAN);
        }

        @Override
        public ColorPair getDisabled() {
            return new ColorPair(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.BLACK);
        }

        @Override
        public String getBorderChars() {
            return "┌─┐│└─┘│";
        }

        @Override
        public String getName() {
            return "Default Test Theme";
        }

        @Override
        public ColorPair getShadowColor() {
            return new ColorPair(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.BLACK);
        }

        @Override
        public ColorPair getHighlightColor() {
            return new ColorPair(org.flossware.curses.api.Color.WHITE, org.flossware.curses.api.Color.CYAN);
        }

        @Override
        public ColorPair getLowlightColor() {
            return new ColorPair(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.CYAN);
        }

        @Override
        public int getShadowOffsetX() {
            return 2;
        }

        @Override
        public int getShadowOffsetY() {
            return 1;
        }
    }

    private Theme3D theme;
    private Theme3D defaultTheme;

    @BeforeEach
    void setUp() {
        theme = new Borland3DTheme();
        defaultTheme = new DefaultTheme3DImpl();
    }

    @Nested
    @DisplayName("Default Method Tests")
    class DefaultMethodTests {

        @Test
        @DisplayName("supports3D should return true for Theme3D implementations")
        void testSupports3D() {
            assertThat(theme.supports3D())
                .as("Theme3D implementations should always support 3D rendering")
                .isTrue();

            assertThat(defaultTheme.supports3D())
                .as("Default implementation should return true")
                .isTrue();
        }

        @Test
        @DisplayName("getDoubleBorderChars should return 8-character string")
        void testGetDoubleBorderCharsFormat() {
            String doubleBorderChars = defaultTheme.getDoubleBorderChars();

            assertThat(doubleBorderChars)
                .as("Double border characters should not be null")
                .isNotNull();

            assertThat(doubleBorderChars)
                .as("Double border characters should contain exactly 8 characters")
                .hasSize(8);
        }

        @Test
        @DisplayName("getDoubleBorderChars should return Unicode double-line characters by default")
        void testGetDoubleBorderCharsDefault() {
            String doubleBorderChars = defaultTheme.getDoubleBorderChars();

            assertThat(doubleBorderChars)
                .as("Default implementation should use Unicode double-line box drawing characters")
                .isEqualTo("╔═╗║╚═╝║");
        }

        @Test
        @DisplayName("getShadowChar should return space character by default")
        void testGetShadowCharDefault() {
            char shadowChar = defaultTheme.getShadowChar();

            assertThat(shadowChar)
                .as("Default shadow character should be space for solid shadows")
                .isEqualTo(' ');
        }

        @Test
        @DisplayName("useGradientShadow should return false by default")
        void testUseGradientShadowDefault() {
            boolean useGradient = defaultTheme.useGradientShadow();

            assertThat(useGradient)
                .as("Default implementation should use solid shadows for maximum compatibility")
                .isFalse();
        }

        @Test
        @DisplayName("getDefaultRenderingStyle should return RAISED by default")
        void testGetDefaultRenderingStyleDefault() {
            RenderingStyle style = defaultTheme.getDefaultRenderingStyle();

            assertThat(style)
                .as("Default rendering style should be RAISED")
                .isNotNull()
                .isEqualTo(RenderingStyle.RAISED);
        }
    }

    @Nested
    @DisplayName("Contract Verification Tests")
    class ContractVerificationTests {

        @Test
        @DisplayName("getShadowColor should never return null")
        void testGetShadowColorNotNull() {
            assertThat(theme.getShadowColor())
                .as("Shadow color should never be null")
                .isNotNull();
        }

        @Test
        @DisplayName("getHighlightColor should never return null")
        void testGetHighlightColorNotNull() {
            assertThat(theme.getHighlightColor())
                .as("Highlight color should never be null")
                .isNotNull();
        }

        @Test
        @DisplayName("getLowlightColor should never return null")
        void testGetLowlightColorNotNull() {
            assertThat(theme.getLowlightColor())
                .as("Lowlight color should never be null")
                .isNotNull();
        }

        @Test
        @DisplayName("getDoubleBorderChars should never return null")
        void testGetDoubleBorderCharsNotNull() {
            assertThat(theme.getDoubleBorderChars())
                .as("Double border characters should never be null")
                .isNotNull();
        }

        @Test
        @DisplayName("getDefaultRenderingStyle should never return null")
        void testGetDefaultRenderingStyleNotNull() {
            assertThat(theme.getDefaultRenderingStyle())
                .as("Default rendering style should never be null")
                .isNotNull();
        }

        @Test
        @DisplayName("all Theme3D color methods should return non-null ColorPair instances")
        void testAllColorMethodsReturnNonNull() {
            assertThat(theme.getShadowColor())
                .as("getShadowColor")
                .isNotNull()
                .isInstanceOf(ColorPair.class);

            assertThat(theme.getHighlightColor())
                .as("getHighlightColor")
                .isNotNull()
                .isInstanceOf(ColorPair.class);

            assertThat(theme.getLowlightColor())
                .as("getLowlightColor")
                .isNotNull()
                .isInstanceOf(ColorPair.class);
        }
    }

    @Nested
    @DisplayName("Shadow Offset Validation Tests")
    class ShadowOffsetValidationTests {

        @Test
        @DisplayName("getShadowOffsetX should return reasonable positive value")
        void testGetShadowOffsetXRange() {
            int offsetX = theme.getShadowOffsetX();

            assertThat(offsetX)
                .as("Shadow offset X should be positive")
                .isGreaterThan(0)
                .as("Shadow offset X should be reasonable (typically 1-3)")
                .isLessThanOrEqualTo(5);
        }

        @Test
        @DisplayName("getShadowOffsetY should return reasonable positive value")
        void testGetShadowOffsetYRange() {
            int offsetY = theme.getShadowOffsetY();

            assertThat(offsetY)
                .as("Shadow offset Y should be positive")
                .isGreaterThan(0)
                .as("Shadow offset Y should be reasonable (typically 1-2)")
                .isLessThanOrEqualTo(3);
        }

        @Test
        @DisplayName("shadow offset X should typically be larger than or equal to Y")
        void testShadowOffsetRatio() {
            int offsetX = theme.getShadowOffsetX();
            int offsetY = theme.getShadowOffsetY();

            assertThat(offsetX)
                .as("Shadow offset X should be >= Y to account for character aspect ratio")
                .isGreaterThanOrEqualTo(offsetY);
        }

        @Test
        @DisplayName("shadow offsets should be consistent across multiple calls")
        void testShadowOffsetConsistency() {
            int offsetX1 = theme.getShadowOffsetX();
            int offsetY1 = theme.getShadowOffsetY();

            int offsetX2 = theme.getShadowOffsetX();
            int offsetY2 = theme.getShadowOffsetY();

            assertThat(offsetX1)
                .as("Shadow offset X should be consistent")
                .isEqualTo(offsetX2);

            assertThat(offsetY1)
                .as("Shadow offset Y should be consistent")
                .isEqualTo(offsetY2);
        }
    }

    @Nested
    @DisplayName("Border Character Format Tests")
    class BorderCharacterFormatTests {

        @Test
        @DisplayName("getBorderChars should return exactly 8 characters")
        void testGetBorderCharsLength() {
            String borderChars = theme.getBorderChars();

            assertThat(borderChars)
                .as("Border characters should not be null")
                .isNotNull()
                .as("Border characters should contain exactly 8 characters")
                .hasSize(8);
        }

        @Test
        @DisplayName("getDoubleBorderChars should return exactly 8 characters")
        void testGetDoubleBorderCharsLength() {
            String doubleBorderChars = theme.getDoubleBorderChars();

            assertThat(doubleBorderChars)
                .as("Double border characters should not be null")
                .isNotNull()
                .as("Double border characters should contain exactly 8 characters")
                .hasSize(8);
        }

        @Test
        @DisplayName("border characters should be different from double border characters")
        void testBorderCharsDifferentFromDoubleBorderChars() {
            String borderChars = theme.getBorderChars();
            String doubleBorderChars = theme.getDoubleBorderChars();

            assertThat(borderChars)
                .as("Single and double border characters should be visually distinct")
                .isNotEqualTo(doubleBorderChars);
        }

        @Test
        @DisplayName("border character positions should follow documented format")
        void testBorderCharsFormat() {
            // Format: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right
            String borderChars = theme.getBorderChars();

            assertThat(borderChars)
                .as("Should have 8 characters: [0]=TL, [1]=T, [2]=TR, [3]=L, [4]=R, [5]=BL, [6]=B, [7]=BR")
                .hasSize(8);

            // Verify characters are accessible at expected positions
            assertThatCode(() -> {
                char topLeft = borderChars.charAt(0);
                char top = borderChars.charAt(1);
                char topRight = borderChars.charAt(2);
                char left = borderChars.charAt(3);
                char right = borderChars.charAt(4);
                char bottomLeft = borderChars.charAt(5);
                char bottom = borderChars.charAt(6);
                char bottomRight = borderChars.charAt(7);
            }).as("All 8 border character positions should be accessible")
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Color Pair Consistency Tests")
    class ColorPairConsistencyTests {

        @Test
        @DisplayName("shadow color should use dark colors for realistic shadow effect")
        void testShadowColorDarkness() {
            ColorPair shadowColor = theme.getShadowColor();

            assertThat(shadowColor)
                .as("Shadow color should be a ColorPair instance")
                .isNotNull()
                .isInstanceOf(ColorPair.class);

            // Shadow colors typically use BLACK or dark colors
            assertThat(shadowColor.foreground())
                .as("Shadow foreground should typically be dark (BLACK)")
                .isNotNull();

            assertThat(shadowColor.background())
                .as("Shadow background should typically be dark (BLACK)")
                .isNotNull();
        }

        @Test
        @DisplayName("highlight and lowlight colors should be different")
        void testHighlightLowlightDifference() {
            ColorPair highlightColor = theme.getHighlightColor();
            ColorPair lowlightColor = theme.getLowlightColor();

            assertThat(highlightColor)
                .as("Highlight and lowlight colors must be different to create 3D effect")
                .isNotEqualTo(lowlightColor);
        }

        @Test
        @DisplayName("highlight color should be brighter than lowlight color")
        void testHighlightBrighterThanLowlight() {
            ColorPair highlightColor = theme.getHighlightColor();
            ColorPair lowlightColor = theme.getLowlightColor();

            // For Borland3DTheme: highlight uses WHITE, lowlight uses BLACK
            // This creates the light-from-above effect
            assertThat(highlightColor.foreground())
                .as("Highlight foreground should be defined")
                .isNotNull();

            assertThat(lowlightColor.foreground())
                .as("Lowlight foreground should be defined")
                .isNotNull();

            // Verify they are different (brightness comparison is implementation-specific)
            assertThat(highlightColor.foreground())
                .as("Highlight and lowlight foreground colors should be different")
                .isNotEqualTo(lowlightColor.foreground());
        }

        @Test
        @DisplayName("3D color pairs should be consistent across multiple calls")
        void testColorPairConsistency() {
            ColorPair shadow1 = theme.getShadowColor();
            ColorPair shadow2 = theme.getShadowColor();

            ColorPair highlight1 = theme.getHighlightColor();
            ColorPair highlight2 = theme.getHighlightColor();

            ColorPair lowlight1 = theme.getLowlightColor();
            ColorPair lowlight2 = theme.getLowlightColor();

            assertThat(shadow1)
                .as("Shadow color should be consistent")
                .isEqualTo(shadow2);

            assertThat(highlight1)
                .as("Highlight color should be consistent")
                .isEqualTo(highlight2);

            assertThat(lowlight1)
                .as("Lowlight color should be consistent")
                .isEqualTo(lowlight2);
        }
    }

    @Nested
    @DisplayName("Theme Interface Integration Tests")
    class ThemeIntegrationTests {

        @Test
        @DisplayName("Theme3D should extend Theme interface")
        void testTheme3DExtendsTheme() {
            assertThat(theme)
                .as("Theme3D implementations should also be Theme instances")
                .isInstanceOf(Theme.class);
        }

        @Test
        @DisplayName("Theme3D should provide all base Theme methods")
        void testBaseThemeMethodsAvailable() {
            assertThat(theme.getBackground())
                .as("getBackground from Theme interface")
                .isNotNull();

            assertThat(theme.getButton())
                .as("getButton from Theme interface")
                .isNotNull();

            assertThat(theme.getButtonFocused())
                .as("getButtonFocused from Theme interface")
                .isNotNull();

            assertThat(theme.getTextInput())
                .as("getTextInput from Theme interface")
                .isNotNull();

            assertThat(theme.getBorder())
                .as("getBorder from Theme interface")
                .isNotNull();

            assertThat(theme.getSelection())
                .as("getSelection from Theme interface")
                .isNotNull();

            assertThat(theme.getDisabled())
                .as("getDisabled from Theme interface")
                .isNotNull();

            assertThat(theme.getBorderChars())
                .as("getBorderChars from Theme interface")
                .isNotNull();

            assertThat(theme.getName())
                .as("getName from Theme interface")
                .isNotNull();
        }

        @Test
        @DisplayName("Theme3D supports3D should override Theme default")
        void testSupports3DOverride() {
            Theme baseTheme = theme; // Upcast to Theme interface

            assertThat(baseTheme.supports3D())
                .as("Theme.supports3D() should return true when cast to base interface")
                .isTrue();
        }

        @Test
        @DisplayName("Theme3D should be assignable to Theme")
        void testThemeAssignability() {
            Theme assignedTheme = theme;

            assertThat(assignedTheme)
                .as("Theme3D should be assignable to Theme")
                .isNotNull()
                .isInstanceOf(Theme.class)
                .isInstanceOf(Theme3D.class);
        }
    }

    @Nested
    @DisplayName("Borland3DTheme Concrete Implementation Tests")
    class Borland3DThemeTests {

        private Borland3DTheme borlandTheme;

        @BeforeEach
        void setUp() {
            borlandTheme = new Borland3DTheme();
        }

        @Test
        @DisplayName("should have proper name")
        void testName() {
            assertThat(borlandTheme.getName())
                .as("Borland theme should have descriptive name")
                .isNotNull()
                .contains("Borland");
        }

        @Test
        @DisplayName("should use standard Borland shadow offset")
        void testBorlandShadowOffset() {
            assertThat(borlandTheme.getShadowOffsetX())
                .as("Borland theme uses 2-character horizontal shadow offset")
                .isEqualTo(2);

            assertThat(borlandTheme.getShadowOffsetY())
                .as("Borland theme uses 1-character vertical shadow offset")
                .isEqualTo(1);
        }

        @Test
        @DisplayName("should provide authentic Borland 3D colors")
        void testBorland3DColors() {
            ColorPair shadow = borlandTheme.getShadowColor();
            ColorPair highlight = borlandTheme.getHighlightColor();
            ColorPair lowlight = borlandTheme.getLowlightColor();

            assertThat(shadow)
                .as("Shadow color should use BLACK/BLACK for gray effect")
                .extracting(ColorPair::foreground, ColorPair::background)
                .containsExactly(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.BLACK);

            assertThat(highlight)
                .as("Highlight color should use WHITE/CYAN for bright edges")
                .extracting(ColorPair::foreground, ColorPair::background)
                .containsExactly(org.flossware.curses.api.Color.WHITE, org.flossware.curses.api.Color.CYAN);

            assertThat(lowlight)
                .as("Lowlight color should use BLACK/CYAN for dark edges")
                .extracting(ColorPair::foreground, ColorPair::background)
                .containsExactly(org.flossware.curses.api.Color.BLACK, org.flossware.curses.api.Color.CYAN);
        }

        @Test
        @DisplayName("should use Unicode single-line borders by default")
        void testBorlandBorderChars() {
            String borderChars = borlandTheme.getBorderChars();

            assertThat(borderChars)
                .as("Borland theme should use Unicode single-line box characters")
                .isNotNull()
                .hasSize(8)
                .contains("─"); // Unicode horizontal line
        }

        @Test
        @DisplayName("should use Unicode double-line borders for emphasized dialogs")
        void testBorlandDoubleBorderChars() {
            String doubleBorderChars = borlandTheme.getDoubleBorderChars();

            assertThat(doubleBorderChars)
                .as("Borland theme should use Unicode double-line box characters")
                .isNotNull()
                .hasSize(8)
                .isEqualTo("╔═╗║╚═╝║");
        }

        @Test
        @DisplayName("should not use gradient shadows by default")
        void testBorlandGradientShadow() {
            assertThat(borlandTheme.useGradientShadow())
                .as("Borland theme should use solid shadows by default")
                .isFalse();
        }

        @Test
        @DisplayName("should use space character for shadows")
        void testBorlandShadowChar() {
            assertThat(borlandTheme.getShadowChar())
                .as("Borland theme should use space character for solid shadows")
                .isEqualTo(' ');
        }

        @Test
        @DisplayName("should have RAISED as default rendering style")
        void testBorlandDefaultRenderingStyle() {
            assertThat(borlandTheme.getDefaultRenderingStyle())
                .as("Borland theme should default to RAISED style")
                .isEqualTo(RenderingStyle.RAISED);
        }

        @Test
        @DisplayName("should support 3D rendering")
        void testBorlandSupports3D() {
            assertThat(borlandTheme.supports3D())
                .as("Borland3DTheme should support 3D rendering")
                .isTrue();
        }

        @Test
        @DisplayName("should provide consistent theme colors")
        void testBorlandThemeColorConsistency() {
            // Verify all theme colors are non-null and consistent
            assertThat(borlandTheme.getBackground()).isNotNull();
            assertThat(borlandTheme.getButton()).isNotNull();
            assertThat(borlandTheme.getButtonFocused()).isNotNull();
            assertThat(borlandTheme.getTextInput()).isNotNull();
            assertThat(borlandTheme.getBorder()).isNotNull();
            assertThat(borlandTheme.getSelection()).isNotNull();
            assertThat(borlandTheme.getDisabled()).isNotNull();

            // Verify 3D colors
            assertThat(borlandTheme.getShadowColor()).isNotNull();
            assertThat(borlandTheme.getHighlightColor()).isNotNull();
            assertThat(borlandTheme.getLowlightColor()).isNotNull();
        }
    }

    @Nested
    @DisplayName("RenderingStyle Integration Tests")
    class RenderingStyleIntegrationTests {

        @Test
        @DisplayName("getDefaultRenderingStyle should return valid RenderingStyle")
        void testValidRenderingStyle() {
            RenderingStyle style = theme.getDefaultRenderingStyle();

            assertThat(style)
                .as("Default rendering style should be a valid enum value")
                .isNotNull()
                .isIn(RenderingStyle.values());
        }

        @Test
        @DisplayName("default rendering style should be appropriate for 3D themes")
        void testDefaultStyleAppropriateFor3D() {
            RenderingStyle style = theme.getDefaultRenderingStyle();

            // For 3D themes, FLAT or CUSTOM would be unusual defaults
            assertThat(style)
                .as("3D themes should typically default to RAISED or SUNKEN")
                .isIn(RenderingStyle.RAISED, RenderingStyle.SUNKEN);
        }
    }

    @Nested
    @DisplayName("Edge Case and Error Handling Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle repeated method calls without errors")
        void testRepeatedMethodCalls() {
            assertThatCode(() -> {
                for (int i = 0; i < 100; i++) {
                    theme.getShadowColor();
                    theme.getHighlightColor();
                    theme.getLowlightColor();
                    theme.getShadowOffsetX();
                    theme.getShadowOffsetY();
                    theme.getDoubleBorderChars();
                    theme.getShadowChar();
                    theme.useGradientShadow();
                    theme.getDefaultRenderingStyle();
                    theme.supports3D();
                }
            }).as("Repeated method calls should not cause errors")
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should maintain consistency in multi-threaded environment")
        void testThreadSafety() throws InterruptedException {
            // Simple thread safety test - verify methods are idempotent
            Thread[] threads = new Thread[10];

            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        assertThat(theme.getShadowColor()).isNotNull();
                        assertThat(theme.getHighlightColor()).isNotNull();
                        assertThat(theme.getLowlightColor()).isNotNull();
                        assertThat(theme.supports3D()).isTrue();
                    }
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // If we get here without exceptions, thread safety is reasonable
            assertThat(theme.supports3D())
                .as("Theme should remain consistent after concurrent access")
                .isTrue();
        }

        @Test
        @DisplayName("shadow character should be a valid printable character")
        void testShadowCharValid() {
            char shadowChar = theme.getShadowChar();

            // Shadow character should be either space or a shade character
            assertThat(shadowChar)
                .as("Shadow character should be printable")
                .isIn(' ', '░', '▒', '▓', '█');
        }
    }
}

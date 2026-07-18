package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DBase4_3DTheme.
 */
@DisplayName("DBase4_3DTheme Tests")
class DBase4_3DThemeTest {

    private DBase4_3DTheme theme;

    @BeforeEach
    void setUp() {
        theme = new DBase4_3DTheme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("dBASE IV 3D");
    }

    @Test
    @DisplayName("should support 3D effects")
    void shouldSupport3DEffects() {
        assertThat(theme.supports3D()).isTrue();
    }

    @Test
    @DisplayName("should have white on blue background")
    void shouldHaveWhiteOnBlueBackground() {
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.WHITE);
        assertThat(background.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have yellow on blue for buttons")
    void shouldHaveYellowOnBlueForButtons() {
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.YELLOW);
        assertThat(button.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have blue on yellow for focused buttons")
    void shouldHaveBlueOnYellowForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.BLUE);
        assertThat(buttonFocused.background()).isEqualTo(Color.YELLOW);
    }

    @Test
    @DisplayName("should have cyan on blue for text input")
    void shouldHaveCyanOnBlueForTextInput() {
        ColorPair textInput = theme.getTextInput();
        assertThat(textInput.foreground()).isEqualTo(Color.CYAN);
        assertThat(textInput.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have white on blue for borders")
    void shouldHaveWhiteOnBlueForBorders() {
        ColorPair border = theme.getBorder();
        assertThat(border.foreground()).isEqualTo(Color.WHITE);
        assertThat(border.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have blue on white for selection")
    void shouldHaveBlueOnWhiteForSelection() {
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLUE);
        assertThat(selection.background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should have blue on blue for disabled components")
    void shouldHaveBlueOnBlueForDisabled() {
        ColorPair disabled = theme.getDisabled();
        assertThat(disabled.foreground()).isEqualTo(Color.BLUE);
        assertThat(disabled.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should use single-line Unicode box drawing characters by default")
    void shouldUseSingleLineUnicodeBoxDrawing() {
        String borderChars = theme.getBorderChars();
        assertThat(borderChars).isEqualTo("┌─┐│└─┘│");
        assertThat(borderChars).hasSize(8);
    }

    @Test
    @DisplayName("should use double-line Unicode box drawing characters for dialogs")
    void shouldUseDoubleLineUnicodeBoxDrawingForDialogs() {
        String doubleBorderChars = theme.getDoubleBorderChars();
        assertThat(doubleBorderChars).isEqualTo("╔═╗║╚═╝║");
        assertThat(doubleBorderChars).hasSize(8);
    }

    @Test
    @DisplayName("should have black on black shadow for 3D depth effect (simulates gray with A_BOLD)")
    void shouldHaveBlackOnBlackShadow() {
        ColorPair shadow = theme.getShadowColor();
        assertThat(shadow.foreground()).isEqualTo(Color.BLACK);
        assertThat(shadow.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have white on cyan highlight for 3D raised effect")
    void shouldHaveWhiteOnCyanHighlight() {
        ColorPair highlight = theme.getHighlightColor();
        assertThat(highlight.foreground()).isEqualTo(Color.WHITE);
        assertThat(highlight.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have black on cyan lowlight for 3D recessed effect")
    void shouldHaveBlackOnCyanLowlight() {
        ColorPair lowlight = theme.getLowlightColor();
        assertThat(lowlight.foreground()).isEqualTo(Color.BLACK);
        assertThat(lowlight.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have all required color pairs defined")
    void shouldHaveAllRequiredColorPairsDefined() {
        assertThat(theme.getBackground()).isNotNull();
        assertThat(theme.getButton()).isNotNull();
        assertThat(theme.getButtonFocused()).isNotNull();
        assertThat(theme.getTextInput()).isNotNull();
        assertThat(theme.getBorder()).isNotNull();
        assertThat(theme.getSelection()).isNotNull();
        assertThat(theme.getDisabled()).isNotNull();
    }

    @Test
    @DisplayName("should have 3D-specific color pairs defined")
    void shouldHave3DSpecificColorPairsDefined() {
        assertThat(theme.getShadowColor()).isNotNull();
        assertThat(theme.getHighlightColor()).isNotNull();
        assertThat(theme.getLowlightColor()).isNotNull();
    }

    @Test
    @DisplayName("should use blue background consistently across components")
    void shouldUseBlueBackgroundConsistently() {
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getButton().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getTextInput().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBorder().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getDisabled().background()).isEqualTo(Color.BLUE);
        // Note: focused and selected intentionally use different backgrounds for contrast
    }

    @Test
    @DisplayName("should use yellow and white for highlights and focus")
    void shouldUseYellowAndWhiteForHighlights() {
        // Focused buttons use yellow background for visibility
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.YELLOW);
        // Selection uses white background for contrast
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should match classic dBASE IV color scheme")
    void shouldMatchClassicDBase4Colors() {
        // Verify the iconic dBASE IV color scheme
        // Background: white on blue (professional)
        // Buttons: yellow on blue (menu bar)
        // Borders: white on blue
        // Selection: blue on white (inverted)
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButton().foreground()).isEqualTo(Color.YELLOW);
        assertThat(theme.getBorder().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should have contrasting shadow and highlight for 3D depth")
    void shouldHaveContrastingShadowAndHighlight() {
        // Shadow should be darker (black) and highlight should be brighter (white)
        // for proper 3D depth perception
        ColorPair shadow = theme.getShadowColor();
        ColorPair highlight = theme.getHighlightColor();

        assertThat(shadow.foreground()).isEqualTo(Color.BLACK);
        assertThat(highlight.foreground()).isEqualTo(Color.WHITE);

        // Shadow uses black background for A_BOLD gray simulation, highlight uses cyan
        assertThat(shadow.background()).isEqualTo(Color.BLACK);
        assertThat(highlight.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should use single-line borders distinct from base dBASE IV theme")
    void shouldUseSingleLineBordersDistinctFromBaseTheme() {
        DBase4Theme baseTheme = new DBase4Theme();

        // DBase4_3DTheme should use different border characters than DBase4Theme
        assertThat(theme.getBorderChars()).isNotEqualTo(baseTheme.getBorderChars());

        // Base theme uses rounded corners
        assertThat(baseTheme.getBorderChars()).isEqualTo("+-+|+-+|");

        // 3D theme uses single-line (standard for most dialogs)
        assertThat(theme.getBorderChars()).isEqualTo("┌─┐│└─┘│");
    }

    @Test
    @DisplayName("should inherit core dBASE IV color scheme from base theme")
    void shouldInheritCoreDBase4ColorScheme() {
        DBase4Theme baseTheme = new DBase4Theme();

        // All standard color pairs should match the base dBASE IV theme
        assertThat(theme.getBackground()).isEqualTo(baseTheme.getBackground());
        assertThat(theme.getButton()).isEqualTo(baseTheme.getButton());
        assertThat(theme.getButtonFocused()).isEqualTo(baseTheme.getButtonFocused());
        assertThat(theme.getTextInput()).isEqualTo(baseTheme.getTextInput());
        assertThat(theme.getBorder()).isEqualTo(baseTheme.getBorder());
        assertThat(theme.getSelection()).isEqualTo(baseTheme.getSelection());
        assertThat(theme.getDisabled()).isEqualTo(baseTheme.getDisabled());
    }

    @Test
    @DisplayName("single-line border characters should contain proper Unicode box drawing characters")
    void singleLineBorderCharactersShouldContainProperUnicodeBoxDrawing() {
        String borderChars = theme.getBorderChars();

        // Verify each character in the 8-character border string
        assertThat(borderChars.charAt(0)).isEqualTo('┌'); // ┌ - top-left corner
        assertThat(borderChars.charAt(1)).isEqualTo('─'); // ─ - horizontal
        assertThat(borderChars.charAt(2)).isEqualTo('┐'); // ┐ - top-right corner
        assertThat(borderChars.charAt(3)).isEqualTo('│'); // │ - vertical
        assertThat(borderChars.charAt(4)).isEqualTo('└'); // └ - bottom-left corner
        assertThat(borderChars.charAt(5)).isEqualTo('─'); // ─ - horizontal
        assertThat(borderChars.charAt(6)).isEqualTo('┘'); // ┘ - bottom-right corner
        assertThat(borderChars.charAt(7)).isEqualTo('│'); // │ - vertical
    }

    @Test
    @DisplayName("double-line border characters should contain proper Unicode box drawing characters")
    void doubleLineBorderCharactersShouldContainProperUnicodeBoxDrawing() {
        String doubleBorderChars = theme.getDoubleBorderChars();

        // Verify each character in the 8-character double border string
        // Order: top-left, top, top-right, left, bottom-left, bottom, bottom-right, right
        assertThat(doubleBorderChars.charAt(0)).isEqualTo('╔'); // ╔ - top-left corner
        assertThat(doubleBorderChars.charAt(1)).isEqualTo('═'); // ═ - top horizontal
        assertThat(doubleBorderChars.charAt(2)).isEqualTo('╗'); // ╗ - top-right corner
        assertThat(doubleBorderChars.charAt(3)).isEqualTo('║'); // ║ - left vertical
        assertThat(doubleBorderChars.charAt(4)).isEqualTo('╚'); // ╚ - bottom-left corner
        assertThat(doubleBorderChars.charAt(5)).isEqualTo('═'); // ═ - bottom horizontal
        assertThat(doubleBorderChars.charAt(6)).isEqualTo('╝'); // ╝ - bottom-right corner
        assertThat(doubleBorderChars.charAt(7)).isEqualTo('║'); // ║ - right vertical
    }

    @Test
    @DisplayName("should maintain dBASE IV identity while adding 3D enhancement")
    void shouldMaintainDBase4IdentityWithEnhancement() {
        // The theme name should indicate it's a dBASE IV variant
        assertThat(theme.getName()).contains("dBASE IV");
        assertThat(theme.getName()).contains("3D");

        // Should maintain the core dBASE IV blue/white/yellow palette
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);

        // Should add 3D support
        assertThat(theme.supports3D()).isTrue();

        // Should provide 3D-specific methods
        assertThat(theme.getShadowColor()).isNotNull();
        assertThat(theme.getHighlightColor()).isNotNull();
        assertThat(theme.getLowlightColor()).isNotNull();
    }

    @Test
    @DisplayName("shadow and highlight should be suitable for creating depth perception")
    void shadowAndHighlightShouldBeSuitableForDepthPerception() {
        ColorPair shadow = theme.getShadowColor();
        ColorPair highlight = theme.getHighlightColor();
        ColorPair background = theme.getBackground();

        // For proper 3D effect:
        // - Shadow should be darker than background
        // - Highlight should be lighter than background
        // - Shadow uses black background for A_BOLD gray simulation, highlight uses cyan

        // Shadow uses black (darker)
        assertThat(shadow.foreground()).isEqualTo(Color.BLACK);

        // Highlight uses white (lighter)
        assertThat(highlight.foreground()).isEqualTo(Color.WHITE);

        // Background uses white (lighter)
        assertThat(background.foreground()).isEqualTo(Color.WHITE);

        // Shadow uses BLACK background (simulating gray via A_BOLD for authentic dBASE IV 3D effect)
        // Background uses BLUE, highlight uses CYAN for 3D contrast
        assertThat(shadow.background()).isEqualTo(Color.BLACK);
        assertThat(highlight.background()).isEqualTo(Color.CYAN);
        assertThat(background.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have authentic dBASE IV shadow offset (2 right, 1 down)")
    void shouldHaveAuthenticDBase4ShadowOffset() {
        // dBASE IV used a 2-column right, 1-row down shadow offset
        assertThat(theme.getShadowOffsetX()).isEqualTo(2);
        assertThat(theme.getShadowOffsetY()).isEqualTo(1);
    }

    @Test
    @DisplayName("should default to RAISED rendering style")
    void shouldDefaultToRaisedRenderingStyle() {
        assertThat(theme.getDefaultRenderingStyle()).isEqualTo(RenderingStyle.RAISED);
    }

    @Test
    @DisplayName("should differentiate from Borland 3D theme")
    void shouldDifferentiateFromBorland3DTheme() {
        Borland3DTheme borlandTheme = new Borland3DTheme();

        // Different names
        assertThat(theme.getName()).isNotEqualTo(borlandTheme.getName());

        // Different background foreground colors (white vs yellow)
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(borlandTheme.getBackground().foreground()).isEqualTo(Color.YELLOW);

        // Different button colors (yellow vs cyan)
        assertThat(theme.getButton().foreground()).isEqualTo(Color.YELLOW);
        assertThat(borlandTheme.getButton().foreground()).isEqualTo(Color.CYAN);

        // Different selection colors (blue on white vs black on cyan)
        assertThat(theme.getSelection().foreground()).isEqualTo(Color.BLUE);
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
        assertThat(borlandTheme.getSelection().foreground()).isEqualTo(Color.BLACK);
        assertThat(borlandTheme.getSelection().background()).isEqualTo(Color.CYAN);

        // Both support 3D
        assertThat(theme.supports3D()).isTrue();
        assertThat(borlandTheme.supports3D()).isTrue();
    }

    @Test
    @DisplayName("should provide both single-line and double-line border options")
    void shouldProvideBothBorderOptions() {
        String singleLine = theme.getBorderChars();
        String doubleLine = theme.getDoubleBorderChars();

        // Should be different
        assertThat(singleLine).isNotEqualTo(doubleLine);

        // Both should be 8 characters
        assertThat(singleLine).hasSize(8);
        assertThat(doubleLine).hasSize(8);

        // Single-line uses ┌─┐│└─┘│
        assertThat(singleLine).isEqualTo("┌─┐│└─┘│");

        // Double-line uses ╔═╗║╚═╝║
        assertThat(doubleLine).isEqualTo("╔═╗║╚═╝║");
    }

    @Test
    @DisplayName("should use cyan background for lowlight (consistent with highlight)")
    void shouldUseCyanBackgroundForLowlight() {
        ColorPair lowlight = theme.getLowlightColor();
        ColorPair highlight = theme.getHighlightColor();

        // Both should use cyan background for 3D consistency
        assertThat(lowlight.background()).isEqualTo(Color.CYAN);
        assertThat(highlight.background()).isEqualTo(Color.CYAN);

        // But different foreground colors (black vs white)
        assertThat(lowlight.foreground()).isEqualTo(Color.BLACK);
        assertThat(highlight.foreground()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should provide professional database application aesthetic")
    void shouldProvideProfessionalDatabaseApplicationAesthetic() {
        // dBASE IV was known for its professional, businesslike appearance
        // White on blue is more subdued than yellow on blue
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.WHITE);
        assertThat(background.background()).isEqualTo(Color.BLUE);

        // Menu items use yellow for visibility but not as primary text
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.YELLOW);

        // Selection uses inverted colors (blue on white) for clarity
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLUE);
        assertThat(selection.background()).isEqualTo(Color.WHITE);
    }
}

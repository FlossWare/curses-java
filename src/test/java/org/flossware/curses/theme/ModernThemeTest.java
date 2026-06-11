package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ModernTheme.
 */
@DisplayName("ModernTheme Tests")
class ModernThemeTest {

    private ModernTheme theme;

    @BeforeEach
    void setUp() {
        theme = new ModernTheme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("Modern");
    }

    @Test
    @DisplayName("should have white on black background")
    void shouldHaveWhiteOnBlackBackground() {
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.WHITE);
        assertThat(background.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have cyan on black for buttons")
    void shouldHaveCyanOnBlackForButtons() {
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.CYAN);
        assertThat(button.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have yellow on black for focused buttons")
    void shouldHaveYellowOnBlackForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.YELLOW);
        assertThat(buttonFocused.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have white on black for text input")
    void shouldHaveWhiteOnBlackForTextInput() {
        ColorPair textInput = theme.getTextInput();
        assertThat(textInput.foreground()).isEqualTo(Color.WHITE);
        assertThat(textInput.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have white on black for borders")
    void shouldHaveWhiteOnBlackForBorders() {
        ColorPair border = theme.getBorder();
        assertThat(border.foreground()).isEqualTo(Color.WHITE);
        assertThat(border.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have black on yellow for selection")
    void shouldHaveBlackOnYellowForSelection() {
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLACK);
        assertThat(selection.background()).isEqualTo(Color.YELLOW);
    }

    @Test
    @DisplayName("should have black on black for disabled components")
    void shouldHaveBlackOnBlackForDisabled() {
        ColorPair disabled = theme.getDisabled();
        assertThat(disabled.foreground()).isEqualTo(Color.BLACK);
        assertThat(disabled.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should use Unicode box drawing characters")
    void shouldUseUnicodeBoxDrawing() {
        String borderChars = theme.getBorderChars();
        assertThat(borderChars).isEqualTo("┌─┐│└─┘│");
        assertThat(borderChars).hasSize(8);
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
    @DisplayName("should use black background consistently across components")
    void shouldUseBlackBackgroundConsistently() {
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getButton().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getTextInput().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getBorder().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getDisabled().background()).isEqualTo(Color.BLACK);
        // Note: selection intentionally uses yellow background for contrast
    }

    @Test
    @DisplayName("should use yellow for selection highlights")
    void shouldUseYellowForHighlights() {
        // Selection should use yellow for high visibility
        assertThat(theme.getSelection().background()).isEqualTo(Color.YELLOW);
        // Focused buttons use yellow foreground
        assertThat(theme.getButtonFocused().foreground()).isEqualTo(Color.YELLOW);
    }

    @Test
    @DisplayName("should match modern high-contrast color scheme")
    void shouldMatchModernHighContrastColors() {
        // Verify the modern high-contrast color scheme
        // Background: black
        // Text: white
        // Buttons: cyan
        // Focus: yellow
        // Selection: inverted (black on yellow)
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButton().foreground()).isEqualTo(Color.CYAN);
        assertThat(theme.getButtonFocused().foreground()).isEqualTo(Color.YELLOW);
        assertThat(theme.getSelection().background()).isEqualTo(Color.YELLOW);
        assertThat(theme.getSelection().foreground()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have high contrast for accessibility")
    void shouldHaveHighContrastForAccessibility() {
        // White on black - high contrast
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.WHITE);
        assertThat(background.background()).isEqualTo(Color.BLACK);

        // Yellow on black - high contrast
        ColorPair focused = theme.getButtonFocused();
        assertThat(focused.foreground()).isEqualTo(Color.YELLOW);
        assertThat(focused.background()).isEqualTo(Color.BLACK);

        // Black on yellow - high contrast (inverted)
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLACK);
        assertThat(selection.background()).isEqualTo(Color.YELLOW);
    }

    @Test
    @DisplayName("should differentiate focus and selection states")
    void shouldDifferentiateFocusAndSelection() {
        ColorPair focused = theme.getButtonFocused();
        ColorPair selection = theme.getSelection();

        // Focus: yellow foreground on black background
        assertThat(focused.foreground()).isEqualTo(Color.YELLOW);
        assertThat(focused.background()).isEqualTo(Color.BLACK);

        // Selection: inverted - black foreground on yellow background
        assertThat(selection.foreground()).isEqualTo(Color.BLACK);
        assertThat(selection.background()).isEqualTo(Color.YELLOW);

        // They should be visually distinct
        assertThat(focused).isNotEqualTo(selection);
    }

    @Test
    @DisplayName("should use Unicode characters for modern terminals")
    void shouldUseUnicodeForModernTerminals() {
        String borderChars = theme.getBorderChars();

        // Verify it's using Unicode box-drawing characters, not ASCII
        assertThat(borderChars).isNotEqualTo("+-+||+-+"); // Not ASCII
        assertThat(borderChars).contains("┌", "─", "┐", "│", "└", "┘");
    }
}

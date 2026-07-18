package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TRS80Theme.
 */
@DisplayName("TRS80Theme Tests")
class TRS80ThemeTest {

    private TRS80Theme theme;

    @BeforeEach
    void setUp() {
        theme = new TRS80Theme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("TRS-80");
    }

    @Test
    @DisplayName("should have white on black background")
    void shouldHaveWhiteOnBlackBackground() {
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.WHITE);
        assertThat(background.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have white on black for buttons")
    void shouldHaveWhiteOnBlackForButtons() {
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.WHITE);
        assertThat(button.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have black on white for focused buttons")
    void shouldHaveBlackOnWhiteForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.BLACK);
        assertThat(buttonFocused.background()).isEqualTo(Color.WHITE);
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
    @DisplayName("should have black on white for selection")
    void shouldHaveBlackOnWhiteForSelection() {
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLACK);
        assertThat(selection.background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should have black on black for disabled components")
    void shouldHaveBlackOnBlackForDisabled() {
        ColorPair disabled = theme.getDisabled();
        assertThat(disabled.foreground()).isEqualTo(Color.BLACK);
        assertThat(disabled.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should use ASCII block-style box drawing characters")
    void shouldUseAsciiBlockStyleBoxDrawing() {
        String borderChars = theme.getBorderChars();
        assertThat(borderChars).isEqualTo("+-+|+-+|");
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
        assertThat(theme.getTextInput().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getBorder().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getDisabled().background()).isEqualTo(Color.BLACK);
        // Note: focused and selected intentionally use white background for contrast
    }

    @Test
    @DisplayName("should use white for highlights and focus")
    void shouldUseWhiteForHighlights() {
        // Focused and selected items should use white for visibility
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.WHITE);
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should match TRS-80 monochrome aesthetic")
    void shouldMatchTrs80MonochromeAesthetic() {
        // Verify the iconic TRS-80 monochrome color scheme
        // Background: black
        // Text: white
        // Borders: white
        // Highlights: inverted (black on white)
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getBorder().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.WHITE);
        assertThat(theme.getButtonFocused().foreground()).isEqualTo(Color.BLACK);
    }
}

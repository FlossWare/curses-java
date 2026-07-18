package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DBase3Theme.
 */
@DisplayName("DBase3Theme Tests")
class DBase3ThemeTest {

    private DBase3Theme theme;

    @BeforeEach
    void setUp() {
        theme = new DBase3Theme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("dBASE III");
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
    @DisplayName("should have black on cyan for focused buttons")
    void shouldHaveBlackOnCyanForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.BLACK);
        assertThat(buttonFocused.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have green on black for text input")
    void shouldHaveGreenOnBlackForTextInput() {
        ColorPair textInput = theme.getTextInput();
        assertThat(textInput.foreground()).isEqualTo(Color.GREEN);
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
    @DisplayName("should have black on cyan for selection")
    void shouldHaveBlackOnCyanForSelection() {
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLACK);
        assertThat(selection.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have black on black for disabled components")
    void shouldHaveBlackOnBlackForDisabled() {
        ColorPair disabled = theme.getDisabled();
        assertThat(disabled.foreground()).isEqualTo(Color.BLACK);
        assertThat(disabled.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should use ASCII box drawing characters")
    void shouldUseAsciiBoxDrawing() {
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
        // Note: focused and selected intentionally use different backgrounds for contrast
    }

    @Test
    @DisplayName("should use cyan for highlights and focus")
    void shouldUseCyanForHighlights() {
        // Focused and selected items should use cyan for visibility
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.CYAN);
        assertThat(theme.getSelection().background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should match classic dBASE III color scheme")
    void shouldMatchClassicDBase3Colors() {
        // Verify the iconic dBASE III color scheme
        // Background: black
        // Text: white
        // Menus: cyan
        // Data entry: green
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButton().foreground()).isEqualTo(Color.CYAN);
        assertThat(theme.getTextInput().foreground()).isEqualTo(Color.GREEN);
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.CYAN);
    }
}

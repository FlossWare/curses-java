package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BorlandTheme.
 */
@DisplayName("BorlandTheme Tests")
class BorlandThemeTest {

    private BorlandTheme theme;

    @BeforeEach
    void setUp() {
        theme = new BorlandTheme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("Borland");
    }

    @Test
    @DisplayName("should have yellow on blue background")
    void shouldHaveYellowOnBlueBackground() {
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.YELLOW);
        assertThat(background.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have cyan on blue for buttons")
    void shouldHaveCyanOnBlueForButtons() {
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.CYAN);
        assertThat(button.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have black on cyan for focused buttons")
    void shouldHaveBlackOnCyanForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.BLACK);
        assertThat(buttonFocused.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have white on blue for text input")
    void shouldHaveWhiteOnBlueForTextInput() {
        ColorPair textInput = theme.getTextInput();
        assertThat(textInput.foreground()).isEqualTo(Color.WHITE);
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
    @DisplayName("should have black on cyan for selection")
    void shouldHaveBlackOnCyanForSelection() {
        ColorPair selection = theme.getSelection();
        assertThat(selection.foreground()).isEqualTo(Color.BLACK);
        assertThat(selection.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have black on blue for disabled components")
    void shouldHaveBlackOnBlueForDisabled() {
        ColorPair disabled = theme.getDisabled();
        assertThat(disabled.foreground()).isEqualTo(Color.BLACK);
        assertThat(disabled.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should use rounded Unicode box drawing characters")
    void shouldUseRoundedUnicodeBoxDrawing() {
        String borderChars = theme.getBorderChars();
        assertThat(borderChars).isEqualTo("╭─╮│╰─╯│");
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
    @DisplayName("should use cyan for highlights and focus")
    void shouldUseCyanForHighlights() {
        // Focused and selected items should use cyan for visibility
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.CYAN);
        assertThat(theme.getSelection().background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should match classic Borland IDE color scheme")
    void shouldMatchClassicBorlandColors() {
        // Verify the iconic Borland IDE color scheme
        // Background: blue
        // Text: yellow
        // Borders: white
        // Highlights: cyan
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.YELLOW);
        assertThat(theme.getBorder().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.CYAN);
    }
}

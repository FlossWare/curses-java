package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DOSTheme.
 */
@DisplayName("DOSTheme Tests")
class DOSThemeTest {

    private DOSTheme theme;

    @BeforeEach
    void setUp() {
        theme = new DOSTheme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("DOS");
    }

    @Test
    @DisplayName("should have white on black background")
    void shouldHaveWhiteOnBlackBackground() {
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.WHITE);
        assertThat(background.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have yellow on black for buttons")
    void shouldHaveYellowOnBlackForButtons() {
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.YELLOW);
        assertThat(button.background()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("should have black on yellow for focused buttons")
    void shouldHaveBlackOnYellowForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.BLACK);
        assertThat(buttonFocused.background()).isEqualTo(Color.YELLOW);
    }

    @Test
    @DisplayName("should have cyan on black for text input")
    void shouldHaveCyanOnBlackForTextInput() {
        ColorPair textInput = theme.getTextInput();
        assertThat(textInput.foreground()).isEqualTo(Color.CYAN);
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
    @DisplayName("should use ASCII box drawing characters")
    void shouldUseASCIIBoxDrawing() {
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
    @DisplayName("should use bright colors for highlights and focus")
    void shouldUseBrightColorsForHighlights() {
        // Focused items should use yellow for visibility
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.YELLOW);
        // Selected items should use white for high contrast
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should match DOS aesthetic")
    void shouldMatchDOSAesthetic() {
        // Verify the classic DOS color scheme
        // Background: white on black (standard DOS text mode)
        // Buttons: yellow on black (DOS menu highlighting)
        // Borders: white on black (box drawing)
        // Input: cyan on black (form fields)
        // Selection: black on white (inverted selection)
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLACK);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButton().foreground()).isEqualTo(Color.YELLOW);
        assertThat(theme.getBorder().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getTextInput().foreground()).isEqualTo(Color.CYAN);
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
        assertThat(theme.getSelection().foreground()).isEqualTo(Color.BLACK);
    }
}

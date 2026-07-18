package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TI994ATheme.
 */
@DisplayName("TI994ATheme Tests")
class TI994AThemeTest {

    private TI994ATheme theme;

    @BeforeEach
    void setUp() {
        theme = new TI994ATheme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("TI-99/4A");
    }

    @Test
    @DisplayName("should have cyan on blue background")
    void shouldHaveCyanOnBlueBackground() {
        ColorPair background = theme.getBackground();
        assertThat(background.foreground()).isEqualTo(Color.CYAN);
        assertThat(background.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have white on blue for buttons")
    void shouldHaveWhiteOnBlueForButtons() {
        ColorPair button = theme.getButton();
        assertThat(button.foreground()).isEqualTo(Color.WHITE);
        assertThat(button.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have blue on cyan for focused buttons")
    void shouldHaveBlueOnCyanForFocusedButtons() {
        ColorPair buttonFocused = theme.getButtonFocused();
        assertThat(buttonFocused.foreground()).isEqualTo(Color.BLUE);
        assertThat(buttonFocused.background()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should have cyan on blue for text input")
    void shouldHaveCyanOnBlueForTextInput() {
        ColorPair textInput = theme.getTextInput();
        assertThat(textInput.foreground()).isEqualTo(Color.CYAN);
        assertThat(textInput.background()).isEqualTo(Color.BLUE);
    }

    @Test
    @DisplayName("should have cyan on blue for borders")
    void shouldHaveCyanOnBlueForBorders() {
        ColorPair border = theme.getBorder();
        assertThat(border.foreground()).isEqualTo(Color.CYAN);
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
    @DisplayName("should use blue background consistently across components")
    void shouldUseBlueBackgroundConsistently() {
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getButton().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getTextInput().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBorder().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getDisabled().background()).isEqualTo(Color.BLUE);
        // Note: focused uses cyan background, selection uses white background for contrast
    }

    @Test
    @DisplayName("should use cyan for primary text elements")
    void shouldUseCyanForPrimaryText() {
        // TI-99/4A's signature cyan text appearance
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.CYAN);
        assertThat(theme.getTextInput().foreground()).isEqualTo(Color.CYAN);
        assertThat(theme.getBorder().foreground()).isEqualTo(Color.CYAN);
    }

    @Test
    @DisplayName("should match TI-99/4A aesthetic")
    void shouldMatchTI99_4AAesthetic() {
        // Verify the iconic TI-99/4A color scheme
        // Background: cyan on blue
        // Buttons: white on blue
        // Focus: inverted blue on cyan
        // Selection: high contrast blue on white
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.CYAN);
        assertThat(theme.getButton().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButtonFocused().foreground()).isEqualTo(Color.BLUE);
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.CYAN);
        assertThat(theme.getSelection().foreground()).isEqualTo(Color.BLUE);
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }
}

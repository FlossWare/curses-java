package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DBase4Theme.
 */
@DisplayName("DBase4Theme Tests")
class DBase4ThemeTest {

    private DBase4Theme theme;

    @BeforeEach
    void setUp() {
        theme = new DBase4Theme();
    }

    @Test
    @DisplayName("should have correct name")
    void shouldHaveCorrectName() {
        assertThat(theme.getName()).isEqualTo("dBASE IV");
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
    @DisplayName("should use ASCII box drawing characters")
    void shouldUseAsciiBoxDrawing() {
        String borderChars = theme.getBorderChars();
        assertThat(borderChars).isEqualTo("+-+||+-+");
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
    @DisplayName("should use blue background consistently across most components")
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
        // Focused items should use yellow for visibility
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.YELLOW);
        // Selected items should use white for contrast
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("should match classic dBASE IV Control Center color scheme")
    void shouldMatchClassicDBase4Colors() {
        // Verify the iconic dBASE IV Control Center color scheme
        // Background: blue
        // Text: white
        // Menus: yellow
        // Focus: inverted yellow
        // Selection: white
        assertThat(theme.getBackground().background()).isEqualTo(Color.BLUE);
        assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
        assertThat(theme.getButton().foreground()).isEqualTo(Color.YELLOW);
        assertThat(theme.getButtonFocused().background()).isEqualTo(Color.YELLOW);
        assertThat(theme.getButtonFocused().foreground()).isEqualTo(Color.BLUE);
        assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
    }
}

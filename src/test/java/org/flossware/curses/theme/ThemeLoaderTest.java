package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for ThemeLoader JSON parsing, serialization, and ThemeManager integration.
 */
@DisplayName("ThemeLoader Tests")
class ThemeLoaderTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void resetThemeManager() {
        ThemeManager.getInstance().useDefaultTheme();
    }

    // ========================================================================
    // Flat theme parsing
    // ========================================================================

    @Nested
    @DisplayName("Flat Theme Parsing")
    class FlatThemeParsing {

        @Test
        @DisplayName("should parse a minimal flat theme from JSON string")
        void shouldParseMinimalFlatTheme() {
            String json = """
                    {
                      "name": "Test",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);

            assertThat(theme.getName()).isEqualTo("Test");
            assertThat(theme.getBackground().foreground()).isEqualTo(Color.WHITE);
            assertThat(theme.getBackground().background()).isEqualTo(Color.BLACK);
            assertThat(theme.getButton().foreground()).isEqualTo(Color.CYAN);
            assertThat(theme.getButtonFocused().foreground()).isEqualTo(Color.BLACK);
            assertThat(theme.getButtonFocused().background()).isEqualTo(Color.CYAN);
            assertThat(theme.getTextInput().foreground()).isEqualTo(Color.GREEN);
            assertThat(theme.getBorder().foreground()).isEqualTo(Color.WHITE);
            assertThat(theme.getSelection().foreground()).isEqualTo(Color.BLACK);
            assertThat(theme.getSelection().background()).isEqualTo(Color.WHITE);
            assertThat(theme.getDisabled().foreground()).isEqualTo(Color.WHITE);
            assertThat(theme.getBorderChars()).isEqualTo("+-+||+-+");
            assertThat(theme.supports3D()).isFalse();
            assertThat(theme).isNotInstanceOf(Theme3D.class);
        }

        @Test
        @DisplayName("should parse flat theme with Unicode border chars")
        void shouldParseFlatThemeWithUnicodeBorders() {
            String json = """
                    {
                      "name": "Unicode",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "CYAN", "bg": "BLACK" },
                        "button": { "fg": "BLUE", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "BLUE" },
                        "text_input": { "fg": "WHITE", "bg": "BLACK" },
                        "border": { "fg": "BLUE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "CYAN" },
                        "disabled": { "fg": "BLUE", "bg": "BLACK" }
                      },
                      "borders": { "single": "┌─┐│└─┘│" }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);

            assertThat(theme.getBorderChars()).hasSize(8);
        }

        @Test
        @DisplayName("should parse flat theme from file")
        void shouldParseFlatThemeFromFile() throws IOException {
            String json = """
                    {
                      "name": "FromFile",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "RED", "bg": "WHITE" },
                        "button": { "fg": "BLUE", "bg": "WHITE" },
                        "button_focused": { "fg": "WHITE", "bg": "BLUE" },
                        "text_input": { "fg": "BLACK", "bg": "WHITE" },
                        "border": { "fg": "RED", "bg": "WHITE" },
                        "selection": { "fg": "WHITE", "bg": "RED" },
                        "disabled": { "fg": "MAGENTA", "bg": "WHITE" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            Path file = tempDir.resolve("test.json");
            Files.writeString(file, json);

            Theme theme = ThemeLoader.fromJson(file);

            assertThat(theme.getName()).isEqualTo("FromFile");
            assertThat(theme.getBackground().foreground()).isEqualTo(Color.RED);
            assertThat(theme.getBackground().background()).isEqualTo(Color.WHITE);
        }

        @Test
        @DisplayName("should ignore extra fields in flat theme JSON")
        void shouldIgnoreExtraFields() {
            String json = """
                    {
                      "name": "Extra",
                      "version": "1.0",
                      "description": "A theme with extra fields",
                      "metadata": { "author": "Test" },
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);
            assertThat(theme.getName()).isEqualTo("Extra");
        }
    }

    // ========================================================================
    // 3D theme parsing
    // ========================================================================

    @Nested
    @DisplayName("3D Theme Parsing")
    class ThreeDThemeParsing {

        @Test
        @DisplayName("should parse 3D theme from JSON string")
        void shouldParse3DTheme() {
            String json = """
                    {
                      "name": "Test 3D",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "YELLOW", "bg": "BLUE" },
                        "button": { "fg": "CYAN", "bg": "BLUE" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLUE" },
                        "selection": { "fg": "BLACK", "bg": "CYAN" },
                        "disabled": { "fg": "BLUE", "bg": "BLUE" }
                      },
                      "borders": {
                        "single": "┌─┐│└─┘│",
                        "double": "╔═╗║╚═╝║"
                      },
                      "3d": {
                        "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
                        "highlight_color": { "fg": "WHITE", "bg": "CYAN" },
                        "lowlight_color": { "fg": "BLACK", "bg": "CYAN" },
                        "shadow_offset": { "x": 2, "y": 1 },
                        "rendering_style": "RAISED"
                      }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);

            assertThat(theme).isInstanceOf(Theme3D.class);
            assertThat(theme.supports3D()).isTrue();
            assertThat(theme.getName()).isEqualTo("Test 3D");

            Theme3D theme3d = (Theme3D) theme;
            assertThat(theme3d.getShadowColor().foreground()).isEqualTo(Color.BLACK);
            assertThat(theme3d.getShadowColor().background()).isEqualTo(Color.BLACK);
            assertThat(theme3d.getHighlightColor().foreground()).isEqualTo(Color.WHITE);
            assertThat(theme3d.getHighlightColor().background()).isEqualTo(Color.CYAN);
            assertThat(theme3d.getLowlightColor().foreground()).isEqualTo(Color.BLACK);
            assertThat(theme3d.getLowlightColor().background()).isEqualTo(Color.CYAN);
            assertThat(theme3d.getShadowOffsetX()).isEqualTo(2);
            assertThat(theme3d.getShadowOffsetY()).isEqualTo(1);
            assertThat(theme3d.getDefaultRenderingStyle()).isEqualTo(RenderingStyle.RAISED);
        }

        @Test
        @DisplayName("should parse 3D theme with double border chars")
        void shouldParse3DThemeWithDoubleBorders() {
            String json = """
                    {
                      "name": "Double Border 3D",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLUE" },
                        "button": { "fg": "YELLOW", "bg": "BLUE" },
                        "button_focused": { "fg": "BLUE", "bg": "YELLOW" },
                        "text_input": { "fg": "CYAN", "bg": "BLUE" },
                        "border": { "fg": "WHITE", "bg": "BLUE" },
                        "selection": { "fg": "BLUE", "bg": "WHITE" },
                        "disabled": { "fg": "BLUE", "bg": "BLUE" }
                      },
                      "borders": {
                        "single": "┌─┐│└─┘│",
                        "double": "╔═╗║╚═╝║"
                      },
                      "3d": {
                        "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
                        "highlight_color": { "fg": "WHITE", "bg": "CYAN" },
                        "lowlight_color": { "fg": "BLACK", "bg": "CYAN" },
                        "shadow_offset": { "x": 2, "y": 1 },
                        "rendering_style": "RAISED"
                      }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);
            Theme3D theme3d = (Theme3D) theme;

            assertThat(theme3d.getDoubleBorderChars()).hasSize(8);
        }

        @Test
        @DisplayName("should default rendering style to RAISED when not specified")
        void shouldDefaultRenderingStyleToRaised() {
            String json = """
                    {
                      "name": "No Style",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" },
                      "3d": {
                        "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
                        "highlight_color": { "fg": "WHITE", "bg": "WHITE" },
                        "lowlight_color": { "fg": "BLACK", "bg": "BLACK" },
                        "shadow_offset": { "x": 1, "y": 1 }
                      }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);
            Theme3D theme3d = (Theme3D) theme;

            assertThat(theme3d.getDefaultRenderingStyle()).isEqualTo(RenderingStyle.RAISED);
        }

        @Test
        @DisplayName("should parse SUNKEN rendering style")
        void shouldParseSunkenRenderingStyle() {
            String json = """
                    {
                      "name": "Sunken",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" },
                      "3d": {
                        "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
                        "highlight_color": { "fg": "WHITE", "bg": "WHITE" },
                        "lowlight_color": { "fg": "BLACK", "bg": "BLACK" },
                        "shadow_offset": { "x": 1, "y": 1 },
                        "rendering_style": "SUNKEN"
                      }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);
            Theme3D theme3d = (Theme3D) theme;

            assertThat(theme3d.getDefaultRenderingStyle()).isEqualTo(RenderingStyle.SUNKEN);
        }

        @Test
        @DisplayName("3D theme should use default double border when not specified")
        void shouldUseDefaultDoubleBorderWhenNotSpecified() {
            String json = """
                    {
                      "name": "No Double",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" },
                      "3d": {
                        "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
                        "highlight_color": { "fg": "WHITE", "bg": "WHITE" },
                        "lowlight_color": { "fg": "BLACK", "bg": "BLACK" },
                        "shadow_offset": { "x": 1, "y": 1 }
                      }
                    }
                    """;

            Theme theme = ThemeLoader.fromJson(json);
            Theme3D theme3d = (Theme3D) theme;

            // Should fall back to Theme3D default
            assertThat(theme3d.getDoubleBorderChars()).isEqualTo("╔═╗║║╚═╝");
        }
    }

    // ========================================================================
    // Round-trip serialization
    // ========================================================================

    @Nested
    @DisplayName("Round-trip Serialization")
    class RoundTripSerialization {

        @Test
        @DisplayName("should round-trip a flat theme through toJson/fromJson")
        void shouldRoundTripFlatTheme() {
            Theme original = new DefaultTheme();

            String json = ThemeLoader.toJson(original);
            Theme restored = ThemeLoader.fromJson(json);

            assertThat(restored.getName()).isEqualTo(original.getName());
            assertColorPairEqual(restored.getBackground(), original.getBackground());
            assertColorPairEqual(restored.getButton(), original.getButton());
            assertColorPairEqual(restored.getButtonFocused(), original.getButtonFocused());
            assertColorPairEqual(restored.getTextInput(), original.getTextInput());
            assertColorPairEqual(restored.getBorder(), original.getBorder());
            assertColorPairEqual(restored.getSelection(), original.getSelection());
            assertColorPairEqual(restored.getDisabled(), original.getDisabled());
            assertThat(restored.getBorderChars()).isEqualTo(original.getBorderChars());
            assertThat(restored.supports3D()).isEqualTo(original.supports3D());
        }

        @Test
        @DisplayName("should round-trip a 3D theme through toJson/fromJson")
        void shouldRoundTrip3DTheme() {
            Theme3D original = new DBase4_3DTheme();

            String json = ThemeLoader.toJson(original);
            Theme restored = ThemeLoader.fromJson(json);

            assertThat(restored).isInstanceOf(Theme3D.class);
            Theme3D restored3d = (Theme3D) restored;

            assertThat(restored3d.getName()).isEqualTo(original.getName());
            assertColorPairEqual(restored3d.getBackground(), original.getBackground());
            assertColorPairEqual(restored3d.getShadowColor(), original.getShadowColor());
            assertColorPairEqual(restored3d.getHighlightColor(), original.getHighlightColor());
            assertColorPairEqual(restored3d.getLowlightColor(), original.getLowlightColor());
            assertThat(restored3d.getShadowOffsetX()).isEqualTo(original.getShadowOffsetX());
            assertThat(restored3d.getShadowOffsetY()).isEqualTo(original.getShadowOffsetY());
            assertThat(restored3d.getDefaultRenderingStyle()).isEqualTo(original.getDefaultRenderingStyle());
        }

        @Test
        @DisplayName("toJson should produce valid JSON with all required fields")
        void toJsonShouldProduceValidJson() {
            Theme theme = new DarkTheme();

            String json = ThemeLoader.toJson(theme);

            assertThat(json).contains("\"name\"");
            assertThat(json).contains("\"version\"");
            assertThat(json).contains("\"colors\"");
            assertThat(json).contains("\"borders\"");
            assertThat(json).contains("\"background\"");
            assertThat(json).contains("\"button\"");
            assertThat(json).contains("\"button_focused\"");
            assertThat(json).contains("\"text_input\"");
            assertThat(json).contains("\"border\"");
            assertThat(json).contains("\"selection\"");
            assertThat(json).contains("\"disabled\"");
            assertThat(json).doesNotContain("\"3d\"");
        }

        @Test
        @DisplayName("toJson for 3D theme should include 3d section")
        void toJsonFor3DThemeShouldInclude3DSection() {
            Theme theme = new Borland3DTheme();

            String json = ThemeLoader.toJson(theme);

            assertThat(json).contains("\"3d\"");
            assertThat(json).contains("\"shadow_color\"");
            assertThat(json).contains("\"highlight_color\"");
            assertThat(json).contains("\"lowlight_color\"");
            assertThat(json).contains("\"shadow_offset\"");
            assertThat(json).contains("\"rendering_style\"");
        }
    }

    // ========================================================================
    // Error handling
    // ========================================================================

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("should throw ThemeLoadException for invalid JSON")
        void shouldThrowForInvalidJson() {
            assertThatThrownBy(() -> ThemeLoader.fromJson("not json"))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("Invalid JSON");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for missing name field")
        void shouldThrowForMissingName() {
            String json = """
                    {
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for missing colors object")
        void shouldThrowForMissingColors() {
            String json = """
                    {
                      "name": "Bad",
                      "version": "1.0",
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("colors");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for missing borders object")
        void shouldThrowForMissingBorders() {
            String json = """
                    {
                      "name": "Bad",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("borders");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for invalid color name")
        void shouldThrowForInvalidColorName() {
            String json = """
                    {
                      "name": "Bad Color",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "PURPLE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("Invalid color name")
                    .hasMessageContaining("PURPLE");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for wrong border char length")
        void shouldThrowForWrongBorderCharLength() {
            String json = """
                    {
                      "name": "Short Border",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+" }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("8 characters");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for missing color pair field")
        void shouldThrowForMissingColorPairField() {
            String json = """
                    {
                      "name": "Missing",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "WHITE", "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("disabled");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for non-existent file")
        void shouldThrowForNonExistentFile() {
            Path nonExistent = tempDir.resolve("does-not-exist.json");

            assertThatThrownBy(() -> ThemeLoader.fromJson(nonExistent))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("Failed to read theme file");
        }

        @Test
        @DisplayName("should throw ThemeLoadException for empty JSON object")
        void shouldThrowForEmptyJsonObject() {
            assertThatThrownBy(() -> ThemeLoader.fromJson("{}"))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class);
        }

        @Test
        @DisplayName("should throw ThemeLoadException for missing fg in color pair")
        void shouldThrowForMissingFgInColorPair() {
            String json = """
                    {
                      "name": "Bad",
                      "version": "1.0",
                      "colors": {
                        "background": { "bg": "BLACK" },
                        "button": { "fg": "CYAN", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "WHITE", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "WHITE" },
                        "disabled": { "fg": "WHITE", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            assertThatThrownBy(() -> ThemeLoader.fromJson(json))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("fg");
        }
    }

    // ========================================================================
    // Built-in JSON theme files
    // ========================================================================

    @Nested
    @DisplayName("Built-in JSON Theme Files")
    class BuiltInThemeFiles {

        private Path themesDir;

        @BeforeEach
        void setUp() {
            // Navigate from the worktree root to the themes directory
            themesDir = Path.of(System.getProperty("user.dir")).resolve("themes");
            // If running from a different dir, try project root
            if (!Files.exists(themesDir)) {
                themesDir = Path.of("").toAbsolutePath().resolve("themes");
            }
        }

        @Test
        @DisplayName("dark.json should load and match DarkTheme")
        void darkJsonShouldMatchDarkTheme() {
            Path file = themesDir.resolve("dark.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new DarkTheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("default.json should load and match DefaultTheme")
        void defaultJsonShouldMatchDefaultTheme() {
            Path file = themesDir.resolve("default.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new DefaultTheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("light.json should load and match LightTheme")
        void lightJsonShouldMatchLightTheme() {
            Path file = themesDir.resolve("light.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new LightTheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("modern.json should load and match ModernTheme")
        void modernJsonShouldMatchModernTheme() {
            Path file = themesDir.resolve("modern.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new ModernTheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("borland.json should load and match BorlandTheme")
        void borlandJsonShouldMatchBorlandTheme() {
            Path file = themesDir.resolve("borland.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new BorlandTheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("ti994a.json should load and match TI994ATheme")
        void ti994aJsonShouldMatchTI994ATheme() {
            Path file = themesDir.resolve("ti994a.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new TI994ATheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("trs80.json should load and match TRS80Theme")
        void trs80JsonShouldMatchTRS80Theme() {
            Path file = themesDir.resolve("trs80.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new TRS80Theme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("dos.json should load and match DOSTheme")
        void dosJsonShouldMatchDOSTheme() {
            Path file = themesDir.resolve("dos.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new DOSTheme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("dbase3.json should load and match DBase3Theme")
        void dbase3JsonShouldMatchDBase3Theme() {
            Path file = themesDir.resolve("dbase3.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new DBase3Theme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("dbase4.json should load and match DBase4Theme")
        void dbase4JsonShouldMatchDBase4Theme() {
            Path file = themesDir.resolve("dbase4.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);
            Theme java = new DBase4Theme();

            assertThemesMatch(loaded, java);
        }

        @Test
        @DisplayName("borland3d.json should load as Theme3D")
        void borland3dJsonShouldLoadAsTheme3D() {
            Path file = themesDir.resolve("borland3d.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);

            assertThat(loaded).isInstanceOf(Theme3D.class);
            assertThat(loaded.getName()).isEqualTo("Borland 3D");
            assertThat(loaded.supports3D()).isTrue();
        }

        @Test
        @DisplayName("dbase4-3d.json should load as Theme3D")
        void dbase43dJsonShouldLoadAsTheme3D() {
            Path file = themesDir.resolve("dbase4-3d.json");
            if (!Files.exists(file)) { return; }

            Theme loaded = ThemeLoader.fromJson(file);

            assertThat(loaded).isInstanceOf(Theme3D.class);
            assertThat(loaded.getName()).isEqualTo("dBASE IV 3D");
            assertThat(loaded.supports3D()).isTrue();
        }

        @Test
        @DisplayName("all 12 JSON theme files should load without error")
        void allJsonThemeFilesShouldLoad() {
            String[] files = {
                "dark.json", "default.json", "light.json", "modern.json",
                "borland.json", "borland3d.json",
                "ti994a.json", "trs80.json", "dos.json",
                "dbase3.json", "dbase4.json", "dbase4-3d.json"
            };

            for (String filename : files) {
                Path file = themesDir.resolve(filename);
                if (!Files.exists(file)) { continue; }

                Theme theme = ThemeLoader.fromJson(file);
                assertThat(theme.getName())
                        .as("Theme from %s should have a name", filename)
                        .isNotEmpty();
                assertThat(theme.getBorderChars())
                        .as("Theme from %s should have 8 border chars", filename)
                        .hasSize(8);
            }
        }
    }

    // ========================================================================
    // ThemeManager integration
    // ========================================================================

    @Nested
    @DisplayName("ThemeManager Integration")
    class ThemeManagerIntegration {

        @Test
        @DisplayName("loadThemeFromJson should set the loaded theme as current")
        void loadThemeFromJsonShouldSetCurrentTheme() throws IOException {
            String json = """
                    {
                      "name": "Loaded",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "RED", "bg": "BLACK" },
                        "button": { "fg": "YELLOW", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "YELLOW" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "RED", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "RED" },
                        "disabled": { "fg": "RED", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            Path file = tempDir.resolve("loaded.json");
            Files.writeString(file, json);

            ThemeManager manager = ThemeManager.getInstance();
            manager.loadThemeFromJson(file);

            assertThat(manager.getCurrentTheme().getName()).isEqualTo("Loaded");
            assertThat(manager.getCurrentTheme().getBackground().foreground()).isEqualTo(Color.RED);
        }

        @Test
        @DisplayName("loadThemesFromDirectory should load all JSON files except schema.json")
        void loadThemesFromDirectoryShouldLoadAllJsonFiles() throws IOException {
            // Create some theme files
            writeMinimalTheme(tempDir.resolve("alpha.json"), "Alpha");
            writeMinimalTheme(tempDir.resolve("beta.json"), "Beta");
            Files.writeString(tempDir.resolve("schema.json"), "{}");

            ThemeManager manager = ThemeManager.getInstance();
            int loaded = manager.loadThemesFromDirectory(tempDir);

            assertThat(loaded).isEqualTo(2);
            assertThat(manager.getAvailableThemes()).containsKey("Alpha");
            assertThat(manager.getAvailableThemes()).containsKey("Beta");
        }

        @Test
        @DisplayName("loadThemesFromDirectory should skip invalid JSON files")
        void loadThemesFromDirectoryShouldSkipInvalidFiles() throws IOException {
            writeMinimalTheme(tempDir.resolve("good.json"), "Good");
            Files.writeString(tempDir.resolve("bad.json"), "not valid json");

            ThemeManager manager = ThemeManager.getInstance();
            int loaded = manager.loadThemesFromDirectory(tempDir);

            assertThat(loaded).isEqualTo(1);
            assertThat(manager.getAvailableThemes()).containsKey("Good");
        }

        @Test
        @DisplayName("loadThemesFromDirectory should skip non-json files")
        void loadThemesFromDirectoryShouldSkipNonJsonFiles() throws IOException {
            writeMinimalTheme(tempDir.resolve("theme.json"), "Theme");
            Files.writeString(tempDir.resolve("readme.txt"), "Not a theme");

            ThemeManager manager = ThemeManager.getInstance();
            int loaded = manager.loadThemesFromDirectory(tempDir);

            assertThat(loaded).isEqualTo(1);
        }

        @Test
        @DisplayName("useTheme should switch to a registered theme by name")
        void useThemeShouldSwitchByName() {
            ThemeManager manager = ThemeManager.getInstance();

            boolean result = manager.useTheme("Dark");

            assertThat(result).isTrue();
            assertThat(manager.getCurrentTheme().getName()).isEqualTo("Dark");
        }

        @Test
        @DisplayName("useTheme should return false for unknown theme name")
        void useThemeShouldReturnFalseForUnknown() {
            ThemeManager manager = ThemeManager.getInstance();

            boolean result = manager.useTheme("NonExistentTheme");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("getAvailableThemes should include all 12 built-in themes")
        void getAvailableThemesShouldIncludeBuiltInThemes() {
            ThemeManager manager = ThemeManager.getInstance();
            Map<String, Theme> themes = manager.getAvailableThemes();

            // Singleton may have extra themes from other tests, so check at-least
            assertThat(themes).containsKey("Default");
            assertThat(themes).containsKey("Dark");
            assertThat(themes).containsKey("Light");
            assertThat(themes).containsKey("Modern");
            assertThat(themes).containsKey("Borland");
            assertThat(themes).containsKey("Borland 3D");
            assertThat(themes).containsKey("TI-99/4A");
            assertThat(themes).containsKey("TRS-80");
            assertThat(themes).containsKey("DOS");
            assertThat(themes).containsKey("dBASE III");
            assertThat(themes).containsKey("dBASE IV");
            assertThat(themes).containsKey("dBASE IV 3D");
            assertThat(themes.size()).isGreaterThanOrEqualTo(12);
        }

        @Test
        @DisplayName("getAvailableThemes should return unmodifiable map")
        void getAvailableThemesShouldReturnUnmodifiableMap() {
            ThemeManager manager = ThemeManager.getInstance();
            Map<String, Theme> themes = manager.getAvailableThemes();

            assertThatThrownBy(() -> themes.put("test", new DefaultTheme()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should be able to cycle through all built-in themes by name")
        void shouldCycleThroughAllThemesByName() {
            ThemeManager manager = ThemeManager.getInstance();
            Map<String, Theme> themes = manager.getAvailableThemes();

            for (String name : themes.keySet()) {
                boolean switched = manager.useTheme(name);
                assertThat(switched)
                        .as("Should switch to theme: %s", name)
                        .isTrue();
                assertThat(manager.getCurrentTheme().getName()).isEqualTo(name);
            }
        }

        @Test
        @DisplayName("loadThemesFromDirectory should throw for non-existent directory")
        void loadThemesFromDirectoryShouldThrowForBadDir() {
            ThemeManager manager = ThemeManager.getInstance();
            Path badDir = tempDir.resolve("non-existent-dir");

            assertThatThrownBy(() -> manager.loadThemesFromDirectory(badDir))
                    .isInstanceOf(ThemeLoader.ThemeLoadException.class)
                    .hasMessageContaining("Failed to read theme directory");
        }

        @Test
        @DisplayName("loaded JSON theme should be accessible via useTheme after loadThemeFromJson")
        void loadedJsonThemeShouldBeAccessibleViaUseTheme() throws IOException {
            String json = """
                    {
                      "name": "Custom User",
                      "version": "1.0",
                      "colors": {
                        "background": { "fg": "MAGENTA", "bg": "BLACK" },
                        "button": { "fg": "YELLOW", "bg": "BLACK" },
                        "button_focused": { "fg": "BLACK", "bg": "YELLOW" },
                        "text_input": { "fg": "GREEN", "bg": "BLACK" },
                        "border": { "fg": "MAGENTA", "bg": "BLACK" },
                        "selection": { "fg": "BLACK", "bg": "MAGENTA" },
                        "disabled": { "fg": "RED", "bg": "BLACK" }
                      },
                      "borders": { "single": "+-+||+-+" }
                    }
                    """;

            Path file = tempDir.resolve("custom.json");
            Files.writeString(file, json);

            ThemeManager manager = ThemeManager.getInstance();
            manager.loadThemeFromJson(file);

            // Switch away
            manager.useDefaultTheme();
            assertThat(manager.getCurrentTheme().getName()).isEqualTo("Default");

            // Switch back by name
            boolean result = manager.useTheme("Custom User");
            assertThat(result).isTrue();
            assertThat(manager.getCurrentTheme().getName()).isEqualTo("Custom User");
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void assertColorPairEqual(ColorPair actual, ColorPair expected) {
        assertThat(actual.foreground())
                .as("foreground color")
                .isEqualTo(expected.foreground());
        assertThat(actual.background())
                .as("background color")
                .isEqualTo(expected.background());
    }

    private void assertThemesMatch(Theme loaded, Theme java) {
        assertThat(loaded.getName()).isEqualTo(java.getName());
        assertColorPairEqual(loaded.getBackground(), java.getBackground());
        assertColorPairEqual(loaded.getButton(), java.getButton());
        assertColorPairEqual(loaded.getButtonFocused(), java.getButtonFocused());
        assertColorPairEqual(loaded.getTextInput(), java.getTextInput());
        assertColorPairEqual(loaded.getBorder(), java.getBorder());
        assertColorPairEqual(loaded.getSelection(), java.getSelection());
        assertColorPairEqual(loaded.getDisabled(), java.getDisabled());
        assertThat(loaded.getBorderChars()).isEqualTo(java.getBorderChars());
    }

    private void writeMinimalTheme(Path path, String name) throws IOException {
        String json = """
                {
                  "name": "%s",
                  "version": "1.0",
                  "colors": {
                    "background": { "fg": "WHITE", "bg": "BLACK" },
                    "button": { "fg": "CYAN", "bg": "BLACK" },
                    "button_focused": { "fg": "BLACK", "bg": "CYAN" },
                    "text_input": { "fg": "GREEN", "bg": "BLACK" },
                    "border": { "fg": "WHITE", "bg": "BLACK" },
                    "selection": { "fg": "BLACK", "bg": "WHITE" },
                    "disabled": { "fg": "WHITE", "bg": "BLACK" }
                  },
                  "borders": { "single": "+-+||+-+" }
                }
                """.formatted(name);
        Files.writeString(path, json);
    }
}

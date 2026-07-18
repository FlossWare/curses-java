package org.flossware.curses.theme;

import org.flossware.curses.api.ColorPair;
import org.flossware.curses.ffi.NcursesBridge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional coverage tests for Borland3DTheme covering constructor variants,
 * BorderStyle enum, shadow/shade methods, Borland-specific colors, and toString.
 */
@DisplayName("Borland3DTheme Coverage Tests")
class Borland3DThemeCoverageTest {

    @Nested
    @DisplayName("Constructor variants")
    class ConstructorTests {

        @Test
        @DisplayName("should create with default constructor")
        void testDefaultConstructor() {
            Borland3DTheme theme = new Borland3DTheme();
            assertNotNull(theme);
            assertEquals("Borland 3D", theme.getName());
        }

        @Test
        @DisplayName("should create with custom border style")
        void testCustomBorderStyle() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.DOUBLE_LINE, true, false, true);
            assertEquals("╔═╗║╚═╝║", theme.getBorderChars());
        }

        @Test
        @DisplayName("should create with ASCII border style")
        void testAsciiBorderStyle() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.ASCII, true, false, true);
            assertEquals("+-+|+-+|", theme.getBorderChars());
        }

        @Test
        @DisplayName("should create with rounded border style")
        void testRoundedBorderStyle() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.ROUNDED, true, false, true);
            assertEquals("╭─╮│╰─╯│", theme.getBorderChars());
        }

        @Test
        @DisplayName("should create with non-adaptive shadow offset")
        void testNonAdaptiveShadow() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.SINGLE_LINE, false, false, true);
            assertEquals(2, theme.getShadowOffsetX());
            assertEquals(1, theme.getShadowOffsetY());
        }

        @Test
        @DisplayName("should create with gradient shadows enabled")
        void testGradientShadows() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.SINGLE_LINE, true, true, true);
            assertTrue(theme.useGradientShadow());
            assertEquals('░', theme.getShadowChar()); // light shade
        }

        @Test
        @DisplayName("should create with bold intensity disabled")
        void testBoldDisabled() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.SINGLE_LINE, true, false, false);
            assertFalse(theme.useBoldIntensity());
        }
    }

    @Nested
    @DisplayName("BorderStyle enum")
    class BorderStyleTests {

        @Test
        @DisplayName("should return correct characters for SINGLE_LINE")
        void testSingleLineChars() {
            assertEquals("┌─┐│└─┘│", Borland3DTheme.BorderStyle.SINGLE_LINE.getCharacters());
        }

        @Test
        @DisplayName("should return correct characters for DOUBLE_LINE")
        void testDoubleLineChars() {
            assertEquals("╔═╗║╚═╝║", Borland3DTheme.BorderStyle.DOUBLE_LINE.getCharacters());
        }

        @Test
        @DisplayName("should return correct characters for ROUNDED")
        void testRoundedChars() {
            assertEquals("╭─╮│╰─╯│", Borland3DTheme.BorderStyle.ROUNDED.getCharacters());
        }

        @Test
        @DisplayName("should return correct characters for ASCII")
        void testAsciiChars() {
            assertEquals("+-+|+-+|", Borland3DTheme.BorderStyle.ASCII.getCharacters());
        }

        @Test
        @DisplayName("should have all four values")
        void testAllValues() {
            assertEquals(4, Borland3DTheme.BorderStyle.values().length);
        }

        @Test
        @DisplayName("should support valueOf")
        void testValueOf() {
            assertEquals(Borland3DTheme.BorderStyle.SINGLE_LINE,
                Borland3DTheme.BorderStyle.valueOf("SINGLE_LINE"));
        }
    }

    @Nested
    @DisplayName("Shadow and shade methods")
    class ShadowTests {

        @Test
        @DisplayName("should return shadow offset X")
        void testShadowOffsetX() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals(2, theme.getShadowOffsetX());
        }

        @Test
        @DisplayName("should return shadow offset Y")
        void testShadowOffsetY() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals(1, theme.getShadowOffsetY());
        }

        @Test
        @DisplayName("should return space for shadow char when gradient disabled")
        void testShadowCharNoGradient() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals(' ', theme.getShadowChar());
        }

        @Test
        @DisplayName("should return shade char for shadow char when gradient enabled")
        void testShadowCharWithGradient() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.SINGLE_LINE, true, true, true);
            assertEquals('░', theme.getShadowChar());
        }

        @Test
        @DisplayName("should return double border chars")
        void testDoubleBorderChars() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals("╔═╗║╚═╝║", theme.getDoubleBorderChars());
        }

        @Test
        @DisplayName("should not use gradient shadow by default")
        void testGradientShadowDefault() {
            Borland3DTheme theme = new Borland3DTheme();
            assertFalse(theme.useGradientShadow());
        }
    }

    @Nested
    @DisplayName("Shade characters")
    class ShadeCharacterTests {

        private final Borland3DTheme theme = new Borland3DTheme();

        @Test
        @DisplayName("should return light shade at intensity 0")
        void testShadeIntensity0() {
            assertEquals('░', theme.getShadeCharacter(0));
        }

        @Test
        @DisplayName("should return medium shade at intensity 1")
        void testShadeIntensity1() {
            assertEquals('▒', theme.getShadeCharacter(1));
        }

        @Test
        @DisplayName("should return dark shade at intensity 2")
        void testShadeIntensity2() {
            assertEquals('▓', theme.getShadeCharacter(2));
        }

        @Test
        @DisplayName("should return solid block at intensity 3")
        void testShadeIntensity3() {
            assertEquals('█', theme.getShadeCharacter(3));
        }

        @Test
        @DisplayName("should return light shade for default intensity")
        void testShadeIntensityDefault() {
            assertEquals('░', theme.getShadeCharacter(99));
        }

        @Test
        @DisplayName("should return CP437 code 176 at intensity 0")
        void testShadeCP437Intensity0() {
            assertEquals(176, theme.getShadeCharacterCP437(0));
        }

        @Test
        @DisplayName("should return CP437 code 177 at intensity 1")
        void testShadeCP437Intensity1() {
            assertEquals(177, theme.getShadeCharacterCP437(1));
        }

        @Test
        @DisplayName("should return CP437 code 178 at intensity 2")
        void testShadeCP437Intensity2() {
            assertEquals(178, theme.getShadeCharacterCP437(2));
        }

        @Test
        @DisplayName("should return CP437 code 219 at intensity 3")
        void testShadeCP437Intensity3() {
            assertEquals(219, theme.getShadeCharacterCP437(3));
        }

        @Test
        @DisplayName("should return CP437 code 176 for default intensity")
        void testShadeCP437IntensityDefault() {
            assertEquals(176, theme.getShadeCharacterCP437(-1));
        }
    }

    @Nested
    @DisplayName("Borland-specific colors")
    class BorlandSpecificColorTests {

        private final Borland3DTheme theme = new Borland3DTheme();

        @Test
        @DisplayName("should return inactive color")
        void testInactiveColor() {
            ColorPair color = theme.getInactiveColor();
            assertNotNull(color);
        }

        @Test
        @DisplayName("should return menu color")
        void testMenuColor() {
            ColorPair color = theme.getMenuColor();
            assertNotNull(color);
        }

        @Test
        @DisplayName("should return status color")
        void testStatusColor() {
            ColorPair color = theme.getStatusColor();
            assertNotNull(color);
        }

        @Test
        @DisplayName("should return active title color")
        void testActiveTitleColor() {
            ColorPair color = theme.getActiveTitleColor();
            assertNotNull(color);
        }
    }

    @Nested
    @DisplayName("Configuration methods")
    class ConfigTests {

        @Test
        @DisplayName("should return bold intensity state")
        void testUseBoldIntensity() {
            Borland3DTheme theme = new Borland3DTheme();
            assertTrue(theme.useBoldIntensity());
        }

        @Test
        @DisplayName("should return border style")
        void testGetBorderStyle() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals(Borland3DTheme.BorderStyle.SINGLE_LINE, theme.getBorderStyle());
        }

        @Test
        @DisplayName("should return A_BOLD attribute when bold enabled")
        void testGetAttributesBold() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals(NcursesBridge.A_BOLD, theme.getAttributes(true));
        }

        @Test
        @DisplayName("should return A_NORMAL when useBold is false")
        void testGetAttributesNotBold() {
            Borland3DTheme theme = new Borland3DTheme();
            assertEquals(NcursesBridge.A_NORMAL, theme.getAttributes(false));
        }

        @Test
        @DisplayName("should return A_NORMAL when boldIntensity is disabled")
        void testGetAttributesBoldDisabledTheme() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.SINGLE_LINE, true, false, false);
            assertEquals(NcursesBridge.A_NORMAL, theme.getAttributes(true));
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("should return formatted string")
        void testToString() {
            Borland3DTheme theme = new Borland3DTheme();
            String str = theme.toString();
            assertTrue(str.contains("Borland3DTheme"));
            assertTrue(str.contains("SINGLE_LINE"));
            assertTrue(str.contains("2x1"));
            assertTrue(str.contains("gradient=false"));
            assertTrue(str.contains("bold=true"));
        }

        @Test
        @DisplayName("should reflect custom config in toString")
        void testToStringCustom() {
            Borland3DTheme theme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.ASCII, false, true, false);
            String str = theme.toString();
            assertTrue(str.contains("ASCII"));
            assertTrue(str.contains("gradient=true"));
            assertTrue(str.contains("bold=false"));
        }
    }
}

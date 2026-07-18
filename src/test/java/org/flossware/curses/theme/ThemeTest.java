package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Theme Tests")
class ThemeTest {

    @Test
    @DisplayName("DefaultTheme should have correct name and colors")
    void testDefaultTheme() {
        Theme theme = new DefaultTheme();

        assertEquals("Default", theme.getName());
        assertEquals(Color.WHITE, theme.getBackground().foreground());
        assertEquals(Color.BLACK, theme.getBackground().background());
        assertEquals("+-+|+-+|", theme.getBorderChars());
    }

    @Test
    @DisplayName("DarkTheme should have correct name and colors")
    void testDarkTheme() {
        Theme theme = new DarkTheme();

        assertEquals("Dark", theme.getName());
        assertEquals(Color.CYAN, theme.getBackground().foreground());
        assertEquals(Color.BLACK, theme.getBackground().background());
        assertTrue(theme.getBorderChars().contains("─"));  // Unicode box chars
    }

    @Test
    @DisplayName("LightTheme should have correct name and colors")
    void testLightTheme() {
        Theme theme = new LightTheme();

        assertEquals("Light", theme.getName());
        assertEquals(Color.BLACK, theme.getBackground().foreground());
        assertEquals(Color.WHITE, theme.getBackground().background());
        assertTrue(theme.getBorderChars().contains("═"));  // Double-line Unicode
    }

    @Test
    @DisplayName("all themes should have all required color pairs")
    void testThemeCompleteness() {
        Theme[] themes = {new DefaultTheme(), new DarkTheme(), new LightTheme()};

        for (Theme theme : themes) {
            assertNotNull(theme.getBackground(), theme.getName() + " missing background");
            assertNotNull(theme.getButton(), theme.getName() + " missing button");
            assertNotNull(theme.getButtonFocused(), theme.getName() + " missing button focused");
            assertNotNull(theme.getTextInput(), theme.getName() + " missing text input");
            assertNotNull(theme.getBorder(), theme.getName() + " missing border");
            assertNotNull(theme.getSelection(), theme.getName() + " missing selection");
            assertNotNull(theme.getDisabled(), theme.getName() + " missing disabled");
            assertNotNull(theme.getBorderChars(), theme.getName() + " missing border chars");
            assertEquals(8, theme.getBorderChars().length(), theme.getName() + " border chars should be 8 characters");
        }
    }

    @Test
    @DisplayName("ThemeManager should default to DefaultTheme")
    void testThemeManagerDefault() {
        ThemeManager manager = ThemeManager.getInstance();

        assertEquals("Default", manager.getCurrentTheme().getName());
    }

    @Test
    @DisplayName("ThemeManager should be a singleton")
    void testThemeManagerSingleton() {
        ThemeManager manager1 = ThemeManager.getInstance();
        ThemeManager manager2 = ThemeManager.getInstance();

        assertSame(manager1, manager2);
    }

    @Test
    @DisplayName("ThemeManager should switch themes")
    void testThemeManagerSwitching() {
        ThemeManager manager = ThemeManager.getInstance();

        manager.useDarkTheme();
        assertEquals("Dark", manager.getCurrentTheme().getName());

        manager.useLightTheme();
        assertEquals("Light", manager.getCurrentTheme().getName());

        manager.useDefaultTheme();
        assertEquals("Default", manager.getCurrentTheme().getName());
    }

    @Test
    @DisplayName("ThemeManager should allow custom themes")
    void testThemeManagerCustomTheme() {
        ThemeManager manager = ThemeManager.getInstance();
        Theme customTheme = new DefaultTheme() {
            @Override
            public String getName() {
                return "Custom";
            }
        };

        manager.setTheme(customTheme);

        assertEquals("Custom", manager.getCurrentTheme().getName());

        // Reset to default
        manager.useDefaultTheme();
    }

    @Test
    @DisplayName("ThemeManager should ignore null theme")
    void testThemeManagerNullTheme() {
        ThemeManager manager = ThemeManager.getInstance();
        Theme originalTheme = manager.getCurrentTheme();

        manager.setTheme(null);

        assertSame(originalTheme, manager.getCurrentTheme());
    }
}

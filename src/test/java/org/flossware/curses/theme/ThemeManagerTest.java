package org.flossware.curses.theme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ThemeManager singleton and all convenience theme-switching methods.
 */
@DisplayName("ThemeManager Tests")
class ThemeManagerTest {

    @BeforeEach
    void setUp() {
        // Reset to default theme before each test
        ThemeManager.getInstance().useDefaultTheme();
    }

    @AfterEach
    void tearDown() {
        // Restore default theme
        ThemeManager.getInstance().useDefaultTheme();
    }

    @Test
    @DisplayName("should return singleton instance")
    void testSingleton() {
        ThemeManager instance1 = ThemeManager.getInstance();
        ThemeManager instance2 = ThemeManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("should have DefaultTheme by default")
    void testDefaultTheme() {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();
        assertNotNull(theme);
        assertEquals("Default", theme.getName());
    }

    @Test
    @DisplayName("should set custom theme")
    void testSetTheme() {
        Theme dark = new DarkTheme();
        ThemeManager.getInstance().setTheme(dark);
        assertSame(dark, ThemeManager.getInstance().getCurrentTheme());
    }

    @Test
    @DisplayName("should ignore null theme")
    void testSetNullTheme() {
        Theme original = ThemeManager.getInstance().getCurrentTheme();
        ThemeManager.getInstance().setTheme(null);
        assertSame(original, ThemeManager.getInstance().getCurrentTheme());
    }

    @Test
    @DisplayName("should switch to Dark theme")
    void testUseDarkTheme() {
        ThemeManager.getInstance().useDarkTheme();
        assertEquals("Dark", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to Light theme")
    void testUseLightTheme() {
        ThemeManager.getInstance().useLightTheme();
        assertEquals("Light", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to Borland theme")
    void testUseBorlandTheme() {
        ThemeManager.getInstance().useBorlandTheme();
        assertEquals("Borland", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to Borland 3D theme")
    void testUseBorland3DTheme() {
        ThemeManager.getInstance().useBorland3DTheme();
        assertEquals("Borland 3D", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to Modern theme")
    void testUseModernTheme() {
        ThemeManager.getInstance().useModernTheme();
        assertEquals("Modern", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to TI-99/4A theme")
    void testUseTI994ATheme() {
        ThemeManager.getInstance().useTI994ATheme();
        assertEquals("TI-99/4A", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to TRS-80 theme")
    void testUseTRS80Theme() {
        ThemeManager.getInstance().useTRS80Theme();
        assertEquals("TRS-80", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to DOS theme")
    void testUseDOSTheme() {
        ThemeManager.getInstance().useDOSTheme();
        assertEquals("DOS", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to dBASE III theme")
    void testUseDBase3Theme() {
        ThemeManager.getInstance().useDBase3Theme();
        assertEquals("dBASE III", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to dBASE IV theme")
    void testUseDBase4Theme() {
        ThemeManager.getInstance().useDBase4Theme();
        assertEquals("dBASE IV", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to dBASE IV 3D theme via static method")
    void testUseDBase4_3DTheme() {
        ThemeManager.useDBase4_3DTheme();
        assertEquals("dBASE IV 3D", ThemeManager.getInstance().getCurrentTheme().getName());
    }

    @Test
    @DisplayName("should switch to Default theme")
    void testUseDefaultTheme() {
        ThemeManager.getInstance().useDarkTheme();
        ThemeManager.getInstance().useDefaultTheme();
        assertEquals("Default", ThemeManager.getInstance().getCurrentTheme().getName());
    }
}

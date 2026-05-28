package org.flossware.curses.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for KeyBindings class.
 */
@DisplayName("KeyBindings Tests")
class KeyBindingsTest {

    private KeyBindings bindings;

    @BeforeEach
    void setUp() {
        bindings = new KeyBindings();
    }

    @Test
    @DisplayName("Constructor should set default bindings")
    void testConstructorSetsDefaults() {
        assertNotNull(bindings);
        assertEquals(9, bindings.getKey(KeyBindings.NEXT), "NEXT should be TAB (9)");
        assertEquals(353, bindings.getKey(KeyBindings.PREV), "PREV should be Shift-TAB (353)");
        assertEquals(32, bindings.getKey(KeyBindings.ACTIVATE), "ACTIVATE should be Space (32)");
        assertEquals(10, bindings.getKey(KeyBindings.ACTIVATE_ALT), "ACTIVATE_ALT should be Enter (10)");
        assertEquals(113, bindings.getKey(KeyBindings.QUIT), "QUIT should be 'q' (113)");
        assertEquals(27, bindings.getKey(KeyBindings.QUIT_ALT), "QUIT_ALT should be Esc (27)");
        assertEquals(259, bindings.getKey(KeyBindings.UP), "UP should be KEY_UP (259)");
        assertEquals(258, bindings.getKey(KeyBindings.DOWN), "DOWN should be KEY_DOWN (258)");
        assertEquals(260, bindings.getKey(KeyBindings.LEFT), "LEFT should be KEY_LEFT (260)");
        assertEquals(261, bindings.getKey(KeyBindings.RIGHT), "RIGHT should be KEY_RIGHT (261)");
    }

    @Test
    @DisplayName("setDefaults should reset all bindings")
    void testSetDefaultsResetBindings() {
        // Modify some bindings
        bindings.setKey(KeyBindings.QUIT, 999);
        bindings.setKey("CUSTOM", 123);

        // Reset to defaults
        bindings.setDefaults();

        // Verify defaults are restored
        assertEquals(113, bindings.getKey(KeyBindings.QUIT));
        assertEquals(-1, bindings.getKey("CUSTOM"), "Custom bindings should be cleared");
    }

    @Test
    @DisplayName("setKey should set custom binding")
    void testSetKeyCustomBinding() {
        bindings.setKey("CUSTOM_ACTION", 42);
        assertEquals(42, bindings.getKey("CUSTOM_ACTION"));
    }

    @Test
    @DisplayName("setKey should override existing binding")
    void testSetKeyOverrideExisting() {
        assertEquals(113, bindings.getKey(KeyBindings.QUIT));
        bindings.setKey(KeyBindings.QUIT, 999);
        assertEquals(999, bindings.getKey(KeyBindings.QUIT));
    }

    @Test
    @DisplayName("getKey should return -1 for unknown action")
    void testGetKeyUnknownAction() {
        assertEquals(-1, bindings.getKey("UNKNOWN_ACTION"));
    }

    @Test
    @DisplayName("matches should return true when key matches action")
    void testMatchesTrue() {
        assertTrue(bindings.matches(KeyBindings.QUIT, 113));
        assertTrue(bindings.matches(KeyBindings.NEXT, 9));
    }

    @Test
    @DisplayName("matches should return false when key does not match action")
    void testMatchesFalse() {
        assertFalse(bindings.matches(KeyBindings.QUIT, 999));
        assertFalse(bindings.matches(KeyBindings.NEXT, 999));
    }

    @Test
    @DisplayName("matches should return false for unknown action")
    void testMatchesUnknownAction() {
        assertFalse(bindings.matches("UNKNOWN", 113));
    }

    @Test
    @DisplayName("load should load numeric key codes from properties file")
    void testLoadNumericKeyCodes(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("test.properties");
        Files.writeString(configFile, """
            QUIT=999
            NEXT=42
            ACTIVATE=100
            """);

        bindings.load(configFile);

        assertEquals(999, bindings.getKey(KeyBindings.QUIT));
        assertEquals(42, bindings.getKey(KeyBindings.NEXT));
        assertEquals(100, bindings.getKey(KeyBindings.ACTIVATE));
    }

    @Test
    @DisplayName("load should load single character key codes from properties file")
    void testLoadCharacterKeyCodes(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("test.properties");
        Files.writeString(configFile, """
            QUIT=x
            NEXT=n
            ACTIVATE=a
            """);

        bindings.load(configFile);

        assertEquals('x', bindings.getKey(KeyBindings.QUIT));
        assertEquals('n', bindings.getKey(KeyBindings.NEXT));
        assertEquals('a', bindings.getKey(KeyBindings.ACTIVATE));
    }

    @Test
    @DisplayName("load should ignore invalid key codes")
    void testLoadIgnoreInvalidKeyCodes(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("test.properties");
        Files.writeString(configFile, """
            QUIT=999
            INVALID=
            MULTI=abc
            NEXT=42
            """);

        bindings.load(configFile);

        assertEquals(999, bindings.getKey(KeyBindings.QUIT));
        assertEquals(42, bindings.getKey(KeyBindings.NEXT));
        assertEquals(-1, bindings.getKey("INVALID"), "Empty value should be ignored");
        assertEquals(-1, bindings.getKey("MULTI"), "Multi-char value should be ignored");
    }

    @Test
    @DisplayName("load should handle mixed numeric and character codes")
    void testLoadMixedKeyCodes(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("test.properties");
        Files.writeString(configFile, """
            QUIT=q
            NEXT=9
            ACTIVATE=32
            UP=259
            """);

        bindings.load(configFile);

        assertEquals('q', bindings.getKey(KeyBindings.QUIT));
        assertEquals(9, bindings.getKey(KeyBindings.NEXT));
        assertEquals(32, bindings.getKey(KeyBindings.ACTIVATE));
        assertEquals(259, bindings.getKey(KeyBindings.UP));
    }

    @Test
    @DisplayName("load should throw IOException for non-existent file")
    void testLoadNonExistentFile(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("does-not-exist.properties");
        assertThrows(IOException.class, () -> bindings.load(nonExistent));
    }

    @Test
    @DisplayName("load should preserve defaults for keys not in file")
    void testLoadPreservesDefaults(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("test.properties");
        Files.writeString(configFile, """
            QUIT=999
            """);

        bindings.load(configFile);

        assertEquals(999, bindings.getKey(KeyBindings.QUIT), "QUIT should be overridden");
        assertEquals(9, bindings.getKey(KeyBindings.NEXT), "NEXT should keep default");
        assertEquals(32, bindings.getKey(KeyBindings.ACTIVATE), "ACTIVATE should keep default");
    }

    @Test
    @DisplayName("getAll should return all bindings")
    void testGetAllReturnsAllBindings() {
        Map<String, Integer> all = bindings.getAll();

        assertNotNull(all);
        assertEquals(10, all.size(), "Should have 10 default bindings");
        assertTrue(all.containsKey(KeyBindings.NEXT));
        assertTrue(all.containsKey(KeyBindings.PREV));
        assertTrue(all.containsKey(KeyBindings.QUIT));
        assertTrue(all.containsKey(KeyBindings.ACTIVATE));
    }

    @Test
    @DisplayName("getAll should return immutable map")
    void testGetAllReturnsImmutableMap() {
        Map<String, Integer> all = bindings.getAll();

        assertThrows(UnsupportedOperationException.class, () -> {
            all.put("TEST", 999);
        });
    }

    @Test
    @DisplayName("getAll should reflect custom bindings")
    void testGetAllReflectsCustomBindings() {
        bindings.setKey("CUSTOM", 123);

        Map<String, Integer> all = bindings.getAll();

        assertTrue(all.containsKey("CUSTOM"));
        assertEquals(123, all.get("CUSTOM"));
    }

    @Test
    @DisplayName("load should handle empty properties file")
    void testLoadEmptyFile(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("empty.properties");
        Files.writeString(configFile, "");

        bindings.load(configFile);

        // Defaults should still be present
        assertEquals(113, bindings.getKey(KeyBindings.QUIT));
        assertEquals(9, bindings.getKey(KeyBindings.NEXT));
    }

    @Test
    @DisplayName("load should handle whitespace in property values")
    void testLoadWhitespaceHandling(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("test.properties");
        Files.writeString(configFile, """
            QUIT = 999
            NEXT=  42
            """);

        bindings.load(configFile);

        // Properties automatically trims whitespace
        assertEquals(999, bindings.getKey(KeyBindings.QUIT));
        assertEquals(42, bindings.getKey(KeyBindings.NEXT));
    }

    @Test
    @DisplayName("Constants should have expected values")
    void testConstantValues() {
        assertEquals("NEXT", KeyBindings.NEXT);
        assertEquals("PREV", KeyBindings.PREV);
        assertEquals("ACTIVATE", KeyBindings.ACTIVATE);
        assertEquals("ACTIVATE_ALT", KeyBindings.ACTIVATE_ALT);
        assertEquals("QUIT", KeyBindings.QUIT);
        assertEquals("QUIT_ALT", KeyBindings.QUIT_ALT);
        assertEquals("UP", KeyBindings.UP);
        assertEquals("DOWN", KeyBindings.DOWN);
        assertEquals("LEFT", KeyBindings.LEFT);
        assertEquals("RIGHT", KeyBindings.RIGHT);
    }
}

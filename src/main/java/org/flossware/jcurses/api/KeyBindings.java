package org.flossware.jcurses.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Manages customizable key bindings for the application.
 *
 * <p>KeyBindings allows users to configure keyboard shortcuts for common
 * actions. Default bindings are provided but can be overridden by loading
 * a properties file.
 *
 * <h2>Default Bindings</h2>
 * <ul>
 *   <li>NEXT: Tab (9) - Navigate to next component</li>
 *   <li>PREV: Shift-Tab (353) - Navigate to previous component</li>
 *   <li>ACTIVATE: Space (32) - Activate focused component</li>
 *   <li>ACTIVATE_ALT: Enter (10) - Alternative activation key</li>
 *   <li>QUIT: q (113) - Quit application</li>
 *   <li>QUIT_ALT: Esc (27) - Alternative quit key</li>
 * </ul>
 *
 * <h2>Configuration File Format</h2>
 * <p>Key bindings can be loaded from a properties file:
 * <pre>
 * NEXT=9
 * PREV=353
 * ACTIVATE=32
 * QUIT=q
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * KeyBindings bindings = new KeyBindings();
 * // Load custom bindings
 * bindings.load(Path.of("keybindings.properties"));
 *
 * // Check if a key matches an action
 * if (keyCode == bindings.getKey("QUIT")) {
 *     System.exit(0);
 * }
 * }</pre>
 *
 * @author FlossWare
 * @since 1.26
 */
public class KeyBindings {
    private final Map<String, Integer> bindings = new HashMap<>();

    /**
     * Standard key action names.
     */
    public static final String NEXT = "NEXT";
    public static final String PREV = "PREV";
    public static final String ACTIVATE = "ACTIVATE";
    public static final String ACTIVATE_ALT = "ACTIVATE_ALT";
    public static final String QUIT = "QUIT";
    public static final String QUIT_ALT = "QUIT_ALT";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";

    /**
     * Creates a KeyBindings instance with default bindings.
     */
    public KeyBindings() {
        setDefaults();
    }

    /**
     * Resets all bindings to their default values.
     */
    public void setDefaults() {
        bindings.clear();
        bindings.put(NEXT, 9);          // TAB
        bindings.put(PREV, 353);        // Shift-TAB (ncurses code)
        bindings.put(ACTIVATE, 32);     // Space
        bindings.put(ACTIVATE_ALT, 10); // Enter
        bindings.put(QUIT, 113);        // 'q'
        bindings.put(QUIT_ALT, 27);     // Esc
        bindings.put(UP, 259);          // Up arrow (ncurses KEY_UP)
        bindings.put(DOWN, 258);        // Down arrow (ncurses KEY_DOWN)
        bindings.put(LEFT, 260);        // Left arrow (ncurses KEY_LEFT)
        bindings.put(RIGHT, 261);       // Right arrow (ncurses KEY_RIGHT)
    }

    /**
     * Sets a custom key binding for an action.
     *
     * @param action the action name (e.g., "QUIT", "NEXT")
     * @param keyCode the ncurses key code or character code
     */
    public void setKey(String action, int keyCode) {
        bindings.put(action, keyCode);
    }

    /**
     * Gets the key code bound to an action.
     *
     * @param action the action name
     * @return the key code, or -1 if not bound
     */
    public int getKey(String action) {
        return bindings.getOrDefault(action, -1);
    }

    /**
     * Checks if a key code matches a specific action.
     *
     * @param action the action name to check
     * @param keyCode the key code to test
     * @return true if the key is bound to the action
     */
    public boolean matches(String action, int keyCode) {
        return getKey(action) == keyCode;
    }

    /**
     * Loads key bindings from a properties file.
     *
     * <p>The properties file should contain action names as keys and
     * either numeric key codes or single characters as values.
     *
     * <p>Example:
     * <pre>
     * NEXT=9
     * QUIT=q
     * ACTIVATE=32
     * </pre>
     *
     * @param configFile path to the properties file
     * @throws IOException if the file cannot be read
     */
    public void load(Path configFile) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(configFile)) {
            props.load(in);
        }

        for (String action : props.stringPropertyNames()) {
            String value = props.getProperty(action);
            int keyCode = parseKeyCode(value);
            if (keyCode != -1) {
                bindings.put(action, keyCode);
            }
        }
    }

    /**
     * Parses a key code from a string value.
     *
     * <p>Supports:
     * <ul>
     *   <li>Numeric codes: "32", "113"</li>
     *   <li>Single characters: "q", " "</li>
     * </ul>
     *
     * @param value the string to parse
     * @return the key code, or -1 if invalid
     */
    private int parseKeyCode(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        // Try parsing as numeric code
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // If not numeric, use first character's code
            if (value.length() == 1) {
                return value.charAt(0);
            }
            return -1;
        }
    }

    /**
     * Returns all configured bindings as an immutable map.
     *
     * @return map of action names to key codes
     */
    public Map<String, Integer> getAll() {
        return Map.copyOf(bindings);
    }
}

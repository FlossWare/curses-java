package org.flossware.curses.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;
import org.flossware.curses.api.RenderingStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads and serializes themes from/to JSON format.
 *
 * <p>ThemeLoader supports both flat {@link Theme} and 3D {@link Theme3D} themes.
 * The presence of a {@code "3d"} key in the JSON determines whether a flat or 3D
 * theme is created.</p>
 *
 * <h2>JSON Format</h2>
 * <p>Flat theme:</p>
 * <pre>{@code
 * {
 *   "name": "My Theme",
 *   "version": "1.0",
 *   "colors": {
 *     "background": { "fg": "CYAN", "bg": "BLACK" },
 *     "button": { "fg": "BLUE", "bg": "BLACK" },
 *     ...
 *   },
 *   "borders": { "single": "+-+|+-+|" }
 * }
 * }</pre>
 *
 * <p>3D theme (same as flat, plus a {@code "3d"} section):</p>
 * <pre>{@code
 * {
 *   ...
 *   "3d": {
 *     "shadow_color": { "fg": "BLACK", "bg": "BLACK" },
 *     "highlight_color": { "fg": "WHITE", "bg": "CYAN" },
 *     "lowlight_color": { "fg": "BLACK", "bg": "CYAN" },
 *     "shadow_offset": { "x": 2, "y": 1 },
 *     "rendering_style": "RAISED"
 *   }
 * }
 * }</pre>
 *
 * @see Theme
 * @see Theme3D
 * @see ThemeManager
 */
public final class ThemeLoader {

    private ThemeLoader() {
        // Utility class
    }

    /**
     * Loads a theme from a JSON file.
     *
     * @param path the path to the JSON file
     * @return a Theme or Theme3D depending on the JSON content
     * @throws ThemeLoadException if the file cannot be read or the JSON is invalid
     */
    public static Theme fromJson(Path path) {
        try {
            String json = Files.readString(path);
            return fromJson(json);
        } catch (IOException e) {
            throw new ThemeLoadException("Failed to read theme file: " + path, e);
        }
    }

    /**
     * Parses a theme from a JSON string.
     *
     * @param json the JSON string
     * @return a Theme or Theme3D depending on the JSON content
     * @throws ThemeLoadException if the JSON is invalid or missing required fields
     */
    public static Theme fromJson(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            String name = requireString(root, "name");
            JsonObject colors = requireObject(root, "colors");
            JsonObject borders = requireObject(root, "borders");

            ColorPair background = parseColorPair(colors, "background");
            ColorPair button = parseColorPair(colors, "button");
            ColorPair buttonFocused = parseColorPair(colors, "button_focused");
            ColorPair textInput = parseColorPair(colors, "text_input");
            ColorPair border = parseColorPair(colors, "border");
            ColorPair selection = parseColorPair(colors, "selection");
            ColorPair disabled = parseColorPair(colors, "disabled");

            String borderChars = requireString(borders, "single");
            if (borderChars.length() != 8) {
                throw new ThemeLoadException(
                        "Border chars must be exactly 8 characters, got " + borderChars.length());
            }

            if (root.has("3d")) {
                JsonObject td = root.getAsJsonObject("3d");
                ColorPair shadowColor = parseColorPair(td, "shadow_color");
                ColorPair highlightColor = parseColorPair(td, "highlight_color");
                ColorPair lowlightColor = parseColorPair(td, "lowlight_color");

                JsonObject offset = requireObject(td, "shadow_offset");
                int offsetX = offset.get("x").getAsInt();
                int offsetY = offset.get("y").getAsInt();

                RenderingStyle renderingStyle = RenderingStyle.RAISED;
                if (td.has("rendering_style")) {
                    renderingStyle = RenderingStyle.valueOf(td.get("rendering_style").getAsString());
                }

                String doubleBorder = null;
                if (borders.has("double")) {
                    doubleBorder = borders.get("double").getAsString();
                }

                return new Json3DTheme(name, background, button, buttonFocused,
                        textInput, border, selection, disabled, borderChars,
                        shadowColor, highlightColor, lowlightColor,
                        offsetX, offsetY, renderingStyle, doubleBorder);
            }

            return new JsonTheme(name, background, button, buttonFocused,
                    textInput, border, selection, disabled, borderChars);

        } catch (JsonParseException e) {
            throw new ThemeLoadException("Invalid JSON: " + e.getMessage(), e);
        } catch (ThemeLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new ThemeLoadException("Failed to parse theme JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a theme to JSON format.
     *
     * @param theme the theme to serialize
     * @return a JSON string representation
     */
    public static String toJson(Theme theme) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject root = new JsonObject();
        root.addProperty("name", theme.getName());
        root.addProperty("version", "1.0");

        JsonObject colors = new JsonObject();
        colors.add("background", colorPairToJson(theme.getBackground()));
        colors.add("button", colorPairToJson(theme.getButton()));
        colors.add("button_focused", colorPairToJson(theme.getButtonFocused()));
        colors.add("text_input", colorPairToJson(theme.getTextInput()));
        colors.add("border", colorPairToJson(theme.getBorder()));
        colors.add("selection", colorPairToJson(theme.getSelection()));
        colors.add("disabled", colorPairToJson(theme.getDisabled()));
        root.add("colors", colors);

        JsonObject borders = new JsonObject();
        borders.addProperty("single", theme.getBorderChars());
        if (theme instanceof Theme3D theme3d) {
            String doubleBorderChars = theme3d.getDoubleBorderChars();
            if (doubleBorderChars != null) {
                borders.addProperty("double", doubleBorderChars);
            }
        }
        root.add("borders", borders);

        if (theme instanceof Theme3D theme3d) {
            JsonObject td = new JsonObject();
            td.add("shadow_color", colorPairToJson(theme3d.getShadowColor()));
            td.add("highlight_color", colorPairToJson(theme3d.getHighlightColor()));
            td.add("lowlight_color", colorPairToJson(theme3d.getLowlightColor()));

            JsonObject offset = new JsonObject();
            offset.addProperty("x", theme3d.getShadowOffsetX());
            offset.addProperty("y", theme3d.getShadowOffsetY());
            td.add("shadow_offset", offset);

            td.addProperty("rendering_style", theme3d.getDefaultRenderingStyle().name());

            root.add("3d", td);
        }

        return gson.toJson(root);
    }

    // ========================================================================
    // Private helpers
    // ========================================================================

    private static String requireString(JsonObject obj, String key) {
        JsonElement element = obj.get(key);
        if (element == null || !element.isJsonPrimitive()) {
            throw new ThemeLoadException("Missing required field: " + key);
        }
        return element.getAsString();
    }

    private static JsonObject requireObject(JsonObject obj, String key) {
        JsonElement element = obj.get(key);
        if (element == null || !element.isJsonObject()) {
            throw new ThemeLoadException("Missing required object: " + key);
        }
        return element.getAsJsonObject();
    }

    private static ColorPair parseColorPair(JsonObject parent, String key) {
        JsonObject obj = requireObject(parent, key);
        String fgStr = requireString(obj, "fg");
        String bgStr = requireString(obj, "bg");

        Color fg = parseColor(fgStr);
        Color bg = parseColor(bgStr);

        return new ColorPair(fg, bg);
    }

    private static Color parseColor(String name) {
        try {
            return Color.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new ThemeLoadException("Invalid color name: '" + name
                    + "'. Valid values: BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE");
        }
    }

    private static JsonObject colorPairToJson(ColorPair pair) {
        JsonObject obj = new JsonObject();
        obj.addProperty("fg", pair.foreground().name());
        obj.addProperty("bg", pair.background().name());
        return obj;
    }

    // ========================================================================
    // Inner theme implementations
    // ========================================================================

    /**
     * A Theme implementation backed by parsed JSON values.
     */
    static final class JsonTheme implements Theme {
        private final String name;
        private final ColorPair background;
        private final ColorPair button;
        private final ColorPair buttonFocused;
        private final ColorPair textInput;
        private final ColorPair border;
        private final ColorPair selection;
        private final ColorPair disabled;
        private final String borderChars;

        JsonTheme(String name, ColorPair background, ColorPair button, ColorPair buttonFocused,
                  ColorPair textInput, ColorPair border, ColorPair selection,
                  ColorPair disabled, String borderChars) {
            this.name = name;
            this.background = background;
            this.button = button;
            this.buttonFocused = buttonFocused;
            this.textInput = textInput;
            this.border = border;
            this.selection = selection;
            this.disabled = disabled;
            this.borderChars = borderChars;
        }

        @Override public String getName() { return name; }
        @Override public ColorPair getBackground() { return background; }
        @Override public ColorPair getButton() { return button; }
        @Override public ColorPair getButtonFocused() { return buttonFocused; }
        @Override public ColorPair getTextInput() { return textInput; }
        @Override public ColorPair getBorder() { return border; }
        @Override public ColorPair getSelection() { return selection; }
        @Override public ColorPair getDisabled() { return disabled; }
        @Override public String getBorderChars() { return borderChars; }
    }

    /**
     * A Theme3D implementation backed by parsed JSON values.
     */
    static final class Json3DTheme implements Theme3D {
        private final String name;
        private final ColorPair background;
        private final ColorPair button;
        private final ColorPair buttonFocused;
        private final ColorPair textInput;
        private final ColorPair border;
        private final ColorPair selection;
        private final ColorPair disabled;
        private final String borderChars;
        private final ColorPair shadowColor;
        private final ColorPair highlightColor;
        private final ColorPair lowlightColor;
        private final int shadowOffsetX;
        private final int shadowOffsetY;
        private final RenderingStyle renderingStyle;
        private final String doubleBorderChars;

        Json3DTheme(String name, ColorPair background, ColorPair button,
                    ColorPair buttonFocused, ColorPair textInput, ColorPair border,
                    ColorPair selection, ColorPair disabled, String borderChars,
                    ColorPair shadowColor, ColorPair highlightColor,
                    ColorPair lowlightColor, int shadowOffsetX, int shadowOffsetY,
                    RenderingStyle renderingStyle, String doubleBorderChars) {
            this.name = name;
            this.background = background;
            this.button = button;
            this.buttonFocused = buttonFocused;
            this.textInput = textInput;
            this.border = border;
            this.selection = selection;
            this.disabled = disabled;
            this.borderChars = borderChars;
            this.shadowColor = shadowColor;
            this.highlightColor = highlightColor;
            this.lowlightColor = lowlightColor;
            this.shadowOffsetX = shadowOffsetX;
            this.shadowOffsetY = shadowOffsetY;
            this.renderingStyle = renderingStyle;
            this.doubleBorderChars = doubleBorderChars;
        }

        @Override public String getName() { return name; }
        @Override public ColorPair getBackground() { return background; }
        @Override public ColorPair getButton() { return button; }
        @Override public ColorPair getButtonFocused() { return buttonFocused; }
        @Override public ColorPair getTextInput() { return textInput; }
        @Override public ColorPair getBorder() { return border; }
        @Override public ColorPair getSelection() { return selection; }
        @Override public ColorPair getDisabled() { return disabled; }
        @Override public String getBorderChars() { return borderChars; }
        @Override public ColorPair getShadowColor() { return shadowColor; }
        @Override public ColorPair getHighlightColor() { return highlightColor; }
        @Override public ColorPair getLowlightColor() { return lowlightColor; }
        @Override public int getShadowOffsetX() { return shadowOffsetX; }
        @Override public int getShadowOffsetY() { return shadowOffsetY; }

        @Override
        public RenderingStyle getDefaultRenderingStyle() {
            return renderingStyle;
        }

        @Override
        public String getDoubleBorderChars() {
            return doubleBorderChars != null ? doubleBorderChars : Theme3D.super.getDoubleBorderChars();
        }
    }

    /**
     * Exception thrown when theme loading or parsing fails.
     */
    public static class ThemeLoadException extends RuntimeException {
        public ThemeLoadException(String message) {
            super(message);
        }

        public ThemeLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

package org.flossware.jcurses.theme;

import org.flossware.jcurses.api.Color;
import org.flossware.jcurses.api.ColorPair;

/**
 * Dark theme with muted colors and dark background.
 * Modern dark mode aesthetic.
 */
public class DarkTheme implements Theme {
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.BLUE, Color.BLACK);
    }

    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.BLUE);
    }

    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.BLUE, Color.BLACK);
    }

    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.BLUE, Color.BLACK);  // Muted blue
    }

    @Override
    public String getBorderChars() {
        return "┌─┐│└─┘│";  // Unicode box drawing
    }

    @Override
    public String getName() {
        return "Dark";
    }
}

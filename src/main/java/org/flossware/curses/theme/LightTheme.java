package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Light theme with dark text on light background.
 * High contrast light mode.
 */
public class LightTheme implements Theme {
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.BLUE, Color.WHITE);
    }

    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.BLACK, Color.CYAN);  // Light cyan background
    }

    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.CYAN, Color.WHITE);  // Light gray appearance
    }

    @Override
    public String getBorderChars() {
        return "╔═╗║╚═╝║";  // Double-line Unicode box
    }

    @Override
    public String getName() {
        return "Light";
    }
}

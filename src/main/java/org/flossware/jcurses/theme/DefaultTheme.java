package org.flossware.jcurses.theme;

import org.flossware.jcurses.api.Color;
import org.flossware.jcurses.api.ColorPair;

/**
 * Default theme with white text on black background.
 * Classic terminal appearance.
 */
public class DefaultTheme implements Theme {
    @Override
    public ColorPair getBackground() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    @Override
    public ColorPair getButton() {
        return new ColorPair(Color.CYAN, Color.BLACK);
    }

    @Override
    public ColorPair getButtonFocused() {
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    @Override
    public ColorPair getTextInput() {
        return new ColorPair(Color.GREEN, Color.BLACK);
    }

    @Override
    public ColorPair getBorder() {
        return new ColorPair(Color.WHITE, Color.BLACK);
    }

    @Override
    public ColorPair getSelection() {
        return new ColorPair(Color.BLACK, Color.WHITE);
    }

    @Override
    public ColorPair getDisabled() {
        return new ColorPair(Color.WHITE, Color.BLACK);  // Dimmed appearance
    }

    @Override
    public String getBorderChars() {
        return "+-+||+-+";  // ASCII box drawing
    }

    @Override
    public String getName() {
        return "Default";
    }
}

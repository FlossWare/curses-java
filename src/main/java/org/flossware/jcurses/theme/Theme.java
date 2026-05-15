package org.flossware.jcurses.theme;

import org.flossware.jcurses.api.ColorPair;

/**
 * Interface for defining visual themes for jcurses components.
 *
 * Themes control colors and rendering characters for all UI elements.
 */
public interface Theme {
    /**
     * Get the background color pair for normal components.
     */
    ColorPair getBackground();

    /**
     * Get the color pair for buttons in normal state.
     */
    ColorPair getButton();

    /**
     * Get the color pair for buttons when focused.
     */
    ColorPair getButtonFocused();

    /**
     * Get the color pair for text input fields.
     */
    ColorPair getTextInput();

    /**
     * Get the color pair for borders and frames.
     */
    ColorPair getBorder();

    /**
     * Get the color pair for selected/highlighted items.
     */
    ColorPair getSelection();

    /**
     * Get the color pair for disabled components.
     */
    ColorPair getDisabled();

    /**
     * Get the border characters used for drawing boxes.
     * Returns a string with 8 characters: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right.
     * Default: "+-+|+-+--" (ASCII)
     * Unicode box: "┌─┐│└─┘│"
     */
    String getBorderChars();

    /**
     * Get the name of this theme.
     */
    String getName();
}

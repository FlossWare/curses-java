package org.flossware.curses.theme;

import org.flossware.curses.api.ColorPair;

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
     * Default: "+-+||+-+" (ASCII)
     * Unicode box: "┌─┐│└─┘│"
     */
    String getBorderChars();

    /**
     * Get the name of this theme.
     */
    String getName();

    /**
     * Indicates whether this theme supports 3D rendering effects.
     *
     * <p>Themes that implement the {@link Theme3D} interface should override this
     * method to return {@code true}. This allows components to detect 3D capability
     * without requiring instanceof checks or type casting.</p>
     *
     * <p>Default implementation returns {@code false} to maintain backward compatibility
     * with existing theme implementations.</p>
     *
     * @return {@code true} if this theme supports 3D effects, {@code false} otherwise
     * @see Theme3D
     */
    default boolean supports3D() {
        return false;
    }
}

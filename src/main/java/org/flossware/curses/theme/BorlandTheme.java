package org.flossware.curses.theme;

import org.flossware.curses.api.Color;
import org.flossware.curses.api.ColorPair;

/**
 * Classic Borland IDE theme.
 *
 * Recreates the iconic blue background with yellow text and cyan highlights
 * that defined the Borland IDE experience (Turbo Pascal, Turbo C++, etc.)
 * of the late 1980s and early 1990s.
 *
 * Color scheme:
 * - Background: Blue
 * - Normal text: Yellow on Blue
 * - Borders/Frames: White on Blue
 * - Highlights/Selection: Black on Cyan (bright highlight)
 * - Focused buttons: Black on Cyan
 * - Text input: White on Blue
 * - Disabled: Black on Blue (dimmed)
 */
public class BorlandTheme implements Theme {
    @Override
    public ColorPair getBackground() {
        // Classic blue background with yellow text
        return new ColorPair(Color.YELLOW, Color.BLUE);
    }

    @Override
    public ColorPair getButton() {
        // Buttons with cyan text on blue background
        return new ColorPair(Color.CYAN, Color.BLUE);
    }

    @Override
    public ColorPair getButtonFocused() {
        // Focused buttons: inverted cyan (black on cyan)
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    @Override
    public ColorPair getTextInput() {
        // Text input fields: white on blue
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getBorder() {
        // Borders and frames: white on blue
        return new ColorPair(Color.WHITE, Color.BLUE);
    }

    @Override
    public ColorPair getSelection() {
        // Selected items: black on cyan (bright highlight)
        return new ColorPair(Color.BLACK, Color.CYAN);
    }

    @Override
    public ColorPair getDisabled() {
        // Disabled components: black on blue (dimmed appearance)
        return new ColorPair(Color.BLACK, Color.BLUE);
    }

    @Override
    public String getBorderChars() {
        // Unicode rounded box drawing characters for smooth, modern appearance
        // Format: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right
        // Characters used:
        //   ╭ (U+256D) - Box Drawings Light Arc Down and Right (top-left corner)
        //   ─ (U+2500) - Box Drawings Light Horizontal (top/bottom edge)
        //   ╮ (U+256E) - Box Drawings Light Arc Down and Left (top-right corner)
        //   │ (U+2502) - Box Drawings Light Vertical (left/right edge)
        //   ╰ (U+2570) - Box Drawings Light Arc Up and Right (bottom-left corner)
        //   ╯ (U+256F) - Box Drawings Light Arc Up and Left (bottom-right corner)
        return "╭─╮│╰─╯│";
    }

    @Override
    public String getName() {
        return "Borland";
    }
}

package org.flossware.jcurses.events;

/**
 * Represents a key press in the terminal.
 */
public record KeyEvent(int keyCode, boolean altPressed, boolean ctrlPressed) implements JcursesEvent {}

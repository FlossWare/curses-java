package org.flossware.curses.events;

/**
 * Represents a mouse action within the terminal grid.
 */
public record MouseEvent(int x, int y, int button) implements CursesEvent {}

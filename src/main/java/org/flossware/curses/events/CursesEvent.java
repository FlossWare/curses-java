package org.flossware.curses.events;

/**
 * Base interface for all library events.
 */
public sealed interface CursesEvent permits KeyEvent, MouseEvent, WindowEvent {}

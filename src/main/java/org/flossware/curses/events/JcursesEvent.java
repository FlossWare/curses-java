package org.flossware.curses.events;

/**
 * Base interface for all library events.
 */
public sealed interface JcursesEvent permits KeyEvent, MouseEvent, WindowEvent {}
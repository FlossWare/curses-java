package org.flossware.jcurses.events;

/**
 * Base interface for all library events.
 */
public sealed interface JcursesEvent permits KeyEvent, MouseEvent, WindowEvent {}
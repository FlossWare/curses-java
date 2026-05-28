package org.flossware.curses.events;

/**
 * Represents system-level changes like terminal resizing.
 */
public record WindowEvent(int width, int height) implements JcursesEvent {}

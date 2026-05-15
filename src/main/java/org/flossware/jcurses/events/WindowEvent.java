package org.flossware.jcurses.events;

/**
 * Represents system-level changes like terminal resizing.
 */
public record WindowEvent(int width, int height) implements JcursesEvent {}

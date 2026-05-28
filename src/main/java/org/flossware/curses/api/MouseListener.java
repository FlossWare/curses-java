package org.flossware.curses.api;

import org.flossware.curses.events.MouseEvent;

@FunctionalInterface
public interface MouseListener {
    void onMouseEvent(MouseEvent event);
}

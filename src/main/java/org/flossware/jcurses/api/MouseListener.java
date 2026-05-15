package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;

@FunctionalInterface
public interface MouseListener {
    void onMouseEvent(MouseEvent event);
}

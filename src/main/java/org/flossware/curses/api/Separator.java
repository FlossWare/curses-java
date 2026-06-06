package org.flossware.curses.api;

public class Separator extends Component {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;

    public Separator() {
        this(HORIZONTAL);
    }

    public Separator(int orientation) {
        this.orientation = orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < width; i++) {
                writeStringToBuffer(buffer, "-", getX() + i, getY());
            }
        } else {
            for (int i = 0; i < height; i++) {
                writeStringToBuffer(buffer, "|", getX(), getY() + i);
            }
        }
    }
}

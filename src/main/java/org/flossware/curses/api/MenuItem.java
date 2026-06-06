package org.flossware.curses.api;

public class MenuItem extends Component {
    private String label;
    private Runnable action;

    public MenuItem(String label) {
        this.label = label;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void activate() {
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        writeStringToBuffer(buffer, label, getX(), getY());
    }
}

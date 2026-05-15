package org.flossware.jcurses.api;

public class JMenuItem extends Component {
    private String label;
    private Runnable action;

    public JMenuItem(String label) {
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

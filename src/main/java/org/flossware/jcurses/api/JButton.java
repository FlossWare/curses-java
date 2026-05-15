package org.flossware.jcurses.api;

public class JButton extends Component {
    private String label;
    private Runnable action;
    private boolean enabled = true;

    public JButton(String label) {
        this.label = label;
    }

    public void setLabel(String label) {
        renderLock.lock();
        try {
            this.label = label;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public String getLabel() {
        return label;
    }

    public void addActionListener(Runnable action) {
        this.action = action;
    }

    public void setEnabled(boolean enabled) {
        renderLock.lock();
        try {
            this.enabled = enabled;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void doClick() {
        if (enabled && action != null) {
            action.run();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        String display = enabled ? "[ " + label + " ]" : "( " + label + " )";
        writeStringToBuffer(buffer, display, getX(), getY());
    }
}

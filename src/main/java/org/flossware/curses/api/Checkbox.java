package org.flossware.curses.api;

public class Checkbox extends Component {
    private boolean checked = false;
    private String label;
    private CheckboxGroup group;

    public Checkbox(String label) {
        this.label = label;
    }

    public void setChecked(boolean checked) {
        CheckboxGroup g;
        renderLock.lock();
        try {
            g = this.group;
            if (g == null) {
                this.checked = checked;
            }
        } finally {
            renderLock.unlock();
        }
        if (g != null) {
            g.setSelected(this);
        }
        repaint();
    }

    public boolean isChecked() {
        return checked;
    }

    void setGroup(CheckboxGroup group) {
        renderLock.lock();
        try {
            this.group = group;
        } finally {
            renderLock.unlock();
        }
    }

    void setStateInternal(boolean checked) {
        renderLock.lock();
        try {
            this.checked = checked;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        String display = (checked ? "[X] " : "[ ] ") + label;
        writeStringToBuffer(buffer, display, getX(), getY());
    }
}

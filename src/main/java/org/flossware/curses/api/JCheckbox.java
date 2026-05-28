package org.flossware.curses.api;

public class JCheckbox extends Component {
    private boolean checked = false;
    private String label;
    private JCheckboxGroup group;

    public JCheckbox(String label) {
        this.label = label;
    }

    public void setChecked(boolean checked) {
        renderLock.lock();
        try {
            if (group != null) {
                group.setSelected(this);
            } else {
                this.checked = checked;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public boolean isChecked() {
        return checked;
    }

    void setGroup(JCheckboxGroup group) {
        this.group = group;
    }

    void setStateInternal(boolean checked) {
        this.checked = checked;
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        String display = (checked ? "[X] " : "[ ] ") + label;
        writeStringToBuffer(buffer, display, getX(), getY());
    }
}

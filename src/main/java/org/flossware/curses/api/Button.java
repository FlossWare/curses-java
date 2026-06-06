package org.flossware.curses.api;

/**
 * A terminal UI button component that responds to keyboard and mouse events.
 *
 * <p>Button displays a clickable button with a text label. Users can activate
 * the button by pressing SPACE/ENTER when focused or by clicking with the mouse.
 * When activated, the button executes its registered action listener.
 *
 * <p>Usage example:
 * <pre>{@code
 * Button button = new Button("Click Me!");
 * button.setLocation(10, 5);
 * button.setSize(15, 3);
 * button.addActionListener(() -> System.out.println("Button clicked!"));
 * }</pre>
 *
 * @author FlossWare
 * @since 1.0
 */
public class Button extends Component {
    private String label;
    private Runnable action;
    private boolean enabled = true;

    /**
     * Creates a new button with the specified label.
     *
     * @param label the button text to display
     */
    public Button(String label) {
        this.label = label;
    }

    /**
     * Sets the button's display text.
     *
     * <p>This method is thread-safe and triggers a repaint.
     *
     * @param label the new button text
     */
    public void setLabel(String label) {
        renderLock.lock();
        try {
            this.label = label;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    /**
     * Returns the button's current label text.
     *
     * @return the button label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Registers an action listener to be executed when the button is activated.
     *
     * <p>The action runs when the user presses SPACE/ENTER while focused
     * or clicks the button with the mouse.
     *
     * @param action the action to execute, or null to remove the listener
     */
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

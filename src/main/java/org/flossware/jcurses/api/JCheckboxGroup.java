package org.flossware.jcurses.api;

import java.util.SequencedCollection;
import java.util.ArrayList;

/**
 * Manages a group of checkboxes where only one can be true.
 */
public class JCheckboxGroup {
    private final SequencedCollection<JCheckbox> group = new ArrayList<>();
    private JCheckbox currentSelected;

    public void add(JCheckbox checkbox) {
        group.addLast(checkbox); // [cite: 99, 150]
        checkbox.setGroup(this);
    }

    public void setSelected(JCheckbox selected) {
        for (JCheckbox cb : group) {
            cb.setStateInternal(cb == selected);
        }
        this.currentSelected = selected;
    }
}
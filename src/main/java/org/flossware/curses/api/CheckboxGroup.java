package org.flossware.curses.api;

import java.util.SequencedCollection;
import java.util.ArrayList;

/**
 * Manages a group of checkboxes where only one can be true.
 */
public class CheckboxGroup {
    private final SequencedCollection<Checkbox> group = new ArrayList<>();
    private Checkbox currentSelected;

    public void add(Checkbox checkbox) {
        group.addLast(checkbox); // [cite: 99, 150]
        checkbox.setGroup(this);
    }

    public void setSelected(Checkbox selected) {
        for (Checkbox cb : group) {
            cb.setStateInternal(cb == selected);
        }
        this.currentSelected = selected;
    }
}
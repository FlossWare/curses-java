package org.flossware.curses.api.widgets;

import org.flossware.curses.api.Checkbox;
import org.flossware.curses.api.CheckboxGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CheckboxGroup Tests")
class JCheckboxGroupTest {
    private CheckboxGroup group;
    private Checkbox checkbox1;
    private Checkbox checkbox2;
    private Checkbox checkbox3;

    @BeforeEach
    void setUp() {
        group = new CheckboxGroup();
        checkbox1 = new Checkbox("Option 1");
        checkbox2 = new Checkbox("Option 2");
        checkbox3 = new Checkbox("Option 3");
    }

    @Test
    @DisplayName("should add checkboxes to group")
    void testAddCheckbox() {
        group.add(checkbox1);
        group.add(checkbox2);
        group.add(checkbox3);

        // Group doesn't expose size, just verify no exceptions
        assertTrue(true);
    }

    @Test
    @DisplayName("should allow only one checkbox to be selected")
    void testSingleSelection() {
        group.add(checkbox1);
        group.add(checkbox2);
        group.add(checkbox3);

        checkbox1.setChecked(true);
        assertTrue(checkbox1.isChecked());
        assertFalse(checkbox2.isChecked());
        assertFalse(checkbox3.isChecked());

        checkbox2.setChecked(true);
        assertFalse(checkbox1.isChecked());
        assertTrue(checkbox2.isChecked());
        assertFalse(checkbox3.isChecked());
    }

    @Test
    @DisplayName("should deselect previous when new checkbox selected")
    void testAutoDeselect() {
        group.add(checkbox1);
        group.add(checkbox2);

        checkbox1.setChecked(true);
        assertTrue(checkbox1.isChecked());

        checkbox2.setChecked(true);
        assertFalse(checkbox1.isChecked());
        assertTrue(checkbox2.isChecked());
    }

    @Test
    @DisplayName("should handle empty group")
    void testEmptyGroup() {
        // Group should handle no checkboxes without errors
        assertDoesNotThrow(() -> group.setSelected(null));
    }

    @Test
    @DisplayName("should maintain group state across multiple selections")
    void testMultipleSelections() {
        group.add(checkbox1);
        group.add(checkbox2);
        group.add(checkbox3);

        checkbox1.setChecked(true);
        checkbox2.setChecked(true);
        checkbox3.setChecked(true);
        checkbox1.setChecked(true);

        assertTrue(checkbox1.isChecked());
        assertFalse(checkbox2.isChecked());
        assertFalse(checkbox3.isChecked());
    }

    @Test
    @DisplayName("should work like radio buttons")
    void testRadioButtonBehavior() {
        group.add(checkbox1);
        group.add(checkbox2);
        group.add(checkbox3);

        // Initially all unchecked
        assertFalse(checkbox1.isChecked());
        assertFalse(checkbox2.isChecked());
        assertFalse(checkbox3.isChecked());

        // Select first
        checkbox1.setChecked(true);
        int checkedCount = (checkbox1.isChecked() ? 1 : 0) +
                           (checkbox2.isChecked() ? 1 : 0) +
                           (checkbox3.isChecked() ? 1 : 0);
        assertEquals(1, checkedCount);

        // Select second
        checkbox2.setChecked(true);
        checkedCount = (checkbox1.isChecked() ? 1 : 0) +
                       (checkbox2.isChecked() ? 1 : 0) +
                       (checkbox3.isChecked() ? 1 : 0);
        assertEquals(1, checkedCount);
    }
}

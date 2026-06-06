package org.flossware.curses.api.widgets;

import org.flossware.curses.api.TextField;
import org.flossware.curses.api.edit.Clipboard;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TextField Advanced Editing Tests")
class TextFieldAdvancedTest extends ComponentTestBase {
    private TextField field;

    @BeforeEach
    void setUp() {
        field = new TextField("Hello World");
        field.setSize(30, 1);
        field.setLocation(0, 0);
        Clipboard.getInstance().clear();
    }

    // Selection tests
    @Test
    @DisplayName("should set and get selection")
    void testSetSelection() {
        field.setSelection(0, 5);

        assertTrue(field.hasSelection());
        assertEquals("Hello", field.getSelectedText());
    }

    @Test
    @DisplayName("should clear selection")
    void testClearSelection() {
        field.setSelection(0, 5);
        field.clearSelection();

        assertFalse(field.hasSelection());
        assertEquals("", field.getSelectedText());
    }

    @Test
    @DisplayName("should normalize selection order")
    void testSelectionNormalization() {
        field.setSelection(5, 0);  // Reversed order

        assertEquals("Hello", field.getSelectedText());
    }

    // Clipboard tests
    @Test
    @DisplayName("should copy selected text to clipboard")
    void testCopy() {
        field.setSelection(6, 11);  // "World"
        field.copy();

        assertEquals("World", Clipboard.getInstance().getContent());
        assertEquals("Hello World", field.getText());  // Text unchanged
    }

    @Test
    @DisplayName("should cut selected text")
    void testCut() {
        field.setSelection(6, 11);  // "World"
        field.cut();

        assertEquals("World", Clipboard.getInstance().getContent());
        assertEquals("Hello ", field.getText());
        assertFalse(field.hasSelection());
    }

    @Test
    @DisplayName("should paste clipboard content")
    void testPaste() {
        Clipboard.getInstance().setContent("Java");
        field.moveCursor(-6);  // Move to position 5
        field.paste();

        assertEquals("HelloJava World", field.getText());
    }

    @Test
    @DisplayName("should replace selection when pasting")
    void testPasteReplacesSelection() {
        Clipboard.getInstance().setContent("Java");
        field.setSelection(6, 11);  // Select "World"
        field.paste();

        assertEquals("Hello Java", field.getText());
    }

    // Word navigation tests
    @Test
    @DisplayName("should move to word start")
    void testMoveToWordStart() {
        field.moveCursor(-1);  // Position at 'd' in "World"
        field.moveToWordStart();

        // Should move to start of "World"
        field.insertChar('X');
        assertTrue(field.getText().startsWith("Hello XWorld"));
    }

    @Test
    @DisplayName("should move to word end")
    void testMoveToWordEnd() {
        field.moveToStart();
        field.moveToWordEnd();

        // Should be after "Hello"
        field.insertChar('X');
        assertTrue(field.getText().startsWith("HelloX"));
    }

    @Test
    @DisplayName("should move to text start")
    void testMoveToStart() {
        field.moveToStart();

        field.insertChar('X');
        assertEquals("XHello World", field.getText());
    }

    @Test
    @DisplayName("should move to text end")
    void testMoveToEnd() {
        field.moveToStart();
        field.moveToEnd();

        field.insertChar('X');
        assertEquals("Hello WorldX", field.getText());
    }

    // Advanced deletion tests
    @Test
    @DisplayName("should delete forward")
    void testDeleteForward() {
        field.moveToStart();
        field.deleteForward();

        assertEquals("ello World", field.getText());
    }

    @Test
    @DisplayName("should delete word")
    void testDeleteWord() {
        field.moveToStart();
        field.deleteWord();

        assertTrue(field.getText().startsWith(" World") || field.getText().startsWith("World"));
    }

    @Test
    @DisplayName("should not delete forward at end of text")
    void testDeleteForwardAtEnd() {
        field.deleteForward();

        assertEquals("Hello World", field.getText());
    }

    // Undo/Redo tests (basic - full command pattern would need more work)
    // Edge cases
    @Test
    @DisplayName("should handle empty selection operations gracefully")
    void testEmptySelectionOperations() {
        field.clearSelection();
        field.copy();
        field.cut();

        assertEquals("Hello World", field.getText());
    }

    @Test
    @DisplayName("should handle clipboard operations when not editable")
    void testClipboardWhenNotEditable() {
        field.setEditable(false);
        field.setSelection(0, 5);

        field.cut();
        assertEquals("Hello World", field.getText());  // Unchanged

        Clipboard.getInstance().setContent("Test");
        field.paste();
        assertEquals("Hello World", field.getText());  // Unchanged
    }
}

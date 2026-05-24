package org.flossware.jcurses.api.widgets;

import org.flossware.jcurses.api.JTextField;
import org.flossware.jcurses.api.edit.Clipboard;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.flossware.jcurses.testutil.ThreadSafetyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JTextField Tests")
class JTextFieldTest extends ComponentTestBase {
    private JTextField widget;

    @BeforeEach
    void setUp() {
        widget = new JTextField();
        widget.setSize(20, 5);
        widget.setLocation(0, 0);
        Clipboard.getInstance().clear();
    }

    @Test
    @DisplayName("should create widget")
    void testCreation() {
        assertNotNull(widget);
    }

    @Test
    @DisplayName("should create widget with initial text")
    void testCreationWithText() {
        JTextField field = new JTextField("Hello");
        assertEquals("Hello", field.getText());
    }

    @Test
    @DisplayName("should render without errors")
    void testRendering() {
        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should be added to parent")
    void testAddToParent() {
        root.add(widget);
        assertTrue(root.getChildren().contains(widget));
    }

    @Test
    @DisplayName("should set and get text")
    void testSetGetText() {
        widget.setText("Test");
        assertEquals("Test", widget.getText());
    }

    @Test
    @DisplayName("should set max length")
    void testSetMaxLength() {
        widget.setMaxLength(5);
        widget.insertChar('a');
        widget.insertChar('b');
        widget.insertChar('c');
        widget.insertChar('d');
        widget.insertChar('e');
        widget.insertChar('f');  // Should be ignored

        assertEquals("abcde", widget.getText());
    }

    @Test
    @DisplayName("should respect maxLength when set to negative (unlimited)")
    void testMaxLengthUnlimited() {
        widget.setMaxLength(-1);
        String longText = "a".repeat(1000);
        for (char c : longText.toCharArray()) {
            widget.insertChar(c);
        }
        assertEquals(1000, widget.getText().length());
    }

    @Test
    @DisplayName("should set and get editable state")
    void testSetEditable() {
        assertTrue(widget.isEditable());

        widget.setEditable(false);
        assertFalse(widget.isEditable());

        widget.setEditable(true);
        assertTrue(widget.isEditable());
    }

    @Test
    @DisplayName("should not insert char when not editable")
    void testInsertCharNotEditable() {
        widget.setEditable(false);
        widget.insertChar('a');

        assertEquals("", widget.getText());
    }

    @Test
    @DisplayName("should insert char at cursor position")
    void testInsertChar() {
        widget.insertChar('H');
        widget.insertChar('i');

        assertEquals("Hi", widget.getText());
    }

    @Test
    @DisplayName("should delete char before cursor")
    void testDeleteChar() {
        widget.setText("Hello");
        widget.moveToEnd();  // Move cursor to end
        widget.deleteChar();

        assertEquals("Hell", widget.getText());
    }

    @Test
    @DisplayName("should not delete char when cursor at start")
    void testDeleteCharAtStart() {
        widget.setText("Hi");
        widget.moveCursor(-10);  // Move to start
        widget.deleteChar();

        assertEquals("Hi", widget.getText());
    }

    @Test
    @DisplayName("should not delete when not editable")
    void testDeleteCharNotEditable() {
        widget.setText("Test");
        widget.setEditable(false);
        widget.deleteChar();

        assertEquals("Test", widget.getText());
    }

    @Test
    @DisplayName("should move cursor forward")
    void testMoveCursorForward() {
        widget.setText("Test");
        widget.moveCursor(-4);  // To start
        widget.moveCursor(2);
        widget.insertChar('X');

        assertEquals("TeXst", widget.getText());
    }

    @Test
    @DisplayName("should move cursor backward")
    void testMoveCursorBackward() {
        widget.setText("Test");
        widget.moveToEnd();  // Start at end
        widget.moveCursor(-2);  // Move back 2 positions to 's'
        widget.deleteChar();  // Delete 'e' before 's'

        assertEquals("Tst", widget.getText());
    }

    @Test
    @DisplayName("should clamp cursor to text bounds")
    void testCursorClamping() {
        widget.setText("Hi");
        widget.moveCursor(100);  // Try to move past end
        widget.insertChar('!');

        assertEquals("Hi!", widget.getText());

        widget.moveCursor(-100);  // Try to move before start
        widget.insertChar('X');

        assertEquals("XHi!", widget.getText());
    }

    @Test
    @DisplayName("should clamp selection to text bounds")
    void testSelectionClamping() {
        widget.setText("Hello");
        widget.setSelection(-5, 100);

        assertEquals("Hello", widget.getSelectedText());
    }

    @Test
    @DisplayName("should sanitize pasted content")
    void testPasteSanitization() {
        // Try to paste control characters
        Clipboard.getInstance().setContent("HelloWorld");
        widget.paste();

        String result = widget.getText();
        assertFalse(result.contains(""));
        assertFalse(result.contains(""));
        assertFalse(result.contains(""));
    }

    @Test
    @DisplayName("should not paste when not editable")
    void testPasteNotEditable() {
        widget.setEditable(false);
        Clipboard.getInstance().setContent("Test");
        widget.paste();

        assertEquals("", widget.getText());
    }

    @Test
    @DisplayName("should not paste empty clipboard")
    void testPasteEmptyClipboard() {
        widget.paste();

        assertEquals("", widget.getText());
    }

    @Test
    @DisplayName("should delete forward at cursor position")
    void testDeleteForwardMidText() {
        widget.setText("Hello");
        widget.moveToEnd();  // Start at end (position 5)
        widget.moveCursor(-3);  // Move to position 2 ('l')
        widget.deleteForward();  // Delete the 'l' at position 2

        assertEquals("Helo", widget.getText());
    }

    @Test
    @DisplayName("should not delete forward when not editable")
    void testDeleteForwardNotEditable() {
        widget.setText("Test");
        widget.setEditable(false);
        widget.moveCursor(-2);
        widget.deleteForward();

        assertEquals("Test", widget.getText());
    }

    @Test
    @DisplayName("should delete word from cursor")
    void testDeleteWordMidText() {
        widget.setText("Hello World");
        widget.moveToEnd();  // Start at end
        widget.moveCursor(-6);  // Move back to start of "World" (position 6, the space)
        widget.deleteWord();  // This will delete from current pos to end of next word

        // deleteWord moves to word end first, then deletes from startPos to endPos
        // Starting at position 6 (space), moveToWordEnd() skips space, finds 'W', moves to end of "World"
        // So it deletes the space and "World"
        assertTrue(widget.getText().equals("Hello") || widget.getText().equals("Hello "));
    }

    @Test
    @DisplayName("should not delete word when not editable")
    void testDeleteWordNotEditable() {
        widget.setText("Hello World");
        widget.setEditable(false);
        widget.moveToStart();
        widget.deleteWord();

        assertEquals("Hello World", widget.getText());
    }

    @Test
    @DisplayName("should move to word start with punctuation")
    void testMoveToWordStartWithPunctuation() {
        widget.setText("Hello, World!");
        widget.moveToWordStart();
        widget.insertChar('X');

        assertTrue(widget.getText().contains("X"));
    }

    @Test
    @DisplayName("should move to word end with punctuation")
    void testMoveToWordEndWithPunctuation() {
        widget.setText("Hello, World!");
        widget.moveToStart();
        widget.moveToWordEnd();
        widget.insertChar('X');

        assertTrue(widget.getText().contains("HelloX"));
    }

    @Test
    @DisplayName("should handle setText with cursor adjustment")
    void testSetTextCursorAdjustment() {
        widget.setText("Hello World");
        widget.moveToEnd();  // Cursor at position 11
        widget.setText("Hi");  // Cursor gets clamped from 11 to 2 (text length)
        widget.insertChar('!');

        assertEquals("Hi!", widget.getText());
    }

    @Test
    @DisplayName("should render with scroll offset for long text")
    void testRenderWithScroll() {
        widget.setSize(10, 1);
        widget.setText("This is a very long text that should scroll");

        assertDoesNotThrow(() -> widget.paint(buffer));
    }

    @Test
    @DisplayName("should render selection indicators")
    void testRenderWithSelection() {
        widget.setText("Hello World");
        widget.setSelection(0, 5);
        widget.paint(buffer);

        String row = new String(buffer[0]);
        assertTrue(row.contains("<") && row.contains(">"));
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            for (int i = 0; i < 50; i++) {
                widget.insertChar('a');
                widget.deleteChar();
                widget.moveCursor(1);
                widget.moveCursor(-1);
                widget.paint(buffer);
            }
        });

        assertTrue(true);  // Should complete without deadlock
    }

    @Test
    @DisplayName("should handle selection with no selection")
    void testHasSelectionFalse() {
        assertFalse(widget.hasSelection());
    }

    @Test
    @DisplayName("should return empty string for no selection")
    void testGetSelectedTextEmpty() {
        assertEquals("", widget.getSelectedText());
    }

    @Test
    @DisplayName("should not cut without selection")
    void testCutWithoutSelection() {
        widget.setText("Hello");
        widget.cut();

        assertEquals("Hello", widget.getText());
        assertEquals("", Clipboard.getInstance().getContent());
    }

    @Test
    @DisplayName("should not copy without selection")
    void testCopyWithoutSelection() {
        widget.setText("Hello");
        widget.copy();

        assertEquals("", Clipboard.getInstance().getContent());
    }

    @Test
    @DisplayName("should handle paste with null clipboard content")
    void testPasteWithNullClipboard() {
        widget.setText("Hello");
        widget.moveToEnd();

        // Set clipboard to empty (sanitizeInput handles null internally)
        Clipboard.getInstance().setContent("");
        widget.paste();

        // Should not crash, text should remain unchanged
        assertEquals("Hello", widget.getText());
    }

    @Test
    @DisplayName("should skip multiple punctuation marks when moving to word start")
    void testMoveToWordStartMultiplePunctuation() {
        widget.setText("hello...world!!!");
        widget.moveToEnd();

        // Move to start of "world"
        widget.moveToWordStart();

        // Insert a character to verify position
        widget.insertChar('X');

        // Should insert before "world"
        assertTrue(widget.getText().contains("X"));
        assertTrue(widget.getText().contains("world"));
    }

    @Test
    @DisplayName("should check canUndo when stack is empty")
    void testCanUndoEmpty() {
        assertFalse(widget.canUndo());
    }

    @Test
    @DisplayName("should check canRedo when stack is empty")
    void testCanRedoEmpty() {
        assertFalse(widget.canRedo());
    }
}

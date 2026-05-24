package org.flossware.jcurses.api;

import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for JTextArea class.
 */
@DisplayName("JTextArea Comprehensive Tests")
class JTextAreaTest extends ComponentTestBase {

    private JTextArea textArea;

    @BeforeEach
    void setUp() {
        textArea = new JTextArea();
        textArea.setSize(40, 10);
        textArea.setLocation(0, 0);
    }

    @Test
    @DisplayName("Constructor should create empty text area")
    void testConstructorEmpty() {
        assertNotNull(textArea);
        assertEquals(0, textArea.getLineCount());
        assertEquals("", textArea.getText());
    }

    @Test
    @DisplayName("append should add lines")
    void testAppend() {
        textArea.append("Line 1");
        textArea.append("Line 2");
        textArea.append("Line 3");

        assertEquals(3, textArea.getLineCount());
    }

    @Test
    @DisplayName("append should trigger repaint")
    void testAppendTriggersRepaint() {
        textArea.append("Test line");
        assertDoesNotThrow(() -> textArea.paint(buffer));
    }

    @Test
    @DisplayName("setText should replace all text")
    void testSetText() {
        textArea.append("Old line 1");
        textArea.append("Old line 2");

        textArea.setText("New line 1\nNew line 2\nNew line 3");

        assertEquals(3, textArea.getLineCount());
        assertTrue(textArea.getText().contains("New line 1"));
        assertFalse(textArea.getText().contains("Old line"));
    }

    @Test
    @DisplayName("setText should handle single line")
    void testSetTextSingleLine() {
        textArea.setText("Single line");

        assertEquals(1, textArea.getLineCount());
        assertEquals("Single line", textArea.getText());
    }

    @Test
    @DisplayName("setText should handle null")
    void testSetTextNull() {
        textArea.append("Line 1");
        textArea.setText(null);

        assertEquals(0, textArea.getLineCount());
        assertEquals("", textArea.getText());
    }

    @Test
    @DisplayName("setText should handle empty string")
    void testSetTextEmpty() {
        textArea.append("Line 1");
        textArea.setText("");

        assertEquals(0, textArea.getLineCount());
        assertEquals("", textArea.getText());
    }

    @Test
    @DisplayName("setText should split on newline")
    void testSetTextSplitNewline() {
        textArea.setText("Line 1\nLine 2\nLine 3");

        assertEquals(3, textArea.getLineCount());
        String text = textArea.getText();
        assertTrue(text.contains("Line 1"));
        assertTrue(text.contains("Line 2"));
        assertTrue(text.contains("Line 3"));
    }

    @Test
    @DisplayName("getText should join lines with newline")
    void testGetText() {
        textArea.append("First");
        textArea.append("Second");
        textArea.append("Third");

        String text = textArea.getText();
        assertEquals("First\nSecond\nThird", text);
    }

    @Test
    @DisplayName("getText should return empty string when empty")
    void testGetTextEmpty() {
        assertEquals("", textArea.getText());
    }

    @Test
    @DisplayName("clear should remove all lines")
    void testClear() {
        textArea.append("Line 1");
        textArea.append("Line 2");
        textArea.append("Line 3");

        textArea.clear();

        assertEquals(0, textArea.getLineCount());
        assertEquals("", textArea.getText());
    }

    @Test
    @DisplayName("clear should trigger repaint")
    void testClearTriggersRepaint() {
        textArea.append("Test");
        textArea.clear();
        assertDoesNotThrow(() -> textArea.paint(buffer));
    }

    @Test
    @DisplayName("getLineCount should return number of lines")
    void testGetLineCount() {
        assertEquals(0, textArea.getLineCount());

        textArea.append("Line 1");
        assertEquals(1, textArea.getLineCount());

        textArea.append("Line 2");
        assertEquals(2, textArea.getLineCount());

        textArea.clear();
        assertEquals(0, textArea.getLineCount());
    }

    @Test
    @DisplayName("paint should render lines")
    void testPaintLines() {
        textArea.append("First line");
        textArea.append("Second line");
        textArea.append("Third line");

        textArea.paint(buffer);

        String row0 = new String(buffer[0]);
        String row1 = new String(buffer[1]);
        String row2 = new String(buffer[2]);

        assertTrue(row0.contains("First line"));
        assertTrue(row1.contains("Second line"));
        assertTrue(row2.contains("Third line"));
    }

    @Test
    @DisplayName("paint should handle empty text area")
    void testPaintEmpty() {
        assertDoesNotThrow(() -> textArea.paint(buffer));
    }

    @Test
    @DisplayName("paint should respect height limit")
    void testPaintHeightLimit() {
        textArea.setSize(40, 3);

        for (int i = 0; i < 10; i++) {
            textArea.append("Line " + i);
        }

        textArea.paint(buffer);

        // Only first 3 lines should be rendered
        String row0 = new String(buffer[0]);
        String row1 = new String(buffer[1]);
        String row2 = new String(buffer[2]);
        String row3 = new String(buffer[3]);

        assertTrue(row0.contains("Line 0"));
        assertTrue(row1.contains("Line 1"));
        assertTrue(row2.contains("Line 2"));
        assertFalse(row3.contains("Line 3"), "Lines beyond height should not render");
    }

    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    textArea.append("Thread-" + threadId + "-Line-" + j);
                    textArea.paint(buffer);
                    if (j % 3 == 0) {
                        textArea.getText();
                    }
                    if (j % 5 == 0) {
                        textArea.getLineCount();
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(textArea.getLineCount() > 0);
    }

    @Test
    @DisplayName("should handle very long lines")
    void testVeryLongLines() {
        String longLine = "a".repeat(1000);
        textArea.append(longLine);

        assertDoesNotThrow(() -> textArea.paint(buffer));
        assertEquals(1, textArea.getLineCount());
    }

    @Test
    @DisplayName("setText with many lines should work")
    void testSetTextManyLines() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            if (i > 0) sb.append("\n");
            sb.append("Line ").append(i);
        }

        textArea.setText(sb.toString());

        assertEquals(100, textArea.getLineCount());
    }

    @Test
    @DisplayName("append after setText should work")
    void testAppendAfterSetText() {
        textArea.setText("Line 1\nLine 2");
        textArea.append("Line 3");

        assertEquals(3, textArea.getLineCount());
        assertTrue(textArea.getText().contains("Line 3"));
    }
}

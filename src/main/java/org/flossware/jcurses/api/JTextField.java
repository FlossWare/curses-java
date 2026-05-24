package org.flossware.jcurses.api;

import org.flossware.jcurses.api.edit.Clipboard;

import static org.flossware.jcurses.api.Constants.*;

public class JTextField extends Component {
    private StringBuilder text = new StringBuilder();
    private int cursorPosition = 0;
    private int maxLength = UNLIMITED;
    private boolean editable = true;

    // Selection support
    private int selectionStart = NO_INDEX;
    private int selectionEnd = NO_INDEX;

    public JTextField() {
        this("");
    }

    public JTextField(String initialText) {
        this.text = new StringBuilder(initialText);
        this.cursorPosition = initialText.length();
    }

    public void setText(String text) {
        renderLock.lock();
        try {
            this.text = new StringBuilder(text);
            this.cursorPosition = Math.min(cursorPosition, text.length());
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public String getText() {
        return text.toString();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void insertChar(char c) {
        if (!editable) return;

        renderLock.lock();
        try {
            if (maxLength < 0 || text.length() < maxLength) {
                text.insert(cursorPosition, c);
                cursorPosition++;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void deleteChar() {
        if (!editable) return;

        renderLock.lock();
        try {
            if (cursorPosition > 0) {
                text.deleteCharAt(cursorPosition - 1);
                cursorPosition--;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void moveCursor(int delta) {
        renderLock.lock();
        try {
            cursorPosition = Math.clamp(cursorPosition + delta, 0, text.length());
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    // Selection methods
    public void setSelection(int start, int end) {
        renderLock.lock();
        try {
            selectionStart = Math.clamp(start, 0, text.length());
            selectionEnd = Math.clamp(end, 0, text.length());
            if (selectionStart > selectionEnd) {
                int temp = selectionStart;
                selectionStart = selectionEnd;
                selectionEnd = temp;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void clearSelection() {
        renderLock.lock();
        try {
            selectionStart = NO_INDEX;
            selectionEnd = NO_INDEX;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public boolean hasSelection() {
        return selectionStart >= 0 && selectionEnd > selectionStart;
    }

    public String getSelectedText() {
        renderLock.lock();
        try {
            if (hasSelection()) {
                return text.substring(selectionStart, selectionEnd);
            }
            return "";
        } finally {
            renderLock.unlock();
        }
    }

    // Clipboard operations
    public void cut() {
        if (!editable || !hasSelection()) return;

        String selected = getSelectedText();
        Clipboard.getInstance().setContent(selected);
        deleteSelection();
    }

    public void copy() {
        if (!hasSelection()) return;

        String selected = getSelectedText();
        Clipboard.getInstance().setContent(selected);
    }

    public void paste() {
        if (!editable) return;

        String content = Clipboard.getInstance().getContent();
        if (!content.isEmpty()) {
            // Sanitize content to prevent terminal escape sequence injection
            content = sanitizeInput(content);
            if (hasSelection()) {
                deleteSelection();
            }
            insertString(content);
        }
    }

    /**
     * Sanitize input string by removing control characters except newline and tab.
     * Prevents terminal escape sequence injection attacks.
     */
    private String sanitizeInput(String input) {
        // Remove all control characters except newline and tab
        // For single-line text field, also remove newlines
        // Note: Clipboard.getContent() never returns null, always returns ""
        return input.replaceAll("[\\p{Cntrl}&&[^\\t]]", "");
    }

    private void insertString(String str) {
        renderLock.lock();
        try {
            for (char c : str.toCharArray()) {
                if (maxLength < 0 || text.length() < maxLength) {
                    text.insert(cursorPosition, c);
                    cursorPosition++;
                }
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    private void deleteSelection() {
        renderLock.lock();
        try {
            if (hasSelection()) {
                text.delete(selectionStart, selectionEnd);
                cursorPosition = selectionStart;
                clearSelection();
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    // Word navigation
    public void moveToWordStart() {
        renderLock.lock();
        try {
            int maxIterations = text.length() + 1;
            int iterations = 0;

            // Skip non-alphanumeric characters
            while (cursorPosition > 0 && !Character.isLetterOrDigit(text.charAt(cursorPosition - 1)) && iterations < maxIterations) {
                cursorPosition--;
                iterations++;
            }

            // Skip alphanumeric characters
            iterations = 0;
            while (cursorPosition > 0 && Character.isLetterOrDigit(text.charAt(cursorPosition - 1)) && iterations < maxIterations) {
                cursorPosition--;
                iterations++;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void moveToWordEnd() {
        renderLock.lock();
        try {
            int maxIterations = text.length() + 1;
            int iterations = 0;

            // Skip non-alphanumeric characters
            while (cursorPosition < text.length() && !Character.isLetterOrDigit(text.charAt(cursorPosition)) && iterations < maxIterations) {
                cursorPosition++;
                iterations++;
            }

            // Skip alphanumeric characters
            iterations = 0;
            while (cursorPosition < text.length() && Character.isLetterOrDigit(text.charAt(cursorPosition)) && iterations < maxIterations) {
                cursorPosition++;
                iterations++;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void moveToStart() {
        renderLock.lock();
        try {
            cursorPosition = 0;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void moveToEnd() {
        renderLock.lock();
        try {
            cursorPosition = text.length();
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    // Advanced deletion
    public void deleteForward() {
        if (!editable) return;

        renderLock.lock();
        try {
            if (cursorPosition < text.length()) {
                text.deleteCharAt(cursorPosition);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void deleteWord() {
        if (!editable) return;

        renderLock.lock();
        try {
            int startPos = cursorPosition;
            moveToWordEnd();
            int endPos = cursorPosition;
            if (endPos > startPos) {
                text.delete(startPos, endPos);
                cursorPosition = startPos;
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int displayWidth = width > 0 ? width - 2 : 20;
            String content = text.toString();

            int scrollOffset = Math.max(0, cursorPosition - displayWidth + 1);
            int endPos = Math.min(content.length(), scrollOffset + displayWidth);
            String visible = content.substring(scrollOffset, endPos);

            // For now, render selection with <angle brackets> around selected text
            // In a full color implementation, this would use reverse video
            String displayText = visible;
            if (hasSelection() && selectionStart < endPos && selectionEnd > scrollOffset) {
                int visStart = Math.max(0, selectionStart - scrollOffset);
                int visEnd = Math.min(visible.length(), selectionEnd - scrollOffset);
                if (visStart < visEnd) {
                    displayText = visible.substring(0, visStart) +
                                  "<" + visible.substring(visStart, visEnd) + ">" +
                                  visible.substring(visEnd);
                }
            }

            writeStringToBuffer(buffer, "[" + displayText + " ".repeat(Math.max(0, displayWidth - displayText.length())) + "]", getX(), getY());
        } finally {
            renderLock.unlock();
        }
    }
}

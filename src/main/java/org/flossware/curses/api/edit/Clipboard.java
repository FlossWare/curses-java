package org.flossware.curses.api.edit;

/**
 * Simple internal clipboard for text operations.
 * Thread-safe singleton for sharing clipboard content across components.
 */
public class Clipboard {
    private static final Clipboard INSTANCE = new Clipboard();
    private String content = "";

    private Clipboard() {}

    public static Clipboard getInstance() {
        return INSTANCE;
    }

    public synchronized void setContent(String content) {
        this.content = content != null ? content : "";
    }

    public synchronized String getContent() {
        return content;
    }

    public synchronized boolean hasContent() {
        return !content.isEmpty();
    }

    public synchronized void clear() {
        content = "";
    }
}

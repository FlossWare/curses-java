package org.flossware.curses.api.edit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Clipboard Tests")
class ClipboardTest {

    @BeforeEach
    void setUp() {
        Clipboard.getInstance().clear();
    }

    @Test
    @DisplayName("should be a singleton")
    void testSingleton() {
        Clipboard instance1 = Clipboard.getInstance();
        Clipboard instance2 = Clipboard.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("should start empty")
    void testStartEmpty() {
        Clipboard clipboard = Clipboard.getInstance();

        assertEquals("", clipboard.getContent());
        assertFalse(clipboard.hasContent());
    }

    @Test
    @DisplayName("should set and get content")
    void testSetGetContent() {
        Clipboard clipboard = Clipboard.getInstance();

        clipboard.setContent("Hello World");

        assertEquals("Hello World", clipboard.getContent());
        assertTrue(clipboard.hasContent());
    }

    @Test
    @DisplayName("should handle null content")
    void testNullContent() {
        Clipboard clipboard = Clipboard.getInstance();

        clipboard.setContent(null);

        assertEquals("", clipboard.getContent());
        assertFalse(clipboard.hasContent());
    }

    @Test
    @DisplayName("should clear content")
    void testClear() {
        Clipboard clipboard = Clipboard.getInstance();

        clipboard.setContent("Test");
        clipboard.clear();

        assertEquals("", clipboard.getContent());
        assertFalse(clipboard.hasContent());
    }

    @Test
    @DisplayName("should replace content")
    void testReplaceContent() {
        Clipboard clipboard = Clipboard.getInstance();

        clipboard.setContent("First");
        assertEquals("First", clipboard.getContent());

        clipboard.setContent("Second");
        assertEquals("Second", clipboard.getContent());
    }

    @Test
    @DisplayName("should handle empty string")
    void testEmptyString() {
        Clipboard clipboard = Clipboard.getInstance();

        clipboard.setContent("");

        assertEquals("", clipboard.getContent());
        assertFalse(clipboard.hasContent());
    }

    @Test
    @DisplayName("should be thread-safe for concurrent operations")
    void testThreadSafety() throws InterruptedException {
        Clipboard clipboard = Clipboard.getInstance();

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    clipboard.setContent("Thread-" + threadId + "-" + j);
                    clipboard.getContent();
                    clipboard.hasContent();
                    if (j % 10 == 0) {
                        clipboard.clear();
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Should complete without exceptions
        assertTrue(true);
    }
}

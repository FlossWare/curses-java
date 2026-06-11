package org.flossware.curses.api.widgets;

import org.flossware.curses.api.FileDialog;
import org.flossware.curses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileDialog Tests")
class JFileDialogTest extends ComponentTestBase {
    private FileDialog loadDialog;
    private FileDialog saveDialog;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loadDialog = new FileDialog("Select File", FileDialog.LOAD);
        loadDialog.setSize(40, 20);
        loadDialog.setLocation(0, 0);

        saveDialog = new FileDialog("Save File", FileDialog.SAVE);
        saveDialog.setSize(40, 20);
        saveDialog.setLocation(0, 0);
    }

    @Test
    @DisplayName("should create load dialog")
    void testCreation() {
        assertNotNull(loadDialog);
    }

    @Test
    @DisplayName("should create save dialog")
    void testSaveDialogCreation() {
        assertNotNull(saveDialog);
    }

    @Test
    @DisplayName("should render without errors")
    void testRendering() {
        assertDoesNotThrow(() -> loadDialog.paint(buffer));
    }

    @Test
    @DisplayName("should set and get selected file")
    void testSetSelectedFile() {
        File file = new File("test.txt");
        loadDialog.setSelectedFile(file);
        assertEquals(file, loadDialog.getSelectedFile());
    }

    @Test
    @DisplayName("should initially have no selected file")
    void testInitiallyNoSelectedFile() {
        assertNull(loadDialog.getSelectedFile());
    }

    @Test
    @DisplayName("should set directory")
    void testSetDirectory() throws IOException {
        File dir = tempDir.toFile();
        loadDialog.setDirectory(dir);
        assertDoesNotThrow(() -> loadDialog.paint(buffer));
    }

    @Test
    @DisplayName("should ignore null directory")
    void testSetNullDirectory() {
        File originalDir = new File(".");
        loadDialog.setDirectory(originalDir);
        loadDialog.setDirectory(null);
        assertDoesNotThrow(() -> loadDialog.paint(buffer));
    }

    @Test
    @DisplayName("should ignore non-directory file")
    void testSetNonDirectory() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.createFile(file);

        loadDialog.setDirectory(file.toFile());
        assertDoesNotThrow(() -> loadDialog.paint(buffer));
    }

    @Test
    @DisplayName("should render directory listing")
    void testRenderDirectoryListing() throws IOException {
        // Create some test files
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        Files.createDirectory(tempDir.resolve("subdir"));

        loadDialog.setDirectory(tempDir.toFile());
        assertDoesNotThrow(() -> loadDialog.paint(buffer));
    }

    @Test
    @DisplayName("should handle empty directory")
    void testEmptyDirectory() throws IOException {
        Path emptyDir = tempDir.resolve("empty");
        Files.createDirectory(emptyDir);

        loadDialog.setDirectory(emptyDir.toFile());
        assertDoesNotThrow(() -> loadDialog.paint(buffer));
    }

    @Test
    @DisplayName("should be modal by default")
    void testDefaultModal() {
        assertTrue(loadDialog.isModal());
    }

    @Test
    @DisplayName("should support non-modal mode")
    void testNonModal() {
        loadDialog.setModal(false);
        assertFalse(loadDialog.isModal());
    }

    @Test
    @DisplayName("should support title")
    void testTitle() {
        assertEquals("Select File", loadDialog.getTitle());
        loadDialog.setTitle("Choose a File");
        assertEquals("Choose a File", loadDialog.getTitle());
    }

    @Test
    @DisplayName("should be added to parent")
    void testAddToParent() {
        root.add(loadDialog);
        assertTrue(root.getChildren().contains(loadDialog));
    }
}

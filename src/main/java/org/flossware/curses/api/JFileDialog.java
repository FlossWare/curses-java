package org.flossware.curses.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JFileDialog extends JDialog {
    public static final int LOAD = 0;
    public static final int SAVE = 1;

    private int mode = LOAD;
    private File currentDirectory = new File(".");
    private File selectedFile = null;
    private final List<File> files = new ArrayList<>();

    public JFileDialog(String title, int mode) {
        super(title);
        this.mode = mode;
        loadDirectory(currentDirectory);
    }

    public void setDirectory(File directory) {
        renderLock.lock();
        try {
            if (directory != null && directory.isDirectory()) {
                this.currentDirectory = directory;
                loadDirectory(directory);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
    }

    private void loadDirectory(File directory) {
        files.clear();
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                files.add(file);
            }
        }
    }

    @Override
    public void paint(char[][] buffer) {
        super.paint(buffer);

        int currentY = getY() + 2;
        writeStringToBuffer(buffer, "Directory: " + currentDirectory.getAbsolutePath(), getX() + 2, currentY);
        currentY += 2;

        int maxFiles = Math.min(files.size(), height - 6);
        for (int i = 0; i < maxFiles; i++) {
            File file = files.get(i);
            String prefix = file.isDirectory() ? "[D] " : "[F] ";
            String name = file.getName();
            writeStringToBuffer(buffer, prefix + name, getX() + 2, currentY + i);
        }
    }
}

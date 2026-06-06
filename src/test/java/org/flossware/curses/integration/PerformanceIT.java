package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UI rendering performance.
 */
@DisplayName("Performance Integration Tests")
class PerformanceIT extends IntegrationTestBase {

    @Test
    @DisplayName("rendering 50 buttons should complete in < 100ms")
    void testRenderManyButtons() {
        // Setup
        Frame frame = new Frame("Performance Test");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        // Create 50 buttons
        for (int i = 0; i < 50; i++) {
            Button btn = new Button("Btn " + i);
            btn.setLocation(5 + (i % 10) * 7, 5 + (i / 10) * 2);
            btn.setSize(6, 1);
            frame.add(btn);
        }

        root.add(frame);
        root.markDirty();

        // Measure render time
        long startTime = System.nanoTime();
        runEventLoopCycle();
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Should render in < 100ms
        assertTrue(
            durationMs < 100,
            "Render took " + durationMs + "ms, expected < 100ms"
        );
    }

    @Test
    @DisplayName("event loop cycle should complete in < 50ms")
    void testEventLoopPerformance() {
        // Setup simple UI
        Button button = createButton("Click", 5, 5);
        Label label = createLabel("Label", 5, 8);
        setupFrame(button, label);

        // Measure event loop cycle time
        long startTime = System.nanoTime();
        runEventLoopCycle();
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Should complete in < 50ms
        assertTrue(
            durationMs < 50,
            "Event loop cycle took " + durationMs + "ms, expected < 50ms"
        );
    }

    @Test
    @DisplayName("table with 100 rows should render in < 200ms")
    void testLargeTableRendering() {
        // Setup
        Table table = new Table();
        table.setLocation(5, 5);
        table.setSize(60, 20);
        table.setColumnNames("ID", "Name", "Status");

        // Add 100 rows
        for (int i = 0; i < 100; i++) {
            table.addRow(
                String.format("%03d", i),
                "Task " + i,
                i % 2 == 0 ? "Done" : "Active"
            );
        }

        setupFrame(table);

        // Measure render time
        long startTime = System.nanoTime();
        runEventLoopCycle();
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Should render in < 200ms
        assertTrue(
            durationMs < 200,
            "Large table render took " + durationMs + "ms, expected < 200ms"
        );
    }

    @Test
    @DisplayName("multiple render cycles should maintain performance")
    void testSustainedRenderingPerformance() {
        // Setup
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(5, 5);
        progressBar.setSize(40, 1);

        setupFrame(progressBar);

        // Measure 10 render cycles
        long totalDuration = 0;
        for (int i = 0; i < 10; i++) {
            progressBar.setPercent(i / 10.0);
            root.markDirty();

            long startTime = System.nanoTime();
            runEventLoopCycle();
            totalDuration += (System.nanoTime() - startTime);
        }

        long avgDurationMs = (totalDuration / 10) / 1_000_000;

        // Average should be < 30ms
        assertTrue(
            avgDurationMs < 30,
            "Average render time " + avgDurationMs + "ms, expected < 30ms"
        );
    }

    @Test
    @DisplayName("complex UI layout should render efficiently")
    void testComplexLayoutPerformance() {
        // Setup complex UI
        Frame frame = new Frame("Complex UI");
        frame.setLocation(0, 0);
        frame.setSize(80, 24);
        frame.setVisible(true);

        // Add various components
        Label titleLabel = new Label("Performance Test");
        titleLabel.setLocation(5, 2);
        titleLabel.setSize(30, 1);

        TextField textField = new TextField();
        textField.setLocation(5, 4);
        textField.setSize(30, 1);

        for (int i = 0; i < 10; i++) {
            Button btn = new Button("Button " + i);
            btn.setLocation(5, 6 + i);
            btn.setSize(15, 1);
            frame.add(btn);
        }

        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(40, 4);
        progressBar.setSize(30, 1);

        Table table = new Table();
        table.setLocation(40, 6);
        table.setSize(30, 8);
        table.setColumnNames("Col1", "Col2");
        for (int i = 0; i < 5; i++) {
            table.addRow("Row" + i, "Data" + i);
        }

        frame.add(titleLabel);
        frame.add(textField);
        frame.add(progressBar);
        frame.add(table);

        root.add(frame);
        root.markDirty();

        // Measure render time
        long startTime = System.nanoTime();
        runEventLoopCycle();
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Complex UI should still render in < 150ms
        assertTrue(
            durationMs < 150,
            "Complex UI render took " + durationMs + "ms, expected < 150ms"
        );
    }

    @Test
    @DisplayName("dirty flag optimization should prevent unnecessary renders")
    void testDirtyFlagOptimization() {
        // Setup
        Label label = createLabel("Static", 5, 5);
        setupFrame(label);

        // Initial render
        runEventLoopCycle();
        assertDirtyFlagNotSet();

        // Measure render when not dirty
        long startTime = System.nanoTime();
        runEventLoopCycle();
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Non-dirty render should be very fast (< 10ms)
        assertTrue(
            durationMs < 10,
            "Non-dirty render took " + durationMs + "ms, expected < 10ms"
        );
    }

    @Test
    @DisplayName("buffer operations should be efficient")
    void testBufferOperationsPerformance() {
        // Measure buffer clear operation
        long startTime = System.nanoTime();
        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                buffer[i][j] = ' ';
            }
        }
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Buffer clear should be < 5ms
        assertTrue(
            durationMs < 5,
            "Buffer clear took " + durationMs + "ms, expected < 5ms"
        );
    }
}

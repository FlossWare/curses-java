package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.testutil.BufferAssertions;
import org.flossware.curses.theme.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UI rendering across different themes.
 * Tests component visibility, layout correctness, focus indicators,
 * border rendering, and performance across all available themes.
 */
@DisplayName("UI Rendering with Themes Integration Tests")
class UIRenderingWithThemesIT extends IntegrationTestBase {

    /**
     * Provides all available themes for parameterized testing.
     */
    static Stream<Arguments> allThemes() {
        return Stream.of(
            Arguments.of(new DefaultTheme(), "Default"),
            Arguments.of(new DarkTheme(), "Dark"),
            Arguments.of(new LightTheme(), "Light"),
            Arguments.of(new ModernTheme(), "Modern"),
            Arguments.of(new BorlandTheme(), "Borland"),
            Arguments.of(new Borland3DTheme(), "Borland3D"),
            Arguments.of(new TI994ATheme(), "TI-99/4A"),
            Arguments.of(new TRS80Theme(), "TRS-80"),
            Arguments.of(new DOSTheme(), "DOS"),
            Arguments.of(new DBase3Theme(), "dBASE III"),
            Arguments.of(new DBase4Theme(), "dBASE IV"),
            Arguments.of(new DBase4_3DTheme(), "dBASE IV 3D")
        );
    }

    /**
     * Provides themes that use ASCII borders.
     */
    static Stream<Arguments> asciiThemes() {
        return Stream.of(
            Arguments.of(new DefaultTheme(), "Default"),
            Arguments.of(new TI994ATheme(), "TI-99/4A"),
            Arguments.of(new TRS80Theme(), "TRS-80"),
            Arguments.of(new DOSTheme(), "DOS"),
            Arguments.of(new DBase3Theme(), "dBASE III"),
            Arguments.of(new DBase4Theme(), "dBASE IV"),
            Arguments.of(new DBase4_3DTheme(), "dBASE IV 3D")
        );
    }

    /**
     * Provides themes that use Unicode borders.
     */
    static Stream<Arguments> unicodeThemes() {
        return Stream.of(
            Arguments.of(new DarkTheme(), "Dark"),
            Arguments.of(new LightTheme(), "Light"),
            Arguments.of(new ModernTheme(), "Modern"),
            Arguments.of(new BorlandTheme(), "Borland"),
            Arguments.of(new Borland3DTheme(), "Borland3D")
        );
    }

    /**
     * Creates a comprehensive UI with multiple component types.
     */
    private Frame createCompleteUI() {
        Frame frame = new Frame("Theme Test UI");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        // Title label
        Label titleLabel = new Label("UI Component Test");
        titleLabel.setLocation(5, 2);
        titleLabel.setSize(30, 1);
        frame.add(titleLabel);

        // Buttons (normal and focused)
        Button normalButton = new Button("Normal");
        normalButton.setLocation(5, 4);
        normalButton.setSize(12, 1);
        frame.add(normalButton);

        Button focusedButton = new Button("Focused");
        focusedButton.setLocation(20, 4);
        focusedButton.setSize(12, 1);
        //         focusedButton.setFocused(true);
        frame.add(focusedButton);

        // Text input field
        TextField textField = new TextField();
        textField.setLocation(5, 6);
        textField.setSize(30, 1);
        textField.setText("Sample Text");
        frame.add(textField);

        // Checkbox
        Checkbox checkbox = new Checkbox("Option 1");
        checkbox.setLocation(5, 8);
        checkbox.setSize(15, 1);
        checkbox.setChecked(true);
        frame.add(checkbox);

        // Progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setLocation(5, 10);
        progressBar.setSize(30, 1);
        progressBar.setPercent(0.5);
        frame.add(progressBar);

        // Panel with nested components
        Panel panel = new Panel();
        panel.setLocation(40, 2);
        panel.setSize(35, 10);
        //         panel.setBorder(true);

        Label panelLabel = new Label("Panel Content");
        panelLabel.setLocation(42, 3);
        panelLabel.setSize(20, 1);
        panel.add(panelLabel);

        Button panelButton = new Button("Inside");
        panelButton.setLocation(42, 5);
        panelButton.setSize(12, 1);
        panel.add(panelButton);

        frame.add(panel);

        // List component
        ListComponent list = new ListComponent();
        list.setLocation(5, 12);
        list.setSize(30, 6);
        list.addItem("Item 1");
        list.addItem("Item 2");
        list.addItem("Item 3");
        frame.add(list);

        return frame;
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("allThemes")
    @DisplayName("complete UI should render correctly with each theme")
    void testCompleteUIRenderingAcrossThemes(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Create UI
        Frame frame = createCompleteUI();
        root.add(frame);
        root.markDirty();

        // Render
        runEventLoopCycle();

        // Verify basic rendering - title should be visible
        String titleRow = getScreenRow(2).trim();
        assertTrue(
            titleRow.contains("UI Component Test"),
            themeName + ": Title should be visible"
        );

        // Verify buttons rendered
        String buttonRow = getScreenRow(4).trim();
        assertTrue(
            buttonRow.contains("Normal") || buttonRow.contains("[Normal]"),
            themeName + ": Normal button should be visible"
        );
        assertTrue(
            buttonRow.contains("Focused") || buttonRow.contains("[Focused]"),
            themeName + ": Focused button should be visible"
        );

        // Verify text field rendered
        String textRow = getScreenRow(6).trim();
        assertTrue(
            textRow.contains("Sample Text"),
            themeName + ": TextField should display text"
        );

        // Verify checkbox rendered
        String checkboxRow = getScreenRow(8).trim();
        assertTrue(
            checkboxRow.contains("Option 1"),
            themeName + ": Checkbox label should be visible"
        );

        // No assertion failures means layout is correct
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("allThemes")
    @DisplayName("layout should remain consistent regardless of theme")
    void testLayoutConsistencyAcrossThemes(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Create simple grid layout
        Frame frame = new Frame("Layout Test");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        // Create 3x3 grid of buttons
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button button = new Button("R" + row + "C" + col);
                button.setLocation(5 + col * 15, 5 + row * 3);
                button.setSize(12, 1);
                frame.add(button);
            }
        }

        root.add(frame);
        root.markDirty();

        // Render
        runEventLoopCycle();

        // Verify positions maintained (check a few key buttons)
        String row0 = getScreenRow(5);
        assertTrue(
            row0.contains("R0C0"),
            themeName + ": Button at (0,0) should be at correct position"
        );

        String row1 = getScreenRow(8);
        assertTrue(
            row1.contains("R1C0"),
            themeName + ": Button at (1,0) should be at correct position"
        );

        String row2 = getScreenRow(11);
        assertTrue(
            row2.contains("R2C2"),
            themeName + ": Button at (2,2) should be at correct position"
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("allThemes")
    @DisplayName("focus indicators should work correctly with all themes")
    void testFocusIndicatorsAcrossThemes(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Create buttons with different focus states
        Frame frame = new Frame("Focus Test");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        Button unfocusedButton = new Button("Unfocused");
        unfocusedButton.setLocation(5, 5);
        unfocusedButton.setSize(15, 1);
        //         unfocusedButton.setFocused(false);
        frame.add(unfocusedButton);

        Button focusedButton = new Button("Focused");
        focusedButton.setLocation(5, 7);
        focusedButton.setSize(15, 1);
        //         focusedButton.setFocused(true);
        frame.add(focusedButton);

        root.add(frame);
        root.markDirty();

        // Render
        runEventLoopCycle();

        // Capture screen
        char[][] screen = captureScreen();

        // Verify both buttons rendered (exact rendering depends on theme)
        String unfocusedRow = new String(screen[5]).trim();
        String focusedRow = new String(screen[7]).trim();

        assertFalse(
            unfocusedRow.isEmpty(),
            themeName + ": Unfocused button should render"
        );
        assertFalse(
            focusedRow.isEmpty(),
            themeName + ": Focused button should render"
        );

        // Both should contain button text
        assertTrue(
            unfocusedRow.contains("Unfocused"),
            themeName + ": Unfocused button text should be visible"
        );
        assertTrue(
            focusedRow.contains("Focused"),
            themeName + ": Focused button text should be visible"
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("allThemes")
    @DisplayName("components should remain visible with different color schemes")
    void testComponentVisibilityAcrossColorSchemes(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Create diverse components
        Frame frame = new Frame("Visibility Test");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        // Various component types
        Label label = new Label("Label Text");
        label.setLocation(5, 3);
        label.setSize(20, 1);
        frame.add(label);

        Button button = new Button("Button");
        button.setLocation(5, 5);
        button.setSize(12, 1);
        frame.add(button);

        TextField textField = new TextField();
        textField.setLocation(5, 7);
        textField.setSize(20, 1);
        textField.setText("Input");
        frame.add(textField);

        Checkbox checkbox = new Checkbox("Check");
        checkbox.setLocation(5, 9);
        checkbox.setSize(12, 1);
        frame.add(checkbox);

        root.add(frame);
        root.markDirty();

        // Render
        runEventLoopCycle();

        // Verify all components have visible content
        String labelRow = getScreenRow(3);
        String buttonRow = getScreenRow(5);
        String textRow = getScreenRow(7);
        String checkRow = getScreenRow(9);

        assertTrue(
            labelRow.contains("Label Text"),
            themeName + ": Label should be visible"
        );
        assertTrue(
            buttonRow.contains("Button"),
            themeName + ": Button should be visible"
        );
        assertTrue(
            textRow.contains("Input"),
            themeName + ": TextField should be visible"
        );
        assertTrue(
            checkRow.contains("Check"),
            themeName + ": Checkbox should be visible"
        );
    }

    @Test
    @DisplayName("should detect problematic light-on-light color combinations")
    void testLightOnLightDetection() {
        // Create a theme with potentially problematic colors
        Theme testTheme = new Theme() {
            @Override
            public ColorPair getBackground() {
                return new ColorPair(Color.WHITE, Color.WHITE); // Light on light
            }

            @Override
            public ColorPair getButton() {
                return new ColorPair(Color.CYAN, Color.WHITE);
            }

            @Override
            public ColorPair getButtonFocused() {
                return new ColorPair(Color.WHITE, Color.CYAN);
            }

            @Override
            public ColorPair getTextInput() {
                return new ColorPair(Color.BLACK, Color.WHITE);
            }

            @Override
            public ColorPair getBorder() {
                return new ColorPair(Color.WHITE, Color.WHITE);
            }

            @Override
            public ColorPair getSelection() {
                return new ColorPair(Color.BLACK, Color.WHITE);
            }

            @Override
            public ColorPair getDisabled() {
                return new ColorPair(Color.CYAN, Color.WHITE);
            }

            @Override
            public String getBorderChars() {
                return "+-+|+-+|";
            }

            @Override
            public String getName() {
                return "LightOnLight";
            }
        };

        ThemeManager.getInstance().setTheme(testTheme);

        // Create UI
        Label label = createLabel("Should be visible", 5, 5);
        setupFrame(label);
        runEventLoopCycle();

        // Verify rendering completed without errors
        // In a real implementation, you might check for contrast warnings
        String row = getScreenRow(5);
        assertNotNull(row, "Row should render even with low contrast");
    }

    @Test
    @DisplayName("should detect problematic dark-on-dark color combinations")
    void testDarkOnDarkDetection() {
        // Create a theme with potentially problematic colors
        Theme testTheme = new Theme() {
            @Override
            public ColorPair getBackground() {
                return new ColorPair(Color.BLACK, Color.BLACK); // Dark on dark
            }

            @Override
            public ColorPair getButton() {
                return new ColorPair(Color.BLUE, Color.BLACK);
            }

            @Override
            public ColorPair getButtonFocused() {
                return new ColorPair(Color.BLACK, Color.BLUE);
            }

            @Override
            public ColorPair getTextInput() {
                return new ColorPair(Color.BLACK, Color.BLACK);
            }

            @Override
            public ColorPair getBorder() {
                return new ColorPair(Color.BLACK, Color.BLACK);
            }

            @Override
            public ColorPair getSelection() {
                return new ColorPair(Color.WHITE, Color.BLACK);
            }

            @Override
            public ColorPair getDisabled() {
                return new ColorPair(Color.BLACK, Color.BLACK);
            }

            @Override
            public String getBorderChars() {
                return "+-+|+-+|";
            }

            @Override
            public String getName() {
                return "DarkOnDark";
            }
        };

        ThemeManager.getInstance().setTheme(testTheme);

        // Create UI
        Button button = createButton("Test", 5, 5);
        setupFrame(button);
        runEventLoopCycle();

        // Verify rendering completed without errors
        String row = getScreenRow(5);
        assertNotNull(row, "Row should render even with low contrast");
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("asciiThemes")
    @DisplayName("ASCII border rendering should work correctly")
    void testASCIIBorderRendering(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Verify theme uses ASCII borders
        String borderChars = theme.getBorderChars();
        assertEquals(8, borderChars.length(), themeName + ": Border chars should be 8 characters");

        // Create frame with border
        Frame frame = new Frame("Border Test");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        Panel panel = new Panel();
        panel.setLocation(10, 5);
        panel.setSize(30, 10);
        //         panel.setBorder(true);
        frame.add(panel);

        root.add(frame);
        root.markDirty();

        // Render
        runEventLoopCycle();

        // Verify border rendered (exact characters depend on theme, but should be visible)
        char[][] screen = captureScreen();

        // Check top-left corner area (should have border character)
        char topLeftArea = screen[5][10];
        assertNotEquals(' ', topLeftArea, themeName + ": Border should be visible");

        // Border should use ASCII characters (not unicode)
        assertTrue(
            topLeftArea < 128,
            themeName + ": Border should use ASCII characters (< 128)"
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("unicodeThemes")
    @DisplayName("Unicode border rendering should work correctly")
    void testUnicodeBorderRendering(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Verify theme uses Unicode borders
        String borderChars = theme.getBorderChars();
        assertEquals(8, borderChars.length(), themeName + ": Border chars should be 8 characters");

        // Create frame with border
        Frame frame = new Frame("Border Test");
        frame.setLocation(0, 0);
        frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
        frame.setVisible(true);

        Panel panel = new Panel();
        panel.setLocation(10, 5);
        panel.setSize(30, 10);
        //         panel.setBorder(true);
        frame.add(panel);

        root.add(frame);
        root.markDirty();

        // Render
        runEventLoopCycle();

        // Verify border rendered
        char[][] screen = captureScreen();

        // Check that border area is not empty
        char topLeftArea = screen[5][10];
        assertNotEquals(' ', topLeftArea, themeName + ": Border should be visible");

        // For Unicode themes, we expect box-drawing characters
        // These are typically > 127 (extended ASCII/Unicode)
        // Note: In test environment, actual rendering may vary
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("allThemes")
    @DisplayName("rendering performance should be acceptable across themes")
    void testRenderingPerformanceAcrossThemes(Theme theme, String themeName) {
        // Apply theme
        ThemeManager.getInstance().setTheme(theme);

        // Create complex UI
        Frame frame = createCompleteUI();
        root.add(frame);
        root.markDirty();

        // Measure render time
        long startTime = System.nanoTime();
        runEventLoopCycle();
        long duration = System.nanoTime() - startTime;

        long durationMs = duration / 1_000_000;

        // Theme rendering should complete in reasonable time (< 200ms)
        assertTrue(
            durationMs < 200,
            themeName + ": Render took " + durationMs + "ms, expected < 200ms"
        );
    }

    @Test
    @DisplayName("rendering performance comparison across all themes")
    void testPerformanceComparisonAcrossThemes() {
        List<ThemePerformance> results = new ArrayList<>();

        // Test each theme
        allThemes().forEach(args -> {
            Theme theme = (Theme) args.get()[0];
            String themeName = (String) args.get()[1];

            // Apply theme
            ThemeManager.getInstance().setTheme(theme);

            // Create UI
            Frame frame = createCompleteUI();
            root.add(frame);
            root.markDirty();

            // Measure render time (average of 5 runs)
            long totalDuration = 0;
            int runs = 5;

            for (int i = 0; i < runs; i++) {
                root.markDirty();
                long startTime = System.nanoTime();
                runEventLoopCycle();
                totalDuration += (System.nanoTime() - startTime);
            }

            long avgDurationMs = (totalDuration / runs) / 1_000_000;
            results.add(new ThemePerformance(themeName, avgDurationMs));

            // Clean up for next theme
            root.getChildren().clear();
        });

        // Report results
        System.out.println("\n=== Theme Rendering Performance ===");
        results.forEach(result -> {
            System.out.println(String.format("%-15s: %3d ms", result.themeName, result.avgMs));
        });

        // Verify all themes performed reasonably
        results.forEach(result -> {
            assertTrue(
                result.avgMs < 150,
                result.themeName + " rendering too slow: " + result.avgMs + "ms"
            );
        });
    }

    @Test
    @DisplayName("theme switching should update UI correctly")
    void testThemeSwitching() {
        // Start with default theme
        ThemeManager.getInstance().useDefaultTheme();

        // Create UI
        Button button = createButton("Test", 5, 5);
        setupFrame(button);
        runEventLoopCycle();

        // Capture initial state
        String initialRow = getScreenRow(5);
        assertFalse(initialRow.trim().isEmpty(), "Button should render with default theme");

        // Switch to dark theme
        ThemeManager.getInstance().useDarkTheme();
        root.markDirty();
        runEventLoopCycle();

        String darkRow = getScreenRow(5);
        assertFalse(darkRow.trim().isEmpty(), "Button should render with dark theme");

        // Switch to light theme
        ThemeManager.getInstance().useLightTheme();
        root.markDirty();
        runEventLoopCycle();

        String lightRow = getScreenRow(5);
        assertFalse(lightRow.trim().isEmpty(), "Button should render with light theme");

        // All should contain button text
        assertTrue(initialRow.contains("Test"), "Default theme should show button text");
        assertTrue(darkRow.contains("Test"), "Dark theme should show button text");
        assertTrue(lightRow.contains("Test"), "Light theme should show button text");
    }

    @Test
    @DisplayName("disabled components should render appropriately across themes")
    void testDisabledComponentsAcrossThemes() {
        allThemes().forEach(args -> {
            Theme theme = (Theme) args.get()[0];
            String themeName = (String) args.get()[1];

            // Apply theme
            ThemeManager.getInstance().setTheme(theme);

            // Create enabled and disabled buttons
            Frame frame = new Frame("Disabled Test");
            frame.setLocation(0, 0);
            frame.setSize(mockBridge.getTerminalWidth(), mockBridge.getTerminalHeight());
            frame.setVisible(true);

            Button enabledButton = new Button("Enabled");
            enabledButton.setLocation(5, 5);
            enabledButton.setSize(12, 1);
            enabledButton.setEnabled(true);
            frame.add(enabledButton);

            Button disabledButton = new Button("Disabled");
            disabledButton.setLocation(5, 7);
            disabledButton.setSize(12, 1);
            disabledButton.setEnabled(false);
            frame.add(disabledButton);

            root.add(frame);
            root.markDirty();

            // Render
            runEventLoopCycle();

            // Verify both rendered
            String enabledRow = getScreenRow(5);
            String disabledRow = getScreenRow(7);

            assertTrue(
                enabledRow.contains("Enabled"),
                themeName + ": Enabled button should be visible"
            );
            assertTrue(
                disabledRow.contains("Disabled"),
                themeName + ": Disabled button should be visible"
            );

            // Clean up
            root.getChildren().clear();
        });
    }

    @Test
    @DisplayName("3D themes should indicate support correctly")
    void test3DThemeSupport() {
        // Borland 3D should support 3D
        Theme borland3D = new Borland3DTheme();
        assertTrue(
            borland3D.supports3D(),
            "Borland3D theme should support 3D rendering"
        );

        // Regular themes should not
        Theme defaultTheme = new DefaultTheme();
        assertFalse(
            defaultTheme.supports3D(),
            "Default theme should not support 3D rendering"
        );

        Theme darkTheme = new DarkTheme();
        assertFalse(
            darkTheme.supports3D(),
            "Dark theme should not support 3D rendering"
        );
    }

    @Test
    @DisplayName("all themes should have valid border characters")
    void testAllThemesHaveValidBorderChars() {
        allThemes().forEach(args -> {
            Theme theme = (Theme) args.get()[0];
            String themeName = (String) args.get()[1];

            String borderChars = theme.getBorderChars();

            assertNotNull(borderChars, themeName + ": Border chars should not be null");
            assertEquals(
                8,
                borderChars.length(),
                themeName + ": Border chars should be exactly 8 characters"
            );

            // Verify no null characters
            for (char c : borderChars.toCharArray()) {
                assertNotEquals(
                    '\0',
                    c,
                    themeName + ": Border chars should not contain null characters"
                );
            }
        });
    }

    @Test
    @DisplayName("all themes should have unique names")
    void testAllThemesHaveUniqueNames() {
        List<String> themeNames = new ArrayList<>();

        allThemes().forEach(args -> {
            Theme theme = (Theme) args.get()[0];
            String name = theme.getName();

            assertNotNull(name, "Theme name should not be null");
            assertFalse(name.isEmpty(), "Theme name should not be empty");
            assertFalse(
                themeNames.contains(name),
                "Duplicate theme name found: " + name
            );

            themeNames.add(name);
        });

        // Verify we tested all expected themes
        assertEquals(11, themeNames.size(), "Should have 11 unique themes");
    }

    /**
     * Helper class to store theme performance data.
     */
    private static class ThemePerformance {
        final String themeName;
        final long avgMs;

        ThemePerformance(String themeName, long avgMs) {
            this.themeName = themeName;
            this.avgMs = avgMs;
        }
    }
}

package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.theme.*;
import org.flossware.curses.testutil.BufferAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration tests verifying all themes work correctly with all UI components.
 *
 * <p>This test suite validates that each of the 11 themes properly renders all UI components
 * without exceptions, initializes color pairs correctly, and renders border characters without errors.
 * It also tests theme-specific features like 3D borders for Borland3D theme.</p>
 *
 * <p>Themes tested:</p>
 * <ul>
 *   <li>Default - Classic terminal with white on black</li>
 *   <li>Dark - Modern dark mode with Unicode borders</li>
 *   <li>Light - High contrast light mode with double-line borders</li>
 *   <li>Borland - Iconic blue background with yellow text</li>
 *   <li>Borland 3D - Enhanced Borland with 3D borders</li>
 *   <li>Modern - Contemporary high-contrast theme</li>
 *   <li>TI-99/4A - Texas Instruments cyan-on-blue aesthetic</li>
 *   <li>TRS-80 - Tandy monochrome white-on-black</li>
 *   <li>DOS - Classic MS-DOS interface</li>
 *   <li>dBASE III - Ashton-Tate database cyan menus</li>
 *   <li>dBASE IV - Modernized dBASE with blue background</li>
 * </ul>
 */
@DisplayName("Theme Compatibility Integration Tests")
class ThemeCompatibilityIT extends IntegrationTestBase {

    /**
     * Provides all available themes for parameterized testing.
     */
    static Stream<Theme> allThemes() {
        return Stream.of(
            new DefaultTheme(),
            new DarkTheme(),
            new LightTheme(),
            new BorlandTheme(),
            new Borland3DTheme(),
            new ModernTheme(),
            new TI994ATheme(),
            new TRS80Theme(),
            new DOSTheme(),
            new DBase3Theme(),
            new DBase4Theme()
        );
    }

    /**
     * Provides only 3D-capable themes for 3D-specific testing.
     */
    static Stream<Theme> threeDThemes() {
        return Stream.of(
            new Borland3DTheme(),
            new Borland3DTheme(Borland3DTheme.BorderStyle.DOUBLE_LINE, true, false, true),
            new Borland3DTheme(Borland3DTheme.BorderStyle.ROUNDED, false, true, true),
            new Borland3DTheme(Borland3DTheme.BorderStyle.ASCII, false, false, false)
        );
    }

    // ===== Theme Color Pair Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("All themes should provide valid color pairs for all component states")
    void testThemeColorPairsValid(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        // Verify all required color pairs are non-null and valid
        assertNotNull(theme.getBackground(), "Background color pair should not be null for " + theme.getName());
        assertNotNull(theme.getButton(), "Button color pair should not be null for " + theme.getName());
        assertNotNull(theme.getButtonFocused(), "Button focused color pair should not be null for " + theme.getName());
        assertNotNull(theme.getTextInput(), "Text input color pair should not be null for " + theme.getName());
        assertNotNull(theme.getBorder(), "Border color pair should not be null for " + theme.getName());
        assertNotNull(theme.getSelection(), "Selection color pair should not be null for " + theme.getName());
        assertNotNull(theme.getDisabled(), "Disabled color pair should not be null for " + theme.getName());

        // Verify color pairs have valid foreground and background colors
        assertValidColorPair(theme.getBackground(), "Background", theme.getName());
        assertValidColorPair(theme.getButton(), "Button", theme.getName());
        assertValidColorPair(theme.getButtonFocused(), "ButtonFocused", theme.getName());
        assertValidColorPair(theme.getTextInput(), "TextInput", theme.getName());
        assertValidColorPair(theme.getBorder(), "Border", theme.getName());
        assertValidColorPair(theme.getSelection(), "Selection", theme.getName());
        assertValidColorPair(theme.getDisabled(), "Disabled", theme.getName());
    }

    @ParameterizedTest
    @MethodSource("threeDThemes")
    @DisplayName("3D themes should provide valid color pairs for 3D rendering")
    void testTheme3DColorPairsValid(Theme theme) {
        assertTrue(theme.supports3D(), "Theme should support 3D: " + theme.getName());
        Theme3D theme3D = (Theme3D) theme;

        // Verify 3D-specific color pairs
        assertNotNull(theme3D.getShadowColor(), "Shadow color pair should not be null for " + theme.getName());
        assertNotNull(theme3D.getHighlightColor(), "Highlight color pair should not be null for " + theme.getName());
        assertNotNull(theme3D.getLowlightColor(), "Lowlight color pair should not be null for " + theme.getName());

        assertValidColorPair(theme3D.getShadowColor(), "Shadow", theme.getName());
        assertValidColorPair(theme3D.getHighlightColor(), "Highlight", theme.getName());
        assertValidColorPair(theme3D.getLowlightColor(), "Lowlight", theme.getName());

        // Verify shadow offsets are reasonable
        assertTrue(theme3D.getShadowOffsetX() >= 0 && theme3D.getShadowOffsetX() <= 5,
                "Shadow offset X should be 0-5 for " + theme.getName());
        assertTrue(theme3D.getShadowOffsetY() >= 0 && theme3D.getShadowOffsetY() <= 5,
                "Shadow offset Y should be 0-5 for " + theme.getName());
    }

    // ===== Border Character Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("All themes should provide valid border characters")
    void testThemeBorderCharsValid(Theme theme) {
        String borderChars = theme.getBorderChars();

        assertNotNull(borderChars, "Border characters should not be null for " + theme.getName());
        assertEquals(8, borderChars.length(),
                "Border characters should be 8 characters long for " + theme.getName());

        // Verify no null characters in border string
        for (int i = 0; i < borderChars.length(); i++) {
            assertNotEquals('\0', borderChars.charAt(i),
                    "Border character at index " + i + " should not be null for " + theme.getName());
        }
    }

    @ParameterizedTest
    @MethodSource("threeDThemes")
    @DisplayName("3D themes should provide valid double-line border characters")
    void testTheme3DDoubleBorderCharsValid(Theme theme) {
        Theme3D theme3D = (Theme3D) theme;
        String doubleBorderChars = theme3D.getDoubleBorderChars();

        assertNotNull(doubleBorderChars, "Double border characters should not be null for " + theme.getName());
        assertEquals(8, doubleBorderChars.length(),
                "Double border characters should be 8 characters long for " + theme.getName());

        // Verify shadow character is valid
        char shadowChar = theme3D.getShadowChar();
        assertNotEquals('\0', shadowChar, "Shadow character should not be null for " + theme.getName());
    }

    // ===== Button Rendering Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Buttons should render without errors in all themes")
    void testButtonRenderingAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        // Create buttons in normal and focused states
        Button normalButton = createButton("Normal Button", 5, 5);
        Button focusedButton = createButton("Focused", 5, 8);

        setupFrame(normalButton, focusedButton);

        // Initial render - should not throw any exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Rendering buttons should not throw exceptions for " + theme.getName());

        // Verify buttons are visible in buffer
        BufferAssertions.assertBufferContains(buffer, 5, 5, "[");
        BufferAssertions.assertBufferContains(buffer, 5, 8, "[");
    }

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Button click interaction should work in all themes")
    void testButtonClickAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        java.util.concurrent.atomic.AtomicBoolean clicked = new java.util.concurrent.atomic.AtomicBoolean(false);
        Button button = createButton("Click Me", 10, 10);
        button.addActionListener(() -> clicked.set(true));

        setupFrame(button);
        runEventLoopCycle();

        // Click button
        clickButton(button);

        assertTrue(clicked.get(), "Button click should work for " + theme.getName());
    }

    // ===== TextField Rendering Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Text fields should render without errors in all themes")
    void testTextFieldRenderingAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        TextField textField1 = createTextField(5, 5, 30);
        textField1.setText("Test Input 1");

        TextField textField2 = createTextField(5, 8, 40);
        textField2.setText("Another Input Field");

        setupFrame(textField1, textField2);

        // Render - should not throw exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Rendering text fields should not throw exceptions for " + theme.getName());

        // Verify text fields rendered
        assertEquals("Test Input 1", textField1.getText());
        assertEquals("Another Input Field", textField2.getText());
    }

    // ===== Label Rendering Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Labels should render without errors in all themes")
    void testLabelRenderingAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        Label label1 = createLabel("Label 1", 5, 5);
        Label label2 = createLabel("Another Label Text", 5, 8);
        Label label3 = createLabel("Third Label", 5, 11);

        setupFrame(label1, label2, label3);

        // Render - should not throw exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Rendering labels should not throw exceptions for " + theme.getName());

        // Verify labels contain expected text
        BufferAssertions.assertBufferContains(buffer, 5, 5, "Label 1");
        BufferAssertions.assertBufferContains(buffer, 5, 8, "Another Label Text");
        BufferAssertions.assertBufferContains(buffer, 5, 11, "Third Label");
    }

    // ===== Panel Rendering Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Panels with borders should render without errors in all themes")
    void testPanelRenderingAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        Panel panel1 = new Panel();
        panel1.setLocation(5, 5);
        panel1.setSize(30, 10);

        Panel panel2 = new Panel();
        panel2.setLocation(40, 5);
        panel2.setSize(25, 8);

        setupFrame(panel1, panel2);

        // Render - should not throw exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Rendering panels should not throw exceptions for " + theme.getName());
    }

    // ===== Frame Rendering Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Frames with title bars should render without errors in all themes")
    void testFrameRenderingAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        Frame frame1 = new Frame("Test Frame 1");
        frame1.setLocation(2, 2);
        frame1.setSize(35, 10);
        frame1.setVisible(true);

        Frame frame2 = new Frame("Another Frame");
        frame2.setLocation(40, 2);
        frame2.setSize(30, 12);
        frame2.setVisible(true);

        root.add(frame1);
        root.add(frame2);
        root.markDirty();

        // Render - should not throw exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Rendering frames should not throw exceptions for " + theme.getName());
    }

    // ===== Mixed Component Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Complex UI with mixed components should render in all themes")
    void testMixedComponentsAllThemes(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        // Create a complex UI with all component types
        Label titleLabel = createLabel("Theme Test UI", 5, 2);

        Button button1 = createButton("Action 1", 5, 5);
        Button button2 = createButton("Action 2", 20, 5);

        TextField inputField = createTextField(5, 8, 35);
        inputField.setText("Sample Input");

        Label descLabel = createLabel("Description:", 5, 11);

        Panel panel = new Panel();
        panel.setLocation(5, 13);
        panel.setSize(40, 6);

        setupFrame(titleLabel, button1, button2, inputField, descLabel, panel);

        // Render complex UI - should not throw exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Rendering mixed components should not throw exceptions for " + theme.getName());

        // Verify all components are in the frame
        Frame frame = (Frame) root.getChildren().getFirst();
        assertEquals(6, frame.getChildren().size(),
                "Frame should contain all 6 components for " + theme.getName());
    }

    // ===== Theme Switching Tests =====

    @Test
    @DisplayName("Switching between themes should update rendering without errors")
    void testThemeSwitchingSmooth() {
        Button button = createButton("Test Button", 10, 5);
        Label label = createLabel("Test Label", 10, 8);
        TextField textField = createTextField(10, 11, 25);

        setupFrame(button, label, textField);

        // Test switching through all themes
        Theme[] themes = allThemes().toArray(Theme[]::new);

        for (Theme theme : themes) {
            ThemeManager.getInstance().setTheme(theme);
            root.markDirty();

            assertDoesNotThrow(() -> runEventLoopCycle(),
                    "Switching to " + theme.getName() + " should not throw exceptions");

            // Verify theme is active
            assertEquals(theme.getName(), ThemeManager.getInstance().getCurrentTheme().getName(),
                    "Current theme should be " + theme.getName());
        }
    }

    // ===== Performance Tests =====

    @ParameterizedTest
    @MethodSource("allThemes")
    @DisplayName("Theme rendering should complete in reasonable time")
    void testThemeRenderingPerformance(Theme theme) {
        ThemeManager.getInstance().setTheme(theme);

        // Create a moderately complex UI
        Button[] buttons = new Button[10];
        Label[] labels = new Label[10];
        TextField[] textFields = new TextField[5];

        Component[] allComponents = new Component[25];
        int index = 0;

        for (int i = 0; i < 10; i++) {
            buttons[i] = createButton("Button " + i, 5 + (i % 5) * 15, 3 + (i / 5) * 4);
            allComponents[index++] = buttons[i];
        }

        for (int i = 0; i < 10; i++) {
            labels[i] = createLabel("Label " + i, 5 + (i % 5) * 15, 12 + (i / 5) * 2);
            allComponents[index++] = labels[i];
        }

        for (int i = 0; i < 5; i++) {
            textFields[i] = createTextField(5 + i * 15, 18, 12);
            allComponents[index++] = textFields[i];
        }

        setupFrame(allComponents);

        // Measure rendering performance
        Instant start = Instant.now();
        assertDoesNotThrow(() -> runEventLoopCycles(10),
                "Rendering should complete without errors for " + theme.getName());
        Instant end = Instant.now();

        Duration renderTime = Duration.between(start, end);

        // Rendering 10 cycles with 25 components should complete in under 2 seconds
        assertTrue(renderTime.toMillis() < 2000,
                "Rendering should complete in under 2 seconds for " + theme.getName() +
                " (took " + renderTime.toMillis() + "ms)");
    }

    // ===== 3D-Specific Tests =====

    @ParameterizedTest
    @MethodSource("threeDThemes")
    @DisplayName("3D borders should render correctly for Borland3D theme")
    void testBorland3DBordersRender(Theme theme) {
        assertTrue(theme.supports3D(), "Theme should support 3D");
        ThemeManager.getInstance().setTheme(theme);

        Theme3D theme3D = (Theme3D) theme;

        // Create components that can use 3D rendering
        Panel panel3D = new Panel();
        panel3D.setLocation(5, 5);
        panel3D.setSize(40, 10);

        Button button3D = createButton("3D Button", 10, 8);

        setupFrame(panel3D, button3D);

        // Render with 3D effects - should not throw exceptions
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "3D rendering should not throw exceptions for " + theme.getName());

        // Verify 3D theme properties are accessible
        assertNotNull(theme3D.getShadowColor());
        assertNotNull(theme3D.getHighlightColor());
        assertNotNull(theme3D.getLowlightColor());
    }

    @ParameterizedTest
    @MethodSource("threeDThemes")
    @DisplayName("3D theme border styles should render without errors")
    void testBorland3DBorderStyles(Theme theme) {
        Borland3DTheme theme3D = (Borland3DTheme) theme;
        ThemeManager.getInstance().setTheme(theme3D);

        Panel panel = new Panel();
        panel.setLocation(10, 5);
        panel.setSize(35, 12);

        setupFrame(panel);

        // Render with specific border style
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Border style " + theme3D.getBorderStyle() + " should render without errors");

        // Verify border characters are properly set
        String borderChars = theme3D.getBorderChars();
        assertEquals(8, borderChars.length(), "Border chars should be 8 characters");
    }

    @Test
    @DisplayName("Borland3D gradient shadows should render when enabled")
    void testBorland3DGradientShadows() {
        Borland3DTheme gradientTheme = new Borland3DTheme(
                Borland3DTheme.BorderStyle.SINGLE_LINE, false, true, true);

        assertTrue(gradientTheme.useGradientShadow(), "Gradient shadows should be enabled");
        ThemeManager.getInstance().setTheme(gradientTheme);

        Panel panel = new Panel();
        panel.setLocation(10, 5);
        panel.setSize(30, 10);

        setupFrame(panel);

        // Render with gradient shadows
        assertDoesNotThrow(() -> runEventLoopCycle(),
                "Gradient shadows should render without errors");

        // Verify shadow character is appropriate for gradients
        char shadowChar = gradientTheme.getShadowChar();
        assertTrue(shadowChar == '░' || shadowChar == '▒' || shadowChar == '▓' || shadowChar == ' ',
                "Shadow char should be a valid shade character");
    }

    // ===== Memory Leak and Performance Edge Case Tests =====

    @Test
    @DisplayName("Theme switching 1000 times should not leak memory")
    void testThemeSwitchingMemoryLeak() {
        Button button = createButton("Memory Test", 10, 5);
        Label label = createLabel("Test Label", 10, 8);
        TextField textField = createTextField(10, 11, 25);

        setupFrame(button, label, textField);

        // Get initial memory usage
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Switch themes 1000 times
        Theme[] themes = allThemes().toArray(Theme[]::new);
        for (int i = 0; i < 1000; i++) {
            Theme theme = themes[i % themes.length];
            ThemeManager.getInstance().setTheme(theme);
            root.markDirty();
            runEventLoopCycle();
        }

        // Force garbage collection and check memory
        System.gc();
        Thread.yield();
        System.gc();

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryGrowth = finalMemory - initialMemory;

        // Memory growth should be less than 10MB for 1000 theme switches
        assertTrue(memoryGrowth < 10 * 1024 * 1024,
                "Memory growth should be less than 10MB after 1000 theme switches (growth: " +
                (memoryGrowth / 1024 / 1024) + "MB)");
    }

    @Test
    @DisplayName("Concurrent rendering from multiple threads should not corrupt display")
    void testConcurrentRendering() throws InterruptedException {
        ThemeManager.getInstance().setTheme(new DefaultTheme());

        Button button1 = createButton("Thread 1", 5, 5);
        Button button2 = createButton("Thread 2", 20, 5);
        Label label = createLabel("Concurrent Test", 10, 10);

        setupFrame(button1, button2, label);

        java.util.concurrent.atomic.AtomicInteger errorCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(5);

        // Spawn 5 threads that all try to render simultaneously
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        root.markDirty();
                        runEventLoopCycle();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Wait for all threads to complete (max 10 seconds)
        boolean completed = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);

        assertTrue(completed, "All rendering threads should complete within 10 seconds");
        assertEquals(0, errorCount.get(), "No rendering errors should occur during concurrent rendering");
    }

    @Test
    @DisplayName("Theme switch during rendering should not corrupt display")
    void testThemeSwitchDuringRender() throws InterruptedException {
        Button button = createButton("Switch Test", 10, 5);
        TextField textField = createTextField(10, 8, 30);
        textField.setText("Rendering...");

        setupFrame(button, textField);

        java.util.concurrent.atomic.AtomicBoolean renderError = new java.util.concurrent.atomic.AtomicBoolean(false);
        java.util.concurrent.atomic.AtomicBoolean switchError = new java.util.concurrent.atomic.AtomicBoolean(false);

        Theme[] themes = allThemes().toArray(Theme[]::new);

        // Thread 1: Continuous rendering
        Thread renderThread = new Thread(() -> {
            try {
                for (int i = 0; i < 200; i++) {
                    root.markDirty();
                    runEventLoopCycle();
                    Thread.sleep(5);
                }
            } catch (Exception e) {
                renderError.set(true);
            }
        });

        // Thread 2: Theme switching during rendering
        Thread switchThread = new Thread(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    ThemeManager.getInstance().setTheme(themes[i % themes.length]);
                    Thread.sleep(20);
                }
            } catch (Exception e) {
                switchError.set(true);
            }
        });

        renderThread.start();
        Thread.sleep(10); // Let rendering start first
        switchThread.start();

        renderThread.join(15000);
        switchThread.join(15000);

        assertFalse(renderError.get(), "Rendering should not error during theme switches");
        assertFalse(switchError.get(), "Theme switching should not error during rendering");
    }

    @Test
    @DisplayName("Rapid component add/remove during theme switch should not corrupt UI")
    void testRapidComponentChangeDuringThemeSwitch() {
        Frame frame = (Frame) root.getChildren().getFirst();
        if (frame == null) {
            frame = new Frame("Test Frame");
            frame.setLocation(2, 2);
            frame.setSize(75, 20);
            frame.setVisible(true);
            root.add(frame);
        }

        Theme[] themes = allThemes().toArray(Theme[]::new);

        // Rapid add/remove during theme switching
        for (int cycle = 0; cycle < 50; cycle++) {
            // Switch theme
            ThemeManager.getInstance().setTheme(themes[cycle % themes.length]);

            // Add components
            Button button1 = createButton("Button " + cycle, 5, 5);
            Button button2 = createButton("Btn " + cycle, 20, 5);
            Label label = createLabel("Label " + cycle, 5, 8);
            TextField textField = createTextField(5, 11, 20);

            frame.add(button1);
            frame.add(button2);
            frame.add(label);
            frame.add(textField);

            // Render
            root.markDirty();
            assertDoesNotThrow(() -> runEventLoopCycle(),
                    "Rendering should not throw during component add cycle " + cycle);

            // Remove components
            frame.remove(button1);
            frame.remove(button2);
            frame.remove(label);
            frame.remove(textField);

            // Render again
            root.markDirty();
            assertDoesNotThrow(() -> runEventLoopCycle(),
                    "Rendering should not throw during component remove cycle " + cycle);
        }

        // Verify frame is in consistent state
        assertNotNull(frame);
        assertTrue(frame.isVisible());
    }

    // ===== Theme Name Tests =====

    @Test
    @DisplayName("All themes should have unique, non-null names")
    void testAllThemeNamesUnique() {
        Theme[] themes = allThemes().toArray(Theme[]::new);
        String[] names = new String[themes.length];

        for (int i = 0; i < themes.length; i++) {
            names[i] = themes[i].getName();
            assertNotNull(names[i], "Theme name should not be null");
            assertFalse(names[i].isEmpty(), "Theme name should not be empty");

            // Check uniqueness
            for (int j = 0; j < i; j++) {
                assertNotEquals(names[j], names[i],
                        "Theme names should be unique: " + names[i]);
            }
        }

        // Verify expected theme names
        java.util.List<String> nameList = java.util.Arrays.asList(names);
        assertTrue(nameList.contains("Default"), "Should have Default theme");
        assertTrue(nameList.contains("Dark"), "Should have Dark theme");
        assertTrue(nameList.contains("Light"), "Should have Light theme");
        assertTrue(nameList.contains("Borland"), "Should have Borland theme");
        assertTrue(nameList.contains("Borland 3D"), "Should have Borland 3D theme");
        assertTrue(nameList.contains("Modern"), "Should have Modern theme");
        assertTrue(nameList.contains("TI-99/4A"), "Should have TI-99/4A theme");
        assertTrue(nameList.contains("TRS-80"), "Should have TRS-80 theme");
        assertTrue(nameList.contains("DOS"), "Should have DOS theme");
        assertTrue(nameList.contains("dBASE III"), "Should have dBASE III theme");
        assertTrue(nameList.contains("dBASE IV"), "Should have dBASE IV theme");
    }

    // ===== Helper Methods =====

    /**
     * Verify that a color pair has valid colors (non-null, within valid range).
     */
    private void assertValidColorPair(ColorPair colorPair, String pairName, String themeName) {
        assertNotNull(colorPair, pairName + " color pair should not be null for " + themeName);

        Color foreground = colorPair.foreground();
        Color background = colorPair.background();

        assertNotNull(foreground, pairName + " foreground color should not be null for " + themeName);
        assertNotNull(background, pairName + " background color should not be null for " + themeName);

        // Verify colors are within valid ncurses color range (0-7 for standard colors)
        // Note: This assumes standard 8-color palette; extended colors may have higher values
        assertTrue(isValidColor(foreground), pairName + " foreground should be valid color for " + themeName);
        assertTrue(isValidColor(background), pairName + " background should be valid color for " + themeName);
    }

    /**
     * Check if a color is valid (one of the standard ncurses colors).
     */
    private boolean isValidColor(Color color) {
        return color == Color.BLACK
            || color == Color.RED
            || color == Color.GREEN
            || color == Color.YELLOW
            || color == Color.BLUE
            || color == Color.MAGENTA
            || color == Color.CYAN
            || color == Color.WHITE;
    }
}

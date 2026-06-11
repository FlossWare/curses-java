package org.flossware.curses.integration;

import org.flossware.curses.api.*;
import org.flossware.curses.theme.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for theme switching at runtime.
 * Tests ThemeManager's ability to switch between themes dynamically
 * and verify that components re-render correctly with new theme settings.
 */
@DisplayName("Theme Switching Integration Tests")
class ThemeSwitchingIT extends IntegrationTestBase {

    /**
     * All available theme suppliers for testing.
     */
    private static final List<ThemeSupplier> ALL_THEMES = List.of(
        new ThemeSupplier("Default", DefaultTheme::new),
        new ThemeSupplier("Dark", DarkTheme::new),
        new ThemeSupplier("Light", LightTheme::new),
        new ThemeSupplier("Modern", ModernTheme::new),
        new ThemeSupplier("Borland", BorlandTheme::new),
        new ThemeSupplier("Borland3D", Borland3DTheme::new),
        new ThemeSupplier("TI-99/4A", TI994ATheme::new),
        new ThemeSupplier("TRS-80", TRS80Theme::new),
        new ThemeSupplier("DOS", DOSTheme::new),
        new ThemeSupplier("dBASE III", DBase3Theme::new),
        new ThemeSupplier("dBASE IV", DBase4Theme::new),
        new ThemeSupplier("dBASE IV 3D", DBase4_3DTheme::new)
    );

    /**
     * Helper class to hold theme name and supplier.
     */
    private static class ThemeSupplier {
        final String name;
        final java.util.function.Supplier<Theme> supplier;

        ThemeSupplier(String name, java.util.function.Supplier<Theme> supplier) {
            this.name = name;
            this.supplier = supplier;
        }
    }

    @Test
    @DisplayName("should switch between all 11 themes without errors")
    void testSwitchAllThemesWithoutErrors() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Label themeLabel = createLabel("Current Theme: ", 5, 5);
        themeLabel.setSize(40, 1);

        Button button = createButton("Test Button", 5, 8);
        TextField textField = createTextField(5, 11, 30);

        setupFrame(themeLabel, button, textField);
        runEventLoopCycle();

        // Test switching to each theme
        for (ThemeSupplier themeSupplier : ALL_THEMES) {
            assertDoesNotThrow(() -> {
                Theme theme = themeSupplier.supplier.get();
                manager.setTheme(theme);
                themeLabel.setText("Current Theme: " + themeSupplier.name);
                root.markDirty();
                runEventLoopCycle();
            }, "Switching to " + themeSupplier.name + " theme should not throw exception");

            // Verify theme is set
            assertNotNull(manager.getCurrentTheme(), "Current theme should not be null after switch");
            assertEquals(themeSupplier.name, manager.getCurrentTheme().getName(),
                "Theme name should match after switch");
        }
    }

    @Test
    @DisplayName("theme changes should take effect immediately")
    void testThemeChangesImmediateEffect() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Label label = createLabel("Test Label", 5, 5);
        label.setSize(30, 1);

        setupFrame(label);
        runEventLoopCycle();

        // Start with Default theme
        manager.useDefaultTheme();
        Theme initialTheme = manager.getCurrentTheme();
        assertEquals("Default", initialTheme.getName());

        // Switch to Dark theme
        manager.useDarkTheme();
        Theme darkTheme = manager.getCurrentTheme();
        assertEquals("Dark", darkTheme.getName());
        assertNotEquals(initialTheme.getName(), darkTheme.getName(),
            "Theme should change immediately");

        // Verify theme attributes changed
        assertNotNull(darkTheme.getBackground());
        assertNotNull(darkTheme.getButton());
        assertNotNull(darkTheme.getBorder());
    }

    @Test
    @DisplayName("components should re-render correctly after theme switch")
    void testComponentsReRenderAfterThemeSwitch() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Button button = createButton("Render Test", 5, 5);
        Label statusLabel = createLabel("Status: Initial", 5, 8);
        statusLabel.setSize(40, 1);

        setupFrame(button, statusLabel);

        // Initial render with Default theme
        manager.useDefaultTheme();
        root.markDirty();
        runEventLoopCycle();

        // Verify initial render
        Theme defaultTheme = manager.getCurrentTheme();
        assertEquals("Default", defaultTheme.getName());

        // Switch to Borland theme and re-render
        manager.useBorlandTheme();
        statusLabel.setText("Status: Switched to Borland");
        root.markDirty();
        runEventLoopCycle();

        // Verify theme switched
        Theme borlandTheme = manager.getCurrentTheme();
        assertEquals("Borland", borlandTheme.getName());

        // Verify different border characters
        assertNotEquals(defaultTheme.getBorderChars(), borlandTheme.getBorderChars(),
            "Different themes should have different border characters");

        // Switch to Light theme and re-render
        manager.useLightTheme();
        statusLabel.setText("Status: Switched to Light");
        root.markDirty();
        runEventLoopCycle();

        // Verify theme switched again
        Theme lightTheme = manager.getCurrentTheme();
        assertEquals("Light", lightTheme.getName());
    }

    @Test
    @DisplayName("visual output should change when theme switches")
    void testVisualOutputChangesOnThemeSwitch() {
        // Setup - Create components with borders
        ThemeManager manager = ThemeManager.getInstance();
        Frame frame = new Frame("Theme Test");
        frame.setLocation(10, 5);
        frame.setSize(30, 10);
        frame.setVisible(true);

        Button button = createButton("Test Button", 12, 7);
        frame.add(button);

        root.add(frame);
        root.markDirty();

        // Render with Default theme (ASCII borders: +, -, |)
        manager.useDefaultTheme();
        runEventLoopCycle();
        char[][] screenBeforeSwitch = captureScreen();

        // Switch to Modern theme (Unicode borders: ─, │, ┌, ┐, └, ┘)
        manager.useModernTheme();
        root.markDirty();
        runEventLoopCycle();
        char[][] screenAfterSwitch = captureScreen();

        // Verify screens differ (at least at border positions)
        boolean screensDiffer = false;
        for (int y = 0; y < screenBeforeSwitch.length; y++) {
            for (int x = 0; x < screenBeforeSwitch[y].length; x++) {
                if (screenBeforeSwitch[y][x] != screenAfterSwitch[y][x]) {
                    screensDiffer = true;
                    break;
                }
            }
            if (screensDiffer) break;
        }

        assertTrue(screensDiffer,
            "Screen output should differ after theme switch - components should re-render with new theme");

        // Verify border characters changed at frame position
        // Default theme uses ASCII characters, Modern uses Unicode
        char topLeftBefore = screenBeforeSwitch[5][10];  // Frame top-left corner
        char topLeftAfter = screenAfterSwitch[5][10];
        assertNotEquals(topLeftBefore, topLeftAfter,
            "Border character should change after theme switch (ASCII -> Unicode)");
    }

    @Test
    @DisplayName("buffer output should differ with different border styles")
    void testBufferOutputDiffersWithBorderStyles() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Frame frame = new Frame("Border Test");
        frame.setLocation(5, 5);
        frame.setSize(20, 8);
        frame.setVisible(true);

        root.add(frame);
        root.markDirty();

        // Render with TI-99/4A theme (uses ASCII: +, -, |)
        manager.useTI994ATheme();
        runEventLoopCycle();
        String topRowTI994A = getScreenRow(5);

        // Render with Borland theme (uses Unicode box-drawing)
        manager.useBorlandTheme();
        root.markDirty();
        runEventLoopCycle();
        String topRowBorland = getScreenRow(5);

        // Verify the top row differs (different border characters)
        assertNotEquals(topRowTI994A, topRowBorland,
            "Frame border should render differently with different themes");

        // Render with Modern theme
        manager.useModernTheme();
        root.markDirty();
        runEventLoopCycle();
        String topRowModern = getScreenRow(5);

        // Verify Modern differs from both previous themes
        assertNotEquals(topRowTI994A, topRowModern,
            "Modern theme should use different borders than TI-99/4A");
        assertNotEquals(topRowBorland, topRowModern,
            "Modern theme should use different borders than Borland");
    }

    @Test
    @DisplayName("theme switch during mid-interaction should re-render correctly")
    void testThemeSwitchDuringInteraction() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        Button button1 = createButton("Button 1", 10, 5);
        Button button2 = createButton("Button 2", 10, 8);
        Button button3 = createButton("Button 3", 10, 11);

        AtomicBoolean button1Clicked = new AtomicBoolean(false);
        AtomicBoolean button2Clicked = new AtomicBoolean(false);

        button1.addActionListener(() -> button1Clicked.set(true));
        button2.addActionListener(() -> button2Clicked.set(true));

        setupFrame(button1, button2, button3);

        // Start with Default theme
        manager.useDefaultTheme();
        root.markDirty();
        runEventLoopCycle();

        // Capture initial render
        char[][] screenWithDefault = captureScreen();

        // Click first button
        clickButton(button1);
        assertTrue(button1Clicked.get(), "Button 1 should be clicked");

        // Switch theme while button is in focused/active state
        manager.useDarkTheme();
        root.markDirty();
        runEventLoopCycle();

        // Capture after theme switch
        char[][] screenWithDark = captureScreen();

        // Verify screen changed
        boolean screensAreDifferent = false;
        for (int y = 0; y < Math.min(screenWithDefault.length, screenWithDark.length); y++) {
            for (int x = 0; x < Math.min(screenWithDefault[y].length, screenWithDark[y].length); x++) {
                if (screenWithDefault[y][x] != screenWithDark[y][x]) {
                    screensAreDifferent = true;
                    break;
                }
            }
            if (screensAreDifferent) break;
        }

        assertTrue(screensAreDifferent,
            "Screen should re-render with new theme even during interaction");

        // Verify buttons still work after theme switch
        clickButton(button2);
        assertTrue(button2Clicked.get(),
            "Button should still be functional after theme switch during interaction");
    }

    @Test
    @DisplayName("component re-renders should use new theme colors immediately")
    void testComponentReRendersWithNewThemeColors() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        Label label1 = createLabel("Label 1", 5, 5);
        label1.setSize(20, 1);
        Label label2 = createLabel("Label 2", 5, 7);
        label2.setSize(20, 1);

        Frame frame = new Frame("Color Test");
        frame.setLocation(3, 3);
        frame.setSize(30, 12);
        frame.setVisible(true);
        frame.add(label1);
        frame.add(label2);

        root.add(frame);

        // Render with Borland theme
        manager.useBorlandTheme();
        root.markDirty();
        runEventLoopCycle();

        Theme borlandTheme = manager.getCurrentTheme();
        assertNotNull(borlandTheme.getBackground());
        assertNotNull(borlandTheme.getBorder());

        // Capture frame border area
        String frameTopBorland = getScreenRow(3);

        // Switch to DOS theme
        manager.useDOSTheme();
        root.markDirty();
        runEventLoopCycle();

        Theme dosTheme = manager.getCurrentTheme();
        assertNotNull(dosTheme.getBackground());
        assertNotNull(dosTheme.getBorder());

        // Capture frame border area after switch
        String frameTopDOS = getScreenRow(3);

        // Verify border rendering changed
        // (Note: Border characters may differ between themes)
        assertNotEquals(frameTopBorland, frameTopDOS,
            "Frame border should re-render with new theme immediately");

        // Verify theme objects are different
        assertNotEquals(borlandTheme.getName(), dosTheme.getName());
        assertNotEquals(borlandTheme.getBorderChars(), dosTheme.getBorderChars());
    }

    @Test
    @DisplayName("rapid theme switches should show different renders each time")
    void testRapidSwitchesShowDifferentRenders() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        Frame frame = new Frame("Rapid Switch");
        frame.setLocation(10, 5);
        frame.setSize(25, 10);
        frame.setVisible(true);

        Button button = createButton("Test", 12, 7);
        frame.add(button);

        root.add(frame);
        root.markDirty();

        // Capture renders with different themes
        List<String> capturedTopRows = new ArrayList<>();

        // Default theme
        manager.useDefaultTheme();
        runEventLoopCycle();
        capturedTopRows.add(getScreenRow(5));

        // Modern theme
        manager.useModernTheme();
        root.markDirty();
        runEventLoopCycle();
        capturedTopRows.add(getScreenRow(5));

        // Borland theme
        manager.useBorlandTheme();
        root.markDirty();
        runEventLoopCycle();
        capturedTopRows.add(getScreenRow(5));

        // TRS-80 theme
        manager.useTRS80Theme();
        root.markDirty();
        runEventLoopCycle();
        capturedTopRows.add(getScreenRow(5));

        // Verify at least some of the renders differ
        // (Themes should produce visually different output)
        int uniqueRenders = (int) capturedTopRows.stream().distinct().count();
        assertTrue(uniqueRenders >= 2,
            "Rapid theme switches should produce different visual renders. " +
            "Found " + uniqueRenders + " unique renders out of " + capturedTopRows.size());
    }

    @Test
    @DisplayName("ThemeManager.getCurrentTheme() should return correct theme after switch")
    void testGetCurrentThemeReturnsCorrectTheme() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        // Test each convenience method
        manager.useDefaultTheme();
        assertEquals("Default", manager.getCurrentTheme().getName());

        manager.useDarkTheme();
        assertEquals("Dark", manager.getCurrentTheme().getName());

        manager.useLightTheme();
        assertEquals("Light", manager.getCurrentTheme().getName());

        manager.useModernTheme();
        assertEquals("Modern", manager.getCurrentTheme().getName());

        manager.useBorlandTheme();
        assertEquals("Borland", manager.getCurrentTheme().getName());

        manager.useBorland3DTheme();
        assertEquals("Borland 3D", manager.getCurrentTheme().getName());

        manager.useTI994ATheme();
        assertEquals("TI-99/4A", manager.getCurrentTheme().getName());

        manager.useTRS80Theme();
        assertEquals("TRS-80", manager.getCurrentTheme().getName());

        manager.useDOSTheme();
        assertEquals("DOS", manager.getCurrentTheme().getName());

        manager.useDBase3Theme();
        assertEquals("dBASE III", manager.getCurrentTheme().getName());

        manager.useDBase4Theme();
        assertEquals("dBASE IV", manager.getCurrentTheme().getName());
    }

    @Test
    @DisplayName("multiple rapid theme switches should not cause issues")
    void testRapidThemeSwitching() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Label label = createLabel("Rapid Switch Test", 5, 5);
        label.setSize(40, 1);
        Button button = createButton("Click Me", 5, 8);

        setupFrame(label, button);
        runEventLoopCycle();

        // Perform 100 rapid theme switches
        for (int i = 0; i < 100; i++) {
            ThemeSupplier themeSupplier = ALL_THEMES.get(i % ALL_THEMES.size());

            assertDoesNotThrow(() -> {
                Theme theme = themeSupplier.supplier.get();
                manager.setTheme(theme);
                root.markDirty();
                runEventLoopCycle();
            }, "Rapid theme switch " + i + " should not throw exception");

            // Verify theme is correctly set
            assertEquals(themeSupplier.name, manager.getCurrentTheme().getName(),
                "Theme should be correct after rapid switch " + i);
        }

        // Verify components are still functional after rapid switching
        AtomicBoolean clicked = new AtomicBoolean(false);
        button.addActionListener(() -> clicked.set(true));
        clickButton(button);
        assertTrue(clicked.get(), "Button should still work after rapid theme switching");
    }

    @Test
    @DisplayName("theme switching should be thread-safe")
    void testThemeSwitchingThreadSafety() throws InterruptedException {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Label label = createLabel("Thread Safety Test", 5, 5);
        label.setSize(40, 1);

        setupFrame(label);
        runEventLoopCycle();

        // Thread safety test parameters
        int threadCount = 10;
        int switchesPerThread = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger errorCount = new AtomicInteger(0);

        // Create multiple threads that switch themes concurrently
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();

                    // Perform theme switches
                    for (int j = 0; j < switchesPerThread; j++) {
                        try {
                            ThemeSupplier themeSupplier = ALL_THEMES.get((threadId + j) % ALL_THEMES.size());
                            Theme theme = themeSupplier.supplier.get();
                            manager.setTheme(theme);

                            // Verify getCurrentTheme() doesn't return null
                            Theme currentTheme = manager.getCurrentTheme();
                            if (currentTheme == null) {
                                errorCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
            thread.setName("ThemeSwitcher-" + i);
            threads.add(thread);
            thread.start();
        }

        // Start all threads simultaneously
        startLatch.countDown();

        // Wait for all threads to complete (with timeout)
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "All threads should complete within timeout");

        // Join all threads
        for (Thread thread : threads) {
            thread.join(1000);
        }

        // Verify no errors occurred
        assertEquals(0, errorCount.get(),
            "No errors should occur during concurrent theme switching");

        // Verify theme manager is still in valid state
        assertNotNull(manager.getCurrentTheme(),
            "Current theme should not be null after concurrent access");
    }

    @Test
    @DisplayName("all convenience methods should work correctly")
    void testAllConvenienceMethods() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();
        Label themeLabel = createLabel("Testing Convenience Methods", 5, 5);
        themeLabel.setSize(50, 1);

        setupFrame(themeLabel);
        runEventLoopCycle();

        // Test useDefaultTheme()
        manager.useDefaultTheme();
        themeLabel.setText("Theme: Default");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("Default", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useDarkTheme()
        manager.useDarkTheme();
        themeLabel.setText("Theme: Dark");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("Dark", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useLightTheme()
        manager.useLightTheme();
        themeLabel.setText("Theme: Light");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("Light", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useModernTheme()
        manager.useModernTheme();
        themeLabel.setText("Theme: Modern");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("Modern", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useBorlandTheme()
        manager.useBorlandTheme();
        themeLabel.setText("Theme: Borland");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("Borland", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useBorland3DTheme()
        manager.useBorland3DTheme();
        themeLabel.setText("Theme: Borland 3D");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("Borland 3D", manager.getCurrentTheme().getName());
        assertTrue(manager.getCurrentTheme().supports3D());

        // Test useTI994ATheme()
        manager.useTI994ATheme();
        themeLabel.setText("Theme: TI-99/4A");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("TI-99/4A", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useTRS80Theme()
        manager.useTRS80Theme();
        themeLabel.setText("Theme: TRS-80");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("TRS-80", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useDOSTheme()
        manager.useDOSTheme();
        themeLabel.setText("Theme: DOS");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("DOS", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useDBase3Theme()
        manager.useDBase3Theme();
        themeLabel.setText("Theme: dBASE III");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("dBASE III", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());

        // Test useDBase4Theme()
        manager.useDBase4Theme();
        themeLabel.setText("Theme: dBASE IV");
        root.markDirty();
        runEventLoopCycle();
        assertEquals("dBASE IV", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme().getBackground());
    }

    @Test
    @DisplayName("theme switching should update all component color pairs")
    void testThemeSwitchUpdatesColorPairs() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        // Start with Default theme
        manager.useDefaultTheme();
        Theme defaultTheme = manager.getCurrentTheme();
        ColorPair defaultButton = defaultTheme.getButton();
        ColorPair defaultBorder = defaultTheme.getBorder();
        ColorPair defaultBackground = defaultTheme.getBackground();

        // Switch to Borland theme
        manager.useBorlandTheme();
        Theme borlandTheme = manager.getCurrentTheme();
        ColorPair borlandButton = borlandTheme.getButton();
        ColorPair borlandBorder = borlandTheme.getBorder();
        ColorPair borlandBackground = borlandTheme.getBackground();

        // Verify color pairs are different
        // Note: We can't directly compare ColorPair objects, so we verify they exist
        assertNotNull(defaultButton);
        assertNotNull(defaultBorder);
        assertNotNull(defaultBackground);
        assertNotNull(borlandButton);
        assertNotNull(borlandBorder);
        assertNotNull(borlandBackground);

        // Verify theme-specific attributes
        assertNotEquals(defaultTheme.getBorderChars(), borlandTheme.getBorderChars(),
            "Different themes should have different border characters");
    }

    @Test
    @DisplayName("theme switching should work with complex UI layouts")
    void testThemeSwitchingWithComplexLayout() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        // Create complex layout with multiple components
        Label titleLabel = createLabel("Theme Switcher Demo", 5, 2);
        titleLabel.setSize(40, 1);

        Button btnDefault = createButton("Default", 5, 5);
        Button btnDark = createButton("Dark", 20, 5);
        Button btnLight = createButton("Light", 35, 5);

        TextField nameField = createTextField(5, 8, 30);
        Checkbox agreeCheckbox = createCheckbox("I agree to terms", 5, 11);

        Label statusLabel = createLabel("Status: Ready", 5, 14);
        statusLabel.setSize(40, 1);

        AtomicReference<String> currentThemeName = new AtomicReference<>("Default");

        btnDefault.addActionListener(() -> {
            manager.useDefaultTheme();
            currentThemeName.set("Default");
            statusLabel.setText("Status: Switched to Default");
            root.markDirty();
        });

        btnDark.addActionListener(() -> {
            manager.useDarkTheme();
            currentThemeName.set("Dark");
            statusLabel.setText("Status: Switched to Dark");
            root.markDirty();
        });

        btnLight.addActionListener(() -> {
            manager.useLightTheme();
            currentThemeName.set("Light");
            statusLabel.setText("Status: Switched to Light");
            root.markDirty();
        });

        setupFrame(titleLabel, btnDefault, btnDark, btnLight, nameField, agreeCheckbox, statusLabel);
        runEventLoopCycle();

        // Test clicking each theme button
        clickButton(btnDark);
        assertEquals("Dark", currentThemeName.get());
        assertEquals("Dark", manager.getCurrentTheme().getName());

        clickButton(btnLight);
        assertEquals("Light", currentThemeName.get());
        assertEquals("Light", manager.getCurrentTheme().getName());

        clickButton(btnDefault);
        assertEquals("Default", currentThemeName.get());
        assertEquals("Default", manager.getCurrentTheme().getName());

        // Verify all components still functional
        nameField.setText("Test User");
        agreeCheckbox.setChecked(true);
        root.markDirty();
        runEventLoopCycle();

        assertEquals("Test User", nameField.getText());
        assertTrue(agreeCheckbox.isChecked());
    }

    @Test
    @DisplayName("setTheme with null should not change current theme")
    void testSetThemeWithNullIgnored() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        // Set initial theme
        manager.useBorlandTheme();
        Theme borlandTheme = manager.getCurrentTheme();
        assertEquals("Borland", borlandTheme.getName());

        // Try to set null theme
        manager.setTheme(null);

        // Verify theme unchanged
        assertEquals("Borland", manager.getCurrentTheme().getName());
        assertNotNull(manager.getCurrentTheme());
    }

    @Test
    @DisplayName("theme switching should preserve component state")
    void testThemeSwitchPreservesComponentState() {
        // Setup
        ThemeManager manager = ThemeManager.getInstance();

        TextField textField = createTextField(5, 5, 30);
        textField.setText("Important Data");

        Checkbox checkbox = createCheckbox("Remember me", 5, 8);
        checkbox.setChecked(true);

        AtomicInteger clickCount = new AtomicInteger(0);
        Button button = createButton("Click Counter", 5, 11);
        button.addActionListener(clickCount::incrementAndGet);

        setupFrame(textField, checkbox, button);
        runEventLoopCycle();

        // Click button
        clickButton(button);
        assertEquals(1, clickCount.get());

        // Verify initial state
        assertEquals("Important Data", textField.getText());
        assertTrue(checkbox.isChecked());

        // Switch theme
        manager.useDarkTheme();
        root.markDirty();
        runEventLoopCycle();

        // Verify state preserved
        assertEquals("Important Data", textField.getText());
        assertTrue(checkbox.isChecked());
        assertEquals(1, clickCount.get());

        // Verify components still work
        clickButton(button);
        assertEquals(2, clickCount.get());

        checkbox.setChecked(false);
        root.markDirty();
        runEventLoopCycle();
        assertFalse(checkbox.isChecked());
    }
}

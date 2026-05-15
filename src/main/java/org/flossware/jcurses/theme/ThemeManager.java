package org.flossware.jcurses.theme;

/**
 * Singleton manager for the current application theme.
 */
public class ThemeManager {
    private static final ThemeManager INSTANCE = new ThemeManager();
    private Theme currentTheme = new DefaultTheme();

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(Theme theme) {
        if (theme != null) {
            this.currentTheme = theme;
        }
    }

    // Convenience methods for switching themes
    public void useDefaultTheme() {
        setTheme(new DefaultTheme());
    }

    public void useDarkTheme() {
        setTheme(new DarkTheme());
    }

    public void useLightTheme() {
        setTheme(new LightTheme());
    }
}

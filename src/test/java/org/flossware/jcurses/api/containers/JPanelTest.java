package org.flossware.jcurses.api.containers;

import org.flossware.jcurses.api.FlowLayout;
import org.flossware.jcurses.api.JLabel;
import org.flossware.jcurses.api.JPanel;
import org.flossware.jcurses.testutil.ComponentTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JPanel Tests")
class JPanelTest extends ComponentTestBase {
    private JPanel panel;

    @BeforeEach
    void setUp() {
        panel = new JPanel();
    }

    @Test
    @DisplayName("should create panel")
    void testCreation() {
        assertNotNull(panel);
        assertFalse(panel.isBordered());
    }

    @Test
    @DisplayName("should create panel with layout manager")
    void testCreationWithLayout() {
        FlowLayout layout = new FlowLayout();
        JPanel panelWithLayout = new JPanel(layout);

        assertNotNull(panelWithLayout);
        // Layout is set but there's no getter, so we can verify by checking doLayout doesn't crash
        panelWithLayout.setSize(20, 10);
        assertDoesNotThrow(() -> panelWithLayout.doLayout());
    }

    @Test
    @DisplayName("should set and get bordered state")
    void testSetBordered() {
        panel.setBordered(true);
        assertTrue(panel.isBordered());

        panel.setBordered(false);
        assertFalse(panel.isBordered());
    }

    @Test
    @DisplayName("should trigger repaint when bordered state changes")
    void testRepaintOnBorderedChange() {
        root.add(panel);
        clearDirtyFlag();

        panel.setBordered(true);

        assertDirtyFlagSet();
    }

    @Test
    @DisplayName("should render border when bordered")
    void testBorderRendering() {
        panel.setLocation(5, 5);
        panel.setSize(10, 5);
        panel.setBordered(true);

        panel.paint(buffer);

        // Check corners
        assertEquals('+', buffer[5][5]);
        assertEquals('+', buffer[5][14]);
        assertEquals('+', buffer[9][5]);
        assertEquals('+', buffer[9][14]);
    }

    @Test
    @DisplayName("should not render border when not bordered")
    void testNoBorderRendering() {
        panel.setLocation(5, 5);
        panel.setSize(10, 5);
        panel.setBordered(false);

        panel.paint(buffer);

        // No border should be drawn
        assertEquals(' ', buffer[5][5]);
    }

    @Test
    @DisplayName("should act as container for children")
    void testChildManagement() {
        JLabel child = new JLabel("Test");
        panel.add(child);

        assertEquals(1, panel.getChildren().size());
        assertTrue(panel.getChildren().contains(child));
    }

    @Test
    @DisplayName("should paint children")
    void testPaintChildren() {
        JLabel child = new JLabel("Test");
        child.setLocation(10, 10);
        child.setSize(10, 1);

        panel.add(child);

        assertDoesNotThrow(() -> panel.paint(buffer));
    }
}

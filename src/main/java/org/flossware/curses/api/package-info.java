/**
 * Core API for building terminal user interfaces with AWT-like components.
 *
 * <p>This package provides the fundamental building blocks for creating
 * interactive terminal applications using ncurses. The API is modeled after
 * Java AWT to provide a familiar programming model.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.flossware.curses.api.Component} - Base class for all UI elements</li>
 *   <li>{@link org.flossware.curses.api.Container} - Components that can hold child components</li>
 *   <li>{@link org.flossware.curses.api.RootPane} - Singleton root of the component tree</li>
 * </ul>
 *
 * <h2>Widget Components</h2>
 * <ul>
 *   <li>{@link org.flossware.curses.api.JButton} - Clickable button</li>
 *   <li>{@link org.flossware.curses.api.JLabel} - Text label</li>
 *   <li>{@link org.flossware.curses.api.JTextField} - Single-line text input with editing</li>
 *   <li>{@link org.flossware.curses.api.JTable} - Tabular data with sorting and selection</li>
 *   <li>{@link org.flossware.curses.api.JCheckbox} - Checkbox with state</li>
 *   <li>{@link org.flossware.curses.api.JProgressBar} - Progress indicator</li>
 *   <li>...and 23 more widgets</li>
 * </ul>
 *
 * <h2>Container Components</h2>
 * <ul>
 *   <li>{@link org.flossware.curses.api.JPanel} - Generic container with optional borders</li>
 *   <li>{@link org.flossware.curses.api.JFrame} - Top-level window with title bar</li>
 *   <li>{@link org.flossware.curses.api.JDialog} - Modal or non-modal dialog window</li>
 *   <li>{@link org.flossware.curses.api.JScrollPane} - Scrollable viewport</li>
 * </ul>
 *
 * <h2>Layout Management</h2>
 * <ul>
 *   <li>{@link org.flossware.curses.api.LayoutManager} - Interface for layout strategies</li>
 *   <li>{@link org.flossware.curses.api.BorderLayout} - Five-region layout (North, South, East, West, Center)</li>
 *   <li>{@link org.flossware.curses.api.FlowLayout} - Left-to-right flow with wrapping</li>
 *   <li>{@link org.flossware.curses.api.JGridLayout} - Uniform grid layout</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>All components are thread-safe. State modifications are protected by
 * {@link java.util.concurrent.locks.ReentrantLock}, and the framework is
 * compatible with Java 21 Virtual Threads.
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Create a window with a button
 * Frame frame = new Frame("My App");
 * frame.setSize(40, 10);
 * frame.setLocation(10, 5);
 *
 * Button button = new Button("Click Me!");
 * button.setSize(15, 3);
 * button.setLocation(12, 3);
 * button.addActionListener(() -> {
 *     System.out.println("Button clicked!");
 * });
 *
 * frame.add(button);
 * frame.setVisible(true);
 * }</pre>
 *
 * @since 1.0
 * @see org.flossware.curses.events
 * @see org.flossware.curses.theme
 */
package org.flossware.curses.api;

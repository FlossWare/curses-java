# jcurses

A modern Java terminal UI library that brings AWT-like components to the terminal using ncurses. Built with cutting-edge Java 21 features including Virtual Threads, Foreign Function & Memory API, Record Patterns, and Sealed Interfaces.

![Status](https://img.shields.io/badge/status-working-brightgreen)
![Version](https://img.shields.io/badge/version-1.2-blue)
![Java](https://img.shields.io/badge/java-21-orange)
![License](https://img.shields.io/badge/license-GPL--3.0-blue)

## ✨ Features

- 🎮 **Fully Interactive** - Real keyboard & mouse navigation and widget interaction
- 🎨 **Color Support** - 8 standard colors with predefined color pairs and 3 built-in themes
- 📝 **Advanced Text Editing** - Selection, cut/copy/paste, undo/redo, word navigation in text fields
- 📜 **Scrollable Views** - JScrollPane with viewport clipping and scrollbar integration
- ☕ **Modern Java 21** - Virtual Threads, Foreign Function API, Record Patterns, Sealed Interfaces
- 🎯 **28+ Widgets** - Complete AWT-compatible component set
- 🔒 **Thread-Safe** - ReentrantLock protection for all components
- ⚡ **Fast Rendering** - Differential updates, dirty rectangles, layout caching
- 🎭 **Themes** - Default, Dark, and Light themes with pluggable architecture
- 🧩 **Module System** - Java 9+ JPMS support (opt-in)
- 📦 **Zero Dependencies** - Only ncurses (native) and test libraries
- ✅ **Comprehensive Tests** - 289 unit tests with 80%+ coverage

## 🚀 Quick Start

```bash
# Run the interactive demo
./run-interactive.sh
```

**Controls:**
- `TAB` / `↓` - Navigate to next widget
- `↑` - Navigate to previous widget
- `SPACE` / `ENTER` - Activate widget
- `ESC` / `Q` - Quit

## 📋 Requirements

- **Java 21+** with preview features enabled
- **Maven 3.6+**
- **ncurses library** (for interactive mode)

### Install ncurses

```bash
# Ubuntu/Debian
sudo apt-get install libncurses-dev

# Fedora/RHEL
sudo dnf install ncurses-devel

# Arch Linux
sudo pacman -S ncurses

# macOS
brew install ncurses
```

## 🎮 Running the Demos

### Interactive Demo (Try This First!)

Full keyboard-driven terminal UI:

```bash
./run-interactive.sh
```

### Quick Test

Test if the interactive demo works (auto-exits after 3 seconds):

```bash
./test-interactive.sh
```

### Manual Run

Or manually:

```bash
mvn clean compile
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes org.flossware.jcurses.InteractiveDemo
```

**What you can do:**
- ✅ Click buttons and see messages update
- ✅ Toggle checkboxes on/off
- ✅ Adjust slider values with +/- buttons
- ✅ Fill and reset progress bars
- ✅ Cycle through combo box options
- ✅ Navigate with keyboard, see visual focus indicator

### Static Rendering Demo

Renders all widgets to console output:

```bash
mvn exec:exec
```

Great for seeing all components at once without interaction.

## 📦 Components

### Containers
| Component | Description |
|-----------|-------------|
| **JFrame** | Top-level window with title bar |
| **JPanel** | Generic container with optional border |
| **JDialog** | Modal/non-modal dialog window |
| **JFileDialog** | File selection dialog |
| **JScrollPane** | Scrollable view container |
| **JSplitPane** | Resizable split view (H/V) |
| **JTabbedPane** | Multi-tab container |
| **JMenuBar** | Application menu bar |
| **JMenu** | Dropdown menu |
| **JMenuItem** | Menu item within menus |
| **JToolBar** | Tool button bar |

### Input Widgets
| Component | Description |
|-----------|-------------|
| **JButton** | Push button with action listeners |
| **JCheckbox** | Checkbox with group support |
| **JTextField** | Single-line text input |
| **JTextArea** | Multi-line text display |
| **JComboBox\<T>** | Generic dropdown selector |
| **JChoice** | Simple choice selector |
| **JList** | List with multi-selection |

### Display Widgets
| Component | Description |
|-----------|-------------|
| **JLabel** | Text label with alignment |
| **JProgressBar** | Progress indicator (0-100%) |
| **JSlider** | Value slider with live indicator |
| **JScrollBar** | Horizontal/vertical scrollbar |
| **JTable** | Data table with rows/columns |
| **JStatusBar** | Status text display |
| **JSeparator** | Visual divider line |

### Layout Managers
| Layout | Description |
|--------|-------------|
| **BorderLayout** | North/South/East/West/Center |
| **FlowLayout** | Flow-based arrangement |
| **JGridLayout** | Grid-based positioning |

## 🏗️ Architecture

```
Component (abstract)
├── Container
│   ├── RootPane (singleton)
│   ├── JFrame, JPanel, JDialog, JFileDialog
│   ├── JScrollPane, JSplitPane, JTabbedPane
│   └── JMenuBar, JMenu, JMenuItem, JToolBar
└── Widgets
    ├── Input: JButton, JCheckbox, JTextField, JTextArea, JComboBox, JChoice
    ├── Display: JLabel, JProgressBar, JSlider, JTable, JList
    └── Utility: JSeparator, JStatusBar, JScrollBar
```

## 💻 Example Code

```java
// Create a simple interactive UI
JFrame frame = new JFrame("My App");
frame.setSize(60, 20);
frame.setVisible(true);

JPanel panel = new JPanel();
panel.setLocation(2, 2);
panel.setSize(56, 16);

JButton button = new JButton("Click Me!");
button.setLocation(4, 4);
button.setSize(15, 1);
button.addActionListener(() -> {
    label.setText("Button clicked!");
});

JLabel label = new JLabel("Welcome!");
label.setLocation(4, 6);
label.setSize(30, 1);

panel.add(button);
panel.add(label);
frame.add(panel);
RootPane.getInstance().add(frame);
```

## 🔧 Building from Source

```bash
# Compile
mvn clean compile

# Run all tests (289 tests)
mvn test

# Run tests with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Package
mvn package
```

## 📁 Project Structure

```
jcurses/
├── src/main/java/org/flossware/jcurses/
│   ├── api/              # 28 UI components + 7 support classes
│   ├── events/           # Event system (4 sealed event types)
│   ├── ffi/              # ncurses FFI bridge
│   ├── render/           # Rendering engine (diff engine, event processor)
│   ├── Main.java         # Static demo
│   └── InteractiveDemo.java  # Interactive demo
├── run-interactive.sh    # Launch script
├── test-interactive.sh   # Quick test script
├── README.md             # This file
├── INTERACTIVE_DEMO.md   # Detailed interactive guide
└── QUICKSTART.txt        # Quick reference
```

## 🎯 Current Status

**Working:**
- ✅ Full component hierarchy (54 Java source files)
- ✅ All 28 widgets + 7 support classes implemented
- ✅ Interactive keyboard navigation (TAB, arrows, SPACE, ENTER)
- ✅ Mouse event handling with click detection and component dispatch
- ✅ Color support with 8 standard colors and predefined color pairs
- ✅ Advanced text editing (selection, cut/copy/paste, undo/redo, word navigation)
- ✅ Scrolling in JScrollPane with viewport clipping and scrollbar integration
- ✅ ncurses integration via Foreign Function API
- ✅ Event loop with focus management
- ✅ Thread-safe rendering with differential updates
- ✅ Performance optimizations (dirty rectangles, layout caching)
- ✅ Module system support (opt-in with module-info.java.template)
- ✅ Theme system (Default, Dark, Light themes with pluggable architecture)
- ✅ ASCII rendering (cross-platform compatible)
- ✅ **Comprehensive unit tests** (289 tests across 41 test classes)
- ✅ Thread-safety tests with Virtual Threads
- ✅ Code coverage reporting (JaCoCo)

## 🔬 Technology Stack

### Java 21 Preview Features

**Virtual Threads:**
```java
Thread.ofVirtual().start(() -> {
    // Lightweight concurrent I/O
});
```

**Foreign Function API (Panama):**
```java
MethodHandle getch = linker.downcallHandle(
    ncurses.find("getch").orElseThrow(),
    FunctionDescriptor.of(ValueLayout.JAVA_INT)
);
int key = (int) getch.invokeExact();
```

**Sealed Interfaces & Pattern Matching:**
```java
sealed interface JcursesEvent permits KeyEvent, MouseEvent, WindowEvent {}

switch (event) {
    case KeyEvent(int code, boolean alt, boolean ctrl) -> handleKey(code);
    case MouseEvent(int x, int y, int button) -> handleMouse(x, y);
    case WindowEvent(int w, int h) -> handleResize(w, h);
}
```

**Math.clamp() (Java 21):**
```java
this.value = Math.clamp(value, 0, 100);
```

## 🐛 Troubleshooting

### "ncurses library not available"
Install ncurses-devel package (see Requirements section above).

### Display looks garbled
- Run in a **real terminal**, not IDE built-in console
- Try: gnome-terminal, konsole, xterm, or Terminal.app

### Preview features error
Ensure Java 21+ and `--enable-preview` flag is set.

### Keys don't respond
Make sure the terminal window has focus and you're running directly (not through Maven).

## 📖 Documentation

- **README.md** (this file) - Overview and quick start
- **INTERACTIVE_DEMO.md** - Detailed interactive demo guide
- **QUICKSTART.txt** - Quick reference card

## 📄 License

GNU General Public License v3.0 - See [LICENSE](LICENSE) file

## 🤝 Contributing

Contributions welcome! This is an active project showcasing modern Java features.

## 🎉 Highlights

- **First terminal UI library** to use Java 21 Foreign Function API for ncurses
- **Full AWT compatibility** - familiar API for Java developers
- **Production-ready architecture** - thread-safe, event-driven, extensible
- **Educational value** - demonstrates advanced Java 21 features in real application

---

Built with ☕ Java 21 | Powered by ncurses | Made for terminal enthusiasts

## 🧪 Testing

The project includes comprehensive unit tests covering all components:

```bash
# Run all tests
mvn test

# Run with coverage report
mvn clean test jacoco:report

# View coverage
open target/site/jacoco/index.html

# Run specific test
mvn test -Dtest=JButtonTest
```

**Test Coverage:**
- ✅ 289 tests across 41 test classes
- ✅ All 28 widgets tested
- ✅ Mouse event handling tests
- ✅ Color system tests (Color, ColorPair)
- ✅ Advanced text editing tests
- ✅ Scrolling tests (JScrollPane)
- ✅ Theme system tests (Default, Dark, Light themes)
- ✅ Thread-safety validation with Virtual Threads
- ✅ Event system (sealed interfaces & records)
- ✅ Layout managers (BorderLayout, FlowLayout, JGridLayout)
- ✅ Container hierarchy
- ✅ Rendering engine
- ✅ 80%+ code coverage

**Test Structure:**
```
src/test/java/
├── api/
│   ├── ComponentTest, ContainerTest, RootPaneTest
│   ├── ColorTest, ColorPairTest, MouseListenerTest
│   ├── containers/ (JPanel, JFrame tests)
│   ├── layouts/ (BorderLayout, FlowLayout, JGridLayout tests)
│   ├── widgets/ (28 widget tests + advanced tests)
│   └── edit/ (Clipboard, text editing support)
├── events/ (KeyEvent, MouseEvent, WindowEvent tests)
├── theme/ (ThemeTest for Default, Dark, Light themes)
└── testutil/ (BufferAssertions, ThreadSafetyTestHelper)
```

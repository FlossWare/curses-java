# curses-java

A modern Java terminal UI library that brings AWT-like components to the terminal using ncurses. Built with cutting-edge Java 21 features including Virtual Threads, Foreign Function & Memory API, Record Patterns, and Sealed Interfaces.

![Status](https://img.shields.io/badge/status-working-brightgreen)
![Version](https://img.shields.io/badge/version-1.0-blue)
![Java](https://img.shields.io/badge/java-21-orange)
![License](https://img.shields.io/badge/license-Apache--2.0-blue)

## ✨ Features

- 🎮 **Fully Interactive** - Real keyboard & mouse navigation and widget interaction
- 🖱️ **Window Manipulation** - Drag to move windows, resize by dragging edges and corners
- 🎨 **Color Support** - 8 standard colors with predefined color pairs and 12 built-in themes
- 📝 **Advanced Text Editing** - Selection, cut/copy/paste, word navigation in text fields
- 📜 **Scrollable Views** - JScrollPane with viewport clipping and scrollbar integration
- ☕ **Modern Java 21** - Virtual Threads, Foreign Function API, Record Patterns, Sealed Interfaces
- 🎯 **28 Widgets** - Complete AWT-compatible component set
- 🔒 **Thread-Safe** - ReentrantLock protection for all components
- ⚡ **Fast Rendering** - Differential updates, dirty rectangles, layout caching
- 🎭 **Themes** - 12 built-in themes (modern, retro computing, and classic IDE styles) with pluggable architecture
- 🧩 **Module System** - Java 9+ JPMS support (opt-in via `module-info.java.template`)
- 📦 **Minimal Dependencies** - SLF4J logging, Gson for JSON themes, plus ncurses (native)
- ✅ **Comprehensive Tests** - 1820 tests (1683 unit + 137 integration) with 80%+ coverage

## 🚀 Quick Start

**Linux/macOS:**
```bash
./run-interactive.sh
```

**Windows (Command Prompt):**
```cmd
run-interactive.bat
```

**Windows (PowerShell):**
```powershell
.\run-interactive.ps1
```

**Controls:**
- `TAB` / `↓` - Navigate to next widget
- `↑` - Navigate to previous widget
- `SPACE` / `ENTER` - Activate widget
- `MOUSE` - Click widgets, drag title bars to move windows, drag edges/corners to resize
- `ESC` / `Q` - Quit


## 📸 Screenshots

Visual examples of all themes with ASCII art demonstrations:
- **[Theme Screenshots Gallery](docs/screenshots/README.md)** - See all 12 themes in action
- **Borland 3D vs. dBASE IV 3D** - Side-by-side comparison of 3D rendering
- **Retro Themes** - Classic DOS, dBASE III, TI-99/4A, TRS-80
- **Modern Themes** - Dark, Light, Contemporary interfaces

Quick preview:

```
╔════ Borland 3D ═════╗╌╌      ╔═══ dBASE IV 3D ══╗╌╌
║ YELLOW on BLUE      ║╌╌      ║ WHITE on BLUE    ║╌╌
║ Developer aesthetic ║╌╌      ║ Business style   ║╌╌
╚═════════════════════╝╌╌      ╚══════════════════╝╌╌
 ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌         ╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌
 L-shaped drop shadows          L-shaped shadows
```

## 📋 Requirements

- **Java 21+** with preview features enabled
- **Maven 3.6+**
- **ncurses library** (for interactive mode)

> **⚠️ Important for Library Consumers:**  
> This library uses Java 21 **preview features** and requires `--enable-preview` at both **compile time** and **runtime**.  
> Your application must also be compiled and run with `--enable-preview`:
> ```bash
> javac --enable-preview --release 21 YourApp.java
> java --enable-preview YourApp
> ```
> 
> **Maven users** - Add to your `pom.xml`:
> ```xml
> <plugin>
>   <groupId>org.apache.maven.plugins</groupId>
>   <artifactId>maven-compiler-plugin</artifactId>
>   <configuration>
>     <compilerArgs>
>       <arg>--enable-preview</arg>
>     </compilerArgs>
>   </configuration>
> </plugin>
> ```
> 
> **Preview features used:**
> - **Foreign Function & Memory API** (FFI) - Direct ncurses binding without JNI  
>   _(Note: FFI graduated to stable in Java 22, but this library targets Java 21)_
> - **Pattern Matching enhancements** - Advanced switch expressions

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

**Linux/macOS:**
```bash
./run-interactive.sh
```

**Windows (Command Prompt):**
```cmd
run-interactive.bat
```

**Windows (PowerShell):**
```powershell
.\run-interactive.ps1
```

### Quick Test

Test if the interactive demo works (auto-exits after 3 seconds):

**Linux/macOS:**
```bash
./test-interactive.sh
```

**Windows (Command Prompt):**
```cmd
test-interactive.bat
```

**Windows (PowerShell):**
```powershell
.\test-interactive.ps1
```

### Manual Run

Or manually:

```bash
mvn clean compile
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes org.flossware.curses.InteractiveDemo
```

**What you can do:**
- ✅ Click buttons and see messages update
- ✅ Toggle checkboxes on/off
- ✅ Adjust slider values with +/- buttons
- ✅ Fill and reset progress bars
- ✅ Cycle through combo box options
- ✅ Navigate with keyboard, see visual focus indicator
- ✅ Drag window title bars to move windows
- ✅ Drag window edges to resize width/height
- ✅ Drag window corners to resize both dimensions
- ✅ Toggle draggable/resizable flags to control window behavior

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
| **JFrame** | Top-level window with title bar, draggable and resizable with mouse |
| **JPanel** | Generic container with optional border |
| **JDialog** | Modal/non-modal dialog window with title and status bar support |
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
| **JIndeterminateProgress** | Animated progress indicator for unknown duration |
| **JSlider** | Value slider with live indicator |
| **JScrollBar** | Horizontal/vertical scrollbar |
| **JTable** | Data table with column sorting and multi-row selection |
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

// Windows are draggable and resizable by default
// Drag the title bar to move, drag edges/corners to resize
frame.setDraggable(true);   // Enable/disable drag-to-move
frame.setResizable(true);   // Enable/disable resize
frame.setMinWidth(20);      // Set minimum size constraints
frame.setMinHeight(10);

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

## 🎨 Themes

The library includes **12 built-in themes** providing modern, retro, and classic IDE color schemes:

| Theme Name | Description | Example Use Case |
|------------|-------------|------------------|
| **Default** | Standard terminal colors | General purpose |
| **Dark** | Dark mode with muted colors | Low-light environments |
| **Light** | Bright background theme | High-contrast viewing |
| **Modern** | Contemporary color palette | Modern applications |
| **Borland** | Classic Borland IDE blue | Turbo Pascal/C++ nostalgia |
| **DOS** | MS-DOS style colors | Retro computing aesthetic |
| **DBase3** | dBASE III Plus colors | Classic database development |
| **DBase4** | dBASE IV colors | Vintage business applications |
| **TI994A** | Texas Instruments TI-99/4A | 1980s home computer style |
| **TRS80** | Tandy TRS-80 green screen | Retro monochrome look |

### Switching Themes

Use the `ThemeManager` singleton to change themes at runtime:

```java
import org.flossware.curses.theme.ThemeManager;

// Use a pre-defined theme via convenience methods
ThemeManager.getInstance().useDarkTheme();
ThemeManager.getInstance().useBorlandTheme();
ThemeManager.getInstance().useTI994ATheme();

// Or set a custom theme
ThemeManager.getInstance().setTheme(new CustomTheme());
```

### Available Theme Methods

```java
ThemeManager.getInstance().useDefaultTheme();  // Standard terminal
ThemeManager.getInstance().useDarkTheme();     // Dark mode
ThemeManager.getInstance().useLightTheme();    // Light mode
ThemeManager.getInstance().useModernTheme();   // Contemporary
ThemeManager.getInstance().useBorlandTheme();  // Borland IDE
ThemeManager.getInstance().useDOSTheme();      // MS-DOS
ThemeManager.getInstance().useDBase3Theme();   // dBASE III
ThemeManager.getInstance().useDBase4Theme();   // dBASE IV
ThemeManager.getInstance().useTI994ATheme();   // TI-99/4A
ThemeManager.getInstance().useTRS80Theme();    // TRS-80
```

For detailed theme documentation, color palettes, and creating custom themes, see **[themes.md](docs/themes.md)**.

## 🔧 Building from Source

```bash
# Compile
mvn clean compile

# Run unit tests (1683 tests)
mvn test

# Run integration tests (137 tests)
mvn jacoco:prepare-agent failsafe:integration-test

# Run all tests (1820 tests)
mvn clean test jacoco:prepare-agent failsafe:integration-test failsafe:verify

# Generate coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Package
mvn package
```

## 📁 Project Structure

```
curses-java/
├── src/main/java/org/flossware/curses/
│   ├── api/              # 28 UI components + support classes
│   ├── events/           # Event system (4 sealed event types)
│   ├── ffi/              # ncurses FFI bridge
│   ├── render/           # Rendering engine (diff engine, event processor)
│   ├── Main.java         # Static demo
│   └── InteractiveDemo.java  # Interactive demo
├── src/test/java/org/flossware/curses/
│   ├── integration/      # 137 integration tests
│   ├── testutil/         # Test utilities (MockNcursesBridge)
│   └── api/              # 1683 unit tests
├── run-interactive.sh    # Launch script
├── test-interactive.sh   # Quick test script
├── README.md             # This file
└── QUICKSTART.txt        # Quick reference
```

## 🎯 Current Status

**Working:**
- ✅ Full component hierarchy (75 Java source files)
- ✅ All 28 widgets + support classes implemented
- ✅ Interactive keyboard navigation (TAB, arrows, SPACE, ENTER)
- ✅ Mouse event handling with click detection and component dispatch
- ✅ Window drag-to-move and resize operations (title bar, edges, corners)
- ✅ Color support with 8 standard colors, predefined color pairs, and 12 built-in themes
- ✅ Advanced text editing (selection, cut/copy/paste, word navigation)
- ✅ Scrolling in JScrollPane with viewport clipping and scrollbar integration
- ✅ ncurses integration via Foreign Function API
- ✅ Event loop with focus management
- ✅ Thread-safe rendering with differential updates
- ✅ Performance optimizations (dirty rectangles, layout caching)
- ✅ Module system support (opt-in with module-info.java.template)
- ✅ Theme system (12 built-in themes with pluggable architecture - see [themes.md](docs/themes.md))
- ✅ ASCII rendering (cross-platform compatible)
- ✅ **Comprehensive test suite** (1820 tests: 1683 unit + 137 integration)
- ✅ **Automated integration tests** (headless UI testing without terminal)
- ✅ Thread-safety tests with Virtual Threads
- ✅ Performance benchmarks (render < 100ms for 50 components)
- ✅ Code coverage reporting (JaCoCo - 80%+ instruction coverage)

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
sealed interface CursesEvent permits KeyEvent, MouseEvent, WindowEvent {}

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

- **[README.md](README.md)** (this file) - Overview and quick start
- **[themes.md](docs/themes.md)** - Theme system guide and color palettes
- **[testing.md](docs/testing.md)** - Testing guide (unit and integration)
- **[examples.md](docs/examples.md)** - Example applications
- **[QUICKSTART.txt](QUICKSTART.txt)** - Quick reference card

## Related Projects

- **[curses-themes](https://github.com/FlossWare/curses-themes)** -- Python curses theme library that can load curses-java JSON themes directly via auto-detection. See [Migration Guide](docs/MIGRATION.md).

## 📄 License

Apache License 2.0 - See [LICENSE](LICENSE) file

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

The project includes comprehensive test coverage with both unit and integration tests:

```bash
# Run unit tests (1683 tests)
mvn test

# Run integration tests (137 tests - headless, no terminal needed)
mvn jacoco:prepare-agent failsafe:integration-test

# Run all tests (1820 total)
mvn clean test jacoco:prepare-agent failsafe:integration-test failsafe:verify

# Generate coverage report
mvn clean test jacoco:report

# View coverage report (80%+ instruction coverage)
open target/site/jacoco/index.html

# Run specific test class
mvn test -Dtest=ButtonTest
mvn jacoco:prepare-agent failsafe:integration-test -Dit.test=ButtonInteractionIT
```

**Test Coverage:**
- ✅ **1683 unit tests** - Component logic, rendering, thread safety
- ✅ **137 integration tests** - End-to-end UI interactions (buttons, keyboard, mouse, tables, forms)
- ✅ **Mock-based testing** - No terminal required, runs in CI/CD
- ✅ **Performance benchmarks** - Render timing, event loop efficiency
- ✅ **80%+ instruction coverage** - JaCoCo verified

**Integration Tests:**
- ButtonInteractionIT - 8 tests (click handlers, forms, progress bars)
- KeyboardNavigationIT - 9 tests (SPACE, ENTER, ESC, TAB navigation)
- TableInteractionIT - 9 tests (sorting, selection, add/delete)
- PerformanceIT - 7 tests (render < 100ms, event loop < 50ms)

See **[testing.md](docs/testing.md)** for detailed integration testing guide.
- ✅ All 28 widgets tested
- ✅ Mouse event handling tests
- ✅ Window drag/resize tests (WindowDragManager, JFrame integration)
- ✅ Color system tests (Color, ColorPair)
- ✅ Advanced text editing tests
- ✅ Scrolling tests (JScrollPane)
- ✅ Theme system tests (all 12 themes validated)
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

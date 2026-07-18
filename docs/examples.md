# curses-java Examples and Demos

A practical guide to learning curses-java through examples, interactive demos, and component galleries.

---

## 📋 Table of Contents

1. [Running the Interactive Demo](#running-the-interactive-demo)
2. [Example Applications](#example-applications)
3. [Component Gallery](#component-gallery)
4. [Building Your First UI](#building-your-first-ui)

---

## Running the Interactive Demo

The interactive demo is a **fully functional terminal UI application** where you can experiment with all curses-java components in real-time.

### Quick Start

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

### What You Can Do

- ✅ Navigate between widgets using keyboard
- ✅ Click buttons and widgets with the mouse
- ✅ Drag windows by their title bar to move them
- ✅ Resize windows by dragging edges and corners
- ✅ Toggle checkboxes on/off
- ✅ Adjust slider values
- ✅ Fill progress bars
- ✅ Cycle through dropdown options
- ✅ Sort table columns by clicking headers
- ✅ Select multiple table rows
- ✅ Browse files with file dialog
- ✅ See real-time visual feedback

### Demo Screenshot

```
┌─────────────────────────────────────────────────────────────┐
│ Interactive curses-java Demo - Press TAB to navigate...          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│   Welcome to Interactive curses-java!                            │
│   Use TAB to move between widgets, SPACE/ENTER to activate  │
│                                                               │
│  >[ Click Me! ]<   [ Press Me! ]   [ Toggle Below ]          │
│                                                               │
│   [ ] Enable feature 1                                       │
│   [ ] Enable feature 2                                       │
│   [ ] Enable feature 3                                       │
│                                                               │
│   [──────────────────●─────────────]  50   [+]  [-]          │
│                                                               │
│   ███████████████████░░░░░░░░░░░░░░░                         │
│   [ Fill Progress ]  [ Reset Progress ]                      │
│                                                               │
│   [ Option 1 ▼ ]  [ Next ]                                   │
│                                                               │
└─────────────────────────────────────────────────────────────┘
 Ready | TAB=Navigate | SPACE/ENTER=Activate | ESC=Quit
```

The `>` and `<` brackets show which widget currently has focus.

### Interactive Features

#### Window Manipulation
- **Drag title bar**: Click and drag the top edge of any window to move it around
- **Drag edges**: Click and drag the left, right, top, or bottom edge to resize
- **Drag corners**: Click and drag any corner to resize both width and height
- **Toggle Draggable**: In the demo window, disable/enable drag-to-move functionality
- **Toggle Resizable**: In the demo window, disable/enable resize functionality

#### Widget Interactions

**Buttons**
- **Click Me! / Press Me!**: Updates the welcome message
- **Toggle Below**: Toggles all three checkboxes at once
- **+/-**: Adjust the slider value
- **Fill Progress**: Increases progress bar by 10%
- **Reset Progress**: Resets progress bar to 0%
- **Next**: Cycles to the next combo box option

**Checkboxes**
- Press `SPACE` or `ENTER` to toggle checked/unchecked
- Watch the `[X]` appear when checked

**Slider**
- Use the +/- buttons to adjust
- See the `●` indicator move
- Current value displayed on the right

**Progress Bar**
- Use "Fill Progress" to increment
- Watch the `█` blocks fill up
- Use "Reset Progress" to start over

**Combo Box**
- Use "Next" button to cycle through options
- Current selection shown with `▼` indicator

**Table Demo**
- **Show Table Demo**: Opens a window with a sortable, selectable table
- **Column Sorting**: Click column headers to sort (ascending → descending → unsorted)
  - `^` indicates ascending sort
  - `v` indicates descending sort
- **Multi-Row Selection**: Click rows to select/deselect
  - `[*]` indicates selected row
  - `[ ]` indicates unselected row
- **Multiple columns**: Name, Age, City, Role, Status

**File Dialog**
- **Open File Dialog**: Browse and select files from the filesystem
- Shows current directory path
- Lists directories with `[D]` prefix
- Lists files with `[F]` prefix

**Indeterminate Progress**
- **Start/Stop**: Control animated progress indicator
- Shows ongoing activity for operations of unknown duration
- Animated bouncing block moves left and right

**Dialog Windows**
- **Show Dialog**: Opens modal dialog with status bar
- **Update Status**: Changes status bar text
- Demonstrates title bar and status bar features

### Alternative Running Methods

#### Automated Test

Test if the interactive demo works without manual interaction:

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

This script automatically runs the demo for 3 seconds then exits. Great for verifying the setup works.

#### Manual Run

```bash
# Compile first
mvn clean compile

# Run the interactive demo
java --enable-preview \
     --enable-native-access=ALL-UNNAMED \
     -cp target/classes \
     org.flossware.curses.InteractiveDemo
```

#### From IDE

If you're using an IDE (IntelliJ, Eclipse, VS Code):

1. Set the main class to: `org.flossware.curses.InteractiveDemo`
2. Add VM options:
   ```
   --enable-preview --enable-native-access=ALL-UNNAMED
   ```
3. Run in a terminal (not the IDE's built-in terminal)

### Troubleshooting

#### "ncurses library is not available"

Install the ncurses development library:

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

#### Display looks garbled

Make sure you're running in a real terminal, not:
- IDE built-in terminal
- Maven output window
- Non-terminal environment

Try running from:
- gnome-terminal
- konsole
- xterm
- iTerm2 (macOS)
- Terminal.app (macOS)

#### Preview features error

Make sure you're using Java 21 or later:

```bash
java --version
```

#### Nothing happens when I press keys

Make sure:
1. The terminal window has focus
2. You're not running through Maven (use the script or java directly)
3. Your terminal supports ncurses

### Technical Details

The interactive demo uses:

- **NcursesBridge**: FFI calls to ncurses for terminal control
- **Event Loop**: Reads keyboard input in real-time
- **Focus Management**: Tracks which widget is active
- **Differential Rendering**: Only redraws changed areas
- **Virtual Threads**: Non-blocking I/O handling

### Extending the Demo

To add your own widgets:

1. Create the widget instance
2. Add it to the panel
3. Add to `focusableComponents` list (if it should receive focus)
4. Set up action listeners
5. Call `markDirty()` when state changes

Example:

```java
JButton myButton = new JButton("My Button");
myButton.setLocation(10, 10);
myButton.setSize(15, 1);
myButton.addActionListener(() -> {
    // Your action here
    markDirty(); // Trigger re-render
});
focusableComponents.add(myButton);
panel.add(myButton);
```

---

## Example Applications

### Todo List Manager

**Full-featured task management application with professional UI.**

#### Screenshot
```
┌──────────────────────────────────────────────────────────────────────────────┐
│File--EditToViewist Manager v1.0 - TAB to navigate, ENTER to add task│       │
│                                                                              │
│┌────────────────────────────────────────────────────────────────────────────┐│
││                                                                            ││
││                                  MY TASKS                                  ││
││                                                                            ││
││ Add New Task:  >[                                                ]< [ Add ]││
││                                                                            ││
││                                                                            ││
││ ID             Task           Priority	Status                        ││
││ ------------------------------------------------------------               ││
││ [ ] 001            Review pull re High           Active                    ││
││ [ ] 002            Write document Medium         Active                    ││
││ [ ] 003            Fix login bug  High           Active                    ││
││ [ ] 004            Update depende Low            Done                      ││
││ [ ] 005            Deploy to prod High           Active                    ││
││                                                                            ││
││                                                                            ││
││                                                                            ││
││                                                                            ││
││ [ Mark Complete ]  [ Delete ]  [ Clear All ]   ##########................. ││
││                                                                            ││
│└────────────────────────────────────────────────────────────────────────────┘│
│Ready | 5 tasks | TAB=navigate, ENTER=add, ESC=quit                           │
└──────────────────────────────────────────────────────────────────────────────┘
```

#### Features
- ✅ **Task Management**: Add, delete, and complete tasks
- ✅ **Priority Levels**: High, Medium, Low
- ✅ **Interactive Table**: Click rows to select (multi-selection)
- ✅ **Menu System**: File, Edit, View menus
- ✅ **Progress Tracking**: Visual completion bar
- ✅ **Keyboard Navigation**: Full keyboard support

#### Components Demonstrated
- `MenuBar`, `Menu`, `MenuItem` - Application menu system
- `Table` - Multi-column sortable table with checkboxes
- `TextField` - Text input for new tasks
- `Button` - Action buttons (Add, Delete, Complete)
- `ProgressBar` - Overall completion indicator
- `StatusBar` - Real-time task count
- `Panel` - Organized layout container
- `Label` - Section headers

#### Code Example
```java
// Create interactive task table
taskTable = new Table();
taskTable.setColumnNames("ID", "Task", "Priority", "Status");
taskTable.setMultiSelectionEnabled(true);

// Add task from text input
Button addBtn = new Button("Add");
addBtn.addActionListener(() -> {
    String task = newTaskField.getText().trim();
    if (!task.isEmpty()) {
        taskTable.addRow(
            String.format("%03d", taskCounter++),
            task,
            "Medium",
            "Active"
        );
    }
});

// Handle task completion
Button completeBtn = new Button("Mark Complete");
completeBtn.addActionListener(() -> {
    var selected = taskTable.getSelectedRows();
    // Mark selected tasks as complete
});
```

#### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/TodoListApp.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.TodoListApp
```

---

### System Monitor Dashboard

**Real-time system performance monitoring with live-updating metrics.**

#### Screenshot
```
┌──────────────────────────────────────────────────────────────────────────────┐
│         System Monitor Dashboard - Press ESC to quit                        │
│                                                                              │
│┌────────────────────────────────────────────────────────────────────────────┐│
││                                                                            ││
││                         SYSTEM PERFORMANCE MONITOR                         ││
││ ────────────────────────────────────────────────────────────────────────── ││
││                                                                            ││
││ CPU Usage:                                                            45%  ││
││ #################################......................................... ││
││                                                                            ││
││ Memory:                                                               68%  ││
││ ##################################################........................ ││
││                                                                            ││
││ Disk I/O:                                                             23%  ││
││ #######################............................................... ││
││                                                                            ││
││ Network:                                                              89%  ││
││ #################################################################......... ││
││                                                                            ││
││ ────────────────────────────────────────────────────────────────────────── ││
││                                                                            ││
││ Uptime: 3 days, 5 hours, 23 minutes       Processes: 247 running           ││
││                                                                            ││
││ ###===..........................................                           ││
│└────────────────────────────────────────────────────────────────────────────┘│
│Refreshing every 2 seconds | Last update: 1780768762321                       │
└──────────────────────────────────────────────────────────────────────────────┘
```

#### Features
- 📊 **Live Metrics**: CPU, Memory, Disk, Network usage
- 🔄 **Auto-Refresh**: Updates every 2 seconds
- 📈 **Visual Progress Bars**: Clear percentage displays
- ⏱️ **System Info**: Uptime, process count
- 🔄 **Loading Animation**: Indeterminate progress indicator
- 💾 **Resource Efficient**: Smooth updates without flicker

#### Components Demonstrated
- `ProgressBar` (×4) - Individual metric displays
- `Label` - Metric titles and values
- `Separator` - Visual section dividers
- `IndeterminateProgress` - Animated loading bar
- `StatusBar` - Last update timestamp
- `Frame`, `Panel` - Clean layout structure

#### Code Example
```java
// Create progress bars for each metric
cpuBar = new ProgressBar();
cpuBar.setPercent(0.45);  // 45% CPU

memBar = new ProgressBar();
memBar.setPercent(0.68);  // 68% Memory

// Labels show percentage values
cpuLabel = new Label("45%");
cpuLabel.setAlignment(Label.ALIGN_RIGHT);

// Animated loading indicator
loadingBar = new IndeterminateProgress();
loadingBar.start();

// Update metrics in event loop
private void updateMetrics() {
    double newCpu = simulateMetric(cpuBar.getPercent());
    cpuBar.setPercent(newCpu);
    cpuLabel.setText(String.format("%2.0f%%", newCpu * 100));
}
```

#### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/SystemDashboard.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.SystemDashboard
```

---

### Simple Interactive Demo

**Basic introduction to interactive widgets.**

#### Screenshot
```
┌──────────────────────────────────────────────────────────────────────────────┐
│      Simple Demo - TAB=navigate, SPACE=activate, ESC=quit                   │
│                                                                              │
│┌────────────────────────────────────────────────────────────────────────────┐│
││                                                                            ││
││ Interactive Demo - Click the buttons!                                      ││
││                                                                            ││
││                                                                            ││
││>[ Click Me! ]< [ Press Me! ]                                               ││
││                                                                            ││
││                                                                            ││
││ [X] Enable feature                                                         ││
││                                                                            ││
││                                                                            ││
││ [--------------O--------------]  50                                        ││
││                                                                            ││
│└────────────────────────────────────────────────────────────────────────────┘│
│Ready | Terminal: 80x24                                                       │
└──────────────────────────────────────────────────────────────────────────────┘
```

#### Features
- 2 interactive buttons with click events
- Checkbox with toggle functionality
- Slider with value display
- Focus indicators (> <)
- Status bar with terminal info

#### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/SimpleDemo.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.SimpleDemo
```

---

### All Widgets Showcase

**Comprehensive demo of all 28+ UI components.**

#### Screenshot
```
┌──────────────────────────────────────────────────────────────────────────────┐
│File  EditWIDGETS TEST - Page 1/3 - TAB/SPACE=test, n=next page, ESC=quit│   │
│                                                                              │
│┌────────────────────────────────────────────────────────────────────────────┐│
││                                                                            ││
││              Testing 28 UI Components - Page 1: Input Widgets              ││
││                                                                            ││
││ 1-4. Button (enabled, disabled, click te5-7. Checkbox (toggle test):       ││
││>[ Click Me ]< [ Press ]   ( Disabled )  [ ] Option A  [X] Option B         ││
││                                                                            ││
││ 8. ComboBox:                  9. Choice:                                   ││
││ [ Option 1 v ]      [ Next ]  [ Red v ]                                    ││
││                                                                            ││
││ 10. Slider (range 0-100):             11. ProgressBar:                     ││
││ [------------O-----------]  50  [ + ] ################.........  [ +10% ]  ││
││                                                                            ││
││ 12. TextField:             13. IndeterminateProgress:                      ││
││ [Edit me                ]                        [ Start ] [ Stop ]        ││
││                                                                            ││
││ 14. Separator:                                                             ││
││ ──────────────────────────────────────────────────────────────────────── ││
││ 15-16. ScrollBar (H/V):                                                    ││
││ ............##...........  .                                               ││
│└────────────────────────────#───────────────────────────────────────────────┘│
│Terminal: 80x25 | Testing: 16 widgets on this page                            │
└──────────────────────────────────────────────────────────────────────────────┘
```

#### Widgets Shown
1. Button (enabled, disabled)
2. Checkbox (checked, unchecked)
3. ComboBox (dropdown)
4. Choice (selection)
5. Slider (with controls)
6. ProgressBar (percentage)
7. TextField (text input)
8. IndeterminateProgress (animated)
9. Separator (divider)
10. ScrollBar (horizontal & vertical)
11. Label (titles)
12. Panel (container)
13. Frame (window)
14. StatusBar (bottom info)
15. MenuBar (top menus)

#### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/AllWidgetsTest.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.AllWidgetsTest
```

---

### Table Component Test

**Comprehensive table functionality demonstration.**

#### Screenshot
```
┌──────────────────────────────────────────────────────────────────────────────┐
│      Table Component Test - TAB=next, SPACE=action, ESC=quit                │
│                                                                              │
│┌────────────────────────────────────────────────────────────────────────────┐│
││                                                                            ││
││ Table Test: Click column headers to sort, click rows to select             ││
││                                                                            ││
││ ID             Name           Priority	Status         Assignee       ││
││ ──────────────────────────────────────────────────────────────────────     ││
││ [ ] 001            Fix login bug  High           Open           Alice      ││
││ [ ] 002            Add dark mode  Medium         In Progress    Bob        ││
││ [ ] 003            Update docs    Low            Done           Charlie    ││
││ [ ] 004            Refactor API   High           Open           Alice      ││
││ [ ] 005            Write tests    Medium         In Progress    Diana      ││
││ [ ] 006            Deploy prod    High           Blocked        Eve        ││
││ [ ] 007            Fix CSS        Low            Done           Bob        ││
││ [ ] 008            Code review    Medium         Open           Frank      ││
││                                                                            ││
││                                                                            ││
││                                                                            ││
││>[ Sort by ID ]< [ Sort by Name ]  [ Sort Priority ] [ Add Row ]            ││
││                                                                            ││
│└─[ Clear All ]──[ Select Row 0 ][ Clear Selection ]─[ Get Selected ]────────┘│
│Rows: 8 | Cols: 5 | Multi-select: true                                        │
└──────────────────────────────────────────────────────────────────────────────┘
```

#### Table Features Tested
✅ **Column Sorting**: Click headers to sort (ID↑, Name↑, Priority↑)  
✅ **Row Selection**: Checkbox in each row  
✅ **Multi-Selection**: Select multiple rows  
✅ **Dynamic Rows**: Add new rows on-the-fly  
✅ **Row Operations**: Delete selected, clear all  
✅ **Selection Info**: Get count of selected rows  
✅ **8 Rows × 5 Columns**: Real data display  

#### Code Example
```java
// Create table
Table table = new Table();
table.setColumnNames("ID", "Name", "Priority", "Status", "Assignee");
table.setMultiSelectionEnabled(true);

// Add rows
table.addRow("001", "Fix login bug", "High", "Open", "Alice");
table.addRow("002", "Add dark mode", "Medium", "In Progress", "Bob");

// Sort by column
Button sortBtn = new Button("Sort by ID");
sortBtn.addActionListener(() -> {
    table.sortByColumn(0);  // Sort by first column
});

// Get selected rows
Button getBtn = new Button("Get Selected");
getBtn.addActionListener(() -> {
    var selected = table.getSelectedRows();  // Returns Set<Integer>
    int count = selected.size();
});
```

#### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/TableTest.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.TableTest
```

---

## Component Gallery

### Input Components

| Component | Description | Example |
|-----------|-------------|---------|
| **Button** | Clickable button | `[ Click Me ]` |
| **Checkbox** | Toggle checkbox | `[X] Option` or `[ ] Option` |
| **ComboBox** | Dropdown selection | `[ Option 1 v ]` |
| **Choice** | Simple dropdown | `[ Red v ]` |
| **Slider** | Value slider | `[-------O-------] 50` |
| **TextField** | Text input | `[Edit text here___]` |

### Display Components

| Component | Description | Visual |
|-----------|-------------|--------|
| **Label** | Text display | `Label text here` |
| **ProgressBar** | Progress indicator | `#########.........` (45%) |
| **IndeterminateProgress** | Loading animation | `###===...........` |
| **StatusBar** | Bottom status | `Ready \| 5 items` |
| **Separator** | Divider line | `───────────────` |
| **ScrollBar** | Scroll indicator | `....##.....` or `#` (vertical) |

### Container Components

| Component | Description |
|-----------|-------------|
| **Frame** | Top-level window with title bar |
| **Panel** | Container panel (can be bordered) |
| **Dialog** | Modal dialog window |
| **RootPane** | Root container for all components |
| **ScrollPane** | Scrollable viewport |

### Complex Components

| Component | Description | Use Case |
|-----------|-------------|----------|
| **Table** | Multi-column table | Data grids, task lists |
| **TextArea** | Multi-line text | Logs, descriptions |
| **ListComponent** | Item list | File lists, options |
| **TabbedPane** | Tabbed interface | Multi-page UIs |
| **SplitPane** | Split view | Two-panel layouts |

### Menu Components

| Component | Description |
|-----------|-------------|
| **MenuBar** | Top menu bar |
| **Menu** | Menu with items |
| **MenuItem** | Individual menu entry |
| **ToolBar** | Button toolbar |

---

## Building Your First UI

### Minimal Example

```java
import org.flossware.curses.api.*;
import org.flossware.curses.ffi.NcursesBridge;

public class MyFirstUI {
    public static void main(String[] args) throws Throwable {
        // Initialize ncurses
        NcursesBridge.init();
        
        // Get terminal size
        int height = NcursesBridge.getTerminalHeight();
        int width = NcursesBridge.getTerminalWidth();
        
        // Create UI
        RootPane root = RootPane.getInstance();
        root.setSize(width, height);
        
        Frame frame = new Frame("My First App");
        frame.setSize(width, height);
        frame.setVisible(true);
        
        Panel panel = new Panel();
        panel.setLocation(2, 2);
        panel.setSize(width - 4, height - 4);
        panel.setBordered(true);
        
        Label label = new Label("Hello, ncurses-java!");
        label.setLocation(4, 4);
        label.setAlignment(Label.ALIGN_CENTER);
        
        Button button = new Button("Click Me");
        button.setLocation(4, 6);
        button.addActionListener(() -> {
            label.setText("Button clicked!");
            root.markDirty();
        });
        
        panel.add(label);
        panel.add(button);
        frame.add(panel);
        root.add(frame);
        
        // Event loop
        boolean running = true;
        char[][] buffer = new char[height][width];
        
        while (running) {
            if (root.isDirty()) {
                // Clear buffer
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        buffer[i][j] = ' ';
                    }
                }
                
                // Paint
                root.paint(buffer);
                
                // Render to terminal
                NcursesBridge.clear();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (y == height - 1 && x == width - 1) continue;
                        NcursesBridge.moveCursor(y, x, buffer[y][x]);
                    }
                }
                NcursesBridge.refresh();
                
                root.clearDirty();
            }
            
            // Handle input
            int ch = NcursesBridge.getChar();
            if (ch == 27) { // ESC
                running = false;
            }
            
            Thread.sleep(10);
        }
        
        NcursesBridge.stop();
    }
}
```

### Running Your Code

```bash
# Compile
javac --enable-preview --release 21 -cp target/classes MyFirstUI.java

# Run
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:. MyFirstUI
```

### More Examples

Find more examples in the `examples/` directory:
- `TodoListApp.java` - Task management
- `SystemDashboard.java` - System monitoring
- `SimpleDemo.java` - Basic widgets
- `AllWidgetsTest.java` - Complete showcase
- `TableTest.java` - Table features
- `AdvancedComponentTest.java` - Complex widgets

---

## Next Steps

- Try modifying the widget positions
- Add new buttons with custom actions
- Create your own interactive application
- Explore the component API in `src/main/java/org/flossware/curses/api/`
- Check out the theme system in `docs/themes.md`
- Review test results in `COMPLETE_UI_TEST_RESULTS.md`

---

Enjoy building terminal UIs with curses-java!

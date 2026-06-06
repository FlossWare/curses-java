# ncurses-java Examples

Professional example applications demonstrating the ncurses-java library.

## Table of Contents

1. [Todo List Manager](#1-todo-list-manager) - Task management application
2. [System Monitor Dashboard](#2-system-monitor-dashboard) - Real-time system monitoring
3. [Simple Demo](#3-simple-demo) - Basic interactive widgets
4. [Comprehensive UI Test](#4-comprehensive-ui-test) - All widget showcase
5. [Advanced Components](#5-advanced-components) - Complex widgets demo
6. [Table Test](#6-table-test) - Table component features

---

## 1. Todo List Manager

**A professional task management application with a clean UI.**

### Features
- ✅ Add/delete tasks
- ✅ Mark tasks as complete
- ✅ Priority levels (High/Medium/Low)
- ✅ Task table with multi-selection
- ✅ Menu bar (File, Edit, View)
- ✅ Progress indicator
- ✅ Status bar with task count

### Components Used
- `MenuBar`, `Menu`, `MenuItem`
- `Table` (multi-selection enabled)
- `TextField` (task input)
- `Button` (actions)
- `ProgressBar` (completion tracking)
- `StatusBar` (info display)
- `Frame`, `Panel`

### Screenshot
```
File--EditToViewist Manager v1.0 - TAB to navigate, ENTER to add task ]--------+
|                                                                              |
|+----------------------------------------------------------------------------+|
||                                                                            ||
||                                  MY TASKS                                  ||
||                                                                            ||
|| Add New Task:  >[                                                ]< [ Add ]||
||                                                                            ||
||                                                                            ||
|| ID             Task           Priority	Status                        ||
|| ------------------------------------------------------------               ||
|| [ ] 001            Review pull re High           Active                    ||
|| [ ] 002            Write document Medium         Active                    ||
|| [ ] 003            Fix login bug  High           Active                    ||
|| [ ] 004            Update depende Low            Done                      ||
|| [ ] 005            Deploy to prod High           Active                    ||
||                                                                            ||
||                                                                            ||
||                                                                            ||
||                                                                            ||
|| [ Mark Complete ]  [ Delete ]  [ Clear All ]   ##########................. ||
||                                                                            ||
|+----------------------------------------------------------------------------+|
|Ready | 5 tasks | TAB=navigate, ENTER=add, ESC=quit                           |
+------------------------------------------------------------------------------
```

### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/TodoListApp.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.TodoListApp
```

### Code Highlights
```java
// Create table with multi-selection
taskTable = new Table();
taskTable.setColumnNames("ID", "Task", "Priority", "Status");
taskTable.setMultiSelectionEnabled(true);

// Add task from text field
String task = newTaskField.getText().trim();
taskTable.addRow(
    String.format("%03d", taskCounter++),
    task,
    "Medium",
    "Active"
);

// Handle completion
var selected = taskTable.getSelectedRows();
for (int row : selected) {
    // Mark as complete
}
```

---

## 2. System Monitor Dashboard

**Real-time system performance monitoring with live updates.**

### Features
- 📊 CPU/Memory/Disk/Network usage graphs
- 🔄 Auto-refreshing metrics (every 2 seconds)
- 📈 Progress bars for each metric
- ⏱️ System uptime display
- 🔄 Animated loading indicator
- 💻 Process count

### Components Used
- `ProgressBar` (4 instances for metrics)
- `Label` (metric values and titles)
- `Separator` (visual dividers)
- `IndeterminateProgress` (loading animation)
- `StatusBar` (refresh time)
- `Panel`, `Frame`

### Screenshot
```
+---------------[ System Monitor Dashboard - Press ESC to quit ]---------------+
|                                                                              |
|+----------------------------------------------------------------------------+|
||                                                                            ||
||                         SYSTEM PERFORMANCE MONITOR                         ||
|| -------------------------------------------------------------------------- ||
||                                                                            ||
|| CPU Usage:                                                            45%  ||
|| #################################......................................... ||
||                                                                            ||
|| Memory:                                                               68%  ||
|| ##################################################........................ ||
||                                                                            ||
|| Disk I/O:                                                             23%  ||
|| #######################............................................... ||
||                                                                            ||
|| Network:                                                              89%  ||
|| #################################################################......... ||
||                                                                            ||
|| -------------------------------------------------------------------------- ||
||                                                                            ||
|| Uptime: 3 days, 5 hours, 23 minutes       Processes: 247 running           ||
||                                                                            ||
|| ###===..........................................                           ||
|+----------------------------------------------------------------------------+|
|Refreshing every 2 seconds | Last update: 1780768762321                       |
+------------------------------------------------------------------------------
```

### How to Run
```bash
javac --enable-preview --release 21 -cp target/classes -d examples examples/SystemDashboard.java

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.SystemDashboard
```

### Code Highlights
```java
// Create progress bars for each metric
cpuBar = new ProgressBar();
cpuBar.setPercent(0.45);  // 45% CPU usage

memBar = new ProgressBar();
memBar.setPercent(0.68);  // 68% memory

// Animated loading indicator
loadingBar = new IndeterminateProgress();
loadingBar.start();

// Update metrics periodically
private static void updateMetrics() {
    double cpu = Math.min(0.99, cpuBar.getPercent() + (Math.random() - 0.5) * 0.1);
    cpuBar.setPercent(Math.max(0.1, cpu));
    cpuLabel.setText(String.format("%2.0f%%", cpuBar.getPercent() * 100));
}
```

---

## 3. Simple Demo

**Basic interactive demo showing fundamental widgets.**

### Features
- 2 clickable buttons
- Checkbox toggle
- Slider with +/- buttons
- All widgets respond to keyboard

### Screenshot
```
+-----------[ Simple Demo - TAB=navigate, SPACE=activate, ESC=quit ]-----------+
|                                                                              |
|+----------------------------------------------------------------------------+|
||                                                                            ||
|| Interactive Demo - Click the buttons!                                      ||
||                                                                            ||
||                                                                            ||
||>[ Click Me! ]< [ Press Me! ]                                               ||
||                                                                            ||
||                                                                            ||
|| [X] Enable feature                                                         ||
||                                                                            ||
||                                                                            ||
|| [--------------O--------------]  50                                        ||
||                                                                            ||
|+----------------------------------------------------------------------------+|
|Ready | Terminal: 80x24                                                       |
+------------------------------------------------------------------------------
```

### How to Run
```bash
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:examples examples.SimpleDemo
```

---

## 4. Comprehensive UI Test

**Complete showcase of all basic input widgets.**

### Features Demonstrated
- Buttons (enabled/disabled states)
- Checkboxes (toggle, pre-checked)
- ComboBox (dropdown selection)
- Choice widget
- Slider (with increment buttons)
- ProgressBar (with fill button)
- TextField
- IndeterminateProgress (start/stop)
- Separators
- ScrollBars (horizontal & vertical)

### Screenshot
```
File[ EditWIDGETS TEST - Page 1/3 - TAB/SPACE=test, n=next page, ESC=quit ]----+
|                                                                              |
|+----------------------------------------------------------------------------+|
||                                                                            ||
||              Testing 28 UI Components - Page 1: Input Widgets              ||
||                                                                            ||
|| 1-4. Button (enabled, disabled, click te5-7. Checkbox (toggle test):       ||
||>[ Click Me ]< [ Press ]   ( Disabled )  [ ] Option A  [X] Option B         ||
||                                                                            ||
|| 8. ComboBox:                  9. Choice:                                   ||
|| [ Option 1 v ]      [ Next ]  [ Red v ]                                    ||
||                                                                            ||
|| 10. Slider (range 0-100):             11. ProgressBar:                     ||
|| [------------O-----------]  50  [ + ] ################.........  [ +10% ]  ||
||                                                                            ||
|| 12. TextField:             13. IndeterminateProgress:                      ||
|| [Edit me                ]                        [ Start ] [ Stop ]        ||
||                                                                            ||
|| 14. Separator:                                                             ||
|| ------------------------------------------------------------------------   ||
|| 15-16. ScrollBar (H/V):                                                    ||
|| ............##...........  .                                               ||
|+----------------------------#-----------------------------------------------+|
|Terminal: 80x25 | Testing: 16 widgets on this page                            |
+------------------------------------------------------------------------------
```

---

## 5. Advanced Components

**Demonstrates complex widgets like Table, TextArea, TabbedPane.**

### Features
- Table with 3 rows × 5 columns
- Sortable table columns
- TextArea with append capability
- TabbedPane with tab switching
- MenuBar with File/Edit menus
- Vertical ScrollBar

### Screenshot
```
File--Edit Advanced Components Test - TAB=next, SPACE=test, ESC=quit ]---------+
| Tab 2 Content                                                                |
|+----------------------------------------------------------------------------+|
||                                                                            ||
|| Table Component:                          TextArea:                        ||
|| ID             Name           Statu  [ SorLine 1: Testing                  ||
|| -----------------------------------       Line 2: TextArea                 ||
||   1              Task A         Don       Line 3: Component                ||
||   2              Task B         Act       New line added!                  ||
||   3              Task C         Pen                                        ||
||                                                                            ||
||                                                                            ||
|| List Component:                           [ Add Line ]                     ||
||                                                                            ||
||                                           ------------------------------   ||
||  First  [Second]                     [ Switch Tab ]                      . ||
||                                                                          . ||
|+--------------------------------------------------------------------------#-+|
|Terminal: 80x24 | Advanced components loaded                                  |
+------------------------------------------------------------------------------
```

---

## 6. Table Test

**Comprehensive table component testing.**

### Features Tested
- ✅ 8 rows × 5 columns
- ✅ Column sorting (ID, Name, Priority)
- ✅ Row selection with checkboxes
- ✅ Multi-selection enabled
- ✅ Add new rows dynamically
- ✅ Delete selected rows
- ✅ Clear all rows
- ✅ Get selected row count

### Screenshot
```
+---------[ Table Component Test - TAB=next, SPACE=action, ESC=quit ]----------+
|                                                                              |
|+----------------------------------------------------------------------------+|
||                                                                            ||
|| Table Test: Click column headers to sort, click rows to select             ||
||                                                                            ||
|| ID             Name           Priority	Status         Assignee       ||
|| ----------------------------------------------------------------------     ||
|| [ ] 001            Fix login bug  High           Open           Alice      ||
|| [ ] 002            Add dark mode  Medium         In Progress    Bob        ||
|| [ ] 003            Update docs    Low            Done           Charlie    ||
|| [ ] 004            Refactor API   High           Open           Alice      ||
|| [ ] 005            Write tests    Medium         In Progress    Diana      ||
|| [ ] 006            Deploy prod    High           Blocked        Eve        ||
|| [ ] 007            Fix CSS        Low            Done           Bob        ||
|| [ ] 008            Code review    Medium         Open           Frank      ||
||                                                                            ||
||                                                                            ||
||                                                                            ||
||>[ Sort by ID ]< [ Sort by Name ]  [ Sort Priority ] [ Add Row ]            ||
||                                                                            ||
|+-[ Clear All ]--[ Select Row 0 ][ Clear Selection ]-[ Get Selected ]--------+|
|Rows: 8 | Cols: 5 | Multi-select: true                                        |
+------------------------------------------------------------------------------
```

---

## Quick Start

### Prerequisites
```bash
# Install ncurses development library
# Ubuntu/Debian:
sudo apt-get install libncurses-dev

# Fedora/RHEL:
sudo dnf install ncurses-devel

# macOS:
brew install ncurses
```

### Build the Library
```bash
mvn clean compile
```

### Run Any Example
```bash
# Template:
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:/path/to/slf4j-api.jar:/path/to/slf4j-simple.jar:examples \
  examples.<ClassName>

# Specific examples:
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:~/.m2/repository/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar:~/.m2/repository/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar:examples \
  examples.TodoListApp

java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp target/classes:~/.m2/repository/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar:~/.m2/repository/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar:examples \
  examples.SystemDashboard
```

---

## Common Keyboard Controls

| Key | Action |
|-----|--------|
| **TAB** | Navigate between focusable components |
| **SPACE** | Activate focused component (click button, toggle checkbox) |
| **ENTER** | Activate focused component or submit form |
| **ESC** or **q** | Quit application |
| **n** | Next test phase (in test apps) |

---

## Component Reference

### Input Components
- `Button` - Clickable button with label
- `Checkbox` - Toggle checkbox with label
- `ComboBox<T>` - Dropdown selection
- `Choice` - Single selection dropdown
- `Slider` - Value selection (min/max range)
- `TextField` - Single-line text input

### Display Components
- `Label` - Text label (with alignment)
- `ProgressBar` - Progress indicator (0-100%)
- `IndeterminateProgress` - Animated loading indicator
- `StatusBar` - Bottom status bar
- `Separator` - Horizontal/vertical divider
- `ScrollBar` - Scrollbar indicator

### Container Components
- `Frame` - Top-level window with title
- `Panel` - Container panel (can be bordered)
- `Dialog` - Modal dialog window
- `RootPane` - Root container
- `ScrollPane` - Scrollable viewport

### Complex Components
- `Table` - Multi-column table with sorting
- `TextArea` - Multi-line text display
- `ListComponent` - List of items
- `TabbedPane` - Tabbed interface
- `SplitPane` - Split view container

### Menu Components
- `MenuBar` - Top menu bar
- `Menu` - Menu with items
- `MenuItem` - Individual menu item

### Layout Managers
- `BorderLayout` - North/South/East/West/Center
- `GridLayout` - Row/column grid
- `FlowLayout` - Sequential flow

---

## Tips for Building Your Own UI

1. **Start with RootPane**
   ```java
   RootPane root = RootPane.getInstance();
   root.setSize(width, height);
   ```

2. **Create a Frame**
   ```java
   Frame frame = new Frame("My App Title");
   frame.setSize(width, height);
   frame.setVisible(true);
   ```

3. **Add a Panel for Layout**
   ```java
   Panel panel = new Panel();
   panel.setBordered(true);
   panel.setLocation(x, y);
   panel.setSize(width, height);
   ```

4. **Position Components**
   ```java
   Button btn = new Button("Click");
   btn.setLocation(x, y);
   btn.setSize(width, height);
   btn.addActionListener(() -> {
       // Handle click
   });
   ```

5. **Render Loop**
   ```java
   while (running) {
       if (root.isDirty()) {
           render();  // Paint to buffer, send to ncurses
           root.clearDirty();
       }
       
       int ch = NcursesBridge.getChar();
       handleKey(ch);
       
       Thread.sleep(10);
   }
   ```

---

## License

GPL v3.0 - See LICENSE file for details.

## Contributing

Contributions welcome! See CODE_OF_CONDUCT.md for guidelines.

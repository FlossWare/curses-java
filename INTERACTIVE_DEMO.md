# Interactive JCurses Demo Guide

## Quick Start

The fastest way to run the interactive demo:

```bash
./run-interactive.sh
```

## What is the Interactive Demo?

The interactive demo is a **fully functional terminal UI application** where you can:

- ✅ Navigate between widgets using keyboard
- ✅ Click buttons and widgets with the mouse
- ✅ Drag windows by their title bar to move them
- ✅ Resize windows by dragging edges and corners
- ✅ Toggle checkboxes on/off
- ✅ Adjust slider values
- ✅ Fill progress bars
- ✅ Cycle through dropdown options
- ✅ See real-time visual feedback

## Running the Demo

### Method 1: Using the Script (Easiest)

```bash
./run-interactive.sh
```

### Method 2: Quick Test (Automated)

Test if the interactive demo works without manual interaction:

```bash
./test-interactive.sh
```

This script automatically runs the demo for 3 seconds then exits. Great for verifying the setup works before diving into the full interactive experience.

### Method 3: Manual Run

```bash
# Compile first
mvn clean compile

# Run the interactive demo
java --enable-preview \
     --enable-native-access=ALL-UNNAMED \
     -cp target/classes \
     org.flossware.jcurses.InteractiveDemo
```

### Method 4: From IDE

If you're using an IDE (IntelliJ, Eclipse, VS Code):

1. Set the main class to: `org.flossware.jcurses.InteractiveDemo`
2. Add VM options:
   ```
   --enable-preview --enable-native-access=ALL-UNNAMED
   ```
3. Run in a terminal (not the IDE's built-in terminal)

## Controls

### Keyboard

| Key | Action |
|-----|--------|
| `TAB` or `↓` | Move focus to next widget |
| `↑` | Move focus to previous widget |
| `SPACE` or `ENTER` | Activate the focused widget |
| `ESC` or `Q` | Quit the application |

### Mouse

| Action | Result |
|--------|--------|
| **Click** on button/widget | Activate the widget |
| **Drag** window title bar | Move the window |
| **Drag** window edge | Resize width or height |
| **Drag** window corner | Resize both dimensions |

## What You'll See

When you run the demo, you'll see a terminal UI with:

```
┌─────────────────────────────────────────────────────────────┐
│ Interactive JCurses Demo - Press TAB to navigate...          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│   Welcome to Interactive JCurses!                            │
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

## Interactive Features

### Window Manipulation
- **Drag title bar**: Click and drag the top edge of any window to move it around
- **Drag edges**: Click and drag the left, right, top, or bottom edge to resize
- **Drag corners**: Click and drag any corner to resize both width and height
- **Toggle Draggable**: In the demo window, disable/enable drag-to-move functionality
- **Toggle Resizable**: In the demo window, disable/enable resize functionality

### Buttons
- **Click Me! / Press Me!**: Updates the welcome message
- **Toggle Below**: Toggles all three checkboxes at once
- **+/-**: Adjust the slider value
- **Fill Progress**: Increases progress bar by 10%
- **Reset Progress**: Resets progress bar to 0%
- **Next**: Cycles to the next combo box option

### Checkboxes
- Press `SPACE` or `ENTER` to toggle checked/unchecked
- Watch the `[X]` appear when checked

### Slider
- Use the +/- buttons to adjust
- See the `●` indicator move
- Current value displayed on the right

### Progress Bar
- Use "Fill Progress" to increment
- Watch the `█` blocks fill up
- Use "Reset Progress" to start over

### Combo Box
- Use "Next" button to cycle through options
- Current selection shown with `▼` indicator

## Troubleshooting

### "ncurses library is not available"

Install the ncurses development library:

```bash
# Ubuntu/Debian
sudo apt-get install libncurses-dev

# Fedora/RHEL  
sudo dnf install ncurses-devel

# Arch Linux
sudo pacman -S ncurses
```

### Display looks garbled

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

### Preview features error

Make sure you're using Java 21 or later:

```bash
java --version
```

And that `--enable-preview` is in your JVM arguments.

### Nothing happens when I press keys

Make sure:
1. The terminal window has focus
2. You're not running through Maven (use the script or java directly)
3. Your terminal supports ncurses

## Technical Details

The interactive demo uses:

- **NcursesBridge**: FFI calls to ncurses for terminal control
- **Event Loop**: Reads keyboard input in real-time
- **Focus Management**: Tracks which widget is active
- **Differential Rendering**: Only redraws changed areas
- **Virtual Threads**: Non-blocking I/O handling

## Extending the Demo

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

## Next Steps

- Try modifying the widget positions
- Add new buttons with custom actions
- Create your own interactive application
- Explore the component API in `src/main/java/org/flossware/jcurses/api/`

---

Enjoy building terminal UIs with JCurses! 🚀

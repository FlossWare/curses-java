#!/bin/bash

# Interactive curses-java Demo Runner
# This script runs the interactive demo with proper JVM flags

echo "Starting curses-java Interactive Demo..."
echo "======================================"
echo ""
echo "Controls:"
echo "  TAB / Arrow Down  - Move to next widget"
echo "  Arrow Up          - Move to previous widget"
echo "  SPACE / ENTER     - Activate focused widget"
echo "  ESC / Q           - Quit"
echo ""
echo "Press any key to continue..."
read -n 1 -s

# Compile if needed
if [ ! -d "target/classes" ]; then
    echo "Compiling project..."
    mvn clean compile
fi

# Run with ncurses in terminal
java --enable-preview \
     --enable-native-access=ALL-UNNAMED \
     -cp target/classes \
     org.flossware.curses.InteractiveDemo

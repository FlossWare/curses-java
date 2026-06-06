@echo off
REM Interactive JCurses Demo Runner for Windows
REM This script runs the interactive demo with proper JVM flags

echo Starting JCurses Interactive Demo...
echo ======================================
echo.
echo Controls:
echo   TAB / Arrow Down  - Move to next widget
echo   Arrow Up          - Move to previous widget
echo   SPACE / ENTER     - Activate focused widget
echo   ESC / Q           - Quit
echo.
echo Press any key to continue...
pause >nul

REM Compile if needed
if not exist "target\classes" (
    echo Compiling project...
    call mvn clean compile
)

REM Run with ncurses in terminal
java --enable-preview --enable-native-access=ALL-UNNAMED -cp target\classes org.flossware.curses.InteractiveDemo

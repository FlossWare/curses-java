@echo off
REM Test Interactive Demo for Windows
REM Runs the demo for 3 seconds then exits

echo Testing interactive demo...

REM Create a temporary input file with tab, tab, tab, ESC
echo 	  > temp_input.txt

REM Run the demo with timeout (Windows doesn't have built-in timeout command like Linux)
REM This will run for a few seconds - press ESC or Q to exit manually
timeout /t 1 /nobreak >nul
type temp_input.txt | java --enable-preview --enable-native-access=ALL-UNNAMED -cp target\classes org.flossware.jcurses.InteractiveDemo 2>&1

del temp_input.txt >nul 2>&1

echo.
echo Test completed. If you saw a UI above, it's working!

# Interactive curses-java Demo Runner for Windows (PowerShell)
# This script runs the interactive demo with proper JVM flags

Write-Host "Starting curses-java Interactive Demo..." -ForegroundColor Green
Write-Host "======================================"
Write-Host ""
Write-Host "Controls:"
Write-Host "  TAB / Arrow Down  - Move to next widget"
Write-Host "  Arrow Up          - Move to previous widget"
Write-Host "  SPACE / ENTER     - Activate focused widget"
Write-Host "  ESC / Q           - Quit"
Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Compile if needed
if (-not (Test-Path "target\classes")) {
    Write-Host "Compiling project..." -ForegroundColor Yellow
    mvn clean compile
}

# Build classpath with Maven dependencies
Write-Host "Building classpath..." -ForegroundColor Yellow
$classpath = (mvn dependency:build-classpath -DincludeScope=runtime -q -Dmdep.outputFile=cp.txt)
$dependencies = Get-Content cp.txt -Raw
$fullClasspath = "target\classes;$dependencies"

# Run with ncurses in terminal
java --enable-preview --enable-native-access=ALL-UNNAMED -cp $fullClasspath org.flossware.curses.InteractiveDemo

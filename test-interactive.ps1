# Test Interactive Demo for Windows (PowerShell)
# Runs the demo briefly to verify it works

Write-Host "Testing interactive demo..." -ForegroundColor Cyan

# Run the demo with a timeout
$job = Start-Job -ScriptBlock {
    java --enable-preview --enable-native-access=ALL-UNNAMED -cp target\classes org.flossware.curses.InteractiveDemo 2>&1
}

# Wait 3 seconds
Start-Sleep -Seconds 3

# Stop the job
Stop-Job -Job $job
Receive-Job -Job $job
Remove-Job -Job $job

Write-Host ""
Write-Host "Test completed. If you saw a UI above, it's working!" -ForegroundColor Green

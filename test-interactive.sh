#!/bin/bash
echo "Testing interactive demo..."
timeout 3 bash -c "printf '\t\t\t\033' | java --enable-preview --enable-native-access=ALL-UNNAMED -cp target/classes org.flossware.jcurses.InteractiveDemo 2>&1" || true
echo ""
echo "Test completed. If you saw a UI above, it's working!"

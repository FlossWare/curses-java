# Frequently Asked Questions (FAQ)

## General Questions

### What is jcurses?
jcurses is a modern Java terminal UI library that brings AWT-like components to the terminal using ncurses. It leverages cutting-edge Java 21 features like Virtual Threads, Foreign Function & Memory API, and Pattern Matching.

### Why Java 21?
jcurses requires Java 21 for the Foreign Function & Memory API (Project Panama), which allows native ncurses integration without JNI or C code compilation.

### Is jcurses production-ready?
Yes! jcurses v1.28 is production-ready with:
- 399 comprehensive tests (100% passing)
- 80%+ code coverage
- Thread-safe architecture
- Active security scanning
- Comprehensive documentation

### What platforms are supported?
- **Linux**: Fully supported (tested on Ubuntu, Fedora, Arch)
- **macOS**: Fully supported
- **Windows**: Not currently supported (ncurses unavailable), use WSL

---

## Installation & Setup

### How do I install jcurses?

**Maven:**
```xml
<dependency>
    <groupId>org.flossware</groupId>
    <artifactId>jcurses</artifactId>
    <version>1.28</version>
</dependency>
```

**From source:**
```bash
git clone https://github.com/FlossWare/jcurses.git
cd jcurses
mvn clean install
```

### What are the system requirements?
- Java 21+ with preview features enabled
- Maven 3.8+
- ncurses library (Linux/macOS)

### How do I enable preview features?
```bash
java --enable-preview --enable-native-access=ALL-UNNAMED -jar yourapp.jar
```

Or in Maven:
```xml
<compilerArgs>
    <arg>--enable-preview</arg>
</compilerArgs>
```

---

## Development

### How do I run the interactive demo?
```bash
./run-interactive.sh  # Linux/macOS
run-interactive.bat   # Windows CMD
```

### How do I run tests?
```bash
mvn test                    # All tests
mvn test -Dtest=JButtonTest # Specific test
mvn clean test jacoco:report # With coverage
```

### How do I contribute?
See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### What's the versioning strategy?
jcurses uses automatic continuous versioning - every push to `main` increments the version. See [CONTRIBUTING.md#versioning-strategy](CONTRIBUTING.md#versioning-strategy) for details.

---

## Technical Questions

### Why Foreign Function API instead of JNI?
- No C/C++ compilation needed
- Better memory safety with Arena
- Cleaner Java-only codebase
- Better performance
- See [ADR-0001](docs/adr/0001-use-foreign-function-api.md) for full rationale

### Are components thread-safe?
Yes! All components use `ReentrantLock` for thread safety. See [ADR-0003](docs/adr/0003-thread-safety-with-reentrant-lock.md).

### How does the event loop work?
jcurses uses Virtual Threads for the event loop with:
- Non-blocking ncurses input
- Mouse event processing
- Focus management
- Render queue

### Can I customize themes?
Yes! jcurses includes Default, Dark, and Light themes. You can also create custom themes by implementing the Theme interface.

### How do I handle Unicode/emoji?
Currently limited to ASCII. Unicode/emoji support is planned for v2.0 (see ROADMAP.md).

---

## Performance

### What's the rendering performance?
jcurses uses:
- Differential rendering (only changed cells)
- Dirty rectangle tracking
- Layout caching
- Optimized buffer operations

Typical performance: 60+ FPS for most UIs.

### How do I benchmark my application?
Use JMH benchmarks:
```bash
mvn test -Pbenchmarks
```

### Is there a memory leak?
No known memory leaks. jcurses v1.26+ uses `Arena.ofAuto()` to prevent memory leaks from native allocations.

---

## Common Issues

### "ncurses library not available"
**Solution:** Install ncurses development libraries
```bash
# Ubuntu/Debian
sudo apt-get install libncurses-dev

# Fedora/RHEL
sudo dnf install ncurses-devel

# macOS
brew install ncurses
```

### Display looks garbled
**Causes:**
1. Running in IDE built-in console (not a real terminal)
2. Terminal doesn't support required features

**Solution:** Run in a real terminal (gnome-terminal, konsole, Terminal.app)

### Keys don't respond
**Causes:**
1. Terminal window doesn't have focus
2. Running through Maven exec (buffering issues)

**Solution:** Run directly with java command or use run-interactive.sh

### Preview features error
**Solution:** Add `--enable-preview` flag:
```bash
java --enable-preview YourApp
```

### Tests fail with "Arena confined"
**Solution:** Ensure tests don't share Arena instances between threads.

---

## Security

### How do I report a security vulnerability?
See [SECURITY.md](SECURITY.md) for our vulnerability disclosure policy.

### Is input sanitized?
Yes! JTextField and other input components sanitize input to prevent terminal escape sequence injection.

### Are dependencies scanned?
Yes! We use:
- Dependabot for automated updates
- OWASP Dependency Check
- CodeQL security scanning

---

## Roadmap & Future

### When will Windows be supported?
Windows support (via PDCurses) is planned for v2.0 (2026 Q3).

### When will Unicode/emoji be supported?
Unicode/emoji support is planned for v2.0 (2026 Q3).

### Can I use jcurses commercially?
Yes! jcurses is licensed under GPL-3.0. For commercial use, ensure compliance with GPL-3.0 terms.

### Where can I get help?
- GitHub Discussions: https://github.com/FlossWare/jcurses/discussions
- Issues: https://github.com/FlossWare/jcurses/issues
- Documentation: README.md, QUICKSTART.txt, INTERACTIVE_DEMO.md

---

**Still have questions? Open a [GitHub Discussion](https://github.com/FlossWare/jcurses/discussions)!**

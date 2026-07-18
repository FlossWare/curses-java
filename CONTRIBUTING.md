# Contributing to jcurses

Thank you for your interest in contributing to jcurses! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Pull Request Process](#pull-request-process)
- [Code Style](#code-style)
- [Testing](#testing)
- [Documentation](#documentation)

## Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help others learn and grow
- Follow the project's technical standards

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone git@github.com:YOUR_USERNAME/jcurses.git
   cd jcurses
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream git@github.com:FlossWare/jcurses.git
   ```

## Development Setup

### Requirements

- **Java 21+** with preview features enabled
- **Maven 3.8+**
- **ncurses library** (Linux/macOS) or **PDCurses** (Windows)

### Platform-Specific Setup

**Linux (Fedora/RHEL/CentOS):**
```bash
sudo dnf install ncurses-devel
```

**Linux (Debian/Ubuntu):**
```bash
sudo apt-get install libncurses5-dev
```

**macOS:**
```bash
brew install ncurses
```

**Windows:**
- Install PDCurses or use WSL with Linux ncurses

### Build and Test

```bash
# Build the project
mvn clean install

# Run tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Run interactive demo
./run-interactive.sh  # Linux/macOS
run-interactive.bat   # Windows CMD
```

## How to Contribute

### Types of Contributions

- **Bug Fixes**: Fix existing bugs or issues
- **New Features**: Add new widgets, layouts, or functionality
- **Documentation**: Improve README, JavaDoc, or guides
- **Tests**: Add or improve test coverage
- **Performance**: Optimize rendering or memory usage
- **Refactoring**: Improve code quality without changing behavior

### Before You Start

1. **Check existing issues** to avoid duplicate work
2. **Open an issue** to discuss major changes before implementing
3. **Keep changes focused** - one feature or fix per PR
4. **Write tests** for new functionality

## Pull Request Process

### 1. Create a Feature Branch

```bash
git checkout -b feature/my-feature-name
# or
git checkout -b fix/issue-number-description
```

### 2. Make Your Changes

- Write clean, readable code
- Follow existing code style
- Add tests for new functionality
- Update documentation as needed

### 3. Test Thoroughly

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=JButtonTest

# Run interactive demo to verify visually
./run-interactive.sh
```

### 4. Commit Your Changes

Use conventional commit messages:

```bash
git commit -m "feat: add new widget JTreeView"
git commit -m "fix: correct mouse event coordinates"
git commit -m "docs: update README with new examples"
git commit -m "test: add edge cases for JTextField"
git commit -m "refactor: extract layout calculation logic"
```

**Commit Message Format:**
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `test:` Test additions or modifications
- `refactor:` Code refactoring
- `perf:` Performance improvements
- `chore:` Build/tooling changes

### 5. Push to Your Fork

```bash
git push origin feature/my-feature-name
```

### 6. Open a Pull Request

- Provide a clear title and description
- Reference related issues (e.g., "Closes #42")
- Describe what changed and why
- Include screenshots for UI changes
- Ensure all tests pass
- Request review from maintainers

### 7. Address Review Feedback

- Respond to comments
- Make requested changes
- Push updates to your branch
- Be patient and professional

## Versioning Strategy

jcurses uses **automatic continuous versioning**:

- **Every push to `main`** automatically increments the minor version (X.Y format)
- Version bumps are automated via CI/CD (no manual intervention)
- Current version is displayed in README.md badge and pom.xml
- This enables rapid iteration and clear tracking of changes

### What This Means for Contributors

- **Don't manually edit version numbers** in pom.xml - they're auto-updated
- Each merged PR will trigger a new version release
- Version numbers reflect iteration count, not semantic versioning
- Focus on quality code - versioning is handled automatically

### Rationale

This approach prioritizes:
- **Fast iteration** - No manual release process
- **Continuous deployment** - Every merge is a release
- **Clear history** - Each version maps to specific commits
- **Automation** - Reduce manual overhead

## Code Style

### Java Conventions

- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Max 120 characters
- **Braces**: Always use braces, even for single-line blocks
- **Naming**:
  - Classes: `PascalCase`
  - Methods/Variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

### Example

```java
public class JButton extends Component {
    private String label;
    private final List<ActionListener> listeners = new ArrayList<>();
    
    public void setLabel(String label) {
        renderLock.lock();
        try {
            this.label = label;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }
    
    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            // Rendering logic
        } finally {
            renderLock.unlock();
        }
    }
}
```

### Best Practices

- **Thread Safety**: Use `renderLock` for all state modifications
- **Resource Management**: Use try-finally for locks
- **Null Checks**: Validate parameters with `IllegalArgumentException`
- **Immutability**: Use `final` for fields that don't change
- **Comments**: Only add comments for non-obvious "why", not "what"
- **Magic Numbers**: Extract to named constants

## Testing

### Test Requirements

Every contribution should include tests:

- **New Features**: Add comprehensive test coverage
- **Bug Fixes**: Add test that reproduces the bug
- **Refactoring**: Ensure existing tests pass

### Test Structure

```java
@DisplayName("JButton Tests")
class JButtonTest extends ComponentTestBase {
    private JButton button;
    
    @BeforeEach
    void setUp() {
        button = new JButton("Click Me");
        button.setSize(20, 3);
        button.setLocation(0, 0);
    }
    
    @Test
    @DisplayName("should render label centered")
    void testRendering() {
        button.paint(buffer);
        BufferAssertions.assertBufferContains(buffer, 0, 1, "Click Me");
    }
    
    @Test
    @DisplayName("should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        ThreadSafetyTestHelper.runConcurrent(10, () -> {
            button.setLabel("Test");
        });
    }
}
```

### Coverage Goals

- **Core Components**: 85%+ coverage
- **Event System**: 90%+ coverage
- **Overall**: 80%+ coverage

## Documentation

### When to Update Documentation

- **New Features**: Update README, add to feature list
- **API Changes**: Update JavaDoc
- **Breaking Changes**: Update CHANGELOG and migration guide
- **Examples**: Add to InteractiveDemo if appropriate

### Documentation Files

- `README.md` - Project overview and quick start
- `QUICKSTART.txt` - Detailed usage guide
- `TESTING.md` - Test documentation and guide
- `INTERACTIVE_DEMO.md` - Interactive demo documentation
- JavaDoc comments for public API

## Questions?

If you have questions:

1. Check existing documentation
2. Search closed issues
3. Open a new issue with the `question` label
4. Tag maintainers in your PR for guidance

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

---

**Happy Contributing!** 🚀

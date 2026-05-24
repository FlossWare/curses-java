# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [X.Y Versioning](https://github.com/FlossWare/jcurses/blob/main/CONTRIBUTING.md#versioning-strategy).

## [Unreleased]

### Added
- JavaDoc plugin with strict validation
- Software Bill of Materials (SBOM) generation via CycloneDX
- Mutation testing with PITest (75% threshold)
- OWASP Dependency Check for vulnerability scanning
- Checkstyle code quality validation
- SpotBugs static analysis
- License compliance checking
- Property-based testing framework (jqwik)
- API Guardian annotations for API stability
- JMH performance benchmarking framework

## [1.28] - 2026-05-24

### Added
- Accessibility API in Component class (setAccessibleName, setAccessibleRole, setAccessibleDescription)
- EdgeCaseTest with 32 boundary tests for robust validation
- Constants.java for centralized magic numbers
- Comprehensive code review report (REVIEW-2026-05-24.md)
- Versioning strategy documentation in CONTRIBUTING.md

### Fixed
- Version mismatch between README (1.19) and POM (1.28) - now synchronized
- Test count documentation updated from 367 to 399 tests
- Verified CodeQL workflow configuration
- Verified Maven caching in CI/CD
- Verified test result publishing

### Changed
- Updated README.md to reflect current state (version 1.28, 399 tests)
- Documented automatic continuous versioning strategy

## [1.27] - 2026-05-23

### Added
- Complete JavaDoc for Component class
- Accessibility summary generation

### Security
- Input sanitization in JTextField to prevent terminal injection
- Defensive copying in mouse listener iteration

## [1.26] - 2026-05-22

### Fixed
- Memory leak with Arena.global() - switched to Arena.ofAuto() for mouse events
- Thread safety in RootPane dirty tracking - all methods now synchronized
- Infinite loop guards in JTextField word navigation

### Security
- Added SLF4J logging framework replacing System.out/System.err
- Implemented checkResult() for ncurses return value validation

## [1.25] - 2026-05-21

### Added
- Platform detection for ncurses library (Linux/macOS support)
- Null checks in Component.addMouseListener/removeMouseListener

### Changed
- Improved exception handling in NcursesBridge with specific exception types

## [1.24] - 2026-05-20

### Added
- SECURITY.md with vulnerability reporting process
- CONTRIBUTING.md with development guidelines
- Dependabot configuration for automated dependency updates

### Security
- CodeQL security scanning workflow

## [1.0] - Initial Release

### Added
- Core 29 UI widgets (JButton, JTextField, JLabel, etc.)
- Mouse support with drag-to-move and resize
- Thread-safe architecture with ReentrantLock
- Color support with 8 standard colors and predefined pairs
- Advanced text editing (selection, cut/copy/paste, undo/redo)
- Scrollable views with JScrollPane
- Modern Java 21 features (Virtual Threads, FFI, Pattern Matching)
- Comprehensive testing framework (399 tests)
- Theme system (Default, Dark, Light)
- CI/CD with GitHub Actions

### Security
- Thread-safe rendering
- Input validation
- Memory safety with FFI

---

For full commit history, see [GitHub Commits](https://github.com/FlossWare/jcurses/commits/main)

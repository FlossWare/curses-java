# Security Policy

## Supported Versions

We release patches for security vulnerabilities for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.x     | :white_check_mark: |

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security vulnerability, please follow these steps:

### Where to Report

**DO NOT** open a public GitHub issue for security vulnerabilities.

Instead, please report security vulnerabilities by:
1. Opening a [GitHub Security Advisory](https://github.com/FlossWare/curses-java/security/advisories/new)
2. Or emailing the maintainers directly (see GitHub profile for contact information)

### What to Include

Please include the following information in your report:
- Description of the vulnerability
- Steps to reproduce
- Affected versions
- Potential impact
- Suggested fix (if known)

### Response Timeline

- **Initial Response**: Within 48 hours
- **Status Update**: Within 7 days
- **Fix Release**: Varies by severity (critical: 7-14 days, high: 14-30 days, medium: 30-60 days)

### Disclosure Policy

- We will acknowledge your report within 48 hours
- We will provide a detailed response within 7 days
- We will work with you to understand and resolve the issue
- We will credit you for the discovery (unless you prefer to remain anonymous)
- We will coordinate disclosure timing with you

## Known Security Considerations

### Terminal Escape Sequence Injection

curses-java implements input sanitization in text components (JTextField) to prevent terminal escape sequence injection attacks. User input is sanitized to remove control characters except newline and tab.

### Native Library Loading

curses-java loads native ncurses libraries. Ensure you trust the ncurses installation on your system. On Linux, use package managers to install ncurses from trusted repositories.

### Virtual Threads and Concurrency

All components use ReentrantLock for thread-safety. If you encounter race conditions, please report them as potential security issues.

## Security Best Practices for Users

1. **Keep ncurses Updated**: Ensure your system's ncurses library is up to date
2. **Validate User Input**: Always validate and sanitize user input in your applications
3. **Run with Least Privilege**: Don't run terminal applications as root unless necessary
4. **Audit Dependencies**: curses-java has zero runtime dependencies beyond ncurses

## Attribution

We appreciate responsible disclosure and will acknowledge security researchers who help improve curses-java security.

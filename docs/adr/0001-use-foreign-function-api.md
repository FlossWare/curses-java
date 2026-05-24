# ADR 0001: Use Foreign Function & Memory API instead of JNI

**Status:** Accepted  
**Date:** 2024-01-15  
**Deciders:** Core Team  
**Tags:** architecture, ffi, java-21

## Context

jcurses needs to interface with the native ncurses library for terminal manipulation. There are two main approaches:

1. **JNI (Java Native Interface)** - Traditional approach requiring C/C++ code compilation
2. **Foreign Function & Memory API (Project Panama)** - Modern Java 21 approach, pure Java

## Decision

We will use the Foreign Function & Memory API (FFI) from Project Panama.

## Rationale

### Positive Consequences

- **No Native Compilation**: Pure Java solution, no C/C++ code to compile or maintain
- **Better Memory Safety**: Arena-based memory management prevents leaks
- **Cleaner Codebase**: All code in Java, easier to understand and maintain
- **Better Performance**: FFI has lower overhead than JNI in many cases
- **Modern Java**: Demonstrates cutting-edge Java 21 capabilities
- **Type Safety**: Stronger type checking at compile time

### Negative Consequences

- **Java 21 Requirement**: Requires Java 21+ with preview features enabled
- **Preview Feature Risk**: API may change in future Java versions (though finalized in Java 22)
- **Smaller Ecosystem**: Fewer examples and libraries compared to JNI
- **Learning Curve**: Team needs to learn Panama API patterns

## Implementation

See `src/main/java/org/flossware/jcurses/ffi/NcursesBridge.java` for the implementation.

Key patterns used:
- `SymbolLookup.libraryLookup()` for dynamic library loading
- `Linker.nativeLinker()` for creating downcall handles
- `Arena.ofAuto()` for automatic memory management
- `FunctionDescriptor` for native function signatures

## Alternatives Considered

### JNI (Java Native Interface)
**Pros:** Mature, well-documented, widely used  
**Cons:** Requires C code, build complexity, harder to debug  
**Verdict:** Rejected due to build complexity and maintenance burden

### JNA (Java Native Access)
**Pros:** No compilation needed, simpler than JNI  
**Cons:** Third-party dependency, slower than FFI, reflection-based  
**Verdict:** Rejected in favor of official Java solution

## References

- [JEP 454: Foreign Function & Memory API](https://openjdk.org/jeps/454)
- [Project Panama Documentation](https://openjdk.org/projects/panama/)
- [Java 21 Release Notes](https://openjdk.org/projects/jdk/21/)

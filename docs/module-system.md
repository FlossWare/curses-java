# Java Module System Support

jcurses is ready for Java 9+ module system (JPMS) deployment.

## Quick Start

The project ships without `module-info.java` to maintain flexibility for both modular and non-modular applications. To enable module support:

1. **Copy the template:**
   ```bash
   cp module-info.java.template src/main/java/module-info.java
   ```

2. **Update pom.xml** (for module-native access):
   Replace `--enable-native-access=ALL-UNNAMED` with `--enable-native-access=org.flossware.jcurses` in:
   - `maven-surefire-plugin` configuration
   - `exec-maven-plugin` configuration

3. **Build as module:**
   ```bash
   mvn clean package
   ```

## Module Declaration

```java
module org.flossware.jcurses {
    exports org.flossware.jcurses.api;
    exports org.flossware.jcurses.api.edit;
    exports org.flossware.jcurses.events;

    opens org.flossware.jcurses.api;
    opens org.flossware.jcurses.api.edit;
    opens org.flossware.jcurses.events;
}
```

## Exported Packages

- **org.flossware.jcurses.api** - Core widgets and components
- **org.flossware.jcurses.api.edit** - Text editing support (Clipboard, TextEditCommand)
- **org.flossware.jcurses.events** - Event system (KeyEvent, MouseEvent, WindowEvent)

## Internal Packages (Not Exported)

- **org.flossware.jcurses.render** - Rendering engine (DiffEngine, EventProcessor)
- **org.flossware.jcurses.ffi** - Native ncurses FFI bridge

## Why No Default module-info.java?

1. **Testing Compatibility**: JUnit requires reflection access to test utilities. Running tests on the class path (not module path) avoids complex `--add-opens` configuration.

2. **Flexibility**: Many applications don't use modules yet. Shipping without module-info.java allows both modular and non-modular usage.

3. **Industry Practice**: Common pattern for libraries (e.g., Apache Commons, Guava) - provide module descriptor as opt-in.

## Using in Modular Applications

### As a module

If you've added module-info.java:

```java
module your.app {
    requires org.flossware.jcurses;
}
```

### On the classpath

Works without any module configuration:

```bash
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp jcurses-1.0.jar:your-app.jar \
  your.app.Main
```

## jlink / jpackage

For modular runtime images:

```bash
jlink --module-path target/jcurses-1.0.jar:$JAVA_HOME/jmods \
  --add-modules org.flossware.jcurses \
  --output runtime-image
```

## Notes

- **Native Access**: ncurses FFI requires `--enable-native-access` flag
- **Preview Features**: Java 21 preview features must be enabled with `--enable-preview`
- **Reflection**: The `opens` directives allow frameworks (DI containers, etc.) to access internals via reflection

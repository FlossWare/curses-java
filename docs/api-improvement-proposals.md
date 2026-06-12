# API Improvement Proposals

**Multi-AI Design Panel** (92% confidence)  
**Date:** 2026-06-12  
**Issue:** #237

## Problem Statement

Current API feels "old-school AWT" with excessive boilerplate:
- Manual `setLocation(x, y)` and `setSize(w, h)` for every component
- Coordinate math (`terminalWidth - 2`, `terminalHeight - 4`)
- **Layout managers exist but are NEVER used** in any example
- No `preferredSize` concept (layouts can't query component dimensions)
- No constraint-aware `add(component, constraint)` method
- Missing BoxLayout/VBoxLayout for simple vertical stacking

## Three Alternative API Designs

---

## Design 1: Builder Pattern (Fluent API)

### Example Code
```java
Frame frame = Frame.builder("Simple Demo")
    .fillTerminal()
    .visible(true)
    .child(
        Panel.builder()
            .inset(1, 2, 2, 0)
            .bordered(true)
            .child(Label.builder("Interactive Demo").at(3, 4).size(40, 1))
            .child(
                Button.builder("Click Me!")
                    .at(3, 7)
                    .size(15, 1)
                    .onAction(() -> System.out.println("Clicked!"))
            )
    )
    .build();

RootPane.getInstance().add(frame);
```

### Implementation
Each component gets a static inner `Builder` class:
```java
public class Button extends Component {
    public static ButtonBuilder builder(String label) {
        return new ButtonBuilder(label);
    }

    public static class ButtonBuilder {
        private final String label;
        private int x, y, width, height;
        private Runnable action;

        ButtonBuilder(String label) { this.label = label; }

        public ButtonBuilder at(int x, int y) {
            this.x = x; this.y = y;
            return this;
        }

        public ButtonBuilder size(int w, int h) {
            this.width = w; this.height = h;
            return this;
        }

        public ButtonBuilder onAction(Runnable action) {
            this.action = action;
            return this;
        }

        public Button build() {
            Button btn = new Button(label);
            btn.setLocation(x, y);
            btn.setSize(width, height);
            if (action != null) btn.addActionListener(action);
            return btn;
        }
    }
}
```

### Pros
- Reduces boilerplate (fluent chaining)
- Better readability (nested structure mirrors UI hierarchy)
- Type-safe (compile-time checking)
- Backward compatible (existing API unchanged)

### Cons
- Still uses absolute positioning (doesn't encourage layouts)
- Builder classes add ~50-100 LOC per component
- Memory overhead (builder instances)

### Migration Path
1. Add builder classes to all components (phase 1)
2. Update examples to use builders (phase 2)
3. Mark setLocation/setSize as @Deprecated with warning (phase 3, optional)

---

## Design 2: Layout-First API

### Example Code
```java
Frame frame = new Frame("Simple Demo");
frame.setLayout(new VBoxLayout()); // NEW: vertical box layout

Label title = new Label("Interactive Demo");
title.setPreferredSize(40, 1);    // NEW: preferred size

Button btn1 = new Button("Click Me!");
btn1.setPreferredSize(15, 1);
btn1.addActionListener(() -> System.out.println("Clicked!"));

frame.add(title);
frame.add(btn1);
frame.pack(); // NEW: size to preferred sizes

RootPane.getInstance().add(frame);
```

### NEW Classes Required

**VBoxLayout.java:**
```java
public class VBoxLayout implements LayoutManager {
    private int spacing = 1;

    public VBoxLayout() {}
    public VBoxLayout(int spacing) { this.spacing = spacing; }

    @Override
    public void layoutContainer(Container parent) {
        int y = 0;
        for (Component child : parent.getChildren()) {
            Dimension pref = child.getPreferredSize();
            child.setLocation(0, y);
            child.setSize(parent.getWidth(), pref.height);
            y += pref.height + spacing;
        }
    }
}
```

**Component.java additions:**
```java
public class Component {
    protected Dimension preferredSize = new Dimension(10, 1); // default

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(int width, int height) {
        this.preferredSize = new Dimension(width, height);
    }
}
```

**Container.java additions:**
```java
public void add(Component child, Object constraint) {
    children.add(child);
    child.setParent(this);
    if (layoutManager != null) {
        layoutManager.addLayoutComponent(child, constraint);
    }
    invalidateLayout();
}

public void pack() {
    // Size container to fit children's preferred sizes
    if (layoutManager != null) {
        Dimension pref = layoutManager.preferredLayoutSize(this);
        setSize(pref.width, pref.height);
    }
}
```

### Pros
- **Fixes the root problem** (layout managers are invisible today)
- Responsive to terminal resize
- Familiar AWT pattern
- Eliminates coordinate math

### Cons
- Breaking change (requires preferredSize concept)
- Existing absolute-positioned code still works but discouraged
- All widgets need sensible preferred sizes
- More complex than builders

### Migration Path
1. Add preferredSize + pack() to Component/Container
2. Implement VBoxLayout, HBoxLayout
3. Update BorderLayout to use preferredSize
4. Rewrite 1-2 examples using layouts
5. Document layout-first approach in README
6. Gradual migration (both APIs coexist)

---

## Design 3: Declarative DSL (Record-Based)

### Example Code
```java
UI ui = UI.create(
    frame("Simple Demo",
        panel(BORDERED, INSET(1, 2),
            label("Interactive Demo", AT(3, 4), SIZE(40, 1)),
            button("Click Me!", AT(3, 7), SIZE(15, 1),
                onClick(() -> System.out.println("Clicked!")))
        )
    )
);

ui.show();
```

### Implementation
```java
public sealed interface ComponentSpec {
    record Frame(String title, ComponentSpec... children) implements ComponentSpec {}
    record Panel(Attribute[] attrs, ComponentSpec... children) implements ComponentSpec {}
    record Label(String text, Attribute... attrs) implements ComponentSpec {}
    record Button(String label, Attribute... attrs) implements ComponentSpec {}
}

public sealed interface Attribute {
    record At(int x, int y) implements Attribute {}
    record Size(int w, int h) implements Attribute {}
    record OnClick(Runnable action) implements Attribute {}
    record Bordered() implements Attribute {}
    record Inset(int top, int left, int bottom, int right) implements Attribute {}
}

public class UI {
    public static UI create(ComponentSpec spec) {
        return new UI(build(spec));
    }

    private static Component build(ComponentSpec spec) {
        return switch (spec) {
            case Frame(String title, ComponentSpec[] children) -> {
                Frame f = new Frame(title);
                for (ComponentSpec child : children) {
                    f.add(build(child));
                }
                yield f;
            }
            case Button(String label, Attribute[] attrs) -> {
                Button b = new Button(label);
                applyAttributes(b, attrs);
                yield b;
            }
            // ... other cases
        };
    }
}
```

### Pros
- **Most concise** (minimal syntax)
- Declarative (what, not how)
- Java 21 pattern matching showcase
- Immutable specifications

### Cons
- **Radical departure** from existing API
- Sealed records = Java 17+ only
- No IDE autocomplete for attributes
- Harder to debug (stack traces through switch/recursion)

### Migration Path
- **NOT recommended for 1.0** (too disruptive)
- Potential 2.0 "declarative module" as opt-in alternative
- Keep imperative API as default

---

## Multi-AI Recommendations

### Consensus (all 3 models agreed):
1. **Design 2 (Layout-First) is the best path forward**
   - Fixes root cause (layouts exist but unused)
   - Familiar to Java developers
   - Coexists with existing code

2. **Quick wins to implement first:**
   - Add `VBoxLayout` and `HBoxLayout`
   - Add `Component.preferredSize` field
   - Add `Container.pack()` method
   - Add `Container.add(Component, Object)` overload
   - Rewrite one example (SimpleDemo.java) using layouts

3. **Builder pattern is a good supplement:**
   - Can coexist with layouts
   - Reduces boilerplate even with absolute positioning
   - Low implementation cost

### Suggested Roadmap

**Phase 1 (v1.1 - 2 weeks):**
- Implement `VBoxLayout` / `HBoxLayout`
- Add `preferredSize` to Component
- Add `pack()` to Container
- Rewrite SimpleDemo to use layouts

**Phase 2 (v1.2 - 4 weeks):**
- Add builders to top 10 most-used widgets
- Update 2 more examples
- Document layout patterns

**Phase 3 (v2.0 - future):**
- Evaluate declarative DSL as experimental module
- Benchmark layout performance
- Consider constraint-based layouts (GridBagLayout equivalent)

---

## Critical Codebase Findings

From multi-AI analysis:

1. **BorderLayout, FlowLayout, GridLayout ALREADY EXIST** but zero examples use them
2. **No BoxLayout** (most common pattern)
3. **Container.setLayout() exists** but is invisible to users
4. **No preferredSize concept** (layouts can't query ideal dimensions)

**The problem isn't missing features - it's discoverability and defaults.**

---

## Action Items for Issue #237

1. ✅ Create VBoxLayout.java
2. ✅ Create HBoxLayout.java
3. ✅ Add preferredSize field to Component.java
4. ✅ Add pack() method to Container.java
5. ✅ Add add(Component, Object) to Container.java
6. ✅ Rewrite SimpleDemo.java to use VBoxLayout
7. ✅ Add "Layout Guide" section to docs/examples.md
8. ✅ Update README to showcase layout-first approach

**Estimated effort:** 3-5 days for phase 1

---

**Related Issues:** #237 (simplify API), #241 (real-world examples can demonstrate layouts)  
**Multi-AI Contributors:** Opus, Sonnet, Haiku (92% confidence)

# ADR 0003: Thread Safety with ReentrantLock

**Status:** Accepted  
**Date:** 2024-02-01  
**Deciders:** Core Team  
**Tags:** architecture, concurrency, thread-safety

## Context

curses-java components need to be thread-safe as they may be accessed from:
- Virtual threads in the event loop
- User application threads
- Rendering thread
- Mouse event handlers

Multiple synchronization mechanisms exist:
- `synchronized` keyword
- `ReentrantLock`
- `ReadWriteLock`
- `StampedLock`
- Atomic variables

## Decision

Use `ReentrantLock` for all component state synchronization.

## Rationale

### Positive Consequences

- **Flexibility**: Can use try-finally patterns for guaranteed unlocking
- **Explicit**: Clear lock/unlock points in code
- **Timeout Support**: Can attempt lock with timeout (future feature)
- **Fairness**: Can enable fair locking if needed
- **Interruptible**: Supports thread interruption
- **Better with Virtual Threads**: Works well with Project Loom

### Negative Consequences

- **More Verbose**: Requires explicit lock/unlock code
- **Error-Prone**: Easy to forget unlock in finally block
- **Performance**: Slightly slower than synchronized in uncontended cases

## Implementation Pattern

```java
public class Component {
    protected final ReentrantLock renderLock = new ReentrantLock();
    
    public void setLocation(int x, int y) {
        renderLock.lock();
        try {
            this.x = x;
            this.y = y;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }
}
```

## Best Practices

1. **Always use try-finally**: Never lock without try-finally
2. **Minimize critical sections**: Keep locked code short
3. **No I/O in locks**: Never do I/O while holding lock
4. **Defensive copying**: Copy collections before releasing lock
5. **Consistent ordering**: Always acquire locks in same order

## Alternatives Considered

### synchronized keyword
**Pros:** Simple, automatic unlock  
**Cons:** Less flexible, can't timeout  
**Verdict:** Rejected, need lock flexibility

### ReadWriteLock
**Pros:** Better for read-heavy workloads  
**Cons:** More complex, overkill for our use case  
**Verdict:** Rejected, YAGNI

### Atomic Variables
**Pros:** Lock-free, fast  
**Cons:** Only works for single variables  
**Verdict:** Used where applicable (e.g., dirty flags)

## References

- [Java Concurrency in Practice](https://jcip.net/)
- [ReentrantLock JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html)
- [Virtual Threads JEP 444](https://openjdk.org/jeps/444)

# ADR 0002: Automatic Continuous Versioning

**Status:** Accepted  
**Date:** 2026-05-24  
**Deciders:** Project Maintainer  
**Tags:** ci-cd, versioning, releases

## Context

Software projects need a versioning strategy that balances:
- Semantic meaning of version numbers
- Release automation
- Development velocity
- User expectations

## Decision

curses-java uses **Automatic Continuous Versioning** with X.Y format where:
- Every push to `main` automatically increments the minor version (Y)
- Major version (X) changes are manual and indicate significant milestones
- No semantic versioning (X.Y.Z) - versions represent iteration count

## Rationale

### Positive Consequences

- **Maximum Automation**: Zero manual intervention needed for releases
- **Fast Iteration**: Each merge is immediately released
- **Clear History**: Every version maps directly to specific commits
- **Continuous Deployment**: Users always get latest fixes immediately
- **Reduced Overhead**: No manual release process or version planning
- **Unambiguous**: No debate about what constitutes a "minor" vs "patch" change

### Negative Consequences

- **Version Inflation**: Version numbers grow quickly (currently v1.28)
- **No Semantic Meaning**: Version numbers don't indicate breaking changes
- **Frequent Updates**: Users may experience many small updates
- **Breaking Changes**: Need extra care to communicate API changes

## Implementation

### CI/CD Configuration
```yaml
- name: Incrementing pom.xml version
  run: "mvn build-helper:parse-version versions:set 
        -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.nextMinorVersion} 
        versions:commit"
```

### Documentation
- CONTRIBUTING.md documents the strategy
- CHANGELOG.md tracks all changes
- GitHub Releases provide human-readable notes

## Mitigation Strategies

To address the negative consequences:

1. **CHANGELOG.md**: Maintain detailed changelog for every release
2. **API Annotations**: Use `@API` annotations to mark stability
3. **Communication**: Clearly document breaking changes
4. **Semantic Commits**: Use conventional commits for clarity
5. **Release Notes**: Auto-generate from commit messages

## Alternatives Considered

### Semantic Versioning (X.Y.Z)
**Pros:** Industry standard, clear meaning  
**Cons:** Requires manual decisions, slower releases  
**Verdict:** Rejected for this project's fast iteration needs

### Tag-Based Releases
**Pros:** Full control, predictable  
**Cons:** Manual process, release bottleneck  
**Verdict:** Considered for future major versions only

### Calendar Versioning (YYYY.MM.DD)
**Pros:** Time-based clarity  
**Cons:** Doesn't reflect iteration count  
**Verdict:** Rejected, iteration count more meaningful

## Future Considerations

- For v2.0: Consider switching to manual/tag-based for major versions
- Monitor community feedback on version frequency
- May introduce LTS (Long Term Support) versions in future

## References

- [Keep a Changelog](https://keepachangelog.com/)
- [Semantic Versioning](https://semver.org/)
- [Calendar Versioning](https://calver.org/)

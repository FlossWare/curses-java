# ADR 0007: Shared Theme Data Package Evaluation

**Status:** Accepted (Recommendation: Keep in curses-java repo)
**Date:** 2026-07-18
**Deciders:** Project Maintainer
**Tags:** architecture, themes, packaging, distribution

## Context

12 JSON theme files plus a `schema.json` currently live in curses-java's `/themes/` directory. The Python `curses-themes` project accesses them via sibling directory checkout or CI checkout. The question arose (issue #271) whether these shared theme files should be published as a standalone package -- on Maven Central, PyPI, npm, or some combination -- to formalize distribution and version pinning.

The theme data set is small: 12 files totaling less than 20KB. Themes change infrequently, roughly twice per year. Only two consumers exist today: curses-java (Java) and curses-themes (Python).

### Options Evaluated

#### Option 1: Publish `flossware-themes-data` to Maven Central + PyPI

Publish the JSON files as a data-only artifact on Maven Central (for Java consumers) and PyPI (for Python consumers), with potential npm packaging for future JavaScript consumers.

**Pros:**
- Version-pinned dependencies -- consumers declare an exact theme data version
- Easy installation via standard package managers (`mvn`, `pip install`, `npm install`)
- Language-agnostic distribution -- enables future Rust, Go, or JavaScript consumers without repository coupling
- Clear separation of concerns between theme data and theme rendering logic

**Cons:**
- Extra release process for every theme addition or modification
- Themes rarely change (~2x/year), making the release overhead disproportionate
- Only 12 files totaling less than 20KB -- a package feels heavyweight for the payload
- Coordination overhead between three repositories (theme data, curses-java, curses-themes)
- Maven Central publishing requires GPG signing, Sonatype staging, and release promotion

#### Option 2: Git submodule in curses-themes pointing to curses-java/themes

Use a git submodule in the `curses-themes` repository that references the `themes/` directory in `curses-java`.

**Pros:**
- Always in sync with the source of truth
- No publishing infrastructure or release process required
- Zero duplication of theme files

**Cons:**
- Git submodules are notoriously fragile -- developers frequently forget to run `git submodule init` and `git submodule update`
- CI complexity increases (must configure submodule checkout in every pipeline)
- Submodule references can silently fall behind if not explicitly updated
- Poor developer experience for contributors unfamiliar with submodule workflows

#### Option 3: Keep files in curses-java, download in CI (current approach)

Maintain the JSON theme files in curses-java's `/themes/` directory. Consumer projects (currently only `curses-themes`) check out or download the files during CI validation.

**Pros:**
- Simplest approach -- zero extra infrastructure, no new repositories, no release processes
- CI already works and has been validated
- Themes rarely change, so the lack of formal versioning has no practical impact
- Single source of truth with no synchronization concerns

**Cons:**
- No version pinning -- Python adapter could break if Java changes schema without coordination
- Consumer projects depend on a specific directory structure in a foreign repository
- Not easily discoverable for potential new consumers

## Decision

**Option 3** -- keep files in curses-java repo with the current CI checkout approach.

The current approach works well for the project's actual needs:

- CI in `curses-themes` already checks out `curses-java` and validates theme compatibility
- Schema drift detection (via `schema.json` validation) catches breaking changes before they merge
- 12 files totaling less than 20KB does not justify the overhead of a standalone package
- Two consumers (Java and Python) are well-served by the existing workflow
- The release coordination cost of a separate package exceeds the benefit for a data set that changes twice per year

**Revisit this decision if:**
- Theme count exceeds 50 files (payload size begins to justify dedicated packaging)
- A third language consumer appears (distribution complexity increases)
- Theme change frequency increases significantly (version pinning becomes valuable)
- The schema undergoes a breaking change that causes cross-project failures

## Consequences

### Positive

- **No new repos or release processes needed.** The team avoids maintaining a separate package lifecycle for a small, stable data set.
- **curses-java remains the single source of truth.** There is exactly one place where theme files are authored and reviewed.
- **Zero infrastructure cost.** No GPG signing, no Sonatype staging, no PyPI tokens, no npm publishing configuration.
- **Simplicity for contributors.** Adding a new theme is a single PR to curses-java -- no downstream release coordination required.

### Negative

- **No version pinning for consumers.** If curses-java changes the schema in a backward-incompatible way, consumers may break until they update their CI checkout or local copies. Schema validation in CI mitigates this risk.
- **Schema changes must be coordinated via `theme-alignment` issues.** Breaking changes require manual communication between the curses-java and curses-themes maintainers.
- **Python CI continues to depend on curses-java checkout.** The `curses-themes` CI pipeline must fetch curses-java to access theme files, creating a cross-repository dependency in the build.
- **New consumers must discover the convention.** There is no `pip install` or `mvn dependency` -- future consumers need documentation pointing them to the curses-java repository.

## References

- [Issue #271](https://github.com/FlossWare/curses-java/issues/271) -- Evaluate publishing theme data as standalone package
- [ADR 0006](0006-unification-strategy.md) -- Unification strategy establishing JSON-only approach for shared theme data
- [Issue #264](https://github.com/FlossWare/curses-java/issues/264) -- ThemeLoader + JSON theme support (completed)
- `themes/schema.json` -- JSON Schema (draft-07) defining the theme file format
- `themes/README.md` -- End-user documentation for the shared theme format

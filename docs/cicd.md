# CI/CD Pipeline

jcurses uses GitHub Actions for automated continuous integration and deployment.

## Overview

The CI/CD pipeline automatically:
1. Increments the version number (X.Y format)
2. Updates dependencies to latest versions
3. Builds and tests the project
4. Deploys JAR artifacts to packagecloud.io
5. Tags the release in GitHub

## Workflow Trigger

- **Event**: Push to `main` branch
- **Condition**: Skips if pusher email is `version-bump@flossware.org` (prevents recursive builds)

## Pipeline Steps

### 1. Environment Setup
- Updates Ubuntu runner
- Installs `libncurses-dev` (required for tests)
- Sets up JDK 21 (Temurin distribution)
- Checks out repository

### 2. Maven Settings Configuration
- Configures packagecloud.io credentials
- Uses `PACKAGECLOUD_TOKEN` secret for authentication

### 3. Git Configuration
- Sets up git user: "Version Bump" <version-bump@flossware.org>
- Enables automated commits for version bumps

### 4. Version Management
- Parses current version from pom.xml
- Increments minor version: X.Y → X.(Y+1)
- Example: 1.0 → 1.1 → 1.2
- Updates pom.xml automatically

### 5. Dependency Updates
- Updates JUnit Jupiter to latest version
- Updates Mockito to latest version
- Updates AssertJ to latest version

### 6. Build & Test
- Runs `mvn clean install`
- Executes all 289 unit tests
- Enforces code coverage requirements
- Validates X.Y version format

### 7. Deployment
- Deploys JAR to packagecloud.io/flossware/java
- Skips tests during deployment (already run in build step)

### 8. Git Operations
- Commits updated pom.xml
- Creates git tag with version number (e.g., `1.1`)
- Pushes commit and tag to GitHub

## Required Secrets

Configure these in GitHub repository settings:

- **PACKAGECLOUD_TOKEN**: Authentication token for packagecloud.io
- **GITHUB_TOKEN**: Automatically provided by GitHub Actions

## Version Format

The project uses **X.Y versioning** (not semantic X.Y.Z):
- Major version (X): Incremented manually for breaking changes
- Minor version (Y): Auto-incremented on each deployment
- Examples: 1.0, 1.1, 1.2, ..., 1.10, 1.11

## Manual Version Increment

To manually increment the version:

```bash
# Increment minor version
./ci/rev-version.sh
```

This script:
- Parses current version
- Increments minor version
- Updates pom.xml
- Commits, tags, and pushes to GitHub

## Artifact Repository

JARs are published to:
```
https://packagecloud.io/flossware/java/maven2/
```

To use in your project:

```xml
<repositories>
    <repository>
        <id>packagecloud-flossware</id>
        <url>https://packagecloud.io/flossware/java/maven2/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.flossware</groupId>
        <artifactId>jcurses</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

## Build Status

Check the Actions tab in GitHub to monitor builds:
```
https://github.com/FlossWare/jcurses/actions
```

## Preventing Builds

To push changes without triggering CI/CD:
```bash
git commit -m "Your message [ci skip]"
```

Or ensure the commit is made with email `version-bump@flossware.org`.

## Local Testing

Test the build locally before pushing:

```bash
# Full build with tests
mvn clean install

# Deploy to packagecloud (requires credentials)
mvn deploy

# Version increment (creates commit/tag)
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}
```

## Troubleshooting

**Build fails on ncurses:**
- Ensure `libncurses-dev` is installed
- Tests require actual ncurses library

**Version format error:**
- Version must match `^\d+\.\d+$` pattern
- No SNAPSHOT suffixes allowed
- No three-part versions (X.Y.Z)

**Deployment fails:**
- Check PACKAGECLOUD_TOKEN secret is configured
- Verify packagecloud.io repository exists

**Recursive build loop:**
- Workflow checks pusher email to prevent loops
- Version bump commits use `version-bump@flossware.org`

# Maven Central Publishing Guide for curses-java

This guide provides complete step-by-step instructions for publishing the curses-java library to Maven Central. Maven Central is the primary repository for Java libraries and makes curses-java discoverable by Java developers worldwide.

**Current Status:**
- **GroupId:** org.flossware
- **ArtifactId:** curses-java
- **License:** GPL-3.0
- **Repository:** https://github.com/FlossWare/curses-java

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [POM.xml Configuration](#pomxml-configuration)
3. [Release Process](#release-process)
4. [CI/CD Automation](#cicd-automation)
5. [Common Pitfalls & Troubleshooting](#common-pitfalls--troubleshooting)
6. [Post-Publishing](#post-publishing)

---

## Prerequisites

### 1. Create a Sonatype Account

Sonatype hosts Maven Central. All artifacts are published through their Nexus Repository Manager.

**Steps:**

1. Visit https://issues.sonatype.org/secure/Signup!default.jspa
2. Create a Jira account with:
   - Username: (choose your username)
   - Email: (use your project email)
   - Full name: (your name)
3. Verify your email
4. Once logged in, note your Jira username (you'll need it later)

**Keep these credentials secure:**
```
Sonatype Username: <your_username>
Sonatype Password: <your_password>
```

Store in `~/.m2/settings.xml` (see [POM.xml Configuration](#distribute-management-configuration) below).

### 2. Request Domain Verification (org.flossware)

Maven Central requires proof that you control the GroupId. For org.flossware domain:

**Steps:**

1. Create a Jira issue in the Central Repository project: https://issues.sonatype.org/browse/OSSRH
   - Select **Create Issue**
   - Project: **Community Developers (OSSRH)**
   - Issue Type: **New Project**
   - GroupId: `org.flossware`
   - Project URL: `https://github.com/FlossWare/curses-java`
   - SCM URL: `https://github.com/FlossWare/curses-java.git`
   - Username: (your Sonatype username)

2. Sonatype will ask for domain verification. Since you don't own flossware.org domain:
   - **Option A (Recommended):** Create a GitHub organization "FlossWare" if not already done
   - **Option B:** Modify your request to use `com.github.flossware` as the GroupId
   - **Option C:** Request domain verification via DNS TXT record

3. For GitHub-based GroupId (`com.github.flossware`):
   - Create the organization on GitHub: https://github.com/organizations/new
   - Use the org in your GroupId: `com.github.flossware`
   - Sonatype will verify the org exists and you're an admin

4. Sonatype will reply within hours to days with approval

**Note:** If using org.flossware, you'll need to verify domain ownership or use the GitHub organization approach.

### 3. Generate GPG Keys

All artifacts published to Maven Central must be signed with GPG (GNU Privacy Guard).

**Install GPG:**
```bash
# Linux (Debian/Ubuntu)
sudo apt-get install gpg2

# macOS
brew install gnupg2

# Windows
# Download from https://www.gpg4win.org/
```

**Generate your key pair:**
```bash
gpg2 --full-generate-key
```

When prompted:
- **Key type:** RSA and RSA (default)
- **Key size:** 4096 bits (recommended for security)
- **Validity:** 0 (never expires) or 2-5 years
- **Real name:** Your name
- **Email:** Your email
- **Passphrase:** Strong passphrase (20+ characters, mix of upper/lower/numbers/symbols)

**List your keys:**
```bash
gpg2 --list-keys
```

Example output:
```
/home/sfloess/.gnupg/pubring.gpg
--------------------------------
pub   rsa4096 2026-06-12 [SC]
      1234567890ABCDEF1234567890ABCDEF12345678
uid           [ultimate] Scot P. Floess <scot@flossware.org>
sub   rsa4096 2026-06-12 [E]
```

Note your **Key ID** (the 40-character hex string after `rsa4096`).

**Export and distribute your public key:**
```bash
# Export to file
gpg2 --export -a > pubkey.asc

# Distribute to keyservers
gpg2 --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID
gpg2 --keyserver hkp://keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg2 --keyserver hkp://pgp.mit.edu --send-keys YOUR_KEY_ID
```

Store your passphrase securely (you'll need it during releases):
```bash
# DO NOT commit this to git!
# Store in password manager or ~/.bash_profile with restricted permissions
export GPG_PASSPHRASE="your-strong-passphrase"
chmod 600 ~/.gnupg/private-key-store.txt
```

### 4. Configure Maven Settings

Create or edit `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <!-- Sonatype OSSRH Repository -->
    <server>
      <id>ossrh</id>
      <username>YOUR_SONATYPE_USERNAME</username>
      <password>YOUR_SONATYPE_PASSWORD</password>
    </server>
  </servers>
  
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg2</gpg.executable>
        <gpg.keyname>YOUR_KEY_ID</gpg.keyname>
        <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

**IMPORTANT:** Protect this file:
```bash
chmod 600 ~/.m2/settings.xml
```

**Better approach for CI/CD:** Use environment variables instead of plain text (see [CI/CD Automation](#cicd-automation)).

---

## POM.xml Configuration

Update `pom.xml` to meet Maven Central requirements. This is the critical step.

### 1. Essential Metadata

Ensure these elements are present at the root `<project>` level:

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  
  <groupId>org.flossware</groupId>
  <artifactId>curses-java</artifactId>
  <version>1.0</version>
  
  <!-- Required: Project name and description -->
  <name>curses-java</name>
  <description>
    A modern Java terminal UI library that brings AWT-like components to the 
    terminal using ncurses. Built with Java 21 features including Virtual Threads, 
    Foreign Function API, Record Patterns, and Sealed Interfaces.
  </description>
  <url>https://github.com/FlossWare/curses-java</url>
  
  <!-- Required: License information -->
  <licenses>
    <license>
      <name>GNU General Public License v3.0 or later</name>
      <url>https://www.gnu.org/licenses/gpl-3.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  
  <!-- Required: Developer information -->
  <developers>
    <developer>
      <id>sfloess</id>
      <name>Scot P. Floess</name>
      <email>scot@flossware.org</email>
      <url>https://github.com/sfloess</url>
      <organization>FlossWare</organization>
      <organizationUrl>https://github.com/FlossWare</organizationUrl>
      <roles>
        <role>architect</role>
        <role>developer</role>
      </roles>
      <timezone>America/Chicago</timezone>
    </developer>
  </developers>
  
  <!-- Required: SCM (Source Control Management) -->
  <scm>
    <connection>scm:git:https://github.com/FlossWare/curses-java.git</connection>
    <developerConnection>scm:git:https://github.com/FlossWare/curses-java.git</developerConnection>
    <url>https://github.com/FlossWare/curses-java</url>
    <tag>HEAD</tag>
  </scm>
</project>
```

### 2. Distribution Management Configuration

Replace the existing `<distributionManagement>` section with Sonatype OSSRH:

```xml
<distributionManagement>
  <!-- Sonatype OSSRH Staging Repository -->
  <repository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
  </repository>
  
  <!-- Sonatype OSSRH Snapshot Repository -->
  <snapshotRepository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
  </snapshotRepository>
</distributionManagement>
```

### 3. Build Plugins for Maven Central

Add these plugins to the `<build><plugins>` section:

#### A. Maven Source Plugin
Attach source code to artifacts:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-source-plugin</artifactId>
  <version>3.3.0</version>
  <executions>
    <execution>
      <id>attach-sources</id>
      <goals>
        <goal>jar-no-fork</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

#### B. Maven Javadoc Plugin
Generate and attach Javadoc (already present, verify it's configured):

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-javadoc-plugin</artifactId>
  <version>3.10.1</version>
  <executions>
    <execution>
      <id>attach-javadocs</id>
      <goals>
        <goal>jar</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <source>21</source>
    <additionalOptions>--enable-preview</additionalOptions>
    <show>public</show>
    <nohelp>true</nohelp>
    <links>
      <link>https://docs.oracle.com/en/java/javase/21/docs/api/</link>
    </links>
  </configuration>
</plugin>
```

#### C. Maven GPG Plugin
Sign all artifacts with GPG:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-gpg-plugin</artifactId>
  <version>3.2.4</version>
  <executions>
    <execution>
      <id>sign-artifacts</id>
      <phase>verify</phase>
      <goals>
        <goal>sign</goal>
      </goals>
      <configuration>
        <gpgArguments>
          <arg>--pinentry-mode</arg>
          <arg>loopback</arg>
        </gpgArguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```

#### D. Nexus Staging Maven Plugin
Automate staging repository management:

```xml
<plugin>
  <groupId>org.sonatype.plugins</groupId>
  <artifactId>nexus-staging-maven-plugin</artifactId>
  <version>1.7.0</version>
  <extensions>true</extensions>
  <configuration>
    <serverId>ossrh</serverId>
    <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
    <autoReleaseAfterClose>false</autoReleaseAfterClose>
    <!-- Set to true for automatic promotion after 5 minutes -->
    <!-- <autoReleaseAfterClose>true</autoReleaseAfterClose> -->
  </configuration>
</plugin>
```

#### E. Maven Release Plugin
Automate version bumping and tagging:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-release-plugin</artifactId>
  <version>3.0.1</version>
  <configuration>
    <autoVersionSubmodules>true</autoVersionSubmodules>
    <useReleaseProfile>false</useReleaseProfile>
    <releaseProfiles>ossrh</releaseProfiles>
    <goals>deploy</goals>
    <tagNameFormat>v@{project.version}</tagNameFormat>
    <scmCommentPrefix>[maven-release-plugin]</scmCommentPrefix>
  </configuration>
</plugin>
```

### 4. Create a Release Profile

Add a `<profiles>` section to automate signing and deployment:

```xml
<profiles>
  <profile>
    <id>ossrh</id>
    <activation>
      <activeByDefault>false</activeByDefault>
    </activation>
    <!-- All plugins above are automatically active when using mvn release:perform -->
  </profile>
</profiles>
```

### 5. Example Complete POM.xml Snippet

Here's the complete structure for the root project element:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  
  <!-- Coordinates -->
  <groupId>org.flossware</groupId>
  <artifactId>curses-java</artifactId>
  <version>1.0</version>
  <packaging>jar</packaging>
  
  <!-- Project Metadata (Maven Central Required) -->
  <name>curses-java</name>
  <description>
    A modern Java terminal UI library that brings AWT-like components to the 
    terminal using ncurses. Built with Java 21 features including Virtual Threads, 
    Foreign Function API, Record Patterns, and Sealed Interfaces.
  </description>
  <url>https://github.com/FlossWare/curses-java</url>
  
  <!-- License -->
  <licenses>
    <license>
      <name>GNU General Public License v3.0 or later</name>
      <url>https://www.gnu.org/licenses/gpl-3.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  
  <!-- Developer Information -->
  <developers>
    <developer>
      <id>sfloess</id>
      <name>Scot P. Floess</name>
      <email>scot@flossware.org</email>
      <url>https://github.com/sfloess</url>
      <organization>FlossWare</organization>
      <organizationUrl>https://github.com/FlossWare</organizationUrl>
      <roles>
        <role>architect</role>
        <role>developer</role>
      </roles>
      <timezone>America/Chicago</timezone>
    </developer>
  </developers>
  
  <!-- Source Control Management -->
  <scm>
    <connection>scm:git:https://github.com/FlossWare/curses-java.git</connection>
    <developerConnection>scm:git:https://github.com/FlossWare/curses-java.git</developerConnection>
    <url>https://github.com/FlossWare/curses-java</url>
    <tag>HEAD</tag>
  </scm>
  
  <!-- Distribution to Maven Central -->
  <distributionManagement>
    <repository>
      <id>ossrh</id>
      <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
    <snapshotRepository>
      <id>ossrh</id>
      <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
    </snapshotRepository>
  </distributionManagement>
  
  <!-- Properties, Dependencies, Build sections follow... -->
</project>
```

---

## Release Process

### Manual Release Process (Step-by-Step)

This is the process for releasing manually. For automated CI/CD, see [CI/CD Automation](#cicd-automation).

#### Step 1: Prepare Local Environment

```bash
cd /home/sfloess/Development/github/FlossWare/curses-java

# Ensure you're on main branch
git checkout main
git pull origin main

# Verify all tests pass
mvn clean verify

# Check current version
grep "<version>" pom.xml | head -1
```

#### Step 2: Prepare Release (Automated)

The Maven Release Plugin automates this process:

```bash
# This will:
# 1. Verify no uncommitted changes
# 2. Verify no SNAPSHOT dependencies
# 3. Prompt for release version (default: remove -SNAPSHOT)
# 4. Prompt for SCM tag name
# 5. Prompt for next development version
# 6. Create release branch
# 7. Update pom.xml with release version
# 8. Commit and tag

mvn release:prepare \
  -DautoVersionSubmodules=true \
  -Dusername=YOUR_GITHUB_USERNAME \
  -Dpassword=YOUR_GITHUB_TOKEN
```

**When prompted:**
- Release version: `1.0` (just confirm the suggested version)
- SCM tag: `v1.0` (confirm)
- Next development version: `1.1-SNAPSHOT` (or as desired)
- Confirm: `Y`

**What happens:**
- Two commits are created locally (preparation and version update)
- One tag is created (`v1.0`)
- pom.xml shows `1.0` (release version)

#### Step 3: Build and Sign Artifacts

```bash
# Build with signing enabled
mvn clean package -Darguments="-Dgpg.passphrase=YOUR_GPG_PASSPHRASE"
```

Or with GPG agent (interactive):
```bash
# GPG will prompt for passphrase
mvn clean verify
```

**Verify artifacts were created:**
```bash
ls -la target/*.jar
# Should see:
# - curses-java-1.0.jar (main artifact)
# - curses-java-1.0-sources.jar (source code)
# - curses-java-1.0-javadoc.jar (API documentation)
# - curses-java-1.0.asc (signature)
# - curses-java-1.0-sources.jar.asc
# - curses-java-1.0-javadoc.jar.asc
```

#### Step 4: Deploy to Sonatype Staging

```bash
# This uploads to Sonatype's staging repository
# Still not publicly visible yet
mvn deploy \
  -Darguments="-Dgpg.passphrase=YOUR_GPG_PASSPHRASE"
```

**Expected output:**
```
[INFO] Uploading curses-java-1.0.jar to s01.oss.sonatype.org
[INFO] Uploading curses-java-1.0-sources.jar to s01.oss.sonatype.org
[INFO] Uploading curses-java-1.0-javadoc.jar to s01.oss.sonatype.org
[INFO] Uploading curses-java-1.0.pom to s01.oss.sonatype.org
```

#### Step 5: Review Staging Repository

Visit https://s01.oss.sonatype.org and log in with your Sonatype credentials:

1. Click **Staging Repositories** (left sidebar)
2. Find your repository (filter by name "curses-java" or group "org.flossware")
3. Click to select it
4. Review the **Contents** tab:
   - Verify all `.jar`, `-sources.jar`, `-javadoc.jar`, and `.asc` files are present
   - Verify correct version numbers
5. Click **Activity** tab to review validation results
6. If validation passes, click **Close** button

**What "Close" does:**
- Runs automated validation on all artifacts
- Verifies GPG signatures
- Checks that required files are present
- Checks metadata completeness

#### Step 6: Close and Release

If validation passes, close the repository and release it:

1. Click the **Release** button in the staging repository view
2. Sonatype will show: "Repository has been released"
3. Artifacts are now queued for promotion to Maven Central

Alternatively, use Maven:
```bash
mvn nexus-staging:release \
  -DstagingRepositoryId=ORG_FLOSSWARE_INITIAL_RELEASE_ID
```

Or for automatic release after close (if configured):
```bash
mvn nexus-staging:release-skip-close \
  -DstagingRepositoryId=ORG_FLOSSWARE_INITIAL_RELEASE_ID
```

#### Step 7: Push to GitHub

```bash
# Push the release commits and tag
git push origin main --tags

# Verify tag was pushed
git tag -l v1.0
```

#### Step 8: Create GitHub Release

```bash
# Using GitHub CLI (if installed)
gh release create v1.0 \
  --title "Release 1.0" \
  --notes "See https://github.com/FlossWare/curses-java/releases/tag/v1.0 for details"

# Manually: Visit https://github.com/FlossWare/curses-java/releases
# Click "Draft a new release"
# Select tag v1.0
# Add release notes
# Publish
```

### Troubleshooting Manual Release

**If release:prepare fails:**

```bash
# Revert to safe state
mvn release:rollback

# Clean up backup files
rm -f pom.xml.releaseBackup
rm -f pom.xml.tag
rm -f pom.xml.next
rm -f release.properties

# Fix issues and try again
```

**If staging validation fails:**

1. Check the **Activity** tab on https://s01.oss.sonatype.org
2. Common failures:
   - Missing source files: Run `mvn clean package -Darchive.skip`
   - Missing signatures: Ensure GPG is working
   - Invalid POM: Check metadata (see [POM.xml Configuration](#pomxml-configuration))
3. Drop the staging repository and retry

---

## CI/CD Automation

Automate Maven Central publishing with GitHub Actions. This ensures consistent, repeatable releases.

### Setup: GitHub Secrets

Store credentials securely in GitHub Actions:

1. Go to **Settings → Secrets and variables → Actions**
2. Add these repository secrets:

| Secret Name | Value | Source |
|---|---|---|
| `GPG_PRIVATE_KEY` | Content of GPG private key | `gpg2 --export-secret-keys --armor YOUR_KEY_ID` |
| `GPG_PASSPHRASE` | Your GPG passphrase | Store securely |
| `SONATYPE_USERNAME` | Sonatype Jira username | From prerequisites |
| `SONATYPE_PASSWORD` | Sonatype Jira password (or token) | From prerequisites |

**Export GPG private key:**
```bash
# Export ASCII-armored private key (can be multi-line in GitHub)
gpg2 --export-secret-keys --armor YOUR_KEY_ID > private-key.asc

# Copy contents of private-key.asc to GitHub secret
cat private-key.asc
```

**Create Sonatype token (optional but recommended):**

1. Visit https://s01.oss.sonatype.org/
2. Click **Profile** → **User Token**
3. Copy token and use as `SONATYPE_PASSWORD` in GitHub

### Create Release Workflow

Create file: `.github/workflows/maven-central-release.yml`

```yaml
name: Maven Central Release

on:
  workflow_dispatch:
    inputs:
      version:
        description: 'Release version (e.g., 1.0, 1.1, 2.0)'
        required: true
        type: string
      next_version:
        description: 'Next development version (e.g., 1.1-SNAPSHOT)'
        required: true
        type: string

jobs:
  release:
    name: Release to Maven Central
    runs-on: ubuntu-latest
    permissions:
      contents: write
      packages: write

    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Full history for git operations
          token: ${{ secrets.GITHUB_TOKEN }}

      - name: Setup JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'
          server-id: 'ossrh'
          server-username: 'SONATYPE_USERNAME'
          server-password: 'SONATYPE_PASSWORD'
          gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
          gpg-passphrase: 'GPG_PASSPHRASE'

      - name: Install ncurses (for tests)
        run: sudo apt-get update && sudo apt-get install -y libncurses-dev

      - name: Configure Git
        run: |
          git config --global user.email "ci@flossware.org"
          git config --global user.name "FlossWare CI"

      - name: Update version in pom.xml
        run: |
          mvn versions:set -DnewVersion=${{ github.event.inputs.version }} -DgenerateBackupPoms=false
          mvn versions:commit

      - name: Run tests
        run: mvn clean verify

      - name: Build and sign artifacts
        run: |
          mvn clean package \
            -Dgpg.passphrase=${{ secrets.GPG_PASSPHRASE }}
        env:
          SONATYPE_USERNAME: ${{ secrets.SONATYPE_USERNAME }}
          SONATYPE_PASSWORD: ${{ secrets.SONATYPE_PASSWORD }}
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}

      - name: Deploy to Maven Central
        run: |
          mvn deploy \
            -Dgpg.passphrase=${{ secrets.GPG_PASSPHRASE }} \
            -DskipTests
        env:
          SONATYPE_USERNAME: ${{ secrets.SONATYPE_USERNAME }}
          SONATYPE_PASSWORD: ${{ secrets.SONATYPE_PASSWORD }}
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}

      - name: Commit version change
        run: |
          git add pom.xml
          git commit -m "chore: Release version ${{ github.event.inputs.version }}"
          git tag -a v${{ github.event.inputs.version }} -m "Release ${{ github.event.inputs.version }}"

      - name: Push commits and tags
        run: |
          git push origin main
          git push origin --tags

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          tag_name: v${{ github.event.inputs.version }}
          name: Release ${{ github.event.inputs.version }}
          draft: false
          prerelease: false
          generate_release_notes: true
          files: |
            target/curses-java-*.jar
            target/jcurses-sbom.*
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      - name: Bump to next development version
        run: |
          mvn versions:set -DnewVersion=${{ github.event.inputs.next_version }} -DgenerateBackupPoms=false
          mvn versions:commit
          git add pom.xml
          git commit -m "chore: Bump version to ${{ github.event.inputs.next_version }}"
          git push origin main

      - name: Print Release Summary
        run: |
          echo "✅ Release ${{ github.event.inputs.version }} completed!"
          echo ""
          echo "Artifacts deployed to:"
          echo "https://s01.oss.sonatype.org/content/repositories/releases/org/flossware/curses-java/${{ github.event.inputs.version }}/"
          echo ""
          echo "Will appear in Maven Central within 2-10 minutes:"
          echo "https://central.sonatype.com/artifact/org.flossware/curses-java/${{ github.event.inputs.version }}"
          echo ""
          echo "GitHub Release:"
          echo "https://github.com/FlossWare/curses-java/releases/tag/v${{ github.event.inputs.version }}"
```

### Using the Automated Release Workflow

1. Go to **Actions** → **Maven Central Release**
2. Click **Run workflow**
3. Enter:
   - Release version: `1.0`
   - Next development version: `1.1-SNAPSHOT`
4. Click **Run workflow**
5. Monitor the workflow run in the **Actions** tab
6. Once complete, verify artifacts on Maven Central

### Alternative: Automated Release on Tag Push

For fully automatic releases on git tag, create: `.github/workflows/auto-release.yml`

```yaml
name: Auto Release on Tag

on:
  push:
    tags:
      - 'v[0-9]+.[0-9]+.[0-9]+'  # Only semantic version tags

jobs:
  release:
    name: Release to Maven Central
    runs-on: ubuntu-latest
    permissions:
      contents: write

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'
          server-id: 'ossrh'
          server-username: 'SONATYPE_USERNAME'
          server-password: 'SONATYPE_PASSWORD'
          gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
          gpg-passphrase: 'GPG_PASSPHRASE'

      - name: Install ncurses
        run: sudo apt-get update && sudo apt-get install -y libncurses-dev

      - name: Extract version from tag
        id: tag
        run: echo "version=${GITHUB_REF#refs/tags/v}" >> $GITHUB_OUTPUT

      - name: Build and test
        run: mvn clean verify

      - name: Deploy to Maven Central
        run: mvn deploy -DskipTests
        env:
          SONATYPE_USERNAME: ${{ secrets.SONATYPE_USERNAME }}
          SONATYPE_PASSWORD: ${{ secrets.SONATYPE_PASSWORD }}
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          name: Release ${{ steps.tag.outputs.version }}
          draft: false
          prerelease: false
          generate_release_notes: true
          files: |
            target/curses-java-*.jar
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

**Usage:**
```bash
# Simply tag and push
git tag v1.0
git push origin v1.0
# Workflow runs automatically and deploys to Maven Central
```

---

## Common Pitfalls & Troubleshooting

### 1. GPG Signature Verification Fails

**Error:**
```
[ERROR] GPG signature validation failed
[ERROR] Could not verify integrity of artifact
```

**Causes & Solutions:**

```bash
# Check GPG key is available
gpg2 --list-keys YOUR_KEY_ID

# Verify key is in keyservers
gpg2 --keyserver hkp://pool.sks-keyservers.net --recv-keys YOUR_KEY_ID

# Re-export and re-distribute public key
gpg2 --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID
gpg2 --keyserver hkp://keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Test local signing
echo "test" > test.txt
gpg2 --sign test.txt  # Should prompt for passphrase
```

### 2. Missing Required Files

**Error:**
```
MISSING or INVALID: Missing required file in staging repository
```

**Common missing files:**
- `-sources.jar` - Ensure `maven-source-plugin` is configured
- `-javadoc.jar` - Ensure `maven-javadoc-plugin` is configured
- `.asc` signatures - Ensure `maven-gpg-plugin` is configured

**Fix:**
```bash
# Verify all files are built locally
mvn clean package

ls target/curses-java-*.jar
ls target/curses-java-*.asc

# Should see all of these:
# curses-java-1.0.jar
# curses-java-1.0-sources.jar
# curses-java-1.0-javadoc.jar
# curses-java-1.0.pom
# curses-java-1.0.jar.asc
# curses-java-1.0-sources.jar.asc
# curses-java-1.0-javadoc.jar.asc
# curses-java-1.0.pom.asc
```

### 3. Invalid POM Metadata

**Error:**
```
Metadata validation failure: Incomplete POM
```

**Required fields in pom.xml:**
- `<name>` - Project name ✓
- `<description>` - Project description ✓
- `<url>` - Project URL ✓
- `<licenses>` - At least one license ✓
- `<developers>` - At least one developer ✓
- `<scm>` - SCM connection details ✓

**Verification:**
```bash
# Validate POM structure
mvn validate

# Extract POM to verify
mvn help:describe -Dartifact=org.flossware:curses-java:1.0:jar
```

### 4. Connection to Sonatype Fails

**Error:**
```
[ERROR] Failed to deploy artifacts: Could not transfer artifact
Could not resolve host: s01.oss.sonatype.org
```

**Solutions:**
```bash
# Check network connectivity
ping s01.oss.sonatype.org

# Verify Sonatype is not down
# Check https://status.sonatype.com/

# Test HTTPS connection
curl -v https://s01.oss.sonatype.org/

# Verify credentials in settings.xml
cat ~/.m2/settings.xml | grep -A2 "ossrh"
```

### 5. Staging Repository Won't Close

**Error:**
```
Error during close of repository: 
Nexus Staging Maven Plugin: Invalid repository state
```

**Most common causes:**

1. **Javadoc generation failed:**
```bash
# Regenerate javadoc locally
mvn javadoc:jar

# Check for javadoc warnings/errors
mvn clean package 2>&1 | grep -i "javadoc"
```

2. **Missing files:**
```bash
# Drop repository and try again
mvn nexus-staging:drop

# Rebuild locally
mvn clean package

# Redeploy
mvn deploy
```

3. **Validation rules not met:**
   - Visit https://s01.oss.sonatype.org
   - Click **Staging Repositories**
   - Select your repository
   - Click **Activity** tab
   - Expand messages to see specific failures
   - Fix and redeploy

### 6. Version Not in Correct Format

**Error:**
```
Enforcer validation failed: Version must be X.Y format
```

**Fix:**
```bash
# Current version is likely SNAPSHOT or invalid
mvn versions:set -DnewVersion=1.0 -DgenerateBackupPoms=false

# Verify
grep "<version>" pom.xml | head -1
# Should show: <version>1.0</version>
```

### 7. Token Expiration Issues

**Error:**
```
[ERROR] Unauthorized: return code 401
```

**Solutions:**
```bash
# If using password, verify it's correct
# Sonatype added support for Personal Access Tokens (better security)

# Generate token at https://s01.oss.sonatype.org/
# Profile → User Token → Generate Access Token
# Use token instead of password in settings.xml

# Update GitHub secrets if using CI/CD
# Settings → Secrets → Update SONATYPE_PASSWORD with new token
```

---

## Post-Publishing

### Timeline: When Does It Appear?

1. **Immediately after release:** Artifacts on https://s01.oss.sonatype.org/
2. **2-10 minutes:** Syncs to Maven Central
3. **24 hours:** Available in search indexes

### Verify Publication

**Check on Sonatype:**
```bash
curl https://s01.oss.sonatype.org/content/repositories/releases/org/flossware/curses-java/1.0/curses-java-1.0.jar \
  -I  # Check headers only
```

**Check on Maven Central:**
```bash
# Give it 5-10 minutes after release

# Search the public index
curl https://central.sonatype.com/api/v1/search/artifact?q=curses-java | jq .

# Or visit (requires browser):
# https://search.maven.org/artifact/org.flossware/curses-java/1.0/jar
```

**Test local installation:**
```bash
# Create test project
mkdir test-curses
cd test-curses

# Create pom.xml with curses-java dependency
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>test</groupId>
  <artifactId>test-curses</artifactId>
  <version>1.0</version>
  
  <dependencies>
    <dependency>
      <groupId>org.flossware</groupId>
      <artifactId>curses-java</artifactId>
      <version>1.0</version>
    </dependency>
  </dependencies>
</project>
EOF

# Test download from Maven Central
mvn dependency:resolve
```

### How to Use in Other Projects

**Maven:**
```xml
<dependency>
  <groupId>org.flossware</groupId>
  <artifactId>curses-java</artifactId>
  <version>1.0</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'org.flossware:curses-java:1.0'
```

**SBT:**
```scala
libraryDependencies += "org.flossware" % "curses-java" % "1.0"
```

**Apache Ivy:**
```xml
<dependency org="org.flossware" name="curses-java" rev="1.0"/>
```

### Documentation & Discovery

Add to project documentation (README.md):

```markdown
## Installation

### Maven
```xml
<dependency>
  <groupId>org.flossware</groupId>
  <artifactId>curses-java</artifactId>
  <version>1.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'org.flossware:curses-java:1.0'
```

View on [Maven Central](https://search.maven.org/artifact/org.flossware/curses-java/1.0/jar)
```

### Future Releases

After the first release to Maven Central:

1. **Subsequent releases are easier** - Sonatype already verified your GroupId
2. **Follow the same process:**
   - Bump version in pom.xml
   - Run tests
   - Create git tag
   - Deploy to staging
   - Close and release repository
3. **Use the CI/CD workflow** for consistency and automation

**Example for 1.1 release:**
```bash
# Manually or via workflow
mvn versions:set -DnewVersion=1.1 -DgenerateBackupPoms=false
git add pom.xml
git commit -m "chore: Release 1.1"
git tag v1.1
git push origin main --tags

# Then deploy via workflow or manually with mvn deploy
```

---

## Summary Checklist

Before publishing, verify:

- [ ] Sonatype account created and approved
- [ ] GPG key generated and distributed to keyservers
- [ ] `pom.xml` has all required metadata (name, description, license, developers, scm)
- [ ] `pom.xml` points to Sonatype OSSRH repository
- [ ] Maven plugins configured (source, javadoc, gpg, nexus-staging)
- [ ] `~/.m2/settings.xml` has Sonatype credentials
- [ ] GitHub secrets configured (GPG key, Sonatype credentials)
- [ ] All tests pass locally
- [ ] Release workflow tested
- [ ] Version format is X.Y (release) or X.Y-SNAPSHOT (development)

**First release checklist:**
- [ ] Request domain verification for `org.flossware` (see prerequisites)
- [ ] Wait for Sonatype approval
- [ ] Follow manual release process first time
- [ ] Verify artifacts on Maven Central
- [ ] Test `mvn dependency:resolve` in another project
- [ ] Automated workflow ready for future releases

---

## Additional Resources

- **Sonatype OSSRH Guide:** https://central.sonatype.org/publish/publish-guide/
- **Maven Central Requirements:** https://central.sonatype.org/publish/requirements/
- **Maven Release Plugin:** https://maven.apache.org/maven-release/maven-release-plugin/
- **GPG Guide:** https://www.gnupg.org/gph/en/manual/
- **Nexus Repository Manager:** https://help.sonatype.com/repomanager3

---

**Last Updated:** 2026-06-12  
**For curses-java version:** 1.0+

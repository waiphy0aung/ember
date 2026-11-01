# Ember: instructions for Claude

Ember is a personal Android todo app where every task is a flame that burns out if you don't act on it. The owner does not write code: Claude builds everything, the owner tests builds on their phone and merges.

The full specification is the "Ember — Requirements" doc (tabs: Requirements, Data model, Flows, Architecture, Coding standards, Operations, Build plan): https://claude.ai/artifact/H725vpLEhzFfvmssEH7NWk. When this file and the doc disagree, the doc wins; fix this file in the same pull request.

## Non-negotiables

- **Correct data first.** Never risk losing or corrupting a flame. No destructive migrations, ever.
- **One write path.** Only the repository layer writes, one transaction per action, with its `event` row in the same transaction. Alarms and the widget update after the commit.
- **One time type.** `Instant` and `Duration` in code; `Long` milliseconds only at the Room boundary. "now" comes from the injected `java.time.Clock`, read once per action. Never call `Instant.now()` or `System.currentTimeMillis()` outside `AppContainer`.
- **Rules live in the flame clock** (`clock/`), pure Kotlin with no Android imports, fully unit-tested.
- **Words:** flame, lifespan, why, area (a fire), star, ash, relight, rekindle, sweep, hearth. Never "task", "todo" or "item" in code or UI.
- **Small and fast:** release APK under 5 MB; no network permission; no Firebase, analytics, DI frameworks or networking libraries.
- **All visible text in string resources; all colours and sizes from `EmberTheme`.**

## Layers (dependencies point down only)

`MainActivity`, widget, receivers → UI (Compose screens) → ViewModels → repository → Room DAOs. Platform pieces (`AlarmScheduler`, `Notifier`, `WidgetUpdater`) sit behind interfaces. Package layout and rules: Architecture tab.

## Build and check

- JDK 21. Gradle wrapper (9.7.1) with Android Gradle Plugin 9 (built-in Kotlin).
- `./gradlew check :app:assemblePreview` is what CI runs on every pull request (lint with warnings as errors, unit tests, Kotlin warnings as errors).
- Build types: `debug` (`.debug`), `release` (the real app), `preview` (`.preview`, "Ember Preview", separate data, violet icon).
- Signing, three workflows:
  - Gradle never signs anything and never sees a key: release and preview are built unsigned, `scripts/inspect-apk.sh` checks the package, version, target SDK and Ember's permission allowlist on a machine with no keys, and `scripts/sign-apk.sh` signs only that exact file (matched by sha256) with apksigner.
  - `ci.yml` runs pull request code with **no secrets** and a read-only token, and uploads an unsigned Ember Preview with its version name.
  - `publish-preview.yml` runs main's copy via `workflow_run`, signs that APK with the **preview-only key** (`EMBER_PREVIEW_KEYSTORE_BASE64`, `EMBER_PREVIEW_SIGNING_PASSWORD`, alias `ember-preview`) and publishes a pre-release.
  - `release.yml` runs only on main in three jobs on separate machines: `build` (Gradle, no secrets), `inspect` (no secrets) and `sign-and-publish` (apksigner only, **release key**: `EMBER_KEYSTORE_BASE64`, `EMBER_SIGNING_PASSWORD`, alias `ember`). The version always comes from main's `VERSION` plus the run number, never from build output.
  - Raising `targetSdk` also means raising `EXPECTED_TARGET_SDK` in both signing workflows.
  - Signing jobs use pinned build-tools 36.0.0 and must never run Gradle or any project build code.
  - Keys live in GitHub Environments limited to `main`: `release` (release key) and `preview` (preview key); no signing secret exists at repository level. Never remove `environment:` from a signing job.
  - Never give a workflow that runs pull request code access to a secret. Claude cannot read or write these secrets; only the owner can. Never commit a keystore or password.
- Versions: CI sets `EMBER_VERSION_CODE` and `EMBER_VERSION_NAME`.
- Claude's cloud workspace may not be able to download Gradle or the Android SDK; if a local build fails on network, rely on CI and read its logs with `gh run view --log-failed`.

## How to work

1. One small task per branch and pull request: one row of the Writes table, one screen, or one flow. Name it in the PR.
2. Tests first for rules: flame clock and repository changes come with tests in the same commit.
3. Before opening or updating a pull request, run the three sub-agents in `.claude/agents/` (in parallel; they start fresh and have not seen your work): `code-reviewer`, `test-engineer`, `security-reviewer`. Fix every blocker and major they report, then run them again until all three PASS. Paste their final verdicts into the Checks section, and use the test engineer's phone steps for "Try it on your phone".
4. Fill in every section of the pull request template in plain, non-technical words. The owner reads What changed, Risks and Try it on your phone; they do not read code.
5. Wait for CI to be green; fix failures rather than skipping checks.
6. Anything touching the database schema or user data must be listed under Risks and needs the owner's explicit OK in chat.
7. The owner's phone is a Samsung Galaxy S24+ (One UI). Samsung puts unused apps to sleep, so alarm and widget changes must be checked against its battery settings.
8. Commits follow Conventional Commits: `feat(hearth): …`, `fix(sweep): …`, `test(clock): …`, `ci: …`, `docs: …`.

## Definition of done

Does what the doc says, including other paths and errors · tests pass · no hardcoded text or colours · previews for new components · schema change has migration and test · APK under 5 MB · code-reviewer, test-engineer and security-reviewer all PASS · owner tried it in Ember Preview · doc updated if a rule changed.

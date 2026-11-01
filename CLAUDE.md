# Ember: instructions for Claude

Ember is a personal Android todo app where every task is a flame that burns out if you don't act on it. The owner does not write code: Claude builds everything, the owner tests builds on their phone and merges.

The full specification is the "Ember — Requirements" doc (tabs: Requirements, Data model, Flows, Architecture, Coding standards, Operations): https://claude.ai/artifact/H725vpLEhzFfvmssEH7NWk. When this file and the doc disagree, the doc wins; fix this file in the same pull request.

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
- `./gradlew :app:lintRelease :app:testDebugUnitTest :app:assemblePreview` is what CI runs on every pull request.
- Build types: `debug` (`.debug`), `release` (the real app), `preview` (`.preview`, "Ember Preview", separate data, violet icon).
- Signing: CI provides `EMBER_KEYSTORE_PATH`, `EMBER_KEYSTORE_PASSWORD`, `EMBER_KEY_ALIAS`, `EMBER_KEY_PASSWORD`. Without them, release and preview fall back to the debug key (local builds only). Never commit a keystore or password.
- Versions: CI sets `EMBER_VERSION_CODE` and `EMBER_VERSION_NAME`.
- Claude's cloud workspace may not be able to download Gradle or the Android SDK; if a local build fails on network, rely on CI and read its logs with `gh run view --log-failed`.

## How to work

1. One small task per branch and pull request: one row of the Writes table, one screen, or one flow. Name it in the PR.
2. Tests first for rules: flame clock and repository changes come with tests in the same commit.
3. Fill in every section of the pull request template in plain, non-technical words. The owner reads What changed, Risks and Try it on your phone; they do not read code.
4. Wait for CI to be green; fix failures rather than skipping checks.
5. Anything touching the database schema or user data must be listed under Risks and needs the owner's explicit OK in chat.
6. Commits follow Conventional Commits: `feat(hearth): …`, `fix(sweep): …`, `test(clock): …`, `ci: …`, `docs: …`.

## Definition of done

Does what the doc says, including other paths and errors · tests pass · no hardcoded text or colours · previews for new components · schema change has migration and test · APK under 5 MB · reviewer Claude has no open findings · owner tried it in Ember Preview · doc updated if a rule changed.

<!--
  - SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
  - SPDX-License-Identifier: MIT
-->
# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Nextcloud Contribution Policy

All contributions generated or assisted by this agent must fully comply with:

- **[AI Contribution Policy](https://github.com/nextcloud/.github/blob/master/AI_POLICY.md)** — the primary reference for AI-specific rules, covering disclosure, author accountability, communication, security, licensing, code quality, and autonomous agent behavior.
- **[Contribution Guidelines](https://github.com/nextcloud/.github/blob/master/CONTRIBUTING.md)** — covering testing requirements, the Developer Certificate of Origin (DCO), license headers, conventional commits, and translations. These apply in full to all contributions regardless of how they were produced.

### What this agent must always do

- Add an `Assisted-by: AGENT_NAME:MODEL_VERSION` git trailer to every commit containing AI-assisted content.
- Ensure every pull request includes a disclosure of AI tool use in the PR description.
- Produce focused, scoped pull requests that address exactly one concern. Do not touch unrelated files or introduce incidental refactors.
- Verify all dependencies against actual package registries before suggesting them. Do not use hallucinated or unverified package names.
- Explicitly inform the contributor when any action they are about to take, or have taken, would violate the AI Contribution Policy or the Contribution Guidelines. Do not silently proceed. State which rule is at risk and what the contributor should do instead.
- Warn the contributor if a pull request is growing too large. A PR approaching several thousand lines of changed code is a signal that it should be split into smaller, focused PRs. Suggest a logical split before the PR is opened, not after.
- Recommend opening a ticket for discussion before starting implementation whenever a feature or change is sufficiently complex — for example when it touches multiple subsystems, requires architectural decisions, or the right approach is not yet clear. A ticket allows maintainers and the contributor to align on direction before code is written, avoiding wasted effort on a PR that may be rejected or require fundamental rework.

### What this agent must never do

- Open issues, submit pull requests, post review comments, or send security reports autonomously. Every contribution must be reviewed and submitted by a human.
- Add `Signed-off-by` tags to commits. Only the human contributor can certify the Developer Certificate of Origin.
- Generate or submit security reports without independent human verification. Report verified vulnerabilities via [HackerOne](https://hackerone.com/nextcloud), not as GitHub issues.
- Write PR descriptions, review comments, or issue reports on behalf of the contributor. These must be in the contributor's own words.
- Fully automate the resolution of issues labeled [`good first issue`](https://github.com/issues?q=org%3Anextcloud+label%3A%22good+first+issue%22) or similar beginner-friendly labels.
- Submit code that has not been reviewed and cleaned up by the contributor. Dead code, redundant logic, excessive comments, and unrelated changes must be removed before submission.

---

## Build & Development Commands

All commands use the Gradle wrapper (`./gradlew` on Linux/macOS, `gradlew.bat` on Windows).

```bash
# Build
./gradlew assembleDebug

# Run all checks (spotless formatting, detekt static analysis, Android lint)
./gradlew spotlessKotlinCheck detekt lint

# Auto-fix formatting
./gradlew spotlessApply

# Run tests
./gradlew test

# Run a single test class
./gradlew :ui:test --tests "com.nextcloud.android.common.ui.SomeTest"

# Clean build
./gradlew clean assembleDebug
```

The analysis wrapper script (`scripts/analysis/analysis-wrapper.sh`) runs `spotlessKotlinCheck`, `detekt`, and `lint` in sequence — same as what CI runs on PRs.

After finishing code changes, run `./gradlew detekt spotlessKotlinCheck` and fix any new violations before considering the task done.

## Commits & Pull Requests

- Commit messages must follow the [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/#specification) specification — e.g. `feat(ui): add chip theming support`, `fix(color): correct HSL lightness calculation`.
- All PRs target `main`. Backports use `/backport to stable-X.Y` in a PR comment.
- Every commit made with AI assistance must include an `Assisted-by` trailer:

  ```
  Assisted-by: Claude Code:claude-sonnet-4-6
  ```

## Code Style

- **Line length:** 120 characters (150 for YAML)
- **Indentation:** 4 spaces
- **Trailing commas:** disabled
- **Import ordering:** custom (default ktlint rule disabled via `.editorconfig`)
- **Trailing newline:** every file must end with exactly one empty line — no more, no less
- **One type per file:** do not implement multiple classes/interfaces/objects in a single source file
- **No decorative comments:** avoid section-divider comments like `// ── Title ───`, `// ------`, `// ======`
- **No magic numbers:** extract constants with descriptive names
- **Fail fast:** prefer early returns over nested if-else chains
- **State modeling:** use enums or sealed classes instead of multiple boolean flags
- **Resources:** avoid hardcoded strings, colors, and dimensions — use Android resource references

License headers are enforced by the REUSE tooling. Every new source file must carry an SPDX MIT header:

Kotlin/Java:
```kotlin
/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: <year> Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */
```

XML:
```xml
<!--
  ~ Nextcloud Android Common Library
  ~
  ~ SPDX-FileCopyrightText: <year> Nextcloud GmbH and Nextcloud contributors
  ~ SPDX-License-Identifier: MIT
-->
```

Formatting is enforced via Spotless (ktlint under the hood). Run `spotlessApply` before committing to avoid CI failures.

## Design

- Follow Material Design 3 guidelines.
- Follow the [Nextcloud wording guidelines](https://docs.nextcloud.com/server/latest/developer_manual/design/foundations.html#wording) in addition to Material Design wording guidance.
- Ensure all UI works in both light and dark theme.
- Use `viewThemeUtils` (the theming utilities in `:ui`) when applying server primary colors — do not hardcode colors.

## Translations

Only modify `values/strings.xml`. Never edit localized `values-*/strings.xml` files — those are managed via Transifex.

## Architecture

This is a **theming/UI library** for Nextcloud Android apps, published via JitPack as two artifacts: `com.github.nextcloud.android-common:core` and `com.github.nextcloud.android-common:ui`.

### Module layout

| Module | Type | Purpose |
|---|---|---|
| `:core` | Android library | Minimal shared utilities |
| `:ui` | Android library | Theming engine — primary module |
| `:material-color-utilities` | Java library | Material 3 HCT color algorithm (no Android deps) |
| `:sample` | Android app | Usage demo; not published |

Dependency graph: `sample → ui → {core, material-color-utilities}`

### Theming engine (`:ui`)

The library's job is to take a server-supplied primary color and apply a coherent Material Design 3 color scheme across all Android view types. The flow:

1. **`ServerTheme`** (interface) — represents a server-provided palette (primary, element, text colors).
2. **`MaterialSchemesImpl`** — uses the HCT color model from `:material-color-utilities` to generate light and dark `DynamicScheme` objects from the primary color.
3. **`ViewThemeUtilsBase`** — base class for all theming utilities; picks light vs. dark scheme based on the current `Context`.
4. **Concrete theming utilities** — each targets a different view layer:
   - `AndroidViewThemeUtils` — native views (TextView, status bar, etc.)
   - `AndroidXViewThemeUtils` — AndroidX components
   - `MaterialViewThemeUtils` — Material Design 3 components (buttons, chips, toolbars)
   - `DialogViewThemeUtils` — dialog backgrounds
   - `SwitchColorUtils` — switch/toggle theming
5. **`SchemeExtensions`** — converts a `DynamicScheme` into a Compose `ColorScheme` for Jetpack Compose surfaces.

**Dependency injection:** Dagger 2 is used throughout `:ui`. Inject the theming utilities rather than constructing them directly.

### Key patterns

- Theming utilities follow the pattern: receive a `MaterialSchemes` (or `ViewThemeUtilsBase`) via DI, call a `themeXxx(view, scheme)` method.
- `ColorUtil` handles color parsing, HSL lightness adjustment, and opacity — use it rather than raw Android color APIs.
- `ColorStateListUtils` creates themed `ColorStateList` instances for pressed/focused/disabled states.
- `PlatformThemeUtil` detects the system dark-mode state; do not duplicate this logic.

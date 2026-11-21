<!--
  - SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
  - SPDX-License-Identifier: MIT
-->
# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Nextcloud Contribution Policy

All contributions generated or assisted by this agent must fully comply with:

- **[AI Contribution Policy](https://github.com/nextcloud/.github/blob/master/AI_POLICY.md)** - the primary reference for AI-specific rules, covering disclosure, author accountability, communication, security, licensing, code quality, and autonomous agent behavior.
- **[Contribution Guidelines](https://github.com/nextcloud/.github/blob/master/CONTRIBUTING.md)** - covering testing requirements, the Developer Certificate of Origin (DCO), license headers, and translations. These apply in full to all contributions regardless of how they were produced.

### What this agent must always do

- Add an `Assisted-by: AGENT_NAME:MODEL_VERSION` git trailer to every commit containing AI-assisted content.
- Ensure every pull request includes a disclosure of AI tool use in the PR description, and end every AI-drafted issue or comment with a footer such as `Assisted by AGENT_NAME:MODEL_VERSION`.
- Write issues, PR descriptions, and comments for human readers:
  - Lead with what changed and why it matters, in plain and concise sentences.
  - Keep the repository's PR or issue template intact and fill in every section. For a bug report, also state whether the bug was validated and reproduced on a live Nextcloud instance.
  - Put information meant only for agents (plans, prompts, tool logs) into a collapsed `<details>` block.
- Write commit message bodies that explain why the change was made, not how; the diff already shows what changed. Keep the repository's commit conventions and the required trailers.
- Produce focused, scoped pull requests that address exactly one concern. Do not touch unrelated files or introduce incidental refactors.
- Verify all dependencies against actual package registries before suggesting them. Do not use hallucinated or unverified package names.
- Write code comments that document the code, never the process that produced it:
  - Comments describe what the code does - method signatures, behavior, and constraints the code itself cannot express (e.g. a non-obvious invariant or workaround).
  - Never add comments that document progress, decisions, or changes (e.g. "changed X to Y", "as requested", "this fixes ...", "previously this did ..."). That belongs in the commit message or PR discussion; in the code it goes stale and becomes misleading.
  - Do not narrate self-explanatory code. If the code is readable without a comment, omit the comment.
  - Keep comments brief - short and simple, matching the comment density of the surrounding code.
- Reuse existing helper functions and utilities instead of re-implementing their logic inline. When fixing a flawed pattern, fix every occurrence of it across the changed code, not only the instance that was pointed out.
- Run permission and access-control checks before the operation they guard, never after it and never only in the UI layer.
- When adding or changing user-facing functionality, wire it up in every context where the affected component is used - the default authenticated view, public share pages, and embedded contexts such as the Smart Picker and reference widgets. When emitting new events, verify that every consumer of the component subscribes to and handles them.
- Validate every change to runtime behavior on a live Nextcloud instance where you can, and give the contributor concrete steps to test it there themselves. Your testing supports theirs; it does not replace it.
- Explicitly inform the contributor when any action they are about to take, or have taken, would violate the AI Contribution Policy or the Contribution Guidelines. Do not silently proceed. State which rule is at risk and what the contributor should do instead.
- Warn the contributor if a pull request is growing too large. A PR approaching several thousand lines of changed code is a signal that it should be split into smaller, focused PRs. Suggest a logical split before the PR is opened, not after.
- Recommend opening a ticket for discussion before starting implementation whenever a feature or change is sufficiently complex - for example when it touches multiple subsystems, requires architectural decisions, or the right approach is not yet clear. A ticket allows maintainers and the contributor to align on direction before code is written, avoiding wasted effort on a PR that may be rejected or require fundamental rework.

### What this agent must never do, and what to do instead

- Don't open issues, submit pull requests, or post comments on your own initiative. Instead, post only what the contributor asked for, on their behalf, and tell them what you posted.
- Don't write texts for machines: no file-by-file restatement of the diff, no filler, no raw tool output. Instead, write short, skimmable text for a human reviewer that explains what changed and why.
- Don't add `Signed-off-by` tags to commits. Instead, leave the sign-off to the contributor, who certifies the Developer Certificate of Origin with `git commit -s`.
- Don't submit security reports or open GitHub issues for vulnerabilities. Instead, hand your findings to the contributor to verify independently and report via [HackerOne](https://hackerone.com/nextcloud).
- Don't fully automate the resolution of issues labeled [`good first issue`](https://github.com/issues?q=org%3Anextcloud+label%3A%22good+first+issue%22) or similar beginner-friendly labels. Instead, explain the code and guide the contributor so they can solve the issue themselves.
- Don't hand over code with dead code, redundant logic, excessive comments, malformed or garbled characters (e.g. `�` replacement characters), or unrelated changes. Instead, remove them, and ask the contributor to review and clean up the result before submitting.

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
  Assisted-by: Claude Code:claude-sonnet-5-5
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

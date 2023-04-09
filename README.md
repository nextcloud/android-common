# Common library for Nextcloud Android clients

[![Android CI](https://github.com/nextcloud/android-common/workflows/Assemble/badge.svg)](https://github.com/nextcloud/android-common/actions)
[![GitHub issues](https://img.shields.io/github/issues/nextcloud/android-common.svg)](https://github.com/nextcloud/android-common/issues)
[![GitHub stars](https://img.shields.io/github/stars/nextcloud/android-common.svg)](https://github.com/nextcloud/android-common/stargazers)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## How to use

Add the dependency to the `build.gradle` file of your app, replace `X.X.X` with the [latest available version](https://github.com/nextcloud/android-common/releases).

```
implementation 'com.github.nextcloud:android-common:X.X.X'
```

### Theming

```java
// Define your MaterialSchemes and ColorUtil
final var schemes = MaterialSchemes.Companion.fromColor(color); // color should be fetched from the server capabilities
final var utils = new ColorUtil(context);

// Use them to instantiate ThemUtils you need
final var platform = new AndroidViewThemeUtils(schemes, colorUtil);
final var material = new MaterialViewThemeUtils(schemes, colorUtil);
final var androidx = new AndroidXViewThemeUtils(schemes, this.platform);
final var dialog = new DialogViewThemeUtils(schemes);

// Use the methods of the ThemeUtils to apply the actual theme:
material.themeTabLayout(tabLayout);
```

## Known users

- [Nextcloud files](https://github.com/nextcloud/android)
- [Nextcloud talk](https://github.com/nextcloud/talk-android/)
- [Nextcloud notes](https://github.com/nextcloud/notes-android) ([Planned](https://github.com/nextcloud/notes-android/issues/1648#issuecomment-1403223962))
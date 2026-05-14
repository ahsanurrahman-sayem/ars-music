# ArSync 🎵

A **lightweight, privacy-first Android music player** built with Kotlin, Jetpack Compose, and Media3/ExoPlayer.

[![CI](https://github.com/ahsanurrahman-sayem/arsync/actions/workflows/ci.yml/badge.svg)](https://github.com/ahsanurrahman-sayem/arsync/actions/workflows/ci.yml)

---

## Philosophy

> ArSync never scans your entire device. It only plays audio **you explicitly give it**.

- No `MANAGE_EXTERNAL_STORAGE` permission
- No MediaStore global crawling
- No analytics, trackers, or ads
- Scoped storage only — persistent URI references instead of copying files

---

## Features

| Feature | Status |
|---|---|
| Import via share sheet (`ACTION_SEND`) | ✅ |
| Import via SAF file picker | ✅ |
| Persistent URI permissions (no file copying) | ✅ |
| Background playback (foreground service) | ✅ |
| Lock screen / notification controls | ✅ |
| Bluetooth + wired headset support | ✅ |
| Smart audio focus (non-aggressive ducking) | ✅ |
| Per-player volume (independent of system volume) | ✅ |
| Shuffle & Repeat modes | ✅ |
| Favorites | ✅ |
| Recently Played | ✅ |
| Playlist management | ✅ |
| Sleep timer | ✅ |
| Dynamic theme from album art | ✅ |
| Embedded album artwork extraction | ✅ |
| Search | ✅ |
| Duplicate detection | ✅ |
| Material You dynamic colors (API 31+) | ✅ |

---

## Architecture

```
arsync/
├── app/                  — Activity, Navigation, DI root
├── core/                 — Pure domain models (Track, Playlist, RepeatMode…)
├── domain/               — Use cases + repository interfaces
├── data/                 — Import pipeline, URI handling, metadata extraction
├── storage/              — Room DB, DAOs, DataStore settings, ArtworkCache
├── player/               — ExoPlayer wrapper, PlayerController interface
├── background-service/   — MediaSessionService, receivers
├── ui/                   — Reusable Compose components, PaletteExtractor
└── shared-utils/         — Format helpers, coroutine dispatchers, permissions
```

Clean Architecture layers:

```
UI (ViewModels + Screens)
        ↓
    Domain (Use Cases)
        ↓
Data / Storage / Player (implementations)
        ↓
    Core (Models — no Android deps)
```

---

## Build Instructions

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 34

### Debug Build
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
```bash
# Set environment variables:
export KEYSTORE_PATH=keystore/arsync.jks
export KEYSTORE_PASSWORD=yourpassword
export KEY_ALIAS=youralias
export KEY_PASSWORD=yourkeypassword

./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

### Run Tests
```bash
./gradlew testDebugUnitTest          # unit tests
./gradlew connectedDebugAndroidTest  # instrumentation (needs emulator)
```

### Lint
```bash
./gradlew lint
```

---

## Release Process

1. Tag a commit: `git tag v1.0.0 && git push origin v1.0.0`
2. GitHub Actions `release.yml` builds a signed APK + AAB automatically
3. A GitHub Release is created with both artifacts attached

**Required GitHub Secrets:**

| Secret | Description |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded `.jks` keystore file |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias inside keystore |
| `KEY_PASSWORD` | Key password |

---

## Import Audio

ArSync appears in the Android share menu for all audio MIME types. You can also:

1. Open the app → tap **+** → pick a file via the system file picker
2. Use your file manager → long-press an audio file → Share → ArSync

Files are referenced by persistent URI permission — **never copied** unless the URI is inaccessible.

---

## License

MIT © com.ars / Sayem

# UserSettingsApp

Lab Task 11 — Shared Preferences User Settings Manager

## What it does
- Saves student settings with `SharedPreferences` in `AppSettings`
- Saves profile details with `SharedPreferences` in `ProfilePrefs`
- Restores saved data after app restart
- Supports:
  - Settings Dashboard
  - Saved Settings Viewer
  - Profile Setup screen

## Build / Run
Open the project in Android Studio and run the `app` module.

Or build from terminal:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Notes
- Preferences are saved with `.apply()`
- Up/back navigation is enabled on the viewer and profile screens
- The settings viewer shows an empty state until data is saved


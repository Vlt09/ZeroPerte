# 🥫 ZeroPerte

**ZeroPerte** is a native Android app that helps reduce food waste by tracking expiry dates of food stored at home, and alerting the user before expiration.

> 100% local app — no data ever leaves the device.

![App preview](preview.gif)

## ✨ Features

### Food management
- Add, edit, delete food items
- Capture expiry date via photo (OCR)
- Associated info: name, brand, purchase date, expiry date, category, quantity, free comment

### Alerts & notifications
- Configurable reminder X days before expiration
- Notification on the expiration day
- Works even when the app is closed
- Can be toggled globally or per item

### Visualization & search
- Visual indication of status (expired / expiring soon / OK)
- Sort by expiry date, category, alphabetical order

### Stock management & history
- Decrement quantity, duplicate an item with a new date
- History of deleted items, with restore option
- Statistics (total count, expired, expiring soon, breakdown by category)

## 🛠️ Tech stack

| Area | Tech |
| --- | --- |
| Language | Kotlin 2.2.20 |
| UI | Jetpack Compose, Material 3 |
| Dependency injection | Hilt |
| Persistence | Room |
| Architecture | MVVM, Repository pattern |
| Navigation | Navigation Compose (type-safe `@Serializable` routes) |
| Photo capture / OCR | CameraX + ML Kit Text Recognition |
| Forms | compose-form |
| Async | Kotlin Coroutines, StateFlow |
| Tests | JUnit, Room in-memory, Turbine |
| Build | AGP 8.13.2, KSP |

**minSdk: 26 (Android 8.0)**

## 🏗️ Architecture

Single-Activity app, layered architecture:

```
UI (Compose)  →  ViewModel (StateFlow)  →  Repository  →  Room (DAO / Entities)
```

- **Data layer**: Room entities, DAOs, TypeConverters, Repository pattern (`FoodRepository` / `DefaultFoodRepository`)
- **DI**: Hilt (`@HiltAndroidApp`, modules, `@Binds`, `@Singleton`)
- **ViewModel**: state exposed via `StateFlow` and sealed interfaces (`Loading` / `Empty` / `Content`), reactive filters combined with `combine()`
- **Navigation**: type-safe `@Serializable` routes, shared add/edit screen
- **Business logic**: dedicated `FoodStatusCalculator` for expiry status computation

## 🎨 Design

- Material 3 with a custom theme generated via Material Theme Builder
- Blue-teal primary color (`#1F6E6E`), chosen to avoid clashing with status colors (red/orange/green)
- Extended color scheme (`ExtendedColorScheme`) exposed via `CompositionLocal` for status colors (expired, expiring soon...)
- Dates formatted as `dd/MM/yyyy`, handled with `java.time.LocalDate`

## 🚧 Project status

Personal project in active development, used as a learning ground for modern Android/Kotlin development practices.

**Already in place:** data layer, DI, ViewModel + UI for the food list, type-safe navigation, OCR capture, basic unit tests.

**Coming up:**
- Settings screen
- Notifications (WorkManager)
- Sorting list by expiry date, category, alphabetical order
- Searching food by name
- Statistics and history

## 📦 Requirements

- Android Studio (latest stable)
- JDK 17+
- Android SDK with API 26 minimum

## 🚀 Installation

```bash
git clone <repo-url>
cd zeroperte
```

Open the project in Android Studio and let Gradle sync the dependencies.

## 📄 License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

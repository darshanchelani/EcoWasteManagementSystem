
# ♻️ EcoWaste Management

**A reward-based waste recycling mobile application for Android**

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Room](https://img.shields.io/badge/Room-039BE5?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)

---

## 📱 Project Overview

**EcoWaste Management** is a mobile application that encourages households to recycle waste by offering digital rewards (EcoCoins). Users can schedule waste pickups, track their recycling contributions, earn coins per kilogram of waste, and withdraw those coins as cash or coupons. The app connects users with waste collectors and provides a transparent, incentivised system for sustainable waste management.

This project was developed as part of the **Mobile Application Development** course by **Darshan (ID: 023-22-0372)**.

---

## ✨ Key Features

| Module | Description |
|--------|-------------|
| 🔐 **Authentication** | User registration, login, password reset, session management |
| 👤 **User Profile** | View/edit profile, waste contribution history, EcoCoin balance |
| 📦 **Pickup Request** | Schedule pickup by waste type (plastic, paper, e-waste, etc.), real‑time status tracking (Pending → Accepted → In Transit → Completed) |
| 💰 **EcoCoin Bank** | Earn coins per kg (1 kg = 10 EcoCoins), view transaction history |
| 🏦 **Withdrawal** | Request withdrawal to bank account or e‑wallet (minimum 500 coins) |
| 💾 **Offline Support** | Local Room database caches user data and pending requests |
| 🌗 **Theme Support** | Light and dark mode (values-night) |
| 🧭 **Navigation** | Bottom navigation bar with Home, Schedule, Earnings, History, Profile screens |

---

## 🏗️ Architecture & Tech Stack

- **Architecture Pattern:** MVVM (Model-View-ViewModel)
- **Language:** Java
- **Local Database:** Room Persistence Library
- **UI:** XML layouts with ConstraintLayout, Material Design components
- **Navigation:** Android Navigation Component
- **Async Operations:** AsyncTask / LiveData (planned for future)
- **Minimum SDK:** Android 5.0 (API 21)
- **Target SDK:** Android 14 (API 34)

---

## 📂 Project Structure

```
app/src/main/
├── java/com/ecowaste/app/
│   ├── auth/                 # Login, registration, session management
│   ├── bank/                 # EcoCoin balance & transaction logic
│   ├── data/local/           # Room entities, DAOs, database
│   ├── navigation/           # Navigation graphs & screen flow
│   ├── pickup/               # Pickup request & status handling
│   ├── screens/              # All UI screens (activities/fragments)
│   ├── ui/theme/             # Custom themes, colors, styles
│   ├── user/                 # User profile management
│   └── withdrawal/           # Withdrawal request processing
└── res/
    ├── drawable/             # Icons and illustrations
    ├── layout/               # XML layouts for all screens
    ├── menu/                 # Bottom navigation and options menu
    ├── mipmap-*/             # App launcher icons (multiple densities)
    ├── navigation/           # Navigation graph XML
    ├── values/               # Colors, strings, dimensions, themes
    ├── values-night/         # Dark theme overrides
    └── xml/                  # App shortcuts and configuration
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Ladybug or newer)
- JDK 11 or higher
- Android SDK (API 21+)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/darshanchelani/ecowaste-management.git
   ```

2. **Open in Android Studio**
   - Select `File > Open` and choose the project folder.

3. **Build the project**
   - Wait for Gradle sync to complete.
   - If any dependencies are missing, check `build.gradle` files.

4. **Run the app**
   - Connect an Android device (USB debugging enabled) or start an emulator.
   - Click `Run > Run 'app'` (green triangle).

> ⚠️ No external API keys or Firebase setup are required for the current offline version. All data is stored locally using Room.

---

## 🧪 Testing the App

- **Test user registration:** Provide email/phone and password → account created locally.
- **Schedule a pickup:** Select waste type, enter weight, pick date/time → request saved in Room database.
- **Earn EcoCoins:** After a pickup is marked "Completed" (manual update for demo), coins are added to bank.
- **Withdraw coins:** Navigate to Earnings → Withdraw → Enter amount (≥500) and bank details.
- **Offline mode:** Turn off internet – all features work, pending sync is queued.

---

## 🔮 Future Enhancements

- Live GPS tracking of collector vehicles
- QR code‑based weight verification
- Separate collector-facing mobile app
- Backend REST API with Firebase Firestore
- Push notifications (Firebase Cloud Messaging)
- Admin dashboard for monitoring pickups and withdrawals

---

## 📜 License

This project is for **academic purposes** only. Not intended for commercial use without permission.

---

## 👤 Author

**Darshan**  
Student ID: 023-22-0372  
Course: Mobile Application Development  


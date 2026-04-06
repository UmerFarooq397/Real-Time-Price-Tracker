# Real-Time Price Tracker

A high-performance Android application built with modern development practices to track stock market prices in real-time. This project demonstrates the integration of WebSockets for live data streaming and Jetpack Compose for a reactive, state-driven UI.

## Technical Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (100%)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Concurrency:** Kotlin Coroutines & Flow
* **Networking:** OkHttp WebSockets
* **Navigation:** Navigation Compose
* **Dependency Injection:** Custom ViewModel Factory
* **Serialization:** Gson

## Core Features
### 1. Live Market Feed
* **WebSocket Integration:** Connects to `wss://ws.postman-echo.com/raw` to simulate a real-time trading environment.
* **Smart Update Logic:** Every 2 seconds, the app generates random price updates for 25 major stock symbols (AAPL, NVDA, TSLA, etc.), sends them to the echo server, and updates the UI upon receiving the response.
* **Auto-Sorting:** The market list remains sorted by price in real-time, ensuring the most valuable assets are always at the top.
* **Connection Management:** Users can start or stop the live feed via the TopBar toggle. A persistent 🟢/🔴 indicator shows the current WebSocket status.

### 2. Detailed Symbol Analysis
* **Deep Linking:** Supports `stocks://symbol/{symbol}` deep links to jump directly to specific assets.
* **Historical Context:** View detailed descriptions and real-time trending indicators for each stock.
* **Shared Connection:** Uses a repository-pattern to share the WebSocket stream across screens, preventing redundant network connections.

### 3. Polish & UX
* **Visual Cues:** Rows flash green on price increases and red on decreases for 1 second, providing immediate feedback on market volatility.
* **Theme Support:** Fully supports Light and Dark modes using Material 3 design tokens.
* **Robust State:** Built with immutable UI state and `StateFlow` to ensure the UI is always a "pure function" of the data.

## Requirements Check
- [x] 25 Stock Symbols (Scrollable list)
- [x] WebSocket Echo Integration (2s intervals)
- [x] Real-time sorting and ↑/↓ indicators
- [x] Connection status indicator & Toggle
- [x] Detail screen with symbol descriptions
- [x] MVVM Architecture with Navigation Compose
- [x] Deep link support
- [x] Price flash animations
- [x] Unit tests for Repository logic
- [x] Light/Dark theme support

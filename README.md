# Happy Birthday App 🎉

This is an interactive Android app built with **Jetpack Compose** that displays a birthday greeting. Users can trigger confetti animations by clicking on a cake image or by shaking their device! It was initially created based on concepts from the [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course) course by Google and has been extended with more interactive features.

## 📱 Features

-   **Jetpack Compose UI:** Modern UI built entirely with Jetpack Compose.
-   **Interactive Confetti:**
    -   **Click to Celebrate:** Tap the cake image to launch a burst of confetti.
    -   **Shake to Celebrate:** Shake your device to trigger another confetti animation.
-   **Customizable Greeting:** Displays a personalized birthday message and sender.
-   **Image Background:** Features an attractive background image.
-   **Sensor Integration:** Utilizes the device's accelerometer for shake detection.

## ✨ Tech Stack / Key Components

-   **Kotlin:** Primary programming language.
-   **Jetpack Compose:** For building the declarative UI.
    -   Core Composables: `Box`, `Column`, `Text`, `Image`.
    -   State Management: `remember`, `mutableStateOf`.
    -   Side Effects: `LaunchedEffect`, `DisposableEffect` (for sensor lifecycle management).
-   **Konfetti Library:** Used for creating and displaying the confetti animations ([DanielMartinus/Konfetti](https://github.com/DanielMartinus/Konfetti)).
-   **Android Sensors:** `SensorManager` and `SensorEventListener` for accelerometer-based shake detection.
-   **Lifecycle Awareness:** Correctly managing sensor listeners according to the composable's lifecycle.

## 🚀 Getting Started

To run this project:

1.  **Clone the repository:**
   ```bash
   git clone https://github.com/bruceneriDev/HappyBirthdayApp.git

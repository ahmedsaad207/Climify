# Climify - Weather App

Climify is a Kotlin-based weather application that provides real-time weather data, including current conditions, hourly updates, and daily forecasts. The app supports multiple languages (Arabic and English) and allows users to customize their experience with offline support, weather alerts, and user preferences.

## Features

- **Real-time weather updates**: Get the current weather, hourly forecast, and 7-day forecast.
- **Location-based weather**: Choose your location using **GPS** or an interactive **map**.
- **Multiple temperature units**: Switch between **Celsius**, **Fahrenheit**, and **Kelvin**.
- **Wind speed units**: Choose between **meters/second** or **miles/hour**.
- **Offline mode**: View cached weather data without an internet connection.
- **Weather alerts**: Set custom notifications for weather changes.
- **User preferences**: Manage settings using **SharedPreferences**.

## Technologies Used

- **Kotlin** for development.
- **MVVM architecture** for clean code and separation of concerns.
- **Jetpack Compose** for UI design.
- **Retrofit** for API calls to **OpenWeatherMap** and **Google Maps API**.
- **Room** for local database management and caching data.
- **WorkManager** for background tasks (e.g., updating weather data).
- **Coroutines** for asynchronous programming.
- **SharedPreferences** for storing user preferences.

## API Integrations

- **OpenWeatherMap API**: Provides weather data such as current conditions, forecasts, and more.
- **Google Places API**: Allows location search and user input for city names.
- **Google Maps API**: Used for location selection via map.

## Installation

### Prerequisites

- Android Studio
- SDK: **API 21** and above
- **Kotlin** support

### Steps

1. Clone this repository:
   ```bash
   git clone https://github.com/ahmedsaad207/Climify.git
2. Open the project in Android Studio
3. Connect your Android device or start an emulator.
4. Run the app.
5. Configure API keys:

- Sign up at OpenWeatherMap and Google Cloud Console to get your API key for weather data and location services.
6. Launch the app on your device or emulator, and enjoy the weather forecasts!

## Screenshots

Here are some screenshots of the **Climify** app:

<img src="https://github.com/user-attachments/assets/69eadd86-425a-4b51-8e28-975230866beb" width="300"/>
<img src="https://github.com/user-attachments/assets/0fa9bb82-415c-4e9b-a36a-f06c96a4d95d" width="300"/>
<img src="https://github.com/user-attachments/assets/a82b5644-8f6b-4d91-a1a5-858e4c6f72c8" width="300"/>
<img src="https://github.com/user-attachments/assets/ba8d183f-5f94-4d0e-88aa-8080698a2838" width="300"/>
<img src="https://github.com/user-attachments/assets/0dd458dc-cd12-4fbd-919d-a53d17179d99" width="300"/>
<img src="https://github.com/user-attachments/assets/58d2276d-ff6f-4a01-b9c0-20c291bbdc9b" width="300"/>



## Testing

The app is tested using **unit tests**, **mockk**, **fakes**, and **stubs** to ensure reliable functionality.

## Acknowledgements

A big thank you to my instructors for their guidance throughout the development process:

- **Ahmed Mazen**
- **Hager Samir**
- **Heba Ismail**
- **Mohamed Galal**
- **Yasmeen Hosny**


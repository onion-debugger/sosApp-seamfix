Take Home Assignment For SeamFix Android Role

### Overview
The SOS app would help in contacting the police or family members, in a situation where you or someone is in distress and unable to make phone calls. The app is built using MVVM (Model-View-ViewModel).

### Components
- Camera integration using CameraView library
- Location services using FusedLocationProviderClient
- Network calls using Retrofit
- Permission handling
- Base64 image conversion
- API integration

### Features
- Real-time camera preview
- Single-tap SOS button
- Automatic location capture
- Image capture and conversion
- API request with location and image data
- Error handling and user feedback

### Project Structure
- Proper Android app architecture
- Kotlin coroutines for asynchronous operations
- Data models for API communication
- Clean UI layout with Material Design

### Dependencies
- Networking
  - com.squareup.retrofit2:retrofit:2.9.0
  - com.squareup.retrofit2:converter-moshi:2.9.0
  - com.squareup.okhttp3:okhttp:5.0.0-alpha.3
  - com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3
  - com.squareup.retrofit2:converter-gson:2.9.0
- JSON Parsing
  - com.google.code.gson:gson:2.9.0
- Coroutines
  - org.jetbrains.kotlinx.kotlinx-coroutines-android:1.7.1
  - org.jetbrains.kotlinx.kotlinx-coroutines-core:1.7.1
- CameraView
  - com.otaliastudios:cameraview:2.7.2
- Play Service Location
  - com.google.android.gms.play-services-location:21.3.0
  
### Build and Run
To build and run the project, follow these steps:

Clone the repository: git clone https://github.com/onion-debugger/sosApp-seamfix.git

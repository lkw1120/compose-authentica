# Authentica

A native Android OTP authenticator built with Kotlin and Jetpack Compose.

## Overview

Authentica is an offline-first TOTP authenticator application for Android.
It supports manual account registration and `otpauth://` QR import, generates time-based one-time passwords locally, and keeps OTP secrets encrypted at rest.

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android SDK configured
- JDK 17+

### Build & Test

```bash
./gradlew test
./gradlew assembleDebug
```

### Security Note

- OTP secrets are encrypted at rest using Android Keystore-backed encryption.
- Cloud backup and device-transfer backup are disabled by default for authenticator-sensitive data.
- If you need account portability, use the in-app encrypted export/import flow.

## Architecture

The project follows a layered architecture with clear boundaries:

- **Domain Layer**: business models, repository contracts, and use cases
- **Data Layer**: Room database, encrypted secret handling, backup codec, repository implementations
- **Presentation Layer**: Compose UI, navigation, and ViewModels

Dependency injection is managed with **Hilt**, persistence with **Room** and **DataStore**, and OTP generation follows RFC-compliant TOTP flow.

### Project Structure

- **`domain`** - business models, repository interfaces, and use cases
- **`model`** - local database, crypto, backup, and repository implementations
- **`presentation`** - Compose screens, components, navigation, and UI state
- **`common`** - Base32 decoder, URI parser, and TOTP generator
- **`di`** - dependency injection modules

## Tech Stack

- **Kotlin**
- **Jetpack Compose** (Material 3)
- **Hilt** for dependency injection
- **Room** for local persistence
- **DataStore** for app settings
- **Android Keystore + AES/GCM** for secret encryption
- **Google Play services Code Scanner (ML Kit)** for QR scanning

## Features

- **TOTP Generation** with SHA1 / SHA256 / SHA512 support
- **QR Import** from `otpauth://` URI
- **Manual Account Registration** with issuer/account metadata
- **Encrypted Secret Storage** using Android Keystore-backed encryption
- **Secure Backup Export/Import** with password-based encryption
- **Account Edit/Delete/Reorder** for OTP entries
- **Theme and Lock Settings** for usability and local security

## Contributing

Contributions are welcome. Please open an issue first for major changes, then submit a pull request with clear scope and test results.

## License

```
Copyright 2026 lkw1120

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

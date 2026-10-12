# Sisile Chats — Android prototype v0.1

Purple/black, offline-first Android prototype. It saves a local conversation on one device and offers Android's share sheet to manually send text using a method available on the phone.

## Current scope
- Android Compose interface
- Locally saved messages (SharedPreferences)
- Works to read/write local messages without mobile data
- Manual text sharing via Android share chooser (Bluetooth/Nearby Share may appear depending on device and settings)

## Not yet implemented
This is not yet a WhatsApp replacement and does not provide automatic phone-to-phone chat, Bluetooth mesh, internet account sync, phone-number authentication, groups, voice/video calls, media transfer inside the app, or end-to-end encryption. Those require additional networking, permissions, backend services and security testing. Do not describe this prototype as end-to-end encrypted.

## Build an APK
1. On a computer, install Android Studio (stable) and the Android SDK platform 35.
2. Extract this ZIP.
3. Open the `SisileChats` folder in Android Studio.
4. Let Gradle sync. Use JDK 17 if Android Studio asks for a Gradle JDK.
5. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. After it finishes, click **locate** in the notification. The APK is usually `app/build/outputs/apk/debug/app-debug.apk`.

## Install on your Android phone
1. Move `app-debug.apk` to your phone (USB, Bluetooth, or a file transfer app).
2. Tap the APK in Files/Downloads.
3. If Android asks, allow that file manager to **Install unknown apps**.
4. Tap Install, then open **Sisile Chats**.
5. Turn mobile data off to test local message saving. Type a message and close/reopen the app; it should remain.

## Build directly to a connected phone
Enable Developer options and USB debugging on the phone, connect it to Android Studio, accept the debugging prompt, then press Run.

## Next development milestones
1. Nearby device discovery and transfer using Android Bluetooth / Wi-Fi Direct / Nearby Connections.
2. Encrypted local storage and authenticated device pairing.
3. Internet messaging backend (e.g. Firebase or a custom WebSocket service).
4. Call signaling and a real-time media stack such as WebRTC.
5. Security review, privacy policy, data deletion and release signing.

A phone-only path is possible but less reliable: Android Studio is a desktop application. For a true APK build, use a computer or a trusted cloud Android build environment.

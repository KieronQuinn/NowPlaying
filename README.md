![Ambient Music Mod Banner](https://i.imgur.com/SPWAuFll.png)

[Ambient Music Mod](https://github.com/KieronQuinn/AmbientMusicMod) | **Now Playing** 

Now Playing is a modified version of Android System Intelligence, running only the Now Playing (music recognition) component on other devices. It is only usable with the Ambient Music Mod app.

**If you are trying to install Now Playing, please use the links on the Ambient Music Mod repo for prebuilt APKs**

## Building

> Note: If you are building Now Playing yourself, you **must** also build Ambient Music Mod, since the signatures have to match for security reasons.

1. Clone the repository as normal
2. Download the latest original Pixel 7 APK of Android System Intelligence from [APKmirror](https://www.apkmirror.com/apk/google-inc/device-personalization-services/). (Note: At the time of writing, the build "U.0.droidfood.pixel6.514591756" was being used for the prebuilt APKs. Newer versions have not been tested.)

> Note: If you wish to include armv7 support, in addition download [this](https://www.apkmirror.com/apk/google-inc/pixel-ambient-services/pixel-ambient-services-1-0-181470108-release/pixel-ambient-services-1-0-181470108-android-apk-download/) version of Pixel Ambient Services, and place `libsense.so` from it in `overlay/src/main/lib/armeabi-v7a`, as well as [this](https://www.apkmirror.com/apk/google-inc/google-play-services/google-play-services-22-22-55-release/google-play-services-22-22-55-020300-453326789-android-apk-download/) version of Google Play services, and place `libleveldbjni.so` from it in the same directory.

> If you wish to include x86_64 support, in addition download [the latest](https://www.apkmirror.com/apk/google-inc/device-personalization-services) version of Android System Intelligence for x86_64, and place `libsense.so`, `libsense_nnfp_v3.so` and `libmodeleditor-jni.so` from it in `overlay/src/main/lib/x86_64`.

3. Place the downloaded APK in the root of the repository, name it `base.apk`
4. Download the latest [APKtool](https://github.com/iBotPeaches/Apktool/releases) JAR and place it in `tools/`. Name it `apktool.jar`
5. Create a `local.properties` file in the root of the project, and set it up:
```
sdk.dir=<path to your Android SDK>
storeFile=<path to your keystore>
keyAlias=<keystore alias>
storePassword=<keystore password>
keyPassword=<key password>
build.tools.version=<the version of build tools to use, eg. 32.0.0>
```
6. Open the project in Android Studio, and run the `installApkRelease` Gradle task. This will build an APK (placed in `build/out-release.apk`) and install it on your device.

## Sources

This repository contains a local version of [dain/leveldb](https://github.com/dain/leveldb) ([Apache 2.0 licence](https://github.com/dain/leveldb/blob/master/license.txt)).

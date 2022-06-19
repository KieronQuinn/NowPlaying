val versionName = "1.0"
val versionCode = 100
val minSdk = 28
val targetSdk = 31
//armv7 currently disabled pending LSPlant fix
val supportedAbis = arrayOf("arm64-v8a") //, "armeabi-v7a")

project.extra.apply {
    set("versionName", versionName)
    set("versionCode", versionCode)
    set("minSdk", minSdk)
    set("targetSdk", targetSdk)
    set("supportedAbis", supportedAbis)
}
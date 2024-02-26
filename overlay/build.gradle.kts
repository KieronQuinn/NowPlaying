import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    id("com.google.protobuf")
}
apply {
    plugin("kotlin-android")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kieronquinn.app.pixelambientmusic"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        prefab = true
    }
    buildFeatures {
        aidl = true
        buildConfig = true
    }
    namespace = "com.kieronquinn.app.pixelambientmusic"
}

dependencies {
    //Refer to code from system stubs + manifest code stubs, but don't include in APK
    compileOnly(project(mapOf("path" to ":systemstubs")))
    implementation(project(mapOf("path" to ":leveldb")))
    implementation("androidx.core:core:1.12.0")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("com.aliucord:Aliuhook:6cc5993")
    implementation("top.canyie.pine:core:0.2.8")
    implementation("com.google.protobuf:protobuf-lite:3.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.68")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.68")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.10.1"
    }
    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("javalite")
            }
        }
    }
}
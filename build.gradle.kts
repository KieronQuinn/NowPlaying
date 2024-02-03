// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.android").version("1.9.22").apply(false)
    id("com.android.application").version("8.2.2").apply(false)
    id("com.android.library").version("8.2.2").apply(false)
    id("com.google.protobuf").version("0.9.4").apply(false)
}

apply("variables.gradle.kts")
apply("apktool.gradle.kts")

tasks.create<Delete>("clean") {
    delete(
        fileTree(rootProject.buildDir)
    )
}

fun createApktoolTasks() {
    val createApktoolTask = project.extra["createApktoolTask"] as (String, String, String, Boolean) -> Unit
    mapOf(
        "Debug" to false,
        "Release" to true
    ).forEach { (key: String, value: Boolean) ->
        createApktoolTask("buildApk", "installApk", key, value)
    }
}

createApktoolTasks()

//Copies in splits XML from the overlay resources to fix compilation issue
tasks.getByPath(":copyOverlay").doLast {
    val splits0in = File("overlay/src/main/res/xml/splits0.xml")
    val splits0out = File("base/res/xml/splits0.xml")
    val splits1out = File("base/res/xml/splits1.xml")
    splits0in.copyTo(splits0out, true)
    splits0in.copyTo(splits1out, true)
}
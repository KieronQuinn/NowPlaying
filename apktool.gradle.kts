/**
 *  Apktool helper methods for Android Studio. Allows modification of a "base" APK
 *  including resources and code from "overlay" module
 *  in the built, aligned and signed APK.
 */

val toolsDir = File(project.rootDir, "tools")
val apktoolJar = File(toolsDir, "apktool.jar")
val baseApk = File(project.rootDir, "base.apk")
val baseDir = File(project.rootDir, "base")
val baseManifest = File(baseDir, "AndroidManifest.xml")
val androidDir = File(System.getProperty("user.home"), ".android")
val isWindows = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)

val smaliToRemove = arrayOf(
    "kotlin/internal/jdk8/JDK8PlatformImplementations.smali",
    "kotlin/internal/jdk8/JDK8PlatformImplementations\$ReflectSdkVersion.smali",
    "kotlin/internal/jdk7/JDK7PlatformImplementations.smali",
    "kotlin/internal/jdk7/JDK7PlatformImplementations\$ReflectSdkVersion.smali"
)

fun getLocalProperty(key: String): String? {
    return java.util.Properties().apply {
        load(java.io.FileInputStream(File(rootProject.rootDir, "local.properties")))
    }.getProperty(key)
}

fun assertAndroidHome(): String {
    return getLocalProperty("sdk.dir")
        ?: throw Exception("sdk.dir not set in local.properties")
}

fun assertBuildTools(): File {
    val androidHome = assertAndroidHome()
    val buildToolsBaseDir = File(androidHome, "build-tools")
    val buildToolsVersion = getLocalProperty("build.tools.version")
        ?: throw Exception("build.tools.version not set in local.properties, please set it to a version in ${buildToolsBaseDir.absolutePath}")
    val buildToolsDir = File(buildToolsBaseDir, buildToolsVersion)
    if(!buildToolsDir.exists()){
        throw Exception("Invalid build.tools.version specified, not found in ${buildToolsBaseDir.absolutePath}")
    }
    return buildToolsDir
}

data class SignApkConfig(val keystore: File, val keystorePass: String, val keyAlias: String, val keyPass: String)

fun getSignApkConfig(release: Boolean): SignApkConfig {
    if(!release){
        val keystore = File(androidDir, "debug.keystore")
        return SignApkConfig(
            keystore,
            "android",
            "androiddebugkey",
            "android"
        )
    }
    val keystorePath = getLocalProperty("storeFile")
        ?: throw Exception("Keystore storeFile not defined in local.properties")
    val storePassword = getLocalProperty("storePassword")
        ?: throw Exception("Keystore storePassword not defined in local.properties")
    val keyAlias = getLocalProperty("keyAlias")
        ?: throw Exception("Keystore keyAlias not defined in local.properties")
    val keyPassword = getLocalProperty("keyPassword")
        ?: throw Exception("Keystore keyPassword not defined in local.properties")
    if(!File(keystorePath).exists()){
        throw Exception("Keystore $keystorePath does not exist")
    }
    return SignApkConfig(File(keystorePath), storePassword, keyAlias, keyPassword)
}

fun assertApktool() {
    if (!apktoolJar.exists()) {
        throw Exception("apktool.jar not found, please place the latest apktool.jar in the tools directory")
    }
}

fun assertBaseApk() {
    if (!baseApk.exists()) {
        throw Exception("base.apk not found, please place the original APK in the root directory, named 'base.apk'")
    }
}

fun assertBaseManifest() {
    if (!baseManifest.exists()) {
        throw Exception("base has not been decompiled, run decompile first")
    }
}

fun getOutApk(suffix: String): File {
    return File(project.buildDir, "out-${suffix.toLowerCase()}-unaligned.apk")
}

fun assertOutApk(suffix: String) {
    val outApk = getOutApk(suffix)
    if (!outApk.exists()) {
        throw Exception("${outApk.name} not found, run compile first")
    }
}

fun getOutAlignedApk(suffix: String): File {
    return File(project.buildDir, "out-${suffix.toLowerCase()}.apk")
}

fun assertOutAlignedApk(suffix: String) {
    val outAlignedApk = getOutAlignedApk(suffix)
    if (!outAlignedApk.exists()) {
        throw Exception("${outAlignedApk.name} not found, run compile & align first")
    }
}

fun findSmaliDirs(directory: File): List<File> {
    return directory.listFiles().filter { it.name.startsWith("smali") }
}

fun copySmaliDirs(from: File, to: File) {
    val currentSize = findSmaliDirs(to).size
    val smaliDirs = findSmaliDirs(from)
    smaliDirs.forEachIndexed { index, folder ->
        //Index of smali_classes starts at 2
        val folderName = "smali_classes${currentSize + index + 1}"
        folder.copyRecursively(File(to, folderName))
    }
}

fun copyRawSmaliDirs(from: File, to: File) {
    from.listFiles().forEach { folder ->
        folder.copyRecursively(File(to, folder.name), true)
    }
}

fun copyResDir(from: File, to: File) {
    from.copyRecursively(File(to, "res"), true)
}

fun copyAssetsDir(from: File, to: File) {
    File(from, "assets").copyRecursively(File(to, "assets"), true)
}

fun copyLibsDir(from: File, to: File) {
    File(from, "lib").copyRecursively(File(to, "lib"), true)
}

fun copyManifest(from: File, to: File) {
    File(from, "AndroidManifest.xml").copyRecursively(File(to, "AndroidManifest.xml"), true)
}

fun stripLibs(from: File) {
    val libsDir = File(from, "lib")
    val supported = project.extra.get("supportedAbis") as Array<String>
    libsDir.listFiles()?.filterNot { supported.contains(it.name) }?.forEach { it.deleteRecursively() }
}

fun stripSmali(from: File) {
    findSmaliDirs(from).forEach {
        smaliToRemove.forEach { smaliPath ->
            File(it.absolutePath + "/" + smaliPath).let { file ->
                if(!file.exists()) return@let
                file.delete()
            }
        }
    }
}

fun modifyApktoolYml(apktoolYml: File) {
    val yaml = apktoolYml.readText()
        .replaceGroup("  minSdkVersion: '(.*)'", 1, project.extra.get("minSdk").toString())
        .replaceGroup("  targetSdkVersion: '(.*)'", 1, project.extra.get("targetSdk").toString())
        .replaceGroup("  versionCode: '(.*)'", 1, project.extra.get("versionCode").toString())
        .replaceGroup("  versionName: (.*)", 1, project.extra.get("versionName").toString())
    apktoolYml.writeText(yaml)
}

/**
 *  Decompile the base APK into the base folder, which should not be modified
 */
task<Exec>("decompileBase") {
    doLast {
        assertApktool()
        assertBaseApk()
    }
    commandLine(
        "java",
        "-jar",
        apktoolJar.absolutePath,
        "d",
        "-o",
        baseDir.absolutePath,
        "-f",
        baseApk.absolutePath
    )
}

/**
 *  Decompiles the base and packages the overlay (their order isn't important)
 */
task("buildOverlay") {
    dependsOn("decompileBase", ":overlay:packageDebugUniversalApk")
}

/**
 *  Compile and then decompile the overlay module, producing smali and the merged manifest to
 *  be copied into the base
 */
task<Exec>("decompileOverlay") {
    dependsOn("buildOverlay")
    val overlayModule = File(project.rootDir, "overlay")
    val overlayBuild = File(overlayModule, "build")
    val overlayOutputs = File(overlayBuild, "outputs")
    val overlayApkDir = File(overlayOutputs, "universal_apk")
    val overlayDebugApkDir = File(overlayApkDir, "debug")
    val overlayApk = File(overlayDebugApkDir, "overlay-debug-universal.apk")
    val decompiledDir = File(overlayBuild, "decompiled")
    doLast {
        assertApktool()
        if(!overlayApk.exists()){
            throw Exception("Overlay not built, run build first")
        }
    }
    commandLine(
        "java",
        "-jar",
        apktoolJar.absolutePath,
        "d",
        "-o",
        decompiledDir.absolutePath,
        "-f",
        overlayApk.absolutePath
    )
}

/**
 *  Copy the decompiled overlay into base. This includes smali and manifest, but NOT the resources
 *  as the values would collide. Instead we copy a folder called "res-overlay" which contains
 *  select resources overriding the default ones.
 */
task("copyOverlay"){
    dependsOn("decompileOverlay")
    //We can't use a copy task for this as it's too dynamic
    val overlayModule = File(project.rootDir, "overlay")
    val overlayBuild = File(overlayModule, "build")
    val decompiledDir = File(overlayBuild, "decompiled")
    val overlaySrc = File(overlayModule, "src")
    val overlaySrcMain = File(overlaySrc, "main")
    val rawSmaliDir = File(overlaySrcMain, "smali")
    val rawResDir = File(overlaySrcMain, "res-overlay")
    val apktoolYml = File(baseDir, "apktool.yml")
    doLast {
        assertBaseManifest()
        if(!decompiledDir.exists()){
            throw Exception("Overlay not decompiled, run decompile first")
        }
        modifyApktoolYml(apktoolYml)
        copySmaliDirs(decompiledDir, baseDir)
        copyResDir(rawResDir, baseDir)
        copyRawSmaliDirs(rawSmaliDir, baseDir)
        copyAssetsDir(decompiledDir, baseDir)
        copyLibsDir(decompiledDir, baseDir)
        copyLibsDir(overlaySrcMain, baseDir)
        stripLibs(baseDir)
        stripSmali(baseDir)
        copyManifest(decompiledDir, baseDir)
    }
}

/**
 *  Compile the decompiled & merged base back into an APK using apktool
 */
fun createCompileTask(suffix: String) {
    task<Exec>("compileBase$suffix") {
        doLast {
            assertApktool()
            assertBaseManifest()
            assertOutApk(suffix)
        }
        val outApk = getOutApk(suffix)
        dependsOn("copyOverlay")
        commandLine(
            "java",
            "-jar",
            apktoolJar.absolutePath,
            "b",
            "-f",
            "-o",
            outApk.absolutePath,
            baseDir.absolutePath
        )
    }
}

/**
 *  Sign the built base APK
 */
fun createSignTask(suffix: String, release: Boolean) {
    task<Exec>("signOutApk$suffix") {
        doLast {
            assertOutAlignedApk(suffix)
            assertBaseManifest()
        }
        dependsOn("alignOutApk$suffix")
        val signApkConfig = getSignApkConfig(release)
        if (!signApkConfig.keystore.exists()) {
            throw Exception("Keystore ${signApkConfig.keystore.absolutePath} does not exist")
        }
        val buildTools = assertBuildTools()
        val apkSigner = if (isWindows) {
            File(buildTools, "apksigner.bat")
        } else {
            File(buildTools, "apksigner")
        }
        val outAlignedApk = getOutAlignedApk(suffix)
        commandLine(
            apkSigner.absolutePath,
            "sign",
            "--ks",
            signApkConfig.keystore.absolutePath,
            "--ks-key-alias",
            signApkConfig.keyAlias,
            "--ks-pass",
            "pass:" + signApkConfig.keystorePass,
            "--key-pass",
            "pass:" + signApkConfig.keyPass,
            outAlignedApk.absolutePath
        )
    }
}

/**
 *  Zipalign the base APK
 */
fun createAlignTask(suffix: String) {
    task<Exec>("alignOutApk$suffix") {
        doLast {
            assertOutApk(suffix)
        }
        dependsOn("compileBase$suffix")
        val buildTools = assertBuildTools()
        val zipalign = File(buildTools, "zipalign")
        val outApk = getOutApk(suffix)
        val outAlignedApk = getOutAlignedApk(suffix)
        commandLine(
            zipalign.absolutePath,
            "-p",
            "-f",
            "-v",
            "4",
            outApk.absolutePath,
            outAlignedApk.absolutePath
        )
    }
}

project.extra["createApktoolTask"] = { buildName: String, installName: String, suffix: String, release: Boolean ->
    createCompileTask(suffix)
    createSignTask(suffix, release)
    createAlignTask(suffix)
    task("$buildName$suffix"){
        dependsOn("signOutApk$suffix")
    }
    task<Exec>("$installName$suffix"){
        dependsOn("$buildName$suffix")
        doLast {
            assertOutAlignedApk(suffix)
        }
        val outAlignedApk = getOutAlignedApk(suffix)
        commandLine(
            "adb",
            "install",
            "-r",
            outAlignedApk.absolutePath
        )
    }
}

fun String.replaceGroup(
    regex: String,
    groupToReplace: Int,
    replacement: String
): String {
    return replaceGroup(regex, this, groupToReplace, 1, replacement)
}

fun replaceGroup(
    regex: String,
    source: String,
    groupToReplace: Int,
    groupOccurrence: Int,
    replacement: String
): String {
    val m: java.util.regex.Matcher = java.util.regex.Pattern.compile(regex).matcher(source)
    for (i in 0 until groupOccurrence) if (!m.find()) return source // pattern not met, may also throw an exception here
    return StringBuilder(source).replace(
        m.start(groupToReplace),
        m.end(groupToReplace),
        replacement
    ).toString()
}
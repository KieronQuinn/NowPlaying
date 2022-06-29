val versionName = "1.0.3"
val versionCode = 103
val minSdk = 28
val targetSdk = 31
val supportedAbis = arrayOf("arm64-v8a", "armeabi-v7a")

val smaliReplacements = arrayOf(
    //Add NNFP init for armv7
    Triple(
        "com/google/audio/ambientmusic/NnfpRecognizer.smali",
        ".method private static native init(.*)\\(",
        "([Ljava/lang/String;[B)J\n.end method\n\n.method private static native init"
    ),
    //Add NNFP init for armv7
    Triple(
        "com/google/audio/ambientmusic/NnfpRecognizer.smali",
        ".method private static native recognize(.*)\\(",
        "(J[SIII)[B\n.end method\n\n.method private static native recognize"
    ),
    //Fix setDecorFitsSystemWindows for Android 10 in history activity
    Triple(
        "com/google/intelligence/sense/ambientmusic/history/HistoryActivity.smali",
        ", L(.*)\\(Landroid/view/Window;\\)V",
        "com/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->setDecorFitsSystemWindows"
    )
)

val dynamicSmaliReplacements = arrayOf(
    //Redirect accessibility for Android 10
    Triple(
        "Landroid/view/accessibility/AccessibilityNodeInfo\$AccessibilityAction;->ACTION_PRESS_AND_HOLD",
        "(Landroid/view/accessibility/AccessibilityNodeInfo\\\$AccessibilityAction;->ACTION_PRESS_AND_HOLD)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->ACTION_PRESS_AND_HOLD"
    ),
    Triple(
        "Landroid/view/accessibility/AccessibilityNodeInfo\$AccessibilityAction;->ACTION_IME_ENTER",
        "(Landroid/view/accessibility/AccessibilityNodeInfo\\\$AccessibilityAction;->ACTION_IME_ENTER)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->ACTION_IME_ENTER"
    ),
    //Redirect state description for Android 10
    Triple(
        "invoke-virtual {p0}, Landroid/view/View;->getStateDescription()Ljava/lang/CharSequence;",
        "(invoke-virtual \\{p0\\}, Landroid/view/View;->getStateDescription\\(\\)Ljava/lang/CharSequence;)",
        "invoke-static {p0}, Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->getStateDescription(Landroid/view/View;)Ljava/lang/CharSequence;"
    ),
    Triple(
        "invoke-virtual {p0, p1}, Landroid/view/View;->setStateDescription(Ljava/lang/CharSequence;)V",
        "(invoke-virtual \\{p0, p1\\}, Landroid/view/View;->setStateDescription\\(Ljava/lang/CharSequence;\\)V)",
        "invoke-static {p0, p1}, Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->setStateDescription(Landroid/view/View;Ljava/lang/CharSequence;)V"
    ),
    Triple(
        "invoke-virtual {v2, v1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setStateDescription(Ljava/lang/CharSequence;)V",
        "(invoke-virtual \\{v2, v1\\}, Landroid/view/accessibility/AccessibilityNodeInfo;->setStateDescription\\(Ljava/lang/CharSequence;\\)V)",
        "invoke-static {v2, v1}, Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->setStateDescription(Landroid/view/accessibility/AccessibilityNodeInfo;Ljava/lang/CharSequence;)V"
    )
)

project.extra.apply {
    set("versionName", versionName)
    set("versionCode", versionCode)
    set("minSdk", minSdk)
    set("targetSdk", targetSdk)
    set("supportedAbis", supportedAbis)
    set("smaliReplacements", smaliReplacements)
    set("dynamicSmaliReplacements", dynamicSmaliReplacements)
}
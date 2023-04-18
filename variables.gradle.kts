val versionName = "1.2"
val versionCode = 120
val minSdk = 28
val targetSdk = 33
val supportedAbis = arrayOf("arm64-v8a", "armeabi-v7a", "x86_64")

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
    ),
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
    //Redirect accessibility for Android 12
    Triple(
        "Landroid/view/accessibility/AccessibilityNodeInfo\$AccessibilityAction;->ACTION_DRAG_START",
        "(Landroid/view/accessibility/AccessibilityNodeInfo\\\$AccessibilityAction;->ACTION_DRAG_START)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->ACTION_DRAG_START"
    ),
    Triple(
        "Landroid/view/accessibility/AccessibilityNodeInfo\$AccessibilityAction;->ACTION_DRAG_DROP",
        "(Landroid/view/accessibility/AccessibilityNodeInfo\\\$AccessibilityAction;->ACTION_DRAG_DROP)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->ACTION_DRAG_DROP"
    ),
    Triple(
        "Landroid/view/accessibility/AccessibilityNodeInfo\$AccessibilityAction;->ACTION_DRAG_CANCEL",
        "(Landroid/view/accessibility/AccessibilityNodeInfo\\\$AccessibilityAction;->ACTION_DRAG_CANCEL)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->ACTION_DRAG_CANCEL"
    ),
    Triple(
        "Landroid/view/accessibility/AccessibilityNodeInfo\$AccessibilityAction;->ACTION_SHOW_TEXT_SUGGESTIONS",
        "(Landroid/view/accessibility/AccessibilityNodeInfo\\\$AccessibilityAction;->ACTION_SHOW_TEXT_SUGGESTIONS)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/SmaliUtils;->ACTION_SHOW_TEXT_SUGGESTIONS"
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
    ),
    //Redirect system audio captioning check to isEnabled, we don't provide captions anyway
    Triple(
        "Landroid/view/accessibility/CaptioningManager;->isSystemAudioCaptioningEnabled()Z",
        "(Landroid/view/accessibility/CaptioningManager;->isSystemAudioCaptioningEnabled\\(\\)Z)",
        "Landroid/view/accessibility/CaptioningManager;->isEnabled()Z"
    ),
    //Redirect system audio captioning UI check to isEnabled, we don't provide captions anyway
    Triple(
        "Landroid/view/accessibility/CaptioningManager;->isSystemAudioCaptioningUiEnabled()Z",
        "(Landroid/view/accessibility/CaptioningManager;->isSystemAudioCaptioningUiEnabled\\(\\)Z)",
        "Landroid/view/accessibility/CaptioningManager;->isEnabled()Z"
    ),
    //Redirect call captioning check to isEnabled, we don't provide captions anyway
    Triple(
        "Landroid/view/accessibility/CaptioningManager;->isCallCaptioningEnabled()Z",
        "(Landroid/view/accessibility/CaptioningManager;->isCallCaptioningEnabled\\(\\)Z)",
        "Landroid/view/accessibility/CaptioningManager;->isEnabled()Z"
    ),
    //Redirect PackageInfoFlags to compat model
    Triple(
        "Landroid/content/pm/PackageManager\$ApplicationInfoFlags;->of(J)Landroid/content/pm/PackageManager\$ApplicationInfoFlags",
        "(Landroid/content/pm/PackageManager\\\$ApplicationInfoFlags;->of\\(J\\)Landroid/content/pm/PackageManager\\\$ApplicationInfoFlags)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/compat/PackageManagerCompat\$ApplicationInfoFlags;->of(J)Lcom/kieronquinn/app/pixelambientmusic/utils/compat/PackageManagerCompat\$ApplicationInfoFlags"
    ),
    //Redirect calls to PackageManager.getApplicationInfo to the compat method w/ the compat model
    Triple(
        "invoke-virtual {p1, v0, v1}, Landroid/content/pm/PackageManager;->getApplicationInfo(Ljava/lang/String;Landroid/content/pm/PackageManager\$ApplicationInfoFlags;)Landroid/content/pm/ApplicationInfo;",
        "(invoke-virtual \\{p1, v0, v1\\}, Landroid/content/pm/PackageManager;->getApplicationInfo\\(Ljava/lang/String;Landroid/content/pm/PackageManager\\\$ApplicationInfoFlags;\\)Landroid/content/pm/ApplicationInfo;)",
        "invoke-static {p1, v0, v1}, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/PackageManagerCompat;->getApplicationInfo(Landroid/content/pm/PackageManager;Ljava/lang/String;Lcom/kieronquinn/app/pixelambientmusic/utils/compat/PackageManagerCompat\$ApplicationInfoFlags;)Landroid/content/pm/ApplicationInfo;"
    ),
    //Redirect calls to PackageManager.getApplicationInfo to the compat method w/ the compat model
    Triple(
        "invoke-virtual {p1, v1, v3}, Landroid/content/pm/PackageManager;->getApplicationInfo(Ljava/lang/String;Landroid/content/pm/PackageManager\$ApplicationInfoFlags;)Landroid/content/pm/ApplicationInfo;",
        "(invoke-virtual \\{p1, v1, v3\\}, Landroid/content/pm/PackageManager;->getApplicationInfo\\(Ljava/lang/String;Landroid/content/pm/PackageManager\\\$ApplicationInfoFlags;\\)Landroid/content/pm/ApplicationInfo;)",
        "invoke-static {p1, v1, v3}, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/PackageManagerCompat;->getApplicationInfo(Landroid/content/pm/PackageManager;Ljava/lang/String;Lcom/kieronquinn/app/pixelambientmusic/utils/compat/PackageManagerCompat\$ApplicationInfoFlags;)Landroid/content/pm/ApplicationInfo;"
    ),
    //Redirect calls to SpeechRecognizer.isOnDeviceRecognitionAvailable to compat
    Triple(
        "Landroid/speech/SpeechRecognizer;->isOnDeviceRecognitionAvailable(Landroid/content/Context;)Z",
        "(Landroid/speech/SpeechRecognizer;->isOnDeviceRecognitionAvailable\\(Landroid/content/Context;\\)Z)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/compat/SpeechRecognizerCompat;->isOnDeviceRecognitionAvailable(Landroid/content/Context;)Z"
    ),
    //Redirect calls to getOnBackInvokedDispatcher to compat method
    Triple(
        "getOnBackInvokedDispatcher()Landroid/window/OnBackInvokedDispatcher;",
        "(invoke-virtual .*;->getOnBackInvokedDispatcher\\(\\)Landroid/window/OnBackInvokedDispatcher;)",
        "invoke-static {p0}, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/ActivityCompat;->getOnBackInvokedDispatcher(Ljava/lang/Object;)Landroid/window/OnBackInvokedDispatcher;"
    ),
    //Redirect new SOC_MODEL field
    Triple(
        "Landroid/os/Build;->SOC_MODEL:Ljava/lang/String;",
        "(Landroid/os/Build;->SOC_MODEL:Ljava/lang/String;)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/compat/BuildCompat;->SOC_MODEL:Ljava/lang/String;"
    ),
    //Redirect SdkExtensions to compat
    Triple(
        "Landroid/os/ext/SdkExtensions;->getExtensionVersion(I)I",
        "(Landroid/os/ext/SdkExtensions;->getExtensionVersion\\(I\\)I)",
        "Lcom/kieronquinn/app/pixelambientmusic/utils/compat/SdkExtensionsCompat;->getExtensionVersion(I)I"
    ),
    //Redirect setDecorFitsSystemWindows to compat
    Triple(
        "Landroid/view/Window;->setDecorFitsSystemWindows(Z)V",
        "(invoke-virtual \\{p0, p1\\}, Landroid/view/Window;->setDecorFitsSystemWindows\\(Z\\)V)",
        "invoke-static {p0, p1}, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/WindowCompat;->setDecorFitsSystemWindows(Landroid/view/Window;Z)V"
    ),
    //Redirect Outline.setPath to compat
    Triple(
        "Landroid/graphics/Outline;->setPath(Landroid/graphics/Path;)V",
        "(invoke-virtual \\{p1, v0\\}, Landroid/graphics/Outline;->setPath\\(Landroid/graphics/Path;\\)V)",
        "invoke-static {p1, v0}, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/OutlineCompat;->setPath(Landroid/graphics/Outline;Landroid/graphics/Path;)V"
    ),
    //Redirect ShortcutInfo.Builder.setExcludedFromSurfaces to compat
    Triple(
        "Landroid/content/pm/ShortcutInfo\$Builder;->setExcludedFromSurfaces(I)Landroid/content/pm/ShortcutInfo\$Builder",
        "(invoke-virtual \\{v0, v1\\}, Landroid/content/pm/ShortcutInfo\\\$Builder;->setExcludedFromSurfaces\\(I\\)Landroid/content/pm/ShortcutInfo\\\$Builder)",
        "invoke-static {v0, v1}, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/ShortcutInfoCompat;->setExcludedFromSurfaces(Landroid/content/pm/ShortcutInfo\$Builder;I)Landroid/content/pm/ShortcutInfo\$Builder"
    ),
    //Redirect Configuration.fontWeightAdjustment
    Triple(
        "iget p0, p0, Landroid/content/res/Configuration;->fontWeightAdjustment:I",
        "(iget p0, p0, Landroid/content/res/Configuration;->fontWeightAdjustment:I)",
        "sget p0, Lcom/kieronquinn/app/pixelambientmusic/utils/compat/ConfigurationCompat;->fontWeightAdjustment:I"
    ),
    //Increase Now Playing superpacks quota to long limit (effectively infinite)
    Triple(
        "const-wide/32 v5, 0x25800000",
        "(const-wide/32 v5, 0x25800000)",
        "const-wide v5, 0x7fffffffffffffffL"
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
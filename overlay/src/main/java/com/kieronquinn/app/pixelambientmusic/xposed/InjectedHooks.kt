package com.kieronquinn.app.pixelambientmusic.xposed

/**
 *  Variant of [XposedHooks] that does not initially know its class. Instead, it uses a label that
 *  is not obfuscated, and used in the injecting in ASI.
 *
 *  When the class is set from another hook (using [setClass]), [XposedHooks.init] is called.
 */
abstract class InjectedHooks: XposedHooks() {

    abstract val label: String

    private var _clazz: Class<*>? = null
        set(value) {
            //Only set once and make sure it's not set to null
            if(field != null || value == null) return
            field = value
            //Now we can init
            super.init()
            custom(value)
        }

    override val clazz
        get() = _clazz!!

    fun setClass(clazz: Class<*>) {
        _clazz = clazz
    }

    override fun init() {
        //Disable regular init until we have a valid class
    }

    open fun custom(clazz: Class<*>) {
        //No-op by default
    }

}
package com.ztc1997.miuiaodnotificationicon.extention

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object KXposedHelpers {
    fun findAndHookMethod(
        clazz: Class<*>, methodName: String, vararg parameterTypes: Any,
        callback: _XC_MethodHook.() -> Unit
    ): XC_MethodHook.Unhook? =
        XposedHelpers.findAndHookMethod(clazz, methodName, *parameterTypes, methodHookCallback(callback))

    fun findAndHookMethod(
        className: String, classLoader: ClassLoader, methodName: String,
        vararg parameterTypes: Any, callback: _XC_MethodHook.() -> Unit
    ): XC_MethodHook.Unhook? = XposedHelpers.findAndHookMethod(
        className,
        classLoader,
        methodName,
        *parameterTypes,
        methodHookCallback(callback)
    )

    fun findAndHookConstructor(
        clazz: Class<*>, vararg parameterTypes: Any,
        callback: _XC_MethodHook.() -> Unit
    ): XC_MethodHook.Unhook? =
        XposedHelpers.findAndHookConstructor(clazz, *parameterTypes, methodHookCallback(callback))

    fun findAndHookConstructor(
        className: String, classLoader: ClassLoader,
        vararg parameterTypes: Any, callback: _XC_MethodHook.() -> Unit
    ): XC_MethodHook.Unhook? =
        XposedHelpers.findAndHookConstructor(className, classLoader, *parameterTypes, methodHookCallback(callback))

}
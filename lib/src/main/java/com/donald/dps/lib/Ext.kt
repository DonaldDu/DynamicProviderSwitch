package com.donald.dps.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

fun KClass<*>.field(name: String): Field {
    return java.field(name)
}

fun Class<*>.field(name: String): Field {
    val field = getDeclaredField(name)
    field.isAccessible = true
    return field
}

fun KClass<*>.method(name: String, vararg parameterTypes: KClass<*>): Method {
    return java.method(name, *parameterTypes)
}

fun Class<*>.method(name: String, vararg parameterTypes: KClass<*>): Method {
    val types = parameterTypes.map { it.java }.toTypedArray()
    val method = getDeclaredMethod(name, *types)
    method.isAccessible = true
    return method
}

internal fun runInIoScope(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block()
    }
}

internal fun runInMainScope(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}
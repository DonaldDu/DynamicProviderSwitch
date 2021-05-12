package com.donald.dps.lib

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(minSdk = 21, manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class InstrumentationTest {
    @Test
    fun hasInstrumentationField() {
        val cls = Class.forName("android.app.ActivityThread")
        val method = cls.getDeclaredMethod("currentActivityThread")
        val currentActivityThread = method.invoke(null)
        val mInstrumentationF = currentActivityThread.javaClass.getDeclaredField("mInstrumentation")
        mInstrumentationF.isAccessible = true
    }
}
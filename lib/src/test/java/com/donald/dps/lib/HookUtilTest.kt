package com.donald.dps.lib

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(minSdk = 21, maxSdk = 27)
@RunWith(RobolectricTestRunner::class)
class HookUtilTest {
    @Test
    fun compatDynamicProvider() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        HookUtil.compatDynamicProvider(appContext)
    }
}
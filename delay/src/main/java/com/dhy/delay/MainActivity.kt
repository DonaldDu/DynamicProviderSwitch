package com.dhy.delay

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.donald.dps.lib.DelayProviderFactory
import com.donald.dps.lib.DelayProviderHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assert(MyDerectContentProvider.started)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!DelayProviderFactory.derectProvider) {
                assert(!MyDelayContentProvider.started)
                DelayProviderHelper.enableDelayProvider(null)
                window.decorView.postDelayed({
                    assert(MyDelayContentProvider.started)//异步启动Provider，只能延迟检查
                }, MyDelayContentProvider.delayMS + 300)
            }

        } else {
            assert(MyDelayContentProvider.started)
        }
    }
}
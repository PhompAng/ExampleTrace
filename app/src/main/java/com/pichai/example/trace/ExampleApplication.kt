package com.pichai.example.trace

import android.app.Application
import androidx.tracing.Trace

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Trace.forceEnableAppTracing()
    }
}

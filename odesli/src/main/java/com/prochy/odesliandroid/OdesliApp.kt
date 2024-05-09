package com.prochy.odesliandroid

import android.app.Application
import com.google.android.material.color.DynamicColors

class OdesliApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}

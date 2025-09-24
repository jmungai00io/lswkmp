package com.lswmobile.app.platform

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

internal object CurrentActivityHolder : Application.ActivityLifecycleCallbacks {
    private var currentActivityRef: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = currentActivityRef?.get()

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivityRef?.get() == activity) {
            currentActivityRef = null
        }
    }

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityRef?.get() == activity) {
            currentActivityRef = null
        }
    }
}

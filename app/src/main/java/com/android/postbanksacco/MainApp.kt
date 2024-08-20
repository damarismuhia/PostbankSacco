package com.android.postbanksacco

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.android.postbanksacco.utils.Constants
import com.android.postbanksacco.utils.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApp: Application() {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
        private var appInstance: MainApp? = null
        fun applicationContext(): Context? {
          return appInstance?.applicationContext
        }
    }
    init {
        appInstance = this
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?
            ) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
               // if (!BuildConfig.DEBUG) {
//                    activity.window.setFlags(
//                        WindowManager.LayoutParams.FLAG_SECURE,
//                        WindowManager.LayoutParams.FLAG_SECURE
//                    )
               // }
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }else{
            Timber.plant(ReleaseTree())
        }

        Constants.baseUrl = getBaseURL()
        Constants.getRootFunction = rootFunction()

    }
    private external fun getBaseURL(): String
    private external fun rootFunction(): Int
}
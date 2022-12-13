package org.ar.call

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.*
import com.kongzue.dialogx.DialogX
import org.ar.call.utils.SpUtil
import kotlin.properties.Delegates
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import com.kongzue.dialogx.style.IOSStyle
import com.tencent.bugly.crashreport.CrashReport
import org.ar.call.service.OnlineService


class CallApplication :Application(), ViewModelStoreOwner,Application.ActivityLifecycleCallbacks{

    private val appViewModel by lazy { ViewModelStore() }
    private var mActivateActivityCount = 0
    var curActivity:Activity? = null

    companion object{
        var callApp : CallApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        callApp = this
        SpUtil.init(this)
        DialogX.init(this)
        DialogX.cancelButtonText="取消"
        DialogX.globalStyle = IOSStyle.style();
        registerActivityLifecycleCallbacks(this)
        CrashReport.initCrashReport(this.applicationContext, "939abb0f89", BuildConfig.DEBUG)

        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStopped(activity: Activity) {
//                Log.d("MyLifeCycle", "onActivityStopped")
            }

            override fun onActivityStarted(activity: Activity) {
//                Log.d("MyLifeCycle", "onActivityStarted")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//                Log.d("MyLifeCycle", "onActivitySaveInstanceState")
            }

            override fun onActivityResumed(activity: Activity) {
//                Log.d("MyLifeCycle", "onActivityResumed")
            }

            override fun onActivityPaused(activity: Activity) {
//                Log.d("MyLifeCycle", "onActivityPaused")
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                mActivateActivityCount++
                Log.d("MyLifeCycle", "onActivityCreated")
            }

            override fun onActivityDestroyed(activity: Activity) {

                Log.d("MyLifeCycle", "onActivityDestroyed")
            }

            override fun onActivityPreDestroyed(activity: Activity) {
                mActivateActivityCount--
                if (mActivateActivityCount == 0) {
                    val editor = getSharedPreferences("runStatusData", Context.MODE_PRIVATE).edit()
                    editor.putBoolean("foreground", false)
                    editor.apply()

                    val intent = Intent("OnlineService")
                    intent.setPackage("org.ar.call")
                    startService(intent) // 启动OnlineService
                }
                Log.d("MyLifeCycle", "onActivityPreDestroyed")
                super.onActivityPreDestroyed(activity)

            }

        })
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModel
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        curActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }


}
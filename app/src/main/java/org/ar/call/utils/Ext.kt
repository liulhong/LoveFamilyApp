package org.ar.call.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


fun Activity.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(text:String){
    Toast.makeText(activity,text,Toast.LENGTH_SHORT).show()
}

fun<T> Activity.go(clazz: Class<T>){
    startActivity(Intent().apply {
        setClass(this@go,clazz)
    })
}


fun<T> Activity.goAndFinish(clazz: Class<T>){
    startActivity(Intent().apply {
        setClass(this@goAndFinish,clazz)
        finish()
    })
}

fun View.gone(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun Float.dp2px():Int{
    return (0.5f + this * org.ar.call.CallApplication.callApp.resources.displayMetrics.density).roundToInt()
}


/**
 * 默认主线程的协程
 */
fun launch(
    block: suspend (CoroutineScope) -> Unit,
    error_: ((e: Throwable) -> Unit)? = null,
    context: CoroutineContext = Dispatchers.Main
) = GlobalScope.launch(context + CoroutineExceptionHandler { _, e ->
    error_?.let { it(e) }
}) {
    try {
        block(this)
    } catch (e: Exception) {
        e.printStackTrace()
        if (e is SocketTimeoutException) {
        }
        error_?.let { it(e) }
    }
}

fun Activity.showError(text:String){
    TipDialog.show(this as AppCompatActivity,text, WaitDialog.TYPE.ERROR)
}

fun Activity.showSuccess(text:String){
    TipDialog.show(this as AppCompatActivity,text, WaitDialog.TYPE.SUCCESS)
}

fun toast (context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun <T> Boolean?.matchValue(valueTrue: T, valueFalse: T): T {
    return if (this == true) valueTrue else valueFalse
}

fun getPackageContext(context: Context, packageName: String?): Context? {
    var pkgContext: Context? = null
    if (context.getPackageName().equals(packageName)) {
        pkgContext = context
    } else {
        try {
            pkgContext = context.createPackageContext(
                packageName, Context.CONTEXT_IGNORE_SECURITY
                        or Context.CONTEXT_INCLUDE_CODE
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
    return pkgContext
}

@SuppressLint("WrongConstant")
fun getAppOpenIntentByPackageName(context: Context, packageName: String): Intent? {
    var mainAct: String? = null
    // 根据包名寻找MainActivity
    val pkgMag = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
    val list = pkgMag.queryIntentActivities(
        intent,
        PackageManager.GET_ACTIVITIES
    )
    for (i in list.indices) {
        val info = list[i]
        if (info.activityInfo.packageName == packageName) {
            mainAct = info.activityInfo.name
            break
        }
    }
    if (TextUtils.isEmpty(mainAct)) {
        return null
    }
    intent.component = ComponentName(packageName, mainAct!!)
    return intent
}

fun getSpValue(key:String):Int{
    return SpUtil.get().getInt(key,2)
}


object DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getAndroiodScreenProperty(context: Context) : Pair<Int, Int> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels // 屏幕宽度（像素）
        val height = dm.heightPixels // 屏幕高度（像素）
        val density = dm.density // 屏幕密度（0.75 / 1.0 / 1.5）
        val densityDpi = dm.densityDpi // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        val screenWidth = (width / density).toInt() // 屏幕宽度(dp)
        val screenHeight = (height / density).toInt() // 屏幕高度(dp)
        return Pair(screenWidth, screenHeight)
    }

}
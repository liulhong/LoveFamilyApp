package org.ar.call.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.kongzue.dialogx.dialogs.WaitDialog
import org.ar.call.BuildConfig
import org.ar.call.R
import org.ar.call.ui.BaseActivity
import org.ar.call.ui.MainActivity
import org.ar.call.utils.showError
import org.ar.call.utils.showSuccess

class OnlineService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @SuppressLint("ServiceCast")
    override fun onCreate() {
        super.onCreate()
        Log.d("OnlineService", "onCreate executed")
        //在Service服务类中发送广播消息给Activity活动界面
        val intentBroadcastReceiver : Intent = Intent()
        intentBroadcastReceiver.setAction(MainActivity.ACTION_SERVICE_NEED)
        sendBroadcast(intentBroadcastReceiver)

//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
//                NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("my_service", "前台Service通知",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            manager.createNotificationChannel(channel)
//        }
//        val intent = Intent(this, MainActivity::class.java)
//        val pi = PendingIntent.getActivity(this, 0, intent, 0)
//        val notification = NotificationCompat.Builder(this, "my_service")
//            .setContentTitle("This is content title")
//            .setContentText("This is content text")
//            .setSmallIcon(R.drawable.small_icon)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon))
//            .setContentIntent(pi)
//            .build()
//        startForeground(1, notification)
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("OnlineService", "onStartCommand executed")
        //在Service服务类中发送广播消息给Activity活动界面
//        val intentBroadcastReceiver : Intent = Intent()
//        intentBroadcastReceiver.setAction(MainActivity.ACTION_SERVICE_NEED)
//        sendBroadcast(intentBroadcastReceiver)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("OnlineService", "onDestroy executed")

    }

}
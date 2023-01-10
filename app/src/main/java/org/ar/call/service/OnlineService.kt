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
import kotlinx.coroutines.*
import org.ar.call.R
import org.ar.call.ui.*

class OnlineService : Service() {

    private var ForeGround = false
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    @SuppressLint("NotificationId0")
    override fun onCreate() {
        super.onCreate()
        Log.d("MyLifeCycle", "onCreate: OnlineService")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_service", "前台Service通知",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentTitle("This is content title")
            .setContentText("This is content text")
            .setSmallIcon(R.drawable.small_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon))
            .setContentIntent(pi)
            .build()
        startForeground(0, notification)

        val prefs = getSharedPreferences("runStatusData", MODE_PRIVATE)
        val editor = prefs.edit()
        ForeGround = prefs.getBoolean("foreground", false)
        if (ForeGround) {
            editor.putBoolean("foreground", false)
            editor.apply()
        } else {
            Log.d("MyLifeCycle", "onJump: OnlineService to MainActivity")
            var intent = Intent(this@OnlineService, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("BackGround", true)
            startActivity(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyLifeCycle", "onStartCommand: OnlineService")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("MyLifeCycle", "onDestroy: OnlineService")
        val mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val NOTICE_ID = 0
        mManager.cancel(NOTICE_ID)
        super.onDestroy()

    }
}

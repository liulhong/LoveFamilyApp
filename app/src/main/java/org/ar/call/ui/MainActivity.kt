package org.ar.call.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.kongzue.dialogx.dialogs.MessageDialog
import org.ar.call.*
import org.ar.call.databinding.ActivityMainBinding
import org.ar.call.service.OnlineService
import org.ar.call.utils.go
import org.ar.call.utils.showError
import org.ar.rtm.RemoteInvitation
import org.json.JSONObject


class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var BackGround = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 判断呼叫Id是否设置
        val prefs = getSharedPreferences("globalData", Context.MODE_PRIVATE)
        val userId = prefs.getString("userId", "")
        if (userId == "") {
            go(EditUserIdActivity::class.java)
        }
        setContentView(binding.root)
        ViewCompat.setTransitionName(binding.ivLogo, "logo")
        loginRtm()
        BackGround = intent.getBooleanExtra("BackGround", false)
        if (BackGround) {
            Log.d("MyLog", "BackGround is True")
            moveTaskToBack(true)
        } else {
            val editor = getSharedPreferences("runStatusData", Context.MODE_PRIVATE).edit()
            editor.putBoolean("foreground", true)
            editor.apply()
        }

        binding.run {
            tvUser.text = "您的呼叫ID:${callViewModel.userId}"
            btnP2p.setOnClickListener {
                if (callViewModel.isLoginSuccess) {
//                    go(P2PActivity::class.java)
                    go(P2PCallActivity::class.java)
                } else {
                    showReLoginDialog()
                }
            }
            btnMultiple.setOnClickListener {
                if (callViewModel.isLoginSuccess) {
                   go(GroupCallActivity::class.java)
                } else {
                    showReLoginDialog()
                }
            }
            modifyUserId.paint.flags = Paint. UNDERLINE_TEXT_FLAG
            modifyUserId.setOnClickListener {
                startActivity(Intent().apply {
                    setClass(this@MainActivity, EditUserIdActivity::class.java)
                    putExtra("modify",true)//是否是收到呼叫 no
                })
            }
        }

    }

    override fun onStop() {
        super.onStop()
        Log.d("MyLifeCycle", "onStop: MainActivity")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MyLifeCycle", "onResume: MainActivity")
    }
    override fun onDestroy() {
        val intent = Intent(this, OnlineService::class.java)
        stopService(intent) // 停止Service
        super.onDestroy()
        Log.d("MyLifeCycle", "onDestroy: MainActivity")
    }

    private fun loginRtm() {
        lifecycleScope.launchWhenResumed {
            if (callViewModel.login()) {
                Log.d("loginRtm", "MainActivity: 登录成功")
            } else {
                if (BuildConfig.APPID.equals("YOUR APPID")){
                    showError("登录失败，请配置APPID")
                }else{
                    showError("登录失败，请检查网络")
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showReLoginDialog(){
        MessageDialog.show("提示", "登录RTM失败，请检查网络", "重新登录","取消")
            .setOkButtonClickListener { baseDialog, v ->
                baseDialog.dismiss()
                loginRtm()
                true
            }.setCancelable(false)
    }

    override fun onRemoteInvitationReceived(var1: RemoteInvitation?) {
        super.onRemoteInvitationReceived(var1)
        Log.d("RtmEvents", "MainActivity：onRemoteInvitationReceived: ")
        val isMultiple = JSONObject(var1?.content)["Conference"]
        startActivity(Intent().apply {
            if (isMultiple==1||isMultiple==true){
                setClass(this@MainActivity, GroupCallActivity::class.java)
            }else{
                setClass(this@MainActivity, P2PVideoActivity::class.java)
            }
            putExtra("isCalled",true)//是否是收到呼叫 no
        })

    }



}
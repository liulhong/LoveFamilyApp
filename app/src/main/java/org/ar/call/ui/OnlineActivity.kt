package org.ar.call.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import kotlinx.coroutines.launch
import org.ar.call.BuildConfig
import org.ar.call.R
import org.ar.call.databinding.ActivityOnlineBinding
import org.ar.call.utils.showError
import org.ar.call.utils.showSuccess
import org.ar.rtm.RemoteInvitation
import org.json.JSONObject

class OnlineActivity : BaseActivity() {
    private val binding by lazy { ActivityOnlineBinding.inflate(layoutInflater) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moveTaskToBack(true)
        setContentView(binding.root)
        loginRtm()
        Log.d("MyLifeCycle", "onCreate: OnlineActivity")

        binding.run {
            tvUser1.text = "您的呼叫ID:1234"
        }
    }

    private fun loginRtm() {
        lifecycleScope.launch {
//            WaitDialog.show("正在登录...")
            if (callViewModel.login()) {
//            if (callViewModel.isLoginSucce
//                showSuccess("登录成功")
                Log.d("loginRtm", "OnlineActivity: 登录成功")
            } else {
                if (BuildConfig.APPID.equals("YOUR APPID")){
//                    showError("登录失败，请配置APPID")
                    Log.d("loginRtm", "OnlineActivity: 登录失败，请配置APPID")
                }else{
//                    showError("登录失败，请检查网络")
                    Log.d("loginRtm", "OnlineActivity: 登录失败，请检查网络")
                }
            }
        }
    }

    override fun onRemoteInvitationReceived(var1: RemoteInvitation?) {
//        super.onRemoteInvitationReceived(var1)
        Log.d("RtmEvents", "OnlineActivity：onRemoteInvitationReceived: ")
        val isMultiple = JSONObject(var1?.content)["Conference"]
        startActivity(Intent().apply {
            if (isMultiple==1||isMultiple==true){
                setClass(this@OnlineActivity, GroupCallActivity::class.java)
            }else{
                setClass(this@OnlineActivity, P2PVideoActivity::class.java)
            }
            putExtra("isCalled",true)//是否是收到呼叫 no
            putExtra("isFromOnline", true)
        })
//        Toast.makeText(this, "来电显示", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
//        finish()
        super.onResume()
        Log.d("MyLifeCycle", "onResume: OnlineActivity")

//        moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyLifeCycle", "onDestroy: OnlineActivity")
    }
}
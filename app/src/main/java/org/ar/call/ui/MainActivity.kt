package org.ar.call.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import org.ar.call.*
import org.ar.call.databinding.ActivityMainBinding
import org.ar.call.service.OnlineService
import org.ar.call.utils.go
import org.ar.call.utils.showError
import org.ar.call.utils.showSuccess
import org.ar.rtm.RemoteInvitation
import org.json.JSONObject


class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //定义静态变量
    companion object {
        //声明一个静态操作常量字符串
        public val ACTION_SERVICE_NEED : String = "action.ServiceNeed"
    }
    //声明一个内部广播实例
    private lateinit var broadcastReceiver : ServiceNeedBroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivityLod", "onCreate: ")
        setContentView(binding.root)
        ViewCompat.setTransitionName(binding.ivLogo, "logo")
//        loginRtm()
        val intent = Intent(this, OnlineService::class.java)
        startService(intent) // 启动OnlineService
        /**
         * 注册广播实例（在初始化的时候）
         */
        val filter = IntentFilter()
        filter.addAction(ACTION_SERVICE_NEED)
        broadcastReceiver = ServiceNeedBroadcastReceiver()
        registerReceiver(broadcastReceiver, filter)


        binding.run {
            tvUser.text = "您的呼叫ID:${callViewModel.userId}"
            btnP2p.setOnClickListener {
                if (callViewModel.isLoginSuccess) {
                    go(P2PActivity::class.java)
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
        }

    }

    private fun loginRtm() {
        lifecycleScope.launchWhenResumed {
            WaitDialog.show("正在登录...")
            if (callViewModel.login()) {
//            if (callViewModel.isLoginSuccess) {
                showSuccess("登录成功")
            } else {
                if (BuildConfig.APPID.equals("YOUR APPID")){
                    showError("登录失败，请配置APPID")
                }else{
                    showError("登录失败，请检查网络")
                }
            }
        }
    }

    /**
     * 定义广播接收器，用于执行Service服务的需求（内部类）
     */
    inner class ServiceNeedBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //这里是要在Activity活动里执行的代码
            loginRtm()
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            showExitDialog()
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showExitDialog() {
        MessageDialog.show("提示", "确定要退出吗？", "确定","取消")
            .setOkButtonClickListener { baseDialog, v ->
//                callViewModel.release()
//                exitProcess(0)
                finish()
                true
            }.setCancelable(false)

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
        val isMultiple = JSONObject(var1?.content)["Conference"]
        startActivity(Intent().apply {
            if (isMultiple==1||isMultiple==true){
                setClass(this@MainActivity, GroupCallActivity::class.java)
            }else{
                setClass(this@MainActivity, P2PVideoActivity::class.java)
            }
            putExtra("isCalled",true)//是否是收到呼叫 no
        })
//        Toast.makeText(this, "来电显示", Toast.LENGTH_SHORT).show()

    }



}
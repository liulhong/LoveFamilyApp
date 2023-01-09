package org.ar.call.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import org.ar.call.databinding.ActivityEditUserIdBinding
import org.ar.call.utils.go
import org.ar.call.utils.showError
import org.ar.call.utils.showSuccess
import org.ar.call.utils.toast
import org.ar.rtm.RemoteInvitation
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditUserIdActivity : BaseActivity() {
    private val binding by lazy { ActivityEditUserIdBinding.inflate(layoutInflater) }
    private var modify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        modify = intent.getBooleanExtra("modify", false)
        binding.run {
            if (modify) {
                val prefs = getSharedPreferences("globalData", Context.MODE_PRIVATE)
                val userId = prefs.getString("userId", "")
                etUserId.text = Editable.Factory.getInstance().newEditable(userId)
            }
            btnUserId.setOnClickListener {
                if (etUserId.text.toString().isNullOrEmpty()) {
                    showError("请输入呼叫ID")
                    return@setOnClickListener
                }
                if (11 < etUserId.text.toString().length) {
                    showError("长度超过11")
                    return@setOnClickListener
                }
                if (!isNumeric(etUserId.text.toString())) {
                    showError("呼叫ID只能包含数字")
                    return@setOnClickListener
                }
                val editor = getSharedPreferences("globalData", Context.MODE_PRIVATE).edit()
                editor.putString("userId", etUserId.text.toString())
                editor.apply()
                callViewModel.userId = etUserId.text.toString()
                toast(this@EditUserIdActivity, "设置成功")
                go(MainActivity::class.java)

            }
        }
    }

    private fun isNumeric(str: String?): Boolean {
        val pattern: Pattern = Pattern.compile("[0-9]*")
        val isNum: Matcher = pattern.matcher(str)
        return isNum.matches()
    }

    override fun onRemoteInvitationReceived(var1: RemoteInvitation?) {
        super.onRemoteInvitationReceived(var1)
        val isMultiple = JSONObject(var1?.content)["Conference"]
        startActivity(Intent().apply {
            if (isMultiple==1||isMultiple==true){
                setClass(this@EditUserIdActivity, GroupCallActivity::class.java)
            }else{
                setClass(this@EditUserIdActivity, P2PVideoActivity::class.java)
            }
            putExtra("isCalled",true)//是否是收到呼叫 no
        })
    }

}
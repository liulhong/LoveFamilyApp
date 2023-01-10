package org.ar.call.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

/**
 * 选择照片的协定
 * Input type  : Unit? 不需要传值
 * Output type : Uri?  选择完成后的 image uri
 */
class SelectPhotoContract : ActivityResultContract<Unit?, Uri?>() {

    @CallSuper
    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        Log.d("pickPhoto", "pick photo result: ${intent?.data}")
        return intent?.data
    }
}
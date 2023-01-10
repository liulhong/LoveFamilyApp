package org.ar.call.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.content.contentValuesOf
import java.io.File

/**
 * 剪裁照片的协定
 * Input type  : CropParams 剪裁照片的相关参数
 * Output type : Uri?       照片剪裁完成后的uri
 */
class CropPhotoContract : ActivityResultContract<CropParams, Pair<Uri?, String>>() {

    private var outputUri: Uri? = null
    private var imageName: String = ""
    @CallSuper
    override fun createIntent(context: Context, input: CropParams): Intent {
        // 获取输入图片uri的媒体类型
        val mimeType = context.contentResolver.getType(input.uri)
        // 创建新的图片名称
        imageName = "${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }"
        Log.d("printData", Environment.getExternalStorageDirectory().toString() + Environment.DIRECTORY_DCIM + imageName)
        outputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                 Android 10 及以上获取图片uri
                val values = contentValuesOf(
                    Pair(MediaStore.MediaColumns.DISPLAY_NAME, imageName),
                    Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                    Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                )
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                Uri.fromFile(File(context.externalCacheDir!!.absolutePath, imageName))
            }

        return Intent("com.android.camera.action.CROP")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(input.uri, mimeType)
            .putExtra("outputX", input.outputX)
            .putExtra("outputY", input.outputY)
            .putExtra("aspectX", input.aspectX)
            .putExtra("aspectY", input.aspectY)
            .putExtra("scale", input.scale)
            .putExtra("crop", input.crop)
            .putExtra("return-data", input.returnData)
            .putExtra("noFaceDetection", input.noFaceDetection)
            .putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            .putExtra("outputFormat", input.outputFormat)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Uri?, String> {
        Log.d("pickPhoto", "crop photo outputUri : $outputUri")
        return Pair(outputUri, imageName)
    }
}
package org.ar.call.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

object PicFunc {
    fun blobToBitmap(picByteArray: ByteArray): Bitmap {
        val opts: BitmapFactory.Options = BitmapFactory.Options()
        return BitmapFactory.decodeByteArray(picByteArray, 0, picByteArray.size, opts)
    }
}
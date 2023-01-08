package org.ar.call.bean

import android.graphics.Bitmap

class Person(var id: Int, var name:String, var image: Bitmap?, var editIcon: Int, var callId: String="", var callFree: Boolean=false) {
    constructor() : this(0, "", null, 0) {}
}
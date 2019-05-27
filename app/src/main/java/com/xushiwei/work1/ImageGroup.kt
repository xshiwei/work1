package com.xushiwei.work1

import android.graphics.Bitmap
import android.graphics.Matrix

class ImageGroup {
    var bitmap: Bitmap? = null
    var matrix: Matrix? = Matrix()

    fun release() {
        if (bitmap != null) {
            bitmap?.recycle()
            bitmap = null
        }
        if (matrix != null) {
            matrix?.reset()
            matrix = null
        }
    }


}
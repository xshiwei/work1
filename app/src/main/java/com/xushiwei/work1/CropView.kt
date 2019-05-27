package com.xushiwei.work1

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class CropView(context: Context, attributeSet: AttributeSet) : BaseView(context, attributeSet) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {

            MotionEvent.ACTION_POINTER_DOWN -> {
                downMatrix.set(mCropImageGroup.matrix)
                mode = ZOOM
                oldDistance = getDistance(event)
                midPoint = midPoint(event)
            }

            MotionEvent.ACTION_MOVE -> if (mode === ZOOM) {
                moveMatrix.set(downMatrix)
                val newDist = getDistance(event)
                val scale = newDist / oldDistance
                moveMatrix.postScale(scale, scale, midPoint.x, midPoint.y)// 縮放
                mCropImageGroup.matrix!!.set(moveMatrix)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (mCropImageGroup.bitmap != null) {
                    matrixFix()
                }
                mode = NONE
            }

            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        return true
    }

    private fun matrixFix() {
        val points = getBitmapPoints(mCropImageGroup.bitmap!!, moveMatrix)
        val x1 = points[0]
        val y1 = points[1]
        val x2 = points[2]
        val y3 = points[5]

        if (mCropImageGroup.bitmap!!.getWidth() <= mCropImageGroup.bitmap!!.getHeight()) {
            if (x2 - x1 < width) {
                moveMatrix.set(matrixBig)
            }

            if (y3 - y1 < height) {
                moveMatrix.set(matrixSmall)
            }
        } else if (mCropImageGroup.bitmap!!.getWidth() > mCropImageGroup.bitmap!!.getHeight()) {
            if (y3 - y1 < height) {
                moveMatrix.set(matrixBig)
            }

            if (x2 - x1 < width) {
                moveMatrix.set(matrixSmall)
            }
        }

        if (!moveMatrix.equals(matrixBig) && !moveMatrix.equals(matrixSmall)) {
            if (x1 >= targetRect.left) {
                moveMatrix.postTranslate(targetRect.left - x1, 0F)
            }

            if (x2 <= targetRect.left + width) {
                moveMatrix.postTranslate(width - x2, 0F)
            }

            if (y1 >= targetRect.top) {
                moveMatrix.postTranslate(0F, targetRect.top - y1)
            }

            if (y3 <= targetRect.top + height) {
                moveMatrix.postTranslate(0F, targetRect.top + height - y3)
            }
        }

        mCropImageGroup.matrix!!.set(moveMatrix)
        invalidate()
    }
}
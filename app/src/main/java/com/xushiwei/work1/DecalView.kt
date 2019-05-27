package com.xushiwei.work1

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import java.util.ArrayList

class DecalView : BaseView {
    private lateinit var mPaintForLineAndCircle: Paint

    private var moveTag = 0
    private var transformTag = 0
    private var deleteTag = 0

    private val mDecalImageGroupList = ArrayList<ImageGroup>()

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        mPaintForLineAndCircle = Paint()
        mPaintForLineAndCircle.isAntiAlias = true
        mPaintForLineAndCircle.color = Color.BLACK
        mPaintForLineAndCircle.alpha = 170
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (imageGroup in mDecalImageGroupList) {
            val points = getBitmapPoints(imageGroup)
            val x1 = points[0]
            val y1 = points[1]
            val x2 = points[2]
            val y2 = points[3]
            val x3 = points[4]
            val y3 = points[5]
            val x4 = points[6]
            val y4 = points[7]

            canvas.drawLine(x1, y1, x2, y2, mPaintForLineAndCircle)
            canvas.drawLine(x2, y2, x4, y4, mPaintForLineAndCircle)
            canvas.drawLine(x4, y4, x3, y3, mPaintForLineAndCircle)
            canvas.drawLine(x3, y3, x1, y1, mPaintForLineAndCircle)
            canvas.drawCircle(x2, y2, 40f, mPaintForLineAndCircle)

            canvas.drawBitmap(imageGroup.bitmap, imageGroup.matrix, mPaintForBitmap)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                anchorX = event.x
                anchorY = event.y
                moveTag = decalCheck(anchorX, anchorY)
                deleteTag = deleteCheck(anchorX, anchorY)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                moveTag = decalCheck(event.getX(0), event.getY(0))
                transformTag = decalCheck(event.getX(1), event.getY(1))
                if (moveTag != -1 && transformTag == moveTag && deleteTag == -1) {
                    downMatrix.set(mDecalImageGroupList[moveTag].matrix)
                    mode = ZOOM
                }
                oldDistance = getDistance(event)
                oldRotation = getRotation(event)

                midPoint = midPoint(event)
            }

            MotionEvent.ACTION_MOVE -> if (mode === ZOOM) {
                moveMatrix.set(downMatrix)
                val newRotation = getRotation(event) - oldRotation
                val newDistance = getDistance(event)
                val scale = newDistance / oldDistance
                moveMatrix.postScale(scale, scale, midPoint.x, midPoint.y)// 縮放
                moveMatrix.postRotate(newRotation, midPoint.x, midPoint.y)// 旋轉
                if (moveTag != -1) {
                    mDecalImageGroupList[moveTag].matrix!!.set(moveMatrix)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (deleteTag != -1) {
                    mDecalImageGroupList.removeAt(deleteTag).release()
                    invalidate()
                }
                mode = NONE
            }

            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        return true
    }

    private fun pointCheck(imageGroup: ImageGroup, x: Float, y: Float): Boolean {
        val points = getBitmapPoints(imageGroup)
        val x1 = points[0]
        val y1 = points[1]
        val x2 = points[2]
        val y2 = points[3]
        val x3 = points[4]
        val y3 = points[5]
        val x4 = points[6]
        val y4 = points[7]

        val edge = Math.sqrt(Math.pow((x1 - x2).toDouble(), 2.0) + Math.pow((y1 - y2).toDouble(), 2.0)).toFloat()
        return (2 + Math.sqrt(2.0)) * edge >= (Math.sqrt(
            Math.pow(
                (x - x1).toDouble(),
                2.0
            ) + Math.pow((y - y1).toDouble(), 2.0)
        )
                + Math.sqrt(Math.pow((x - x2).toDouble(), 2.0) + Math.pow((y - y2).toDouble(), 2.0))
                + Math.sqrt(Math.pow((x - x3).toDouble(), 2.0) + Math.pow((y - y3).toDouble(), 2.0))
                + Math.sqrt(Math.pow((x - x4).toDouble(), 2.0) + Math.pow((y - y4).toDouble(), 2.0)))
    }

    private fun circleCheck(imageGroup: ImageGroup, x: Float, y: Float): Boolean {
        val points = getBitmapPoints(imageGroup)
        val x2 = points[2]
        val y2 = points[3]

        val checkDis = Math.sqrt(Math.pow((x - x2).toDouble(), 2.0) + Math.pow((y - y2).toDouble(), 2.0)).toInt()

        return checkDis < 40
    }

    private fun deleteCheck(x: Float, y: Float): Int {
        for (i in mDecalImageGroupList.indices) {
            if (circleCheck(mDecalImageGroupList[i], x, y)) {
                return i
            }
        }
        return -1
    }

    private fun decalCheck(x: Float, y: Float): Int {
        for (i in mDecalImageGroupList.indices) {
            if (pointCheck(mDecalImageGroupList[i], x, y)) {
                return i
            }
        }
        return -1
    }

    fun addDecal(bitmap: Bitmap) {
        val imageGroupTemp = ImageGroup()
        imageGroupTemp.bitmap = bitmap
        if (imageGroupTemp.matrix == null) {
            imageGroupTemp.matrix = Matrix()
        }
        val transX = (width - imageGroupTemp.bitmap!!.getWidth()) / 2
        val transY = (height - imageGroupTemp.bitmap!!.getHeight()) / 2
        imageGroupTemp.matrix!!.postTranslate(transX.toFloat(), transY.toFloat())
        imageGroupTemp.matrix!!.postScale(0.5f, 0.5f, (width / 2).toFloat(), (height / 2).toFloat())
        mDecalImageGroupList.add(imageGroupTemp)

        invalidate()
    }
}
package com.xushiwei.work1

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class BaseView : View {

    interface OnSizeChangeListerer {
        fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    }

    companion object {
        const val NONE = 0
        const val ZOOM = 1
    }

    protected var mode: Int = NONE
    protected var anchorX: Float = 0F
    protected var anchorY: Float = 0F
    protected var oldDistance: Float = 1F
    protected var oldRotation: Float = 0F

    protected var midPoint: PointF = PointF()
    protected var moveMatrix: Matrix = Matrix()
    protected var downMatrix: Matrix = Matrix()
    protected var matrixBig: Matrix = Matrix()
    protected var matrixSmall: Matrix = Matrix()

    protected lateinit var targetRect: RectF
    protected var isFirst: Boolean = true

    protected var mOnSizeChangedListener: OnSizeChangeListerer? = null

    protected var mCropImageGroup: ImageGroup = ImageGroup()

    protected var mPaintForBitmap: Paint

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        mPaintForBitmap = Paint()
        mPaintForBitmap.isAntiAlias = true
        mPaintForBitmap.isFilterBitmap = true
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            targetRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mCropImageGroup.bitmap != null) {
            canvas.drawBitmap(mCropImageGroup.bitmap, mCropImageGroup.matrix, mPaintForBitmap)
        }
    }

    // 触碰两点间距
    fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt(Math.pow(x.toDouble(), 2.0) + Math.pow(y.toDouble(), 2.0)).toFloat()
    }

    // 取手势中心点
    fun midPoint(event: MotionEvent): PointF {
        val point = PointF()
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)

        return point
    }

    // 取旋转角
    fun getRotation(event: MotionEvent): Float {
        val x = (event.getX(0) - event.getX(1)).toDouble()
        val y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isFirst) {
            isFirst = false
            setBackgroundBitmap()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mOnSizeChangedListener != null) {
            mOnSizeChangedListener!!.onSizeChanged(w, h, oldw, oldh)
        }
        setBackgroundBitmap()
    }

    fun setBackgroundBitmap() {
        if (mCropImageGroup.bitmap != null) {
            setBackgroundBitmap(mCropImageGroup.bitmap!!)
        }
    }

    fun setBackgroundBitmap(bitmap: Bitmap) {
        mCropImageGroup.bitmap = bitmap
        if (mCropImageGroup.matrix == null) {
            mCropImageGroup.matrix = Matrix()
        }
        mCropImageGroup.matrix!!.reset()

        if (matrixBig != null && matrixSmall != null) {
            matrixBig.reset()
            matrixSmall.reset()
        }

        var scale: Float
        val transY = ((height - mCropImageGroup.bitmap!!.getHeight()) / 2).toFloat()
        val transX = ((width - mCropImageGroup.bitmap!!.getWidth()) / 2).toFloat()

        matrixBig.postTranslate(transX, transY)
        if (mCropImageGroup.bitmap!!.getHeight() <= mCropImageGroup.bitmap!!.getWidth()) {
            scale = height.toFloat() / mCropImageGroup.bitmap!!.getHeight()
        } else {
            scale = width.toFloat() / mCropImageGroup.bitmap!!.getWidth()
        }
        matrixBig.postScale(scale, scale, (width / 2).toFloat(), (height / 2).toFloat())

        matrixSmall.postTranslate(transX, transY)
        if (mCropImageGroup.bitmap!!.getHeight() >= mCropImageGroup.bitmap!!.getWidth()) {
            scale = width.toFloat() / mCropImageGroup.bitmap!!.getHeight()
        } else {
            scale = width.toFloat() / mCropImageGroup.bitmap!!.getWidth()
        }
        matrixSmall.postScale(scale, scale, (width / 2).toFloat(), (height / 2).toFloat())

        mCropImageGroup.matrix!!.set(matrixBig)

        invalidate()
    }

    protected fun getBitmapPoints(imageGroup: ImageGroup): FloatArray {
        return getBitmapPoints(imageGroup.bitmap!!, imageGroup.matrix!!)
    }

    protected fun getBitmapPoints(bitmap: Bitmap, matrix: Matrix): FloatArray {
        val dst = FloatArray(8)
        val src = floatArrayOf(
            0f,
            0f,
            bitmap.width.toFloat(),
            0f,
            0f,
            bitmap.height.toFloat(),
            bitmap.width.toFloat(),
            bitmap.height.toFloat()
        )

        matrix.mapPoints(dst, src)
        return dst
    }


}
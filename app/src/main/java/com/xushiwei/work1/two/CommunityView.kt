package com.xushiwei.work1.two

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout

/**
 * Created by 54966 on 2018/6/27.
 */

class CommunityView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val halfWidth: Int
    private val halfHeight: Int        // w:540 h:1004

    private lateinit var mMatrix: Matrix

    private val matrixValue = FloatArray(9)

    private var lastX: Int = 0

    private var lastY: Int = 0

    private var downX: Int = 0

    private var downY: Int = 0

    private var isScale: Boolean = false

    private var pointer: Boolean = false

    internal var scaleGestureDetector =
        ScaleGestureDetector(getContext(), object : ScaleGestureDetector.OnScaleGestureListener {

            override fun onScale(detector: ScaleGestureDetector): Boolean {

                var scaleFactor =
                    detector.scaleFactor                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    // scaleFactor:2
                // getMatrixScale:2
                if (matrixScaleX * scaleFactor <= 1.0f) {
                    scaleFactor = 1.0f / matrixScaleX
                } else if (matrixScaleX * scaleFactor >= 2.0f) {
                    scaleFactor = 2.0f / matrixScaleX
                }

                mMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                invalidate()

                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                isScale = true
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                isScale = false
            }
        })

    internal var gestureDetector = GestureDetector(getContext(), object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val x = e.x.toInt()
            val y = e.y.toInt()
            val rawX = e.rawX.toInt()
            val rawY = e.rawY.toInt()
            Log.e("onSingleTapConfirmed", "x=" + x + "y=" + y + "rawX=" + rawX + "rawY=" + rawY)

            return super.onSingleTapConfirmed(e)
        }
    })

    val matrixTranslateX: Float
        get() {
            mMatrix.getValues(matrixValue)
            return matrixValue[Matrix.MTRANS_X]
        }

    val matrixTranslateY: Float
        get() {
            mMatrix.getValues(matrixValue)
            return matrixValue[Matrix.MTRANS_Y]
        }

    val matrixScaleX: Float
        get() {
            mMatrix.getValues(matrixValue)
            return matrixValue[Matrix.MSCALE_X]
        }

    val matrixScaleY: Float
        get() {
            mMatrix.getValues(matrixValue)
            return matrixValue[Matrix.MSCALE_Y]
        }

    init {

        halfWidth = getScreenWidth(context) / 2
        halfHeight =  getScreenHeight(context) / 2
        mMatrix = Matrix()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.concat(mMatrix)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        gestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)

        if (event.pointerCount > 1) {
            pointer = true
        }

        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
                pointer = false
            }
            MotionEvent.ACTION_MOVE -> if (!isScale) {
                var dx = Math.abs(x - downX)
                var dy = Math.abs(y - downY)
                if (dx > 10 && dy > 10 && !pointer) {
                    dx = x - lastX
                    dy = y - lastY
                    mMatrix.postTranslate(dx.toFloat(), dy.toFloat())
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        lastX = x
        lastY = y

        return true
    }


    /***
     * 屏幕的高度
     *
     * @param context
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }

    /***
     * 屏幕的高度
     *
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

}

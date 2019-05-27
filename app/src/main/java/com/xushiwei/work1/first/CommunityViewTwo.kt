package com.xushiwei.work1.first

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.xushiwei.work1.R
import com.xushiwei.work1.first.PathModel
import java.util.ArrayList

/**
 * Created by 54966 on 2018/6/27.
 */

class CommunityViewTwo @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
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

    internal var paint: Paint

    internal var startX = 0f
    internal var startY = 0f

    /**
     * 用来保存绘制的路径
     */
    internal var paths: ArrayList<PathModel>

    private val color: Int


    internal val width = 4

    internal lateinit var path: Path

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
                Log.e("222", "onscale")

                mMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                invalidate()
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                isScale = true
                Log.e("222", "onscaleBegin")
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                Log.e("222", "onscaleEnd")
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

    val matrixScaleX: Float
        get() {
            mMatrix.getValues(matrixValue)
            return matrixValue[Matrix.MSCALE_X]
        }

    init {
        halfWidth = getScreenWidth(context) / 2
        halfHeight = getScreenHeight(context) / 2
        mMatrix = Matrix()
        //初始化画笔
        color = resources.getColor(R.color.Black)
        paint = Paint()
        paint.strokeWidth = width.toFloat()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.STROKE
        paths = ArrayList()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.concat(mMatrix)
        /**
         * 循环绘制集合里面的路径
         */
        for (i in paths.indices) {
            val p = paths[i]
            //每次绘制路径都需要设置画笔的宽度，颜色
            paint.strokeWidth = p.width.toFloat()
            paint.color = p.color
            canvas.drawPath(p.path, paint)
        }
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

                    //当手指按下的时候开始记录绘制路径
                    path = Path()
                    val p = PathModel()
                    p.path = path//保存当前路径
                    p.color = paint.color//保存路径的颜色
                    p.width = paint.strokeWidth.toInt() //保存路径的大小
                    paths.add(p)
                    startX = event.x
                    startY = event.y
                    path.moveTo(startX, startY)
            }
            MotionEvent.ACTION_MOVE -> if (!isScale) {
                path.lineTo(event.x, event.y)
                var dx = Math.abs(x - downX)
                var dy = Math.abs(y - downY)
//                if (dx > 10 && dy > 10 && !pointer) {
//                    dx = x - lastX
//                    dy = y - lastY
//                    mMatrix.postTranslate(dx.toFloat(), dy.toFloat())
//                    invalidate()
//                }
                invalidate()
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

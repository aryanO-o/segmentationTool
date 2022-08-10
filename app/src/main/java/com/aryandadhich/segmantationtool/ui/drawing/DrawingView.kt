package com.aryandadhich.segmantationtool.ui.drawing

import android.R
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrashSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    /**
     * This method initializes the attributes of the
     * ViewForDrawing class.
     */
    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrashSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrashSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    //change color
    fun setColor(newColor: Int) {
        color = newColor
        mDrawPaint!!.color = color
    }

    fun getCurrentColor(): Int {
        return color
    }

    /**
     * This method is called when a stroke is drawn on the canvas
     * as a part of the painting.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        //the draw stays on screen(draw the path that saved in ArrayList)
        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    /**
     * This method acts as an event listener when a touch
     * event is detected on the device.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrashSize
                mDrawPath!!.reset()

                if (touchX != null && touchY != null)
                    mDrawPath!!.moveTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null && touchY != null) {
                    mDrawPath!!.lineTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_UP -> {
                //save current path
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrashSize)
            }
            else -> {
                return false
            }
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrashSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )
    }

    fun onClickUndo() {
        if (mPaths.isNotEmpty()) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))

            invalidate() //call onDraw again
        }
    }

    fun onClickRedo() {
//        if (mUndoPaths.isNotEmpty()) {
//            mPaths.add(mUndoPaths.removeAt(mUndoPaths.size - 1))
//
//            invalidate() //call onDraw again
//        }

        colorAll()
    }


    private fun colorAll(){
//        Toast.makeText(context, "this is a toast", Toast.LENGTH_LONG).show()

        for (i in 0 until mCanvasBitmap?.width!!) {
            for (j in 0 until mCanvasBitmap?.height!!) {
                val pixel: Int = mCanvasBitmap?.getPixel(i, j)!!
                Toast.makeText(context, "this is a init out toast", Toast.LENGTH_LONG).show()
                if (pixel == getCurrentColor()) {
                    Toast.makeText(context, "this is a init toast", Toast.LENGTH_LONG).show()
                    mCanvasBitmap!!.setPixel(i, j, 0xFFFF0000.toInt())
                }
            }
        }
    }

    // An inner class for custom path with two params as color and stroke size.
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }


}
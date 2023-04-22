package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Actual width and heigth of the view
    private var widthSize = 0
    private var heightSize = 0

    // Radius of the circle appearing during loading
    private var circleRadius: Float = 0F

    // Variables for loading
    private var loadingWidth: Float = 0F
    private var loadingAngle: Float = 0F

    // Animator object
    private var widthAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    // Button state observable
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                widthAnimator.apply {
                    setFloatValues(0F, width.toFloat())
                    duration = 1000
                    addUpdateListener {
                        loadingWidth = it.animatedValue as Float
                        invalidate()
                    }
                }
                circleAnimator.apply {
                    setFloatValues(0F, 360F)
                    duration = 1000
                    addUpdateListener {
                        loadingAngle = it.animatedValue as Float
                        invalidate()
                    }
                }
                buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                AnimatorSet().apply {
                    playTogether(widthAnimator, circleAnimator)
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(p0: Animator?) {
                            buttonState = ButtonState.Completed
                        }
                    })
                    start()
                }
            }
            ButtonState.Completed -> {
                loadingWidth = 0F
                loadingAngle = 0F
                invalidate()
            }
        }
    }

    // Attribute that are specified in the layout
    private var textSize: Float = 0F
    private var baseText: String = ""
    private var loadingText: String = ""
    private var baseColor = 0
    private var loadingColor = 0
    private var highlightColor = 0
    private var textColor = 0

    init {
        isClickable = true
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)

        textSize = typedArray.getDimension(R.styleable.LoadingButton_android_textSize, 48F)
        baseText = typedArray.getString(R.styleable.LoadingButton_baseText).toString()
        loadingText = typedArray.getString(R.styleable.LoadingButton_loadingText).toString()
        baseColor = typedArray.getColor(R.styleable.LoadingButton_baseColor, 0)
        loadingColor = typedArray.getColor(R.styleable.LoadingButton_loadingColor, 0)
        highlightColor = typedArray.getColor(R.styleable.LoadingButton_highlightColor, 0)
        textColor = typedArray.getColor(R.styleable.LoadingButton_textColor, 0)

        typedArray.recycle()
    }

    /**
     * Called everytime the view size changes
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        circleRadius = ((h / 2) * 0.5).toFloat()
        widthSize = w
        heightSize = h
    }

    // Paint object with which drawing shapes
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = this@LoadingButton.textSize
    }

    /**
     * Draws the view from scratch
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        when (buttonState) {
            ButtonState.Loading -> {
                // Base rectangle
                paint.color = baseColor
                canvas.drawRect(
                    0F, 0F, width.toFloat(), height.toFloat(), paint
                )

                // Loading rectangle
                paint.color = loadingColor
                canvas.drawRect(0F, 0F, loadingWidth, heightSize.toFloat(), paint)

                // Loading text
                paint.color = textColor
                canvas.drawText(loadingText, (width/2).toFloat(),
                    (height/2).toFloat() + (textSize / 2), paint)

                // Loading circle
                paint.color = highlightColor
                canvas.drawArc((width * 3/4) - circleRadius,
                    (height / 2) - circleRadius,
                    (width * 3/4) + circleRadius,
                    (height / 2) + circleRadius,
                    0F, loadingAngle, true, paint)
            }
            else -> {
                paint.color = baseColor
                canvas.drawRect(
                    0F, 0F, width.toFloat(), height.toFloat(), paint
                )
                paint.color = textColor
                canvas.drawText(baseText, (width / 2).toFloat(),
                    (height / 2).toFloat() + (textSize / 2), paint)
            }
        }
    }


    /**
     * Called when the view is clicked
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        return true
    }


    /**
     * Called to measure the size of the view and its children
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}
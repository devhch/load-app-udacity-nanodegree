package com.devhch.loadapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var buttonText: String
    private var buttonBackgroundColor = R.attr.buttonBackgroundColor
    private var progress: Float = 0f
    private val textRect = Rect()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                setText(context.getString(R.string.button_loading))
                setBackgroundColorFromRes(R.color.colorPrimaryDark)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = ValueAnimator.INFINITE
                    duration = 3000
                    disableViewDuringAnimation()
                    start()
                }
                disableLoadingButton()
            }

            ButtonState.Completed -> {
                setText(context.getString(R.string.download))
                setBackgroundColorFromRes(R.color.colorPrimary)
                valueAnimator.cancel()
                resetProgress()
                enableLoadingButton()
            }

            ButtonState.Clicked -> {
                setText(context.getString(R.string.button_clicked))
                setBackgroundColorFromRes(R.color.colorAccent)
            }
        }
        invalidate()
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                buttonText = getString(R.styleable.LoadingButton_text).toString()
                buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
            } finally {
                recycle()
            }
        }
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0F
        color = Color.WHITE
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    private val inProgressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }

    private val inProgressArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cornerRadius = 10.0F
        val backgroundWidth = measuredWidth.toFloat()
        val backgroundHeight = measuredHeight.toFloat()

        canvas.drawColor(buttonBackgroundColor)
        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        canvas.drawRoundRect(
            0F,
            0F,
            backgroundWidth,
            backgroundHeight,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )

        if (buttonState == ButtonState.Loading) {
            var progressVal = progress * measuredWidth.toFloat()
            canvas.drawRoundRect(
                0F,
                0F,
                progressVal,
                backgroundHeight,
                cornerRadius,
                cornerRadius,
                inProgressBackgroundPaint
            )

            val arcDiameter = cornerRadius * 2
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            progressVal = progress * 360F
            canvas.drawArc(
                paddingStart.toFloat() + arcDiameter,
                paddingTop.toFloat() + arcDiameter,
                arcRectSize,
                arcRectSize,
                0F,
                progressVal,
                true,
                inProgressArcPaint
            )
        }
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - textRect.centerY()

        canvas.drawText(buttonText, centerX, centerY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }

    private fun disableLoadingButton() {
        this.isEnabled = false
    }

    private fun enableLoadingButton() {
        this.isEnabled = true
    }

    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

    private fun setText(buttonText: String) {
        this.buttonText = buttonText
        invalidate()
        requestLayout()
    }

    private fun setBackgroundColorFromRes(id: Int) {
        buttonBackgroundColor = getColor(id)
        invalidate()
        requestLayout()
    }

    private fun resetProgress() {
        progress = 0f
    }

    private fun getColor(id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    private fun ValueAnimator.disableViewDuringAnimation() {
        // This extension method listens for start/end events on an animation and disables
        // the given view for the entirety of that animation.
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator) {
                isEnabled = true
            }
        })
    }
}
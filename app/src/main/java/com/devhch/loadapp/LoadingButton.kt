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
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ValueAnimator
    private var valueAnimator = ValueAnimator()

    // Button Text
    private var buttonText: String = "Null"

    // Button Background Color
    private var buttonBackgroundColor = 0

    // Progress
    private var progress: Float = 0f

    // Button State
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                // Set Button Text to We are loading
                setText(context.getString(R.string.button_loading))

                //  Changes Background Paint Color to colorPrimary when the button state is Loading
                backgroundPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)

                // Change buttonBackgroundColor and Re Draw
                setBackgroundColorFromRecourse(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimaryDark
                    )
                )

                // Start Animation...
                valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    addUpdateListener {
                        progress = animatedValue as Float

                        // Forcing a call to onDraw() to redraw the view.
                        invalidate()
                    }
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = ValueAnimator.INFINITE
                    duration = 2000

                    // Call disableViewDuringAnimation() Method,
                    // and set enabled to false when the animation is started
                    disableViewDuringAnimation()

                    // Start Animation
                    start()
                }

                // Set enabled to false, To avoid pressing the button while downloading
                isEnabled = false
            }

            ButtonState.Completed -> {
                // Set Progress to 0
                progress = 0F

                // Set Button Text to Download
                setText(context.getString(R.string.download))

                //  Changes Background Paint Color to colorPrimary when the button state is Completed
                backgroundPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)

                // Set enabled to true, So the user can click again
                // On the button when state is Completed
                isEnabled = true

                // Change buttonBackgroundColor and Re Draw
                setBackgroundColorFromRecourse(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )

                // Cancel ValueAnimator
                valueAnimator.cancel()
            }

            ButtonState.Clicked -> {
                // Set Button Text to Clicked
                setText(context.getString(R.string.button_clicked))

                // Changes Background Paint Color to colorAccent when the button state is clicked
                backgroundPaint.color = ContextCompat.getColor(context, R.color.colorAccent)

                // Change buttonBackgroundColor and Re Draw
                setBackgroundColorFromRecourse(ContextCompat.getColor(context, R.color.colorAccent))
            }
        }
        reDraw()
    }

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.LoadingButton
        ) {
            // Get LoadingButton_text String
            getString(R.styleable.LoadingButton_text)?.let { buttonText = it }
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
        }
    }

    // Background Paint
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    // In Progress Background Paint
    private val inProgressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }

    // In Progress Arc Paint
    private val inProgressArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    // Text Paint
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Set Style to FILL
        style = Paint.Style.FILL

        // Set Text Alignment to Center
        textAlign = Paint.Align.CENTER

        // Set Text Size to 60
        textSize = 60.0F

        // Set Font
        typeface = ResourcesCompat.getFont(context, R.font.univia_pro_regular)

        // Set Text Color to WHITE
        color = Color.WHITE
    }

    // Create instance of Rect, To Avoid object allocations during draw/layout operations
    private val textRect = Rect()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get Width and Height
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        
        // Set dial background color to green if selection not off.
        canvas.drawColor(buttonBackgroundColor)

        // Paint Text
        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)

        // Center X
        val centerX = width / 2

        // Get afterPaint textPaint Center Y
        val centerY = height / 2 - textRect.centerY()

        // Background Paint, Draw Round Rect
        canvas.drawRoundRect(
            0F,
            0F,
            width,
            height,
            0F,
            0F,
            backgroundPaint
        )

        // Show Loading Shape if buttonState == Loading
        if (buttonState == ButtonState.Loading) {

            // Draw Animated Rect Background
            canvas.drawRoundRect(
                0F,
                0F,
                progress * width,
                height,
                0F,
                0F,
                inProgressBackgroundPaint
            )

            // Arc Padding
            val padding = 40F

            // Arc Size
            val arcSize = 150F

            // Arc Space From Left
            val spaceFromLeft = width - textRect.width().toFloat() - padding / 2

            // Draw Arc on Top of Rect Background
            canvas.drawArc(
                spaceFromLeft, // LEFT
                padding, // TOP
                spaceFromLeft + arcSize, // RIGHT
                height - padding, // BOTTOM
                0F,
                progress * 360F,
                true,
                inProgressArcPaint
            )
        }

        // Draw Button Text on Top of all canvas
        canvas.drawText(buttonText, centerX, centerY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Min Width
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth

        // Width
        val width: Int = resolveSizeAndState(
            minWidth,
            widthMeasureSpec,
            1
        )

        // Height
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )

        // Set Measured Dimension
        setMeasuredDimension(width, height)
    }

    private fun setText(buttonText: String) {
        this.buttonText = buttonText
        // Call reDraw
        reDraw()
    }

    private fun setBackgroundColorFromRecourse(color: Int) {
        buttonBackgroundColor = color

        // Call reDraw
        reDraw()
    }

    private fun reDraw() {
        // Forcing a call to onDraw() to redraw the view.
        invalidate()
    }

    fun setState(pState: ButtonState) {
        this.buttonState = pState
    }

    private fun ValueAnimator.disableViewDuringAnimation() {
        // This extension method listens for start/end events on an animation and disables
        // The given view for the entirety of that animation.
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                // Set enabled to false, To avoid pressing the button while animating
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator) {
                // Set enabled to true, So the user can click again
                // On the button when state is Completed
                isEnabled = true
            }
        })
    }
}
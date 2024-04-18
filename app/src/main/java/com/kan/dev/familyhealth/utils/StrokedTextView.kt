package com.kan.dev.familyhealth.utils
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.kan.dev.familyhealth.R

class StrokedTextView : AppCompatTextView {

    constructor(ctx: Context) : super(ctx, null)
    constructor(ctx: Context, attr: AttributeSet?) : super(ctx, attr, 0) {
        getStyledAttributes(attr)
    }

    constructor(ctx: Context, attr: AttributeSet?, defStyleAttr: Int) : super(ctx, attr, defStyleAttr) {
        getStyledAttributes(attr)
    }

    private var calcWidth = 0

    var strokeWidth = 4f
    var strokeColor = Color.TRANSPARENT
    var startColor = Color.BLUE
    var endColor = Color.GREEN
    var fontWeight = FontWeight.NORMAL

    private lateinit var staticLayout: StaticLayout

    private val staticLayoutPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = this@StrokedTextView.textSize
            typeface = this@StrokedTextView.typeface
        }
    }

    private fun getStyledAttributes(attr: AttributeSet?) {
        context.obtainStyledAttributes(attr, R.styleable.StrokedTextView).apply {
            strokeWidth = getDimensionPixelSize(R.styleable.StrokedTextView_strokeThickness, 4).toFloat()
            strokeColor = getColor(R.styleable.StrokedTextView_strokeColor, Color.TRANSPARENT)
            startColor = getColor(R.styleable.StrokedTextView_gradientStartColor, Color.BLUE)
            endColor = getColor(R.styleable.StrokedTextView_gradientEndColor, Color.GREEN)
            fontWeight = FontWeight.fromValue(getInt(R.styleable.StrokedTextView_fontWeight, FontWeight.NORMAL.value))
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setPadding(paddingStart + strokeWidth.toInt() / 2, paddingTop, paddingRight + strokeWidth.toInt() / 2, paddingBottom)
        calcWidth = (MeasureSpec.getSize(widthMeasureSpec) - paddingStart)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (canvas == null) return
        reinitializeStaticLayout()
        with(canvas) {
            save()
            translate(paddingStart.toFloat(), 0f)

            // Draw stroke first
            staticLayoutPaint.configureForStroke()
            staticLayout.draw(this)

            // Draw gradient text
            drawGradientText(this)

            restore()
        }
    }

    private fun reinitializeStaticLayout() {
        staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, staticLayoutPaint, calcWidth)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        } else {
            StaticLayout(
                text,
                staticLayoutPaint,
                calcWidth,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                true
            )
        }
    }

    private fun drawGradientText(canvas: Canvas) {
        val lineCount = staticLayout.lineCount
        val lineHeight = staticLayout.height / lineCount.toFloat()

        for (i in 0 until lineCount) {
            val lineStart = staticLayout.getLineStart(i)
            val lineEnd = staticLayout.getLineEnd(i)

            val lineX = staticLayout.getLineLeft(i)
            val lineY = staticLayout.getLineBottom(i) - staticLayout.paint.descent()

            val lineText = text.substring(lineStart, lineEnd)

            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            textPaint.textSize = textSize
            textPaint.shader = LinearGradient(
                lineX,
                lineY - lineHeight, // Change the y positions
                lineX,
                lineY,
                startColor,
                endColor,
                Shader.TileMode.CLAMP
            )

            // Draw text with gravity
            val textWidth = textPaint.measureText(lineText)
            val x = when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.LEFT -> lineX
                Gravity.RIGHT -> lineX + (calcWidth - textWidth)
                Gravity.CENTER_HORIZONTAL -> lineX + ((calcWidth - textWidth) / 2)
                else -> lineX
            }

            val oldTextSize = textPaint.textSize
            val oldColor = textPaint.color
            val oldFakeBoldText = textPaint.isFakeBoldText
            val oldTextSkewX = textPaint.textSkewX

            val typeface = Typeface.create(typeface, fontWeight.value)
            textPaint.typeface = typeface
            textPaint.isFakeBoldText = fontWeight == FontWeight.BOLD
            textPaint.textSkewX = if (fontWeight == FontWeight.BOLD) -0.25f else 0f

            canvas.drawText(lineText, x, lineY, textPaint)

            // Restore textPaint properties
            textPaint.textSize = oldTextSize
            textPaint.color = oldColor
            textPaint.isFakeBoldText = oldFakeBoldText
            textPaint.textSkewX = oldTextSkewX
        }
    }

    private fun Paint.configureForStroke() {
        style = Paint.Style.STROKE
        color = strokeColor
        strokeWidth = this@StrokedTextView.strokeWidth
    }

    enum class FontWeight(val value: Int) {
        NORMAL(400),
        BOLD(700);

        companion object {
            fun fromValue(value: Int): FontWeight {
                return values().firstOrNull { it.value == value } ?: NORMAL
            }
        }
    }
}

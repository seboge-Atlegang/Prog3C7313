package com.example.uniprobudget.data

import com.example.uniprobudget.data.BudgetProgressChart  // since it's in data folder
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class BudgetProgressChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var spentPercentage = 0f
    private var minPercentage = 0f
    private var isOverBudget = false

    private val underMinColor = Color.parseColor("#4CAF50")
    private val onTrackColor = Color.parseColor("#2196F3")
    private val overBudgetColor = Color.parseColor("#F44336")
    private val minMarkerColor = Color.parseColor("#1976D2")

    fun updateData(spent: Double, minGoal: Double, maxGoal: Double) {
        spentPercentage = if (maxGoal > 0) (spent / maxGoal).toFloat().coerceIn(0f, 1f) else 0f
        minPercentage = if (maxGoal > 0) (minGoal / maxGoal).toFloat().coerceIn(0f, 1f) else 0f
        isOverBudget = spent > maxGoal && maxGoal > 0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radius = min(width, height) / 2 * 0.7f

        backgroundPaint.color = Color.parseColor("#E0E0E0")
        backgroundPaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        val spentAngle = spentPercentage * 360
        paint.color = when {
            isOverBudget -> overBudgetColor
            spentPercentage > minPercentage -> onTrackColor
            else -> underMinColor
        }
        paint.style = Paint.Style.FILL
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -90f, spentAngle, true, paint
        )

        if (minPercentage > 0) {
            val minAngle = minPercentage * 360
            paint.color = minMarkerColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                -90f, minAngle, false, paint
            )
        }

        val percentage = (spentPercentage * 100).toInt()
        val percentageText = "$percentage%"
        textPaint.color = Color.BLACK
        textPaint.textSize = radius * 0.35f
        textPaint.textAlign = Paint.Align.CENTER
        val textX = centerX
        val textY = centerY + textPaint.textSize / 3
        canvas.drawText(percentageText, textX, textY, textPaint)
    }
}
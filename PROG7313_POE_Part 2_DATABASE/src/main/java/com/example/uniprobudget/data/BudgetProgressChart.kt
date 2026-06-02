package com.example.uniprobudget.ui

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

    private var spentPercentage = 0f  // 0 to 1
    private var minPercentage = 0f    // 0 to 1
    private var maxPercentage = 1f    // 0 to 1
    private var isOverBudget = false

    // New color scheme: Blue theme for budget tracking
    private val underMinColor = Color.parseColor("#4CAF50")     // Green - Below minimum (good saving)
    private val onTrackColor = Color.parseColor("#2196F3")      // BLUE - Between min and max (on track)
    private val overBudgetColor = Color.parseColor("#F44336")   // Red - Over budget (warning)
    private val minMarkerColor = Color.parseColor("#1976D2")    // Dark Blue - Minimum goal marker

    fun updateData(spent: Double, minGoal: Double, maxGoal: Double) {
        spentPercentage = if (maxGoal > 0) (spent / maxGoal).toFloat().coerceIn(0f, 1f) else 0f
        minPercentage = if (maxGoal > 0) (minGoal / maxGoal).toFloat().coerceIn(0f, 1f) else 0f
        isOverBudget = spent > maxGoal && maxGoal > 0
        invalidate() // Redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radius = min(width, height) / 2 * 0.7f

        // Draw background circle (light gray)
        backgroundPaint.color = Color.parseColor("#E0E0E0")
        backgroundPaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Draw spent arc (colored based on budget)
        val spentAngle = spentPercentage * 360
        paint.color = when {
            isOverBudget -> overBudgetColor      // RED - Over budget
            spentPercentage > minPercentage -> onTrackColor   // BLUE - On track
            else -> underMinColor                // GREEN - Under minimum goal
        }
        paint.style = Paint.Style.FILL
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -90f, spentAngle, true, paint
        )

        // Draw min goal marker (dark blue line) - only if min goal exists
        if (minPercentage > 0) {
            val minAngle = minPercentage * 360
            paint.color = minMarkerColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                -90f, minAngle, false, paint
            )
        }

        // Draw text in center (black)
        val percentage = (spentPercentage * 100).toInt()
        val percentageText = "${percentage}%"
        textPaint.color = Color.BLACK
        textPaint.textSize = radius * 0.4f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        val textX = centerX
        val textY = centerY + textPaint.textSize / 3
        canvas.drawText(percentageText, textX, textY, textPaint)
    }
}
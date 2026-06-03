package com.example.uniprobudget.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CategorySpendingChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var categoryData: List<CategorySummary> = emptyList()
    private var minGoal = 0.0
    private var maxGoal = 0.0

    fun updateData(
        categories: List<CategorySummary>,
        minGoal: Double,
        maxGoal: Double
    ) {
        this.categoryData = categories
        this.minGoal = minGoal
        this.maxGoal = maxGoal

        android.util.Log.d(
            "GRAPH_DEBUG",
            "Categories found: ${categories.size}"
        )

        categories.forEach {
            android.util.Log.d(
                "GRAPH_DEBUG",
                "${it.categoryName}: ${it.totalAmount}"
            )
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (categoryData.isEmpty()) {
            textPaint.color = Color.RED
            textPaint.textSize = 40f
            canvas.drawText(
                "No Category Data",
                50f,
                100f,
                textPaint
            )
            return
        }

        val chartWidth = width.toFloat()
        val chartHeight = height - 120f

        // Scale using spending only
        val highest =
            (categoryData.maxOf { it.totalAmount } * 1.2)
                .coerceAtLeast(100.0)

        val barWidth =
            chartWidth / (categoryData.size * 2)

        textPaint.color = Color.BLACK
        textPaint.textSize = 24f

        categoryData.forEachIndexed { index, item ->

            val left =
                50f + (index * (barWidth * 2))

            val right =
                left + barWidth

            val barHeight =
                ((item.totalAmount / highest) * chartHeight).toFloat()

            val top =
                chartHeight - barHeight

            barPaint.color =
                Color.parseColor("#6200EE")

            canvas.drawRect(
                left,
                top,
                right,
                chartHeight,
                barPaint
            )

            // Amount label
            canvas.drawText(
                "R${item.totalAmount.toInt()}",
                left,
                top - 10,
                textPaint
            )

            // Category label
            canvas.drawText(
                item.categoryName.take(8),
                left,
                chartHeight + 35,
                textPaint
            )
        }

        // Minimum Goal Line
        if (minGoal > 0 && minGoal <= highest) {

            val minY =
                chartHeight -
                        ((minGoal / highest) * chartHeight).toFloat()

            linePaint.color = Color.GREEN
            linePaint.strokeWidth = 6f

            canvas.drawLine(
                0f,
                minY,
                chartWidth,
                minY,
                linePaint
            )

            canvas.drawText(
                "Min Goal",
                10f,
                minY - 10,
                textPaint
            )
        }

        // Maximum Goal Line
        if (maxGoal > 0 && maxGoal <= highest) {

            val maxY =
                chartHeight -
                        ((maxGoal / highest) * chartHeight).toFloat()

            linePaint.color = Color.RED
            linePaint.strokeWidth = 6f

            canvas.drawLine(
                0f,
                maxY,
                chartWidth,
                maxY,
                linePaint
            )

            canvas.drawText(
                "Max Goal",
                10f,
                maxY - 10,
                textPaint
            )
        }
    }
}
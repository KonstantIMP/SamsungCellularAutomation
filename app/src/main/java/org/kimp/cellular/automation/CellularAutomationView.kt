package org.kimp.cellular.automation

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import java.util.*
import kotlin.collections.HashSet

class CellularAutomationView: View {
    private final val emptyCell: Int = 0
    private final val lifeCell: Int = 1

    private var backgroundColor : Color = Color.valueOf(1.0f, 1.0f, 1.0f)
    private var cellColor : Color = Color.valueOf(0.0f, 0.0f, 0.0f)

    private var cellSize : Int = 50

    private lateinit var currentCells : HashSet<Pair<Int, Int>>

    constructor(@NonNull context: Context) : super(context) {}

    constructor(@NonNull context: Context, attrs : AttributeSet) : super(context, attrs) {
        applyAttributes(context, attrs)
    }

    constructor(@NonNull context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr) {
        applyAttributes(context, attrs)
    }

    private fun applyAttributes(@NonNull context : Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CellularAutomationView)

        backgroundColor = Color.valueOf(a.getColor(R.styleable.CellularAutomationView_backgroundColor, backgroundColor.toArgb()))
        cellColor = Color.valueOf(a.getColor(R.styleable.CellularAutomationView_cellColor, cellColor.toArgb()))

        cellSize = attrs.getAttributeIntValue(R.styleable.CellularAutomationView_cellSize, cellSize)

        a.recycle()
    }

    /**
     * Calculate next automation iteration
     */
    fun processAutomation() {
        currentCells = fullCheckCell()
        invalidate()
    }

    private fun fullCheckCell(): HashSet<Pair<Int, Int>> {
        var newCells: HashSet<Pair<Int, Int>> = HashSet()

        for (cell in currentCells) {
            for (dx in arrayOf(-1, 0, 1)) {
                for (dy in arrayOf(-1, 0, 1)) {
                    if (checkCell(Pair(cell.first + dx, cell.second + dy)) == lifeCell) {
                        newCells.add(Pair(cell.first + dx, cell.second + dy))
                    }
                }
            }
        }

        return newCells
    }

    private fun checkCell(cell: Pair<Int, Int>): Int {
        var neighbourCounter: Int = 0

        for (dx in arrayOf(-1, 0, 1)) {
            for (dy in arrayOf(-1, 0, 1)) {
                if (dx == 0 && dy == 0) continue
                if (currentCells.contains(Pair(cell.first + dx, cell.second + dy)))
                    neighbourCounter += 1
            }
        }

        if (neighbourCounter == 3 && !currentCells.contains(cell)) return lifeCell
        else if (neighbourCounter <= 3 && neighbourCounter >= 2 && currentCells.contains(cell)) return lifeCell
        else return emptyCell
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            var newPoint: Pair<Int, Int> = Pair(event.x.toInt() / cellSize, event.y.toInt() / cellSize)
            if (currentCells.contains(newPoint)) currentCells.remove(newPoint)
            else currentCells.add(newPoint)
            invalidate()
        }

        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        if (!this::currentCells.isInitialized) currentCells = HashSet()

        canvas?.drawColor(backgroundColor.toArgb())

        val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = cellColor.toArgb()

        for (cell in currentCells) {
            var currentRect: Rect = Rect(cell.first * cellSize, cell.second * cellSize, (cell.first + 1) * cellSize, (cell.second + 1) * cellSize)
            canvas?.drawRect(currentRect, paint)
        }
    }
}
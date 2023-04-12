package com.sortinghat.funny.view.rollingview.strategy

import android.graphics.Canvas
import android.graphics.Paint
import java.util.*

internal class MyTextManager(
        private val textPaint: Paint,
        private val charOrderManager: MyCharOrderManager) {

    companion object {
        const val EMPTY: Char = 0.toChar()
    }

    private val map: MutableMap<Char, Float> = LinkedHashMap(36)

    private val textColumns = mutableListOf<TextColumn>()

    private var charListColumns: List<List<Char>> = Collections.emptyList()

    init {
        updateFontMatrics()
    }

    fun charWidth(c: Char, textPaint: Paint): Float {
        return if (c == EMPTY) {
            0f
        } else {
            map[c] ?: textPaint.measureText(c.toString()).also { map[c] = it }
        }
    }

    fun updateFontMatrics() {
        map.clear()
        with(textPaint.fontMetrics) {
            textHeight = bottom - top
            textBaseline = -top
        }
        textColumns.forEach { it.measure() }
    }

    fun updateAnimation(progress: Float) {
        //当changeCharList.size大于7位数的时候 有可能使Float溢出 所以要用Double
        val initialize = MyPreviousProgress(0, 0.0, progress.toDouble())
        textColumns.foldRightIndexed(initialize) { index, column, previousProgress ->
            val nextProgress = charOrderManager.getProgress(previousProgress, index,
                    charListColumns, column.index)

            val previous = column.onAnimationUpdate(nextProgress.currentIndex,
                    nextProgress.offsetPercentage, nextProgress.progress)
            previous
        }
    }

    fun onAnimationEnd() {
        textColumns.forEach { it.onAnimationEnd() }
        charOrderManager.afterCharOrder()
    }

    fun draw(canvas: Canvas) {
        textColumns.forEach {
            it.draw(canvas)
            canvas.translate(it.currentWidth, 0f)
        }
    }

    val currentTextWidth: Float
        get() = textColumns.map { it.currentWidth }.fold(0f) { total, next -> total + next }

    fun setText(targetText: CharSequence) {

        val itr = textColumns.iterator()
        while (itr.hasNext()) {
            val column = itr.next()
            if (column.currentWidth.toInt() == 0) {
                itr.remove()
            }
        }

        val sourceText = String(currentText)

        val maxLen = Math.max(sourceText.length, targetText.length)

        charOrderManager.beforeCharOrder(sourceText, targetText)
        for (idx in 0 until maxLen) {
            val (list, direction) = charOrderManager.findCharOrder(sourceText, targetText, idx)
            if (idx >= maxLen - sourceText.length) {
                textColumns[idx].setChangeCharList(list, direction)
            } else {
                textColumns.add(idx, TextColumn(this, textPaint, list, direction))
            }
        }
        charListColumns = textColumns.map { it.changeCharList }
    }

    val currentText
        get(): CharArray = CharArray(textColumns.size) { index -> textColumns[index].currentChar }

    var textHeight: Float = 0f
        private set(value) {
            field = value
        }

    var textBaseline = 0f
        private set(value) {
            field = value
        }
}

data class MyPreviousProgress(
        val currentIndex: Int,
        val offsetPercentage: Double,
        val progress: Double,
        val currentChar: Char = MyTextManager.EMPTY,
        val currentWidth: Float = 0f
)

data class MyNextProgress(
        val currentIndex: Int,
        var offsetPercentage: Double,
        val progress: Double
)
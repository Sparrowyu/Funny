package com.sortinghat.funny.view.rollingview.strategy

import com.sortinghat.funny.view.rollingview.strategy.utils.MyCircularList
import com.sortinghat.funny.view.rollingview.strategy.utils.MyReplaceList


open class MyNonZeroFirstStrategy(private val strategy: MyCharOrderStrategy) : MyCharOrderStrategy by strategy {

    private var sourceZeroFirst = true
    private var targetZeroFirst = true

    override fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {
        strategy.beforeCompute(sourceText, targetText, charPool)
        sourceZeroFirst = true
        targetZeroFirst = true
    }

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool
    ): Pair<List<Char>, MyDirection> {

        val (list, direction) = strategy.findCharOrder(sourceText, targetText, index, charPool)

        val maxLen = Math.max(sourceText.length, targetText.length)
        val firstIdx = firstZeroAfterEmpty(list)
        val lastIdx = lastZeroBeforeEmpty(list)
        var replaceFirst = false
        var replaceLast = false

        if (sourceZeroFirst && firstIdx != -1 && index != maxLen - 1) {
            replaceFirst = true
        } else {
            sourceZeroFirst = false
        }

        if (targetZeroFirst && lastIdx != -1 && index != maxLen - 1) {
            replaceLast = true
        } else {
            targetZeroFirst = false
        }

        var replaceList = if (replaceFirst && replaceLast) {
            MyReplaceList(list, MyTextManager.EMPTY, MyTextManager.EMPTY, { firstIdx }, { lastIdx })
        } else if (replaceFirst) {
            MyReplaceList(list, first = MyTextManager.EMPTY, firstReplacePosition = { firstIdx },
                    lastReplacePosition = { lastIdx })
        } else if (replaceLast) {
            MyReplaceList(list, last = MyTextManager.EMPTY, firstReplacePosition = { firstIdx },
                    lastReplacePosition = { lastIdx })
        } else {
            list
        }

        replaceList = if (replaceFirst && replaceLast) {
            MyCircularList(replaceList, lastIdx - firstIdx + 1, firstIdx)
        } else if (replaceFirst) {
            MyCircularList(replaceList, replaceList.size - firstIdx, firstIdx)
        } else if (replaceLast) {
            MyCircularList(replaceList, lastIdx + 1)
        } else {
            replaceList
        }

        return replaceList to direction
    }

    private fun firstZeroAfterEmpty(list: List<Char>): Int {
        for ((idx, c) in list.withIndex()) {
            if (c == '0') {
                return idx
            }
            if (c != MyTextManager.EMPTY) {
                break
            }
        }
        return -1
    }

    private fun lastZeroBeforeEmpty(list: List<Char>): Int {
        val iter = list.listIterator(list.size)
        var idx = list.size
        while (iter.hasPrevious()) {
            val c = iter.previous()
            idx--
            if (c == '0') {
                return idx
            }
            if (c != MyTextManager.EMPTY) {
                break
            }
        }
        return -1
    }
}
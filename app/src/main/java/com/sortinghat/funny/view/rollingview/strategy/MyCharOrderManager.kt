package com.sortinghat.funny.view.rollingview.strategy

import java.util.*

internal class MyCharOrderManager {

    var charStrategy: MyCharOrderStrategy = MyStrategy.NormalAnimation()

    private val charOrderList = mutableListOf<LinkedHashSet<Char>>()

    fun addCharOrder(orderList: Iterable<Char>) {
        val list = mutableListOf(MyTextManager.EMPTY)
        list.addAll(orderList)
        val set = LinkedHashSet<Char>(list)
        charOrderList.add(set)
    }

    fun findCharOrder(sourceText: CharSequence, targetText: CharSequence, index: Int)
            : Pair<List<Char>, MyDirection> {
        return charStrategy.findCharOrder(sourceText, targetText, index, charOrderList)
    }

    fun beforeCharOrder(sourceText: CharSequence, targetText: CharSequence) =
            charStrategy.beforeCompute(sourceText, targetText, charOrderList)

    fun afterCharOrder() = charStrategy.afterCompute()

    fun getProgress(previousProgress: MyPreviousProgress, index: Int, columns: List<List<Char>>, charIndex: Int) =
            charStrategy.nextProgress(previousProgress, index, columns, charIndex)
}
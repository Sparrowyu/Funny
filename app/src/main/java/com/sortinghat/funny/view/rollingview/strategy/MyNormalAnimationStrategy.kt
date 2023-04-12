package com.sortinghat.funny.view.rollingview.strategy


open class MyNormalAnimationStrategy : MySimpleCharOrderStrategy(), MyCharOrderStrategy {

    override fun findCharOrder(
            sourceChar: Char,
            targetChar: Char,
            index: Int,
            order: Iterable<Char>?): Pair<List<Char>, MyDirection> {

        return if (sourceChar == targetChar) {
            listOf(targetChar) to MyDirection.SCROLL_DOWN

        } else if (order == null) {
            listOf(sourceChar, targetChar) to MyDirection.SCROLL_DOWN

        } else {
            val srcIndex = order.indexOf(sourceChar)
            val tgtIndex = order.indexOf(targetChar)

            if (srcIndex < tgtIndex) {
                order.subList(srcIndex, tgtIndex) to MyDirection.SCROLL_DOWN
            } else {
                order.subList(tgtIndex, srcIndex).asReversed() to MyDirection.SCROLL_UP
            }
        }
    }

    private fun <T> Iterable<T>.subList(start: Int, end: Int): List<T> {
        return this.filterIndexed { index, _ -> index in start..end }
    }
}
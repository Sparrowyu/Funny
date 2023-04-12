package com.lzx.lock.myapplication.view.strategy

import com.sortinghat.funny.view.rollingview.strategy.*


class MySameDirectionStrategy(
        private val direction: MyDirection,
        private val otherStrategy: MyCharOrderStrategy = MyStrategy.NormalAnimation()
) : MySimpleCharOrderStrategy() {

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool): Pair<List<Char>, MyDirection> {

        return otherStrategy.findCharOrder(sourceText, targetText, index, charPool).first to direction
    }
}
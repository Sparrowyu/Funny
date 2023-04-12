package com.sortinghat.funny.view.rollingview.strategy


class StickyStrategy(val factor: Double) : MyNormalAnimationStrategy() {

    init {
        if (factor <= 0.0 && factor > 1.0) {
            throw IllegalStateException("factor must be in range (0,1] but now is $factor")
        }
    }

    override fun getFactor(previousProgress: MyPreviousProgress, index: Int, size: Int, charList: List<Char>): Double {
        return factor
    }
}
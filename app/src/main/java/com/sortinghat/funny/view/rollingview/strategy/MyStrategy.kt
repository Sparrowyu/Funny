@file:Suppress("FunctionName")

package com.sortinghat.funny.view.rollingview.strategy

import com.lzx.lock.myapplication.view.strategy.MySameDirectionStrategy


object MyStrategy {// JvmStatic  listOf

    /**
     * 不显示动画效果
     *
     * the source text will be transformed directly into the target text without animation
     */
    @JvmStatic
    fun NoAnimation(): MyCharOrderStrategy = object : MySimpleCharOrderStrategy() {
        override fun findCharOrder(sourceChar: Char, targetChar: Char, index: Int, order: Iterable<Char>?) =
                listOf(targetChar) to MyDirection.SCROLL_DOWN
    }

    /**
     * 默认的动画效果：
     * 当调用[RollingTextView.addCharOrder]之后，在*charOder*里面的顺序存在这样的关系： **【目标字符在原字符的右边】** ，
     * 则会有向下滚动的动画效果。如果 **【目标字符在原字符的左边】** ，则会有向上滚动的动画效果。如果目标字符和原字符不在同一个
     * *charOrder* 中，则不会有动画效果
     *
     * it's default animation. a character will roll down to another character on the right and vice versa.
     */
    @JvmStatic
    fun NormalAnimation(): MyCharOrderStrategy = MyNormalAnimationStrategy()

    /**
     * 指定方向滚动的动画：
     * 与默认动画效果相似，但一定会沿指定方向滚动。见[Direction]
     *
     * the original character rolls in the specified direction no matter, regardless of the target character
     * on the left or the right.
     */
    @JvmStatic
    fun SameDirectionAnimation(direction: MyDirection): MyCharOrderStrategy = MySameDirectionStrategy(direction)

    /**
     * 进位动画：(已去除上下滑动 动画效果 需要效果待去除)
     * 高位数字会在低位数字滚动到达上限的时候再滚动。比如十进制数低位滚动9的时候，下一次滚动高位数将进1
     *
     * the animation starts with the rightmost digits and works to the left.
     */
    @JvmStatic
    fun CarryBitAnimation(): MyCharOrderStrategy = MyNonZeroFirstStrategy(MyCarryBitStrategy())

    /**
     * 装饰者模式，使动画最高位数字不为0
     *
     * Decoration Pattern: to limit the number to start with 0
     */
    @JvmStatic
    fun NonZeroFirstAnimation(orderStrategy: MyCharOrderStrategy): MyCharOrderStrategy =
            MyNonZeroFirstStrategy(orderStrategy)


    /**
     * 粘稠动画：
     * [factor]值是一个满足 (0,1] 的数字，数字越大，滚动越平滑，数字越小，滚动越跳跃
     *
     * parameter [factor] is a number in (0,1], which determines the fluency of animation.
     */
    @JvmStatic
    fun StickyAnimation(factor: Double): MyCharOrderStrategy =
            StickyStrategy(factor)
}
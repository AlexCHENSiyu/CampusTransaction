package com.example.campustransaction.ui.message

import android.graphics.Path
import pw.xiaohaozi.bubbleview.BubbleView.DrawIndicator


internal class MyDrawIndicator : DrawIndicator {
    override fun drawLeft(path: Path, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        val h = bottom - top
        path.moveTo(right.toFloat(), (top + h / 2).toFloat())
        path.arcTo(left.toFloat(), (top - h * 3 / 2).toFloat(),
            (right + h).toFloat(), (bottom - h * 2 / 3).toFloat(), 90F, 72F, false)
        path.arcTo(left.toFloat(),
            (top - h).toFloat(), (right + h).toFloat(), bottom.toFloat(), 180F, -90F, false)
        path.lineTo(right.toFloat(), bottom.toFloat())
    }

    override fun drawTop(path: Path?, left: Int, top: Int, right: Int, bottom: Int) {}
    override fun drawRight(path: Path, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        val h = bottom - top
        path.moveTo(left.toFloat(), (top + h / 2).toFloat())
        path.arcTo(
            (left - h).toFloat(), (top - h * 3 / 2).toFloat(),
            right.toFloat(), (bottom - h * 2 / 3).toFloat(), 90F, -72F, false)
        path.arcTo((left - h).toFloat(),
            (top - h).toFloat(), right.toFloat(), bottom.toFloat(), 0F, 90F, false)
        path.lineTo(left.toFloat(), bottom.toFloat())
    }

    override fun drawBottom(path: Path?, left: Int, top: Int, right: Int, bottom: Int) {}
}
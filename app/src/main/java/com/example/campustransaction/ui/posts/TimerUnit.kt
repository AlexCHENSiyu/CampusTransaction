package com.example.campustransaction.ui.posts

import android.os.Handler
import android.view.View
import android.widget.ImageView

// 3s倒计时
class TimerUnit(private val imageView: ImageView) : Handler() {
    private var defaultTime = 3
    private var time = defaultTime

    private var timeEndListener: OnTimeEndListener? = null

    private var runnable: Runnable = object : Runnable {
        override fun run() {
            time--
            if (time == 0) {
                endTime()
                return
            }
            postDelayed(this, 1000)
        }
    }

    fun setTimeEndListener(timeEndListener: OnTimeEndListener) {
        this.timeEndListener = timeEndListener
    }


    fun setTime(time: Int) {
        this.defaultTime = time
        this.time = defaultTime
    }

    fun startTime() {
        post(runnable)
        imageView.isClickable = true
        imageView.visibility = View.VISIBLE
    }

    fun pauseTime() {
        removeCallbacks(runnable)
        time = defaultTime
    }

    fun endTime() {
        imageView.isClickable = false
        imageView.visibility = View.INVISIBLE
        removeCallbacks(runnable)
        time = defaultTime
        if (timeEndListener != null) {
            timeEndListener!!.timeEnd()
        }
    }

    interface OnTimeEndListener {
        fun timeEnd()
    }

}
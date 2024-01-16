package com.example.campustransaction.create_account

import android.os.Handler
import android.widget.TextView

// 120s倒计时
class TimerUnit(private val textView: TextView) : Handler() {
    private var defaultTime = 120
    private var time = defaultTime
    private var isShowEndText = true

    private var timeEndListener: OnTimeEndListener? = null

    private var runnable: Runnable = object : Runnable {
        override fun run() {
            time--
            if (time == 0) {
                endTime()
                return
            }
            textView.text = String.format("%ds", time)
            postDelayed(this, 1000)
        }
    }

    fun setTimeEndListener(timeEndListener: OnTimeEndListener) {
        this.timeEndListener = timeEndListener
    }

    fun setShowEndText(showEndText: Boolean) {
        isShowEndText = showEndText
    }


    fun setTime(time: Int) {
        this.defaultTime = time
        this.time = defaultTime
    }

    fun startTime() {
        post(runnable)
        textView.isEnabled = false
    }

    fun pauseTime() {
        removeCallbacks(runnable)
        time = defaultTime
    }

    fun endTime() {
        if (isShowEndText) {
            textView.text = "Resend"
        }
        textView.isEnabled = true
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
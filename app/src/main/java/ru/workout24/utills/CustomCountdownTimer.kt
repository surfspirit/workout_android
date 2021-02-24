package io.playreplay.playreplayandroid.utils

import android.os.CountDownTimer
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class CustomCountdownTimer {

    private var isStarted = false
    private var seconds = 0L
    private var listener: CustomCountdownTimerListener? = null
    private var timer:CountDownTimer? = null

    interface CustomCountdownTimerListener{
        fun onStartTimer()
        fun onStopTimer()
        fun onTickTime(time: String, seconds: Long)
        fun onFinish()
    }

    fun start(s:Long) {
        seconds = s
        timer = object : CountDownTimer(s*1000,1000) {
            override fun onTick(millisLeft:Long) {
                seconds--
                listener?.onTickTime(convertTimeToString(seconds), seconds)
            }
            override fun onFinish() {
                listener?.onFinish()
            }
        }
        timer?.start()
        isStarted = true
        listener?.onStartTimer()
    }

    fun start() {
        start(seconds)
    }

    fun convertTimeToString(seconds: Long): String {
        return String.format(
                "%02d:%02d",
                TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
                TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        )
    }

    fun stop() {
        timer?.cancel()
        listener?.onStopTimer()
    }


    fun setListener(listener: CustomCountdownTimerListener) {
        this.listener = listener
    }

    fun removeListener() {
        listener = null
    }

    fun setTimeSeconds(seconds: Long){
        this.seconds = seconds
        if (!isStarted) start()
    }

    fun setTimeTimestamp(timestamp: Long){
        seconds = System.currentTimeMillis() / 1000 - timestamp
        if (!isStarted) start()
    }

    fun getCurrentTime(): Long = seconds
}
package ru.workout24.features

import java.util.*
import java.util.concurrent.TimeUnit

class CustomTimer {

    private var isStarted = false
    private var seconds = 0L
    private var listener: CustomTimerListener? = null
    private var timer: Timer? = null

    interface CustomTimerListener {
        fun onStartTimer()
        fun onStopTimer()
        fun onTickTime(time: String, seconds: Long)
    }

    fun start() {
        timer?.cancel()
        timer?.purge()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                var s = seconds++
                listener?.onTickTime(convertTimeToString(s), s)
                //  Log.i("EXERCISE TIMER == ", s.toString())
            }
        }, 0, 1000)
        isStarted = true
        listener?.onStartTimer()
    }

    fun restart() {
        seconds = 0
        start()
    }

    fun clear() {
        seconds = 0
        listener?.onTickTime(convertTimeToString(seconds), seconds)
        listener?.onStopTimer()
    }

    fun convertTimeToString(seconds: Long): String {
        /*
        return String.format(
                "%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(seconds),
                TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
                TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        )
        */
        return String.format(
            "%02d:%02d",
            TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
            TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        )
    }

    fun stop() {
        timer?.cancel()
        timer?.purge()
        listener?.onStopTimer()
    }


    fun setListener(listener: CustomTimerListener) {
        this.listener = listener
    }

    fun removeListener() {
        listener = null
    }

    fun setTimeSeconds(seconds: Long) {
        this.seconds = seconds
        if (!isStarted) start()
    }

    fun setTimeTimestamp(timestamp: Long) {
        seconds = System.currentTimeMillis() / 1000 - timestamp
        if (!isStarted) start()
    }

    fun getCurrentTime(): Long = seconds - 1
}
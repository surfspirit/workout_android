package ru.workout24.ui.exercises_execution

import android.content.Context
import android.os.Handler
import org.jetbrains.anko.runOnUiThread
import java.util.*
import java.util.concurrent.TimeUnit

class ProcessTimer(
    private val context: Context? = null,
    private var listener: TimerListener? = null
) {
    var state: TIMER_STATE = TIMER_STATE.INIT
        private set
    val isStarted get() = state == TIMER_STATE.START
    private var _seconds = 0L
    var seconds
        get() = _seconds
        set(value) {
            if (!isStarted){
                _seconds = value
            }
        }
    private var timer = Timer()

    interface TimerListener {
        fun onTimerStart()
        fun onTimerStop()
        fun onTickTime(time: String, seconds: Long)
    }

    fun start() {
        if (!isStarted) {
            timer.apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        _seconds++
                        context?.let {
                            context.runOnUiThread {
                                listener?.onTickTime(convertTimeToString(_seconds), _seconds)
                            }
                        } ?: listener?.onTickTime(convertTimeToString(_seconds), _seconds)
                    }
                }, 0, 1000)
            }
            state = TIMER_STATE.START
            listener?.onTimerStart()
        }
    }

    fun startReverse() {
        if (!isStarted) {
            timer.apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        _seconds--
                        context?.let {
                            context.runOnUiThread {
                                listener?.onTickTime(convertTimeToString(_seconds), _seconds)
                            }
                        } ?: listener?.onTickTime(convertTimeToString(_seconds), _seconds)
                    }
                }, 0, 1000)
            }
            state = TIMER_STATE.START
            listener?.onTimerStart()
        }
    }

    fun startAfterMillis(millis: Long) {
        Handler().postDelayed({
            start()
        }, millis)
    }

    fun stop() {
        if (state != TIMER_STATE.STOP) {
            timer.apply {
                cancel()
                purge()
            }
            timer = Timer()
            state = TIMER_STATE.STOP
        }
        listener?.onTimerStop()
    }

    fun restart() {
        _seconds = 0
        start()
        state = TIMER_STATE.RESTART
    }

    fun clear() {
        _seconds = 0
        state = TIMER_STATE.CLEAR
    }

    fun convertTimeToString(seconds: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.SECONDS.toHours(
                    seconds
                )
            ),
            TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.SECONDS.toMinutes(
                    seconds
                )
            )
        )
    }

    fun setListener(listener: TimerListener) {
        this.listener = listener
    }

    fun removeListener() {
        listener = null
    }

    fun setTimeSeconds(seconds: Long) {
        this._seconds = seconds
        if (!isStarted) start()
    }

    fun setTimeTimestamp(timestamp: Long) {
        _seconds = System.currentTimeMillis() / 1000 - timestamp
        if (!isStarted) start()
    }

    fun getCurrentTime(): Long = _seconds - 1

    companion object {
        enum class TIMER_STATE {
            INIT, START, STOP, RESTART, CLEAR
        }
    }
}
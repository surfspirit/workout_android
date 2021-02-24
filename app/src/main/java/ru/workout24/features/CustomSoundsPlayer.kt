package ru.workout24.features

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler
import androidx.annotation.RawRes
import ru.workout24.R

class CustomSoundsPlayer(private val context: Context) {
    private val mp by lazy {
        MediaPlayer()
    }
    var currentSound: SOUNDS? = null
        private set
    var isLooping = true
    var isPlaying = false
        private set

    fun playSound(sound: SOUNDS) {
        currentSound = sound
        try {
            val afd: AssetFileDescriptor = context.resources.openRawResourceFd(sound.value)
            mp.run {
                reset()
                isLooping = this@CustomSoundsPlayer.isLooping
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnPreparedListener { start() }
                prepareAsync()
            }
        } catch (e: Exception) {

        }
    }

    fun playSoundUntilMillis(sound: SOUNDS, millis: Long, readyCallback: () -> Unit = {}) {
        playSound(sound)
        Handler().postDelayed({
            mp.stop()
            readyCallback()
        }, millis)
    }

    fun pauseSound() {
        mp.pause()
    }

    fun stopSound() {
        mp.stop()
    }

    companion object {
        enum class SOUNDS(@RawRes val value: Int) {
            THREE_SECONDS_START(R.raw.start_timer_tick),
            EXERCISE_START(R.raw.exercise_start_sound),
            EXERCISES_PROCESS(R.raw.metronome),
            RECREATION_END(R.raw.exercise_ending_timer_tick)
        }
    }
}
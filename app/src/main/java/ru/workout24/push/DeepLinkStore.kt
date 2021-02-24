package ru.workout24.push

import android.content.Intent
import android.os.Bundle
import ru.workout24.R
import java.util.*

object DeepLinkStore {
    val hasData get() = !storeQueue.isEmpty()

    private val storeQueue = ArrayDeque<DeepLinkObject>(0)

    private fun putObjects(vararg objs: DeepLinkObject) {
        objs.forEach {
            storeQueue.push(it)
        }
    }

    fun getObject(): DeepLinkObject = storeQueue.pop()

    fun createPushNavigation(intent: Intent?) {
        (intent?.extras?.getSerializable(FirebasePushMessagingService.NOTIF_PENDING_INTENT_ACTION)
                as? FirebasePushMessagingService.PUSH_ACTION)?.let { action ->

            when (action) {
                FirebasePushMessagingService.PUSH_ACTION.OPEN_PLAN_TRAINING -> {
                    // в обратном порядке
                    putObjects(
                        DeepLinkObject(R.id.workout_diary),
                        DeepLinkObject(R.id.onceTrainingListFragment)
                    )
                }
                FirebasePushMessagingService.PUSH_ACTION.OPEN_TRAININGS -> {
                    putObjects(DeepLinkObject(R.id.onceTrainingListFragment))
                }
                FirebasePushMessagingService.PUSH_ACTION.OPEN_PROGRAM_TRAININGS -> {
                    putObjects(DeepLinkObject(R.id.trainingProgramsFragment))
                }
                FirebasePushMessagingService.PUSH_ACTION.OPEN_WORKOUT_CALENDAR -> {
                    // в обратном порядке
                    putObjects(
                        DeepLinkObject(R.id.workout_diary),
                        DeepLinkObject(R.id.lk_layer)
                    )
                }
            }
        }
    }
}

data class DeepLinkObject(val value: Int, val bundle: Bundle? = null)
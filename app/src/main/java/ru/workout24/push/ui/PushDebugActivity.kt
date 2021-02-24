package ru.workout24.push.ui

import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_push_debug.*
import ru.workout24.R
import ru.workout24.push.FirebasePushMessagingService
import ru.workout24.push.data.PushBodyDto
import ru.workout24.push.ui.adapter.OnPushRepeatListener
import ru.workout24.push.ui.adapter.PushListAdapter
import ru.workout24.push.ui.adapter.PushViewModel
import ru.workout24.utills.base.BaseActivity

class PushDebugActivity : BaseActivity(), OnPushRepeatListener {

    private val adapter by lazy {
        PushListAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_debug)

        rv_pushlist.adapter = adapter
        if (pref.cachedPushes.isEmpty()) {
            adapter.setData(
                arrayListOf(
                    PushViewModel(
                        PushBodyDto(
                            messageTitle = "Фейовый пуш",
                            messageBody = "Завтра тренировка «Имя тренировки»",
                            dataMessage = mapOf(),
                            pendingIntent = FirebasePushMessagingService.createActivityPendingIntent(this, FirebasePushMessagingService.PUSH_ACTION.OPEN_PLAN_TRAINING),
                            lowPriority = false
                        )
                    ),
                    PushViewModel(
                        PushBodyDto(
                            messageTitle = "Фейовый пуш",
                            messageBody = "Сегодня у вас тренировка «Имя тренировки»",
                            dataMessage = mapOf(),
                            pendingIntent = FirebasePushMessagingService.createActivityPendingIntent(this, FirebasePushMessagingService.PUSH_ACTION.OPEN_PLAN_TRAINING),
                            lowPriority = false
                        )
                    ),
                    PushViewModel(
                        PushBodyDto(
                            messageTitle = "Фейовый пуш",
                            messageBody = "Тренировка «Имя тренировки» не выполнена!",
                            dataMessage = mapOf(),
                            pendingIntent = FirebasePushMessagingService.createActivityPendingIntent(this, FirebasePushMessagingService.PUSH_ACTION.OPEN_WORKOUT_CALENDAR),
                            lowPriority = false
                        )
                    ),
                    PushViewModel(
                        PushBodyDto(
                            messageTitle = "Фейовый пуш",
                            messageBody = "Для вашего уровня появилась программа тренировок «Имя программы»",
                            dataMessage = mapOf(),
                            pendingIntent = FirebasePushMessagingService.createActivityPendingIntent(this, FirebasePushMessagingService.PUSH_ACTION.OPEN_PROGRAM_TRAININGS),
                            lowPriority = false
                        )
                    ),
                    PushViewModel(
                        PushBodyDto(
                            messageTitle = "Фейовый пуш",
                            messageBody = "Для вашего уровня появилась тренировка «Имя тренировки»",
                            dataMessage = mapOf(),
                            pendingIntent = FirebasePushMessagingService.createActivityPendingIntent(this, FirebasePushMessagingService.PUSH_ACTION.OPEN_TRAININGS),
                            lowPriority = false
                        )
                    )
                )
            )
        } else {
            adapter.setData(ArrayList(pref.cachedPushes.map {
                PushViewModel(
                    Gson().fromJson(
                        it,
                        PushBodyDto::class.java
                    )
                )
            }))
        }

        btnBack.setOnClickListener { finish() }
    }

    override fun onPushRepeat(push: PushBodyDto) {
        FirebasePushMessagingService.createPushNotification(this, push)
    }
}

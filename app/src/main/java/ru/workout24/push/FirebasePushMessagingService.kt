package ru.workout24.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import ru.workout24.network.Api
import ru.workout24.utills.Preferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import ru.workout24.R
import ru.workout24.push.data.PushBodyDto
import ru.workout24.ui.RootActivity
import javax.inject.Inject

class FirebasePushMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var preferences: Preferences

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    /**
    'data_message': {
      'type': [TRAINING_TODAY, TRAINING_TOMORROW, TRAINING_SKIPPED, NEW_TRAINING, NEW_TRAINING_SET]
      'id': 'somerandomuuid'
    }
     */

    override fun onMessageReceived(pushMessage: RemoteMessage) {
        super.onMessageReceived(pushMessage)
        val mappedPush = parsePushMessage(pushMessage)
        createPushNotification(applicationContext, mappedPush)
//        preferences.cachePush(mappedPush)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (preferences.pushToken.isNullOrEmpty()) {
            preferences.pushToken = token
        }
    }

    private fun parsePushMessage(pushMessage: RemoteMessage): PushBodyDto {
        return PushBodyDto(
            messageTitle = pushMessage.notification?.title ?: "",
            messageBody = pushMessage.notification?.body ?: "",
            pendingIntent = createServicePendingIntent(this, PUSH_ACTION.OPEN_WORKOUT_CALENDAR),
            lowPriority = false
        )
    }

    enum class PUSH_TYPE {
        TOMORROW_TRAINING,
        TODAY_TRAINING,
        TRAINING_WILL_BE_SKIPPED,
        APPEAR_NEW_TRAINING,
        APPEAR_NEW_PROGRAM
    }

    companion object {
        const val NOTIFICATION_ID = 243
        const val NOTIF_PENDING_INTENT = 144
        const val NOTIF_PENDING_INTENT_ACTION = "NOTIF_PENDING_INTENT_ACTION"

        fun createPushNotification(context: Context, parsedPush: PushBodyDto) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val id = context.resources.getString(R.string.default_notification_channel_id)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(id, id, importance)
                val notificationManager: NotificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(
                context,
                context.resources.getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_stat_name)
                .setColor(ContextCompat.getColor(context, R.color.black))
                .setContentTitle(parsedPush.messageTitle)
                .setContentText(parsedPush.messageBody)
                .setDefaults(Notification.DEFAULT_SOUND and Notification.DEFAULT_LIGHTS and Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setPriority(if (parsedPush.lowPriority) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(parsedPush.pendingIntent)

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        }

        fun createActivityPendingIntent(context: Context, action: PUSH_ACTION): PendingIntent {
            return PendingIntent.getActivity(
                context,
                NOTIF_PENDING_INTENT,
                Intent(context, RootActivity::class.java).apply {
                    putExtra(
                        NOTIF_PENDING_INTENT_ACTION,
                        action
                    )
                },
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        fun createServicePendingIntent(context: Context, action: PUSH_ACTION): PendingIntent {
            return PendingIntent.getService(
                context,
                NOTIF_PENDING_INTENT,
                Intent(context, RootActivity::class.java).apply {
                    putExtra(
                        NOTIF_PENDING_INTENT_ACTION,
                        action
                    )
                },
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

    enum class PUSH_ACTION {
        OPEN_PLAN_TRAINING, OPEN_WORKOUT_CALENDAR, OPEN_TRAININGS, OPEN_PROGRAM_TRAININGS
    }
}
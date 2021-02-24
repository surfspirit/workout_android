package ru.workout24.dagger.modules

import ru.workout24.push.FirebasePushMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PushModule {
    @ContributesAndroidInjector
    abstract fun providePushService(): FirebasePushMessagingService
}
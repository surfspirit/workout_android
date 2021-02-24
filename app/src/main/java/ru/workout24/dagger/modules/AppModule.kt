package ru.workout24.dagger.modules

import android.app.Application
import android.content.Context
import ru.workout24.utills.Preferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun providePreferences(application: Application): Preferences {
        return Preferences(application)
    }
}
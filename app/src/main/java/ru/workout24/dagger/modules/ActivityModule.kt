package ru.workout24.dagger.modules

import ru.workout24.dagger.ActivityScope
import ru.workout24.ui.RootActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.workout24.push.ui.PushDebugActivity

@Module
abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(FragmentModule::class))
    abstract fun contributeRootActivity(): RootActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributePushDebugActivity(): PushDebugActivity
}
package ru.workout24

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import ru.workout24.dagger.DaggerAppComponent
import ru.workout24.dagger.modules.RoomModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import ru.workout24.features.AppLifecycleObserver
import javax.inject.Inject


class CustomApplication : Application(), HasActivityInjector, HasSupportFragmentInjector,
    HasServiceInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var mFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = mFragmentInjector
    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
    override fun serviceInjector(): AndroidInjector<Service> = serviceInjector

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver)

        DaggerAppComponent
            .builder()
            .application(this)
            .roomModule(RoomModule(this))
            .build()
            .inject(this)
    }
}
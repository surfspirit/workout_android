package ru.workout24.dagger

import android.app.Application
import ru.workout24.CustomApplication
import ru.workout24.dagger.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = arrayOf(
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        ActivityModule::class,
        ViewModelModule::class,
        RoomModule::class,
        PushModule::class,
        FeatureModule::class
    )
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun roomModule(module: RoomModule): Builder
        fun build(): AppComponent
    }

    fun inject(customApp: CustomApplication)
}
package ru.workout24.dagger.modules

import android.content.Context
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import dagger.Module
import dagger.Provides
import ru.workout24.features.GoogleBilling
import ru.workout24.network.Api
import ru.workout24.features.SocialLoginFeature
import ru.workout24.utills.Preferences
import javax.inject.Singleton

@Module
class FeatureModule {
    @Provides
    internal fun provideGoogleBilling(applicationContext: Context): GoogleBilling {
        return GoogleBilling(applicationContext)
    }

    @Provides
    @Singleton
    internal fun initSocialLoginFeature(
        applicationContext: Context,
        api: Api,
        preference: Preferences
    ): SocialLoginFeature {
        VK.addTokenExpiredHandler(object : VKTokenExpiredHandler {
            override fun onTokenExpired() {
                //TODO:сделать сброс приложения
            }
        })
        VK.initialize(applicationContext)
        return SocialLoginFeature(applicationContext, api, preference)
    }
}
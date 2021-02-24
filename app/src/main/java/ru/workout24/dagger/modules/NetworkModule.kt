package ru.workout24.dagger.modules

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import ru.workout24.BuildConfig
import ru.workout24.network.Api
import ru.workout24.network.ApiAuthenticator
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import ru.workout24.utills.AUTH_TOKEN
import ru.workout24.utills.Preferences
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.gson.GsonBuilder
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


@Module
class NetworkModule : AnkoLogger {


    val errors = mapOf(
        "" to ""
    )

    @Provides
    @Singleton
    internal fun getErrors(): Map<String, String> {
        return errors
    }


    @Provides
    @Singleton
    internal fun getGlide(context: Context): RequestManager {
        return Glide.with(context).applyDefaultRequestOptions(
            RequestOptions().placeholder(ColorDrawable(Color.DKGRAY)).error(
                ColorDrawable(Color.DKGRAY)
            )
        ).apply { RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL) }
    }


    @Provides
    @Singleton
    internal fun getRetrofit(client: OkHttpClient): Retrofit {

        val gson = GsonBuilder()

            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    internal fun getApi(retorfit: Retrofit, authenticator: ApiAuthenticator, preference: Preferences): Api {
        val api = retorfit.create<Api>(Api::class.java)
        authenticator.setApi(api, preference)
        return api
    }

    @Provides
    @Singleton
    internal fun getApiProvider(api: Api, appDatabase : AppDatabase): ResourceProvider {
        return ResourceProvider(api, appDatabase)
    }


    @Provides
    @Singleton
    internal fun okHttpClient(preferences: Preferences?, authenticator: ApiAuthenticator): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logging)
        }

        clientBuilder.addInterceptor { chain ->
            var request = chain.request()
            val requestBuilder = request.newBuilder()

            preferences?.authToken?.let {
                if (it.isNotEmpty()) {
                    info { "CURRENT Bearer $it" }
                    requestBuilder.addHeader(AUTH_TOKEN, "Bearer $it")
                }
            }

            request = requestBuilder.build()
            var response: Response? = null

            try {
                response = chain.proceed(request)
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            response ?: getFakeResponse(request)
        }

        clientBuilder.authenticator(authenticator)

        return clientBuilder.connectTimeout(200, TimeUnit.SECONDS).readTimeout(200,TimeUnit.SECONDS).build()
    }

    private fun getFakeResponse(request: Request): Response = Response.Builder()
        .body(
            ResponseBody.create(
                MediaType.parse("application/json; charset=UTF-8"),
                "{\"status\":\"error\",\"code\":\"1488\",\"message\":\"Произошла ошибка запроса. Проверьте подключение к интернету\"}"
            )
        )
        .request(request).protocol(Protocol.HTTP_1_1)
        .code(1488)
        .message("Network Error")
        .build()

    @Provides
    @Singleton
    internal fun authenticator(): ApiAuthenticator {
        return ApiAuthenticator()
    }
}
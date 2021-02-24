package ru.workout24.network

import android.util.Log
import ru.workout24.utills.AUTH_TOKEN
import ru.workout24.utills.Preferences
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class ApiAuthenticator : Authenticator {

    private var mApi: Api? = null
    private var mPreferences: Preferences? = null

    fun setApi(api: Api, preferences: Preferences) {
        mApi = api
        mPreferences = preferences
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.isSuccessful) {
            val authToken = response.header(AUTH_TOKEN)
            return if (mPreferences != null) {
                mPreferences?.authToken = authToken!!
                response.request().newBuilder()
                    .header(AUTH_TOKEN, authToken)
                    .build()
            } else {
                response.request().newBuilder()
                    .header("appUserId", authToken!!)
                    .build()
            }
        } else if (response.code() == 401) {
            Log.d("auth error", /*response.header(AUTH_TOKEN)*/ "Exception in authenticate")
        }
        return null
    }
}

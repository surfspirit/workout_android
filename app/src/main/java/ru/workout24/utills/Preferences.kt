package ru.workout24.utills

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ru.workout24.push.data.PushBodyDto
import javax.inject.Singleton

@Singleton
class Preferences(context: Context) {

    private val mSharedPreferences: SharedPreferences? =
        context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    var authToken: String? = null
        get() = field ?: mSharedPreferences?.getString(AUTH_TOKEN, "") ?: ""
        set(newToken) {
            mSharedPreferences?.edit()?.putString(AUTH_TOKEN, newToken)?.apply()
            field = newToken
        }

    var pushToken: String? = null
        get() = field ?: mSharedPreferences?.getString(PUSH_TOKEN, "") ?: ""
        set(newToken) {
            mSharedPreferences?.edit()?.putString(PUSH_TOKEN, newToken)?.apply()
            field = newToken
        }

    var login: String? = null
        get() = field ?: mSharedPreferences?.getString(LOGIN, "") ?: ""
        set(value) {
            mSharedPreferences?.edit()?.putString(LOGIN, value)?.apply()
            field = value
        }

    var password: String? = null
        get() = field ?: mSharedPreferences?.getString(PASSWORD, "") ?: ""
        set(value) {
            mSharedPreferences?.edit()?.putString(PASSWORD, value)?.apply()
            field = value
        }

    var notify_passed: Boolean? = null
        get() = field ?: mSharedPreferences?.getBoolean(NOTIFY_PASSED, false)
        set(value) {
            if (value != null) {
                mSharedPreferences?.edit()?.putBoolean(NOTIFY_PASSED, value)?.apply()
            }
            field = value
        }

    var notifications: Boolean? = null
        get() = field ?: mSharedPreferences?.getBoolean(NOTIFICATIONS, false)
        set(value) {
            if (value != null) {
                mSharedPreferences?.edit()?.putBoolean(NOTIFICATIONS, value)?.apply()
            }
            field = value
        }

    var gender_date_selected: Boolean? = null
        get() = field ?: mSharedPreferences?.getBoolean(GENDER_DATE_SELECTED, false)
        set(value) {
            if (value != null) {
                mSharedPreferences?.edit()?.putBoolean(GENDER_DATE_SELECTED, value)?.apply()
            }
            field = value
        }

    var sub_passed: Boolean? = null
        get() = field ?: mSharedPreferences?.getBoolean(SUB_PASSED, false)
        set(value) {
            if (value != null) {
                mSharedPreferences?.edit()?.putBoolean(SUB_PASSED, value)?.apply()
            }
            field = value
        }

    var anket_passed: Boolean? = null
        get() = field ?: mSharedPreferences?.getBoolean(ANKET_PASSED, false)
        set(value) {
            if (value != null) {
                mSharedPreferences?.edit()?.putBoolean(ANKET_PASSED, value)?.apply()
            }
            field = value
        }

    var test_passed: Boolean? = null
        get() = field ?: mSharedPreferences?.getBoolean(TEST_PASSED, false)
        set(value) {
            if (value != null) {
                mSharedPreferences?.edit()?.putBoolean(TEST_PASSED, value)?.apply()
            }
            field = value
        }
    var training_sets: String = ""
        set(value) {
            mSharedPreferences?.edit()?.putString(SETS_ARRAY, value)?.apply()
            field = value
        }

    val cachedPushes: MutableSet<String> = mutableSetOf()
        get() = mSharedPreferences?.getStringSet(CACHED_PUSHES, field) ?: field

    var vk: String?
        get() = mSharedPreferences?.getString(VK, "")
        set(value) {
            mSharedPreferences?.edit()?.putString(VK, value)?.apply()
        }

    var fb: String?
        get() = mSharedPreferences?.getString(FB, "")
        set(value) {
            mSharedPreferences?.edit()?.putString(FB, value)?.apply()
        }

    var timeZone: String?
        get() = mSharedPreferences?.getString(TIME_ZONE, "")
        set(value) {
            mSharedPreferences?.edit()?.putString(TIME_ZONE, value)?.apply()
        }

    fun cachePush(push: PushBodyDto) {
        cachedPushes.add(Gson().toJson(push))
        mSharedPreferences?.edit()?.putStringSet(CACHED_PUSHES, cachedPushes)?.apply()
    }

    var userProfileId
        get() = mSharedPreferences?.getString("user_profile_id", null)
        set(value) {
            mSharedPreferences?.edit()?.putString("user_profile_id", value)?.apply()
        }

    fun clearAll() {
        sub_passed = null
        test_passed = null
        notifications = null
        gender_date_selected = null
//        mSharedPreferences?.edit()?.clear()?.apply()
        userProfileId = null
    }


    companion object {
        const val SHARED_NAME = "SHARED_NAME"
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val PUSH_TOKEN = "PUSH_TOKEN"
        const val LOGIN = "LOGIN"
        const val PASSWORD = "PASSWORD"
        const val NOTIFY_PASSED = "NOTIFY_PASSED"
        const val NOTIFICATIONS = "NOTIFICATIONS"
        const val GENDER_DATE_SELECTED = "GENDER_DATE_SELECTED"
        const val SUB_PASSED = "SUB_PASSED"
        const val ANKET_PASSED = "ANKET_PASSED"
        const val TEST_PASSED = "TEST_PASSED"
        const val SETS_ARRAY = "SETS_ARRAY"
        const val USER_OBJECT_STRING = "USER_OBJECT_STRING"
        const val CACHED_PUSHES = "CACHED_PUSHES"
        const val VK = "VK"
        const val FB = "FB"
        const val TIME_ZONE = "TIME_ZONE"
    }
}

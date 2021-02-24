package ru.workout24.utills

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun <R : Any> Boolean.ifTrue(block: () -> R?): R? {
    return if (this) block() else null
}

fun <R : Any> Boolean.ifFalse(block: () -> R?): R? {
    return if (!this) block() else null
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val arraylist = arrayListOf<T>()
    forEach {
        arraylist.add(it)
    }
    return arraylist
}

fun <T> List<T>.toAL(): ArrayList<T> = ArrayList(this)

inline fun <reified T> toArray(list: List<*>): Array<T> {
    return (list as List<T>).toTypedArray()
}

/**
 * Методы для работы с лайвдатой
 *
 *
 */

fun <T> MutableLiveData<T>.triggerSelf() {
    postValue(this.value)
}

/**
 * Методы для работы с датой
 *
 *
 */

fun String.dateFrom_dd_mm_yyyy_HH_mm(): Date? {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")

    try {
        val date = formatter.parse(this)
        return date
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }

}

fun String.dateFrom_dd_mm_yyyy(): Date? {
    val formatter = SimpleDateFormat("dd.MM.yyyy")

    try {
        val date = formatter.parse(this)
        return date
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }

}

fun Calendar.ISOfromCalendar(): String? {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        formatter.format(time)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun Date.ISOfromDate(): String? {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        formatter.format(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun String.dateFrom_ISO(): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    formatter.timeZone = TimeZone.getTimeZone("UTC")

    try {
        val date = formatter.parse(this)
        return date
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }

}

fun String?.calendarFrom_ISO(): Calendar {
    //2020-06-01T13:18:43.268797+00:00
    val formatter = SimpleDateFormat(STATISTIC_DATE_PATTERN)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    val cal = Calendar.getInstance()
    return try {
        cal.time = formatter.parse(this)
        cal
    } catch (e: ParseException) {
        e.printStackTrace()
        cal
    }
}


fun Date.to_dd_MMMString(): String? {
    val formatter = SimpleDateFormat("dd MMM")
    return formatter.format(this)
}

fun Calendar.fromCalendarTo_dd_MMM_String(): String? {
    val formatter = SimpleDateFormat("dd MMM")
    return formatter.format(time)
}

fun Date.to_dd_MMMM_HH_mm_String(): String? {
    val formatter = SimpleDateFormat("dd MMMM HH:mm")
    formatter.timeZone = Calendar.getInstance().timeZone
    return formatter.format(this)
}

fun Date.to_HH_mm_dd_MM_yyyy_String(): String? {
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy")
    formatter.timeZone = Calendar.getInstance().timeZone
    return formatter.format(this)
}

@SuppressLint("SimpleDateFormat")
fun transform(date: String?, to: String?, from: String?): String =
    SimpleDateFormat(to).format(SimpleDateFormat(from).parse(date))

@SuppressLint("SimpleDateFormat")
fun transformLocale(date: String?, to: String?, from: String?, locale: Locale): String =
    SimpleDateFormat(to, locale).format(SimpleDateFormat(from, locale).parse(date))


@SuppressLint("SimpleDateFormat")
fun transformStringDate(date: String?): String =
    SimpleDateFormat("dd.MM.yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(date))

@SuppressLint("SimpleDateFormat")
fun transformStringDateRevers(date: String?): String =
    SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd.MM.yyyy").parse(date))

@SuppressLint("SimpleDateFormat")
fun transformStringDateWording(date: String?): String =
    SimpleDateFormat("dd MMMM yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(date))

@SuppressLint("SimpleDateFormat")
fun transformStringDateWordingFromISO(date: String?): String =
    SimpleDateFormat("dd MMMM yyyy").format(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(
            date
        )
    )

@SuppressLint("SimpleDateFormat")
fun transformStringDateWordingWithoutYear(date: String?): String =
    SimpleDateFormat("dd MMMM").format(SimpleDateFormat("yyyy-MM-dd").parse(date))


@SuppressLint("SimpleDateFormat")
fun transformStringToTime(time: String?): String =
    SimpleDateFormat("HH:mm").format(SimpleDateFormat("HH:mm:ss").parse(time))

@SuppressLint("SimpleDateFormat")
fun transformStringToTimeFromISO(time: String?): String =
    SimpleDateFormat("HH:mm").format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(time))

@SuppressLint("SimpleDateFormat")
fun transformStringToTimeWithDuration(time: String?, duration: Int?): String {
    val format = SimpleDateFormat("HH:mm:ss")
    val date: Date? = format.parse(time)
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.MINUTE, duration!!)
    return SimpleDateFormat("HH:mm").format(calendar.time)
}

@SuppressLint("SimpleDateFormat")
fun transformStringToTimeWithDurationSeconds(time: String?, duration: Int?): String {
    val format = SimpleDateFormat("HH:mm:ss")
    val date: Date? = format.parse(time)
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.MINUTE, duration!!)
    return SimpleDateFormat("HH:mm:ss").format(calendar.time)
}

@SuppressLint("SimpleDateFormat")
fun transformStringToDayofweekanddate(time: String?): String =
    SimpleDateFormat("EEEE dd.MM").format(SimpleDateFormat("yyyy-MM-dd").parse(time))

@SuppressLint("SimpleDateFormat")
fun transformStringToDayofWeekandDate(date_and_time: String?): String =
    SimpleDateFormat("EEEE dd.MM").format(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
            date_and_time
        )
    )


/**
 * Методы для различных эелементов экрана
 *
 *
 */

fun EditText.setTextChangeListener(
    onChangeCallback: (view: EditText, text: CharSequence?) -> Unit
) {

    val view = this

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChangeCallback(view, s)
        }

    })
}

fun TextView.setHtmlText(text: String?) {
    setText(Html.fromHtml(text))
}

fun View.visible(value: Boolean) {
    if (value) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }

}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.toggle() {
    if (this.visibility == View.VISIBLE) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }

}

// slide the view from below itself to the current position
fun View.slideUp() {
    this.visibility = View.VISIBLE
    val animate = TranslateAnimation(
        0f, // fromXDelta
        0f, // toXDelta
        this.height.toFloat(), // fromYDelta
        0f
    )                // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    this.startAnimation(animate)
}

// slide the view from its current position to below itself
fun View.slideDown() {
    val animate = TranslateAnimation(
        0f, // fromXDelta
        0f, // toXDelta
        0f, // fromYDelta
        this.height.toFloat()
    ) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    this.startAnimation(animate)
}

/**
 * Методы для работы с Rx
 *
 *
 */

fun <T : Response<*>> Flowable<T>.apiIOSubscribe(
    successCallback: (result: Any?) -> Unit,
    errorCallback: (t: Throwable?, errorCode: Int?, message: String?) -> Unit = { _, _, _ -> }
): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({

        responseCheck(it, {
            successCallback(it)
        }, { errorCode, message ->
            errorCallback(null, errorCode, message)
        })

    }, {
        errorCallback(it, null, null)
    })
}

fun <T : Response<*>, R> Flowable<T>.apiIOSubscribeLiveData(
    liveData: MutableLiveData<R>,
    errorCallback: (t: Throwable?, errorCode: Int?, message: String?) -> Unit = { _, _, _ -> }
): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({

        responseCheck(it, {
            (it as? R)?.let {
                liveData.postValue(it)
            }
        }, { errorCode, message ->
            errorCallback(null, errorCode, message)
        })

    }, {
        errorCallback(it, null, null)
    })
}

fun <T : Response<*>, R> Flowable<T>.apiMainSubscribe(
    successCallback: (result: Any?) -> Unit,
    errorCallback: (t: Throwable?, errorCode: Int?, message: String?) -> Unit = { _, _, _ -> }
): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({

        responseCheck(it, {
            successCallback(it)
        }, { errorCode, message ->
            errorCallback(null, errorCode, message)
        })

    }, {
        errorCallback(it, null, null)
    })
}

fun <T : Response<*>, R> Flowable<T>.apiMainSubscribeLiveData(
    liveData: MutableLiveData<R>,
    errorCallback: (t: Throwable?, errorCode: Int?, message: String?) -> Unit = { _, _, _ -> }
): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({

        responseCheck(it, {
            (it as? R)?.let {
                liveData.postValue(it)
            }
        }, { errorCode, message ->
            errorCallback(null, errorCode, message)
        })

    }, {
        errorCallback(it, null, null)
    })
}

fun <T> Flowable<T>.dbIOSubscribe(
    successCallback: (result: T) -> Unit,
    errorCallback: (t: Throwable) -> Unit = {}
): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({
        successCallback(it)
    }, {
        errorCallback(it)
    })
}

fun <T> Flowable<T>.dbMainSubscribe(
    successCallback: (result: T) -> Unit,
    errorCallback: (t: Throwable) -> Unit = {}
): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        successCallback(it)
    }, {
        errorCallback(it)
    })
}

fun <T> Flowable<T>.subscribeLiveData(vararg liveData: MutableLiveData<T>): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({
        liveData.forEach { data ->
            data.postValue(it)
        }
    }, {
        //errorCallback(it)
    })
}

fun <T, K> Map<T, K>.getKeyByValue(value: K): T? {
    entries.forEach {
        if (it.value == value) {
            return it.key
        }
    }
    return null
}

fun Int.dpToPx(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun Int.pxToDp(context: Context): Int {
    return (this / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun log(message: Any?, label: String = "") {
    Log.e(label, message?.toString() ?: "null")
}

inline fun <T> Iterable<T>.doIf(predicate: Boolean, action: Iterable<T>.() -> Unit): Iterable<T> {
    if (predicate) action()
    return this
}

inline fun <T> Iterable<T>.doIfElse(predicate: Boolean, action1: Iterable<T>.() -> Unit, action2: Iterable<T>.() -> Unit): Iterable<T> {
    if (predicate) action1() else action2()
    return this
}


package ru.workout24.utills

import java.util.concurrent.TimeUnit

fun getFormattedStringWeek(count: Int): String {

    return if (count % 100 in 10..20) {
        "$count недель"
    } else if (count % 10 == 1) {
        if (count % 100 >= 10) {
            "$count неделя"
        } else {
            "$count неделя"
        }
    } else if (count % 10 in 2..4) {
        "$count недели"
    } else {
        "$count недель"
    }
}

fun getFormattedStringPovtorov(count: Int): String {

    return if (count % 100 in 10..20) {
        "$count повторов"
    } else if (count % 10 == 1) {
        if (count % 100 >= 10) {
            "$count повтор"
        } else {
            "$count повтор"
        }
    } else if (count % 10 in 2..4) {
        "$count повтора"
    } else {
        "$count повторов"
    }
}

fun getCountByRange(min:Int?,max:Int?): String?
{
    if (min==max)
        return max.toString()
    else
        return "$min - $max"
}
fun convertTimeToString(seconds: Long): String {
    /*
    return String.format(
            "%02d:%02d:%02d",
            TimeUnit.SECONDS.toHours(seconds),
            TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
            TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
    )
    */
    return String.format(
        "%02d:%02d",
        TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
        TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
    )
}
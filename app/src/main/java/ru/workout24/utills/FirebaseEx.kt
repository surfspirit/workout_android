package ru.workout24.utills

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Throwable.sendFirebaseException() {
    FirebaseCrashlytics.getInstance().recordException(this)
}
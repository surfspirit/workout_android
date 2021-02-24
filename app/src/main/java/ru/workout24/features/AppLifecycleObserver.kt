package ru.workout24.features

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.subjects.PublishSubject

object AppLifecycleObserver: LifecycleObserver {
    private val _isActiveObservable = PublishSubject.create<Boolean>()
    val isActive: PublishSubject<Boolean> get() = _isActiveObservable

    // Application level lifecycle events
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnteredForeground() {
        _isActiveObservable.onNext(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnteredBackground() {
        _isActiveObservable.onNext(false)
    }
}
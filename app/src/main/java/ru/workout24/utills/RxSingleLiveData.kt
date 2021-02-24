package ru.workout24.utills

import androidx.lifecycle.LifecycleOwner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor

/**
 * RxLiveData без сохранения значения в кэш-памяти
 */
class RxSingleLiveData<T> {
    private val observable by lazy {
        PublishProcessor.create<T>()
    }

    fun observe(lifecycleOwner: LifecycleOwner, o: (T) -> Unit) {
        RxLifecycleHandler<T>(
            lifecycleOwner,
            observable.hide().observeOn(AndroidSchedulers.mainThread()),
            o
        )
    }

    fun postValue(data: T) {
        observable.offer(data)
    }
}
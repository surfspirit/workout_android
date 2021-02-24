package ru.workout24.utills

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Класс для представления ресурса. Чтобы использовать этот класс, от него обязательно нужно отнаследоваться и переопределить абстрактные методы.
 * Чтобы обсервить данные ресурса, необходимо подписаться на свойство resourceLiveData.
 * Если там приходит не null, то последняя загрузка была успешной и можно брать данные
 * иначе можно посмотреть данные соответствующие ошибке в errorData
 * @param RequestDataType тип данных, передаваемых для осуществления запроса за ресурсом, если этот параметр нужен
 * @param ResponseDataType тип данных успешного ответа
 * @param ErrorDataType тип данных на случай ответа с ошибкой, если нужно обрабатывать данные ошибки
 * @param requestData данные, передаваемык для осуществления запроса за ресурсом, если этот параметр нужен
 */
abstract class CustomNetworkResource<RequestDataType, ResponseType, ResponseDataType, ErrorDataType>(requestData: RequestDataType) {

    // не смотреть эту функцию
    private fun getErrorType(): Type {
        val clazz = this::class.java
        val superType = clazz.genericSuperclass as ParameterizedType
        val errorType = superType.actualTypeArguments[3]
        return errorType
    }

    // region Properties
    private var _isDirty : Boolean = true
    /**
     * Нужно ли обновлять кэш, невалиден ли ресурс
     */
    var isDirty : Boolean
        get() = (_isDirty) ||
                (_resourceData.value == null || errorData != null)
        set(value) {
            _isDirty = value
        }

    private var _isLoading : Boolean = false
    /**
     * Загружается ли ресурс по сети сейчас
     */
    var isLoading : Boolean
        get() = _isLoading
        set(value) {
            _isLoading = value
        }

    private var _resourceData = MutableLiveData<ResponseDataType?>()
    val resourceData get() = _resourceData.value

    private var _errorData : ErrorDataType? = null

    /**
     * Данные при возникновении ошибки при загрузке ресурса
     */
    var errorData : ErrorDataType?
        get() = _errorData
        private set(value) {
            _errorData = value
        }

    /**
     * Последний полученный объект throwable. Имеет смысл смотреть если и resourceLiveData == nul и errorData == null
     */
    var lastThrowable : Throwable? = null

    private var _lastData : ResponseDataType? = null

    /**
     * Последние успешно полученные данные. На случай если текущие данные null из-за происходящей сейчас ошибки
     */
    var lastData : ResponseDataType? = null
        get() = _lastData

    //endregion

    private var retrofitCall : Single<Response<ResponseType>>

    init {
        retrofitCall = getRetrofitCall(requestData)
    }

    /**
     * Обработчик на обновление данных ресурса
     */
    fun onChanged(lifecycleOwner : LifecycleOwner, callback : (ResponseDataType?) -> Unit){
        _resourceData.observe(lifecycleOwner, Observer(callback))
    }

    /**
     * Взять данные из кэша, а если их нет или если isDirty, то еще и запросить обновление с сервера
     */
    fun loadIfNeeded()  {

        triggerDataFromCache()

        if (isDirty && !isLoading)
            loadFromNetwork()
    }

    /**
     * Взять данные из кэша и запросить обновление с сервера
     */
    fun load() {
        triggerDataFromCache()
        loadFromNetwork()
    }

    @SuppressLint("CheckResult")
    private fun loadFromNetwork() {
        isLoading = true

        retrofitCall
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({ response ->
                isLoading = false

                val gson = Gson()

                if (response.isSuccessful) {
                    isDirty = false
                    if (response.body()==null)
                        _resourceData.postValue(null)
                    response.body()?.let { body ->
                        val result = processResponseDataType(body)
                        _resourceData.postValue(result)
                        result?.let { saveCallResult(result) }
                    }
                }
                else {
                    errorData = gson.fromJson(response.errorBody()?.string(), getErrorType())
                    lastData = _resourceData.value
                    _resourceData.postValue(null)
                }
            }, {throwable ->
                // что-то нужно тоже положить в errorData наверное
                isLoading = false
                lastThrowable = throwable
                lastData = _resourceData.value
                _resourceData.postValue(null)
            })
    }

    @SuppressLint("CheckResult")
    private fun triggerDataFromCache(){
        if (_resourceData.value != null) {
            // триггерим обновление данных их кэша оперативной памяти
            _resourceData.triggerSelf()
        }
        else { // иначе триггерим обновление данных из персистентного кэша
            getDbFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ data->
                    _resourceData.postValue(data)
                }, {error -> /* todo */})
        }
    }

    /**
     * Почистить кэш оперативной памяти
     */
    fun clear() {
        errorData = null
        lastData = null
        _resourceData.postValue(null)
    }

    protected abstract fun getRetrofitCall(requestData : RequestDataType): Single<Response<ResponseType>>

    protected abstract fun getDbFlowable(): Single<ResponseDataType>

    protected abstract fun saveCallResult(response: ResponseDataType)

    protected abstract fun processResponseDataType(response: ResponseType):ResponseDataType?

}

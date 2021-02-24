package ru.workout24.ui.wg_bonuses

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.ui.wg_bonuses.data.dto.WgLeaderDto
import ru.workout24.ui.wg_bonuses.data.viewmodel.LeaderViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class WgLeadersPagingDataSource(
    private val api: Api,
    private val pStore: PageAnchor
) : PageKeyedDataSource<Int, WgLeaderDto>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, WgLeaderDto>
    ) {
        val page = pStore.getCurrentPage() ?: 1
        api.getWgLeaders(page, params.requestedLoadSize)
            .enqueueWithCallback { result ->
                if (pStore.hasCurrentPage() && page > 0) {
                    callback.onResult(result.data, page - 1, if (result.hasMore) page + 1 else null)
                } else {
                    callback.onResult(result.data, null, 2)
                    pStore.setCurrentPage(1)
                }
            }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, WgLeaderDto>) {
        pStore.setCurrentPage(params.key)
        api.getWgLeaders(params.key, params.requestedLoadSize)
            .enqueueWithCallback { result ->
                val nextKey = if (result.hasMore) params.key + 1 else null
                callback.onResult(result.data, nextKey)
            }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, WgLeaderDto>) {
        pStore.setCurrentPage(params.key)
        api.getWgLeaders(params.key, params.requestedLoadSize)
            .enqueueWithCallback { result ->
                val prevKey = if (params.key - 1 > 0) params.key - 1 else null
                callback.onResult(result.data, prevKey)
            }
    }
}

class WgLeadersPagingDataSourceFactory(
    private val api: Api
) : DataSource.Factory<Int, LeaderViewModel>(), PageAnchor {
    private var currentPage: Int? = null

    private lateinit var pagingDataSource: WgLeadersPagingDataSource
    lateinit var data: MutableLiveData<PageKeyedDataSource<Int, LeaderViewModel>>
        private set

    override fun create(): DataSource<Int, LeaderViewModel> {
        pagingDataSource = WgLeadersPagingDataSource(api, this)
        val mappedList = pagingDataSource.map { item ->
            LeaderViewModel(
                item.id,
                "${item.name} ${item.surname}",
                item.avatarUrl,
                item.value,
                item.place
            )
        }
        data = MutableLiveData()
        data.postValue(mappedList)
        return mappedList
    }

    fun loadSpecifyPage(page: Int) {
        val pageAlwaysLoaded = currentPage?.let {
            return@let page in it.minus(1)..it.plus(1)
        } ?: false
        if (page > 1 && !pageAlwaysLoaded) {
            currentPage = page
            pagingDataSource.invalidate()
        }
    }

    override fun getCurrentPage(): Int? = currentPage

    override fun setCurrentPage(page: Int) {
        currentPage = page
    }

    override fun hasCurrentPage(): Boolean = currentPage != null

    fun createPagedLiveData() =
        LivePagedListBuilder<Int, LeaderViewModel>(
            this,
            PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE).setPrefetchDistance(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .setMaxSize(PAGE_SIZE * 3)
                .build()
        ).setFetchExecutor(Executors.newFixedThreadPool(5)).build()

    companion object {
        const val PAGE_SIZE = 30
    }
}

/**
 * Якорь страницы
 */
interface PageAnchor {
    fun hasCurrentPage(): Boolean
    fun getCurrentPage(): Int?
    fun setCurrentPage(page: Int)
}

fun <T> Call<BaseResponse<T>>.enqueueWithCallback(successCallback: (T) -> Unit) {
    enqueue(object : Callback<BaseResponse<T>> {
        override fun onFailure(call: Call<BaseResponse<T>>, t: Throwable) {

        }

        override fun onResponse(call: Call<BaseResponse<T>>, response: Response<BaseResponse<T>>) {
            if (response.isSuccessful) {
                successCallback(response.body()!!.data!!)
            }
        }
    })
}
package ru.workout24.ui.wg_bonuses


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.bumptech.glide.Glide

import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.wg_bonuses.adapter.LeadersAdapter
import ru.workout24.ui.wg_bonuses.data.viewmodel.LeaderViewModel
import ru.workout24.ui.wg_bonuses_detail.WgBonusesDetailFragment
import ru.workout24.ui.wg_bonuses_detail.data.dto.WgLeaderDetailDto
import ru.workout24.utills.NetworkResource
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import ru.workout24.utills.base.typed_holder_adapter.OnItemSelectListener
import ru.workout24.utills.custom_views.SimpleDivider
import kotlinx.android.synthetic.main.fragment_wg_bonuses.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import javax.inject.Inject
import kotlin.math.ceil

/**
 * A simple [Fragment] subclass.
 */
class WgBonusesFragment : BaseFragment(R.layout.fragment_wg_bonuses) {
    @Inject
    lateinit var resourceProvider: ResourceProvider
    @Inject
    lateinit var api: Api

    private val glideManager by lazy {
        Glide.with(this)
    }
    private lateinit var wgLeaderDetailSource: NetworkResource.WithoutDbSavingWithQueryWithError<WgLeaderDetailDto, String>
    private var currentUserPosition: Int? = null

    private lateinit var pageSource: WgLeadersPagingDataSourceFactory
    private val adapter by lazy {
        LeadersAdapter(glideManager)
    }

    private var lastPageKey: Int? = null

    private var showMinePosButtonClick = 0
    private val listener by lazy { PageAdapterReadyListener(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageSource = WgLeadersPagingDataSourceFactory(api)
        pageSource.createPagedLiveData().observe(this, Observer { adapterData ->
            adapter.setAdapterData(adapterData)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUserPosition()
        appBar.setOnBackClick {
            controller.popBackStack()
        }
        show_mine_pos.setOnClickListener {
            if (showMinePosButtonClick == 0){
                showMinePosButtonClick += 1
                getCurrentUserId { userId ->
                    adapter.selectUserById(userId) { position ->
                        rv_bonuses.smoothScrollToPosition(position)
                    }
                }
            } else {
                showMinePosButtonClick = 0
                getCurrentUserId { userId ->
                    adapter.goToUserProfile(userId)
                }
            }
        }
        initRecycler()
    }

    private fun getCurrentUserId(callback: (userId: String) -> Unit) {
        currentUserPosition?.let {
            pageSource.loadSpecifyPage(getPageWithCurrentUser(it))
            listener.readyCallback = {
                resourceProvider.userResource.liveData.value?.id?.let { userId ->
                    callback(userId)
                    adapter.removeLoadStateListener(listener)
                }
            }
            adapter.addLoadStateListener(listener)
        }
    }

    private fun initRecycler() {
        rv_bonuses.addItemDecoration(
            SimpleDivider(
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_height),
                ContextCompat.getColor(requireContext(), R.color.silver),
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_left_margin)
            )
        )
        rv_bonuses.adapter = adapter

//        lastPageKey?.let { key ->
//            pageSource.loadSpecifyPage(key)
//        }
//        pageSource.createPagedLiveData().observe(this, Observer { adapterData ->
//            adapter.setAdapterData(adapterData)
//        })
        adapter.setOnItemSelect(object : OnItemSelectListener {
            override fun onItemSelect(position: Int, item: AbstractTypeViewModel) {
                val leader = item as LeaderViewModel
                controller.navigate(
                    R.id.action_wg_bonuses_fragment_to_wg_bonuses_detail_fragment,
                    WgBonusesDetailFragment.getBundle(leader.id)
                )
            }
        })
    }

    private fun getPageWithCurrentUser(userPosition: Int): Int {
        return ceil(userPosition.toDouble() / WgLeadersPagingDataSourceFactory.PAGE_SIZE).toInt()
    }

    private fun getCurrentUserPosition() {
        wgLeaderDetailSource = resourceProvider.wgLeaderDetail
        wgLeaderDetailSource.onChange(this, { currentUserDetail ->
            currentUserPosition = currentUserDetail.place
            show_mine_pos.setText("Вы №$currentUserPosition")
        })
        resourceProvider.userResource.onChange(this, {
            wgLeaderDetailSource.query = it.id
            wgLeaderDetailSource.load()
        })
        resourceProvider.userResource.loadIfNeeded()
    }
}

class PageAdapterReadyListener(private val _context: Context): PagedList.LoadStateListener {
    var readyCallback: () -> Unit = {}
    override fun onLoadStateChanged(
        type: PagedList.LoadType,
        state: PagedList.LoadState,
        error: Throwable?
    ) {
        if ((state == PagedList.LoadState.DONE || state == PagedList.LoadState.IDLE) && type == PagedList.LoadType.END) {
            readyCallback()
        }
        if (state == PagedList.LoadState.ERROR) {
            _context.toast("Не удалось загрузить список лидеров")
        }
    }
}
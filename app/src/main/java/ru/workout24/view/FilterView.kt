package ru.workout24.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.ui.trainings.pager.adapters.CategoryOffsetDecorator
import ru.workout24.ui.trainings.pager.adapters.MultiCheckCategoriesAdapter
import ru.workout24.ui.trainings.pager.adapters.MultiCheckCategory
import ru.workout24.ui.trainings.pager.pojos.Criteria
import ru.workout24.utills.custom_views.CustomButton
import org.jetbrains.anko.find

class FilterView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    LinearLayout(context, attr) {
    private var adapter: MultiCheckCategoriesAdapter? = null
    private var applyCallback: () -> Unit = {}
    private var resetCallback: () -> Unit = {}
    private var set: (String, Any?) -> Unit = {field, value -> }
    private var delete: (String, Any?) -> Unit = {field, value -> }

    private val recycler by lazy {
        find<RecyclerView>(R.id.recycler_view)
    }

    private val apply by lazy {
        val apply = find<CustomButton>(R.id.btn_apply)
        apply.setOnClickListener {
            applyCallback()
        }
        apply
    }

    private val reset by lazy {
        val reset = find<Button>(R.id.btn_reset)
        reset.setOnClickListener {
            adapter?.resetAll()
            resetCallback()
        }
        reset
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.filter_view, this, true)
        apply
        reset
        recycler.addItemDecoration(CategoryOffsetDecorator(resources.getDimensionPixelSize(R.dimen.filter_bottom_margin)))
    }

    fun setFilterLiveData(lifecycleOwner: LifecycleOwner, data: LiveData<MutableList<MultiCheckCategory>>) {
        data.observe(lifecycleOwner, Observer {
            setFilterData(it)
            adapter?.notifyDataSetChanged()
        })
    }

    fun setFilterData(data: List<MultiCheckCategory>) {
        adapter = MultiCheckCategoriesAdapter(data)
        val animator = recycler.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        adapter?.setChildClickListener { v, checked, group, childIndex ->
            val field = (group as MultiCheckCategory).serverName
            val value = (group.items[childIndex] as Criteria).value
            if (checked) {
                set(field, value)
            } else {
                delete(field, value)
            }
            enableApplyFilter(true)
        }
        recycler.adapter = adapter
    }

    fun setAcceptFilterLiveData(data: MutableLiveData<Boolean>, lifecycleOwner: LifecycleOwner) {
        data.observe(lifecycleOwner, Observer {
            enableApplyFilter(it)
        })
    }

    fun setFilterItem(callback: (String, Any?) -> Unit) {
        set = callback
    }

    fun deleteFilterItem(callback: (String, Any?) -> Unit) {
        delete = callback
    }

    fun enableApplyFilter(value: Boolean) {
        apply.isEnable = value
        reset.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

    fun setApplyClick(callback: () -> Unit) {
        applyCallback = callback
    }

    fun setResetClick(callback: () -> Unit) {
        resetCallback = callback
    }
}
package ru.workout24.utills.base.typed_holder_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractTypeAdapter(
    @LayoutRes private val headerResId: Int? = null,
    @LayoutRes private val footerResId: Int? = null
) : RecyclerView.Adapter<AbstractTypeHolder<out AbstractTypeViewModel>>(), SelectListener {
    private val HEADER_POSITION = 0
    private val FOOTER_POSITION get() = itemCount - 1

    private val hasHeader get() = headerResId != null
    private val hasFooter get() = footerResId != null
    private var headerViewModel: AbstractHeaderViewModel? = null
    private var footerViewModel: AbstractFooterViewModel? = null
    private var data: ArrayList<out AbstractTypeViewModel> = ArrayList(0)
    var useDiffUtil: Boolean = false
    private val adapterFilter by lazy {
        AdapterFilter()
    }
    var selectItems: Boolean = true
    private var selectPosition: Int? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> {
        val inflater = LayoutInflater.from(parent.context)
        if (hasHeader && viewType == DefaultHolderType.HEADER.value) return createHeaderHolder(
            parent,
            createHeaderView(parent, inflater)
        )
        if (hasFooter && viewType == DefaultHolderType.FOOTER.value) return createFooterHolder(
            parent,
            createFooterView(parent, inflater)
        )
        return createHolder(inflater, parent, viewType)
    }

    open fun createHeaderView(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): View {
        return inflater.inflate(headerResId!!, parent, false)
    }

    open fun createFooterView(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): View {
        return inflater.inflate(footerResId!!, parent, false)
    }

    open fun createHeaderHolder(
        parent: ViewGroup,
        headerView: View
    ): AbstractHeaderHolder<AbstractHeaderViewModel> {
        // если метод переопределен, то не вызывать super
        return DefaultHeaderHolder(headerView)
    }

    open fun createFooterHolder(
        parent: ViewGroup,
        footerView: View
    ): AbstractFooterHolder<AbstractFooterViewModel> {
        // если метод переопределен, то не вызывать super
        return DefaultFooterHolder(footerView)
    }

    abstract fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel>

    fun getRealItemCount(): Int = data.size

    override fun getItemCount(): Int {
        var count = data.size
        if (hasHeader) count += 1
        if (hasFooter) count += 1
        return count
    }

    private fun getActualPosition(position: Int) = if (hasHeader) {
        position - 1
    } else {
        position
    }

    override fun onBindViewHolder(
        holder: AbstractTypeHolder<out AbstractTypeViewModel>,
        position: Int
    ) {
        if (hasHeader && holder is AbstractHeaderHolder) {
            headerViewModel?.let { holder.adapterBind(it) }
            return
        }
        if (hasFooter && holder is AbstractFooterHolder) {
            footerViewModel?.let { holder.adapterBind(it) }
            return
        }
        if (holder is SelectableTypeHolder && selectItems) {
            holder.isSelected = position == selectPosition
        }
        holder.adapterBind(data[getActualPosition(position)])
    }

    override fun getItemViewType(position: Int): Int {
        if (hasHeader && position == HEADER_POSITION) return DefaultHolderType.HEADER.value
        if (hasFooter && position == FOOTER_POSITION) return DefaultHolderType.FOOTER.value
        return data[getActualPosition(position)].type
    }

    fun setHeader(data: AbstractHeaderViewModel) {
        if (hasHeader) {
            headerViewModel = data
            notifyItemChanged(HEADER_POSITION)
        }
    }

    fun setFooter(data: AbstractFooterViewModel) {
        if (hasFooter) {
            footerViewModel = data
            notifyItemChanged(FOOTER_POSITION)
        }
    }

    fun filter(
        query: CharSequence?,
        condition: (CharSequence, AbstractTypeViewModel) -> Boolean
    ) {
        adapterFilter.filtering(query, condition)
    }

    fun setData(data: ArrayList<out AbstractTypeViewModel>) {
        if (useDiffUtil) {
            val diffUtilCallback = DiffUtilCallback(this.data, data)
            val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback, true)
            this.data = data
            diffUtilResult.dispatchUpdatesTo(this)
        } else {
            this.data = data
            notifyDataSetChanged()
        }
    }

    override fun onSelect(position: Int, data: AbstractTypeViewModel) {
        if (selectPosition != null) notifyItemChanged(selectPosition!!)
        notifyItemChanged(position)
        selectPosition = position
    }

    private inner class AdapterFilter {
        var oldData: ArrayList<out AbstractTypeViewModel>? = null

        fun filtering(
            query: CharSequence?,
            condition: (String, T: AbstractTypeViewModel) -> Boolean
        ) {
            if (checkQuery(query)) {
                if (oldData == null) {
                    oldData = data
                }
                setData(ArrayList(oldData!!.filter { condition(query!!.toString(), it) }))
            } else {
                oldData?.let { setData(it) }
            }
        }

        private fun checkQuery(query: CharSequence?) =
            query != null && query.firstOrNull() != ' '
    }
}

abstract class AbstractTypedPaginationAdapter :
    PagedListAdapter<AbstractTypeViewModel, AbstractTypeHolder<out AbstractTypeViewModel>>(
        DIFF_CALLBACK
    ), SelectListener {

    protected var selectPosition: Int? = null
    private var itemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> {
        val inflater = LayoutInflater.from(parent.context)
        return createHolder(inflater, parent, viewType)
    }

    abstract fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel>

    override fun onBindViewHolder(
        holder: AbstractTypeHolder<out AbstractTypeViewModel>,
        position: Int
    ) {
        if (holder is SelectableTypeHolder) {
            holder.isSelected = position == selectPosition
        }
        getItem(position)?.let { item ->
            holder.adapterBind(item)
        }
    }

    override fun onSelect(position: Int, data: AbstractTypeViewModel) {
        if (selectPosition != null) notifyItemChanged(selectPosition!!)
        notifyItemChanged(position)
        selectPosition = position
        itemSelectListener?.onItemSelect(position, data)
    }

    fun setAdapterData(data: PagedList<out AbstractTypeViewModel>) {
        submitList(data as PagedList<AbstractTypeViewModel>)
    }

    fun setOnItemSelect(listener: OnItemSelectListener) {
        itemSelectListener = listener
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AbstractTypeViewModel>() {
            override fun areItemsTheSame(
                oldItem: AbstractTypeViewModel,
                newItem: AbstractTypeViewModel
            ): Boolean = oldItem.type == newItem.type

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: AbstractTypeViewModel,
                newItem: AbstractTypeViewModel
            ): Boolean = oldItem == newItem
        }
    }
}

abstract class AbstractTypedIdSelectPaginationAdapter :
    PagedListAdapter<AbstractIdTypeViewModel, AbstractTypeHolder<out AbstractIdTypeViewModel>>(
        DIFF_CALLBACK
    ), IdSelectListener {

    protected var selectId: String? = null
    protected var selectPosition: Int? = null
    protected var itemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractIdTypeViewModel> {
        val inflater = LayoutInflater.from(parent.context)
        return createHolder(inflater, parent, viewType)
    }

    abstract fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractIdTypeViewModel>

    override fun onBindViewHolder(
        holder: AbstractTypeHolder<out AbstractIdTypeViewModel>,
        position: Int
    ) {
        getItem(position)?.let { item ->
            if (holder is SelectableTypeHolder) {
                holder.isSelected = item.id == selectId
            }
            holder.adapterBind(item)
        }
    }

    override fun onSelect(itemId: String, position: Int, data: AbstractTypeViewModel) {
        if (selectId != null) notifyItemChanged(selectPosition!!)
        notifyItemChanged(position)
        selectId = itemId
        selectPosition = position
        itemSelectListener?.onItemSelect(position, data)
    }

    fun setAdapterData(data: PagedList<out AbstractIdTypeViewModel>) {
        submitList(data as PagedList<AbstractIdTypeViewModel>)
    }

    fun setOnItemSelect(listener: OnItemSelectListener) {
        itemSelectListener = listener
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AbstractIdTypeViewModel>() {
            override fun areItemsTheSame(
                oldItem: AbstractIdTypeViewModel,
                newItem: AbstractIdTypeViewModel
            ): Boolean = oldItem.type == newItem.type

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: AbstractIdTypeViewModel,
                newItem: AbstractIdTypeViewModel
            ): Boolean = oldItem == newItem
        }
    }
}

abstract class AbstractIdTypeViewModel(type: Int, val id: String) : AbstractTypeViewModel(type)

interface SelectListener {
    fun onSelect(position: Int, data: AbstractTypeViewModel)
}

interface IdSelectListener {
    fun onSelect(itemId: String, position: Int, data: AbstractTypeViewModel)
}

interface OnItemSelectListener {
    fun onItemSelect(position: Int, item: AbstractTypeViewModel)
}

abstract class AbstractHeaderHolder<ViewModel : AbstractHeaderViewModel>(itemView: View) :
    AbstractTypeHolder<ViewModel>(itemView)

abstract class AbstractFooterHolder<ViewModel : AbstractFooterViewModel>(itemView: View) :
    AbstractTypeHolder<ViewModel>(itemView)

abstract class AbstractHeaderViewModel : AbstractTypeViewModel(DefaultHolderType.HEADER.value)

abstract class AbstractFooterViewModel : AbstractTypeViewModel(DefaultHolderType.FOOTER.value)

class DefaultHeaderHolder(val view: View) : AbstractHeaderHolder<AbstractHeaderViewModel>(view) {
    override fun bind(data: AbstractHeaderViewModel) {}
}

class DefaultHeaderViewModel : AbstractHeaderViewModel()

class DefaultFooterHolder(val view: View) : AbstractFooterHolder<AbstractFooterViewModel>(view) {
    override fun bind(data: AbstractFooterViewModel) {}
}

class DefaultFooterViewModel : AbstractHeaderViewModel()

enum class DefaultHolderType(val value: Int) {
    HEADER(Integer.MIN_VALUE),
    FOOTER(Integer.MAX_VALUE)
}

class DiffUtilCallback(
    private val oldList: ArrayList<out AbstractTypeViewModel>,
    private val newList: ArrayList<out AbstractTypeViewModel>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].type == newList[newItemPosition].type

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
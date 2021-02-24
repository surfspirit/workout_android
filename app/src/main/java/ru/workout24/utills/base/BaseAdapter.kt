package ru.workout24.utills.base


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager

/*
abstract class BaseAdapter(
    private var headerResId: Int? = null,
    private var normalResId: Int? = null,
    private var footerResId: Int? = null,
    private var dividerResId: Int? = null
) : RecyclerView.Adapter<BaseAdapter.ViewHolder>() {

    private val HEADER_MARKER = 0
    private val DATA_MARKER = 1
    private val DIVIDER_MARKER = 2
    private val FOOTER_MARKER = 3

    private var comparatorCallback: Comparator<in Any>? = null
    private var onItemClickCallback: (pos: Int?, item: Any?) -> Unit = { pos, item -> }
    private var getNormalResCallback: (pos: Int, item: Any) -> Int? = { pos, item -> normalResId }
    private var onBindHeaderCallback: (holder: ViewHolder) -> Unit = {}
    private var onBindDividerCallback: (holder: ViewHolder, pos: Int) -> Unit = { holder, pos -> }
    private var onBindNormalCallback: (holder: ViewHolder, pos: Int) -> Unit = { holder, pos -> }
    private var onBindFooterCallback: (holder: ViewHolder) -> Unit = {}
    private var onDividerCallback: (pos: Int, item: Any) -> Boolean = { pos, item -> false }

    private var offsetPosition = 0
    private var dataPosition = 0

    var glide: RequestManager? = null

    var offsetData = arrayListOf<Int>()
    var data = arrayListOf<Any>()
        set(value) {
            //itemCount += value.size
            offsetData = arrayListOf()
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        when (offsetData[offsetPosition]) {
            HEADER_MARKER -> {
                offsetPosition += 1
                return ViewHolder(LayoutInflater.from(parent.context).inflate(headerResId!!, parent, false))
            }
            DIVIDER_MARKER -> {
                offsetPosition += 1
                return ViewHolder(LayoutInflater.from(parent.context).inflate(dividerResId!!, parent, false))
            }
            DATA_MARKER -> {
                val resId = getNormalResCallback(dataPosition, data[dataPosition])
                offsetPosition += 1
                //dataPosition += 1
                resId?.let {
                    return ViewHolder(LayoutInflater.from(parent.context).inflate(it, parent, false))
                } ?: run {
                    return ViewHolder(View(parent.context))
                }
            }
            FOOTER_MARKER -> {
                offsetPosition += 1
                return ViewHolder(LayoutInflater.from(parent.context).inflate(footerResId!!, parent, false))
            }
            else -> {
                offsetPosition += 1
                return ViewHolder(View(parent.context))
            }
        }
    }

    private fun getDataPosition(): Int {
        val all = offsetData.filter { it == DATA_MARKER }.size
        val sub = offsetData.subList(offsetPosition - 1, offsetData.lastIndex).filter { it == DATA_MARKER }.size

        return all - sub
    }

    override fun getItemCount(): Int {
        if (offsetData.isEmpty()) initOffsetData()

        return offsetData.size
    }

    private fun initOffsetData() {
        headerResId?.let {
            offsetData.add(HEADER_MARKER)
        }

        dividerResId?.let { res ->
            data.forEachIndexed { index, item ->
                if (onDividerCallback(index, item)) {
                    offsetData.add(DIVIDER_MARKER)
                    offsetData.add(DATA_MARKER)
                } else {
                    offsetData.add(DATA_MARKER)
                }
            }
        } ?: run {
            offsetData.addAll(Collections.nCopies(data.size, DATA_MARKER))
        }

        footerResId?.let {
            offsetData.add(FOOTER_MARKER)
        }
    }

    open inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun <T : View> find(id: Int): T {
            return view.findViewById(id)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (offsetData[offsetPosition - 1]) {
            HEADER_MARKER -> {
                onBindHeaderCallback(holder)
            }
            DIVIDER_MARKER -> {
                onBindDividerCallback(holder, dataPosition)
            }
            DATA_MARKER -> {
                onBindNormalCallback(holder, dataPosition)
                dataPosition = getDataPosition()
            }
            FOOTER_MARKER -> {
                onBindFooterCallback(holder)
            }
            else -> {

            }
        }
    }

    fun setNormalRes(callback: (pos: Int, item: Any) -> Int?) {
        getNormalResCallback = callback
    }

    fun setOnBindHeader(onBindHeader: (holder: ViewHolder) -> Unit) {
        onBindHeaderCallback = onBindHeader
    }

    fun setOnBindDivider(onBindDivider: (holder: ViewHolder, pos: Int) -> Unit) {
        onBindDividerCallback = onBindDivider
    }

    fun setOnBindNormal(onBindNormal: (holder: ViewHolder, pos: Int) -> Unit) {
        onBindNormalCallback = onBindNormal
    }

    fun setOnBindFooter(onBindFooter: (holder: ViewHolder) -> Unit) {
        onBindFooterCallback = onBindFooter
    }

    fun setOnItemClick(onItemClick: (position: Int?, item: Any?) -> Unit) {
        onItemClickCallback = onItemClick
    }

    fun callOnItemClick(position: Int? = null, item: Any? = null) {
        onItemClickCallback(position, item)
    }


    fun setDividers(
        categoryDividerResId: Int,
        /*comparatorCallback: Comparator<in Any>,*/
        onDividerCallback: (pos: Int, item: Any) -> Boolean
    ) {
        //this.comparatorCallback = comparatorCallback
        this.dividerResId = categoryDividerResId
        this.onDividerCallback = onDividerCallback

    }


    private fun loadPhoto(imageView: ImageView, url: String) {
        glide?.let {
            it.bitmapTarget(imageView, url)
        }
    }
}
*/

abstract class BaseAdapter(
    val resource: Int
) : RecyclerView.Adapter<BaseAdapter.ViewHolder>() {

    var onItemClickCallback: (pos: Int) -> Unit = {}
    private var onBindCallback: (holder: View, pos: Int) -> Unit = { holder, pos -> }
    private var onDataUpdatedCallback: () -> Unit = {}

    var glide: RequestManager? = null

    var count = 0

    var data = arrayListOf<Any>()
        set(value) {
            count = value.size
            field = value
            onDataUpdatedCallback()
        }
        get() {
            return field
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(resource, parent, false))

    override fun getItemCount(): Int = count

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener { onItemClickCallback(position) }
        onBindCallback(holder.itemView, position)
    }

    fun setOnItemClick(callback: (position: Int) -> Unit) {
        onItemClickCallback = callback
    }

    fun setOnDataUpdated(callback: () -> Unit) {
        onDataUpdatedCallback = callback
    }

    fun onBind(callback: (holder: View, pos: Int) -> Unit) {
        onBindCallback = callback
    }

    private fun loadPhoto(imageView: ImageView, url: String) {
        glide?.let {
            it.load(url).into(imageView)
        }
    }
}

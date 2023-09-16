package com.a10miaomiao.bilimiao.comm.recycler

import android.view.ViewGroup
import cn.a10miaomiao.miao.binding.MiaoBinding
import com.a10miaomiao.bilimiao.comm.entity.home.RecommendCardInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule

open class MiaoBindingAdapter<T>(
    data: MutableList<T>?,
    private val ui: MiaoBindingItemUi<T>,
) : BaseQuickAdapter<T, MiaoBindingViewHolder>(0, data) , LoadMoreModule {

    init {
        loadMoreModule.loadMoreView = CustomLoadMoreView()
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): MiaoBindingViewHolder {
        val data = getItemOrNull(0)
        return if (data == null) {
            super.onCreateDefViewHolder(parent, viewType)
        } else {
            val binding = MiaoBinding()
            if (data is RecommendCardInfo) println(listOf("ui.getView", (data as RecommendCardInfo).title))
            val view = ui.getView(binding, data)
            return MiaoBindingViewHolder(binding, view)
        }
    }

    override fun convert(holder: MiaoBindingViewHolder, item: T) {
        if (item is RecommendCardInfo) println(listOf("ui.update", holder.layoutPosition - headerLayoutCount, (item as RecommendCardInfo).title))
        ui.update(holder.binding, item, holder.layoutPosition - headerLayoutCount)
    }

}
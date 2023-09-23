package com.a10miaomiao.bilimiao.page.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bilibili.app.dynamic.v2.ModuleOuterClass
import cn.a10miaomiao.miao.binding.android.view._show
import cn.a10miaomiao.miao.binding.android.view._tag
import cn.a10miaomiao.miao.binding.miaoEffect
import com.a10miaomiao.bilimiao.comm.*
import com.a10miaomiao.bilimiao.comm.delegate.theme.ThemeDelegate
import com.a10miaomiao.bilimiao.comm.recycler.*
import com.a10miaomiao.bilimiao.commponents.dynamic.dynamicCardView
import com.a10miaomiao.bilimiao.commponents.dynamic.dynamicUpItem
import com.a10miaomiao.bilimiao.commponents.loading.ListState
import com.a10miaomiao.bilimiao.commponents.loading.listStateView
import com.a10miaomiao.bilimiao.commponents.video.videoItem
import com.a10miaomiao.bilimiao.config.config
import com.a10miaomiao.bilimiao.page.bangumi.BangumiDetailFragment
import com.a10miaomiao.bilimiao.page.user.UserFragment
import com.a10miaomiao.bilimiao.page.video.VideoInfoFragment
import com.a10miaomiao.bilimiao.store.WindowStore
import com.a10miaomiao.bilimiao.widget.recyclerviewAtViewPager2
import com.chad.library.adapter.base.listener.OnItemClickListener
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import splitties.dimensions.dip
import splitties.toast.toast
import splitties.views.backgroundColor
import splitties.views.dsl.core.*
import splitties.views.dsl.recyclerview.recyclerView

class DynamicFragment : RecyclerViewFragment(), DIAware {

    companion object {
        fun newFragmentInstance(): DynamicFragment {
            val fragment = DynamicFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override val di: DI by lazyUiDi(ui = { ui })

    private val viewModel by diViewModel<DynamicViewModel>(di)

    private val themeDelegate by instance<ThemeDelegate>()

    private var themeId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (themeDelegate.getThemeResId() != themeId) {
            ui.cleanCacheView()
            themeId = themeDelegate.getThemeResId()
        }
        ui.parentView = container
        return ui.root
    }

    override fun refreshList() {
        if (!viewModel.currentDynamicPage.paginationInfo.loading) {
            viewModel.refreshList()
        }
    }

    private val handleRefresh = SwipeRefreshLayout.OnRefreshListener {
        viewModel.refreshList()
    }

    private val handleItemClick = OnItemClickListener { adapter, view, position ->
//        val item = viewModel.list.data[position]
//        val args = bundleOf(
//            MainNavArgs.id to item.param
//        )
//        if (item.goto == "av" || item.goto == "vertical_av") {
//            Navigation.findNavController(view)
//                .navigate(MainNavGraph.action.global_to_videoInfo, args)
//        } else {
//            BiliUrlMatcher.toUrlLink(view, item.uri)
//        }
    }

    private val handleAuthorClick = View.OnClickListener {
        val tag = it.tag
        if (tag is Pair<*, *>
            && tag.first is Int
            && tag.second is String
        ) {
            val (type, id) = tag as Pair<Int, String>
            when (type) {
                ModuleOuterClass.ModuleDynamicType.mdl_dyn_archive_VALUE -> {
                    val args = UserFragment.createArguments(id)
                    Navigation.findNavController(it)
                        .navigate(UserFragment.actionId, args)
                }

                ModuleOuterClass.ModuleDynamicType.mdl_dyn_pgc_VALUE -> {
                    val args = BangumiDetailFragment.createArguments(id)
                    Navigation.findNavController(it)
                        .navigate(BangumiDetailFragment.actionId, args)
                }

                else -> {
                    toast("未知跳转类型")
                }
            }
        }
    }

    private val handleDynamicContentClick = View.OnClickListener {
        val index = it.tag
        if (index is Int && index in viewModel.currentDynamicPage.paginationInfo.data.indices) {
            val item = viewModel.currentDynamicPage.paginationInfo.data[index]
            when (item.dynamicType) {
                ModuleOuterClass.ModuleDynamicType.mdl_dyn_archive_VALUE -> {
                    val args = VideoInfoFragment.createArguments(item.dynamicContent.id)
                    Navigation.findNavController(it)
                        .navigate(VideoInfoFragment.actionId, args)
                }

                ModuleOuterClass.ModuleDynamicType.mdl_dyn_pgc_VALUE -> {
                    val args = BangumiDetailFragment.createArguments(item.dynamicContent.id)
                    Navigation.findNavController(it)
                        .navigate(BangumiDetailFragment.actionId, args)
                }

                else -> {
                    toast("未知跳转类型")
                }
            }
        }
    }

    private val handleUpClick = View.OnClickListener { view ->
        val tag = view.tag as Int
        val selectedUp = viewModel.upList[tag]

        val currPos = mLayoutManager?.findFirstCompletelyVisibleItemPosition() ?: 0
        val currOffset = mLayoutManager?.findViewByPosition(currPos)?.y?.toInt() ?: 0
        viewModel.currentDynamicPage.scrollLocation =
            DynamicViewModel.ScrollLocation(currPos, currOffset)

        val alreadySelected = selectedUp.uid == viewModel.selectedUpUid
        val newUid = if (alreadySelected) null else selectedUp.uid

        viewModel.setSelectedAndLoadNewIfPossible(newUid) {
            val (newPos, newOffset) = viewModel.currentDynamicPage.scrollLocation
            mLayoutManager?.scrollToPositionWithOffset(newPos, newOffset)
        }
    }

    private val dynamicItemUi = miaoBindingItemUi<DynamicViewModel.DataInfo> { item, index ->
        dynamicCardView(
            dynamicType = item.dynamicType,
            mid = item.mid,
            name = item.name,
            face = item.face,
            labelText = item.labelText,
            like = item.like,
            reply = item.reply,
            contentView = videoItem(
                title = item.dynamicContent.title,
                pic = item.dynamicContent.pic,
                remark = item.dynamicContent.remark,
            ).apply {
                _tag = index
                miaoEffect(null) {
                    setOnClickListener(handleDynamicContentClick)
                }
//                _show = item.dynamicType == ModuleOuterClass.ModuleDynamicType.mdl_dyn_archive
            },
            onAuthorClick = handleAuthorClick,
        )
    }

    private val upListItemUi = miaoBindingItemUi<DynamicViewModel.UpListItem> { item, index ->
        val isSelected = viewModel.selectedUpUid == item.uid

        dynamicUpItem(item.face, item.name, isSelected, item.hasUpdate).apply {
            _tag = index

            miaoEffect(null) {
                setOnClickListener(handleUpClick)
            }
        }
    }

    val ui = miaoBindingUi {
        val windowStore = miaoStore<WindowStore>(viewLifecycleOwner, di)
        val contentInsets = windowStore.getContentInsets(parentView)

//        if (resources.configuration.screenWidthDp > 400) {
        horizontalLayout {
            backgroundColor = config.blockBackgroundColor

            views {
                +dynamicUpListView(viewModel.upList)..lParams(
                    matchParent,
                    matchParent
                ) {
                    width = dip(220)
                    bottomMargin = contentInsets.bottom
                }

                +dynamicRecyclerView(viewModel.currentDynamicPage, contentInsets)
            }
        }
//        } else {
//            verticalLayout {
//                backgroundColor = config.blockBackgroundColor
//
//                views {
//                    +dynamicUpListView(viewModel.upList, contentInsets)..lParams(
//                        matchParent,
//                        matchParent
//                    ) {
//                        height = dip(64)
////                        bottomMargin = contentInsets.bottom
//                    }
//
//                    +dynamicRecyclerView(viewModel.currentDynamicPage, contentInsets)
//                }
//            }
//        }
    }

    fun MiaoUI.dynamicRecyclerView(
        dynamicPaginationInfo: DynamicViewModel.DynamicPaginationInfo,
        contentInsets: WindowStore.Insets
    ): View {
        val paginationInfo = dynamicPaginationInfo.paginationInfo

        return verticalLayout {
            views {
                +recyclerviewAtViewPager2 {
                    mLayoutManager = _miaoLayoutManage(
                        LinearLayoutManager(requireContext())
                    )
                    clipToPadding = false
                    setPadding(0, dip(6), 0, dip(6))

                    val mAdapter = _miaoAdapter(
                        items = paginationInfo.data,
                        itemUi = dynamicItemUi,
                        isForceUpdate = true
                    ) {
                        stateRestorationPolicy =
                            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                        setOnItemClickListener(handleItemClick)
                        loadMoreModule.setOnLoadMoreListener {
                            viewModel.loadMode()
                        }
                    }
                    footerViews(mAdapter) {
                        +listStateView(
                            when {
                                viewModel.triggered -> ListState.NORMAL
                                paginationInfo.loading -> ListState.LOADING
                                paginationInfo.fail -> ListState.FAIL
                                paginationInfo.finished -> ListState.NOMORE
                                else -> ListState.NORMAL
                            },
                            viewModel::tryAgainLoadData,
                        )..lParams(matchParent, wrapContent) {
                            topMargin = dip(16)
                            bottomMargin = dip(16) + contentInsets.bottom
                        }

                    }
                }.wrapInSwipeRefreshLayout {
                    setColorSchemeResources(config.themeColorResource)
                    setOnRefreshListener(handleRefresh)
                    _isRefreshing = viewModel.triggered
                }..lParams(matchParent, matchParent)
            }
        }
    }

    fun MiaoUI.dynamicUpListView(
        upList: MutableList<DynamicViewModel.UpListItem>,
        @Orientation orientation: Int = RecyclerView.VERTICAL
    ): View = horizontalLayout {
        views {
            +recyclerView {
                _miaoLayoutManage(
                    LinearLayoutManager(context, orientation, false)
                )
                clipToPadding = false
                setPadding(0, dip(6), 0, dip(6))

                _miaoAdapter(
                    items = upList,
                    itemUi = upListItemUi,
                    isForceUpdate = true
                ) {
                    stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//                    setOnItemClickListener(handleItemClick)
                }
            }..lParams(matchParent, matchParent)
        }
    }
}
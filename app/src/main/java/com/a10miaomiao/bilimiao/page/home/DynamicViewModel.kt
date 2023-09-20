package com.a10miaomiao.bilimiao.page.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bilibili.app.dynamic.v2.DynamicCommonOuterClass
import bilibili.app.dynamic.v2.DynamicGrpc
import bilibili.app.dynamic.v2.DynamicOuterClass
import bilibili.app.dynamic.v2.ModuleOuterClass
import bilibili.app.dynamic.v2.ModuleOuterClass.DynamicItem
import com.a10miaomiao.bilimiao.comm.MiaoBindingUi
import com.a10miaomiao.bilimiao.comm.entity.comm.PaginationInfo
import com.a10miaomiao.bilimiao.comm.network.request
import com.a10miaomiao.bilimiao.comm.utils.DebugMiao
import com.a10miaomiao.bilimiao.store.FilterStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class DynamicViewModel(
    override val di: DI,
) : ViewModel(), DIAware {

    val context: Context by instance()
    val ui: MiaoBindingUi by instance()
    val fragment: Fragment by instance()
    val filterStore: FilterStore by instance()

    //    private var _offset = ""
//    private var _baseline = ""

    val allDynamicPage = DynamicPaginationInfo()

    var upList: MutableList<UpListItem> = mutableListOf()
        private set
    val upDynamicPagesMap: MutableMap<String, DynamicPaginationInfo> = mutableMapOf()

    var triggered = false

    var selectedUpUid: String? = null
        private set

    var currentDynamicPage: DynamicPaginationInfo = allDynamicPage
        private set


    init {
        loadAllData()
    }

    fun setSelectedUpAndLoadNewIfPossible(uid: String?) {
        ui.setState {
            selectedUpUid = uid
        }

        if (uid != null) {
            // putIfAbsent
            upDynamicPagesMap.getOrPut(uid) { DynamicPaginationInfo() }
        }

        ui.setState {
            currentDynamicPage = upDynamicPagesMap[selectedUpUid ?: ""] ?: allDynamicPage

            if (uid != null && currentDynamicPage.paginationInfo.data.isEmpty()) {
                loadUpData(uid, "")
            }
        }
    }


    fun tryAgainLoadData() {
        val (loading, finished) = this.currentDynamicPage.paginationInfo
        if (!finished && !loading) {
            if (selectedUpUid == null) {
                loadAllData()
            } else {
                loadUpData(selectedUpUid!!)
            }
        }
    }

    fun loadMode() {
        val (loading, finished, pageNum) = this.currentDynamicPage.paginationInfo
        if (!finished && !loading) {
            val selectedUpUid = selectedUpUid
            if (selectedUpUid == null) {
                loadAllData(true)
            } else {
                loadUpData(selectedUpUid, currentDynamicPage.offset)
            }
        }
    }

    fun refreshList() {
        ui.setState {
            triggered = true
            val selectedUpUid = selectedUpUid
            if (selectedUpUid == null) {
                loadAllData()
            } else {
                loadUpData(selectedUpUid, "")
            }
        }
    }

    private fun loadUpData(
        uid: String,
        offset: String = ""
    ) = viewModelScope.launch(Dispatchers.IO) {
        val upPage = upDynamicPagesMap[uid]!!

        try {
            ui.setState {
                upPage.paginationInfo.loading = true
            }
            val page = if (offset.isBlank()) 1 else upPage.paginationInfo.pageNum

            val req = DynamicOuterClass.DynVideoPersonalReq.newBuilder()
                .setHostUid(uid.toLong())
                .setLocalTime(8)
                .setOffset(offset)
                .setPage(page)
                .build()
            val result = DynamicGrpc.getDynVideoPersonalMethod()
                .request(req)
                .awaitCall()
//            if (result.hasDynamicList()) {

            upPage.offset = result.offset
            upPage.baseline = result.readOffset
            upPage.paginationInfo.pageNum = page + 1

            val itemsList = result.listList
                .filter { item ->
                    item.cardType != DynamicCommonOuterClass.DynamicType.dyn_none
                            && item.cardType != DynamicCommonOuterClass.DynamicType.ad
                }
                .map { item ->
                    getDynamicItemInfo(item)
                }
            DebugMiao.log(itemsList)

            ui.setState {
                if (offset.isBlank()) {
                    upPage.paginationInfo = PaginationInfo()
                    upPage.paginationInfo.data = itemsList.toMutableList()
                } else {
                    upPage.paginationInfo.data.addAll(itemsList)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            ui.setState {
                upPage.paginationInfo.fail = true
            }
        } finally {
            ui.setState {
                upPage.paginationInfo.loading = false
                triggered = false
            }
        }
    }

    private fun loadAllData(
        loadMore: Boolean = true
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            ui.setState {
                allDynamicPage.paginationInfo.loading = true
            }

            val offset = if (loadMore) allDynamicPage.offset else ""
            val type = if (offset.isBlank()) {
                DynamicCommonOuterClass.Refresh.refresh_new
            } else {
                DynamicCommonOuterClass.Refresh.refresh_history
            }
            val req = DynamicOuterClass.DynVideoReq.newBuilder()
                .setRefreshType(type)
                .setLocalTime(8)
                .setOffset(offset)
                .setUpdateBaseline(allDynamicPage.baseline)
                .build()
            val result = DynamicGrpc.getDynVideoMethod()
                .request(req)
                .awaitCall()
            if (result.hasDynamicList()) {
                val dynamicListData = result.dynamicList
                if (offset.isBlank()) {
                    // 只有下拉有up列表，上拉时无
                    val newUpList = result.videoUpList.listList.map {
                        getUpItem(it)
                    }
                    upList = newUpList.toMutableList()
                }

                allDynamicPage.offset = dynamicListData.historyOffset
                allDynamicPage.baseline = dynamicListData.updateBaseline

                val itemsList = dynamicListData.listList
                    .filter { item ->
                        item.cardType != DynamicCommonOuterClass.DynamicType.dyn_none
                                && item.cardType != DynamicCommonOuterClass.DynamicType.ad
                    }
                    .map { item ->
                        getDynamicItemInfo(item)
                    }

                DebugMiao.log(itemsList)

                ui.setState {
                    if (offset.isBlank()) {
                        allDynamicPage.paginationInfo = PaginationInfo()
                        allDynamicPage.paginationInfo.data = itemsList.toMutableList()
                    } else {
                        allDynamicPage.paginationInfo.data.addAll(itemsList)
                    }
                }
            } else {
                ui.setState {
                    allDynamicPage.paginationInfo.data = mutableListOf()
                    allDynamicPage.paginationInfo.finished = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ui.setState {
                allDynamicPage.paginationInfo.fail = true
            }
        } finally {
            ui.setState {
                allDynamicPage.paginationInfo.loading = false
                triggered = false
            }
        }
    }

    private fun getUpItem(item: DynamicOuterClass.UpListItem): UpListItem {
        return UpListItem(
            hasUpdate = item.hasUpdate, // 是否有更新
            face = item.face, // up主头像
            name = item.name, // up主昵称
            uid = item.uid.toString(), // up主uid
            pos = item.pos // 排序字段 从1开始
        )
    }

    private fun getDynamicItemInfo(item: DynamicItem): DataInfo {
        val modules = item.modulesList
        val userModule = modules.first { it.hasModuleAuthor() }.moduleAuthor
        val descModule = modules.find { it.hasModuleDesc() }?.moduleDesc
        val dynamicModule = modules.first { it.hasModuleDynamic() }.moduleDynamic
        val statModule = modules.first { it.hasModuleStat() }.moduleStat
        return DataInfo(
            mid = userModule.author.mid.toString(),
            name = userModule.author.name,
            face = userModule.author.face,
            labelText = userModule.ptimeLabelText,
            dynamicType = dynamicModule.typeValue,
            like = statModule.like,
            reply = statModule.reply,
            dynamicContent = getDynamicContent(dynamicModule),
        )
    }

    private fun getDynamicContent(dynamicModule: ModuleOuterClass.ModuleDynamic): DynamicContentInfo {
        return when (dynamicModule.type) {
            ModuleOuterClass.ModuleDynamicType.mdl_dyn_archive -> {
                val dynArchive = dynamicModule.dynArchive
                DynamicContentInfo(
                    id = dynArchive.avid.toString(),
                    title = dynArchive.title,
                    pic = dynArchive.cover,
                    remark = dynArchive.coverLeftText2 + "    " + dynArchive.coverLeftText3,
                )
            }

            ModuleOuterClass.ModuleDynamicType.mdl_dyn_pgc -> {
                val dynPgc = dynamicModule.dynPgc
                DynamicContentInfo(
                    id = dynPgc.seasonId.toString(),
                    title = dynPgc.title,
                    pic = dynPgc.cover,
                    remark = dynPgc.coverLeftText2 + "    " + dynPgc.coverLeftText3,
                )
            }

            else -> DynamicContentInfo("")
        }
    }

    data class DynamicPaginationInfo(
        var paginationInfo: PaginationInfo<DataInfo> = PaginationInfo(),
        var offset: String = "",
        var baseline: String = ""
    )

    data class DataInfo(
        val mid: String,
        val name: String,
        val face: String,
        val labelText: String,
        val like: Long,
        val reply: Long,
        val dynamicType: Int,
        val dynamicContent: DynamicContentInfo,
    )

    data class DynamicContentInfo(
        val id: String,
        val title: String = "",
        val pic: String = "",
        val remark: String? = null,
    )

    data class UpListItem(
        // 是否有更新
        val hasUpdate: Boolean,
        // up主头像
        val face: String,
        // up主昵称
        val name: String,
        // up主uid
        val uid: String,
        // 排序字段 从1开始
        val pos: Long,
//        // 用户类型
//        val userItemType: UserItemType,
//        // 直播头像样式-日
//        val displayStyleDay: UserItemStyle,
//        // 直播头像样式-夜
//        val displayStyleNight: UserItemStyle,
//        // 直播埋点
//        val styleId: Long,
//        // 直播状态
//        val liveState: LiveState,
//        // 分割线
//        val separator: Boolean,
//        // 跳转
//        val uri: String,
//        // UP主预约上报使用
//        val isRecall: Boolean
    )

    enum class LiveState {
        // 未直播
        LIVE_NONE,

        // 直播中
        LIVE_LIVE,

        // 轮播中
        LIVE_ROTATION
    }
}
//19:51:18.861  I  footprint: "6e4d753732fddd9971251ca13b650839"
//19:51:18.861  I  list {
//19:51:18.861  I    face: "https://i1.hdslb.com/bfs/face/feda887ac0f2a7bd98c827bfb03a2ecfb62cb723.jpg"
//19:51:18.861  I    has_update: true
//19:51:18.861  I    name: "\344\276\233\347\224\265\346\234\215\345\212\241\347\253\231\346\212\200\346\234\257\345\221\230Ran"
//19:51:18.861  I    pos: 1
//19:51:18.861  I    style_id: 0
//19:51:18.861  I    uid: 1142554945
//19:51:18.861  I    user_item_type: user_item_type_normal
//19:51:18.861  I    user_item_type_value: 3
//19:51:18.861  I  }
//19:51:18.862  I  title: "\346\234\200\345\270\270\350\256\277\351\227\256"
//19:51:18.862  I  title_switch: 1
package com.a10miaomiao.bilimiao.commponents.video

import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setMargins
import cn.a10miaomiao.miao.binding.android.view._backgroundColor
import cn.a10miaomiao.miao.binding.android.view._backgroundTintList
import cn.a10miaomiao.miao.binding.android.view._show
import cn.a10miaomiao.miao.binding.android.widget._text
import cn.a10miaomiao.miao.binding.miaoEffect
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.comm.MiaoUI
import com.a10miaomiao.bilimiao.comm._network
import com.a10miaomiao.bilimiao.comm.utils.DebugMiao
import com.a10miaomiao.bilimiao.comm.utils.HtmlTagHandler
import com.a10miaomiao.bilimiao.comm.utils.NumberUtil
import com.a10miaomiao.bilimiao.comm.views
import com.a10miaomiao.bilimiao.config.config
import com.a10miaomiao.bilimiao.widget.glideRationImageView
import com.a10miaomiao.bilimiao.widget.rcImageView
import splitties.dimensions.dip
import splitties.views.bottomPadding
import splitties.views.dsl.core.*
import splitties.views.dsl.material.materialCardView
import splitties.views.imageResource
import splitties.views.lines
import splitties.views.padding

fun MiaoUI.videoItem(
    title: String? = null,
    pic: String? = null,
    upperName: String? = null,
    remark: String? = null,
    playNum: String? = null,
    damukuNum: String? = null,
    isHtml: Boolean = false,
): View {
    return horizontalLayout {
        layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
        setBackgroundResource(config.selectableItemBackground)
        padding = dip(10)

        views {
            // 封面
            +rcImageView {
                radius = dip(5)
                _network(pic, "@672w_378h_1c_")
            }..lParams {
                width = dip(140)
                height = dip(85)
                rightMargin = dip(8)
            }

            +verticalLayout {

                views {
                    // 标题
                    +textView {
                        ellipsize = TextUtils.TruncateAt.END
                        maxLines = 2
                        setTextColor(config.foregroundColor)
                        textSize = 14f
                        if (isHtml) {
                            miaoEffect(title) {
                                DebugMiao.log(it)
                                text = HtmlTagHandler.fromHtml("<html><body>$it</body></html>")
                            }
                        } else {
                            _text = title ?: ""
                        }
                    }..lParams(matchParent, matchParent) {
                        weight = 1f
                    }

                    // UP主
                    +horizontalLayout {
                        gravity = Gravity.CENTER_VERTICAL
                        _show = upperName != null

                        views {
                            +imageView {
                                imageResource = R.mipmap.ic_card_up//R.drawable.icon_up
                                imageTintList =
                                    ColorStateList.valueOf(config.foregroundAlpha45Color)
//                                apply(ViewStyle.roundRect(dip(5)))
                            }..lParams {
                                width = dip(16)
                                height = dip(16)
                                topMargin = dip(1)
                                rightMargin = dip(2)
                            }

                            +textView {
                                textSize = 14f
                                setTextColor(config.foregroundAlpha45Color)
                                maxLines = 1
                                _text = upperName ?: ""
                            }
                        }
                    }

                    // 备注
                    +horizontalLayout {
                        gravity = Gravity.CENTER_VERTICAL
                        _show = remark != null

                        views {

                            +textView {
                                textSize = 14f
                                setTextColor(config.foregroundAlpha45Color)
                                _text = remark ?: ""
                            }
                        }
                    }

                    // 播放量，弹幕数量
                    +horizontalLayout {
                        gravity = Gravity.CENTER_VERTICAL
                        _show = playNum != null || damukuNum != null

                        views {
                            +imageView {
                                imageResource =
                                    R.mipmap.ic_card_play//R.drawable.ic_info_views//R.drawable.ic_play_circle_outline_black_24dp
                                imageTintList =
                                    ColorStateList.valueOf(config.foregroundAlpha45Color)
                            }..lParams {
                                width = dip(16)
                                height = dip(16)
                                topMargin = dip(1)
                                rightMargin = dip(2)
                            }
                            +textView {
                                textSize = 14f
                                setTextColor(config.foregroundAlpha45Color)
                                _text = NumberUtil.converString(playNum ?: "0")
                            }
                            +space()..lParams(width = dip(10))
                            +imageView {
                                imageResource =
                                    R.mipmap.ic_card_danmu//R.drawable.ic_info_danmakus//R.drawable.ic_subtitles_black_24dp
                                imageTintList =
                                    ColorStateList.valueOf(config.foregroundAlpha45Color)
                            }..lParams {
                                width = dip(16)
                                height = dip(16)
                                topMargin = dip(1)
                                rightMargin = dip(2)
                            }
                            +textView {
                                textSize = 14f
                                setTextColor(config.foregroundAlpha45Color)
                                _text = NumberUtil.converString(damukuNum ?: "0")
                            }
                        }
                    }

                }

            }..lParams(width = matchParent, height = matchParent)

        }

    }
}

// 垂直布局
fun MiaoUI.videoItemV(
    title: String? = null,
    pic: String? = null,
    upperName: String? = null,
    remark: String? = null,
    playNum: String? = null,
    damukuNum: String? = null,
    duration: String? = null,
    isLive: Boolean = false,
    isHtml: Boolean = false
): View {
    return materialCardView {
        strokeWidth = 0
        elevation = 2f
        radius = 12f
        setCardBackgroundColor(config.blockBackgroundColor)
        layoutParams =
            ViewGroup.MarginLayoutParams(matchParent, wrapContent)
                .apply { setMargins(dip(3), dip(4), dip(3), dip(3)) }

        views {
            +verticalLayout {
                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
//        setBackgroundResource(config.selectableItemBackground)
//        padding = dip(10)

                views {

                    +frameLayout {
                        views {
                            // 封面
                            +glideRationImageView {
//                                setImageUrl(pic)
                                _network(pic, "@672w_378h_1c_")
                                ration = 3f / 5f
                            }..lParams(matchParent, dip(0))

                            +textView {
                                text = "LIVE"//resources.getString(R.string.live) //"直播"
                                lines = 1
                                textSize = 10f
                                alpha = 0.9f
                                setTextColor(config.white)
                                setPadding(dip(4), dip(1), dip(4), dip(2))
                                setBackgroundResource(config.videoCardLiveBackground)
                                _backgroundTintList = ColorStateList.valueOf(config.themeColor)

                                _show = isLive

                            }..lParams(wrapContent, wrapContent) {
                                setMargins(dip(6))
                                gravity = Gravity.RIGHT
                            }

                            // 播放量，弹幕数量
                            +horizontalLayout {
                                gravity = Gravity.CENTER_VERTICAL
                                _show = playNum != null || damukuNum != null
                                setBackgroundResource(config.videoCardTextBackground)
                                setPadding(dip(8), dip(4), dip(8), dip(4))

                                views {
                                    +imageView {
                                        imageResource =
                                            R.mipmap.ic_card_play//R.drawable.ic_play_circle_outline_black_24dp
                                        imageTintList = ColorStateList.valueOf(config.white)
                                    }..lParams {
                                        width = dip(16)
                                        height = dip(16)
                                        topMargin = 1
                                        rightMargin = dip(3)
                                    }
                                    +textView {
                                        textSize = 12f
                                        setTextColor(config.white)
                                        _text = NumberUtil.converString(playNum ?: "0")
                                    }

                                    +space()..lParams(width = dip(10))
                                    +imageView {
                                        imageResource =
                                            R.mipmap.ic_card_danmu//R.drawable.ic_subtitles_black_24dp
                                        imageTintList = ColorStateList.valueOf(config.white)

                                        _show = damukuNum != null

                                    }..lParams {
                                        width = dip(16)
                                        height = dip(16)
                                        topMargin = 1
                                        rightMargin = dip(3)
                                    }
                                    +textView {
                                        textSize = 12f
                                        setTextColor(config.white)
                                        _text = NumberUtil.converString(damukuNum ?: "0")

                                        _show = damukuNum != null
                                    }

                                    +space()..lParams { weight = 1f }
                                    +textView {
                                        textSize = 12f
                                        setTextColor(config.white)
                                        _text = duration ?: ""
                                    }
                                }
                            }..lParams(matchParent, wrapContent) {
                                gravity = Gravity.BOTTOM
                            }
                        }
                    }..lParams(matchParent, wrapContent)

                    +verticalLayout {
                        layoutParams =
                            ViewGroup.MarginLayoutParams(matchParent, matchParent)
//                                .apply { setMargins(dip(8), dip(6), dip(8), dip(2)) }

                        setPadding(dip(8), dip(6), dip(8), dip(4))

                        views {
                            // 标题
                            +textView {
                                ellipsize = TextUtils.TruncateAt.END
                                maxLines = 2
                                setTextColor(config.foregroundColor)
                                textSize = 14f
                                if (isHtml) {
                                    miaoEffect(title) {
                                        DebugMiao.log(it)
                                        text =
                                            HtmlTagHandler.fromHtml("<html><body>$it</body></html>")
                                    }
                                } else {
                                    _text = title ?: ""
                                }

                                bottomPadding = dip(2)
                            }..lParams(matchParent, matchParent) {
                                weight = 1f
//                                marginBottom = dip(6)
                            }

                            // UP主
                            +horizontalLayout {
                                gravity = Gravity.CENTER_VERTICAL
                                _show = upperName != null

                                views {
                                    +imageView {
                                        imageResource = R.mipmap.ic_card_up//R.drawable.icon_up
                                        imageTintList =
                                            ColorStateList.valueOf(config.foregroundAlpha45Color)
                                        //apply(ViewStyle.roundRect(dip(5)))
                                    }..lParams {
                                        width = dip(16)
                                        height = dip(16)
                                        topMargin = dip(1)
                                        rightMargin = dip(3)
                                    }

                                    +textView {
                                        textSize = 12f
                                        setTextColor(config.foregroundAlpha45Color)
                                        maxLines = 1
                                        _text = upperName ?: ""
                                    }
                                }
                            }

                            // 备注
                            +horizontalLayout {
                                gravity = Gravity.CENTER_VERTICAL
                                _show = remark != null

                                views {

                                    +textView {
                                        textSize = 14f
                                        setTextColor(config.foregroundAlpha45Color)
                                        _text = remark ?: ""
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

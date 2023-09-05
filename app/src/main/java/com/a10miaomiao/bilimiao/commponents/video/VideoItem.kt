package com.a10miaomiao.bilimiao.commponents.video

import android.content.res.ColorStateList
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.a10miaomiao.bilimiao.config.ViewStyle
import com.a10miaomiao.bilimiao.config.config
import com.a10miaomiao.bilimiao.widget.rcImageView
import splitties.dimensions.dip
import splitties.resources.color
import splitties.views.dsl.constraintlayout.bottomOfParent
import splitties.views.dsl.core.*
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.endOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.leftOfParent
import splitties.views.dsl.constraintlayout.rightOfParent
import splitties.views.dsl.constraintlayout.startOfParent
import splitties.views.dsl.constraintlayout.topOfParent
import splitties.views.imageResource
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
                rightMargin = dip(5)
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
                                imageResource = R.drawable.icon_up
                                apply(ViewStyle.roundRect(dip(5)))
                            }..lParams {
                                width = dip(16)
                                rightMargin = dip(3)
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
                                imageResource = R.drawable.ic_play_circle_outline_black_24dp
                            }..lParams {
                                width = dip(16)
                                rightMargin = dip(3)
                            }
                            +textView {
                                textSize = 14f
                                setTextColor(config.foregroundAlpha45Color)
                                _text = NumberUtil.converString(playNum ?: "0")
                            }
                            +space()..lParams(width = dip(10))
                            +imageView {
                                imageResource = R.drawable.ic_subtitles_black_24dp
                            }..lParams {
                                width = dip(16)
                                rightMargin = dip(3)
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
    isHtml: Boolean = false,
): View {
    return verticalLayout {
        layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
//        setBackgroundResource(config.selectableItemBackground)
        setBackgroundResource(config.blockBackgroundResource)
//        padding = dip(10)

        views {

            +constraintLayout {
                views {
                    // 封面
                    +rcImageView {
//                        radius = dip(5)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        _network(pic, "@672w_378h_1c_")
                    }..lParams(matchParent, dip(0)) {
                        topOfParent()
                        startOfParent()
                        endOfParent()
                        dimensionRatio = "8:5"
                    }

                    // 播放量，弹幕数量
                    +horizontalLayout {
                        gravity = Gravity.CENTER_VERTICAL
                        _show = playNum != null || damukuNum != null
                        setBackgroundResource(config.videoCardTextBackground)
                        setPadding(dip(8), dip(2), dip(8), dip(2))

                        views {
                            +imageView {
                                imageResource = R.drawable.ic_play_circle_outline_black_24dp
                                imageTintList = ColorStateList.valueOf(config.white)
                            }..lParams {
                                width = dip(12)
                                rightMargin = dip(3)
                            }
                            +textView {
                                textSize = 12f
                                setTextColor(config.white)
                                _text = NumberUtil.converString(playNum ?: "0")
                            }
                            +space()..lParams(width = dip(10))
                            +imageView {
                                imageResource = R.drawable.ic_subtitles_black_24dp
                                imageTintList = ColorStateList.valueOf(config.white)
                            }..lParams {
                                width = dip(12)
                                rightMargin = dip(3)
                            }
                            +textView {
                                textSize = 12f
                                setTextColor(config.white)
                                _text = NumberUtil.converString(damukuNum ?: "0")
                            }
                        }
                    }..lParams(matchParent, wrapContent) {
                        startOfParent()
                        endOfParent()
                        bottomOfParent()
                    }
                }
//                ..lParams {
//                    width = matchParent//dip(140)
//                    height = dip(114)
////                rightMargin = dip(5)
//                }
            }..lParams(matchParent, wrapContent)

            +verticalLayout {
                layoutParams =
                    ViewGroup.MarginLayoutParams(matchParent, wrapContent)
                        .apply { setMargins(dip(8), dip(6), dip(8), dip(4)) }

                views {
                    // 标题
                    +textView {
                        ellipsize = TextUtils.TruncateAt.END
                        maxLines = 1
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
                                imageResource = R.drawable.icon_up
                                apply(ViewStyle.roundRect(dip(5)))
                            }..lParams {
                                width = dip(15)
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

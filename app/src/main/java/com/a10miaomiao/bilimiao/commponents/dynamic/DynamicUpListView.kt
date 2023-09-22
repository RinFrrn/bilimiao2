package com.a10miaomiao.bilimiao.commponents.dynamic

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import cn.a10miaomiao.miao.binding.android.view._show
import cn.a10miaomiao.miao.binding.android.widget._text
import cn.a10miaomiao.miao.binding.android.widget._textColor
import cn.a10miaomiao.miao.binding.exception.BindingOnlySetException
import cn.a10miaomiao.miao.binding.miaoEffect
import com.a10miaomiao.bilimiao.comm.MiaoUI
import com.a10miaomiao.bilimiao.comm._network
import com.a10miaomiao.bilimiao.comm.views
import com.a10miaomiao.bilimiao.config.ViewStyle
import com.a10miaomiao.bilimiao.config.config
import com.a10miaomiao.bilimiao.widget.rcImageView
import com.google.android.material.card.MaterialCardView
import splitties.dimensions.dip
import splitties.views.backgroundColor
import splitties.views.dsl.core.frameLayout
import splitties.views.dsl.core.horizontalLayout
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.verticalLayout
import splitties.views.dsl.core.wrapContent
import splitties.views.dsl.material.materialCardView
import splitties.views.gravityCenter
import splitties.views.lines

fun MiaoUI.dynamicUpItem(face: String, name: String, selected: Boolean, hasUpdate: Boolean): View = materialCardView {
    layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent).apply {
        setMargins(dip(16), dip(4), dip(4), dip(4))
    }
    setContentPadding(dip(16), dip(8), dip(16), dip(8))
    radius = dip(24f)
//    radius = dip(8f)
    strokeWidth = 0
    _cardBackgroundColor =
        if (selected) config.themeColor else config.blockBackgroundColor

    views {
        +horizontalLayout {
            gravity = Gravity.CENTER_VERTICAL

            views {
                +frameLayout {

                    views {
                        +rcImageView {
                            isCircle = true
                            _network(face)
                        }..lParams {
                            width = dip(36)
                            height = dip(36)
                            gravity = gravityCenter
                        }

                        +verticalLayout {
                            _show = hasUpdate
                            backgroundColor = config.themeColor
                            apply(ViewStyle.circle)
//                            strokeWidth = dip(4)
//                            strokeColor = config.white
                        }..lParams {
                            width = dip(8)
                            height = dip(8)
                            gravity = Gravity.BOTTOM or Gravity.END
                        }
                    }
                }..lParams(wrapContent, wrapContent) {
                    rightMargin = dip(12)
                }

                +textView {
                    _text = name
                    lines = 1
                    _textColor = if (selected) config.white else config.foregroundAlpha66Color
                    textSize = 13f
                }..lParams(matchParent, wrapContent)
            }
        }..lParams(matchParent, wrapContent)
    }
}


inline var MaterialCardView._cardBackgroundColor: Int
    get() { throw BindingOnlySetException() }
    set(value) = miaoEffect(value) {
        setCardBackgroundColor(value)
    }
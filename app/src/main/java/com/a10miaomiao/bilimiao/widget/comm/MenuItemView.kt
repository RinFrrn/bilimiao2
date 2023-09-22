package com.a10miaomiao.bilimiao.widget.comm

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.a10miaomiao.bilimiao.comm.attr
import com.a10miaomiao.bilimiao.comm.mypage.MenuItemPropInfo
import com.a10miaomiao.bilimiao.config.config
import splitties.dimensions.dip
import splitties.views.dsl.core.*

class MenuItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val ui = ViewUi()
    var prop = MenuItemPropInfo()
        set(value) {
            field = value
            updateView()
        }

    var iconSize: Int = dip(20)
        set(newSize) {
            field = newSize
            ui.icon.layoutParams.apply {
                height = iconSize
                width = iconSize
            }
        }

    init {
        gravity = Gravity.CENTER
        addView(ui.icon, lParams {
            horizontalMargin = dip(5)
            verticalMargin = dip(4)

            height = dip(iconSize)
            width = dip(iconSize)
        })
        addView(ui.root, lParams {
            height = wrapContent
            width = wrapContent
        })
    }

    private fun updateView() {
        if (visibility != prop.visibility) {
            visibility = prop.visibility
        }

        ui.icon.imageTintList = prop.iconTintColor?.let { ColorStateList.valueOf(it) }

        if (prop.iconResource == null && prop.iconDrawable == null && prop.iconFileName == null) {
            ui.icon.visibility = View.GONE
        } else if (prop.iconFileName != null) {
            val iconResource =
                context.resources.getIdentifier(prop.iconFileName, "drawable", context.packageName)
            ui.icon.visibility = View.VISIBLE
            ui.icon.setImageResource(iconResource)
        } else if (prop.iconDrawable != null) {
            ui.icon.visibility = View.VISIBLE
            ui.icon.setImageDrawable(prop.iconDrawable)
        } else if (prop.iconResource != null) {
            ui.icon.visibility = View.VISIBLE
            ui.icon.setImageResource(prop.iconResource!!)
        }
        if (prop.title == null) {
            ui.title.visibility = View.GONE
        } else {
            ui.title.text = prop.title
        }
        val subTitle = prop.subTitle
        if (subTitle == null) {
            ui.subTitle.visibility = View.GONE
        } else {
            ui.subTitle.visibility = View.VISIBLE
            ui.subTitle.text = if (orientation == HORIZONTAL) {
                subTitle
            } else {
                subTitle.replace("\n", " ")
            }
        }
    }

    inner class ViewUi : Ui {
        override val ctx: Context get() = context

        val title = textView {
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(config.foregroundAlpha45Color)
        }

        val subTitle = textView {
            textSize = 10f
            gravity = Gravity.CENTER
            setTextColor(config.foregroundAlpha45Color)
        }

        override val root = verticalLayout {
            gravity = Gravity.CENTER

            addView(title, lParams {
                width = matchParent
                height = wrapContent
            })
            addView(subTitle, lParams {
                width = matchParent
                height = wrapContent
                topMargin = dip(2)
            })
        }


        val icon = imageView {

        }

    }

}
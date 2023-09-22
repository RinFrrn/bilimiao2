package com.a10miaomiao.bilimiao.widget.comm.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.a10miaomiao.bilimiao.comm.attr
import com.a10miaomiao.bilimiao.comm.flexboxLayout
import com.a10miaomiao.bilimiao.comm.scrollView
import com.a10miaomiao.bilimiao.comm.utils.DebugMiao
import com.a10miaomiao.bilimiao.config.config
import com.a10miaomiao.bilimiao.widget.comm.AppBarView
import com.a10miaomiao.bilimiao.widget.comm.MenuItemView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.JustifyContent
import splitties.dimensions.dip
import splitties.views.*
import splitties.views.dsl.constraintlayout.centerHorizontally
import splitties.views.dsl.constraintlayout.centerVertically
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.endOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.startOfParent
import splitties.views.dsl.constraintlayout.topOfParent
import splitties.views.dsl.coordinatorlayout.coordinatorLayout
import splitties.views.dsl.core.*

class AppBarHorizontalUi(
    override val ctx: Context,
    val menuItemClick: View.OnClickListener,
    val menuItemLongClick: View.OnLongClickListener,
    val backClick: View.OnClickListener,
    val backLongClick: View.OnLongClickListener,
) : AppBarUi {

    val mNavigationIcon = imageView {
        setBackgroundResource(ctx.attr(android.R.attr.selectableItemBackgroundBorderless))
    }

    val mNavigationIconLayout = frameLayout {
        padding = dip(24)
        bottomPadding = 16
        setOnClickListener(backClick)
        setOnLongClickListener(backLongClick)
        addView(mNavigationIcon, lParams {
            gravity = Gravity.CENTER
            width = dip(28)
            height = dip(28)
        })
    }

    val mTitle = textView {
        gravity = Gravity.CENTER
        setTextColor(config.foregroundAlpha45Color)
    }

    val mTitleLayout = frameLayout {
        padding = dip(10)
        addView(mTitle, lParams {
            width = matchParent
            height = wrapContent
        })
    }

    val mNavigationMemuLayout = verticalLayout {
        gravity = Gravity.CENTER
    }

    val mTopLinearLayout = verticalLayout {
        addView(mNavigationIconLayout)
        addView(mTitleLayout)
    }

    override val root = scrollView {
        isFillViewport = true

        addView(verticalLayout {

            addView(mTopLinearLayout, lParams(matchParent, 0) {
                weight = 1f
            })
            addView(mNavigationMemuLayout, lParams(matchParent, wrapContent))
            addView(space(), lParams(matchParent, 0) { weight = 1f })
        }, lParams(matchParent, wrapContent))
    }

//    addView(frameLayout {
//
//        addView(mTopLinearLayout, lParams(matchParent, wrapContent) {
//            gravity = Gravity.START
//        })
//        addView(mNavigationMemuLayout, lParams(matchParent, wrapContent) {
//            gravity = Gravity.CENTER
//        })
//    }, lParams(matchParent, wrapContent))

//    addView(constraintLayout {
//
//        addView(mTopLinearLayout, lParams(wrapContent, wrapContent) {
//            topOfParent()
//            centerHorizontally()
//            bottomToTop = mNavigationMemuLayout.id
//        })
//        addView(mNavigationMemuLayout, lParams(wrapContent, wrapContent) {
//            centerVertically()
//            centerHorizontally()
//            topToBottom = mTopLinearLayout.id
//        })
//    }, lParams(matchParent, wrapContent))


    override fun setProp(prop: AppBarView.PropInfo?) {
        if (prop != null) {
            if (prop.navigationIcon != null) {
                mNavigationIconLayout.visibility = View.VISIBLE
                mNavigationIcon.imageDrawable = prop.navigationIcon
            } else {
                mNavigationIconLayout.visibility = View.GONE
            }
            if (prop.title != null) {
                mTitleLayout.visibility = View.VISIBLE
                mTitle.text = prop.title ?: ""
            } else {
                mTitleLayout.visibility = View.GONE
            }

            val menus = prop.menus?.reversed()
            if (menus == null) {
                mNavigationMemuLayout.removeAllViews()
            } else {
                mNavigationMemuLayout.apply {
                    menus.forEachIndexed { index, menu ->
                        var menuItemView: MenuItemView
                        if (index >= childCount) {
                            menuItemView = MenuItemView(ctx).apply {
                                orientation = LinearLayout.VERTICAL
                                minimumHeight = dip(64)
                                iconSize = dip(26)
                                setOnClickListener(menuItemClick)
                                setBackgroundResource(config.selectableItemBackground)
                            }
                            addView(menuItemView, lParams {
                                width = matchParent
                                height = wrapContent
                            })
                        } else {
                            menuItemView = getChildAt(index) as MenuItemView
                        }
                        menuItemView.prop = menu
                    }
                    if (childCount > menus.size) {
                        removeViews(
                            menus.size,
                            childCount - menus.size
                        )
                    }
                }
            }
        }
    }
}
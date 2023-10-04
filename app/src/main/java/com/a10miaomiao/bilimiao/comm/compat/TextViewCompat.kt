package com.a10miaomiao.bilimiao.comm.compat

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.a10miaomiao.bilimiao.R
import splitties.dimensions.dip
import splitties.views.setCompoundDrawables


fun TextView.setTextAppearanceCompat(@StyleRes resId: Int) {
    TextViewCompat.setTextAppearance(this, resId)
}

fun TextView.setDrawableStart(@DrawableRes resId: Int, size: Int, tintList: ColorStateList? = null) {
    val drawable = ResourcesCompat.getDrawable(
        resources, resId, null
    )?.apply {
        setBounds(0, 1, size, size)
        setTintList(tintList)
    }

    setCompoundDrawables(drawable)
}
package com.a10miaomiao.bilimiao.page.video

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoPreLoadParam(
    val aid: String,
    val cid: String,
    val cover: String,
    val title: String,
    val ownerId: String,
    val ownerName: String,
) : Parcelable
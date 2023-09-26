package com.a10miaomiao.bilimiao.comm.delegate.helper

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import android.view.View
import androidx.annotation.RequiresApi
//import cn.a10miaomiao.player.callback.MediaPlayerListener
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.widget.player.DanmakuVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer


@RequiresApi(Build.VERSION_CODES.O)
class PicInPicHelper(
    val activity: Activity,
    val videoPlayer: DanmakuVideoPlayer
) {

    companion object {
        val ACTION_MEDIA_CONTROL = "media_control"
        val EXTRA_CONTROL_TYPE = "control_type"

        val CONTROL_TYPE_PLAY = 1
        val CONTROL_TYPE_PAUSE = 2

        val REQUEST_TYPE_PLAY = 1
        val REQUEST_TYPE_PAUSE = 2
    }


    private val builder = PictureInPictureParams.Builder()

    var isInPictureInPictureMode = false


    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                return
            }
            if (intent.action != ACTION_MEDIA_CONTROL) {
                return
            }
            when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                CONTROL_TYPE_PLAY -> {
                    videoPlayer.onVideoResume()
                }

                CONTROL_TYPE_PAUSE -> {
                    videoPlayer.onVideoPause()
                }
            }
        }
    }

//    private val mOnLayoutChangeListener =
//        View.OnLayoutChangeListener { v: View?, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int, newLeft: Int, newTop: Int, newRight: Int, newBottom: Int ->
//            if (newLeft != oldLeft || newRight != oldRight || newTop != oldTop || newBottom != oldBottom) {
//                // The playerView's bounds changed, update the source hint rect to
//                // reflect its new bounds.
//                val sourceRectHint = Rect()
//                videoPlayer.getGlobalVisibleRect(sourceRectHint)
//                sourceRectHint.set(
//                    calculateSizeWithAspectRatioAndCenter(
//                        sourceRectHint,
//                        aspectRatio
//                    )
//                )
//                builder.setSourceRectHint(sourceRectHint)
//                activity.setPictureInPictureParams(builder.build())
//            }
//        }
//
//    init {
//        videoPlayer.addOnLayoutChangeListener(mOnLayoutChangeListener)
//    }

    fun enterPictureInPictureMode(aspectRatio: Rational) {
        // 判断Android版本是否大于等于8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 设置画中画窗口的宽高比例
            builder.setAspectRatio(aspectRatio)
            builder.setActions(getActions(videoPlayer.currentState))

            // 设置画面 rect
            val sourceRectHint = Rect()
            videoPlayer.getGlobalVisibleRect(sourceRectHint)
            sourceRectHint.set(
                calculateSizeWithAspectRatioAndCenter(
                    sourceRectHint,
                    aspectRatio
                )
            )
            builder.setSourceRectHint(sourceRectHint)

            // 进入画中画模式，注意enterPictureInPictureMode是Android8.0之后新增的方法
            activity.enterPictureInPictureMode(builder.build());
        };
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getActions(state: Int): List<RemoteAction> {
        val action = if (state == GSYVideoPlayer.CURRENT_STATE_PLAYING) {
            RemoteAction(
                Icon.createWithResource(activity, R.drawable.bili_player_play_can_pause),
                "暂停",
                "",
                PendingIntent.getBroadcast(
                    activity,
                    REQUEST_TYPE_PAUSE,
                    Intent(ACTION_MEDIA_CONTROL).putExtra(
                        EXTRA_CONTROL_TYPE,
                        CONTROL_TYPE_PAUSE
                    ),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            )
        } else {
            RemoteAction(
                Icon.createWithResource(activity, R.drawable.bili_player_play_can_play),
                "播放",
                "",
                PendingIntent.getBroadcast(
                    activity,
                    REQUEST_TYPE_PLAY,
                    Intent(ACTION_MEDIA_CONTROL).putExtra(
                        EXTRA_CONTROL_TYPE,
                        CONTROL_TYPE_PLAY
                    ),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            )
        }
        return listOf(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePictureInPictureActions(state: Int) {
        builder.setActions(getActions(state))
        activity.setPictureInPictureParams(builder.build());
    }

    fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        this.isInPictureInPictureMode = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            activity.registerReceiver(broadcastReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
        } else {
            activity.unregisterReceiver(broadcastReceiver)
        }
    }

    private fun calculateSizeWithAspectRatioAndCenter(
        rect: Rect,
        aspectRatio: Rational
    ): Rect {
        // 获取矩形的宽度和高度
        val rectWidth = rect.width()
        val rectHeight = rect.height()

        // 获取视频画面的宽度和高度
        val videoWidth = aspectRatio.numerator
        val videoHeight = aspectRatio.denominator

        // 初始化新的矩形
        val newRect = Rect(rect)

        // 计算新的尺寸以适应比例
        var newWidth = rectWidth
        var newHeight = rectHeight

        if (videoWidth > 0 && videoHeight > 0) {
            val currentAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()

            if (currentAspectRatio > 0) {
                if (currentAspectRatio > rectWidth.toFloat() / rectHeight.toFloat()) {
                    // 视频更宽，适应宽度
                    newWidth = rectWidth
                    newHeight = (rectWidth.toFloat() / currentAspectRatio).toInt()
                } else {
                    // 视频更高，适应高度
                    newWidth = (rectHeight.toFloat() * currentAspectRatio).toInt()
                    newHeight = rectHeight
                }
            }
        }

        // 确保新的尺寸在矩形内
        if (newWidth > rectWidth) {
            newWidth = rectWidth
        }
        if (newHeight > rectHeight) {
            newHeight = rectHeight
        }

        // 计算新矩形的左上角坐标以使其居中
        val left = (rectWidth - newWidth) / 2
        val top = (rectHeight - newHeight) / 2
        val right = left + newWidth
        val bottom = top + newHeight

        // 设置新矩形的坐标
        newRect.set(left, top, right, bottom)

        return newRect
    }
}
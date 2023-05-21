package com.a10miaomiao.bilimiao.widget.player.media3;


import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
//import androidx.media3.decoder.av1.Libgav1VideoRenderer;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;


// FIXME: 2023-05-13 support AV1
@OptIn(markerClass = UnstableApi.class)
public class Libgav1Media3ExoPlayerManager extends Media3ExoPlayerManager {
    @Override
    protected ExoMediaPlayer buildMediaPlayer(Context context) {
        Toast.makeText(context, "Using AV1 Player Manager", Toast.LENGTH_SHORT).show();
        return super.buildMediaPlayer(context);
    }

    //    private DefaultRenderersFactory renderersFactory;
//
//    @Override
//    protected ExoMediaPlayer buildMediaPlayer(Context context) {
//        if (renderersFactory == null) {
//            renderersFactory = new Libgav1VideoRendererFactory(context);
//        }
//        ExoMediaPlayer exoMediaPlayer = new ExoMediaPlayer(context);
//        exoMediaPlayer.setRendererFactory(renderersFactory);
//        return exoMediaPlayer;
//    }
//
//    class Libgav1VideoRendererFactory extends DefaultRenderersFactory {
//
//        public Libgav1VideoRendererFactory(Context context) {
//            super(context);
//            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
//        }
//
//        @Override
//        protected void buildVideoRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
//            Libgav1VideoRenderer renderer = new Libgav1VideoRenderer(
//                    allowedVideoJoiningTimeMs,
//                    eventHandler,
//                    eventListener,
//                    MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
//            );
//            out.add(renderer);
//            super.buildVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
//        }
//    }
}
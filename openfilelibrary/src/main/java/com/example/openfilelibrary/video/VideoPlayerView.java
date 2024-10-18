package com.example.openfilelibrary.video;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.openfilelibrary.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;


public class VideoPlayerView extends FrameLayout {

    public VideoPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private StandardGSYVideoPlayer videoPlayer;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_video_player, this, true);
        /*playerView = findViewById(R.id.player_view);*/
        videoPlayer = findViewById(R.id.player_view);
    }


    public void initializePlayer(Context context, String videoUrl, View.OnClickListener clickListener) {
        //https://github.com/CarGuo/GSYVideoPlayer/blob/master/doc/USE.md
        /*ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
        videoPlayer.setThumbImageView(imageView);*/
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
//        videoPlayer.setIsTouchWiget(true);
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        videoPlayer.getBackButton().setOnClickListener(clickListener);
        videoPlayer.setNeedOrientationUtils(false);
        videoPlayer.setAutoFullWithSize(true);
        videoPlayer.setUp(videoUrl, true, "");
        videoPlayer.startPlayLogic();
    }

    public void releasePlayer() {
        GSYVideoManager.releaseAllVideos();
    }
}

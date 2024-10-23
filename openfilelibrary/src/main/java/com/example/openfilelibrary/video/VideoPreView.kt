package com.example.openfilelibrary.video

import android.content.DialogInterface
import android.media.MediaPlayer
import android.media.MediaPlayer.OnErrorListener
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.example.openfilelibrary.R
import com.example.openfilelibrary.base.BaseBottomSheetFrag
import com.example.openfilelibrary.databinding.VideoPlayerBinding
import com.example.openfilelibrary.utile.ScreenUtils
import com.example.openfilelibrary.utile.common.SingleClick
import com.example.openfilelibrary.utile.common.toTimeString
import com.hjq.toast.Toaster

/**
 * @author zyju
 * @date 2024/9/11 16:56
 */
internal class VideoPreView(var uri: Uri) : BaseBottomSheetFrag() {
    private lateinit var binding: VideoPlayerBinding
    override fun getLayoutHeight(): Int {
        return ScreenUtils.getScreenHeight(mContext!!)
    }

    override fun getisDraggable(): Boolean {
        return false
    }

    override fun IsScrollable(): Boolean {
        return false
    }

    var mMediaController: MediaController? = null
    override fun initView() {
        binding = VideoPlayerBinding.bind(rootView!!)
        mMediaController = MediaController(context, true);
        binding.videoView.setVideoURI(uri)
        // 设置播放器控制条
        mMediaController?.setAnchorView(binding.videoView)
        mMediaController?.setMediaPlayer(binding.videoView)
        binding.loadProgress.visibility = View.VISIBLE
        binding.videoView.apply {
            setOnPreparedListener {
                binding.loadProgress.visibility = View.GONE
                setMediaController(mMediaController)
                seekTo(0)
                requestFocus()
                start()
                setOnCompletionListener {
                    // 循环播放
                    start()
                }
                setOnErrorListener { mp, what, extra ->
                    LogUtils.e("video onError: $what, $extra")
                    Toaster.show("播放失败,请检查视频源是否正确")
                    true
                }
            }
        }

    }

    override fun getLayoutResId(): Int {
        return R.layout.video_player
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
        binding.videoView.suspend()
    }

}
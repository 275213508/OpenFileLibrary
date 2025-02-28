package com.example.openfilelibrary.video.view

import android.content.pm.ActivityInfo
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.openfilelibrary.R
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.databinding.MediaControllerBinding
import com.example.openfilelibrary.utile.common.SingleClick
import java.util.Formatter
import java.util.Locale
import kotlin.math.roundToInt

/**
 * @author zyju
 * @date 2025/2/20 16:10
 */
class PlayViews(var context: AppCompatActivity, var binding: MediaControllerBinding, var videoView: VideoView) {

    /** 当前播放进度 */
    private var currentProgress: Int = 0

    /**隐藏延时*/
    private var HideDelay: Long = 5000

    init {
        videoView.setOnTouchListener { v, event -> clickVideo() }
        SingleClick(binding.pause) {
            if (isPlaying()) {
                pause()
            } else {
                play()
            }
        }
        SingleClick(binding.llParent) {
            refreshHideTime()
        }
        SingleClick(binding.isFullScreen){
            if (context.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else {
                context.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        videoView.setOnCompletionListener {
            play()
        }
        binding.mediacontrollerProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    countDownTimer.cancel()
                    countDownTimer.start()
                    refreshHideTime()
                    onProgressChanged(progress, fromUser)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }
    private var onchecked:ICell<Boolean>? = null
    fun setIsShowChengeListener(onchecked:ICell<Boolean>){
        this.onchecked = onchecked
    }
    private fun refreshHideTime() {
        binding.timeCurrent.removeCallbacks(delayHide)
        if (isShow()) {
            binding.timeCurrent.postDelayed(delayHide, HideDelay)
        }
    }

    private fun clickVideo(): Boolean {
        binding.timeCurrent.removeCallbacks(delayHide)
        if (!isShow()) {
            Show()
            binding.timeCurrent.postDelayed(delayHide, HideDelay)
        } else {
            Hide()
        }
        return false
    }

    fun isShow(): Boolean {
        return binding.root.isVisible
    }

    fun Show() {
        binding.root.visibility = View.VISIBLE
        onchecked?.cell(true)
    }

    fun Hide() {
        binding.root.visibility = View.GONE
        onchecked?.cell(false)
    }

    private var countDownTimer: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            RefreshProgress()
        }

        override fun onFinish() {}
    }
    private var delayHide = Runnable {
        Hide()
    }

    /**隐藏延时 毫秒*/
    fun setHideDelay(millis: Long) {
        HideDelay = millis
    }

    /**
     * 播放状态
     */
    fun play() {
        videoView.start()
        setMax(videoView.duration)
        countDownTimer.cancel()
        countDownTimer.start()
        refreshHideTime()
        binding.timeCurrent.postDelayed(delayHide, HideDelay)
        binding.pause.setBackgroundResource(R.drawable.ic_pause)
    }

    /**
     * 暂停状态
     */
    fun pause() {
        videoView.pause()
        refreshHideTime()
        binding.pause.setBackgroundResource(R.drawable.ic_play)
        countDownTimer.cancel()
    }

    fun onDestroy() {
        countDownTimer.cancel()
        binding.timeCurrent.removeCallbacks(delayHide)
        videoView.stopPlayback()
    }

    fun RefreshProgress() {
        var progress: Int = videoView.currentPosition
        // 这里优化了播放的秒数计算，将 800 毫秒估算成 1 秒
        if (progress + 1000 < videoView.duration) {
            // 进行四舍五入计算
            progress = (progress / 1000f).roundToInt() * 1000
        }
        binding.timeCurrent.text = conversionTime(progress)
        binding.mediacontrollerProgress.progress = progress
        binding.mediacontrollerProgress.secondaryProgress = (videoView.bufferPercentage / 100f * videoView.duration).toInt()
    }

    fun setProgress(progress: Int) {
        refreshHideTime()
        var finalProgress: Int = progress
        if (finalProgress > videoView.duration) {
            finalProgress = videoView.duration
        }
        // 要跳转的进度必须和当前播放进度相差 1 秒以上
        videoView.seekTo(finalProgress)
        binding.mediacontrollerProgress.progress = finalProgress
    }

    fun onProgressChanged(progress: Int, fromUser: Boolean) {
        videoView.seekTo(progress)
        if (fromUser) {
            binding.timeCurrent.text = conversionTime(progress)
            return
        }
        if (progress != 0) {
            // 记录当前播放进度
            currentProgress = progress
            binding.timeCurrent.text = conversionTime(progress)
            binding.mediacontrollerProgress.progress = progress
        } else {
            // 如果 Activity 返回到后台，progress 会等于 0，而 mVideoView.getDuration 会等于 -1
            // 所以要避免在这种情况下记录当前的播放进度，以便用户从后台返回到前台的时候恢复正确的播放进度
            if (videoView.duration > 0) {
                currentProgress = progress
                binding.timeCurrent.text = conversionTime(progress)
                binding.mediacontrollerProgress.progress = progress
            }
        }
    }

    fun seekTo(progress: Int) {
        videoView.seekTo(progress)
    }

    fun setMax(max: Int) {
        binding.mediacontrollerProgress.max = max
        binding.time.text = conversionTime(max)
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return videoView.isPlaying
    }

    /**
     * 时间转换
     */
    fun conversionTime(time: Int): String {
        val formatter = Formatter(Locale.getDefault())
        // 总秒数
        val totalSeconds: Int = time / 1000
        // 小时数
        val hours: Int = totalSeconds / 3600
        // 分钟数
        val minutes: Int = (totalSeconds / 60) % 60
        // 秒数
        val seconds: Int = totalSeconds % 60
        return if (hours > 0) {
            formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            formatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }
}